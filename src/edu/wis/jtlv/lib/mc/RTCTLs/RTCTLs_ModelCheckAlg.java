package edu.wis.jtlv.lib.mc.RTCTLs;

import edu.wis.jtlv.env.Env;
import edu.wis.jtlv.env.core.smv.SMVParseException;
import edu.wis.jtlv.env.core.smv.eval.*;
import edu.wis.jtlv.env.module.*;
import edu.wis.jtlv.env.spec.*;
import edu.wis.jtlv.lib.*;
import edu.wis.jtlv.lib.mc.ModelCheckAlgException;
import edu.wis.jtlv.lib.mc.ModelCheckAlgI;
import edu.wis.jtlv.old_lib.mc.ModelCheckException;
import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDVarSet;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.ui.spriteManager.Sprite;
import swing.MCTKFrame;
import swing.Statistic;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Vector;

import static swing.MCTKFrame.*;

class CacheSpecTesterInfo {
    Spec spec=null; // a formula for creating its tester T_{spec}
    BDD specBdd=null; // \chi(spec)
    //LinkedHashSet<ModuleBDDField> auxVars; // X_{spec}: the auxiliary variables created for spec, not including the auxiliray variables for the maximal sub-formulas (Af or Ef) of spec
    LinkedHashSet<SMVModule> testerSet; // the set of sub-testers of spec, not including the sub-testers for the maximal sub-formulas (Af or Ef) of spec
    BDD feasibleStates=null;    // if spec=Ef, then feasibleStates is the feasible states of D||T_f
                                // if spec=Af, then feasibleStates is the feasible states of D||T_!f

    CacheSpecTesterInfo(    Spec spec,
                            BDD specBdd,
                            LinkedHashSet<SMVModule> specTesterSet,
                            BDD feasibleStates){
        this.spec=spec;
        this.specBdd=specBdd;

        this.testerSet = new LinkedHashSet<>();
        if(specTesterSet!=null) this.testerSet.addAll(specTesterSet);

        if(this.spec!=null) {
            SpecExp se = (SpecExp) this.spec;
            Operator op = se.getOperator();
            if (op==Operator.EE || op==Operator.AA)
                this.feasibleStates = feasibleStates;
            else
                this.feasibleStates = null;
        }
    }


    BDD getBdd(){
        if(spec instanceof SpecBDD)
            return ((SpecBDD)spec).getVal();
        else { // spec is SpecExp
            return specBdd;
        }
    }

    boolean testerIsEmpty(){
        return testerSet.size() <= 0;
    }

    // return X_{specStr}: the set of auxiliary variables created for spec
    LinkedHashSet<ModuleBDDField> getAuxVars() {
        LinkedHashSet<ModuleBDDField> auxVars = new LinkedHashSet<>();
        if(testerSet==null) return auxVars;

        for(SMVModule subTester : testerSet)
            auxVars.addAll(subTester.getAll_couples());
        return auxVars;
    }

    BDDVarSet getAuxVars_BDDVarSet() {
        BDDVarSet vs = Env.getEmptySet();
        for (ModuleBDDField var : getAuxVars())
            vs = vs.id().union(var.support());
        return vs;
    }

}

// the tester contains the initial conditions and transitions of ALL temporal operators in a spec
// including these temporal operators restricted by path quantifier E or A
class RTCTLsTester{
    int atomTesterCount=0;
    LinkedHashMap<String, SMVModule> atomTesterSet; // mapping a principal temporal sub-formula to its tester
    LinkedHashMap<String, CacheSpecTesterInfo> cacheSpecsInfo;

//    LinkedHashMap<String, BDD> cachePrinTempSpecBDDs;
        // for element <specStr, bdd>, specStr is the string of a principally temporal spec
        // bdd is the BDD of the spec's output formula

    RTCTLsTester(){
        atomTesterSet = new LinkedHashMap<>();
        cacheSpecsInfo=new LinkedHashMap<String, CacheSpecTesterInfo>();
    }

    CacheSpecTesterInfo putSpecInfo(Spec spec, BDD specBdd, LinkedHashSet<SMVModule> specTesterSet, BDD feasibleStates){
        if(spec==null) return null;
        if(specBdd==null) return null;
        return cacheSpecsInfo.put(spec.toString(),
                new CacheSpecTesterInfo(spec,specBdd,specTesterSet,feasibleStates));
    }

    CacheSpecTesterInfo getSpecInfo(String specStr){
        return cacheSpecsInfo.get(specStr);
    }


    BDD getSpecBdd(Spec spec){
        BDD ret=null;
        if(spec instanceof SpecBDD)
            return ((SpecBDD)spec).getVal();
        else { // spec is SpecExp
            CacheSpecTesterInfo specInfo = this.getSpecInfo(spec.toString());
            if(specInfo!=null) return specInfo.specBdd;
            else return null;
        }
    }
/*
    boolean specTesterIsEmpty(String specStr) {
        CacheSpecTesterInfo specInfo = getSpecInfo(specStr);
        if(specInfo==null) return true;
        if(specInfo.testerSet.size()<=0) return true;
        else return false;
    }

 */


    BDDVarSet varSet_to_BDDVarSet(LinkedHashSet<ModuleBDDField> varSet) {
        BDDVarSet vs = Env.getEmptySet();
        for (ModuleBDDField var : varSet)
            vs = vs.id().union(var.support());
        return vs;
    }

    // build a tester for principally temporal formula specStr
    SMVModule buildEmptyAtomTester(String specStr) throws ModuleException {
        if(atomTesterSet.containsKey(specStr)) return atomTesterSet.get(specStr);

        String testerName="Tester"+(++atomTesterCount);
        SMVModule m = new SMVModule(testerName);
        m.conjunctTrans(Env.TRUE());

        /*
        if(init!=null) m.setInitial(init);
        m.conjunctTrans(Env.TRUE());
        if(trans!=null) m.conjunctTrans(trans);
        if(justice!=null) m.addJustice(justice);
*/
        atomTesterSet.put(specStr, m);
        return m;
    }

    // returns the set of testers for spec1 and spec2, plus aTester
    LinkedHashSet<SMVModule> unionTesters(String specStr1, String specStr2, SMVModule aTester) {
        LinkedHashSet<SMVModule> testers = new LinkedHashSet<SMVModule>();
        CacheSpecTesterInfo spec1Info=null, spec2Info=null;

        if(specStr1!=null) {
            spec1Info = getSpecInfo(specStr1);
            if (spec1Info != null) testers.addAll(spec1Info.testerSet);
        }
        if(specStr2!=null) {
            spec2Info = getSpecInfo(specStr2);
            if (spec2Info != null) testers.addAll(spec2Info.testerSet);
        }
        if(aTester!=null) testers.add(aTester);

        return testers;
    }
/*
    boolean cacheAddSpecAuxVar(Spec spec, ModuleBDDField auxVar) {
        CacheSpecTesterInfo specInfo = cacheGetSpec(spec.toString());
        if(specInfo==null) return false;
        return specInfo.auxVars.add(auxVar);
    }

    boolean cacheDelSpecAuxVar(Spec spec, ModuleBDDField auxVar) {
        CacheSpecTesterInfo specInfo = cacheGetSpec(spec.toString());
        if(specInfo==null) return false;
        return specInfo.auxVars.remove(auxVar);
    }

 */
}

class NodePath {
    Vector<String> nodes; // the list of node IDs of the node path
    int loopIndex; // the first node's index of the period of the lasso node path; loopIndex==-1 denotes that the path is finite path
    int pathIndex;

    BDD firstState=null; // the D||T-state of the first node in the path

    public NodePath(int pathIndex){
        nodes = new Vector<String>();
        loopIndex = -1;
        this.pathIndex=pathIndex;
    }

    public NodePath(int pathIndex, Vector<String> nodeIdList, int loopIndex, BDD firstState){
        this.pathIndex=pathIndex;
        this.nodes = nodeIdList;
        this.loopIndex = loopIndex;
        this.firstState = firstState;
    }

    // return the index of the position of the path
    int at(int pos         // the logical position pos over the path
    ) {
        int i=-1;
        if(pos<0) return -1;
        else if(pos<nodes.size()) return pos;
        else{//pos>=nodes.size()
            return loopIndex+((pos-loopIndex)%(nodes.size()-loopIndex));
        }
    }

    int size(){
        return nodes.size();
    }

    String get(int index){
        return nodes.get(index);
    }

    // premise: before calling this function, the position pos is already explained over path^startPos
    // this function return false when the next position of the current position pos is already explained, return true otherwise
    boolean needExplainNextPosition(int startPos, int curPos){
        int startIdx=at(startPos);
        int idx=at(curPos);
        if((startIdx<loopIndex && idx==this.size()-1) ||
                (startIdx>=loopIndex && at(curPos+1)==startIdx))
            return false;
        else
            return true;
    }

}


public class RTCTLs_ModelCheckAlg extends ModelCheckAlgI {
    MCTKFrame mainFrame;
    private Spec property;

    //private Spec chkProp; // the property actually checked
    private BDD chkBdd; // the BDD obtained by checking chkProp
    private BDDVarSet stateVarSet; // the set of state variables of the original model

    private BDD D_feasibleStates = null; // the feasible states of the original model D to be checked
    private BDD feasibleStatesForWitnessE=null;

    private RTCTLsTester testerInfo =null;

    private int tester_id = 0;
    private int field_id = 0;
    private int createdPathNumber = 0; // the number of the paths currently created

    private Vector<NodePath> trunkNodePaths = new Vector<NodePath>();

    private GraphExplainRTCTLs graph; //used for displaying witness graph

    public void setGraph(GraphExplainRTCTLs g){
        this.graph = g;
    }
    public GraphExplainRTCTLs getGraph() {
        return this.graph;
    }

//    private static HashMap<Spec, BDD> SpecBDDMap = new HashMap<Spec, BDD>(); //spec <-> BDD
//    private static HashMap<Spec, SMVModule> SpecTesterMap = new HashMap<>();//spec <-> Tester

    private static LinkedHashMap<String, CacheSpecTesterInfo> cacheSpecTesters; // <specStr, (spec, specBdd, specTester)>

    public Spec getProperty() {
        return property;
    }

    public void setProperty(Spec property) {
        this.property = property;
    }


    /**
     * <p>
     * Constructor for a given specification \phi (as a formula in temporal
     * logic) which we want to decide whether \phi is valid over finite state
     * program P, i.e. whether all the computations of the design satisfy \phi.
     * </p>
     *
     * @param design   The design to check.
     * @param property The property to check.
     */
    public RTCTLs_ModelCheckAlg(Module design, Spec property) {
        super(design);
        this.property = property;
        stateVarSet = design.moduleUnprimeVars();
    }

    /**
     * <p>
     * Constructor for composing a tester with the design, and perform model
     * checking. If the composition is feasible then a counter example is
     * thrown.
     * </p>
     *
     * @param design The design to check.
     */
    public RTCTLs_ModelCheckAlg(MCTKFrame mainFrame, Module design) {
        super(design);
        this.mainFrame=mainFrame;
//		this.tester = user_tester;
        stateVarSet = design.moduleUnprimeVars();
    }



    // return the BDDs of the aux variables for the tester
    private BDDVarSet tester_getAuxVars_BDDVarSet(SMVModule tester) {
        BDDVarSet vs = Env.getEmptySet();
        for (ModuleBDDField var : tester.getAll_couples()) {
            vs = vs.id().union(var.support());
        }
        return vs;
    }

    BDDVarSet getBDDVarSet(LinkedHashSet<ModuleBDDField> moduleVarSet) {
        BDDVarSet vs = Env.getEmptySet();
        for (ModuleBDDField var : moduleVarSet)
            vs = vs.id().union(var.support());
        return vs;
    }

/*
    LinkedHashSet<ModuleBDDField> getSpecAuxVars(Spec spec) {
         CacheSpecTesterInfo specInfo = tester.getSpecInfo(spec.toString());
         if(specInfo==null)
             return new LinkedHashSet<ModuleBDDField>();
         else
             return specInfo.auxVars; // not null
    }

    boolean setSpecAuxVars(Spec spec, LinkedHashSet<ModuleBDDField> auxVars) {
        CacheSpecTesterInfo specInfo = tester.getSpecInfo(spec.toString());
        if(specInfo==null) return false;
        if(auxVars==null) specInfo.auxVars=null;
        else specInfo.auxVars = (LinkedHashSet<ModuleBDDField>) auxVars.clone();
        return true;
    }
 */

    // Premises: auxVarNames is the vector of aux variables created for spec, it must be created before used
    // Results: this.tester is created for spec; this.testerAuxVarNames contains the set of created aux variables
    public BDD sat(Spec spec)
            throws ModuleException, SMVParseException, ModelCheckException, ModelCheckAlgException, SpecException {
        if (spec instanceof SpecBDD) {
            return ((SpecBDD) spec).getVal();
        }
        if (spec instanceof SpecRange || spec instanceof SpecAgentIdentifier) return null;

        CacheSpecTesterInfo specInfo = testerInfo.getSpecInfo(spec.toString());
        if(specInfo!=null) return specInfo.specBdd;
        // now spec is not in the cache

        SMVModule design = (SMVModule) getDesign();
        SpecExp se = (SpecExp) spec;
        Operator op = se.getOperator();
        Spec[] child = se.getChildren();
        BDD lc = null, rc = null;
        ModuleBDDField x = null;
        BDD xBdd; // output variable of the tester

        BDD specBdd=null;

        LinkedHashSet<ModuleBDDField> auxVars=null;

        CacheSpecTesterInfo c1Info=null, c2Info=null;
        LinkedHashSet<SMVModule> testers=null; // the set of sub-testers for spec

        //-----------------------------------------------------------------------------------------------------------
        // logical connectives
        //-----------------------------------------------------------------------------------------------------------
        if (op == Operator.NOT) {
            lc = sat(child[0]);
            specBdd = lc.not();

            testerInfo.putSpecInfo(spec,specBdd,
                    testerInfo.unionTesters(child[0].toString(),null,null),null);
            return specBdd;
        }
        if (op == Operator.AND) {
            lc = sat(child[0]);
            rc = sat(child[1]);
            specBdd=lc.and(rc);

            testerInfo.putSpecInfo(spec,specBdd,
                    testerInfo.unionTesters(child[0].toString(),child[1].toString(),null),null);
            return specBdd;
        }
        if (op == Operator.OR) {
            lc = sat(child[0]);
            rc = sat(child[1]);
            specBdd=lc.or(rc);

            testerInfo.putSpecInfo(spec,specBdd,
                    testerInfo.unionTesters(child[0].toString(),child[1].toString(),null),null);
            return specBdd;
        }
        //-----------------------------------------------------------------------------------------------------------
        // path quantifiers
        // a new tester will be constructed for the subformula restricted by each path quantifier
        //-----------------------------------------------------------------------------------------------------------
        if (op == Operator.EE) return satEE(spec);
        if (op == Operator.AA) return satAA(spec);

        //-----------------------------------------------------------------------------------------------------------
        // temporal operators
        // the tester of the temporal formula spec will be composited into specTester
        //-----------------------------------------------------------------------------------------------------------
        if (op == Operator.NEXT) {
            lc = sat(child[0]);

            SMVModule T = testerInfo.buildEmptyAtomTester(spec.toString());
            x = T.addVar("X"); // boolean variable
            xBdd = x.getDomain().ithVar(1);
            BDD p_lc = Env.prime(lc);
            T.conjunctTrans(xBdd.imp(p_lc));
            consoleOutput(0, "emph", "Variable " + x.toFullString() + " is created for " + simplifySpecString(spec.toString(),false) + "\n");

            //xBdd.andWith(design.feasible());

            testerInfo.putSpecInfo(spec,xBdd,
                    testerInfo.unionTesters(child[0].toString(),null,T),null);
            return xBdd;
        }
        if (op == Operator.UNTIL) {
            lc = sat(child[0]);
            rc = sat(child[1]);

            SMVModule T = testerInfo.buildEmptyAtomTester(spec.toString());
            x = T.addVar("X"); // boolean variable
            xBdd = x.getDomain().ithVar(1);
            BDD p_x = Env.prime(xBdd);
            //tester.addInitial(xBdd.imp(c1.or(c2)));
            T.conjunctTrans(xBdd.imp(rc.or(lc.and(p_x))));
            T.addJustice(xBdd.imp(rc));
            consoleOutput(0, "emph", "Variable " + x.toFullString() + " is created for " + simplifySpecString(spec.toString(),false) + "\n");

            //xBdd.andWith(design.feasible());

            testerInfo.putSpecInfo(spec,xBdd,
                    testerInfo.unionTesters(child[0].toString(),child[1].toString(),T),null);
            return xBdd;
        }
        if (op == Operator.RELEASES) {
            lc = sat(child[0]);
            rc = sat(child[1]);

            SMVModule T = testerInfo.buildEmptyAtomTester(spec.toString());
            x = T.addVar("X"); // boolean variable
            xBdd = x.getDomain().ithVar(1);
            BDD p_x = Env.prime(xBdd);
            T.conjunctTrans(xBdd.imp(rc.and(lc.or(p_x))));
            consoleOutput(0, "emph", "Variable " + x.toFullString() + " is created for " + simplifySpecString(spec.toString(),false) + "\n");

            //BDD feas=design.feasible2(); //this.ce_fair_g(Env.TRUE());
            //xBdd.andWith(feas);

            testerInfo.putSpecInfo(spec,xBdd,
                    testerInfo.unionTesters(child[0].toString(),child[1].toString(),T),null);
            return xBdd;
        }
        if (op == Operator.B_UNTIL) {
            return satBUNTIL(spec);
        }
        if (op == Operator.B_RELEASES) {
            return satBRELEASE(spec);
        }
        //otherwise
        throw new ModelCheckException("Cannot handle the specification " + spec + ".");
    }

