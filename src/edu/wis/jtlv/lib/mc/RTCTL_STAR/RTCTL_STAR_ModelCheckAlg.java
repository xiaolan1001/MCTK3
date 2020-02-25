package edu.wis.jtlv.lib.mc.RTCTL_STAR;

import edu.wis.jtlv.env.Env;
import edu.wis.jtlv.env.core.smv.SMVParseException;
import edu.wis.jtlv.env.core.smv.eval.*;
import edu.wis.jtlv.env.module.*;
import edu.wis.jtlv.env.spec.*;
import edu.wis.jtlv.lib.AlgExceptionI;
import edu.wis.jtlv.lib.AlgResultI;
import edu.wis.jtlv.lib.AlgResultPath;
import edu.wis.jtlv.lib.AlgResultString;
import edu.wis.jtlv.lib.mc.ModelCheckAlgException;
import edu.wis.jtlv.lib.mc.ModelCheckAlgI;
import edu.wis.jtlv.old_lib.mc.ModelCheckException;
import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDVarSet;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.ui.spriteManager.Sprite;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Vector;

class CacheSpecTesterInfo {
    Spec spec;
    BDD specBdd;
    Vector<String> AuxVarNames; // the set of aux variables created for the tester of spec

    CacheSpecTesterInfo(Spec spec, BDD specBdd){
        this.spec=spec;
        this.specBdd=specBdd;
        this.AuxVarNames=new Vector<String>();
    }
}

// the tester contains the initial conditions and transitions of ALL temporal operators in a spec
// including these temporal operators restricted by path quantifier E or A
class RTCTLsTester{
    SMVModule module;
    Vector<String> auxVarNames;
    LinkedHashMap<String, CacheSpecTesterInfo> cacheSpecsInfo;

//    LinkedHashMap<String, BDD> cachePrinTempSpecBDDs;
        // for element <specStr, bdd>, specStr is the string of a principally temporal spec
        // bdd is the BDD of the spec's output formula

    RTCTLsTester(){
        module=new SMVModule("RTCTLsTester");
        auxVarNames=new Vector<String>();
        cacheSpecsInfo=new LinkedHashMap<String, CacheSpecTesterInfo>();
    }

    Vector<String> getCreatedAuxVarNames(int beforeVarsNum, int afterVarsNum){
        return (Vector<String>) auxVarNames.subList(beforeVarsNum,afterVarsNum);
    }

    CacheSpecTesterInfo cachePutSpec(Spec spec, BDD specBdd){
        return cacheSpecsInfo.put(spec.toString(),new CacheSpecTesterInfo(spec,specBdd));
    }

    CacheSpecTesterInfo cacheGetSpec(String specStr){
        return cacheSpecsInfo.get(specStr);
    }

    BDD cacheGetSpecBdd(Spec spec){
        BDD ret=null;
        if(spec instanceof SpecBDD)
            return ((SpecBDD)spec).getVal();
        else { // spec is SpecExp
            CacheSpecTesterInfo specInfo = this.cacheGetSpec(spec.toString());
            if(specInfo!=null) return specInfo.specBdd;
            else return null;
        }
    }
}

class NodePath {
    Vector<String> nodes; // the list of node IDs of the node path
    int loopIndex; // the first node's index of the period of the lasso node path; loopIndex==-1 denotes that the path is finite path
    int pathIndex;

    public NodePath(int pathIndex){
        nodes = new Vector<String>();
        loopIndex = -1;
        this.pathIndex=pathIndex;
    }

    public NodePath(int pathIndex, Vector<String> nodeIdList, int loopIndex){
        this.pathIndex=pathIndex;
        this.nodes = nodeIdList;
        this.loopIndex = loopIndex;
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
    boolean needExplainNextPosition(int startPos, int pos){
        int startIdx=at(startPos);
        int idx=at(pos);
        if((startIdx<loopIndex && idx==this.size()-1) ||
                (startIdx>=loopIndex && at(pos+1)==startIdx))
            return false;
        else
            return true;
    }

}



public class RTCTL_STAR_ModelCheckAlg extends ModelCheckAlgI {
    private Spec property;

    private Spec chkProp; // the property actually checked
    private BDD chkBdd; // the BDD obtained by checking chkProp
    private BDDVarSet visibleVars;

    private BDD feasibleStatesForWitnessE=null;

    private RTCTLsTester tester=null;

    private int tester_id = 0;
    private int field_id = 0;
    private int createdPathNumber = 0; // the number of the paths currently created

    private Vector<NodePath> trunkNodePaths = new Vector<NodePath>();

    private GraphExplainRTCTLs graph; //used for displaying witness graph
    boolean isShowGraph;

