package edu.wis.jtlv.lib.mc.ATLstar;

import edu.wis.jtlv.env.Env;
import edu.wis.jtlv.env.core.smv.SMVParseException;
import edu.wis.jtlv.env.module.Module;
import edu.wis.jtlv.env.module.ModuleBDDField;
import edu.wis.jtlv.env.module.ModuleException;
import edu.wis.jtlv.env.module.SMVModule;
import edu.wis.jtlv.env.spec.*;
import edu.wis.jtlv.lib.AlgExceptionI;
import edu.wis.jtlv.lib.AlgResultI;
import edu.wis.jtlv.lib.mc.ModelCheckAlgException;
import edu.wis.jtlv.lib.mc.ModelCheckAlgI;
import edu.wis.jtlv.old_lib.mc.ModelCheckException;
import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDVarSet;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class ATLStarModelCheckAlg extends ModelCheckAlgI {
    private static final Logger log = LogManager.getLogger(ATLStarModelCheckAlg.class);
    private Spec property;  //待验证的规约
    private BDDVarSet visibleVars; //可观察变量

    private static HashMap<Spec, BDD> specBDDMap = new HashMap<>();
    private static HashMap<Spec, SMVModule> specTesterMap = new HashMap<>();

    private int testerID = 0;

    private int fieldId = 0;

    //无参构造
    public ATLStarModelCheckAlg() {
    }

    //有参构造
    public ATLStarModelCheckAlg(Module design, Spec property) {
        super(design);
        this.property = property;
        this.visibleVars = design.moduleUnprimeVars();
    }

    //Getter和Setter方法
    public Spec getProperty() {
        return property;
    }

    public void setProperty(Spec property) {
        this.property = property;
    }

    /**
     *
     * @param spec ATLStar 规约
     * @param tester the tester that holds the sub-testers of spec
     * @return
     * @throws ModuleException
     * @throws SMVParseException
     * @throws ModelCheckException
     * @throws ModelCheckAlgException
     * @throws SpecException
     */
    public BDD sat(Spec spec, SMVModule tester)
    throws ModuleException, SMVParseException, ModelCheckException, ModelCheckAlgException, SpecException {
        log.info("ATLStarModelCheckAlg类的sat方法的spec值:{}",spec);
        if(spec instanceof SpecBDD) {
            return ((SpecBDD) spec).getVal();
        }
        if(spec instanceof SpecRange || spec instanceof SpecAgentIdentifier) return null;

        SpecExp specExp = (SpecExp) spec;
        Operator op = specExp.getOperator();
        Spec[] children = specExp.getChildren();
        BDD c1, c2;
        ModuleBDDField x;
        BDD xBDD; //tester的输出变量

        //*********************逻辑连接词*********************
        if(op == Operator.NOT) {
            c1 = sat(children[0], tester);
            specBDDMap.put(children[0], c1.not());
            specTesterMap.put(children[0], tester); //t:=(\emptyset,T,T,/emptyset)空测试器
            return c1;
        }
        if(op == Operator.AND) {
            c1 = sat(children[0], tester);
            c2 = sat(children[1], tester);
            BDD c3 = c1.and(c2);
            specBDDMap.put(children[0], c1);
            specBDDMap.put(children[1], c2);
            specTesterMap.put(children[0], tester);
            specTesterMap.put(children[1], tester); //t:=tester1 || tester2
            return c3;
        }
        if(op == Operator.OR) {
            c1 = sat(children[0], tester);
            c2 = sat(children[1], tester);
            BDD c3 = c1.or(c2);
            specBDDMap.put(children[0], c1);
            specBDDMap.put(children[1], c2);
            specTesterMap.put(children[0], tester);
            specTesterMap.put(children[1], tester); //t:=tester1 || tester2
            return c3;
        }

        //*********************路径量词*********************
        if(op == Operator.CAN_ENFORCE) return satCanEnforce(spec);
        if(op == Operator.CANNOT_AVOID) return satCantAvoid(spec);

        //*********************时态算子*********************
        if(op == Operator.NEXT) {
            x = tester.addVar("X" + (++fieldId));
            xBDD = x.getDomain().ithVar(1);
            c1 = sat(children[0], tester);
            specBDDMap.put(children[0], c1);
            specTesterMap.put(children[0], tester);
            BDD primeC1 = Env.prime(c1);
            tester.conjunctTrans(xBDD.imp(primeC1));
            return xBDD;
        }
        if(op == Operator.UNTIL) {
            x = tester.addVar("X" + (++fieldId));
            xBDD = x.getDomain().ithVar(1);
            c1 = sat(children[0], tester);
            c2 = sat(children[1], tester);
            specBDDMap.put(children[0], c1);
            specBDDMap.put(children[1], c2);
            specTesterMap.put(children[0], tester);
            specTesterMap.put(children[1], tester);

            BDD primeX = Env.prime(xBDD);
            tester.conjunctTrans(xBDD.imp(c2.or(c1.and(primeX))));
            tester.addJustice(xBDD.imp(c2));

            return xBDD;
        }
        if(op == Operator.RELEASES) {
            x = tester.addVar("X" + (++fieldId));
            xBDD = x.getDomain().ithVar(1);
            c1 = sat(children[0], tester);
            c2 = sat(children[1], tester);
            specBDDMap.put(children[0], c1);
            specBDDMap.put(children[1], c2);
            specTesterMap.put(children[0], tester);
            specTesterMap.put(children[1], tester);

            BDD primeX = Env.prime(xBDD);
            tester.conjunctTrans(xBDD.imp(c2.and(c1.or(primeX))));

            return xBDD;
        }
        //otherwise
        throw new ModelCheckException("Cannot handle the specification " + spec + ".");
    }

    public BDD satCanEnforce(Spec spec) throws ModelCheckException, SMVParseException,
            ModuleException, ModelCheckAlgException, SpecException {
        SMVModule design = (SMVModule) this.getDesign();
        SpecExp specExp = (SpecExp) spec;
        Operator op = specExp.getOperator();
        Spec[] children = specExp.getChildren();
        BDD negC1, specBDD = null;

        assert (op == Operator.CAN_ENFORCE);

        //获得智能体列表
        Vector<String> agentList = new Vector<>();
        for (int i = 0; i < children.length - 1; i++) {
            SpecAgentIdentifier agentId = (SpecAgentIdentifier) children[i];
            agentList.add(agentId.getAgentName());
        }

        SMVModule negC1Tester = new SMVModule("Tester" + (++testerID));
        negC1 = sat(NNF(new SpecExp(Operator.NOT, children[children.length-1])), negC1Tester);
        specBDDMap.put(children[children.length-1], negC1.not());
        if(!testerIsEmpty(negC1Tester)) {
            design.syncComposition(negC1Tester);
            BDD feasibleNegC1 = design.ATL_canEnforce_feasible(agentList);
            BDDVarSet auxVars = testerGetAuxVars(negC1Tester);
            specBDD = feasibleNegC1.and(negC1).exist(auxVars).not();
        } else {
            //specBDD = !(feasible states & !c1)
            specBDD = negC1.and(design.ATL_canEnforce_feasible(agentList)).not();
        }
        return specBDD;
    }

    public BDD satCantAvoid(Spec spec) throws ModelCheckException, SMVParseException,
            ModuleException, ModelCheckAlgException, SpecException {
        SMVModule design = (SMVModule) this.getDesign();
        SpecExp specExp = (SpecExp) spec;
        Operator op = specExp.getOperator();
        Spec[] children = specExp.getChildren();
        BDD c1, specBDD = null;

        assert (op == Operator.CANNOT_AVOID);

        //获得智能体列表
        Vector<String> agentList = new Vector<>();
        for (int i = 0; i < children.length - 1; i++) {
            SpecAgentIdentifier agentId = (SpecAgentIdentifier) children[i];
            agentList.add(agentId.getAgentName());
        }

        SMVModule c1Tester = new SMVModule("Tester" + (++testerID));
        c1 = sat(children[children.length-1], c1Tester);
        specBDDMap.put(children[children.length-1], c1);
        if(!testerIsEmpty(c1Tester)) {
            design.syncComposition(c1Tester);
            BDD feasibleC1 = design.ATL_cannotAvoid_feasible(agentList);
            BDDVarSet auxVars = testerGetAuxVars(c1Tester);
            specBDD = feasibleC1.and(c1).exist(auxVars);
        } else {
            //specBDD = (feasible states & c1)
            specBDD = c1.and(design.ATL_cannotAvoid_feasible(agentList));
        }
        return specBDD;
    }

    public static Spec NNF(Spec spec) throws ModelCheckException, SpecException {
        log.info("ATLStarModelCheckAlg类的NNF方法的spec值:{}",spec);
        if(!(spec instanceof SpecExp)) return spec;

        SpecExp propExp = (SpecExp) spec;
        Operator op = propExp.getOperator();
        Spec[] children = propExp.getChildren();

        //除“非”以外的一元算子
        if(op==Operator.NEXT
                || op==Operator.CAN_ENFORCE
                || op==Operator.CANNOT_AVOID) {
            return new SpecExp(op, NNF(children[0]));
        }

        //二元算子
        if(op == Operator.IMPLIES) {
            return new SpecExp(Operator.OR, NNF(new SpecExp(Operator.NOT, children[0])), NNF(children[1]));
        }
        if(op==Operator.AND
                || op==Operator.OR
                || op==Operator.UNTIL
                || op==Operator.RELEASES) {
            return new SpecExp(op, NNF(children[0]), NNF(children[1]));
        }

        //F c1 = true UTIL c1
        if(op == Operator.FINALLY) {
            return new SpecExp(Operator.UNTIL, getTrueSpec(), NNF(children[0]));
        }

        //(G c1) >> (! FINALLY !c1) >> !(true UTIL !c1) >> (false RELEASE c1)
        if (op == Operator.GLOBALLY) {
            return new SpecExp(Operator.RELEASES, getFalseSpec(), NNF(children[0]));
        }

        //NOT
        if(op == Operator.NOT) {
            Spec f = children[0];
            if(!(f instanceof SpecExp))
                return spec;

            SpecExp specExp = (SpecExp) f;
            Operator fOp = specExp.getOperator();
            Spec[] fChildren = specExp.getChildren();
            log.info("ATLStarModelCheckAlg类的NNF方法，fOp:{},fChildren[0]:{},fChildren[1]:{},fChildren[2]:{}",
                    fOp, fChildren[0], fChildren[1], fChildren[2]);

            //NNF(!!c1) >> NNF(c1)
            if(fOp == Operator.NOT) {
                return NNF(fChildren[0]);
            }

            //!(c1 AND c2) >> !c1 OR !c2
            if(fOp == Operator.AND) {
                return new SpecExp(Operator.OR, NNF(new SpecExp(Operator.NOT, fChildren[0])),
                        NNF(new SpecExp(Operator.NOT, fChildren[1])));
            }
            //!(c1 OR c2) >> !c1 AND !c2
            if(fOp == Operator.OR) {
                return new SpecExp(Operator.AND, NNF(new SpecExp(Operator.NOT, fChildren[0])),
                        NNF(new SpecExp(Operator.NOT, fChildren[1])));
            }

            //!(c1 IMPLIES c2) >> c1 AND !c2
            if(fOp == Operator.IMPLIES) {
                return new SpecExp(Operator.AND, NNF(fChildren[0]),
                        NNF(new SpecExp(Operator.NOT, fChildren[1])));
            }

            //!(CAN_ENFORCE c1) >> CANNOT_AVOID !c1
            if(fOp == Operator.CAN_ENFORCE) {
                fChildren[fChildren.length-1] = new SpecExp(Operator.NOT, fChildren[fChildren.length-1]);
                return new SpecExp(Operator.CANNOT_AVOID, fChildren);
            }
            //!(CANNOT_AVOID) c1 >> CAN_ENFORCE !c1
            if(fOp == Operator.CANNOT_AVOID) {
                fChildren[fChildren.length-1] = new SpecExp(Operator.NOT, fChildren[fChildren.length-1]);
                return new SpecExp(Operator.CAN_ENFORCE, fChildren);
            }

            //! X c1 >> X !c1
            if(fOp == Operator.NEXT) {
                return new SpecExp(fOp, NNF(new SpecExp(Operator.NOT, fChildren[0])));
            }

            //!(F c1) >> !(true UTIL c1) >> false RELEASE ! c1
            if(fOp == Operator.FINALLY) {
                Spec negC1 = NNF(new SpecExp(Operator.NOT, fChildren[0]));
                return new SpecExp(Operator.RELEASES, getFalseSpec(), negC1);
            }

            //!(G c1) >> true UTIL !c1
            if(fOp == Operator.GLOBALLY) {
                Spec negC1 = NNF(new SpecExp(Operator.NOT, fChildren[0]));
                return new SpecExp(Operator.UNTIL, getTrueSpec(), negC1);
            }

            //!(c1 UTIL c2) >> !c1 RELEASE !c2
            if(fOp == Operator.UNTIL) {
                return new SpecExp(Operator.RELEASES,
                        NNF(new SpecExp(Operator.NOT, fChildren[0])), NNF(new SpecExp(Operator.NOT, fChildren[1])));
            }
            //!(c1 RELEASE c2) >> !c1 UTIL c2
            if(fOp == Operator.RELEASES) {
                return new SpecExp(Operator.UNTIL,
                        NNF(new SpecExp(Operator.NOT, fChildren[0])), NNF(new SpecExp(Operator.NOT, fChildren[1])));
            }
        }
        throw new ModelCheckException("Failed to construct the Negation Normal Form of " + spec);
    }

    //tester判空方法
    private boolean testerIsEmpty(SMVModule tester) {
        return tester == null || tester.getAll_couples().size() == 0;
    }

    private BDDVarSet testerGetAuxVars(SMVModule tester) {
        BDDVarSet varSet = Env.getEmptySet();
        for(ModuleBDDField var : tester.getAll_couples()) {
            varSet = varSet.id().union(var.support());
        }
        return varSet;
    }

    /**
     *
     * @return TRUE 规约
     */
    private static Spec getTrueSpec() {
        Spec[] specs = Env.loadSpecString("LTLSPEC TRUE ;");

        assert (specs != null) && (specs.length > 0);

        return specs[0];
    }

    /**
     *
     * @return TRUE 规约
     */
    private static Spec getFalseSpec() {
        Spec[] specs = Env.loadSpecString("LTLSPEC FALSE ;");

        assert (specs != null) && (specs.length > 0);

        return specs[0];
    }

    @Override
    public AlgResultI preAlgorithm() throws AlgExceptionI, SMVParseException, ModelCheckException, ModuleException {
        SMVModule design = (SMVModule) getDesign();
        design.removeAllIniRestrictions();
        specBDDMap.clear();
        for(Map.Entry<Spec, SMVModule> entry : specTesterMap.entrySet()) {
            ModuleBDDField[] testerVars = entry.getValue().getAllFields();
            for(ModuleBDDField var : testerVars) {

            }
        }
        return null;
    }

    @Override
    public AlgResultI doAlgorithm() throws AlgExceptionI, ModelCheckException, ModuleException, SMVParseException, SpecException {
        log.info("model checking ATL*K property: {}", this.property);
        if(!this.property.isStateSpec()) {

        }
        return null;
    }

    @Override
    public AlgResultI postAlgorithm() throws AlgExceptionI {
        return null;
    }
}