    private SMVModule context_module = null; // the context module used for bexp()
    public void set_context_module(SMVModule module) {context_module=module;} // this function must be called before bexp()


    // set_context_module() must be called before this function
    public AbstractBinaryOperator bexp(
            Object e1, // e1 is an instance of ModuleBDDField(variable) or String(constant) or AbstractElement(expression)
            String op,
            Object e2 // e2 is an instance of ModuleBDDField(variable) or String(constant) or AbstractElement(expression)
    ) throws SMVParseException, ModelCheckException {
        AbstractElement ae1, ae2;
        if(e1 instanceof ModuleBDDField) { //e1 is variable
            ae1 = new ValueDomStmt(context_module, (ModuleBDDField) e1);
        }else if(e1 instanceof String){ //e1 is constant
            ae1 = new ValueConsStrStmt(context_module, new String[]{(String)e1});
        }else if(e1 instanceof AbstractElement){
            ae1 = (AbstractElement) e1;
        }else throw new ModelCheckException("ERROR: The left operand is not variable, constant or expresion.");

        if(e2 instanceof ModuleBDDField) { //e2 is variable
            ae2 = new ValueDomStmt(context_module, (ModuleBDDField) e2);
        }else if(e2 instanceof String){ //e2 is constant
            ae2 = new ValueConsStrStmt(context_module, new String[]{(String)e2});
        }else if(e1 instanceof AbstractElement){
            ae2 = (AbstractElement) e2;
        }else throw new ModelCheckException("ERROR: The right operand is not variable, constant or expresion.");

        if (op.equals(">")) return new OpGT(ae1,ae2);
        else if (op.equals(">=")) return new OpGE(ae1,ae2);
        else if (op.equals("<")) return new OpLT(ae1,ae2);
        else if (op.equals("<=")) return new OpLE(ae1,ae2);
        else if (op.equals("=")) return new OpEqual(ae1,ae2);
        else if (op.equals("-")) return new OpMinus(ae1,ae2);
        else throw new ModelCheckException("Currently cannot handle the operator " + op + ".");
    }

    public BDD bexp2bdd(AbstractBinaryOperator bexp) throws SMVParseException {
        return new StmtOperator(context_module, bexp).eval_stmt().toBDD();
    }

    public BDD satBUNTIL(Spec spec) throws ModuleException, ModelCheckException, SMVParseException, ModelCheckAlgException, SpecException {
        SMVModule design = (SMVModule) getDesign();
        SpecExp se = (SpecExp) spec;
        Operator op = se.getOperator();
        if (op != Operator.B_UNTIL) return null;

        Spec[] child = se.getChildren();
        BDD c1 = null, c2 = null;
        SpecRange range = (SpecRange) child[1];
        int a = range.getFrom(), b = range.getTo();

        if(a<0) throw new ModelCheckException("The lower bound of " + spec + "cannot be less than 0.");
        if(b<0) throw new ModelCheckException("The upper bound of " + spec + "cannot be less than 0.");
        if(a>b) throw new ModelCheckException("The lower bound of " + spec + "cannot be larger than the upper bound.");
        if(a==0 && b==0) {
            // spec=c1 BU 0..0 c2 which equals to c2
            c2 = sat(child[2]);
            testerInfo.putSpecInfo(spec,c2,
                    testerInfo.unionTesters(null,child[2].toString(),null),null);
            return c2;
        }

        // now 0<=a<=b and !(a=0 and b=0
        c1 = sat(child[0]);
        c2 = sat(child[2]);

        ModuleBDDField x = null, l = null, w = null;
        BDD xBdd; // output variable of the tester

        SMVModule T = testerInfo.buildEmptyAtomTester(spec.toString());
        x = T.addVar("X"); // boolean variable
        xBdd = x.getDomain().ithVar(1);

        if ((a == b) || (a == 0 && b > 0)) {
            l = T.addVar("L", 0, b);
            w = null;
            ValueDomStmt xe = new ValueDomStmt(T, x); // the expression of variable x
            OpNext pxe = new OpNext(xe); // the expression of next(x)
            ValueDomStmt le = new ValueDomStmt(T, l); // the expression of variable l
            OpNext ple = new OpNext(le); // the expression of next(l)

            set_context_module(T);
            BDD lGT0 = bexp2bdd(bexp(l, ">", "0")); // l>0
            BDD NxE1 = bexp2bdd(bexp(pxe,"=","1")); // x'=1
            BDD NlElM1 = bexp2bdd(bexp(ple,"=",bexp(l,"-","1"))); // l'=l-1
            BDD lE0 = bexp2bdd(bexp(l,"=","0")); //l=0
            BDD lEb = bexp2bdd(bexp(l,"=",""+b)); //l=b
/*          BDD lGT0 = new StmtOperator(tester, new OpGT(new ValueDomStmt(tester, l),
                    new ValueConsStrStmt(tester, new String[]{"0"}))).eval_stmt().toBDD(); //l>0
            BDD NxE1 = new StmtOperator(tester, new OpEqual(new OpNext(new ValueDomStmt(tester, x)), //next(x)
                    new ValueConsStrStmt(tester, new String[]{"1"}))).eval_stmt().toBDD();
            BDD NlElM1 = new StmtOperator(tester, new OpEqual(new OpNext(new ValueDomStmt(tester, l)), //next(l)=l-1
                    new OpMinus(new ValueDomStmt(tester, l), new ValueConsStrStmt(tester, new String[]{"1"})))).eval_stmt().toBDD();
            BDD lE0 = new StmtOperator(tester, new OpEqual(new ValueDomStmt(tester, l),
                    new ValueConsStrStmt(tester, new String[]{"0"}))).eval_stmt().toBDD(); //l=0
            BDD lEb = new StmtOperator(tester, new OpEqual(new ValueDomStmt(tester, l),
                    new ValueConsStrStmt(tester, new String[]{"" + b}))).eval_stmt().toBDD(); //l=b
*/
            if (a == b) {
                T.conjunctTrans(xBdd.and(lGT0).imp(c1.and(NxE1).and(NlElM1))); // (x & l>0) -> (c1 & x' & l'=l-1)
                T.conjunctTrans(xBdd.and(lE0).imp(c2)); // (x & l=0) -> c2
            } else { // a==0 && b>0
                T.conjunctTrans(xBdd.and(lGT0).imp(c2.or(c1.and(NxE1).and(NlElM1)))); // (x & l>0) -> (c2 | (c1 & x' & l'=l-1))
                T.conjunctTrans(xBdd.and(lE0).imp(c2)); // (x & l=0) -> c2
            }

            BDD specBdd=xBdd.and(lEb); // x & l=b;
            testerInfo.putSpecInfo(spec,specBdd,
                    testerInfo.unionTesters(child[0].toString(),child[2].toString(),T),null);
            return specBdd;
        } else { // 0<a<b
            l = T.addVar("L", 0, a);
            w = T.addVar("W", 0, b - a);

            ValueDomStmt xe = new ValueDomStmt(T, x); // the expression of variable x
            OpNext pxe = new OpNext(xe); // the expression of next(x)
            ValueDomStmt le = new ValueDomStmt(T, l); // the expression of variable l
            OpNext ple = new OpNext(le); // the expression of next(l)
            ValueDomStmt we = new ValueDomStmt(T, w); // the expression of variable w
            OpNext pwe = new OpNext(we); // the expression of next(w)

            set_context_module(T);
            BDD lGT0 = bexp2bdd(bexp(l, ">", "0")); // l>0
            BDD NxE1 = bexp2bdd(bexp(pxe,"=","1")); // x'=1
            BDD NlElM1 = bexp2bdd(bexp(ple,"=",bexp(l,"-","1"))); // l'=l-1
            BDD lE0 = bexp2bdd(bexp(l,"=","0")); //l=0
            BDD lEa = bexp2bdd(bexp(l,"=",""+a)); //l=a
            BDD wGT0 = bexp2bdd(bexp(w, ">", "0")); // w>0
            BDD NwEw = bexp2bdd(bexp(pwe,"=", we)); // w'=w
            BDD NlE0 = bexp2bdd(bexp(ple, "=", "0")); // l'=0
            BDD NwEwM1 = bexp2bdd(bexp(pwe,"=",bexp(w,"-","1"))); // w'=w-1
            BDD wE0 = bexp2bdd(bexp(w,"=","0")); //w=0
            BDD wEbMa = bexp2bdd(bexp(w, "=", ""+(b-a))); //w=b-a

            T.conjunctTrans(xBdd.and(lGT0).and(wGT0).imp(c1.and(NxE1).and(NlElM1).and(NwEw))); // (x & l>0 & w>0) -> (c1 & x' & l'=l-1 & w'=w)
            T.conjunctTrans(xBdd.and(lE0).and(wGT0).imp(c2.or(c1.and(NxE1).and(NlE0).and(NwEwM1)))); // (x & l=0 & w>0) -> (c2 | (c1 & x' & l'=0 & w'=w-1))
            T.conjunctTrans(xBdd.and(lE0).and(wE0).imp(c2)); // (x & l=0 & w=0) -> c2

            BDD specBdd = xBdd.and(lEa).and(wEbMa); // x & l=a & w=b-a;
            testerInfo.putSpecInfo(spec,specBdd,
                    testerInfo.unionTesters(child[0].toString(),child[2].toString(),T),null);
            return specBdd;
        }
    }

    public BDD satBRELEASE(Spec spec) throws ModuleException, ModelCheckException, SMVParseException, ModelCheckAlgException, SpecException {
        SMVModule design = (SMVModule) getDesign();
        SpecExp se = (SpecExp) spec;
        Operator op = se.getOperator();
        if (op != Operator.B_RELEASES) return null;

        Spec[] child = se.getChildren();
        BDD c1 = null, c2 = null;
        SpecRange range = (SpecRange) child[1];
        int a = range.getFrom(), b = range.getTo();

        if(a<0) throw new ModelCheckException("The lower bound of " + spec + "cannot be less than 0.");
        if(b<0) throw new ModelCheckException("The upper bound of " + spec + "cannot be less than 0.");
        if(a>b) throw new ModelCheckException("The lower bound of " + spec + "cannot be larger than the upper bound.");
        if(a==0 && b==0) {
            // spec=c1 BR 0..0 c2 which equals to c2
            c2 = sat(child[2]);
            testerInfo.putSpecInfo(spec,c2,
                    testerInfo.unionTesters(null,child[2].toString(),null),null);
            return c2;
        }

        // now 0<=a<=b and !(a=0 and b=0)
        c1 = sat(child[0]);
        c2 = sat(child[2]);

        ModuleBDDField x = null, l = null, w = null;
        BDD xBdd; // output variable of the tester

        SMVModule T = testerInfo.buildEmptyAtomTester(spec.toString());
        x = T.addVar("X"); // boolean variable
        xBdd = x.getDomain().ithVar(1);

        if ((a == b) || (a == 0 && b > 0)) {
            l = T.addVar("L", 0, b);
            w = null;
            ValueDomStmt xe = new ValueDomStmt(T, x); // the expression of variable x
            OpNext pxe = new OpNext(xe); // the expression of next(x)
            ValueDomStmt le = new ValueDomStmt(T, l); // the expression of variable l
            OpNext ple = new OpNext(le); // the expression of next(l)

            set_context_module(T);
            BDD lGT0 = bexp2bdd(bexp(l, ">", "0")); // l>0
            BDD NxE1 = bexp2bdd(bexp(pxe,"=","1")); // x'=1
            BDD NlElM1 = bexp2bdd(bexp(ple,"=",bexp(l,"-","1"))); // l'=l-1
            BDD lE0 = bexp2bdd(bexp(l,"=","0")); //l=0
            BDD lEb = bexp2bdd(bexp(l,"=",""+b)); //l=b

            if (a == b) {
                T.conjunctTrans(xBdd.and(lGT0).imp(c1.or(NxE1.and(NlElM1)))); // (x & l>0) -> (c1 | (x' & l'=l-1))
                T.conjunctTrans(xBdd.and(lE0).imp(c2)); // (x & l=0) -> c2
            } else { // a==0 && b>0
                T.conjunctTrans(xBdd.and(lGT0).imp(c2.and(c1.or(NxE1.and(NlElM1))))); // (x & l>0) -> (c2 & (c1 | (x' & l'=l-1)))
                T.conjunctTrans(xBdd.and(lE0).imp(c2)); // (x & l=0) -> c2
            }

            BDD specBdd = xBdd.and(lEb); // x & l=b;
            testerInfo.putSpecInfo(spec,specBdd,
                    testerInfo.unionTesters(child[0].toString(),child[2].toString(),T),null);
            return specBdd;
        } else { // 0<a<b
            l = T.addVar("L", 0, a);
            w = T.addVar("W", 0, b - a);
            ValueDomStmt xe = new ValueDomStmt(T, x); // the expression of variable x
            OpNext pxe = new OpNext(xe); // the expression of next(x)
            ValueDomStmt le = new ValueDomStmt(T, l); // the expression of variable l
            OpNext ple = new OpNext(le); // the expression of next(l)
            ValueDomStmt we = new ValueDomStmt(T, w); // the expression of variable w
            OpNext pwe = new OpNext(we); // the expression of next(w)

            set_context_module(T);
            BDD lGT0 = bexp2bdd(bexp(l, ">", "0")); // l>0
            BDD NxE1 = bexp2bdd(bexp(pxe,"=","1")); // x'=1
            BDD NlElM1 = bexp2bdd(bexp(ple,"=",bexp(l,"-","1"))); // l'=l-1
            BDD lE0 = bexp2bdd(bexp(l,"=","0")); //l=0
            BDD lEa = bexp2bdd(bexp(l,"=",""+a)); //l=a
            BDD wGT0 = bexp2bdd(bexp(w, ">", "0")); // w>0
            BDD NwEw = bexp2bdd(bexp(pwe,"=", we)); // w'=w
            BDD NlE0 = bexp2bdd(bexp(ple, "=", "0")); // l'=0
            BDD NwEwM1 = bexp2bdd(bexp(pwe,"=",bexp(w,"-","1"))); // w'=w-1
            BDD wE0 = bexp2bdd(bexp(w,"=","0")); //w=0
            BDD wEbMa = bexp2bdd(bexp(w, "=", ""+(b-a))); //w=b-a

            T.conjunctTrans(xBdd.and(lGT0).and(wGT0).imp(c1.or(NxE1.and(NlElM1).and(NwEw)))); // (x & l>0 & w>0) -> (c1 | (x' & l'=l-1 & w'=w))
            T.conjunctTrans(xBdd.and(lE0).and(wGT0).imp(c2.and(c1.or(NxE1.and(NlE0).and(NwEwM1))))); // (x & l=0 & w>0) -> (c2 & (c1 | (x' & l'=0 & w'=w-1)))
            T.conjunctTrans(xBdd.and(lE0).and(wE0).imp(c2)); // (x & l=0 & w=0) -> c2

            BDD specBdd = xBdd.and(lEa).and(wEbMa); // x & l=a & w=b-a;
            testerInfo.putSpecInfo(spec,specBdd,
                    testerInfo.unionTesters(child[0].toString(),child[2].toString(),T),null);
            return specBdd;
        }
    }

    // return the set of states satisfying spec
    // spec = EE c1
    public BDD satEE(Spec spec) throws ModelCheckException, ModuleException, SMVParseException, ModelCheckAlgException, SpecException {
        SMVModule design = (SMVModule) getDesign();
        SpecExp se = (SpecExp) spec;
        Operator op = se.getOperator();
        Spec[] child = se.getChildren();
        if (op != Operator.EE) return null;

        BDD c1 = sat(child[0]); // build the sub-tester of child[0]

        CacheSpecTesterInfo c1Info = null;
        c1Info = testerInfo.getSpecInfo(child[0].toString());
        if(c1Info==null) return null;

        BDD specBdd=null;
        if(c1Info.testerSet.size()<=0){
            //the tester for c1 is EMPTY
            specBdd = D_feasibleStates.and(c1);

            testerInfo.putSpecInfo(spec, specBdd, null,D_feasibleStates);
        }else {
            // now the tester for c1 is NOT empty
            // (1) compose the sub-testers of child[0] to the original model D, such that design=D||T_c1
            for (SMVModule tester : c1Info.testerSet) design.syncComposition(tester); // design=D||T_c1

            // (2) compute the set specBdd of feasible states of D satisfying c1
            BDD DTc1_feas = design.feasible();

            LinkedHashSet<ModuleBDDField> c1AuxVars = c1Info.getAuxVars();
            BDDVarSet bddC1AuxVarSet = getBDDVarSet(c1AuxVars);
            specBdd = DTc1_feas.and(c1).exist(bddC1AuxVarSet);

            // (3) decompose the sub-testers of child[0] from the current model, such that design=D, the original model
            for (SMVModule tester : c1Info.testerSet) design.decompose(tester); // design=D

            testerInfo.putSpecInfo(spec, specBdd, null, DTc1_feas);
        }

        return specBdd;
    }