    public void setShowGraph(boolean isShowGraph) {
        this.isShowGraph = isShowGraph;
    }

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
    public RTCTL_STAR_ModelCheckAlg(Module design, Spec property) {
        super(design);
        this.property = property;
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
    public RTCTL_STAR_ModelCheckAlg(Module design) {
        super(design);
//		this.tester = user_tester;
    }

    public boolean testerIsEmpty(SMVModule tester) {
        if (tester == null)
            return true;
        return tester.getAll_couples().size() == 0;
    }

    // return the BDDs of the aux variables for the tester
    private BDDVarSet tester_getAuxVars_BDDVarSet(SMVModule tester) {
        BDDVarSet vs = Env.getEmptySet();
        for (ModuleBDDField var : tester.getAll_couples()) {
            vs = vs.id().union(var.support());
        }
        return vs;
    }

    // Premises: auxVarNames is the vector of aux variables created for spec, it must be created before used
    // Results: this.tester is created for spec; this.testerAuxVarNames contains the set of created aux variables
    public BDD sat(Spec spec)
            throws ModuleException, SMVParseException, ModelCheckException, ModelCheckAlgException, SpecException {
        if (spec instanceof SpecBDD) {
            return ((SpecBDD) spec).getVal();
        }
        if (spec instanceof SpecRange || spec instanceof SpecAgentIdentifier) return null;

        CacheSpecTesterInfo specInfo = tester.cacheGetSpec(spec.toString());
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
        //-----------------------------------------------------------------------------------------------------------
        // logical connectives
        //-----------------------------------------------------------------------------------------------------------
        if (op == Operator.NOT) {
            lc = sat(child[0]);
            specBdd = lc.not();
            tester.cachePutSpec(spec,specBdd); return specBdd;
        }
        if (op == Operator.AND) {
            lc = sat(child[0]);
            rc = sat(child[1]);
            specBdd=lc.and(rc);
            tester.cachePutSpec(spec,specBdd); return specBdd;
        }
        if (op == Operator.OR) {
            lc = sat(child[0]);
            rc = sat(child[1]);
            specBdd=lc.or(rc);
            tester.cachePutSpec(spec,specBdd); return specBdd;
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
            x = tester.module.addVar("X" + (++field_id)); // boolean variable
            xBdd = x.getDomain().ithVar(1);
            BDD p_lc = Env.prime(lc);
            tester.module.conjunctTrans(xBdd.imp(p_lc));
            tester.cachePutSpec(spec,xBdd); return xBdd;
        }
        if (op == Operator.UNTIL) {
            lc = sat(child[0]);
            rc = sat(child[1]);
            x = tester.module.addVar("X" + (++field_id)); // boolean variable
            xBdd = x.getDomain().ithVar(1);
            BDD p_x = Env.prime(xBdd);
            //tester.addInitial(xBdd.imp(c1.or(c2)));
            tester.module.conjunctTrans(xBdd.imp(rc.or(lc.and(p_x))));
            tester.module.addJustice(xBdd.imp(rc));
            tester.cachePutSpec(spec,xBdd); return xBdd;
        }
        if (op == Operator.RELEASES) {
            lc = sat(child[0]);
            rc = sat(child[1]);
            x = tester.module.addVar("X" + (++field_id)); // boolean variable
            xBdd = x.getDomain().ithVar(1);
            BDD p_x = Env.prime(xBdd);
            tester.module.conjunctTrans(xBdd.imp(rc.and(lc.or(p_x))));
            tester.cachePutSpec(spec,xBdd); return xBdd;
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
            tester.cachePutSpec(spec,c2);
            return c2;
        }

        // now 0<=a<=b and !(a=0 and b=0
        c1 = sat(child[0]);
        c2 = sat(child[2]);

        ModuleBDDField x = null, l = null, w = null;
        BDD xBdd; // output variable of the tester
        x = tester.module.addVar("X" + (++field_id)); // boolean variable
        xBdd = x.getDomain().ithVar(1);

        if ((a == b) || (a == 0 && b > 0)) {
            l = tester.module.addVar("L" + field_id, 0, b);
            w = null;
            ValueDomStmt xe = new ValueDomStmt(tester.module, x); // the expression of variable x
            OpNext pxe = new OpNext(xe); // the expression of next(x)
            ValueDomStmt le = new ValueDomStmt(tester.module, l); // the expression of variable l
            OpNext ple = new OpNext(le); // the expression of next(l)

            set_context_module(tester.module);
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
                tester.module.conjunctTrans(xBdd.and(lGT0).imp(c1.and(NxE1).and(NlElM1))); // (x & l>0) -> (c1 & x' & l'=l-1)
                tester.module.conjunctTrans(xBdd.and(lE0).imp(c2)); // (x & l=0) -> c2
            } else { // a==0 && b>0
                tester.module.conjunctTrans(xBdd.and(lGT0).imp(c2.or(c1.and(NxE1).and(NlElM1)))); // (x & l>0) -> (c2 | (c1 & x' & l'=l-1))
                tester.module.conjunctTrans(xBdd.and(lE0).imp(c2)); // (x & l=0) -> c2
            }

            BDD specBdd=xBdd.and(lEb); // x & l=b;
            tester.cachePutSpec(spec,specBdd);
            return specBdd;
        } else { // 0<a<b
            l = tester.module.addVar("L" + field_id, 0, a);
            w = tester.module.addVar("W" + field_id, 0, b - a);
            ValueDomStmt xe = new ValueDomStmt(tester.module, x); // the expression of variable x
            OpNext pxe = new OpNext(xe); // the expression of next(x)
            ValueDomStmt le = new ValueDomStmt(tester.module, l); // the expression of variable l
            OpNext ple = new OpNext(le); // the expression of next(l)
            ValueDomStmt we = new ValueDomStmt(tester.module, w); // the expression of variable w
            OpNext pwe = new OpNext(we); // the expression of next(w)

            set_context_module(tester.module);
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
/*
            BDD lGT0 = new StmtOperator(tester, new OpGT(new ValueDomStmt(tester, l),
                    new ValueConsStrStmt(tester, new String[]{"0"}))).eval_stmt().toBDD(); //l>0
            BDD NxE1 = new StmtOperator(tester, new OpEqual(new OpNext(new ValueDomStmt(tester, x)), //x'=1
                    new ValueConsStrStmt(tester, new String[]{"1"}))).eval_stmt().toBDD();
            BDD NlElM1 = new StmtOperator(tester, new OpEqual(new OpNext(new ValueDomStmt(tester, l)), //l'=l-1
                    new OpMinus(new ValueDomStmt(tester, l), new ValueConsStrStmt(tester, new String[]{"1"})))).eval_stmt().toBDD();
            BDD lE0 = new StmtOperator(tester, new OpEqual(new ValueDomStmt(tester, l),
                    new ValueConsStrStmt(tester, new String[]{"0"}))).eval_stmt().toBDD(); //l=0
            BDD lEa = new StmtOperator(tester, new OpEqual(new ValueDomStmt(tester, l),
                    new ValueConsStrStmt(tester, new String[]{"" + a}))).eval_stmt().toBDD(); //l=a
            BDD wGT0 = new StmtOperator(tester, new OpGT(new ValueDomStmt(tester, w),
                    new ValueConsStrStmt(tester, new String[]{"0"}))).eval_stmt().toBDD();//w>0
            BDD NwEw = new StmtOperator(tester, new OpEqual(new OpNext(new ValueDomStmt(tester, w)), //w'=w
                    new ValueDomStmt(tester, w))).eval_stmt().toBDD();
            BDD NlE0 = new StmtOperator(tester, new OpEqual(new OpNext(new ValueDomStmt(tester, l)), //l'=0
                    new ValueConsStrStmt(tester, new String[]{"0"}))).eval_stmt().toBDD();
            BDD NwEwM1 = new StmtOperator(tester, new OpEqual(new OpNext(new ValueDomStmt(tester, w)),//next(w)=w-1
                    new OpMinus(new ValueDomStmt(tester, w), new ValueConsStrStmt(tester, new String[]{"1"})))).eval_stmt().toBDD();
           BDD wE0 = new StmtOperator(tester, new OpEqual(new ValueDomStmt(tester, w),
                    new ValueConsStrStmt(tester, new String[]{"0"}))).eval_stmt().toBDD();//w=0
            BDD wEbMa = new StmtOperator(tester, new OpEqual(new ValueDomStmt(tester, w),
                    new ValueConsStrStmt(tester, new String[]{"" + (b - a)}))).eval_stmt().toBDD();//w=b-a
*/

            tester.module.conjunctTrans(xBdd.and(lGT0).and(wGT0).imp(c1.and(NxE1).and(NlElM1).and(NwEw))); // (x & l>0 & w>0) -> (c1 & x' & l'=l-1 & w'=w)
            tester.module.conjunctTrans(xBdd.and(lE0).and(wGT0).imp(c2.or(c1.and(NxE1).and(NlE0).and(NwEwM1)))); // (x & l=0 & w>0) -> (c2 | (c1 & x' & l'=0 & w'=w-1))
            tester.module.conjunctTrans(xBdd.and(lE0).and(wE0).imp(c2)); // (x & l=0 & w=0) -> c2

            BDD specBdd = xBdd.and(lEa).and(wEbMa); // x & l=a & w=b-a;
            tester.cachePutSpec(spec,specBdd);
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
            tester.cachePutSpec(spec,c2);
            return c2;
        }

        // now 0<=a<=b and !(a=0 and b=0)
        c1 = sat(child[0]);
        c2 = sat(child[2]);

        ModuleBDDField x = null, l = null, w = null;
        BDD xBdd; // output variable of the tester
        x = tester.module.addVar("X" + (++field_id)); // boolean variable
        xBdd = x.getDomain().ithVar(1);
        if ((a == b) || (a == 0 && b > 0)) {
            l = tester.module.addVar("L" + field_id, 0, b);
            w = null;
            ValueDomStmt xe = new ValueDomStmt(tester.module, x); // the expression of variable x
            OpNext pxe = new OpNext(xe); // the expression of next(x)
            ValueDomStmt le = new ValueDomStmt(tester.module, l); // the expression of variable l
            OpNext ple = new OpNext(le); // the expression of next(l)

            set_context_module(tester.module);
            BDD lGT0 = bexp2bdd(bexp(l, ">", "0")); // l>0
            BDD NxE1 = bexp2bdd(bexp(pxe,"=","1")); // x'=1
            BDD NlElM1 = bexp2bdd(bexp(ple,"=",bexp(l,"-","1"))); // l'=l-1
            BDD lE0 = bexp2bdd(bexp(l,"=","0")); //l=0
            BDD lEb = bexp2bdd(bexp(l,"=",""+b)); //l=b
/*
            BDD lGT0 = new StmtOperator(tester, new OpGT(new ValueDomStmt(tester, l),
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
                tester.module.conjunctTrans(xBdd.and(lGT0).imp(c1.or(NxE1.and(NlElM1)))); // (x & l>0) -> (c1 | (x' & l'=l-1))
                tester.module.conjunctTrans(xBdd.and(lE0).imp(c2)); // (x & l=0) -> c2
            } else { // a==0 && b>0
                tester.module.conjunctTrans(xBdd.and(lGT0).imp(c2.and(c1.or(NxE1.and(NlElM1))))); // (x & l>0) -> (c2 & (c1 | (x' & l'=l-1)))
                tester.module.conjunctTrans(xBdd.and(lE0).imp(c2)); // (x & l=0) -> c2
            }

            BDD specBdd = xBdd.and(lEb); // x & l=b;
            tester.cachePutSpec(spec,specBdd);
            return specBdd;
        } else { // 0<a<b
            l = tester.module.addVar("L" + field_id, 0, a);
            w = tester.module.addVar("W" + field_id, 0, b - a);
            ValueDomStmt xe = new ValueDomStmt(tester.module, x); // the expression of variable x
            OpNext pxe = new OpNext(xe); // the expression of next(x)
            ValueDomStmt le = new ValueDomStmt(tester.module, l); // the expression of variable l
            OpNext ple = new OpNext(le); // the expression of next(l)
            ValueDomStmt we = new ValueDomStmt(tester.module, w); // the expression of variable w
            OpNext pwe = new OpNext(we); // the expression of next(w)

            set_context_module(tester.module);
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
/*
            BDD lGT0 = new StmtOperator(tester, new OpGT(new ValueDomStmt(tester, l),
                    new ValueConsStrStmt(tester, new String[]{"0"}))).eval_stmt().toBDD(); //l>0
            BDD NxE1 = new StmtOperator(tester, new OpEqual(new OpNext(new ValueDomStmt(tester, x)), //next(x)
                    new ValueConsStrStmt(tester, new String[]{"1"}))).eval_stmt().toBDD();
            BDD NlElM1 = new StmtOperator(tester, new OpEqual(new OpNext(new ValueDomStmt(tester, l)), //next(l)=l-1
                    new OpMinus(new ValueDomStmt(tester, l), new ValueConsStrStmt(tester, new String[]{"1"})))).eval_stmt().toBDD();
            BDD lE0 = new StmtOperator(tester, new OpEqual(new ValueDomStmt(tester, l),
                    new ValueConsStrStmt(tester, new String[]{"0"}))).eval_stmt().toBDD(); //l=0
            BDD lEa = new StmtOperator(tester, new OpEqual(new ValueDomStmt(tester, l),
                    new ValueConsStrStmt(tester, new String[]{"" + a}))).eval_stmt().toBDD(); //l=a
            BDD wGT0 = new StmtOperator(tester, new OpGT(new ValueDomStmt(tester, w),
                    new ValueConsStrStmt(tester, new String[]{"0"}))).eval_stmt().toBDD();//w>0
            BDD NwEw = new StmtOperator(tester, new OpEqual(new OpNext(new ValueDomStmt(tester, w)), //next(w)=w)
                    new ValueDomStmt(tester, w))).eval_stmt().toBDD();
            BDD NlE0 = new StmtOperator(tester, new OpEqual(new OpNext(new ValueDomStmt(tester, l)), //next(l)=0)
                    new ValueConsStrStmt(tester, new String[]{"0"}))).eval_stmt().toBDD();
            BDD NwEwM1 = new StmtOperator(tester, new OpEqual(new OpNext(new ValueDomStmt(tester, w)),//next(w)=w-1
                    new OpMinus(new ValueDomStmt(tester, w), new ValueConsStrStmt(tester, new String[]{"1"})))).eval_stmt().toBDD();
            BDD wE0 = new StmtOperator(tester, new OpEqual(new ValueDomStmt(tester, w),
                    new ValueConsStrStmt(tester, new String[]{"0"}))).eval_stmt().toBDD();//w=0
            BDD wEbMa = new StmtOperator(tester, new OpEqual(new ValueDomStmt(tester, w),
                    new ValueConsStrStmt(tester, new String[]{"" + (b - a)}))).eval_stmt().toBDD();//w=b-a
 */
            tester.module.conjunctTrans(xBdd.and(lGT0).and(wGT0).imp(c1.or(NxE1.and(NlElM1).and(NwEw)))); // (x & l>0 & w>0) -> (c1 | (x' & l'=l-1 & w'=w))
            tester.module.conjunctTrans(xBdd.and(lE0).and(wGT0).imp(c2.and(c1.or(NxE1.and(NlE0).and(NwEwM1))))); // (x & l=0 & w>0) -> (c2 & (c1 | (x' & l'=0 & w'=w-1)))
            tester.module.conjunctTrans(xBdd.and(lE0).and(wE0).imp(c2)); // (x & l=0 & w=0) -> c2

            BDD specBdd = xBdd.and(lEa).and(wEbMa); // x & l=a & w=b-a;
            tester.cachePutSpec(spec,specBdd);
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
        BDD c1 = null, specBdd = null;
        if (op != Operator.EE) return null;

        c1 = sat(child[0]); // build the sub-tester of child[0]
        specBdd = design.feasible().and(c1);
/*
        int oldTesterVariablesNumber = tester.module.getAll_couples().size(); // the set of tester variables before building the sub-tester for child[0]
        c1 = sat(child[0]); // build the sub-tester of child[0]
        if(tester.module.getAll_couples().size()<=oldTesterVariablesNumber){
            // child[0] does not contain any temporal operator
            specBdd = originalFeasibleStates.and(c1);
        }else{
            // child[0] contains temporal operator(s)
            specBdd = design.feasible().and(c1);
        }
*/
        tester.cachePutSpec(spec,specBdd);
        return specBdd;
    }

    // return the set of states satisfying spec
    // spec = AA child[0]
    public BDD satAA(Spec spec) throws ModelCheckException, SMVParseException, ModuleException, ModelCheckAlgException, SpecException {
        SMVModule design = (SMVModule) getDesign();
        SpecExp se = (SpecExp) spec;
        Operator op = se.getOperator();
        Spec[] child = se.getChildren();
        BDD c1 = null, specBdd = null;
        if (op != Operator.AA) return null;

        BDD negC1 = null;
        Spec negChild0 = NNF(new SpecExp(Operator.NOT, child[0]));
        negC1 = sat(negChild0);
        // specBdd = feas /\ !(feas /\ !child[0]) = feas /\ (!feas \/ !!child[0]) = feas /\ !!child[0] = feas /\ !negC1
        specBdd = design.feasible().and(negC1.not());
/*
        int oldTesterVariablesNumber = tester.module.getAll_input_variables().size();
        negC1 = sat(negChild0);
        if(tester.module.getAll_couples().size()<=oldTesterVariablesNumber){
            // child[0] does not contain any temporal operator
            // specBdd = feas /\ !(feas /\ !child[0]) = feas /\ (!feas \/ !!child[0]) = feas /\ !!child[0] = feas /\ !negC1
            specBdd = originalFeasibleStates.and(negC1.not());
        }else{
            // child[0] contains temporal operator(s)
            specBdd = design.feasible().and(negC1.not());
        }
*/
        tester.cachePutSpec(spec,specBdd);
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
        if(tester!=null && tester.module!=null) design.decompose(tester.module);
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
        System.out.println("The original property: " + simplifySpecString(property,false));
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
/*
        feasibleStatesComposedTester=design.feasible();
        if(design.getAll_couples().size()<=originalDesignVariablesNumber){
            // the property does not contain any temporal operator, and design is the original one
            // feasibleStatesComposedTester is exactly the feasible states of the original model

        }else {
            // the property contains some temporal operators, and design is with the composed tester
            // feasibleStatesComposedTester is the feasible states of the composition of the original model and the tester of the verified property


        }
*/

        // saving to the previous restriction state
        Vector<BDD> old_ini_restrictions = design.getAllIniRestrictions();
        design.restrictIni(chkBdd);
        BDD feas = design.feasible();// feas = the feasible states of D||T from design.init /\ chkBdd
        BDD Init_unSat = feas.and(design.initial()).and(chkBdd);
        // the initial_condition seems redundant
        if (Init_unSat.isZero()) {
            design.decompose(tester.module);
            design.setAllIniRestrictions(old_ini_restrictions);
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
        int specNum=n.getAttribute("spriteSpecNumber");
        for(int i=1;i<=specNum;i++){
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
                    BDD lc = tester.cacheGetSpecBdd(child[0]); if(lc==null) return false; //SpecBDDMap.get(child[0]);
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
    static boolean specNeedExplainEE(Spec spec){
        if(spec instanceof SpecBDD) return false;
        SpecExp se=(SpecExp)spec;
        Operator op=se.getOperator();
        Spec[] child=se.getChildren();

        // operator op_to_explain need to be explained
        if(op==Operator.EE) return true;

        if(op==Operator.AND || op==Operator.OR) {
            return specNeedExplainEE(child[0]) || specNeedExplainEE(child[1]);
        }else if(op==Operator.NOT || op==Operator.AA) return false;
        else // op is temporal operator
            return false;
    }

    // Premise: spec must be a NNF formula, whose logical connectives are only !, /\ and \/, and ! only preceded assertions
    // Results: return true if spec is composition of temporal operators and other operators, composed by /\ or \/
    //          return false otherwise
    static boolean specNeedExplainTemporalOp(Spec spec){
        if(spec instanceof SpecBDD) return false;
        SpecExp se=(SpecExp)spec;
        Operator op=se.getOperator();
        Spec[] child=se.getChildren();

        // operator op_to_explain need to be explained
        if(op.isTemporalOp()) return true;

        if(op==Operator.AND || op==Operator.OR) {
            return specNeedExplainTemporalOp(child[0]) || specNeedExplainTemporalOp(child[1]);
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

        if(!specNeedExplainEE(spec)){
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
            }else if(op==Operator.OR){
                BDD lc=tester.cacheGetSpecBdd(child[0]); if(lc==null) return false;
                if (!state.and(lc).isZero()) witness(child[0],n);
                else witness(child[1],n);
            }else if(op==Operator.EE){ // spec=Ef will be explained by clicking on the node n
                graph.nodeAddSpec(n.getId(),spec);
                //witnessE(child[0], n);
            }
            return true;
        }
        return true;
    }

/*
    // Premises: spec is a NNF STATE formula; n |= spec
    // Results: generate witnesses for state formula
    public boolean witness(
            Spec spec,      // the state formula spec. under checked
            Node n          // node n contains the state satisfying spec
    ) throws ModelCheckAlgException, ModelCheckException, SpecException, SMVParseException, ModuleException {
        if(n==null) return false;
        BDD state = n.getAttribute("BDD");
        int pathNo = n.getAttribute("pathNo");
        int stateNo = n.getAttribute("stateNo");

        if(!specNeedExplainEE(spec)){
            // spec is a state formula composed by !, /\, \/ and AA, note that ! only restrict assertions
            BDD bdd=spec.toBDD();
            if(!bdd.isOne()) graph.nodeAddAnnotation(n.getId(),simplifySpecString(spec.toString(),false));
        }else{ // specNeedExplainEE(spec)=true
            // spec is a state formula composed by !, /\, \/, AA, EE
            SpecExp se = (SpecExp) spec;
            Operator op = se.getOperator();
            Spec[] child = se.getChildren();

            if(op==Operator.AND){
                boolean b1=witness(child[0],n);
                boolean b2=witness(child[1],n);
                return b1&&b2;
            }else if(op==Operator.OR){
                BDD c0 = SpecBDDMap.get(child[0]);
                if (!state.and(c0).isZero()) return witness(child[0],n);
                else return witness(child[1],n);
            }else if(op==Operator.EE){ // spec=Ef will be explained by clicking on the node n
                graph.nodeAddAnnotation(n.getId(),simplifySpecString(spec.toString(),false));
                graph.nodeAddSpec(n.getId(),child[0]);
                //return witnessE(child[0], n);
                return true;
            }
            return true;
        }
        return true;
    }
*/

    // Premises: !specNeedExplainEE(spec) && specNeedExplainTemporalOp(spec); n|=E spec
    // Results: return true if spec need to be explained over a new lasso path
    // 			return false if it is enough to explain spec only over node n
    boolean needCreatePath(Spec spec, Node n){
        if(spec.isStateSpec()) return false;
        //now spec is NOT a state formula

        BDD state=graph.nodeGetBDD(n.getId());

        SpecExp se=(SpecExp)spec;
        Operator op=se.getOperator();
        Spec[] child=se.getChildren();

        if(op==Operator.AND)
            return needCreatePath(child[0],n) || needCreatePath(child[1],n);
        else if(op==Operator.OR){
            BDD lc=tester.cacheGetSpecBdd(child[0]); if(lc==null) return false;  //SpecBDDMap.get(child[0]);
            BDD rc=tester.cacheGetSpecBdd(child[1]); if(rc==null) return false;  //SpecBDDMap.get(child[1]);
            if(child[0].isPropSpec() && !state.and(lc).isZero()) // f is prop formula && n|=f
                return false;
		    else if(child[1].isPropSpec() && !state.and(rc).isZero()) // g is prop formula && n|=g
		        return false;
            if(!state.and(lc).isZero()) //n|=f
                return needCreatePath(child[0],n);
            else // n|=g
                return needCreatePath(child[1],n);
        }else if(op.isTemporalOp()) // spec is a principally temporal formula
            return true;
        else // op==!, EE or AA
            return false;
    }

    // Premises: !specNeedExplainEE(spec) && specNeedExplainTemporalOp(spec); n|=E spec; !needCreatePath(spec,n)
    // Results: explain spec only over node n
    boolean explainOnNode(Spec spec, Node n) throws ModelCheckException, SpecException, ModelCheckAlgException, SMVParseException, ModuleException {
        if(spec.isStateSpec())
            //return witness(spec,n);
            graph.nodeAddSpec(n.getId(),spec);
        //now spec is NOT a state formula

        BDD state=graph.nodeGetBDD(n.getId());

        SpecExp se=(SpecExp)spec;
        Operator op=se.getOperator();
        Spec[] child=se.getChildren();

        if(op==Operator.AND)
            return explainOnNode(child[0],n) || explainOnNode(child[1],n);
        else if(op==Operator.OR){
            BDD lc=tester.cacheGetSpecBdd(child[0]); if(lc==null) return false;  //SpecBDDMap.get(child[0]);
            BDD rc=tester.cacheGetSpecBdd(child[1]); if(rc==null) return false;  //SpecBDDMap.get(child[1]);
            if(child[0].isPropSpec() && !state.and(lc).isZero()) // f is prop formula && n|=f
                //return witness(child[0],n);
                graph.nodeAddSpec(n.getId(),child[0]);
            else if(child[1].isPropSpec() && !state.and(rc).isZero()) // g is prop formula && n|=g
                //return witness(child[1],n);
                graph.nodeAddSpec(n.getId(),child[1]);
            if(!state.and(lc).isZero()) //n|=f
                return explainOnNode(child[0],n);
            else // n|=g
                return explainOnNode(child[1],n);
        }else if(op.isTemporalOp()) // spec is a principally temporal formula
            return false;
        else // op==!, EE or AA
            return true;
    }

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

        if(!specNeedExplainEE(spec) && !specNeedExplainTemporalOp(spec)){
            // spec is a state formula composed by !,/\,\/,AA
            //witness(spec, n);
            return graph.nodeAddSpec(n.getId(),spec);
        }else if(specNeedExplainEE(spec)){
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
        }else{
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


/*
    // Premises: n |= E spec; spec is a NNF formula
    // Results: if spec has to be explained over a path, then generate a new feasible lasso path pi from n, explain spec over pi;
    //          otherwise, explain spec only on node n
    public boolean witnessE(Spec spec,
                            Node n) throws ModelCheckException, SpecException, ModelCheckAlgException, SMVParseException, ModuleException {
        if(n==null) return false;
        BDD fromState = n.getAttribute("BDD");
        int pathNo = n.getAttribute("pathNo");
        int stateNo = n.getAttribute("stateNo");
        String fromNodeId = pathNo+"."+stateNo;
        if (fromState == null || fromState.isZero()) return false;

        if(!specNeedExplainEE(spec) && !specNeedExplainTemporalOp(spec)){
            // spec is a state formula composed by !,/\,\/,AA
            witness(spec, n);
        }else if(specNeedExplainEE(spec)){
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
                BDD pBdd = SpecBDDMap.get(p);
                if (!fromState.and(pBdd).isZero())
                    return witnessE(p,n);
                else return witnessE(q,n);
            }else if(op==Operator.EE){
                return witnessE(child[0],n);
            }else return false;
        }else{
            // now !specNeedExplainEE(spec) && specNeedExplainTemporalOp(spec)
            // spec is composed by !, \/, /\, AA, temporal operators
            if(!needCreatePath(spec,n)){
                return explainOnNode(spec,n);
            }else{
                // create a feasible lasso path pi from n such that pi|=spec; and then explainPath(spec, pi, 0);
                // the tester of spec is not empty, and spec has NOT EE and AA operators
                SMVModule DT = (SMVModule) getDesign(); // DT is the parallel composition of design and the tester of spec
                BDD temp, fulfill;
                int idx_addedIniRestrict=DT.restrictIni(fromState); // restrict the set of initial states to be fromState
                BDD feasStates = DT.feasible(); // feasStates is the set of feasible states from fromState
                DT.removeIniRestriction(idx_addedIniRestrict);

                // saving to the previous restriction state
                Vector<BDD> old_trans_restrictions = DT.getAllTransRestrictions();
                //Lines 1-2 are handled by the caller. ("verify")
                // Line 3
                DT.restrictTrans(feasStates.and(Env.prime(feasStates)));
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
                            vector_period_idx.add((Integer) period.size()-1);
                            vector_fairness.add("Justice:"+simplifySpecString(weakDes.justiceAt(i).toString(),false));
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
                                    vector_period_idx.add((Integer)j);
                                    vector_fairness.add("Compassion.q:"+simplifySpecString(strongDes.qCompassionAt(i).toString(),false));

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
                                vector_period_idx.add((Integer)period.size()-1);
                                vector_fairness.add("Compassion.q:"+simplifySpecString(strongDes.qCompassionAt(i).toString(),false));
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
                    graph.addNode(createdPathNumber, i, prefix.get(i), "");
                    cur_nid = createdPathNumber + "." + i;

                    trunkNodePath.add(cur_nid);

                    String edgeId=pred_nid + "->" + cur_nid;
                    e = graph.addArc(edgeId, pred_nid, cur_nid, true);
                    if(first_created_edgeId==null) {
                        first_created_edgeId=edgeId;
                        graph.edgeAddAnnotation(edgeId,
                                "Path" + createdPathNumber + "|=" + simplifySpecString(spec.toString(), false));
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

                trunkNodePaths.add(new NodePath(trunkNodePath, loopNodeIdx));
                int pathIndex = trunkNodePaths.size()-1;

//                trunkNodePaths.add(trunkNodePath);
//                loopNodeIndexes.add((Integer)loopNodeIdx);
//                int pathIndex = loopNodeIndexes.size()-1;

                return explainPath(spec, pathIndex, 0);
            }
        }
        return true;
    }
*/


    // Premises: path^pos |= spec
    // Results: attached necessary satisfied formulas at some nodes over the suffix path^startPos
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
                boolean b1=explainPath(child[0],path,pos);
                return b1 && explainPath(child[1],path,pos);
            }else if(op==Operator.OR){ //spec=f OR g
                boolean fNeedExplain = specNeedExplainEE(child[0]) || specNeedExplainTemporalOp(child[0]);
                Spec p,q;
                if(!fNeedExplain) {p=child[0]; q=child[1];} else {p=child[1]; q=child[0];}
                BDD pBdd = tester.cacheGetSpecBdd(p); if(pBdd==null) return false; // SpecBDDMap.get(p);
                if (!startState.and(pBdd).isZero())
                    return explainPath(p,path,pos);
                else return explainPath(q,path,pos);
            }else{
                // spec is a principally temporal formula spec=Xf, fUg, fRg, fU a..b g, or f R a..b g
                // explain spec according to its semantics over path^pos;

                if(op==Operator.NEXT){ //spec=X f
                    BDD X=tester.cacheGetSpecBdd(spec); if(X==null) return false; // SpecBDDMap.get(spec);
                    if(X==null || startState.and(X).isZero()) return false;
                    int ni=path.at(pos+1);
                    BDD nextState=graph.nodeGetBDD(path.get(ni));
                    BDD Xf=tester.cacheGetSpecBdd(child[0]); if(Xf==null) return false; //SpecBDDMap.get(child[0]);
                    if(Xf==null || nextState.and(Xf).isZero()) return false;

                    String firstEdgeId=snid+"->"+path.get(ni);
                    graph.edgeAddSpec(firstEdgeId,spec,path,pos,true);

                    String eid = path.get(path.at(pos + 1)) + "->" + path.get(path.at(pos + 2));
                    if(child[0].isStateSpec()) graph.nodeAddSpec(path.get(ni),child[0]);
                    else graph.edgeAddSpec(eid, child[0], path, pos + 1, false);
                    //return explainPath(child[0],path,pos+1);
                }else if(op==Operator.UNTIL){
                    BDD X=tester.cacheGetSpecBdd(spec); if(X==null) return false; //SpecBDDMap.get(spec);
                    BDD Xf=tester.cacheGetSpecBdd(child[0]); if(Xf==null) return false; //SpecBDDMap.get(child[0]);
                    BDD Xg=tester.cacheGetSpecBdd(child[1]); if(Xg==null) return false; //SpecBDDMap.get(child[1]);
                    if(X==null) return false;
                    if(Xf==null) return false;
                    if(Xg==null) return false;
                    if(startState.and(X).isZero()) return false;

                    String firstEdgeId=snid+"->"+path.get(path.at(pos+1));
                    graph.edgeAddSpec(firstEdgeId,spec,path,pos,true);

                    boolean exp=true;  // exp=false then stop explaining f
                    for(int p=pos;;p++){
                        int i=path.at(p);
                        String nid=path.get(i);
                        BDD nState=graph.nodeGetBDD(nid); if(nState==null) return false;
                        String eid = path.get(path.at(p))+"->"+path.get(path.at(p+1));
                        if(!nState.and(Xg).isZero()) { // |=g
                            if(child[1].isStateSpec()) graph.nodeAddSpec(path.get(path.at(p)),child[1]);
                            else graph.edgeAddSpec(eid,child[1],path,p,false);
                            return true;
                            //return explainPath(child[1],path,p);
                        }else if(exp && !nState.and(Xf).isZero()){ // |=f
                            if(child[0].isStateSpec()) graph.nodeAddSpec(path.get(path.at(p)),child[0]);
                            else graph.edgeAddSpec(eid,child[0],path,p,false);
                            //explainPath(child[0],path,p);
                            exp=path.needExplainNextPosition(pos,p);
                        }
                    }
                }else if(op==Operator.RELEASES){
                    BDD X=tester.cacheGetSpecBdd(spec); if(X==null) return false; //SpecBDDMap.get(spec);
                    BDD Xf=tester.cacheGetSpecBdd(child[0]); if(Xf==null) return false; //SpecBDDMap.get(child[0]);
                    BDD Xg=tester.cacheGetSpecBdd(child[1]); if(Xg==null) return false; //SpecBDDMap.get(child[1]);
                    if(X==null) return false;
                    if(Xf==null) return false;
                    if(Xg==null) return false;
                    if(startState.and(X).isZero()) return false;

                    String firstEdgeId=snid+"->"+path.get(path.at(pos+1));
                    graph.edgeAddSpec(firstEdgeId,spec,path,pos,true);

                    boolean exp=true;  // exp=false then stop explaining f
                    for(int p=pos;;p++){
                        int i=path.at(p);
                        String nid=path.get(i);
                        BDD nState=graph.nodeGetBDD(nid); if(nState==null) return false;
                        String eid = path.get(path.at(p))+"->"+path.get(path.at(p+1));
                        if(!nState.and(Xf.and(Xg)).isZero()) {  // |=f/\g
                            if(child[0].isStateSpec()) graph.nodeAddSpec(path.get(path.at(p)),child[0]);
                            else graph.edgeAddSpec(eid,child[0],path,p,false);
                            if(child[1].isStateSpec()) graph.nodeAddSpec(path.get(path.at(p)),child[1]);
                            else graph.edgeAddSpec(eid,child[1],path,p,false);
                            return true;
                            //boolean b1=explainPath(child[0],path,p); // explain f
                            //return b1 && explainPath(child[1],path,p); // explain g
                        }else if(exp && !nState.and(Xg).isZero()){  // |=g
                            if(child[1].isStateSpec()) graph.nodeAddSpec(path.get(path.at(p)),child[1]);
                            else graph.edgeAddSpec(eid,child[1],path,p,false);
                            // explainPath(child[1],path,p); // explain g
                            exp=path.needExplainNextPosition(pos,p);
                        }
                        if(!exp) return true;
                    }

                }else if(op==Operator.B_UNTIL){

                }else if(op==Operator.B_RELEASES){

                }else
                    return true;
            }
        }
        return true;
    }

/*
    // Premises: path^pos |= spec
    // Results: attached necessary satisfied formulas at some nodes over the suffix path^startPos
    public boolean explainPath(Spec spec,            // the spec to be explained
                               int pathIndex,        // the path is trunkNodePaths.get(pathIndex)
                               int pos              // spec is explained over path^pos, the suffix of the path starting from the logical position pos
    ) throws ModelCheckException, SpecException, ModelCheckAlgException, SMVParseException, ModuleException {
        NodePath path = trunkNodePaths.get(pathIndex); if(path==null) return false;

        int startIdx=path.at(pos);
        String snid=path.get(startIdx);
        Node sn=graph.getNode(snid);
        BDD startState=graph.nodeGetBDD(snid);

        SMVModule tester=SpecTesterMap.get(spec); // the tester for spec
        boolean expEE=specNeedExplainEE(spec);
        boolean expTempOp=specNeedExplainTemporalOp(spec);

        if(spec.isStateSpec()){
            // spec is a state formula
            return witness(spec,sn);
        }else{
            // spec is NOT a state formula
            SpecExp se=(SpecExp)spec;
            Operator op=se.getOperator();
            Spec[] child=se.getChildren();

            if(op==Operator.AND){
                boolean b1=explainPath(child[0],pathIndex,pos);
                return b1 && explainPath(child[1],pathIndex,pos);
            }else if(op==Operator.OR){ //spec=f OR g
                boolean fNeedExplain = specNeedExplainEE(child[0]) || specNeedExplainTemporalOp(child[0]);
                Spec p,q;
                if(!fNeedExplain) {p=child[0]; q=child[1];} else {p=child[1]; q=child[0];}
                BDD pBdd = SpecBDDMap.get(p);
                if (!startState.and(pBdd).isZero())
                    return explainPath(p,pathIndex,pos);
                else return explainPath(q,pathIndex,pos);
            }else{
                // spec is a principally temporal formula spec=Xf, fUg, fRg, fU a..b g, or f R a..b g
                // explain spec according to its semantics over path^pos;

                if(op==Operator.NEXT){ //spec=X f
                    BDD X=SpecBDDMap.get(spec);
                    if(X==null || startState.and(X).isZero()) return false;
                    int i=path.at(pos+1);
                    BDD nextState=graph.nodeGetBDD(path.get(i));
                    BDD Xf=SpecBDDMap.get(child[0]);
                    if(Xf==null || nextState.and(Xf).isZero()) return false;

                    graph.edgeAddAnnotation(snid+"->"+path.get(i), simplifySpecString(spec.toString(),false));
                    return explainPath(child[0],pathIndex,pos+1);

                }else if(op==Operator.UNTIL){
                    BDD X=SpecBDDMap.get(spec);
                    BDD Xf=SpecBDDMap.get(child[0]);
                    BDD Xg=SpecBDDMap.get(child[1]);
                    if(X==null) return false;
                    if(Xf==null) return false;
                    if(Xg==null) return false;
                    if(startState.and(X).isZero()) return false;

                    boolean exp=true;  // exp=false then stop explaining f
                    for(int p=pos;;p++){
                        int i=path.at(p);
                        String nid=path.get(i);
                        BDD nState=graph.nodeGetBDD(nid); if(nState==null) return false;
                        if(!nState.and(Xg).isZero()) { // |=g
                            return explainPath(child[1],pathIndex,p);
                        }else if(exp && !nState.and(Xf).isZero()){ // |=f
                            explainPath(child[0],pathIndex,p);
                            exp=path.needExplainNextPosition(pos,p);
                        }
                    }
                }else if(op==Operator.RELEASES){
                    BDD X=SpecBDDMap.get(spec);
                    BDD Xf=SpecBDDMap.get(child[0]);
                    BDD Xg=SpecBDDMap.get(child[1]);
                    if(X==null) return false;
                    if(Xf==null) return false;
                    if(Xg==null) return false;
                    if(startState.and(X).isZero()) return false;

                    boolean exp=true;  // exp=false then stop explaining f
                    for(int p=pos;;p++){
                        int i=path.at(p);
                        String nid=path.get(i);
                        BDD nState=graph.nodeGetBDD(nid); if(nState==null) return false;
                        if(!nState.and(Xf.and(Xg)).isZero()) {  // |=f/\g
                            boolean b1=explainPath(child[0],pathIndex,p); // explain f
                            return b1 && explainPath(child[1],pathIndex,p); // explain g
                        }else if(exp && !nState.and(Xg).isZero()){  // |=g
                            explainPath(child[1],pathIndex,p); // explain g
                            exp=path.needExplainNextPosition(pos,p);
                        }
                    }

                }else if(op==Operator.B_UNTIL){

                }else if(op==Operator.B_RELEASES){

                }else
                    return true;
            }
        }
        return true;
    }
*/


    @Override
    public AlgResultI postAlgorithm() throws AlgExceptionI {
        //getDesign().removeAllTransRestrictions();
        return null;
    }

}