    // return the set of states satisfying spec
    // spec = AA child[0]
    public BDD satAA(Spec spec) throws ModelCheckException, SMVParseException, ModuleException, ModelCheckAlgException, SpecException {
        SMVModule design = (SMVModule) getDesign();
        SpecExp se = (SpecExp) spec;
        Operator op = se.getOperator();
        Spec[] child = se.getChildren();
        if (op != Operator.AA) return null;

        Spec EnegC1 = NNF(new SpecExp( Operator.EE,
                                    new SpecExp(Operator.NOT, child[0])));
        BDD bddEnegC1 = sat(EnegC1);
        BDD specBdd = D_feasibleStates.and(bddEnegC1.not());
        testerInfo.putSpecInfo(spec, specBdd, null, D_feasibleStates);
        return specBdd;
    }
        // return the set of states satisfying spec
    // spec = AA child[0]
    public BDD satAA_old(Spec spec) throws ModelCheckException, SMVParseException, ModuleException, ModelCheckAlgException, SpecException {
        SMVModule design = (SMVModule) getDesign();
        SpecExp se = (SpecExp) spec;
        Operator op = se.getOperator();
        Spec[] child = se.getChildren();
        if (op != Operator.AA) return null;

        Spec negC1 = NNF(new SpecExp(Operator.NOT, child[0]));
        BDD bddNegC1 = sat(negC1);

        CacheSpecTesterInfo nc1Info=null;
        nc1Info = testerInfo.getSpecInfo(negC1.toString());
        if(nc1Info==null) return null;

        BDD specBdd=null;
        if(nc1Info.testerSet.size()<=0){
            // the tester for negC1 is EMPTY
            specBdd = D_feasibleStates.and(bddNegC1.not());

            testerInfo.putSpecInfo(spec,specBdd,null,D_feasibleStates);
        }else{
            // the tester for negC1 is NOT empty
            // (1) compose the sub-testers of !child[0] to the original model D, such that design=D||T_nc1
            for (SMVModule tester : nc1Info.testerSet) design.syncComposition(tester); // design=D||T_nc1

            // (2) compute the set specBdd of feasible states of D satisfying c1
            BDD DTnc1_feas = design.feasible();

            LinkedHashSet<ModuleBDDField> nc1AuxVars = nc1Info.getAuxVars();
            BDDVarSet bddNC1AuxVarSet = getBDDVarSet(nc1AuxVars);
            BDD DTnc1_feas_negC1 = DTnc1_feas.and(bddNegC1);
            BDD D_feas_negC1 = DTnc1_feas_negC1.exist(bddNC1AuxVarSet);
            specBdd = DTnc1_feas.and(D_feas_negC1.not()).exist(bddNC1AuxVarSet);

//        BDDVarSet allAuxVars = tester_getAuxVars_BDDVarSet(tester.module);
//        BDD D_feas_negC1 = DTnc1_feas_negC1.exist(allAuxVars); // the set of D-feasible states that does not satisfy c1
//        specBdd = feas.and(feas_negC1_no_auxVars.not()).and(negC1.not());
//        BDD specBdd = DTnc1_feas.and(D_feas_negC1.not());

            // (3) decompose the sub-testers of child[0] from the current model, such that design=D, the original model
            for (SMVModule tester : nc1Info.testerSet) design.decompose(tester); // design=D

            testerInfo.putSpecInfo(spec, specBdd, null, DTnc1_feas);
        }

        return specBdd;
    }

    //------------------------------------------------------------------------------------------------------------------
    // return the Negation Normal Form of spec, which includes the following operators:
    //   -- logic connectives NOT, AND, OR,
    //   -- temporal operators NEXT, FINALLY, GLOBALLY, UNTIL, RELEASES,
    //   -- bounded temporal operators B_UNTIL a..b, B_RELEASES a..b, where (0<a<b) or (a=0 and b>0)
    //   -- path quantifiers EE, AA, CAN_ENFORCE, CANNOT_AVOID
    //   -- epistemic modalities
    //------------------------------------------------------------------------------------------------------------------
    public static Spec NNF(Spec spec) throws ModelCheckException, SpecException {
        //System.out.println("spec--------"+spec);
        if (!(spec instanceof SpecExp))
            return spec;
        SpecExp propExp = (SpecExp) spec;
        Operator op = propExp.getOperator();
        Spec[] child = propExp.getChildren();
        // unaryOp of LTL except for Not
        if (op == Operator.NEXT
//				||op == Operator.FINALLY
//				||op == Operator.GLOBALLY
/*
                ||op == Operator.HISTORICALLY)
				||op == Operator.NOT_PREV_NOT)
				||op == Operator.ONCE)
				||op == Operator.PREV)
*/
                // path quantifiers
                || op == Operator.EE
                || op == Operator.AA
                || op == Operator.CAN_ENFORCE
                || op == Operator.CANNOT_AVOID
                ) {
            return new SpecExp(op, NNF(child[0]));
        }

        // binaryOp of LTL except for KNOW,SKNOW,NKNOW

        // earse IMPLIE, IFF, XOR and XOR that include negation operator implicitly
        if (op == Operator.IMPLIES) { // c1 IMPLIES c2 ==> !c1 OR c2
            return new SpecExp(Operator.OR,
                    NNF(new SpecExp(Operator.NOT, child[0])),
                    NNF(child[1]));
        }
        if (op == Operator.IFF || op == Operator.XNOR) { // c1 IFF/XNOR c2 ==> (c1 AND c2) OR (!c1 AND !c2)
            Spec c1_c2 = new SpecExp(Operator.AND, NNF(child[0]), NNF(child[1]));
            Spec negC1_negC2 = new SpecExp(Operator.AND,
                    NNF(new SpecExp(Operator.NOT, child[0])),
                    NNF(new SpecExp(Operator.NOT, child[1])));
            return new SpecExp(Operator.OR, c1_c2, negC1_negC2);
        }
        if (op == Operator.XOR) { // c1 XOR c2 ==> !(c1 IFF c2)
            return NNF(new SpecExp(Operator.NOT,
                    new SpecExp(Operator.IFF, child[0], child[1])));
        }

        if (op == Operator.AND
                || op == Operator.OR

                // temporal operators
                || op == Operator.UNTIL
                || op == Operator.RELEASES
/*
                ||op == Operator.SINCE)
				||op == Operator.TRIGGERED)
*/
                ) {
            return new SpecExp(op, NNF(child[0]), NNF(child[1]));
        }

        // F c1 = true U c1
        if (op == Operator.FINALLY) {
            return new SpecExp(Operator.UNTIL, getTrueSpec(), NNF(child[0]));
        }
        // G c1 ==> ! F !c1 ==> !(true U !c1) ==> false R c1
        if (op == Operator.GLOBALLY) {
            return new SpecExp(Operator.RELEASES, getFalseSpec(), NNF(child[0]));
        }
        // F a..b f
        if (op == Operator.B_FINALLY) {
            SpecRange range = (SpecRange) child[0];
            if (range.getFrom() < 0) // a<0
                throw new ModelCheckException("The lower bound of " + spec + " cannot be less than 0.");
            // a>=0
            if (range.getTo() >= 0) { // b>=0
                if (range.getFrom() > range.getTo()) // a>b
                    throw new ModelCheckException("The lower bound of " + spec + " must be no lager than the upper bound.");
                // 0<=a<=b
                if (range.getFrom() == 0 && range.getTo() == 0) { // a=0 and b=0
                    return NNF(child[1]);
                } else { // F a..b f (b>0) ==> T U a..b f
                    Spec c1_nnf = NNF(child[1]);
                    Spec trueSpec = getTrueSpec();
                    return new SpecExp(Operator.B_UNTIL, trueSpec, range, c1_nnf);
                }
            } else //F a..b f (b<0) ==> T U a..a ( F f)
            {
                Spec c1_nnf = NNF(new SpecExp(Operator.FINALLY, child[1]));
                SpecRange newRange = new SpecRange(range.getFrom(), range.getFrom());
                return new SpecExp(Operator.B_UNTIL, getTrueSpec(), newRange, c1_nnf);
            }
        }
        // G a..b f
        if (op == Operator.B_GLOBALLY) {
            SpecRange range = (SpecRange) child[0];
            if (range.getFrom() < 0) // a<0
                throw new ModelCheckException("The lower bound of " + spec + " cannot be less than 0.");
            // a>=0
            if (range.getTo() >= 0) { // b>=0
                if (range.getFrom() > range.getTo()) // a>b
                    throw new ModelCheckException("The lower bound of " + spec + " must be no lager than the upper bound.");
                // 0<=a<=b
                if (range.getFrom() == 0 && range.getTo() == 0) { // a=0 and b=0
                    return NNF(child[1]);
                } else { // G a..b f (b>0) ==> ! F a..b !f ==> !(T U a..b !f) ==> false R a..b f
                    Spec c1_nnf = NNF(child[1]);
                    return new SpecExp(Operator.B_RELEASES, getFalseSpec(), range, c1_nnf);
                }
            } else // G a..b f (b<0) ==> G a..a G f ==> F a..a G f ==> T U a..a G f
            {
                Spec p1 = NNF(new SpecExp(Operator.GLOBALLY, child[1])); // p1 = G f
                SpecRange newRange = new SpecRange(range.getFrom(), range.getFrom());
                return new SpecExp(Operator.B_UNTIL, getTrueSpec(), newRange, p1);
            }
        }

        // tripletOp of LTL
        if (op == Operator.B_UNTIL || op == Operator.B_RELEASES) {
            SpecRange range = (SpecRange) child[1];
            //System.out.println(""+child[0]+child[1]+child[2]);
            if (range.getFrom() < 0) // a<0
                throw new ModelCheckException("The lower bound of " + spec + " cannot be less than 0.");
            // a>=0
            if (range.getTo() >= 0) { // b>=0
                if (range.getFrom() > range.getTo()) // a>b
                    throw new ModelCheckException("The lower bound of " + spec + " must be no lager than the upper bound.");
                // 0<=a<=b
                if (range.getFrom() == 0 && range.getTo() == 0) { // a=0 and b=0
                    return NNF(child[1]);
                } else { // c1 U/R a..b c2 (b>0)
                    Spec c1_nnf = NNF(child[0]);
                    Spec c2_nnf = NNF(child[2]);
                    return new SpecExp(op, c1_nnf, range, c2_nnf);
                }
            } else // c1 U/R a..b c2 (b<0) ==> c1 U/R a..a (c1 U/R c2)
            {
                Spec c1_nnf = NNF(child[0]);
                Spec c1_nnf_copy = NNF(child[0]);
                Spec c2_nnf = NNF(child[2]);
                Operator newOp = op == Operator.B_UNTIL ? Operator.UNTIL : Operator.RELEASES;
                Spec c1Copy_newOp_c2 = new SpecExp(newOp, c1_nnf_copy, c2_nnf);
                SpecRange newRange = new SpecRange(range.getFrom(), range.getFrom());
                return new SpecExp(op, c1_nnf, newRange, c1Copy_newOp_c2);
            }
        }
        // epistemic operators
        if (op == Operator.KNOW
                || op == Operator.NKNOW
                || op == Operator.SKNOW
                || op == Operator.NSKNOW
                ) {
            return new SpecExp(op, child[0], NNF(child[1]));
        }
        //---------------------------------------------------------------
        // when spec=!f, return the NNF of !f
        //---------------------------------------------------------------
        if (op == Operator.NOT) {
            Spec f = child[0];
            if (!(f instanceof SpecExp))
                return spec;
            SpecExp se = (SpecExp) f;
            Operator fOp = se.getOperator();
            Spec[] fChild = se.getChildren();
            // System.out.println("ops-"+ops+" 0-"+childs[0]+" 1-"+childs[1]+" 2-"+childs[2]);
            if (fOp == Operator.NOT) { // NNF(!!c1) ==> NNF(c1)
                return NNF(fChild[0]);
            }
            if (fOp == Operator.AND) { // !(c1 AND c2) ==> !c1 OR !c2
                return new SpecExp(Operator.OR, NNF(new SpecExp(Operator.NOT, fChild[0])), NNF(new SpecExp(Operator.NOT, fChild[1])));
            }
            if (fOp == Operator.OR) { // !(c1 OR c2) ==> !c1 AND !c2
                return new SpecExp(Operator.AND, NNF(new SpecExp(Operator.NOT, fChild[0])), NNF(new SpecExp(Operator.NOT, fChild[1])));
            }
            // for f, earse IMPLIE, IFF, XOR and XOR that include negation operator implicitly
            if (fOp == Operator.IMPLIES) { // !(c1 IMPLIES c2) ==> c1 AND !c2
                return new SpecExp(Operator.AND,
                        NNF(fChild[0]),
                        NNF(new SpecExp(Operator.NOT, fChild[1])));
            }
            if (fOp == Operator.IFF || fOp == Operator.XNOR) { // !(c1 IFF/XNOR c2) ==> (c1 AND !c2) OR (!c1 AND c2)
                Spec c1_negC2 = new SpecExp(Operator.AND, NNF(fChild[0]), NNF(new SpecExp(Operator.NOT, fChild[1])));
                Spec negC1_C2 = new SpecExp(Operator.AND, NNF(new SpecExp(Operator.NOT, fChild[0])), NNF(fChild[1]));
                return new SpecExp(Operator.OR, c1_negC2, negC1_C2);
            }
            if (fOp == Operator.XOR) { // !(c1 XOR c2) ==> (c1 IFF c2)
                return new SpecExp(Operator.IFF, NNF(fChild[0]), NNF(fChild[1]));
            }
            if (fOp == Operator.EE) { // ! EE c1 ==> AA !c1
                return new SpecExp(Operator.AA, NNF(new SpecExp(Operator.NOT, fChild[0])));
            }
            if (fOp == Operator.AA) { // ! AA c1 ==> EE !c1
                return new SpecExp(Operator.EE, NNF(new SpecExp(Operator.NOT, fChild[0])));
            }
            if (fOp == Operator.CAN_ENFORCE) { // ! CAN_ENFORCE c1 ==> CANNOT_AVOID !c1
                Spec[] elements = new Spec[fChild.length];
                for (int i = 0; i < fChild.length - 1; i++) elements[i] = fChild[i];
                elements[fChild.length - 1] = NNF(new SpecExp(Operator.NOT, fChild[fChild.length - 1]));
                return new SpecExp(Operator.CANNOT_AVOID, elements);
            }
            if (fOp == Operator.CANNOT_AVOID) { // ! CANNOT_AVOID c1 ==> CAN_ENFORCE !c1
                Spec[] elements = new Spec[fChild.length];
                for (int i = 0; i < fChild.length - 1; i++) elements[i] = fChild[i];
                elements[fChild.length - 1] = NNF(new SpecExp(Operator.NOT, fChild[fChild.length - 1]));
                return new SpecExp(Operator.CAN_ENFORCE, elements);
            }

            if (fOp == Operator.NEXT) { // ! X c1 ==> X !c1
                return new SpecExp(fOp, NNF(new SpecExp(Operator.NOT, fChild[0])));
            }
            // !(F c1) ==> !(true U c1) ==> false R !c1
            if (fOp == Operator.FINALLY) {
                Spec negC1 = NNF(new SpecExp(Operator.NOT, fChild[0]));
                return new SpecExp(Operator.RELEASES, getFalseSpec(), negC1);
            }
            // !G c1 ==> true U !c1
            if (fOp == Operator.GLOBALLY) {
                Spec negC1 = NNF(new SpecExp(Operator.NOT, fChild[0]));
                return new SpecExp(Operator.UNTIL, getTrueSpec(), negC1);
            }
            // !(F a..b c1)
            if (fOp == Operator.B_FINALLY) {
                SpecRange range = (SpecRange) fChild[0];
                if (range.getFrom() < 0) // a<0
                    throw new ModelCheckException("The lower bound of " + f + " cannot be less than 0.");
                // a>=0
                if (range.getTo() >= 0) { // b>=0
                    if (range.getFrom() > range.getTo()) // a>b
                        throw new ModelCheckException("The lower bound of " + f + " must be no lager than the upper bound.");
                    // 0<=a<=b
                    if (range.getFrom() == 0 && range.getTo() == 0) { // a=0 and b=0
                        return NNF(new SpecExp(Operator.NOT, fChild[1]));
                    } else { // if b>0, then !(F a..b c1) ==> !(T U a..b c1) ==> false R a..b !c1
                        Spec negC1 = NNF(new SpecExp(Operator.NOT, fChild[1]));
                        return new SpecExp(Operator.B_RELEASES, getFalseSpec(), range, negC1);
                    }
                } else { // if b<0, then !(F a..b c1) ==> !(true U a..a (true U c1)) ==> false R a..a (false R !c1)
                    Spec negC1 = NNF(new SpecExp(Operator.NOT, fChild[1]));
                    Spec false_R_negC1 = new SpecExp(Operator.RELEASES, getFalseSpec(), negC1);
                    SpecRange newRange = new SpecRange(range.getFrom(), range.getFrom());
                    return new SpecExp(Operator.B_RELEASES, getFalseSpec(), newRange, false_R_negC1);
                }
            }
            if (fOp == Operator.B_GLOBALLY) {
                SpecRange range = (SpecRange) fChild[0];
                if (range.getFrom() < 0) // a<0
                    throw new ModelCheckException("The lower bound of " + f + " cannot be less than 0.");
                // a>=0
                if (range.getTo() >= 0) { // b>=0
                    if (range.getFrom() > range.getTo()) // a>b
                        throw new ModelCheckException("The lower bound of " + f + " must be no lager than the upper bound.");
                    // 0<=a<=b
                    if (range.getFrom() == 0 && range.getTo() == 0) { // a=0 and b=0
                        return NNF(new SpecExp(Operator.NOT, fChild[1]));
                    } else { // if b>0, then !G a..b c1 ==> true U a..b !c1
                        Spec negC1 = NNF(new SpecExp(Operator.NOT, fChild[1]));
                        return new SpecExp(Operator.B_UNTIL, getTrueSpec(), range, negC1);
                    }
                } else {    // if b<0, then !G a..b c1 ==> F a..b !c1 ==> true U a..a (true U !c1)
                    Spec negC1 = NNF(new SpecExp(Operator.NOT, fChild[1]));
                    Spec true_U_negC1 = new SpecExp(Operator.UNTIL, getTrueSpec(), negC1);
                    SpecRange newRange = new SpecRange(range.getFrom(), range.getFrom());
                    return new SpecExp(Operator.B_UNTIL, getFalseSpec(), newRange, true_U_negC1);
                }
            }
            if (fOp == Operator.UNTIL) { // !(c1 U c2) ==> !c1 R !c2
                return new SpecExp(Operator.RELEASES, NNF(new SpecExp(Operator.NOT, fChild[0])), NNF(new SpecExp(Operator.NOT, fChild[1])));
            }
            // !(c1 U a..b c2)
            if (fOp == Operator.B_UNTIL) {
                SpecRange range = (SpecRange) fChild[1];
                if (range.getFrom() < 0) // a<0
                    throw new ModelCheckException("The lower bound of " + f + " cannot be less than 0.");
                // a>=0
                if (range.getTo() >= 0) { // b>=0
                    if (range.getFrom() > range.getTo()) // a>b
                        throw new ModelCheckException("The lower bound of " + f + " must be no lager than the upper bound.");
                    // 0<=a<=b
                    if (range.getFrom() == 0 && range.getTo() == 0) { // a=0 and b=0
                        return NNF(new SpecExp(Operator.NOT, fChild[1]));
                    } else { // if b>=0, then !(c1 U a..b c2) ==> !c1 R a..b !c2
                        Spec negC1 = NNF(new SpecExp(Operator.NOT, fChild[0]));
                        Spec negC2 = NNF(new SpecExp(Operator.NOT, fChild[2]));
                        return new SpecExp(Operator.B_RELEASES, negC1, range, negC2);
                    }
                } else { // if b<0, then !(c1 U a..b c2) ==> !c1 R a..b !c2 ==> !c1 R a..a (!c1 R !c2)
                    Spec negC1 = NNF(new SpecExp(Operator.NOT, fChild[0]));
                    Spec negC2 = NNF(new SpecExp(Operator.NOT, fChild[2]));
                    Spec negC1_R_negC2 = new SpecExp(Operator.RELEASES, negC1, negC2);
                    SpecRange newRange = new SpecRange(range.getFrom(), range.getFrom());
                    return new SpecExp(Operator.B_RELEASES, negC1, newRange, negC1_R_negC2);
                }
            }
            if (fOp == Operator.RELEASES) { // !(c1 R c2) ==> !c1 U !c2
                return new SpecExp(Operator.UNTIL, NNF(new SpecExp(Operator.NOT, fChild[0])), NNF(new SpecExp(Operator.NOT, fChild[1])));
            }
            // !(c1 R a..b c2)
            if (fOp == Operator.B_RELEASES) {
                SpecRange range = (SpecRange) fChild[1];
                if (range.getFrom() < 0) // a<0
                    throw new ModelCheckException("The lower bound of " + f + " cannot be less than 0.");
                // a>=0
                if (range.getTo() >= 0) { // b>=0
                    if (range.getFrom() > range.getTo()) // a>b
                        throw new ModelCheckException("The lower bound of " + f + " must be no lager than the upper bound.");
                    // 0<=a<=b
                    if (range.getFrom() == 0 && range.getTo() == 0) { // a=0 and b=0
                        return NNF(new SpecExp(Operator.NOT, fChild[1]));
                    } else { // if b>=0, then !(c1 R a..b c2) ==> !c1 U a..b !c2
                        Spec negC1 = NNF(new SpecExp(Operator.NOT, fChild[0]));
                        Spec negC2 = NNF(new SpecExp(Operator.NOT, fChild[2]));
                        return new SpecExp(Operator.B_UNTIL, negC1, range, negC2);
                    }
                } else { // if b<0, then !(c1 R a..b c2) ==> !c1 U a..b !c2 ==> !c1 U a..a (!c1 U !c2)
                    Spec negC1 = NNF(new SpecExp(Operator.NOT, fChild[0]));
                    Spec negC2 = NNF(new SpecExp(Operator.NOT, fChild[2]));
                    Spec negC1_U_negC2 = new SpecExp(Operator.UNTIL, negC1, negC2);
                    SpecRange newRange = new SpecRange(range.getFrom(), range.getFrom());
                    return new SpecExp(Operator.B_UNTIL, negC1, newRange, negC1_U_negC2);
                }
            }
        } // end of the case of spec=!f

        throw new ModelCheckException("Failed to construct the Negation Normal Form of " + spec);
    }

    /**
     * @return The TRUE specification.
     */
    public static Spec getTrueSpec() {
        Spec[] str = Env.loadSpecString("LTLSPEC TRUE ;");
        return str[0];
    }

    /**
     * @return The FALSE  specification.
     */
    public static Spec getFalseSpec() {
        Spec[] str = Env.loadSpecString("LTLSPEC FALSE ;");
        return str[0];
    }

    /**
     * <p>
     * Preparing the ATL*K tester.
     * </p>
     *
     * @return Nothing.
     * @throws AlgExceptionI If The specification is not an LTL specification.
     * @see edu.wis.jtlv.lib.AlgI#preAlgorithm()
     */
    @Override
    public AlgResultI preAlgorithm() throws AlgExceptionI, SMVParseException, ModelCheckException, ModuleException {
        SMVModule design = (SMVModule) getDesign(); // without the composed tester...

/*
        design.removeAllIniRestrictions();//reset IniRestrictions
        SpecBDDMap.clear();
        for (Map.Entry<Spec, SMVModule> entry : SpecTesterMap.entrySet()) {
            ModuleBDDField[] TesterVar = null;
            if (entry.getValue() != null) {
                TesterVar = entry.getValue().getAllFields();
                for (ModuleBDDField var : TesterVar) {
                    all_couples.remove(var);
                }
                design.decompose(entry.getValue());
            }
        }
        SpecTesterMap.clear();//reset SpecTesterMap
        //design.allSucc(design.initial());
        design.feasible().free();
*/
        return null;
    }

    /**
     * <p>
     * Compose the design with the tester (user's or the one built from the LTL
     * specification), and check feasible states.
     * </p>
     *
     * @return A counter example if the algorithm fails (i.e.
     * {@link AlgResultPath}), or a string with
     * "VALID" (i.e. {@link AlgResultString}).
     * @see edu.wis.jtlv.lib.AlgI#doAlgorithm()
     */
    public AlgResultI doAlgorithm() throws AlgExceptionI, ModelCheckException, ModuleException, SMVParseException, SpecException {
        AlgResultString res = modelCheckingOneSpec(property);
        if(res.getResultStat()== AlgResultI.ResultStatus.succeed){
            consoleOutput(0,"emph", res.resultString());
        }
        if(res.getResultStat()== AlgResultI.ResultStatus.failed){
            consoleOutput(0,"error", res.resultString());
        }

        return res;
/*
        System.out.println("The original property: " + simplifySpecString(property,false));
        Spec chkProp;
        if (!property.isStateSpec()) {
            chkProp = NNF(new SpecExp(Operator.EE,
                    new SpecExp(Operator.NOT, property))); // newp = E !property
        } else { // the property is a state formula
            chkProp = NNF(new SpecExp(Operator.NOT, property)); // newp = !property
        }
        System.out.println("The negative propperty: " + simplifySpecString(chkProp,false));
        visibleVars = this.getRelevantVars(getDesign(), chkProp);
        // now chkProp is a state property

        SMVModule design = (SMVModule) getDesign(); // now design does not contain the tester

        int originalDesignVariablesNumber = design.getAll_couples().size();

        tester = new RTCTLsTester();
        design.syncComposition(tester.module); // the tester will be built in the following function sat()
        BDD chkBdd = sat(chkProp);
        // now design is the composition of the original model and the tester of the verified property

        // saving to the previous restriction state
        Vector<BDD> old_ini_restrictions = design.getAllIniRestrictions();
        int chkBdd_idx = design.restrictIni(chkBdd);
        BDD feas = design.feasible();// feas = the feasible states of D||T from design.init /\ chkBdd
        BDD Init_unSat = feas.and(design.initial()).and(chkBdd);
        // the initial_condition seems redundant
        design.removeIniRestriction(chkBdd_idx);
        design.setAllIniRestrictions(old_ini_restrictions);
        if (Init_unSat.isZero()) {
            design.decompose(tester.module);

            return new AlgResultString(true, "*** Property is TRUE ***");
        } else {
            graph = new GraphExplainRTCTLs("A counterexample of " + simplifySpecString(property, false), this);
            graph.addAttribute("ui.title", graph.getId());
            // design with the composed tester...
            // create the initial node
            BDD initState = Init_unSat.satOne(getDesign().moduleUnprimeVars(), false);
            Node n = graph.addNode(1, 0, initState);
            n.setAttribute("ui.class", "initialState");
            //graph.nodeAddSpec("1.0",chkProp);

//            boolean ok = witness(chkProp, n);
            boolean ok = witness(chkProp,n);
            String returned_msg = "";
            if (ok) {
                returned_msg = "*** Property is NOT VALID and its counterexample is as follows ***\n ";
                new Thread(){@Override
                    public void run() {
                        new ViewerExplainRTCTLs(graph);
                    }
                }.start();
            } else {
                returned_msg = "*** Property is NOT VALID ***\n ";
            }
            //design.decompose(tester.module); // delay the decomposition of tester to reserve the tester during showing the counterexample
            return new AlgResultString(false, returned_msg);
        }
*/
    }

    public BDD allPredsIn(BDD p, BDD q) {
        Module design = getDesign();
        for (FixPoint<BDD> ires = new FixPoint<BDD>(); ires.advance(q);)
            q = q.id().or(p.and(design.pred(q.id())));
        return q;
    }

    /**
     * <p>
     * Li-on's ce_fair_g package <br>
     * Compute all accessible states satisfying e_fair_g p
     * </p>
     * Handles both justice and compassion using Lions algorithm.
     *
     * @param p
     * @return
     */
    public BDD ce_fair_g(BDD p) {
        // some kind of variant to feasible algorithm.
        ModuleWithStrongFairness design = (ModuleWithStrongFairness) getDesign();
        // saving the previous restriction state.
        Vector<BDD> trans_restriction = design.getAllTransRestrictions();
        BDD res = design.allSucc(design.initial()).and(p);  // Line 2

        // Line 3
        design.restrictTrans(res.id().and(Env.prime(res.id())));

        for (FixPoint<BDD> ires = new FixPoint<BDD>(); ires.advance(res);) {
            // I'm doing reverse so it will be completely identical to the
            // original TLV implementation.
            for (int i = design.justiceNum() - 1; i >= 0; i--) {
                res = res.id().and(design.justiceAt(i));
                res = design.allPred(res.id()).and(design.allSucc(res.id())); // res is the set of states in the SCC, in which each circle path must past Justice i
                //if (printable) System.out.println("justice No. " + i);
                design.restrictTrans(res.id().and(Env.prime(res.id())));
            }

            for (int i = design.compassionNum() - 1; i >= 0; i--) {
                BDD tmp = res.id().and(design.qCompassionAt(i));
                tmp = design.allPred(tmp.id()).and(design.allSucc(tmp.id()));
                res = tmp.or(res.id().and(design.pCompassionAt(i).not()));
                //if (printable) System.out.println("compassion No. " + i);
                design.restrictTrans(res.id().and(Env.prime(res.id())));
            }

            design.removeAllTransRestrictions();
            BDD resPreds = design.pred(res.id());
            BDD resSuccs = design.succ(res.id());
            res = res.id().and(resSuccs).and(resPreds);
            design.restrictTrans(res.id().and(Env.prime(res.id())));
        }
        design.removeAllTransRestrictions();

        // returning to the previous restriction state.
        design.setAllTransRestrictions(trans_restriction);
        return this.allPredsIn(p.id(), res.id());
    }

    public AlgResultString modelCheckingOneSpec(Spec aProperty) throws SpecException, ModelCheckException, ModuleException, ModelCheckAlgException, SMVParseException {
        consoleOutput(0,"emph","Model checking RTCTL* property: " + simplifySpecString(aProperty,false) +"\n");
        if(statistic==null) statistic=new Statistic();
        else statistic.beginStatistic(true,false);

        Spec negProp;
        if (!aProperty.isStateSpec()) { // aProperty is not state formula
            negProp = NNF(new SpecExp(Operator.EE,
                    new SpecExp(Operator.NOT, aProperty))); // newp = E !property
        } else { // the property is a state formula
            negProp = NNF(new SpecExp(Operator.NOT, aProperty)); // newp = !property
        }
        consoleOutput(0,"emph","The negative propperty: " + simplifySpecString(negProp,false)+"\n");
        //visibleVars = this.getRelevantVars(getDesign(), chkProp);
        // now chkProp is a state property

        SMVModule design = (SMVModule) getDesign(); // now design does not contain the tester
        restoreOriginalModuleData();

        if(D_feasibleStates==null) D_feasibleStates = design.feasible();

        testerInfo = new RTCTLsTester();

        BDD bddNegProp = sat(negProp);

        BDD D_feas_init_unSat = D_feasibleStates.and(design.initial()).and(bddNegProp);
        if (D_feas_init_unSat.isZero()) {
            String res="*** Property is TRUE ***\n";
            consoleOutput(0,"emph", res);
            consoleOutput(0,"weak", statistic.getUsedInfo(true,true,true,true));
            return new AlgResultString(true, res);
        } else {
            graph = new GraphExplainRTCTLs(aProperty, negProp, testerInfo, this);
            graph.addAttribute("ui.title", graph.getId());
            // design with the composed tester...
            // create the initial node
            BDD initState = D_feas_init_unSat.satOne(getDesign().moduleUnprimeVars(), false);
            Node n = graph.addNode(1, 0, initState);
            n.setAttribute("ui.class", "initialState");

//            graph.nodeAddSpec(n.getId(), chkProp);
            boolean ok = witness(negProp, n);

            String returned_msg = "";
            returned_msg = "*** Property is FALSE ***\n";
            consoleOutput(0,"error", returned_msg);
            consoleOutput(0,"weak", statistic.getUsedInfo(true,true,true,true));

            new Thread(){@Override
                public void run() {
                    isOpeningCounterexampleWindow=true;
                try {
                    new ViewerExplainRTCTLs(graph);
                } catch (SpecException e) {
                    e.printStackTrace();
                }
            }
            }.start();
            //design.decompose(tester.module); // delay the decomposition of tester to reserve the tester during showing the counterexample
            return new AlgResultString(false, returned_msg);
        }
    }

    @Override
    public AlgResultI postAlgorithm() throws AlgExceptionI {
        //getDesign().removeAllTransRestrictions();
        return null;
    }

    public AlgResultI Swing_doAlgorithm() throws AlgExceptionI, ModelCheckException, ModuleException, SMVParseException, SpecException {
/*
        //System.out.println("model checking RTCTL* property: " + property);
        if (!property.isStateSpec()) {
            chkProp = NNF(new SpecExp(Operator.EE,
                    new SpecExp(Operator.NOT, property))); // newp = E !property
        } else { // the property is a state formula
            chkProp = NNF(new SpecExp(Operator.NOT, property)); // newp = !property
        }
        System.out.println("After NNF get the formula--" + chkProp);
        visibleVars = this.getRelevantVars(getDesign(), chkProp);
        // now chkProp is a state property
        SMVModule chkPropTester = null;
        chkBdd = sat(chkProp, chkPropTester); // after executing sat function, newpTester must be null
        SMVModule design = (SMVModule) getDesign(); // with the composed tester...
        // saving to the previous restriction state
        Vector<BDD> old_ini_restrictions = design.getAllIniRestrictions();
        design.restrictIni(chkBdd);
        BDD feas = design.feasible();// feas = fair(D||T)

        BDD Init_unSat = feas.and(design.initial()).and(chkBdd);
        // the initial_condition seems redundant
        if (Init_unSat.isZero()) {
            design.setAllIniRestrictions(old_ini_restrictions);
            return new AlgResultString(true, "*** Property is TRUE ***");
        } else {
            String returned_msg = "";
            if (isShowGraph == false) {//只输出结果
                returned_msg = "*** Property is NOT VALID ***";
                return new AlgResultString(false, returned_msg);
            } else {
                GraphExplainRTCTLs G = new GraphExplainRTCTLs("A counterexample of " + simplifySpecString(property.toString(), false), this);
                G.addAttribute("ui.label", G.getId());
                // design  with the composed tester...
                boolean ok = Witness_old(chkProp, Init_unSat, G, 1, 0);
                if (ok) {
                    returned_msg = "*** Property is NOT VALID and its counterexample is as follows ***\n ";
                    this.graph = G;
                } else {
                    returned_msg = "*** Property is NOT VALID ***\n ";
                }
                return new AlgResultString(false, returned_msg);
            }
        }

 */
        return null;
    }

    public static String simplifySpecString(Spec spec, boolean delTrue) throws SpecException {
        if(spec==null) return "";
        String res="";
        if(spec.isPropSpec()){
            res=spec.toBDD().toString();
        }else
            res=spec.toString();

        res = res.replaceAll("main.", "");
        if (delTrue) {
            res = res.replace("#[TRUE], \n", "");
            res = res.replace("#[TRUE]", "");
            res = res.replace("TRUE, \n", "");
            res = res.replace("TRUE", "");
        }
        return res;
    }

    public static String simplifySpecString(String specStr, boolean delTrue) throws SpecException {
        if(specStr==null) return "";
        String res = specStr.replaceAll("main.", "");
        if (delTrue) {
            res = res.replace("#[TRUE], \n", "");
            res = res.replace("#[TRUE]", "");
            res = res.replace("TRUE, \n", "");
            res = res.replace("TRUE", "");
        }
        return res;
    }
/*
    public static String simplifySpecString(String spec, boolean delTrue) {
        String res = spec.replaceAll("main.", "");
        if (delTrue) {
            res = res.replace("#[TRUE], \n", "");
            res = res.replace("#[TRUE]", "");
            res = res.replace("TRUE, \n", "");
            res = res.replace("TRUE", "");
        }
        return res;
    }
*/

    public static BDDVarSet getRelevantVars(Module m, Spec p) {
        // p.releventVars();
        BDDVarSet vars = Env.getEmptySet();
        if (p != null) {
            vars = vars.id().union(p.releventVars());
        }
        if (m != null) {
            // these are usually too much...
            // vars = vars.id().union(m.moduleUnprimeVars());

            // // removing running
            // ModuleBDDField r = m.getVar("running", false);
            // if (r != null) {
            // BDDVarSet rmR = Env.globalPrimeVarsMinus(r.other().support());
            // BDDVarSet rmPR = Env.globalUnprimeVarsMinus(r.support());
            // vars = Env.intersect(vars, rmR.union(rmPR));
            // }

            // fairness variables are important to illustrate feasibility.
            if (m instanceof ModuleWithWeakFairness) {
                ModuleWithStrongFairness weakM = (ModuleWithStrongFairness) m;
                for (int i = 0; i < weakM.justiceNum(); i++) {
                    vars = vars.id().union(weakM.justiceAt(i).support());
                }
            }
            if (m instanceof ModuleWithStrongFairness) {
                ModuleWithStrongFairness strongM = (ModuleWithStrongFairness) m;
                for (int i = 0; i < strongM.compassionNum(); i++) {
                    vars = vars.id().union(strongM.pCompassionAt(i).support());
                    vars = vars.id().union(strongM.qCompassionAt(i).support());
                }
            }
        }
        return vars;
    }

    // explain all state specs in this node, and all temporal specs taking this node as the first node of the path
    public boolean explainOneNode(String nodeId)
            throws ModelCheckAlgException, ModelCheckException, SpecException, SMVParseException, ModuleException {
        Node n=graph.getNode(nodeId); if(n==null) return false;

        // explain all state specs in this node
        int specNum=n.getAttribute("spriteSpecNumber"); // specNum is the number of state formulas in n.A
        for(int i=1; i<=specNum; i++){
            Sprite s=n.getAttribute("spriteSpec"+i); if(s==null) return false;
            boolean needExplained=s.getAttribute("needExplained");
            boolean explained=s.getAttribute("explained");
            if(needExplained && !explained){
                Spec spec=s.getAttribute("spec");
                SpecExp se=(SpecExp)spec;
                Operator op=se.getOperator();
                Spec[] child=se.getChildren();

                if(op==Operator.EE)
                   witnessE(child[0],n);
                else if(op==Operator.AND){
                    witness(child[0],n);
                    witness(child[1],n);
                }else if(op==Operator.OR){
                    BDD lc = testerInfo.getSpecBdd(child[0]); if(lc==null) return false; //SpecBDDMap.get(child[0]);
                    BDD state=graph.nodeGetBDD(nodeId);
                    if (!state.and(lc).isZero()) witness(child[0],n);
                    else witness(child[1],n);
                }

                s.setAttribute("explained",true);
            }
        }

        // explain all temporal specs over the suffix path that takes this node as the first node
        Iterator<Edge> iter = (Iterator<Edge>) n.getEachLeavingEdge().iterator();
        while(iter.hasNext()){
            Edge e = iter.next();
            //String eid=e.getId();
            specNum=e.getAttribute("spriteSpecNumber");
            for(int i=1;i<=specNum;i++){
                Sprite s=e.getAttribute("spriteSpec"+i); if(s==null) return false;
                boolean needExplained=s.getAttribute("needExplained");
                boolean explained=s.getAttribute("explained");
                if(needExplained && !explained){
                    Spec spec=s.getAttribute("spec");
                    NodePath path=s.getAttribute("path");
                    int pos=s.getAttribute("pos");
                    explainPath(spec,path,pos);
                    s.setAttribute("explained",true);
                }
            }
        }
        return true;
    }

    /*
    // Premise: spec must be a NNF formula, whose logical connectives are only !, /\ and \/, and ! only preceded assertions
    // Results: return true if in spec, all temporal operators and path quantifier EE are preceded by path quantifier AA;
    //          return false otherwise
    boolean specNeedExplain(Spec spec){
        if(spec instanceof SpecBDD) return false;
        SpecExp se=(SpecExp)spec;
        Operator op=se.getOperator();
        Spec[] child=se.getChildren();

        if(op==Operator.NOT) return false; // spec=!f and f is an assertion
        if(op==Operator.AND || op==Operator.OR){
            return specNeedExplain(child[0]) || specNeedExplain(child[1]);
        }
        if(op==Operator.AA) return false;

        // temporal operators and EE need to be explained
        if(op.isTemporalOp()) return true;
        if(op==Operator.EE) return true;

        //op is other operator
        return false;
    }
    */

    // Premise: spec must be a NNF formula, whose logical connectives are only !, /\ and \/, and ! only preceded assertions
    // Results: return true if spec is composition of operator op_to_explain and other operators, composed by /\ or \/
    //          return false otherwise
    static boolean needExpE(Spec spec){
        if(spec instanceof SpecBDD) return false;
        SpecExp se=(SpecExp)spec;
        Operator op=se.getOperator();
        Spec[] child=se.getChildren();

        // operator op_to_explain need to be explained
        if(op==Operator.EE) return true;

        if(op==Operator.AND || op==Operator.OR) {
            return needExpE(child[0]) || needExpE(child[1]);
        }else if(op==Operator.NOT || op==Operator.AA) return false;
        else // op is temporal operator
            return false;
    }

    // Premise: spec must be a NNF formula, whose logical connectives are only !, /\ and \/, and ! only preceded assertions
    // Results: return true if spec is composition of temporal operators and other operators, composed by /\ or \/
    //          return false otherwise
    static boolean needExpT(Spec spec){
        if(spec instanceof SpecBDD) return false;
        SpecExp se=(SpecExp)spec;
        Operator op=se.getOperator();
        Spec[] child=se.getChildren();

        // operator op_to_explain need to be explained
        if(op.isTemporalOp()) return true;

        if(op==Operator.AND || op==Operator.OR) {
            return needExpT(child[0]) || needExpT(child[1]);
        }else if(op==Operator.NOT || op==Operator.AA || op==Operator.EE) return false;
        else // op is other operator
            return false;
    }

    // Premises: n|=spec and spec is a state formula
    // Results: generate witnesses for spec from node n
    public boolean witness(
            Spec spec,
            Node n
    ) throws ModelCheckAlgException, ModelCheckException, SpecException, SMVParseException, ModuleException {
        if(n==null) return false;
        BDD state = n.getAttribute("BDD");

        if(!needExpE(spec)){
            // spec is a state formula composed by !, /\, \/ and AA, note that ! only restrict assertions
            graph.nodeAddSpec(n.getId(),spec);
        }else{ // specNeedExplainEE(spec)=true
            // spec is a state formula composed by !, /\, \/, AA, EE
            SpecExp se = (SpecExp) spec;
            Operator op = se.getOperator();
            Spec[] child = se.getChildren();

            if(op==Operator.AND){
                witness(child[0],n);
                witness(child[1],n);
            }else if(op==Operator.OR){ // spec=f\/g
                BDD f= testerInfo.getSpecBdd(child[0]); if(f==null) return false;
                BDD g= testerInfo.getSpecBdd(child[1]); if(g==null) return false;
                if (!state.and(f).isZero() && !needExpE(child[0])) witness(child[0],n);
                else if (!state.and(g).isZero() && !needExpE(child[1])) witness(child[1],n);
                else if (!state.and(f).isZero()) witness(child[0],n);
                else witness(child[1],n);
            }else if(op==Operator.EE){ // spec=Ef will be explained by clicking on the node n
                graph.nodeAddSpec(n.getId(),spec);
                //witnessE(child[0], n);
            }
            return true;
        }
        return true;
    }


    // Premises: n |= E spec
    // Results: return true if it is necessary to create a new lasso path to explain formula spec
    // 			return false if it is enough to explain spec only over node n
    // needCrtPath() is invoked by witnessE()
    boolean needCrtPath(Spec spec, Node n){
        if(!needExpE(spec) && !needExpT(spec)) return false;
        //now specNeedExplainEE(spec) || specNeedExplainTemporalOp(spec)

        BDD state=graph.nodeGetBDD(n.getId()); if(state==null) return false;

        SpecExp se=(SpecExp)spec;
        Operator op=se.getOperator();
        Spec[] child=se.getChildren();

        if(op==Operator.AND) // spec=f/\g
            return needCrtPath(child[0],n) || needCrtPath(child[1],n);
        else if(op==Operator.OR){ // spec=f\/g
            BDD f= testerInfo.getSpecBdd(child[0]); if(f==null) return false;  //SpecBDDMap.get(child[0]);
            BDD g= testerInfo.getSpecBdd(child[1]); if(g==null) return false;  //SpecBDDMap.get(child[1]);
            if(!state.and(f).isZero() && state.and(g).isZero()) // n.s|=f and n.s|=\=g
                return needCrtPath(child[0],n);
            else if(state.and(f).isZero() && !state.and(g).isZero()) // n.s|=\=f and n.s|=g
                return needCrtPath(child[1],n);
            else if(!needCrtPath(child[0],n)) // n.s|=f and n.s|=g
                return false;
            else return needCrtPath(child[1],n);
        }else if(op.isTemporalOp()) { // spec is a principally temporal formula
            if(op==Operator.UNTIL){
                BDD g= testerInfo.getSpecBdd(child[1]);
                if(!state.and(g).isZero()) // n|=g
                    return needCrtPath(child[1],n);
                else return true;
            }else if(op==Operator.RELEASES){
                BDD f= testerInfo.getSpecBdd(child[0]);
                BDD g= testerInfo.getSpecBdd(child[1]);
                if(!state.and(f.and(g)).isZero()) // n|=f/\g
                    return needCrtPath(child[0],n) || needCrtPath(child[1],n);
                else return true;
            }else if(op==Operator.B_UNTIL){
                SpecRange range = (SpecRange) child[1];
                int a = range.getFrom();
                BDD g= testerInfo.getSpecBdd(child[2]); // spec=f U 0..b g
                if(a==0 && !state.and(g).isZero()) // a=0 & n|=g
                    return needCrtPath(child[2],n);
                else return true;
            }else if(op==Operator.B_RELEASES){
                SpecRange range = (SpecRange) child[1];
                int a = range.getFrom();
                int b = range.getTo();
                BDD f= testerInfo.getSpecBdd(child[0]);
                BDD g= testerInfo.getSpecBdd(child[2]);
                if(a==0 && b==0 && !state.and(g).isZero()) // a=b=0 & n|=g
                    return needCrtPath(child[2],n);
                else if(a==0 && b>0 && !state.and(f.and(g)).isZero()) // a=0 & b>0 & n|=f/\g
                    return needCrtPath(child[0],n) || needCrtPath(child[2],n);
                else return true;
            }else // op = X
                return true;
        }else { // op==EE
            return needCrtPath(spec,n);
        }
    }

    // Premises: n |= E spec; !needCrtPath(spec,n)
    // Results: return true if it is necessary to create a new lasso path to explain formula spec
    // 			return false if it is enough to explain spec only over node n
    // explainOnNode() is invoked by witnessE()
    void witnessEonNode(Spec spec, Node n) throws SpecException {
        if(!needExpE(spec) && !needExpT(spec)) {
            graph.nodeAddSpec(n.getId(),spec);
            return;
        }
        //now specNeedExplainEE(spec) || specNeedExplainTemporalOp(spec)

        BDD state=graph.nodeGetBDD(n.getId()); if(state==null) return;
        SpecExp se=(SpecExp)spec;
        Operator op=se.getOperator();
        Spec[] child=se.getChildren();

        if(op==Operator.AND) { // spec=f/\g
            witnessEonNode(child[0], n);
            witnessEonNode(child[1], n);
        }else if(op==Operator.OR){ // spec=f\/g
            BDD f= testerInfo.getSpecBdd(child[0]); if(f==null) return;
            BDD g= testerInfo.getSpecBdd(child[1]); if(g==null) return;
            if(!state.and(f).isZero() && state.and(g).isZero()) // n.s|=f and n.s|=\=g
                witnessEonNode(child[0],n);
            else if(state.and(f).isZero() && !state.and(g).isZero()) // n.s|=\=f and n.s|=g
                witnessEonNode(child[1],n);
            else if(!needCrtPath(child[0],n)) // n.s|=f and n.s|=g
                witnessEonNode(child[0],n);
            else
                witnessEonNode(child[1],n);
        }else if(op.isTemporalOp()) { // spec is a principally temporal formula
            if(op==Operator.UNTIL){
                BDD g= testerInfo.getSpecBdd(child[1]);
                if(!state.and(g).isZero()) // n|=g
                    witnessEonNode(child[1],n);
            }else if(op==Operator.RELEASES){
                BDD f= testerInfo.getSpecBdd(child[0]);
                BDD g= testerInfo.getSpecBdd(child[1]);
                if(!state.and(f.and(g)).isZero()) { // n|=f/\g
                    witnessEonNode(child[0], n);
                    witnessEonNode(child[1], n);
                }
            }else if(op==Operator.B_UNTIL){
                SpecRange range = (SpecRange) child[1];
                int a = range.getFrom();
                BDD g= testerInfo.getSpecBdd(child[2]); // spec=f U 0..b g
                if(a==0 && !state.and(g).isZero()) // a=0 & n|=g
                    witnessEonNode(child[2],n);
            }else if(op==Operator.B_RELEASES){
                SpecRange range = (SpecRange) child[1];
                int a = range.getFrom();
                int b = range.getTo();
                BDD f= testerInfo.getSpecBdd(child[0]);
                BDD g= testerInfo.getSpecBdd(child[2]);
                if(a==0 && b==0 && !state.and(g).isZero()) witnessEonNode(child[2],n); // a=b=0 & n|=g
                else if(a==0 && b>0 && !state.and(f.and(g)).isZero()) { // a=0 & b>0 & n|=f/\g
                    witnessEonNode(child[0],n);
                    witnessEonNode(child[2],n);
                }
            }
        }else { // op==EE
            witnessEonNode(child[0],n);
        }
    }

    // Premises: !specNeedExplainEE(spec) && specNeedExplainTemporalOp(spec); n|=E spec; !needCreatePath(spec,n)
    // Results: explain spec only over node n
    boolean witnessEonNode_old(Spec spec, Node n) throws ModelCheckException, SpecException, ModelCheckAlgException, SMVParseException, ModuleException {
        if(spec.isStateSpec())
            //return witness(spec,n);
            return graph.nodeAddSpec(n.getId(),spec);
        //now spec is NOT a state formula

        BDD state=graph.nodeGetBDD(n.getId());

        SpecExp se=(SpecExp)spec;
        Operator op=se.getOperator();
        Spec[] child=se.getChildren();

        if(op==Operator.AND)
            return witnessEonNode_old(child[0],n) || witnessEonNode_old(child[1],n);
        else if(op==Operator.OR){
            BDD lc= testerInfo.getSpecBdd(child[0]); if(lc==null) return false;  //SpecBDDMap.get(child[0]);
            BDD rc= testerInfo.getSpecBdd(child[1]); if(rc==null) return false;  //SpecBDDMap.get(child[1]);
            if(child[0].isPropSpec() && !state.and(lc).isZero()) // f is prop formula && n|=f
                //return witness(child[0],n);
                graph.nodeAddSpec(n.getId(),child[0]);
            else if(child[1].isPropSpec() && !state.and(rc).isZero()) // g is prop formula && n|=g
                //return witness(child[1],n);
                graph.nodeAddSpec(n.getId(),child[1]);
            if(!state.and(lc).isZero()) //n|=f
                return witnessEonNode_old(child[0],n);
            else // n|=g
                return witnessEonNode_old(child[1],n);
        }else if(op.isTemporalOp()) { // spec is a principally temporal formula
            if(op==Operator.UNTIL){
                BDD g= testerInfo.getSpecBdd(child[1]);
                if(!state.and(g).isZero()) return witness(child[1],n); else return false;// n|=g
            }else if(op==Operator.RELEASES){
                BDD f= testerInfo.getSpecBdd(child[0]);
                BDD g= testerInfo.getSpecBdd(child[1]);
                if(!state.and(f.and(g)).isZero()) return witness(child[0],n) && witness(child[1],n); else return false; // n|=f/\g
            }else if(op==Operator.B_UNTIL){
                SpecRange range = (SpecRange) child[1];
                int a = range.getFrom();
                BDD g= testerInfo.getSpecBdd(child[2]); // spec=f U 0..b g
                if(a==0 && !state.and(g).isZero()) return witness(child[2],n); else return false; // a=0 & n|=g
            }else if(op==Operator.B_RELEASES){
                SpecRange range = (SpecRange) child[1];
                int a = range.getFrom();
                int b = range.getTo();
                BDD f= testerInfo.getSpecBdd(child[0]);
                BDD g= testerInfo.getSpecBdd(child[2]);
                if(a==0 && b==0 && !state.and(g).isZero()) return witness(child[2],n); // a=b=0 & n|=g
                else if(a==0 && b>0 && !state.and(f.and(g)).isZero()) return witness(child[0],n) && witness(child[2],n); // a=0 & b>0 & n|=f/\g
                else return false;
            }else return false;
        }else // op==!, EE or AA
            return true;
    }


/*
    // Notation: the advantage of this version is that: for a formula with E f and more than one principally temporal subformulas to be witnessed,
    //          only one node path is constructed for all principally temporal subformulas.
    // Premises:
    //      (1) spec: spec is a NNF formula. Initially spec is a state formula such that n.s|=spec, and path p is null.
    //      (2) n: the node from which spec is witnessed;
    //      (3) p: the path starting from node n, along which spec is explained.
    // Results: (eE denotes explaining path quantifier E, eT denotes explaining temporal operators)
    //      (1) !eE/\!eT: if spec is a state formula without Ef (may with Af) to be witnessed, then add spec to the annotation set of node n;
    //      (2) !eE/\eT: if spec is a non-state formula without Ef to be witnessed, then spec is recursively explained along path p;
    //      (3) eE:
    //      (3.1) if spec=f/\g, then the task for witnessing spec is recursively decomposed according to /\;
    //      (3.2) if spec=f\/g, then the task for witnessing spec is recursively decomposed according to \/;
    //      (3.3) if spec=Ef, a new path p' from node n will be constructed, and then explain f along path p';
    public boolean witness( Spec spec,
                            Node n,
                            NodePath p
    ) throws ModelCheckException, SpecException, ModelCheckAlgException, SMVParseException, ModuleException {
        if(n==null) return false;
        BDD feasibleStates=null;
        BDD fromState = n.getAttribute("BDD");
        int pathNo = n.getAttribute("pathNo");
        int stateNo = n.getAttribute("stateNo");
        String fromNodeId = pathNo+"."+stateNo;
        if (fromState == null || fromState.isZero()) return false;

        SpecExp se = (SpecExp) spec;
        Operator op = se.getOperator();
        Spec[] child = se.getChildren();

        if(!specNeedExplainEE(spec) && !specNeedExplainTemporalOp(spec)) { // (1) !eE/\!eT
            // add spec to the annotation set of node n
            return graph.nodeAddSpec(n.getId(),spec);
        }else if(!specNeedExplainEE(spec) && specNeedExplainTemporalOp(spec)){ // (2) !eE/\eT
            // explain spec along p
            if(p==null) return false;
            return explainPath(spec, p, 0);
        }else if(specNeedExplainEE(spec)){ // (3) eE
            if(op==Operator.AND){ // (3.1) spec=f/\g
                boolean b1=witness(child[0], n, p);
                boolean b2=witness(child[1], n, p);
                return b1&&b2;
            }else if(op==Operator.OR){ // (3.2) spec=f\/g
                BDD lc=tester.cacheGetSpecBdd(child[0]); if(lc==null) return false;
                if (!fromState.and(lc).isZero()) witness(child[0],n,p);
                else witness(child[1],n,p);
            }else if(op==Operator.EE){ // (3.3) spec=Ef
                return graph.nodeAddSpec(n.getId(),spec);
            }
        }
        return true;
    }
*/

    // Premise: all states in destStates are feasible states from aState
    // Results: returns a state in destStates that is closest to aState
    public BDD closest(SMVModule D, BDD destStates, BDD aState){
        BDD succs=aState, r=null;
        while(true){
            r = succs.and(destStates);
            if(!r.isZero())
                return r.satOne(D.moduleUnprimeVars(), false);
            else
                succs=D.succ(succs);
        }
    }

    public boolean witnessE(Spec spec,
                             Node n
    ) throws SpecException, ModelCheckException, ModelCheckAlgException, SMVParseException, ModuleException {
        if (n == null) return false;

        BDD state = n.getAttribute("BDD");
        int pathNo = n.getAttribute("pathNo");
        int stateNo = n.getAttribute("stateNo");
        String fromNodeId = pathNo + "." + stateNo;
        if (state == null || state.isZero()) return false;

        SpecExp se = (SpecExp) spec;
        Operator op = se.getOperator();
        Spec[] child = se.getChildren();

        if (!needExpE(spec) && !needExpT(spec))
            graph.nodeAddSpec(n.getId(),spec);
        else if (needExpE(spec)) {
            if (op==Operator.AND) {
                witnessE(child[0],n);
                witnessE(child[1],n);
            }else if (op==Operator.OR) { // spec = f \/ g
                BDD f= testerInfo.getSpecBdd(child[0]); if(f==null) return false;
                BDD g= testerInfo.getSpecBdd(child[1]); if(g==null) return false;
                if (!state.and(f).isZero() && !needExpE(child[0]) && !needExpT(child[0])) witnessE(child[0],n);
                else if (!state.and(g).isZero() && !needExpE(child[1]) && !needExpE(child[1])) witnessE(child[1],n);
                else if (!state.and(f).isZero()) witnessE(child[0],n);
                else witnessE(child[1],n);
            }else{ // spec=Ef
                graph.nodeAddSpec(n.getId(),spec);
            }
        }else{ // !needExpE(spec) && needExpT(spec)
            if(needCrtPath(spec,n)) lassoPath(spec,n);
            else witnessEonNode(spec,n);
        }
        return true;
    }

    // premise: n.s |= E spec
    // construct a lasso path r s.t. r|=spec;
    // r's prefix is shortest and r's loop is within a SCC with minimal number of states
    public boolean lassoPath(Spec spec,
                            Node n
    ) throws SpecException, ModelCheckException, ModelCheckAlgException, SMVParseException, ModuleException {
        if(n==null) return false;

        BDD fromState = n.getAttribute("BDD");  // fromState is the state of the starting node
        int pathNo = n.getAttribute("pathNo");
        int stateNo = n.getAttribute("stateNo");
        String fromNodeId = pathNo+"."+stateNo;
        if (fromState == null || fromState.isZero()) return false;

        BDDVarSet auxBDDVarSet = Env.globalUnprimeVarsMinus(stateVarSet);
        BDD D_fromState = fromState.exist(auxBDDVarSet); // D_fromState is the D-state of the starting node

        SpecExp se = (SpecExp) spec;
        Operator op = se.getOperator();

        //-------------------------------------------------------------------------------------
        // create a feasible lasso path pi from n such that (D||T_spec, pi)|=spec; and then explainPath(spec, pi, 0);
        //-------------------------------------------------------------------------------------
        // (1) construct design = D||T_spec
        CacheSpecTesterInfo specInfo = null;
        specInfo = testerInfo.getSpecInfo(spec.toString());
        if(specInfo==null) {
            consoleOutput(0,"error","lassoPath(spec,n): the information of spec is unavaliable.\n");
            return false;
        }

        SMVModule DT = (SMVModule) getDesign(); // DT is the original model D
        ModuleWithWeakFairness weakDT=null;
        if (DT instanceof ModuleWithWeakFairness) weakDT = (ModuleWithWeakFairness) DT;

        BDDVarSet specAuxBDDVarSet = Env.getEmptySet();
        if(specInfo.testerSet.size()>0){
            // the tester of spec is NOT empty
            for (SMVModule tester : specInfo.testerSet) DT.syncComposition(tester);
            LinkedHashSet<ModuleBDDField> specAuxVars = specInfo.getAuxVars();
            specAuxBDDVarSet = getBDDVarSet(specAuxVars);
        }
        BDDVarSet specAllBDDVarSet = stateVarSet.union(specAuxBDDVarSet);

        // DT is the parallel composition of design and the tester of spec, if the tester of spec is not empty
        CacheSpecTesterInfo EspecInfo = null;
        EspecInfo = testerInfo.getSpecInfo(new SpecExp(Operator.EE,spec).toString());
        if(EspecInfo==null){
            consoleOutput(0,"error","lassoPath(spec,n): the feasible states of E spec is unavailable.\n");
            return false;
        }
        BDD DT_feas = EspecInfo.feasibleStates;

        BDD DT_fromStates = D_fromState.and(DT_feas.and(specInfo.getBdd())); // DT_fromState is the set of D||T_spec states implied by D_fromState
        //BDD DT_fromState = DT_fromStates.satOne(DT.moduleUnprimeVars(),false);

        // saving to the previous restriction state
        Vector<BDD> oldTransRestrictions = DT.getAllTransRestrictions();

        DT.restrictTrans(DT_feas.and(Env.prime(DT_feas)));
        int oldTransRestrictionsSize = DT.getTransRestrictionsSize();

        //----------------------------------------------------------------------------------
        // Step S1
        //----------------------------------------------------------------------------------
        // (1) Z' := n.s
        BDD Zp=DT_fromStates;
        // (2) Z := n.s ◦ R*
        BDD Z=DT.allSucc(DT_fromStates);  // now Z is the set of reachable states from n.s
        // (3) T := R ∧ (Z × Z);
        DT.restrictTrans(Z.id().and(Env.prime(Z)));
        boolean c=!Z.equals(Zp);
        while(c){
            Zp=Z;
            c=false;
            BDD Y;
            while(true){
                Y=Z.and(DT.succ(Z));
                if(Y.equals(Z)) break; else Z=Y;
            }
            if(!Z.equals(Zp)){ DT.restrictTrans(Z.id().and(Env.prime(Z))); Zp=Z; c=true; }

            if (weakDT!=null) {
                for (int i = 0; i < weakDT.justiceNum(); i++) {
                    Z=DT.allSucc(Z.id().and(weakDT.justiceAt(i)));
                    if(!Z.equals(Zp)){ DT.restrictTrans(Z.id().and(Env.prime(Z))); Zp=Z; c=true; }
                }
            }
        }
        //----------------------------------------------------------------------------------
        // now Z is the set of D||T_spec states containing all fair SCCs which are reachable from and closest to DT_fromStates
        // Step S2
        //----------------------------------------------------------------------------------

        // restore old trans restrictions
        Vector<BDD> addedTransRestrictions=new Vector<BDD>();
        while(DT.getTransRestrictionsSize()>oldTransRestrictionsSize) {
            addedTransRestrictions.add(DT.getTransRestriction(oldTransRestrictionsSize));
            DT.removeTransRestriction(oldTransRestrictionsSize);
        }
        // (16)
        Zp=DT_fromStates;
        BDD reach=DT_fromStates, glue; // "glue" is the set of states that glues the prefix and the loop together
        glue=Zp.and(Z);
        while (glue.isZero()) { // (17)
            Zp=DT.succ(Zp.id()).and(reach.not()); // (18) Y is the set of new successors that have never visited before
            reach=reach.id().or(Zp); // (19) "reach" is the set of states visited before
            glue=Zp.and(Z);
        }
        // now "glue" is the set of states that glues the prefix and the loop together

        // restore new trans restrictions
        for(int i=0; i<addedTransRestrictions.size(); i++) { DT.restrictTrans(addedTransRestrictions.get(i).id()); }

        // the following two lines randomly choose one state from "glue"
/*        BDD t=glue.satOne(DT.moduleUnprimeVars(), false);
        BDD scc=DT.allPred(t);
*/
        /*
        BDD scc, t;
        t = glue.satOne(DT.moduleUnprimeVars(), false);
        scc = DT.allPred(t);
         */

        // (20)
        BDD scc=Env.FALSE(), scc2, t=Env.FALSE(), t2;
        while (!glue.isZero()) { // (21)
            // (22)
            t2=glue.satOne(specAllBDDVarSet, false);
            glue=glue.id().and(t2.not()); // glue = glue - {t2}
            // (23)
            scc2=DT.allPred(t2); // DT.allSucc(t2).and(DT.allPred(t2));
            // (24)
            if(scc.isZero() || scc2.satCount(specAllBDDVarSet)<scc.satCount(specAllBDDVarSet)) {
                scc=scc2; t=t2;
            }
        }

        //----------------------------------------------------------------------------------
        // (1) scc is a fair SCC within Z that contains minimal number of states, from all fair SCCs within Z which are reachable from and closest to DT_fromStates
        // (2) t is a state in scc that is closest to DT_fromStates
        // Step S3
        //----------------------------------------------------------------------------------

        // (25-26)
        // restore old trans restrictions
        while(DT.getTransRestrictionsSize()>oldTransRestrictionsSize) { DT.removeTransRestriction(oldTransRestrictionsSize); }
        Vector<BDD> prefix = new Vector<BDD>();
        BDD[] path = DT.shortestPath(DT_fromStates, t);
        for (int i = 0; i < path.length-1; i++) // abandon the last state of "path" because it is the first state of the glued loop
            prefix.add(path[i]);

        //----------------------------------------------------------------------------------
        // "prefix" is the shortest path from DT_fromStates to t
        // Step S4: construct a fair cycle path "period" within scc from t
        //----------------------------------------------------------------------------------
        // (27)
        DT.restrictTrans(scc.id().and(Env.prime(scc)));

        // (28)
        Vector<BDD> period = new Vector<BDD>();
        period.add(t);

        // LXY: cache the labels of justices and strong fairnesses
        Vector<Integer> vector_period_idx = new Vector<Integer>();
        Vector<String> vector_fairness = new Vector<String>();  // for justice and strong fairness

        BDD fulfill;
        if (weakDT!=null) {
            for (int i = 0; i < weakDT.justiceNum(); i++) {
                // Line 12, check if j[i] already satisfied
                fulfill = Env.FALSE();
                for (int j = 0; j < period.size(); j++) {
                    fulfill = period.elementAt(j).and(weakDT.justiceAt(i))
                            .satOne(specAllBDDVarSet, false);
                    if (!fulfill.isZero())
                        break;
                }
                if (fulfill.isZero()) {
                    BDD from = period.lastElement();
                    BDD to = scc.and(weakDT.justiceAt(i));
                    path = weakDT.shortestPath(from, to);
                    // eliminate the edge since from is already in period
                    for (int j = 1; j < path.length; j++)
                        period.add(path[j]);

                    //LXY
                    //vector_period_idx.add((Integer) period.size()-1);
                    //vector_fairness.add("Justice"+(i+1));
                }
            }
        }
        // Lines 14-16
        if (DT instanceof ModuleWithStrongFairness) {
            ModuleWithStrongFairness strongDT = (ModuleWithStrongFairness) DT;
            for (int i = 0; i < strongDT.compassionNum(); i++) {
                if (!scc.and(strongDT.pCompassionAt(i)).isZero()) {
                    // check if C requirement i is already satisfied
                    fulfill = Env.FALSE();
                    for (int j = 0; j < period.size(); j++) {
                        fulfill = period.elementAt(j).and(
                                strongDT.qCompassionAt(i)).satOne(
                                specAllBDDVarSet, false);
                        // fulfill =
                        // period.elementAt(j).and(design.qCompassionAt(i)).satOne();
                        if (!fulfill.isZero()) {
                            //LXY
                            //vector_period_idx.add((Integer)j);
                            //vector_fairness.add("Compassion.q"+(i+1));
                            break;
                        }
                    }

                    if (fulfill.isZero()) {
                        BDD from = period.lastElement();
                        BDD to = scc.and(strongDT.qCompassionAt(i));
                        path = strongDT.shortestPath(from, to);
                        // eliminate the edge since from is already in period
                        for (int j = 1; j < path.length; j++)
                            period.add(path[j]);

                        //LXY
                        //vector_period_idx.add((Integer)period.size()-1);
                        //vector_fairness.add("Compassion.q"+(i+1));
                    }
                }
            }
        }
        //
        // Close cycle
        //
        // A period of length 1 may be fair, but it might be the case that
        // period[1] is not a successor of itself. The routine path
        // will add nothing. To solve this
        // case we add another state to _period, now it will be OK since
        // period[1] and period[n] will not be equal.

        // Line 17, but modified
        if (!period.firstElement().and(period.lastElement()).isZero()) {
            // The first and last states are already equal, so we do not
            // need to extend them to complete a cycle, unless period is
            // a degenerate case of length = 1, which is not a successor of
            // self.
            if (period.size() == 1) {
                // Check if _period[1] is a successor of itself.
                if (period.firstElement().and(
                        DT.succ(period.firstElement())).isZero()) {
                    // period[1] is not a successor of itself: Add state to
                    // period.
                    period.add(DT.succ(period.firstElement()).satOne(
                            specAllBDDVarSet, false));    // DT.moduleUnprimeVars()
                    // period.add(design.succ(period.firstElement()).satOne());

                    // Close cycle.
                    BDD from = period.lastElement();
                    BDD to = period.firstElement();
                    path = DT.shortestPath(from, to);
                    // eliminate the edges since from and to are already in
                    // period
                    for (int i = 1; i < path.length - 1; i++) {
                        period.add(path[i]);
                    }
                }
            }
        } else {
            BDD from = period.lastElement();
            BDD to = period.firstElement();
            path = DT.shortestPath(from, to);
            // eliminate the edges since from and to are already in period
            for (int i = 1; i < path.length - 1; i++) {
                period.add(path[i]);
            }
        }

        // LXY: Now prefix and period are prepared, and there is a transition from the last element of period
        // to the first element (its index is loopNodeIdx) of period

        int loopNodeIdx=prefix.size();  // the index of the first node of period

        prefix.addAll(period);
        // now prefix is the composition of prefix and period, and the first element of prefix is state n.s

        String first_created_edgeId=null;
        createdPathNumber++; // create a new path no matter the size of path
        Edge e;

        String pred_nid, cur_nid;
        pred_nid = fromNodeId;
        //graph.addNodeSatSpec(fromNodeId, spec, true);

        Vector<String> trunkNodePath=new Vector<String>();   // the trunk node path will be created
        trunkNodePath.add(fromNodeId);

        for (int i = 1; i < prefix.size(); i++) {
            graph.addNode(createdPathNumber, i, prefix.get(i));
            cur_nid = createdPathNumber + "." + i;

            trunkNodePath.add(cur_nid);

            String edgeId=pred_nid + "->" + cur_nid;
            e = graph.addArc(edgeId, pred_nid, cur_nid, true);
            if(first_created_edgeId==null) {
                first_created_edgeId=edgeId;
//                        graph.edgeAddAnnotation(edgeId,
//                                "Path" + createdPathNumber + "|=" + simplifySpecString(spec.toString(), false));
            }
            pred_nid = cur_nid;
        }

        //closing period
        String to_nodeId=null;
        if(loopNodeIdx==0) // the size of original prefix is 0
            to_nodeId=fromNodeId;
        else
            to_nodeId=createdPathNumber+"."+loopNodeIdx;
        e = graph.addArc(pred_nid+"->"+to_nodeId, pred_nid, to_nodeId, true);

        // append the fairness annotations to the nodes of this path
        for(int i=0; i<vector_period_idx.size(); i++){
            int idx=(int)vector_period_idx.get(i);
            String ann=vector_fairness.get(i);
            if(idx==0){
                graph.nodeAddAnnotation(fromNodeId, ann);
            }else{//idx>0
                graph.nodeAddAnnotation(createdPathNumber+"."+idx, ann);
            }
        }

        // restore old trans restrictions
        while(DT.getTransRestrictionsSize()>oldTransRestrictionsSize) { DT.removeTransRestriction(oldTransRestrictionsSize); }

        NodePath nodePath = new NodePath(trunkNodePaths.size(), trunkNodePath, loopNodeIdx, path[0]);
        trunkNodePaths.add(nodePath);

        if(!op.isTemporalOp()){
            // if spec is NOT a principally temporal subformula, it need to be show before calling explainPath(spec...)
            graph.edgeAddSpec(first_created_edgeId, spec, nodePath, 0, true);
        }

        //-------------------------------------------------------------------------------------
        boolean b= explainPath(spec, nodePath, 0);
        //-------------------------------------------------------------------------------------

        DT.setAllTransRestrictions(oldTransRestrictions);;
        if(specInfo.testerSet.size()>0){
            // the tester of spec is NOT empty
            for (SMVModule tester : specInfo.testerSet) DT.decompose(tester);
        }
        return b;
    }

    // premise: n.s |= E spec
    // generate the witness for n.s |= E spec
    // this algorithm extended from the WITNESS algorithm in the paper "MODEL CHECKING WITH STRONG FAIRNESS"
    public boolean witnessE_old(Spec spec,
                            Node n
    ) throws SpecException, ModelCheckException, ModelCheckAlgException, SMVParseException, ModuleException {
        if(n==null) return false;

        if(!needCrtPath(spec,n)) {
            witnessEonNode(spec, n); return true;}
        // now needCrtPath(spec,n)

        BDD feasibleStates=null;
        BDD fromState = n.getAttribute("BDD");
        int pathNo = n.getAttribute("pathNo");
        int stateNo = n.getAttribute("stateNo");
        String fromNodeId = pathNo+"."+stateNo;
        if (fromState == null || fromState.isZero()) return false;

        SpecExp se = (SpecExp) spec;
        Operator op = se.getOperator();
        Spec[] child = se.getChildren();

        //-------------------------------------------------------------------------------------
        // create a feasible lasso path pi from n such that pi|=spec; and then explainPath(spec, pi, 0);
        // the tester of spec is not empty, and spec has NOT EE and AA operators
        //-------------------------------------------------------------------------------------
        SMVModule DT = (SMVModule) getDesign(); // DT is the parallel composition of design and the tester of spec
        BDD temp, fulfill;

        if(feasibleStatesForWitnessE==null) feasibleStatesForWitnessE = DT.feasible();
        // feasibleStatesForWitnessE is the set of feasible states of the composition of the original model and the tester

        // saving to the previous restriction state
        Vector<BDD> old_trans_restrictions = DT.getAllTransRestrictions();
        // Lines 1-2 are handled by the caller. ("verify")
        // Line 3
        DT.restrictTrans(feasibleStatesForWitnessE.and(Env.prime(feasibleStatesForWitnessE)));
        BDD s = fromState;
        // Lines 5-6
        while (true) {
            temp = DT.allSucc(s).and(DT.allPred(s).not());
            if (!temp.isZero())
                s = temp.satOne(DT.moduleUnprimeVars(), false);
            else break;
        }
        // Line 7: Compute MSCS containing s.
        BDD feas = DT.allSucc(s);
        DT.removeAllTransRestrictions();
        Vector<BDD> prefix = new Vector<BDD>();
        BDD[] path = DT.shortestPath(fromState, feas);
        for (int i = 0; i < path.length; i++)
            prefix.add(path[i]);

        // //// Calculate "_period".
        // Line 8: This has to come after line 9, because the way TS.tlv
        // implements restriction.
        DT.restrictTrans(feas.and(Env.prime(feas)));

        // Line 10
        Vector<BDD> period = new Vector<BDD>();
        period.add(prefix.lastElement());

        // Since the last item of the prefix is the first item of
        // the period we don't need to print the last item of the prefix.
        temp = prefix.remove(prefix.size() - 1);

        // LXY: cache the labels of justices and strong fairnesses
        Vector<Integer> vector_period_idx = new Vector<Integer>();
        Vector<String> vector_fairness = new Vector<String>();  // for justice and strong fairness

        // Lines 11-13
        if (DT instanceof ModuleWithWeakFairness) {
            ModuleWithWeakFairness weakDes = (ModuleWithWeakFairness) DT;
            for (int i = 0; i < weakDes.justiceNum(); i++) {
                // Line 12, check if j[i] already satisfied
                fulfill = Env.FALSE();
                for (int j = 0; j < period.size(); j++) {
                    fulfill = period.elementAt(j).and(weakDes.justiceAt(i))
                            .satOne(weakDes.moduleUnprimeVars(), false);
                    // fulfill =
                    // period.elementAt(j).and(design.justiceAt(i)).satOne();
                    if (!fulfill.isZero())
                        break;
                }
                // Line 13
                if (fulfill.isZero()) {
                    BDD from = period.lastElement();
                    BDD to = feas.and(weakDes.justiceAt(i));
                    path = weakDes.shortestPath(from, to);
                    // eliminate the edge since from is already in period
                    for (int j = 1; j < path.length; j++)
                        period.add(path[j]);

                    //LXY
                    //vector_period_idx.add((Integer) period.size()-1);
                    //vector_fairness.add("Justice"+(i+1));
                }
            }
        }
        // Lines 14-16
        if (DT instanceof ModuleWithStrongFairness) {
            ModuleWithStrongFairness strongDes = (ModuleWithStrongFairness) DT;
            for (int i = 0; i < strongDes.compassionNum(); i++) {
                if (!feas.and(strongDes.pCompassionAt(i)).isZero()) {
                    // check if C requirement i is already satisfied
                    fulfill = Env.FALSE();
                    for (int j = 0; j < period.size(); j++) {
                        fulfill = period.elementAt(j).and(
                                strongDes.qCompassionAt(i)).satOne(
                                strongDes.moduleUnprimeVars(), false);
                        // fulfill =
                        // period.elementAt(j).and(design.qCompassionAt(i)).satOne();
                        if (!fulfill.isZero()) {
                            //LXY
                            //vector_period_idx.add((Integer)j);
                            //vector_fairness.add("Compassion.q"+(i+1));
                            break;
                        }
                    }

                    if (fulfill.isZero()) {
                        BDD from = period.lastElement();
                        BDD to = feas.and(strongDes.qCompassionAt(i));
                        path = strongDes.shortestPath(from, to);
                        // eliminate the edge since from is already in period
                        for (int j = 1; j < path.length; j++)
                            period.add(path[j]);

                        //LXY
                        //vector_period_idx.add((Integer)period.size()-1);
                        //vector_fairness.add("Compassion.q"+(i+1));
                    }
                }
            }
        }
        //
        // Close cycle
        //
        // A period of length 1 may be fair, but it might be the case that
        // period[1] is not a successor of itself. The routine path
        // will add nothing. To solve this
        // case we add another state to _period, now it will be OK since
        // period[1] and period[n] will not be equal.

        // Line 17, but modified
        if (!period.firstElement().and(period.lastElement()).isZero()) {
            // The first and last states are already equal, so we do not
            // need to extend them to complete a cycle, unless period is
            // a degenerate case of length = 1, which is not a successor of
            // self.
            if (period.size() == 1) {
                // Check if _period[1] is a successor of itself.
                if (period.firstElement().and(
                        DT.succ(period.firstElement())).isZero()) {
                    // period[1] is not a successor of itself: Add state to
                    // period.
                    period.add(DT.succ(period.firstElement()).satOne(
                            DT.moduleUnprimeVars(), false));
                    // period.add(design.succ(period.firstElement()).satOne());

                    // Close cycle.
                    BDD from = period.lastElement();
                    BDD to = period.firstElement();
                    path = DT.shortestPath(from, to);
                    // eliminate the edges since from and to are already in
                    // period
                    for (int i = 1; i < path.length - 1; i++) {
                        period.add(path[i]);
                    }
                }
            }
        } else {
            BDD from = period.lastElement();
            BDD to = period.firstElement();
            path = DT.shortestPath(from, to);
            // eliminate the edges since from and to are already in period
            for (int i = 1; i < path.length - 1; i++) {
                period.add(path[i]);
            }
        }

        int loopNodeIdx=prefix.size();  // the index of the first node of period

        // LXY: Now prefix and period are prepared, and there is a transition from the last element of period
        // to the first element (its index is loopNodeIdx) of period

        prefix.addAll(period);
        // now prefix is the composition of prefix and period, and the first element of prefix is state n.s

        String first_created_edgeId=null;
        createdPathNumber++; // create a new path no matter the size of path
        Edge e;

        String pred_nid, cur_nid;
        pred_nid = fromNodeId;
        //graph.addNodeSatSpec(fromNodeId, spec, true);

        Vector<String> trunkNodePath=new Vector<String>();   // the trunk node path will be created
        trunkNodePath.add(fromNodeId);

        for (int i = 1; i < prefix.size(); i++) {
            graph.addNode(createdPathNumber, i, prefix.get(i));
            cur_nid = createdPathNumber + "." + i;

            trunkNodePath.add(cur_nid);

            String edgeId=pred_nid + "->" + cur_nid;
            e = graph.addArc(edgeId, pred_nid, cur_nid, true);
            if(first_created_edgeId==null) {
                first_created_edgeId=edgeId;
//                        graph.edgeAddAnnotation(edgeId,
//                                "Path" + createdPathNumber + "|=" + simplifySpecString(spec.toString(), false));
            }
            pred_nid = cur_nid;
        }

        //closing period
        String to_nodeId=null;
        if(loopNodeIdx==0) // the size of original prefix is 0
            to_nodeId=fromNodeId;
        else
            to_nodeId=createdPathNumber+"."+loopNodeIdx;
        e = graph.addArc(pred_nid+"->"+to_nodeId, pred_nid, to_nodeId, true);

        // append the fairness annotations to the nodes of this path
        for(int i=0; i<vector_period_idx.size(); i++){
            int idx=(int)vector_period_idx.get(i);
            String ann=vector_fairness.get(i);
            if(idx==0){
                graph.nodeAddAnnotation(fromNodeId, ann);
            }else{//idx>0
                graph.nodeAddAnnotation(createdPathNumber+"."+idx, ann);
            }
        }

        DT.setAllTransRestrictions(old_trans_restrictions);

        NodePath nodePath = new NodePath(trunkNodePaths.size(), trunkNodePath, loopNodeIdx, null);  // TODO: firstState is temporally setted to be null
        trunkNodePaths.add(nodePath);

        if(!op.isTemporalOp()){
            // if spec is NOT a principally temporal subformula, it need to be show before calling explainPath(spec...)
            graph.edgeAddSpec(first_created_edgeId, spec, nodePath, 0, true);
        }

        //-------------------------------------------------------------------------------------
        return explainPath(spec, nodePath, 0);
        //-------------------------------------------------------------------------------------
    }

    /*
    // Notation: the shortage of this old version is that: for a formula with E f and more than one principally temporal subformulas to be witnessed,
    //          more then one node paths will be contructed for each principally temporal subformulas. This is irrational.
    // Premises: n |= E spec; spec is a NNF formula
    // Results: if spec has to be explained over a path, then generate a new feasible lasso path pi from n, explain spec over pi;
    //          otherwise, explain spec only on node n
    public boolean witnessE(Spec spec,
                            Node n) throws ModelCheckException, SpecException, ModelCheckAlgException, SMVParseException, ModuleException {
        if(n==null) return false;
        BDD feasibleStates=null;
        BDD fromState = n.getAttribute("BDD");
        int pathNo = n.getAttribute("pathNo");
        int stateNo = n.getAttribute("stateNo");
        String fromNodeId = pathNo+"."+stateNo;
        if (fromState == null || fromState.isZero()) return false;

        if(!specNeedExplainEE(spec) && !specNeedExplainTemporalOp(spec)){ // !EE /\ !TT
            // spec is a state formula composed by !,/\,\/,AA
            //witness(spec, n);
            return graph.nodeAddSpec(n.getId(),spec);
        }else if(specNeedExplainEE(spec)){ // (EE /\ !TT) \/ (EE /\ TT) = EE /\ (!TT \/ TT) = EE
            // spec is a formula composed by !,/\,\/,AA,EE,temporal operators
            SpecExp se = (SpecExp) spec;
            Operator op = se.getOperator();
            Spec[] child = se.getChildren();

            if(op==Operator.AND){
                boolean b1=witnessE(child[0],n);
                boolean b2=witnessE(child[1],n);
                return b1&&b2;
            }else if(op==Operator.OR){
                Spec p,q;
                if(child[0].isPropSpec()) {p=child[0]; q=child[1];} else {p=child[1]; q=child[0];}
                BDD pBdd = tester.cacheGetSpecBdd(p); if(pBdd==null) return false; // SpecBDDMap.get(p);
                if (!fromState.and(pBdd).isZero())
                    return witnessE(p,n);
                else return witnessE(q,n);
            }else if(op==Operator.EE){
                //return witnessE(child[0],n);
                return graph.nodeAddSpec(n.getId(),spec);
            }else return false;
        }else{ // !EE /\ TT
            // now !specNeedExplainEE(spec) && specNeedExplainTemporalOp(spec)
            // spec is composed by !, \/, /\, AA, temporal operators
            if(!needCreatePath(spec,n)){
                return explainOnNode(spec,n);
            }else{
                // create a feasible lasso path pi from n such that pi|=spec; and then explainPath(spec, pi, 0);
                // the tester of spec is not empty, and spec has NOT EE and AA operators
                SMVModule DT = (SMVModule) getDesign(); // DT is the parallel composition of design and the tester of spec
                BDD temp, fulfill;

                if(feasibleStatesForWitnessE==null) feasibleStatesForWitnessE = DT.feasible();
                // feasibleStatesForWitnessE is the set of feasible states of the composition of the original model and the tester

                // saving to the previous restriction state
                Vector<BDD> old_trans_restrictions = DT.getAllTransRestrictions();
                //Lines 1-2 are handled by the caller. ("verify")
                // Line 3
                DT.restrictTrans(feasibleStatesForWitnessE.and(Env.prime(feasibleStatesForWitnessE)));
                BDD s = fromState;
                // Lines 5-6
                while (true) {
                    temp = DT.allSucc(s).and(DT.allPred(s).not());
                    if (!temp.isZero())
                        s = temp.satOne(DT.moduleUnprimeVars(), false);
                    else break;
                }
                // Line 7: Compute MSCS containing s.
                BDD feas = DT.allSucc(s);
                DT.removeAllTransRestrictions();
                Vector<BDD> prefix = new Vector<BDD>();
                BDD[] path = DT.shortestPath(fromState, feas);
                for (int i = 0; i < path.length; i++)
                    prefix.add(path[i]);

                // //// Calculate "_period".
                // Line 8: This has to come after line 9, because the way TS.tlv
                // implements restriction.
                DT.restrictTrans(feas.and(Env.prime(feas)));

                // Line 10
                Vector<BDD> period = new Vector<BDD>();
                period.add(prefix.lastElement());

                // Since the last item of the prefix is the first item of
                // the period we don't need to print the last item of the prefix.
                temp = prefix.remove(prefix.size() - 1);

                // LXY: cache the labels of justices and strong fairnesses
                Vector<Integer> vector_period_idx= new Vector<Integer>();
                Vector<String> vector_fairness=new Vector<String>();  // for justice and strong fairness

                // Lines 11-13
                if (DT instanceof ModuleWithWeakFairness) {
                    ModuleWithWeakFairness weakDes = (ModuleWithWeakFairness) DT;
                    for (int i = 0; i < weakDes.justiceNum(); i++) {
                        // Line 12, check if j[i] already satisfied
                        fulfill = Env.FALSE();
                        for (int j = 0; j < period.size(); j++) {
                            fulfill = period.elementAt(j).and(weakDes.justiceAt(i))
                                    .satOne(weakDes.moduleUnprimeVars(), false);
                            // fulfill =
                            // period.elementAt(j).and(design.justiceAt(i)).satOne();
                            if (!fulfill.isZero())
                                break;
                        }
                        // Line 13
                        if (fulfill.isZero()) {
                            BDD from = period.lastElement();
                            BDD to = feas.and(weakDes.justiceAt(i));
                            path = weakDes.shortestPath(from, to);
                            // eliminate the edge since from is already in period
                            for (int j = 1; j < path.length; j++)
                                period.add(path[j]);

                            //LXY
                            //vector_period_idx.add((Integer) period.size()-1);
                            //vector_fairness.add("Justice"+(i+1));
                        }
                    }
                }
                // Lines 14-16
                if (DT instanceof ModuleWithStrongFairness) {
                    ModuleWithStrongFairness strongDes = (ModuleWithStrongFairness) DT;
                    for (int i = 0; i < strongDes.compassionNum(); i++) {
                        if (!feas.and(strongDes.pCompassionAt(i)).isZero()) {
                            // check if C requirement i is already satisfied
                            fulfill = Env.FALSE();
                            for (int j = 0; j < period.size(); j++) {
                                fulfill = period.elementAt(j).and(
                                        strongDes.qCompassionAt(i)).satOne(
                                        strongDes.moduleUnprimeVars(), false);
                                // fulfill =
                                // period.elementAt(j).and(design.qCompassionAt(i)).satOne();
                                if (!fulfill.isZero()) {
                                    //LXY
                                    //vector_period_idx.add((Integer)j);
                                    //vector_fairness.add("Compassion.q"+(i+1));
                                    break;
                                }
                            }

                            if (fulfill.isZero()) {
                                BDD from = period.lastElement();
                                BDD to = feas.and(strongDes.qCompassionAt(i));
                                path = strongDes.shortestPath(from, to);
                                // eliminate the edge since from is already in period
                                for (int j = 1; j < path.length; j++)
                                    period.add(path[j]);

                                //LXY
                                //vector_period_idx.add((Integer)period.size()-1);
                                //vector_fairness.add("Compassion.q"+(i+1));
                            }
                        }
                    }
                }
                //
                // Close cycle
                //
                // A period of length 1 may be fair, but it might be the case that
                // period[1] is not a successor of itself. The routine path
                // will add nothing. To solve this
                // case we add another state to _period, now it will be OK since
                // period[1] and period[n] will not be equal.

                // Line 17, but modified
                if (!period.firstElement().and(period.lastElement()).isZero()) {
                    // The first and last states are already equal, so we do not
                    // need to extend them to complete a cycle, unless period is
                    // a degenerate case of length = 1, which is not a successor of
                    // self.
                    if (period.size() == 1) {
                        // Check if _period[1] is a successor of itself.
                        if (period.firstElement().and(
                                DT.succ(period.firstElement())).isZero()) {
                            // period[1] is not a successor of itself: Add state to
                            // period.
                            period.add(DT.succ(period.firstElement()).satOne(
                                    DT.moduleUnprimeVars(), false));
                            // period.add(design.succ(period.firstElement()).satOne());

                            // Close cycle.
                            BDD from = period.lastElement();
                            BDD to = period.firstElement();
                            path = DT.shortestPath(from, to);
                            // eliminate the edges since from and to are already in
                            // period
                            for (int i = 1; i < path.length - 1; i++) {
                                period.add(path[i]);
                            }
                        }
                    }
                } else {
                    BDD from = period.lastElement();
                    BDD to = period.firstElement();
                    path = DT.shortestPath(from, to);
                    // eliminate the edges since from and to are already in period
                    for (int i = 1; i < path.length - 1; i++) {
                        period.add(path[i]);
                    }
                }

                int loopNodeIdx=prefix.size();  // the index of the first node of period

                // LXY: Now prefix and period are prepared, and there is a transition from the last element of period
                // to the first element (its index is loopNodeIdx) of period

                prefix.addAll(period);
                // now prefix is the composition of prefix and period, and the first element of prefix is state n.s

                String first_created_edgeId=null;
                createdPathNumber++; // create a new path no matter the size of path
                Edge e;

                String pred_nid, cur_nid;
                pred_nid = fromNodeId;
                //graph.addNodeSatSpec(fromNodeId, spec, true);

                Vector<String> trunkNodePath=new Vector<String>();   // the trunk node path will be created
                trunkNodePath.add(fromNodeId);

                for (int i = 1; i < prefix.size(); i++) {
                    graph.addNode(createdPathNumber, i, prefix.get(i));
                    cur_nid = createdPathNumber + "." + i;

                    trunkNodePath.add(cur_nid);

                    String edgeId=pred_nid + "->" + cur_nid;
                    e = graph.addArc(edgeId, pred_nid, cur_nid, true);
                    if(first_created_edgeId==null) {
                        first_created_edgeId=edgeId;
//                        graph.edgeAddAnnotation(edgeId,
//                                "Path" + createdPathNumber + "|=" + simplifySpecString(spec.toString(), false));
                    }
                    pred_nid = cur_nid;
                }

                //closing period
                String to_nodeId=null;
                if(loopNodeIdx==0) // the size of original prefix is 0
                    to_nodeId=fromNodeId;
                else
                    to_nodeId=createdPathNumber+"."+loopNodeIdx;
                e = graph.addArc(pred_nid+"->"+to_nodeId, pred_nid, to_nodeId, true);

                // append the fairness annotations to the nodes of this path
                for(int i=0; i<vector_period_idx.size(); i++){
                    int idx=(int)vector_period_idx.get(i);
                    String ann=vector_fairness.get(i);
                    if(idx==0){
                        graph.nodeAddAnnotation(fromNodeId, ann);
                    }else{//idx>0
                        graph.nodeAddAnnotation(createdPathNumber+"."+idx, ann);
                    }
                }

                DT.setAllTransRestrictions(old_trans_restrictions);

                NodePath nodePath = new NodePath(trunkNodePaths.size(), trunkNodePath, loopNodeIdx);
                trunkNodePaths.add(nodePath);

                SpecExp se = (SpecExp) spec;
                Operator op = se.getOperator();
                Spec[] child = se.getChildren();
                if(!op.isTemporalOp()){
                    // spec is NOT a principally temporal formula, it need to be show before calling explainPath(spec...)
                    graph.edgeAddSpec(first_created_edgeId,spec,nodePath,0,true);
                }
                return explainPath(spec, nodePath, 0);
            }
        }
    }
*/

    // Premises: initially, spec satisfies !eE/\eT; path^pos|=spec;
    // Results: attached necessary satisfied subformulas of spec at some nodes over the suffix path^pos
    public boolean explainPath(Spec spec,            // the spec to be explained
                               NodePath path,        // the node path
                               int pos              // spec is explained over path^pos, the suffix of the path starting from the logical position pos
    ) throws ModelCheckException, SpecException, ModelCheckAlgException, SMVParseException, ModuleException {
        if(path==null) return false;

        int startIdx=path.at(pos);
        String snid=path.get(startIdx);
        Node sn=graph.getNode(snid);
        BDD startState=graph.nodeGetBDD(snid);

        //SMVModule tester=SpecTesterMap.get(spec); // the tester for spec

        if(spec.isStateSpec()){
            // spec is a state formula
            //return witness(spec,sn);
            graph.nodeAddSpec(snid,spec);
        }else{
            // spec is NOT a state formula
            SpecExp se=(SpecExp)spec;
            Operator op=se.getOperator();
            Spec[] child=se.getChildren();

            if(op==Operator.AND){
                boolean b1= explainPath(child[0],path,pos);
                return b1 && explainPath(child[1],path,pos);
            }else if(op==Operator.OR){ //spec=f OR g
                boolean fNeedExplain = needExpE(child[0]) || needExpT(child[0]);
                Spec p,q;
                if(!fNeedExplain) {p=child[0]; q=child[1];} else {p=child[1]; q=child[0];}
                BDD pBdd = testerInfo.getSpecBdd(p); if(pBdd==null) return false; // SpecBDDMap.get(p);
                if (!startState.and(pBdd).isZero())
                    return explainPath(p,path,pos);
                else return explainPath(q,path,pos);
            }else{
                // spec is a principally temporal formula spec=Xf, fUg, fRg, fU a..b g, or f R a..b g
                // explain spec according to its semantics over path^pos;

                if(op==Operator.NEXT){ //spec=X f
                    BDD X= testerInfo.getSpecBdd(spec); if(X==null) return false; // SpecBDDMap.get(spec);
                    if(X==null || startState.and(X).isZero()) return false;
                    int ni=path.at(pos+1);
                    BDD nextState=graph.nodeGetBDD(path.get(ni));
                    BDD Xf= testerInfo.getSpecBdd(child[0]); if(Xf==null) return false; //SpecBDDMap.get(child[0]);
                    if(Xf==null || nextState.and(Xf).isZero()) return false;

                    String firstEdgeId=snid+"->"+path.get(ni);
                    graph.edgeAddSpec(firstEdgeId,spec,path,pos,true);

                    String eid = path.get(path.at(pos + 1)) + "->" + path.get(path.at(pos + 2));
                    if(child[0].isStateSpec()) graph.nodeAddSpec(path.get(ni),child[0]);
                    else graph.edgeAddSpec(eid, child[0], path, pos + 1, false);
                    //return explainPath(child[0],path,pos+1);
                }else if(op==Operator.UNTIL){
                    BDD X= testerInfo.getSpecBdd(spec); if(X==null) return false; //SpecBDDMap.get(spec);
                    BDD Xf= testerInfo.getSpecBdd(child[0]); if(Xf==null) return false; //SpecBDDMap.get(child[0]);
                    BDD Xg= testerInfo.getSpecBdd(child[1]); if(Xg==null) return false; //SpecBDDMap.get(child[1]);
                    if(X==null) return false;
                    if(Xf==null) return false;
                    if(Xg==null) return false;
                    if(startState.and(X).isZero()) return false;

                    graph.edgeAddSpec(path.get(path.at(pos))+"->"+path.get(path.at(pos+1)),spec,path,pos,true);

                    String curNid,nextNid;
                    boolean exp=true;  // exp=false then stop explaining f
                    for(int p=pos;;p++){
                        curNid=path.get(path.at(p));
                        nextNid=path.get(path.at(p+1));
                        BDD curState=graph.nodeGetBDD(curNid); if(curState==null) return false;
                        String eid = path.get(path.at(p))+"->"+path.get(path.at(p+1));
                        if(!curState.and(Xg).isZero()) { // |=g
                            if(child[1].isStateSpec()) graph.nodeAddSpec(path.get(path.at(p)),child[1]);
                            else graph.edgeAddSpec(eid,child[1],path,p,false);
                            return true;
                        }else if(exp && !curState.and(Xf).isZero()){ // |=f
                            if(child[0].isStateSpec()) graph.nodeAddSpec(path.get(path.at(p)),child[0]);
                            else graph.edgeAddSpec(eid,child[0],path,p,false);
                            exp=path.needExplainNextPosition(pos,p);
                        }
                    }
                }else if(op==Operator.RELEASES){
                    BDD X= testerInfo.getSpecBdd(spec); if(X==null) return false; //SpecBDDMap.get(spec);
                    BDD Xf= testerInfo.getSpecBdd(child[0]); if(Xf==null) return false; //SpecBDDMap.get(child[0]);
                    BDD Xg= testerInfo.getSpecBdd(child[1]); if(Xg==null) return false; //SpecBDDMap.get(child[1]);
                    if(X==null) return false;
                    if(Xf==null) return false;
                    if(Xg==null) return false;
                    if(startState.and(X).isZero()) return false;

                    String firstEdgeId=snid+"->"+path.get(path.at(pos+1));
                    graph.edgeAddSpec(firstEdgeId,spec,path,pos,true);

                    String curNid,nextNid;
                    boolean exp=true;  // exp=false then stop explaining f
                    for(int p=pos;;p++){
                        curNid=path.get(path.at(p));
                        nextNid=path.get(path.at(p+1));
                        BDD curState=graph.nodeGetBDD(curNid); if(curState==null) return false;
                        String eid = curNid+"->"+nextNid;
                        if(!curState.and(Xf.and(Xg)).isZero()) {  // |=f/\g
                            if(child[0].isStateSpec()) graph.nodeAddSpec(path.get(path.at(p)),child[0]);
                            else graph.edgeAddSpec(eid,child[0],path,p,false);
                            if(child[1].isStateSpec()) graph.nodeAddSpec(path.get(path.at(p)),child[1]);
                            else graph.edgeAddSpec(eid,child[1],path,p,false);
                            return true;
                        }else if(exp && !curState.and(Xg).isZero()){  // |=g
                            if(child[1].isStateSpec()) graph.nodeAddSpec(path.get(path.at(p)),child[1]);
                            else graph.edgeAddSpec(eid,child[1],path,p,false);
                            exp=path.needExplainNextPosition(pos,p);
                        }
                        // if !exp, it means that f/\g does not hold at all nodes of the path, then stop explaining g
                        if(!exp) return true;
                    }

                }else if(op==Operator.B_UNTIL){
                    SpecRange range = (SpecRange) child[1];
                    int a = range.getFrom(), b = range.getTo();
                    if(a<0) throw new ModelCheckException("The lower bound of " + spec + "cannot be less than 0.");
                    if(b<0) throw new ModelCheckException("The upper bound of " + spec + "cannot be less than 0.");
                    if(a>b) throw new ModelCheckException("The lower bound of " + spec + "cannot be larger than the upper bound.");

                    BDD X= testerInfo.getSpecBdd(spec); if(X==null || (X!=null && startState.and(X).isZero())) return false;
                    BDD Xf= testerInfo.getSpecBdd(child[0]); if(Xf==null) return false;
                    BDD Xg= testerInfo.getSpecBdd(child[2]); if(Xg==null) return false;

                    graph.edgeAddSpec(path.get(path.at(pos))+"->"+path.get(path.at(pos+1)), spec,path,pos,true);

                    String curNid, nextNid;
                    boolean exp=true;
                    for(int p=pos; p<pos+a && exp; p++){
                        curNid=path.get(path.at(p));
                        nextNid=path.get(path.at(p+1));
                        BDD curState=graph.nodeGetBDD(curNid);
                        if(curState.and(Xf).isZero()) return false;
                        if(child[0].isStateSpec()) graph.nodeAddSpec(curNid,child[0]);
                        else graph.edgeAddSpec(curNid+"->"+nextNid,child[0],path,p,false);
                        exp=path.needExplainNextPosition(pos,p);
                    }

                    // show the lower and upper bounds of the spec
                    graph.nodeAddAnnotation(path.get(path.at(pos+a)),"lower bound "+a+" of "+simplifySpecString(spec,false));
                    graph.nodeAddAnnotation(path.get(path.at(pos+b)),"upper bound "+b+" of "+simplifySpecString(spec,false));

                    exp=true;
                    for(int p=pos+a; p<=pos+b; p++){
                        curNid=path.get(path.at(p));
                        nextNid=path.get(path.at(p+1));
                        BDD curState=graph.nodeGetBDD(curNid);
                        if(!curState.and(Xg).isZero()){ // explain g
                            if(child[2].isStateSpec()) graph.nodeAddSpec(curNid,child[2]);
                            else graph.edgeAddSpec(curNid+"->"+nextNid,child[2],path,p,false);
                            return true;
                        }else if(exp && !curState.and(Xf).isZero()){ // explain f
                            if (child[0].isStateSpec()) graph.nodeAddSpec(curNid, child[0]);
                            else graph.edgeAddSpec(curNid + "->" + nextNid, child[0], path, p, false);
                            exp=path.needExplainNextPosition(pos,p);
                        }
                    }
                    return true;

                }else if(op==Operator.B_RELEASES){
                    SpecRange range = (SpecRange) child[1];
                    int a = range.getFrom(), b = range.getTo();
                    if(a<0) throw new ModelCheckException("The lower bound of " + spec + "cannot be less than 0.");
                    if(b<0) throw new ModelCheckException("The upper bound of " + spec + "cannot be less than 0.");
                    if(a>b) throw new ModelCheckException("The lower bound of " + spec + "cannot be larger than the upper bound.");

                    BDD X= testerInfo.getSpecBdd(spec); if(X==null || (X!=null && startState.and(X).isZero())) return false;
                    BDD Xf= testerInfo.getSpecBdd(child[0]); if(Xf==null) return false;
                    BDD Xg= testerInfo.getSpecBdd(child[2]); if(Xg==null) return false;

                    graph.edgeAddSpec(path.get(path.at(pos))+"->"+path.get(path.at(pos+1)), spec,path,pos,true);

                    // search f
                    String curNid, nextNid;
                    int fPos=-1;
                    boolean exp=true;
                    for(int p=pos;p<pos+a && fPos==-1 && exp;p++){
                        curNid=path.get(path.at(p));
                        BDD curState=graph.nodeGetBDD(curNid);
                        if(!curState.and(Xf).isZero()) fPos=p;
                        exp=path.needExplainNextPosition(pos,p);
                    }
                    if(fPos!=-1){
                        //found f in [0,a-1] and explain it
                        curNid=path.get(path.at(fPos));
                        nextNid=path.get(path.at(fPos+1));
                        if(child[0].isStateSpec()) graph.nodeAddSpec(curNid,child[0]);
                        else graph.edgeAddSpec(curNid+"->"+nextNid,child[0],path,fPos,false);
                        return true;
                    }

                    // f is alway false in [0,a-1]
                    Spec neg_f = NNF(new SpecExp(Operator.NOT, child[0])); // neg_f = !f
                    SMVModule design = (SMVModule)getDesign();
                    int oldDesignVariablesNum=design.getAll_couples().size();
                    BDD neg_f_bdd = sat(neg_f);
                    if(design.getAll_couples().size()>oldDesignVariablesNum){
                        // the tester for neg_f is NOT empty, refesh the set of feasible states
                        feasibleStatesForWitnessE = design.feasible();
                    }else {
                        // the tester for neg_f is empty
                        if (feasibleStatesForWitnessE == null) feasibleStatesForWitnessE = design.feasible();
                    }
                    neg_f_bdd = neg_f_bdd.and(feasibleStatesForWitnessE);

                    // explain !f at all positions in [0,a-1]
                    exp=true;
                    for(int p=pos; p<pos+a && exp; p++){
                        curNid=path.get(path.at(p));
                        nextNid=path.get(path.at(p+1));
                        BDD curState=graph.nodeGetBDD(curNid);
                        if(curState.and(neg_f_bdd).isZero()) return false;
                        if(neg_f.isStateSpec()) graph.nodeAddSpec(curNid,neg_f);
                        else graph.edgeAddSpec(curNid+"->"+nextNid,neg_f,path,p,false);
                        exp=path.needExplainNextPosition(pos,p);
                    }

                    // show the lower and upper bounds of the spec
                    graph.nodeAddAnnotation(path.get(path.at(pos+a)),"lower bound "+a+" of "+simplifySpecString(spec,false));
                    graph.nodeAddAnnotation(path.get(path.at(pos+b)),"upper bound "+b+" of "+simplifySpecString(spec,false));

                    exp=true;
                    for(int p=pos+a; p<=pos+b; p++){
                        curNid=path.get(path.at(p));
                        nextNid=path.get(path.at(p+1));
                        BDD curState=graph.nodeGetBDD(curNid);
                        if(!curState.and(Xf).isZero() && !curState.and(Xg).isZero()){  // explain f/\g
                            if(child[0].isStateSpec()) graph.nodeAddSpec(curNid,child[0]);
                            else graph.edgeAddSpec(curNid+"->"+nextNid,child[0],path,p,false);
                            if(child[2].isStateSpec()) graph.nodeAddSpec(curNid,child[2]);
                            else graph.edgeAddSpec(curNid+"->"+nextNid,child[2],path,p,false);
                            return true;
                        }else if(exp && !curState.and(Xg).isZero()){ // explain g
                            if (child[2].isStateSpec()) graph.nodeAddSpec(curNid, child[2]);
                            else graph.edgeAddSpec(curNid + "->" + nextNid, child[2], path, p, false);
                            exp=path.needExplainNextPosition(pos,p);
                        }
                    }
                    return true;

                }else
                    return true;
            }
        }
        return true;
    }



}
