package edu.wis.jtlv.lib.mc.ATLstar;

import edu.wis.jtlv.env.Env;
import edu.wis.jtlv.env.core.smv.SMVParseException;
import edu.wis.jtlv.env.core.smv.eval.*;
import edu.wis.jtlv.env.core.smv.schema.SMVAgentInfo;
import edu.wis.jtlv.env.module.*;
import edu.wis.jtlv.env.spec.*;
import edu.wis.jtlv.lib.AlgExceptionI;
import edu.wis.jtlv.lib.AlgResultI;
import edu.wis.jtlv.lib.AlgResultString;
import edu.wis.jtlv.lib.mc.ModelCheckAlgException;
import edu.wis.jtlv.lib.mc.ModelCheckAlgI;
import edu.wis.jtlv.old_lib.mc.ModelCheckException;
import net.sf.javabdd.BDD;
import net.sf.javabdd.BDDVarSet;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.ui.spriteManager.Sprite;

import java.util.*;

/**
 * ATL*模型检测算法
 */
public class ATLStarModelCheckAlg extends ModelCheckAlgI {
    private Spec property;  //待验证的规约
    private BDDVarSet visibleVars; //可观察变量
    private BDDVarSet stateVarSet; //初始模型的状态变量集合

    private static HashMap<Spec, BDD> specBDDMap = new HashMap<>();
    private static HashMap<Spec, SMVModule> specTesterMap = new HashMap<>();

    private int testerID = 0;

    private int fieldId = 0;

    private Spec checkProp; //实际验证的规约
    private BDD checkBDD; //检测checkProp获得的BDD

    private GraphExplainATLStar graph; //用于展示证据图
    private int createdPathNumber = 0; //当前创建路径的数量
    private final Vector<NodePath> trunkNodePaths = new Vector<>();
    private BDD feasibleStatesForWitnessE = null;

    //无参构造
    public ATLStarModelCheckAlg() {
    }

    //有参构造
    public ATLStarModelCheckAlg(Module design, Spec property) {
        super(design);
        this.property = property;
        this.visibleVars = design.moduleUnprimeVars();
        this.stateVarSet = design.moduleUnprimeVars();
    }

    //Getter和Setter方法
    public Spec getProperty() {
        return property;
    }

    public void setProperty(Spec property) {
        this.property = property;
    }

    public GraphExplainATLStar getGraph() {
        return graph;
    }

    public void setGraph(GraphExplainATLStar graph) {
        this.graph = graph;
    }

    /**
     * 求解可满足状态
     * @param spec ATLStar 规约
     * @param tester the tester that holds the sub-testers of spec
     * @return BDD
     * @throws ModuleException Module异常
     * @throws SMVParseException SMV解析异常
     * @throws ModelCheckException 模型检测异常
     * @throws ModelCheckAlgException 模型检测算法异常
     * @throws SpecException 规约异常
     */
    public BDD sat(Spec spec, SMVModule tester)
    throws ModuleException, SMVParseException, ModelCheckException, ModelCheckAlgException, SpecException {
        if(spec instanceof SpecBDD) {
            return ((SpecBDD) spec).getVal();
        }
        if(spec instanceof SpecRange || spec instanceof SpecAgentIdentifier) return null;

        SpecExp specExp = (SpecExp) spec;
        Operator op = specExp.getOperator();
        Spec[] children = specExp.getChildren();
        BDD c1, c2 = null;
        ModuleBDDField x = null;
        BDD xBDD; //tester的输出变量

        //*********************逻辑连接词*********************
        if(op == Operator.NOT) {
            c1 = sat(children[0], tester);
            specBDDMap.put(children[0], c1);
            specTesterMap.put(children[0], tester); //t:=(\emptyset,T,T,\emptyset)空测试器
            return c1.not();
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
        //*************************************************

        //*********************策略量词*********************
        if(op == Operator.CAN_ENFORCE) return satCanEnforce(spec);
        if(op == Operator.CANNOT_AVOID) return satCantAvoid(spec);
        //*************************************************

        //*********************路径量词*********************
        if(op == Operator.EE) return satE(spec);
        if(op == Operator.AA) return satA(spec);
        //*************************************************

        //*********************时态算子*********************
        if(op == Operator.NEXT) {
            //Xf正时态测试器构造 \sita\phi := TRUE
            x = tester.addVar("X" + (++fieldId));
            xBDD = x.getDomain().ithVar(1);
            c1 = sat(children[0], tester);
            specBDDMap.put(children[0], c1);
            specTesterMap.put(children[0], tester);
            BDD primeC1 = Env.prime(c1);
            tester.conjunctTrans(xBDD.imp(primeC1)); //R\phi := x\phi -> f'
            return xBDD; //J\phi := \emptyset
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
            //f U g正时态测试器构造时似乎并不需要初始断言
            tester.addInitial(xBDD.imp(c1.or(c2))); //\sita\phi := x\phi -> (f or g)
            tester.conjunctTrans(xBDD.imp(c2.or(c1.and(primeX)))); //R\phi := x\phi -> (g or (f and x\phi'))
            tester.addJustice(xBDD.imp(c2)); //J\phi := {x\phi -> g}

            return xBDD;
        }
        if(op == Operator.RELEASES) {
            x = tester.addVar("X" + (++fieldId));
            xBDD = x.getDomain().ithVar(1);
            c1 = sat(children[0], tester);
            c2 = sat(children[1], tester); //\sita\phi := TRUE
            specBDDMap.put(children[0], c1);
            specBDDMap.put(children[1], c2);
            specTesterMap.put(children[0], tester);
            specTesterMap.put(children[1], tester);

            BDD primeX = Env.prime(xBDD);
            tester.conjunctTrans(xBDD.imp(c2.and(c1.or(primeX)))); //R\phi := x\phi -> (g and (f or x\phi'))

            return xBDD; //J\phi := \emptyset
        }
        if(op == Operator.B_UNTIL) {
            return satBUntil(spec, tester);
        }
        if(op == Operator.B_RELEASES) {
            return satBRelease(spec, tester);
        }
        //***************************************************

        //*********************认知模态算子*********************
        if(op == Operator.KNOW) return satKnow(spec);
        if(op == Operator.NKNOW) return satNKnow(spec);
        //****************************************************

        //*********************otherwise*********************
        throw new ModelCheckException("Cannot handle the specification " + spec + ".");
    }

    private BDD satCanEnforce(Spec spec) throws ModelCheckException, SMVParseException,
            ModuleException, ModelCheckAlgException, SpecException {
        SMVModule design = (SMVModule) this.getDesign();
        SpecExp specExp = (SpecExp) spec;
        Operator op = specExp.getOperator();
        Spec[] children = specExp.getChildren();
        BDD negC1, specBDD = null;

        if (op != Operator.CAN_ENFORCE) return null;

        //获取智能体列表
        Vector<String> agentList = new Vector<>();
        for (int i = 0; i < children.length - 1; i++) {
            SpecAgentIdentifier agentId = (SpecAgentIdentifier) children[i];
            agentList.add(agentId.getAgentName());
        }

        SMVModule negC1Tester = new SMVModule("Tester" + (++testerID));
        negC1 = sat(SpecUtil.NNF(new SpecExp(Operator.NOT, children[children.length-1])), negC1Tester);
        specBDDMap.put(children[children.length-1], negC1.not());
        specTesterMap.put(children[children.length-1], negC1Tester);
        if(testerIsEmpty(negC1Tester)) {
            //specBDD = !(feasibleStates & !c1)
            //specBDD = negC1.and(design.ATLCanEnforceFeasible(agentList)).not();
            specBDD = negC1.and(design.ATLCantAvoidFeasible(agentList)).not();

        } else {
            //specBDD = ! forsome auxVars.(feasibleNegC1 & !c1)
            design.syncComposition(negC1Tester); //同步并行组合
            //feasibleNegC1 = 一组智能体(agentList)强制使得!c1成立的可行状态(feasible states)
            //BDD feasibleNegC1 = design.ATLCanEnforceFeasible(agentList);
            BDD feasibleNegC1 = design.ATLCantAvoidFeasible(agentList);
            BDDVarSet auxVars = testerGetAuxVars(negC1Tester);
            specBDD = feasibleNegC1.and(negC1).exist(auxVars).not();
        }
        return specBDD;
    }

//    private BDD satCanEnforce(Spec spec) throws ModelCheckException, SMVParseException,
//            ModuleException, ModelCheckAlgException, SpecException {
//        SMVModule design = (SMVModule) this.getDesign();
//        SpecExp specExp = (SpecExp) spec;
//        Operator op = specExp.getOperator();
//        Spec[] children = specExp.getChildren();
//        BDD c1, specBDD = null;
//
//        if (op != Operator.CAN_ENFORCE) return null;
//
//        //获取智能体列表
//        Vector<String> agentList = new Vector<>();
//        for (int i = 0; i < children.length - 1; i++) {
//            SpecAgentIdentifier agentId = (SpecAgentIdentifier) children[i];
//            agentList.add(agentId.getAgentName());
//        }
//
//        SMVModule c1Tester = new SMVModule("Tester" + (++testerID));
//        c1 = sat(children[children.length-1], c1Tester);
//        specBDDMap.put(children[children.length-1], c1);
//        specTesterMap.put(children[children.length-1], c1Tester);
//        if(testerIsEmpty(c1Tester)) {
//            //specBDD = (feasibleStates & c1)
//            specBDD = c1.and(design.ATLCanEnforceFeasible(agentList));
//        } else {
//            design.syncComposition(c1Tester);
//            // feasibleStates = fair(D||T)
//            BDD feasibleC1 = design.ATLCanEnforceFeasible(agentList);
//            BDDVarSet auxVars = testerGetAuxVars(c1Tester);
//            //specBDD = forsome auxVars.(feasibleStates & c1)
//            specBDD = feasibleC1.and(c1).exist(auxVars);
//        }
//        return specBDD;
//    }

    private BDD satCantAvoid(Spec spec) throws ModelCheckException, SMVParseException,
            ModuleException, ModelCheckAlgException, SpecException {
        SMVModule design = (SMVModule) this.getDesign();
        SpecExp specExp = (SpecExp) spec;
        Operator op = specExp.getOperator();
        Spec[] children = specExp.getChildren();
        BDD c1, specBDD = null;

        if (op != Operator.CANNOT_AVOID) return null;

        //获得智能体列表
        Vector<String> agentList = new Vector<>();
        for (int i = 0; i < children.length - 1; i++) {
            SpecAgentIdentifier agentId = (SpecAgentIdentifier) children[i];
            agentList.add(agentId.getAgentName());
        }

        SMVModule c1Tester = new SMVModule("Tester" + (++testerID));
        c1 = sat(children[children.length-1], c1Tester);
        specBDDMap.put(children[children.length-1], c1);
        specTesterMap.put(children[children.length-1], c1Tester);
        if(testerIsEmpty(c1Tester)) {
            //specBDD = (feasibleStates & c1)
            specBDD = c1.and(design.ATLCantAvoidFeasible(agentList));
        } else {
            design.syncComposition(c1Tester);
            // feasibleStates = fair(D||T)
            BDD feasibleC1 = design.ATLCantAvoidFeasible(agentList);
            BDDVarSet auxVars = testerGetAuxVars(c1Tester);
            //specBDD = forsome auxVars.(feasibleStates & c1)
            specBDD = feasibleC1.and(c1).exist(auxVars);
        }
        return specBDD;
    }

//    private BDD satCantAvoid(Spec spec) throws ModelCheckException, SMVParseException,
//            ModuleException, ModelCheckAlgException, SpecException {
//        SMVModule design = (SMVModule) this.getDesign();
//        SpecExp specExp = (SpecExp) spec;
//        Operator op = specExp.getOperator();
//        Spec[] children = specExp.getChildren();
//        BDD negC1, specBDD = null;
//
//        if (op != Operator.CANNOT_AVOID) return null;
//
//        //获得智能体列表
//        Vector<String> agentList = new Vector<>();
//        for (int i = 0; i < children.length - 1; i++) {
//            SpecAgentIdentifier agentId = (SpecAgentIdentifier) children[i];
//            agentList.add(agentId.getAgentName());
//        }
//
//        SMVModule negC1Tester = new SMVModule("Tester" + (++testerID));
//        negC1 = sat(SpecUtil.NNF(new SpecExp(Operator.NOT, children[children.length-1])), negC1Tester);
//        specBDDMap.put(children[children.length-1], negC1.not());
//        specTesterMap.put(children[children.length-1], negC1Tester);
//        if(testerIsEmpty(negC1Tester)) {
//            //specBDD = !(feasibleStates & !c1)
//            specBDD = negC1.and(design.ATLCanEnforceFeasible(agentList)).not();
//            //specBDD = negC1.and(design.ATLCantAvoidFeasible(agentList)).not();
//
//        } else {
//            //specBDD = ! forsome auxVars.(feasibleNegC1 & !c1)
//            design.syncComposition(negC1Tester); //同步并行组合
//            //feasibleNegC1 = 一组智能体(agentList)强制使得!c1成立的可行状态(feasible states)
//            BDD feasibleNegC1 = design.ATLCanEnforceFeasible(agentList);
//            //BDD feasibleNegC1 = design.ATLCantAvoidFeasible(agentList);
//            BDDVarSet auxVars = testerGetAuxVars(negC1Tester);
//            specBDD = feasibleNegC1.and(negC1).exist(auxVars).not();
//        }
//        return specBDD;
//    }

    private BDD satE(Spec spec)
        throws ModelCheckException, ModuleException, SMVParseException, ModelCheckAlgException, SpecException {
        SMVModule design = (SMVModule) getDesign();
        SpecExp specExp = (SpecExp) spec;
        Operator op = specExp.getOperator();
        Spec[] children = specExp.getChildren();

        BDD c1, specBDD = null;
        if(op != Operator.EE) return null;

        SMVModule c1Tester = new SMVModule("Tester" + (++testerID));
        c1 = sat(children[0], c1Tester);
        specBDDMap.put(children[0], c1);
        specTesterMap.put(children[0], c1Tester);

        if(testerIsEmpty(c1Tester)) {
            //测试器为空, o := fair(D) & c1
            specBDD = c1.and(design.feasible());
        } else {
            design.syncComposition(c1Tester);
            //design.restrictIni(c1); //加上此行代码后, 个别规约验证不符合预期要求, 不需要添加初始状态限制
            BDD feasibleStates = design.feasible();
            BDDVarSet auxVars = testerGetAuxVars(c1Tester);
            //o := fair(D || T) & c1, o为满足Ef的状态集合
            //specBDD = forsome auxVars.fair(D || T) & c1
            specBDD = feasibleStates.and(c1).exist(auxVars);
        }

        return specBDD;
    }

    private BDD satA(Spec spec)
            throws ModelCheckException, ModuleException, SMVParseException, ModelCheckAlgException, SpecException {
        SMVModule design = (SMVModule) getDesign();
        SpecExp specExp = (SpecExp) spec;
        Operator op = specExp.getOperator();
        Spec[] children = specExp.getChildren();

        BDD negC1, specBDD = null;
        if(op != Operator.AA) return null;

        SMVModule negC1Tester = new SMVModule("Tester" + (++testerID));
        negC1 = sat(SpecUtil.NNF(new SpecExp(Operator.NOT, children[0])), negC1Tester);
        specBDDMap.put(children[0], negC1.not());
        specTesterMap.put(children[0], negC1Tester); //c1, T(!c1)

        if(testerIsEmpty(negC1Tester)) {
            specBDD = negC1.and(design.feasible()).not();
        } else {
            //o := !(fair(D || T(!c1)) & !c1)
            //specBDD = ! forsome auxVars.(feasible(D || T(!c1)) & !c1)
            design.syncComposition(negC1Tester);
            BDD feasibleStates = design.feasible();
            BDDVarSet auxVars = testerGetAuxVars(negC1Tester);
            specBDD = feasibleStates.and(negC1).exist(auxVars).not();
        }

        return specBDD;
    }

    private BDD satBUntil(Spec spec, SMVModule tester)
        throws ModuleException, ModelCheckException, SMVParseException, ModelCheckAlgException, SpecException {
        SpecExp specExp = (SpecExp) spec;
        Operator op = specExp.getOperator();
        if(op != Operator.B_UNTIL) return null;

        Spec[] children = specExp.getChildren();
        BDD c1, c2 = null;
        ModuleBDDField x, l, w = null;
        BDD xBDD; //测试器的输出变量

        x = tester.addVar("X" + (++fieldId));
        xBDD = x.getDomain().ithVar(1);
        SpecRange range = (SpecRange) children[1];
        int a = range.getFrom();
        int b = range.getTo();

        c1 = sat(children[0], tester);
        c2 = sat(children[2], tester);
        specBDDMap.put(children[0], c1);
        specBDDMap.put(children[2], c2);
        specTesterMap.put(children[0], tester);
        specTesterMap.put(children[2], tester);

        //0 < a = b 或 0 = a < b
        if((a == b) || (a==0 && b>0)) {
            //若\phi = f U a..a g 且 a>0, 则l属于[0,a]是表示区间下界的整型变量
            //若\phi = f U 0..a g 且 a>0, 则l属于[0,a]是表示区间宽度的整型变量
            l = tester.addVar("L" + fieldId, 0, b);

            //l > 0
            BDD lGreaterThan0 = new StmtOperator(tester, new OpGT(new ValueDomStmt(tester, l),
                    new ValueConsStrStmt(tester, new String[]{"0"}))).eval_stmt().toBDD();
            //next(x)=1 或 x'
            BDD nextXEqual1 = new StmtOperator(tester, new OpEqual(new OpNext(new ValueDomStmt(tester, x)),
                    new ValueConsStrStmt(tester, new String[]{"1"}))).eval_stmt().toBDD();
            //next(l)=l-1 或 l'=l-1
            BDD nextLEqualLMinus1 = new StmtOperator(tester, new OpEqual(new OpNext(new ValueDomStmt(tester, l)),
                    new OpMinus(new ValueDomStmt(tester, l), new ValueConsStrStmt(tester, new String[]{"1"}))))
                    .eval_stmt()
                    .toBDD();
            //l = 0
            BDD lEqual0 = new StmtOperator(tester, new OpEqual(new ValueDomStmt(tester, l),
                    new ValueConsStrStmt(tester, new String[]{"0"}))).eval_stmt().toBDD();
            //l = b
            BDD lEqualB = new StmtOperator(tester, new OpEqual(new ValueDomStmt(tester, l),
                    new ValueConsStrStmt(tester, new String[]{""+b}))).eval_stmt().toBDD();

            if (a == b) { //0 < a = b
                //R\phi := (x & l>0) -> (f & x' & l'=l-1)
                tester.conjunctTrans(xBDD.and(lGreaterThan0).imp(c1.and(nextXEqual1).and(nextLEqualLMinus1)));
                //R\phi := (x & l=0) -> g
                tester.conjunctTrans(xBDD.and(lEqual0).imp(c2));
            } else { //a=0 & b>0
                //R\phi := (x & l>0) -> (g | (f & x' & l'=l-1))
                tester.conjunctTrans(xBDD.and(lGreaterThan0).imp(c2.or(c1.and(nextXEqual1).and(nextLEqualLMinus1))));
                //R\phi := (x & l=0) -> g
                tester.conjunctTrans(xBDD.and(lEqual0).imp(c2));
            }
            //J\phi := \emptyset
            return xBDD.and(lEqualB); //x & l=b
        } else { //0 < a < b
            //l属于[0,a]是表示区间下界的整型变量
            l = tester.addVar("L"+fieldId, 0, a);
            //w属于[0,b-a]是表示区间宽度的整型变量
            w = tester.addVar("W"+fieldId, 0, b-a);

            //next(x)=1 或 x'
            BDD nextXEqual1 = new StmtOperator(tester, new OpEqual(new OpNext(new ValueDomStmt(tester, x)),
                    new ValueConsStrStmt(tester, new String[]{"1"}))).eval_stmt().toBDD();
            //l > 0
            BDD lGreaterThan0 = new StmtOperator(tester, new OpGT(new ValueDomStmt(tester, l),
                    new ValueConsStrStmt(tester, new String[]{"0"}))).eval_stmt().toBDD();
            //l = 0
            BDD lEqual0 = new StmtOperator(tester, new OpEqual(new ValueDomStmt(tester, l),
                    new ValueConsStrStmt(tester, new String[]{"0"}))).eval_stmt().toBDD();
            //l = a
            BDD lEqualA = new StmtOperator(tester, new OpEqual(new ValueDomStmt(tester, l),
                    new ValueConsStrStmt(tester, new String[]{""+a}))).eval_stmt().toBDD();
            //next(l)=0 或 l'=0
            BDD nextLEqual0 = new StmtOperator(tester, new OpEqual(new OpNext(new ValueDomStmt(tester, l)),
                    new ValueConsStrStmt(tester, new String[]{"0"}))).eval_stmt().toBDD();
            //next(l)=l-1 或 l'=l-1
            BDD nextLEqualLMinus1 = new StmtOperator(tester, new OpEqual(new OpNext(new ValueDomStmt(tester, l)),
                    new OpMinus(new ValueDomStmt(tester, l), new ValueConsStrStmt(tester, new String[]{"1"}))))
                    .eval_stmt()
                    .toBDD();
            //w > 0
            BDD wGreaterThan0 = new StmtOperator(tester, new OpGT(new ValueDomStmt(tester, w),
                    new ValueConsStrStmt(tester, new String[]{"0"}))).eval_stmt().toBDD();
            //w = 0
            BDD wEqual0 = new StmtOperator(tester, new OpEqual(new ValueDomStmt(tester, w),
                    new ValueConsStrStmt(tester, new String[]{"0"}))).eval_stmt().toBDD();
            //next(w)=w 或 w'=w
            BDD nextWEqualW = new StmtOperator(tester, new OpEqual(new OpNext(new ValueDomStmt(tester, w)),
                    new ValueDomStmt(tester, w))).eval_stmt().toBDD();
            //next(w)=w-1 或 w'=w-1
            BDD nextWEqualWMinus1 = new StmtOperator(tester, new OpEqual(new OpNext(new ValueDomStmt(tester, w)),
                    new OpMinus(new ValueDomStmt(tester, w), new ValueConsStrStmt(tester, new String[]{"1"}))))
                    .eval_stmt()
                    .toBDD();
            //w = b - a
            BDD wEqualBMinusA = new StmtOperator(tester, new OpEqual(new ValueDomStmt(tester, w),
                    new ValueConsStrStmt(tester, new String[]{""+(b-a)}))).eval_stmt().toBDD();
            //R\phi := (x & l>0 & w>0) -> (f & x' & l'=l-1 & w'=w)
            tester.conjunctTrans(xBDD.and(lGreaterThan0).and(wGreaterThan0)
                    .imp(c1.and(nextXEqual1).and(nextLEqualLMinus1).and(nextWEqualW)));
            //R\phi := (x & l=0 & w>0) -> (g | (f & x' & l'=0 & w'=w-1))
            tester.conjunctTrans(xBDD.and(lEqual0).and(wGreaterThan0).
                    imp(c2.or(c1.and(nextXEqual1).and(nextLEqual0).and(nextWEqualWMinus1))));
            //R\phi := (x & l=0 & w=0) -> g
            tester.conjunctTrans(xBDD.and(lEqual0).and(wEqual0).imp(c2));
            //J\phi := \emptyset
            return xBDD.and(lEqualA).and(wEqualBMinusA); //x & l=a & w=b-a
        }
    }

    private BDD satBRelease(Spec spec, SMVModule tester)
            throws ModuleException, ModelCheckException, SMVParseException, ModelCheckAlgException, SpecException {
        SpecExp specExp = (SpecExp) spec;
        Operator op = specExp.getOperator();
        if(op != Operator.B_RELEASES) return null;

        Spec[] children = specExp.getChildren();
        BDD c1, c2 = null;
        ModuleBDDField x, l, w = null;
        BDD xBDD; //测试器的输出变量

        x = tester.addVar("X" + (++fieldId));
        xBDD = x.getDomain().ithVar(1);
        SpecRange range = (SpecRange) children[1];
        int a = range.getFrom();
        int b = range.getTo();

        c1 = sat(children[0], tester);
        c2 = sat(children[2], tester);
        specBDDMap.put(children[0], c1);
        specBDDMap.put(children[2], c2);
        specTesterMap.put(children[0], tester);
        specTesterMap.put(children[2], tester);

        //0 < a = b 或 0 = a < b
        if((a == b) || (a==0 && b>0)) {
            //若\phi = f R a..a g 且 a>0, 则l属于[0,a]是表示区间下界的整型变量
            //若\phi = f R 0..a g 且 a>0, 则l属于[0,a]是表示区间宽度的整型变量
            l = tester.addVar("L" + fieldId, 0, b);

            //l > 0
            BDD lGreaterThan0 = new StmtOperator(tester, new OpGT(new ValueDomStmt(tester, l),
                    new ValueConsStrStmt(tester, new String[]{"0"}))).eval_stmt().toBDD();
            //next(x)=1 或 x'
            BDD nextXEqual1 = new StmtOperator(tester, new OpEqual(new OpNext(new ValueDomStmt(tester, x)),
                    new ValueConsStrStmt(tester, new String[]{"1"}))).eval_stmt().toBDD();
            //next(l)=l-1 或 l'=l-1
            BDD nextLEqualLMinus1 = new StmtOperator(tester, new OpEqual(new OpNext(new ValueDomStmt(tester, l)),
                    new OpMinus(new ValueDomStmt(tester, l), new ValueConsStrStmt(tester, new String[]{"1"}))))
                    .eval_stmt()
                    .toBDD();
            //l = 0
            BDD lEqual0 = new StmtOperator(tester, new OpEqual(new ValueDomStmt(tester, l),
                    new ValueConsStrStmt(tester, new String[]{"0"}))).eval_stmt().toBDD();
            //l = b
            BDD lEqualB = new StmtOperator(tester, new OpEqual(new ValueDomStmt(tester, l),
                    new ValueConsStrStmt(tester, new String[]{""+b}))).eval_stmt().toBDD();

            if (a == b) { //0 < a = b
                //R\phi := (x & l>0) -> (f | (x' & l'=l-1))
                tester.conjunctTrans(xBDD.and(lGreaterThan0).imp(c1.or(nextXEqual1.and(nextLEqualLMinus1))));
                //R\phi := (x & l=0) -> g
                tester.conjunctTrans(xBDD.and(lEqual0).imp(c2));
            } else { //a=0 & b>0
                //R\phi := (x & l>0) -> (g & (f | (x' & l'=l-1)))
                tester.conjunctTrans(xBDD.and(lGreaterThan0).imp(c2.and(c1.or(nextXEqual1.and(nextLEqualLMinus1)))));
                //R\phi := (x & l=0) -> g
                tester.conjunctTrans(xBDD.and(lEqual0).imp(c2));
            }
            //J\phi := \emptyset
            return xBDD.and(lEqualB); //x & l=b
        } else { //0 < a < b
            //l属于[0,a]是表示区间下界的整型变量
            l = tester.addVar("L"+fieldId, 0, a);
            //w属于[0,b-a]是表示区间宽度的整型变量
            w = tester.addVar("W"+fieldId, 0, b-a);

            //next(x)=1 或 x'
            BDD nextXEqual1 = new StmtOperator(tester, new OpEqual(new OpNext(new ValueDomStmt(tester, x)),
                    new ValueConsStrStmt(tester, new String[]{"1"}))).eval_stmt().toBDD();
            //l > 0
            BDD lGreaterThan0 = new StmtOperator(tester, new OpGT(new ValueDomStmt(tester, l),
                    new ValueConsStrStmt(tester, new String[]{"0"}))).eval_stmt().toBDD();
            //l = 0
            BDD lEqual0 = new StmtOperator(tester, new OpEqual(new ValueDomStmt(tester, l),
                    new ValueConsStrStmt(tester, new String[]{"0"}))).eval_stmt().toBDD();
            //l = a
            BDD lEqualA = new StmtOperator(tester, new OpEqual(new ValueDomStmt(tester, l),
                    new ValueConsStrStmt(tester, new String[]{""+a}))).eval_stmt().toBDD();
            //next(l)=0 或 l'=0
            BDD nextLEqual0 = new StmtOperator(tester, new OpEqual(new OpNext(new ValueDomStmt(tester, l)),
                    new ValueConsStrStmt(tester, new String[]{"0"}))).eval_stmt().toBDD();
            //next(l)=l-1 或 l'=l-1
            BDD nextLEqualLMinus1 = new StmtOperator(tester, new OpEqual(new OpNext(new ValueDomStmt(tester, l)),
                    new OpMinus(new ValueDomStmt(tester, l), new ValueConsStrStmt(tester, new String[]{"1"}))))
                    .eval_stmt()
                    .toBDD();
            //w > 0
            BDD wGreaterThan0 = new StmtOperator(tester, new OpGT(new ValueDomStmt(tester, w),
                    new ValueConsStrStmt(tester, new String[]{"0"}))).eval_stmt().toBDD();
            //w = 0
            BDD wEqual0 = new StmtOperator(tester, new OpEqual(new ValueDomStmt(tester, w),
                    new ValueConsStrStmt(tester, new String[]{"0"}))).eval_stmt().toBDD();
            //next(w)=w 或 w'=w
            BDD nextWEqualW = new StmtOperator(tester, new OpEqual(new OpNext(new ValueDomStmt(tester, w)),
                    new ValueDomStmt(tester, w))).eval_stmt().toBDD();
            //next(w)=w-1 或 w'=w-1
            BDD nextWEqualWMinus1 = new StmtOperator(tester, new OpEqual(new OpNext(new ValueDomStmt(tester, w)),
                    new OpMinus(new ValueDomStmt(tester, w), new ValueConsStrStmt(tester, new String[]{"1"}))))
                    .eval_stmt()
                    .toBDD();
            //w = b - a
            BDD wEqualBMinusA = new StmtOperator(tester, new OpEqual(new ValueDomStmt(tester, w),
                    new ValueConsStrStmt(tester, new String[]{""+(b-a)}))).eval_stmt().toBDD();

            //R\phi := (x & l>0 & w>0) -> (f | (x' & l'=l-1 & w'=w))
            tester.conjunctTrans(xBDD.and(lGreaterThan0).and(wGreaterThan0).
                    imp(c1.or(nextXEqual1.and(nextLEqualLMinus1).and(nextWEqualW))));
            //R\phi := (x & l=0 & w>0) -> (g & (f | (x' & l'=0 & w'=w-1)))
            tester.conjunctTrans(xBDD.and(lEqual0).and(wGreaterThan0).
                    imp(c2.and(c1.or(nextXEqual1.and(nextLEqual0).and(nextWEqualWMinus1)))));
            //R\phi := (x & l=0 & w=0) -> g
            tester.conjunctTrans(xBDD.and(lEqual0).and(wEqual0).imp(c2));
            //J\phi := \emptyset
            return xBDD.and(lEqualA).and(wEqualBMinusA); //x & l=a & w=b-a
        }
    }

    /**
     * spec = i KNOW f
     * @param spec 规约
     * @return BDD
     * @throws ModelCheckAlgException 模型检测算法异常
     * @throws ModelCheckException 模型检测异常
     * @throws SMVParseException SMV解析异常
     * @throws ModuleException Module异常
     * @throws SpecException Spec异常
     */
    private BDD satKnow(Spec spec)
            throws ModelCheckAlgException, ModelCheckException, SMVParseException, ModuleException, SpecException {
        SMVModule design = (SMVModule) getDesign();
        SpecExp specExp = (SpecExp) spec;
        Operator op = specExp.getOperator();
        Spec[] children = specExp.getChildren();

        BDD temp;
        if(op != Operator.KNOW) return null;

        //处理c1, f=children[1];
        SMVModule negC1Tester = new SMVModule("Tester"+(++testerID));
        BDD negC1 = sat(SpecUtil.NNF(new SpecExp(Operator.NOT, children[1])), negC1Tester);

        specBDDMap.put(children[1], negC1.not());
        specTesterMap.put(children[1], negC1Tester);

        if(!testerIsEmpty(negC1Tester)) {
            design.syncComposition(negC1Tester);
            BDD feasibleStates = design.feasible(); //feasibleStates = fair(D || T)
            BDDVarSet auxVars = testerGetAuxVars(negC1Tester);
            //temp = forSome auxVars.(fair(D || T) & !c1)
            temp = feasibleStates.and(negC1).exist(auxVars);
        } else {
            temp = negC1.and(design.feasible()); //temp = (fair(D) & !c1)
        }

        //处理 "i KNOW c1"
        String agentName = children[0].toString();
        SMVAgentInfo agentInfo = getAgentInfo(agentName);
        if (agentInfo == null)
            throw new ModelCheckAlgException("Cannot find the information of agent " + agentName + ".");

        BDDVarSet visibleVars = agentInfo.getVisVars_BDDVarSet();
        BDDVarSet allInvisibleVars = Env.globalUnprimeVarsMinus(visibleVars);

        // return ! forSome (Vars-O_i).temp
        return temp.exist(allInvisibleVars).not();
    }

    private BDD satNKnow(Spec spec)
            throws ModelCheckAlgException, ModelCheckException, SMVParseException, ModuleException, SpecException {
        SMVModule design = (SMVModule) getDesign();
        SpecExp specExp = (SpecExp) spec;
        Operator op = specExp.getOperator();
        Spec[] children = specExp.getChildren();

        BDD temp;
        if(op != Operator.NKNOW) return null;

        //处理c1, f=children[1];
        SMVModule c1Tester = new SMVModule("Tester"+(++testerID));
        BDD c1 = sat(children[1], c1Tester);

        specBDDMap.put(children[1], c1);
        specTesterMap.put(children[1], c1Tester);

        if(!testerIsEmpty(c1Tester)) {
            design.syncComposition(c1Tester);
            BDD feasibleStates = design.feasible(); //feasibleStates = fair(D || T)
            BDDVarSet auxVars = testerGetAuxVars(c1Tester);
            //temp = forSome auxVars.(fair(D || T) & c1)
            temp = feasibleStates.and(c1).exist(auxVars);
            //design.decompose(c1Tester); //此处不需要分解
        } else {
            temp = c1.and(design.feasible()); //temp = (fair(D) & c1)
        }

        //处理 "i NKNOW c1"
        String agentName = children[0].toString();
        SMVAgentInfo agentInfo = getAgentInfo(agentName);
        if (agentInfo == null)
            throw new ModelCheckAlgException("Cannot find the information of agent " + agentName + ".");

        BDDVarSet visibleVars = agentInfo.getVisVars_BDDVarSet();
        BDDVarSet allInvisibleVars = Env.globalUnprimeVarsMinus(visibleVars);

        // return forsome (Vars-O_i).temp
        return temp.exist(allInvisibleVars);
    }

    /**
     * &lt;A&gt; backreach(to, trans)<br/>
     * 计算使用迁移关系trans可以到达to的状态集合, 具体操作是将可达性算法的post()替换为pre(), 并初始化new为to.
     * @param agentList 一组智能体
     * @param trans 迁移关系:(V, α, V')
     * @param to 断言
     * @return 最小不动点
     * @throws ModelCheckAlgException 异常处理
     */
    public static BDD ATLCanEnforceAllPred(Vector<String> agentList, BDD trans, BDD to)
            throws ModelCheckAlgException {
        return ATLCanEnforceKPred(agentList, trans, to, Env.globalPrimeVars(), -1);
    }

    public static BDD ATLCanEnforceKPred(Vector<String> agentList, BDD trans,
                                         BDD to, BDDVarSet primeVars, int k) throws ModelCheckAlgException {
        int cnt = 1;
        BDD oldPred, newPred = to;
        do {
            //储存先前结果
            oldPred = newPred;
            //计算新结果
            newPred = oldPred.or(ATLCanEnforcePred(agentList, trans, oldPred, primeVars));
            cnt++;
        } while (!oldPred.equals(newPred) && (cnt != k)); //当到达不动点时停止

        return newPred;
    }

    /**
     * ATLPre(&lt;&lt;A&gt;&gt; X f)
     * @param agentList 一组智能体
     * @param trans 迁移关系
     * @param to 目标状态
     * @param primeVars 加点变量
     * @return pre
     * @throws ModelCheckAlgException 将异常向上抛出
     */
    public static BDD ATLCanEnforcePred(Vector<String> agentList, BDD trans, BDD to, BDDVarSet primeVars)
            throws ModelCheckAlgException {
        BDDVarSet groupActions, notInGroupActions;
        groupActions = ATLGetAgentActionVars(agentList);
        if(groupActions == null)
            throw new ModelCheckAlgException("Failed obtaining the action variables of the agents " + agentList);
        //notInGroupActions = AllAgentActions - groupActions
        notInGroupActions = ATLGetAllAgentActionVarsMinus(agentList);
        if(notInGroupActions == null)
            throw new ModelCheckAlgException("Failed obtaining the action variables of the adversary agents towards "
                    + agentList);

        BDD primeTo = Env.prime(to);
        //forSome groupActions. forAll notInGroupActions. forAll V'. (trans(V*AllAgentActions, V')->to(V'))
        return trans.imp(primeTo)
                .forAll(primeVars)
                .forAll(notInGroupActions)
                .exist(groupActions);
        //forSome groupActions. forSome notInGroupActions. forSome V'. (trans(V*AllAgentActions, V') and to(V'))
        //and (forSome groupActions. forAll notInGroupActions. forAll V'. (trans(V*AllAgentActions, V')->to(V')))
//        return (trans.and(primeTo)
//                .exist(primeVars)
//                .exist(notInGroupActions)
//                .exist(groupActions))
//                .and(trans.imp(primeTo)
//                        .forAll(primeVars)
//                        .forAll(notInGroupActions)
//                        .exist(groupActions));
    }

    /**
     * [A] backreach(to, trans)<br/>
     * 计算使用迁移关系trans可以到达to的状态集合, 具体操作是将可达性算法的post()替换为pre(), 并初始化new为to.
     * @param agentList 一组智能体
     * @param trans 迁移关系:(V, α, V')
     * @param to 断言
     * @return 最小不动点
     * @throws ModelCheckAlgException 异常处理
     */
    public static BDD ATLCantAvoidAllPred(Vector<String> agentList, BDD trans, BDD to)
            throws ModelCheckAlgException {
        return ATLCantAvoidKPred(agentList, trans, to, Env.globalPrimeVars(), -1);
    }

    public static BDD ATLCantAvoidKPred(Vector<String> agentList, BDD trans, BDD to, BDDVarSet primeVars, int k)
            throws ModelCheckAlgException{
        int cnt = 1;
        BDD oldPred, newPred = to;
        do {
            //储存先前结果
            oldPred = newPred;
            //计算新结果
            newPred = oldPred.or(ATLCantAvoidPred(agentList, trans, oldPred, primeVars));
            cnt++;
        } while (!oldPred.equals(newPred) && (cnt != k)); //当到达不动点时停止

        return newPred;
    }

    /**
     * ATLPre([[A]] X f)
     * @param agentList 一组智能体
     * @param trans 迁移关系
     * @param to 目标状态
     * @param primeVars 加点变量
     * @return pre
     * @throws ModelCheckAlgException 向上抛出异常
     */
    public static BDD ATLCantAvoidPred(Vector<String> agentList, BDD trans, BDD to, BDDVarSet primeVars)
            throws ModelCheckAlgException {
        //[A]X to = !<A>X !to
        return ATLCanEnforcePred(agentList, trans, to.not(), primeVars).not();
    }

    //tester判空方法
    private boolean testerIsEmpty(SMVModule tester) {
        return tester == null || tester.getAll_couples().size() == 0;
    }

    private BDDVarSet testerGetAuxVars(SMVModule tester) {
        BDDVarSet varSet = Env.getEmptySet();
        for(ModuleBDDField var : tester.getAll_couples()) {
            //var.support()方法获取为该字段构造域的BDD变量集
            varSet = varSet.id().union(var.support());
        }
        return varSet;
    }

    private static SMVAgentInfo getAgentInfo(String agentName) throws ModelCheckAlgException {
        return Env.getAll_agent_modules().get(getAgentFullName(agentName));
    }

    private static String getAgentFullName(String agentName) throws ModelCheckAlgException {
        if (agentName == null || agentName.equals(""))
            throw new ModelCheckAlgException("The agent name is null.");

        if (agentName.equals("main")) return agentName;

        int idx_dot = agentName.indexOf('.');
        if (idx_dot == -1) {
            return "main." + agentName;
        } else if (!agentName.substring(0, idx_dot).equals("main")) // agentName = $$$$.####, where $$$$ is not "main"
            throw new ModelCheckAlgException("The agent's name '" + agentName + "' is illegal.");
        else { // the prefix of agentName is "main."
            if (agentName.length() > "main.".length()) // agentName = main.$$$$
                return agentName;
            else // agentName = "main."
                throw new ModelCheckAlgException("The agent's name '" + agentName + "' is illegal.");
        }
    }

    private BDDVarSet getRelevantVars(Module module, Spec spec) {
        BDDVarSet vars = Env.getEmptySet();
        if(spec != null) {
            vars = vars.id().union(spec.releventVars());
        }
        if(module != null) {
            if(module instanceof ModuleWithWeakFairness) {
                ModuleWithWeakFairness weakFairness = (ModuleWithWeakFairness) module;
                for (int i=0; i < weakFairness.justiceNum(); i++) {
                    vars = vars.id().union(weakFairness.justiceAt(i).support());
                }
            }
            if(module instanceof ModuleWithStrongFairness) {
                ModuleWithStrongFairness strongFairness = (ModuleWithStrongFairness) module;
                for (int i=0; i < strongFairness.compassionNum(); i++) {
                    vars = vars.id().union(strongFairness.pCompassionAt(i).support());
                    vars = vars.id().union(strongFairness.qCompassionAt(i).support());
                }
            }
        }
        return vars;
    }

    /**
     * 获取agentList的动作变量集合
     * @param agentList 一组智能体的名称
     * @return 动作变量集合
     * @throws ModelCheckAlgException 异常处理
     */
    private static BDDVarSet ATLGetAgentActionVars(Vector<String> agentList) throws ModelCheckAlgException {
        BDDVarSet actionVars = Env.getEmptySet();
        for(String agentName : agentList) {
            SMVAgentInfo agentInfo = getAgentInfo(agentName);
            if(agentInfo == null)
                throw new ModelCheckAlgException("Cannot find the information of agent " + agentName + ".");
            ModuleBDDField actionField = agentInfo.getActionVar();
            if(actionField == null) continue;
            actionVars = actionVars.id().union(actionField.support());
        }

        return actionVars;
    }

    /**
     * actions(Ag) - actions(agentList)
     * @param minusAgentList 一组智能体
     * @return 动作变量集合
     * @throws ModelCheckAlgException 向上抛出异常
     */
    private static BDDVarSet ATLGetAllAgentActionVarsMinus(Vector<String> minusAgentList) throws ModelCheckAlgException {
        BDDVarSet actionVars = Env.getEmptySet();

        for (String agentFullName : Env.getAll_agent_modules().keySet()) {
            boolean isExist = false;
            for(String minusAgentName : minusAgentList) {
                if(getAgentFullName(minusAgentName).equals(agentFullName)) {
                    isExist = true;
                    break;
                }
            }
            if(!isExist) {
                SMVAgentInfo agentInfo = getAgentInfo(agentFullName);
                if(agentInfo == null)
                    throw new ModelCheckAlgException("Cannot find the information of agent " + agentFullName + ".");
                ModuleBDDField actionFiled = agentInfo.getActionVar();
                if(actionFiled != null)
                    actionVars = actionVars.id().union(actionFiled.support());
            }
        }

        return actionVars;
    }

    public static BDDVarSet ATLGetAllAgentActionVars() throws ModelCheckAlgException {
        BDDVarSet actionVars = Env.getEmptySet();

        for (String agentFullName : Env.getAll_agent_modules().keySet()) {
            SMVAgentInfo agentInfo = getAgentInfo(agentFullName);
            if(agentInfo == null)
                throw new ModelCheckAlgException("Cannot find the information of agent " + agentFullName + ".");
            ModuleBDDField actionFiled = agentInfo.getActionVar();
            if(actionFiled != null)
                actionVars = actionVars.id().union(actionFiled.support());

        }

        return actionVars;
    }

    //********************************证据生成********************************/
    /**
     * <i>witness(&phi;, n)</i>
     * <p>
     *     输入：&phi;是一个CTL* NNF状态公式且n是一个节点, 使得(D<sup>T</sup>, n.s) &#8872; &phi;.<br/>
     *     输出：(从节点node生成) (D<sup>T</sup>, n.s) &#8872; &phi;的证据图
     * </p>
     * @param spec 规约
     * @param node 节点
     * @return boolean
     * @throws ModelCheckAlgException 模型检测算法异常
     * @throws ModelCheckException 模型检测异常
     * @throws SpecException 规约异常
     * @throws SMVParseException SMV解析异常
     * @throws ModuleException 模块异常
     */
    public boolean witness(Spec spec, Node node)
            throws ModelCheckAlgException, ModelCheckException, SpecException, SMVParseException, ModuleException {
        if(node == null) return false;
        BDD state = node.getAttribute("BDD");

        if(!needExpE(spec)) {
            //如果不需要解释算子E, 则将规约spec加入到n.F中, F为依附于该节点的注解集合;
            //F中每条注解是满足状态n.s的spec的状态子公式(s是节点n代表的状态)
            //规约是一个由!, and ,or和AA组成的状态公式, 且!严格限制在断言之前
            graph.nodeAddSpec(node.getId(), spec);
        } else {
            //规约是一个由!, and, or, AA, EE组成的状态公式
            SpecExp specExp = (SpecExp) spec;
            Operator op = specExp.getOperator();
            Spec[] children = specExp.getChildren();

            if(op == Operator.AND) {
                //如果spec = f and g, 分别递归调用witness(f,n);witness(g,n);
                witness(children[0], node);
                witness(children[1], node);
            } else if(op == Operator.OR) {
                //如果spec = f or g
                BDD f = specBDDMap.get(children[0]);
                if(f == null) return false;
                BDD g = specBDDMap.get(children[1]);
                if(g == null) return false;

                //state=n.s, 如果n.s and X(f) != false, 且f不需要解释算子E, 则调用witness(f, n);
                if(!state.and(f).isZero() && !needExpE(children[0]))
                    witness(children[0], node);
                //如果n.s and X(g) != false, 且g不需要解释算子E, 则调用witness(g, n);
                else if(!state.and(g).isZero() && !needExpE(children[1]))
                    witness(children[1], node);
                //如果n.s and X(f) != false, 则调用witness(f, n);
                else if(!state.and(f).isZero())
                    witness(children[0], node);
                //否则调用witness(g, n);
                else witness(children[1], node);
            } else if(op == Operator.EE) {
                //spec=Ef 将会通过点击节点node解释
                graph.nodeAddSpec(node.getId(), spec);
            }
            return true;
        }
        return true;
    }

    /**
     * <i>witnessE(&psi;, n)</i>
     * <p>
     *     输入：&psi;是一个CTL* NNF路径公式且n是一个节点, 使得(D<sup>T</sup>, n.s) &#8872; E&psi;.<br/>
     *     输出：(D<sup>T</sup>, n.s) &#8872; E&psi;的证据
     * </p>
     * @param spec &psi;
     * @param node 节点
     * @return 是否需要解释E
     * @throws SpecException 规约异常
     * @throws ModelCheckException 模型检测异常
     * @throws ModelCheckAlgException 模型检测算法异常
     * @throws SMVParseException SMV解析异常
     * @throws ModuleException 模块异常
     */
    public boolean witnessE(Spec spec, Node node)
            throws SpecException, ModelCheckException, ModelCheckAlgException, SMVParseException, ModuleException {
        if(node == null)return false;

        BDD state = node.getAttribute("BDD");
        if(state==null || state.isZero()) return false;

        SpecExp specExp = (SpecExp) spec;
        Operator op = specExp.getOperator();
        Spec[] children = specExp.getChildren();

        //如果不需要解释算子E也不要解释时态算子, 则将spec加入到n.F中, F是依附于节点n的注解集合
        if(!needExpE(spec) && !needExpT(spec)) {
            graph.nodeAddSpec(node.getId(), spec);
        } else if(needExpE(spec)) {
            //需要解释算子E
            if(op == Operator.AND) {
                //如果spec = f and g, 分别调用方法witnessE(f, n);和witnessE(g, n);
                witnessE(children[0], node);
                witnessE(children[1], node);
            } else if(op == Operator.OR) {
                //如果spec = f or g
                BDD f = specBDDMap.get(children[0]);
                if(f == null) return false;
                BDD g = specBDDMap.get(children[1]);
                if(g == null) return false;

                //如果n.s and X(f) != false 且 !needWitE(f) 且 !needExpT(f), 则递归调用方法witnessE(f, n);
                if(!state.and(f).isZero() && !needExpE(children[0]) && !needExpT(children[0]))
                    witnessE(children[0], node);
                    //如果n.s and X(g) != false 且 !needWitE(g) 且 !needExpT(g), 则递归调用方法witnessE(g, n);
                else if(!state.and(g).isZero() && !needExpE(children[1]) && !needExpE(children[1]))
                    witnessE(children[1], node);
                    //如果n.s and X(f) != false, 则递归调用方法witnessE(f, n);
                else if(!state.and(f).isZero())
                    witnessE(children[0], node);
                    //否则递归调用witnessE(g, n).
                else
                    witnessE(children[1], node);
            } else {
                //n.F := n.F ∪ {spec}, spec = E f
                graph.nodeAddSpec(node.getId(), spec);
            }
        } else {
            //!needWitE(spec) && needExpT(spec)
            //如果节点n满足E spec 且存在时态算子需要被解释, 一般会生成一个新的套索路径;
            //然而若spec是一个路径公式但!needWitE(spec) and needExpT(spec)为真, 仍然可能避免生成套索节点路径
            if(needCrtPath(spec, node)) //needCrtPath(spec, node)判断是否需要生成一个自节点n开始用于解释spec的新的套索节点路径
                lassoPath(spec, node);
            //只在节点n上解释路径公式\psi
            else
                witnessEonNode(spec, node); //for witnessing E \psi at only one node.
        }

        return true;
    }

    /**
     * <i>needWitE(&phi;)</i>
     * <p>
     *     前提：spec必须是NNF公式, 公式的逻辑连接词仅为!, and, or, 并且!在断言之前;<br/>
     *     功能：用于判断在状态公式&phi;中是否存在要被见证(to be witnessed)的路径量词E.
     * </p>
     * <p>
     *     返回真当且仅当在&phi;的语法树中, 存在路径量词 <i>E</i> 使得自它(也许就是root)开始回溯至根(root),
     *     不包含任何时态算子和路径量词, 在这种情况下路径量词 <i>E</i> 需要被见证; 否则返回假.
     * </p>
     * @param spec 规约
     * @return 如果规约由EE和其他连接词and或者or组成, 返回true; 否则返回false
     */
    public static boolean needExpE(Spec spec) {
        //举例: needWitE(p and EFq)返回真; needWitE(p and AFEq)返回假.
        if(spec instanceof SpecBDD) return false;
        SpecExp specExp = (SpecExp) spec;
        Operator op = specExp.getOperator();
        Spec[] children = specExp.getChildren();

        //需要被解释的算子
        if(op == Operator.EE) return true;

        if(op==Operator.AND || op==Operator.OR) {
            return needExpE(children[0]) || needExpE(children[1]);
        } else if(op==Operator.NOT || op==Operator.AA) {
            return false;
        } else {
            //op是时态算子
            return false;
        }
    }

    /**
     * <p>
     *     前提：spec必须是NNF公式, 公式的逻辑连接词仅为!, and, or, 并且!在断言之前;<br/>
     *     功能：判断CTL*NNF路径公式&psi;中是否存在时态算子要被解释.
     * </p>
     * <p>
     *     返回真当且仅当在&psi;的语法树中, 存在时态算子使得自它(也许就是root)开始回溯至根(root),
     *     不包含任何路径量词, 在这种情况下可能需要新建一个套索节点路径解释时态算子; 否则返回假.
     * </p>
     * @param spec 规约
     * @return 如果规约由时态算子和其他连接词and或者or组成, 返回true; 否则返回false.
     */
    public static boolean needExpT(Spec spec) {
        //举例: needExpT(p or E(f U g))返回假; needExpT((f U g) or p), 如果p满足(holds),
        //那么我们可以首先采用p作为(f U g) or p的见证者(witness)以避免为(f U g)生成套索节点路径.
        if(spec instanceof SpecBDD) return false;
        SpecExp specExp = (SpecExp) spec;
        Operator op = specExp.getOperator();
        Spec[] children = specExp.getChildren();

        //需要被解释的时态算子
        if(op.isTemporalOp()) return true;

        if(op==Operator.AND || op==Operator.OR) {
            return needExpT(children[0]) || needExpT(children[1]);
        } else if(op==Operator.NOT || op==Operator.AA || op==Operator.EE) {
            return false;
        } else {
            //op是其他算子
            return false;
        }
    }

    /**
     * needCrtPath(&psi;, n)用于判断为见证E&psi;生成路径的必要性.
     * <p>
     *     输入：&psi;是CTL* NNF 路径公式, 使得&not;needWitE(&psi;) &and; &not;needExpT(&psi;)初始满足;
     *     n是一个节点使得(D<sup>T</sup>, n.s) &#8872; E&psi;
     *     前提：node满足E spec<br/>
     *     结果：如果有必要创建一个新的套索路径以解释公式spec则返回true;<br/>
     *     如果在节点node上已经足够解释spec则返回false.<br/>
     * </p>
     * needCrtPath() 被 witnessE() 调用
     * @param spec 规约
     * @param node 节点
     * @return boolean
     */
    private boolean needCrtPath(Spec spec, Node node) {
        //如果不需要解释算子E, 也不要解释时态算子, 则返回false
        if(!needExpE(spec) && !needExpT(spec))
            return false;

        //ExpE、ExpT
        BDD state = graph.nodeGetBDD(node.getId());
        if(state == null) return false;

        SpecExp specExp = (SpecExp) spec;
        Operator op = specExp.getOperator();
        Spec[] children = specExp.getChildren();

        if(op == Operator.AND) {
            //如果spec = f and g, 返回needCrtPath(f, n) || needCrtPath(g, n);
            return needCrtPath(children[0], node) || needCrtPath(children[1], node);
        } else if(op == Operator.OR) {
            //如果spec = f or g
            BDD f = specBDDMap.get(children[0]);
            if(f == null) return false;
            BDD g = specBDDMap.get(children[1]);
            if(g == null) return false;

            //如果n.s and f != false 且 n.s and g = false, 则递归调用方法needCrtPath(f, n);
            if(!state.and(f).isZero() && state.and(g).isZero())
                return needCrtPath(children[0], node);
            //如果n.s and f = false 且 n.s and g != false, 则递归调用方法needCrtPath(g, n);
            else if(state.and(f).isZero() && !state.and(g).isZero())
                return needCrtPath(children[1], node);
            //如果!needCrtPath(f, n), 则返回false
            else if(!needCrtPath(children[0], node))
                return false;
            //否则返回needCrtPath(g, n);
            else return needCrtPath(children[1], node);
        } else if(op.isTemporalOp()) {
            //如果op是时态算子
            if(op == Operator.UNTIL) {
                //若spec = f U g and (n.s and X(g) != false), 则返回needCrtPath(g, n);
                BDD g = specBDDMap.get(children[1]);
                if(!state.and(g).isZero()) {
                    return needCrtPath(children[1], node);
                } else
                    return true;
            } else if(op == Operator.RELEASES) {
                //若spec = f R g and (n.s and X(f) and X(g) != false), 则返回needCrtPath(f, n) or needCrtPath(g, n);
                BDD f = specBDDMap.get(children[0]);
                BDD g = specBDDMap.get(children[1]);

                if(!state.and(f.and(g)).isZero())
                    return needCrtPath(children[0], node) || needCrtPath(children[1], node);
                else return true;
            } else if(op == Operator.B_UNTIL) {
                SpecRange range = (SpecRange) children[1];
                int a = range.getFrom();
                BDD g = specBDDMap.get(children[2]);

                if(a==0 && !state.and(g).isZero())
                    return needCrtPath(children[2], node);
                else return true;
            } else if(op == Operator.B_RELEASES) {
                SpecRange range = (SpecRange) children[1];
                int a = range.getFrom();
                int b = range.getTo();
                BDD f = specBDDMap.get(children[0]);
                BDD g = specBDDMap.get(children[2]);

                if(a==0 && b==0 && !state.and(g).isZero())
                    return needCrtPath(children[2], node);
                else if(a==0 && b>0 && !state.and(f.and(g)).isZero())
                    return needCrtPath(children[0], node) || needCrtPath(children[2], node);
                else
                    return true;
            }else {
                //op = X
                return true;
            }
        } else {
            //op = EE
            return needCrtPath(spec, node);
        }
    }

    /**
     * <i>witnessEonNode(&psi;,n)</i>用于仅在一个节点见证E&psi;
     * <p>
     *     前提：node满足E spec; &not;needCrtPath(spec, node).<br/>
     *     输入：&psi;是CTL* NNF路径公式, 满足&not;needWitE(&psi;) &and; needExp(&psi;) &and; &not;needCrtPath(&psi;,n);
     *     n是使得(D<sup>T</sup>, n.s) &#8872; E&psi;的节点.
     * </p>
     * @param spec 规约
     * @param node 节点
     * @throws SpecException 向上抛出异常
     */
    private void witnessEonNode(Spec spec, Node node) throws SpecException {
        if(!needExpE(spec) && !needExpT(spec)) {
            //n.F := n.F ∪ {spec}
            graph.nodeAddSpec(node.getId(), spec);
            return;
        }

        //ExpE、ExpT
        BDD state = graph.nodeGetBDD(node.getId());
        if(state == null) return;

        SpecExp specExp = (SpecExp) spec;
        Operator op = specExp.getOperator();
        Spec[] children = specExp.getChildren();

        if(op == Operator.AND) {
            //如果spec = f and g, 则分别递归调用witnessEonNode(f, n);和witnessEonNode(g, n);
            witnessEonNode(children[0], node);
            witnessEonNode(children[1], node);
        } else if(op == Operator.OR) {
            BDD f = specBDDMap.get(children[0]);
            if(f == null) return;
            BDD g = specBDDMap.get(children[1]);
            if(g == null) return;

            //如果n.s and X(f) != false 且 n.s and X(g) = false, 则递归调用witnessEonNode(f, n);
            if(!state.and(f).isZero() && state.and(g).isZero())
                witnessEonNode(children[0], node);
            //如果n.s and X(f) = false 且 n.s and X(g) != false, 则递归调用witnessEonNode(g, n);
            else if(state.and(f).isZero() && !state.and(g).isZero())
                witnessEonNode(children[1], node);
            //如果f不需要生成新的套索路径, 则递归调用witnessEonNode(f, n);
            else if(!needCrtPath(children[0], node))
                witnessEonNode(children[0], node);
            //否则递归调用witnessEonNode(g, n);
            else
                witnessEonNode(children[1], node);
        } else if(op.isTemporalOp()) {
            //op是时态算子
            if(op == Operator.UNTIL) {
                //如果spec = f U g and (n.s and X(g) != false), 则递归调用witnessEonNode(g, n);
                BDD g = specBDDMap.get(children[1]);
                if(!state.and(g).isZero())
                    witnessEonNode(children[1], node);
            } else if(op == Operator.RELEASES) {
                //如果spec = f R g and (n.s and X(f) and X(g) != false),
                //则分别递归调用witnessEonNode(f, n); 和 witnessEonNode(g, n);
                BDD f = specBDDMap.get(children[0]);
                BDD g = specBDDMap.get(children[1]);

                if(!state.and(f.and(g)).isZero()) {
                    witnessEonNode(children[0], node);
                    witnessEonNode(children[1], node);
                }
            } else if(op == Operator.B_UNTIL) {
                SpecRange range = (SpecRange) children[1];
                int a = range.getFrom();
                BDD g = specBDDMap.get(children[2]);

                if(a==0 && !state.and(g).isZero())
                    witnessEonNode(children[2], node);
            } else if(op == Operator.B_RELEASES) {
                SpecRange range = (SpecRange) children[1];
                int a = range.getFrom();
                int b = range.getTo();
                BDD f = specBDDMap.get(children[0]);
                BDD g = specBDDMap.get(children[2]);

                if(a==0 && b==0 && !state.and(g).isZero())
                    witnessEonNode(children[2], node);
                else if(a==0 && b>0 && !state.and(f.and(g)).isZero()) {
                    witnessEonNode(children[0], node);
                    witnessEonNode(children[2], node);
                }
            }
        } else {
            //op = EE
            witnessEonNode(children[0], node);
        }
    }

    /**
     * <i>lassoPath(&psi;,n)</i>, 套索路径&pi; = prefix*period<sup>&omega;</sup>
     * <p>
     *     输入：&psi;是一个CTL* NNF路径公式且n是一个节点, 使得(D<sup>T</sup>, n.s) &#8872; E&psi;.
     * </p>
     * <p>
     *     前提：n.s 满足 E spec<br/>
     *     构造一条套索路径r, 使得r满足于spec;<br/>
     *     r的前缀是最短的, 且r的loop是在具有最少状态数量的SCC内.
     * </p>
     * @param spec 需要创建套索路径的规约
     * @param node 套索路径的起始节点
     * @return boolean
     * @throws SpecException 规约异常
     * @throws ModelCheckException 模型检测异常
     * @throws ModelCheckAlgException 模型检测算法异常
     * @throws SMVParseException SMV解析异常
     * @throws ModuleException 模块异常
     */
    public boolean lassoPath(Spec spec, Node node)
        throws SpecException, ModelCheckException, ModelCheckAlgException, SMVParseException, ModuleException {
        if(node == null) return false;

        //起始节点
        BDD fromState = node.getAttribute("BDD");
        int pathNo = node.getAttribute("pathNo");
        int stateNo = node.getAttribute("stateNo");
        String fromNodeID = pathNo+"."+stateNo;
        if(fromState==null || fromState.isZero())
            return false;

        BDDVarSet auxBDDVarSet = Env.globalUnprimeVarsMinus(stateVarSet);
        //D_fromState是起始节点的D-state
        BDD D_fromState = fromState.exist(auxBDDVarSet);

        SpecExp specExp = (SpecExp) spec;
        Operator op = specExp.getOperator();

        //构造design = D || T_spec
        SMVModule DT = (SMVModule) getDesign();
        ModuleWithWeakFairness weakDT = null;
        if(DT != null) weakDT = DT;

        SMVModule tester = specTesterMap.get(spec);
        BDDVarSet specAuxBDDVarSet = Env.getEmptySet();
        BDD DT_feasible = null;
        assert DT != null;
        if(!testerIsEmpty(tester)) {
            DT.syncComposition(tester);
            DT_feasible = DT.feasible();
            specAuxBDDVarSet = testerGetAuxVars(tester);
        } else {
            DT_feasible = DT.feasible();
        }

        BDDVarSet specAllBDDVarSet = stateVarSet.union(specAuxBDDVarSet);

        //如果spec的测试器非空, DT是design和spec的测试器的同步并行组合
        BDD DT_fromStates = D_fromState.and(DT_feasible.and(specBDDMap.get(spec)));

        //保存先前的限制状态(restriction state)
        Vector<BDD> oldTransRestrictions = DT.getAllTransRestrictions();

        DT.restrictTrans(DT_feasible.and(Env.prime(DT_feasible)));
        int oldTransRestrictionsSize = DT.getTransRestrictionsSize();

        //条件一: 公平环路(cycle)在公平SCC内, SCC自n.s可达且最接近n.s;
        //条件二: 公平SCC拥有最少数量的状态, 状态来自在从n.s可达且最接近n.s的候选SCC之内;
        //条件三: 前缀(prefix)是从n.s至公平SCC的最短路径;
        //条件四: 公平环路(cycle)是建立在前缀(prefix)最后一个状态上的.
        //----------------------------------------------------------------------------------
        // S1(Step 1): 计算D^T状态的集合Z, 这里的D^T状态仅包含所有n.s-可达的公平SCCs和在这些SCCs之间路径上的状态.
        //----------------------------------------------------------------------------------
        //1) Z' := n.s
        BDD Zp = DT_fromStates;
        //2) Z := n.s ◦ R*
        BDD Z = DT.allSucc(DT_fromStates);  //Z是从n.s开始的可达状态集合
        //3) 限制迁移关系 T := R ∧ (Z × Z);
        DT.restrictTrans(Z.id().and(Env.prime(Z)));
        //4) c用于记录新集合Z是否等价于旧集合Z′ c := (Z != Z')
        boolean c = !Z.equals(Zp);
        while(c) {
            Zp = Z; //储存前一次的结果 Z' := Z
            c = false; //c := ⊥
            BDD Y;
            //7-9) 连续去除集合Z中没有后继的状态
            while(true) {
                //8) Y := Z ∧ (Z ◦ T)
                Y = Z.and(DT.succ(Z));
                //9) 如果Y = Z, 则跳出(break); 否则Z = Y.
                if(Y.equals(Z)) break; else Z=Y;
            }
            //10-11) 一旦新集合Z不等于旧集合Z'时, 将迁移关系T限制在集合 Z×Z 内.
            //如果Z != Z', T := T ∧ (Z × Z); Z′ := Z; c := ⊤;
            if(!Z.equals(Zp)) {
                DT.restrictTrans(Z.id().and(Env.prime(Z)));
                Zp = Z;
                c = true;
            }

            //12-15) 去除集合Z中所有不可达J-状态的状态
            for (int i = 0; i < weakDT.justiceNum(); i++) {
                //Z := (Z ∧ J) ◦ T∗;
                Z = DT.allSucc(Z.id().and(weakDT.justiceAt(i)));
                //14-15) 同10-11), 一旦新集合Z不等于旧集合Z'时, 将迁移关系T限制在集合 Z×Z 内.
                //如果Z != Z', T := T ∧ (Z × Z); Z′ := Z; c := ⊤;
                if(!Z.equals(Zp)) {
                    DT.restrictTrans(Z.id().and(Env.prime(Z)));
                    Zp = Z;
                    c = true;
                }
            }
        }
        //----------------------------------------------------------------------------------
        // S2(Step 2): 在Z中选择一个公平的SCC scc, scc包含最少数量的状态且最接近n.s.
        // Z是 D||T_spec状态集合, 包含所有可以自DT_fromStates到达和最接近DT_fromStates的公平SCC.
        //----------------------------------------------------------------------------------

        //恢复所有旧的迁移限制
        Vector<BDD> addedTransRestrictions = new Vector<>();
        while(DT.getTransRestrictionsSize() > oldTransRestrictionsSize) {
            addedTransRestrictions.add(DT.getTransRestriction(oldTransRestrictionsSize));
            DT.removeTransRestriction(oldTransRestrictionsSize);
        }
        //16-20) 广度有限搜索自n.s可达且最接近n.s的状态
        Zp = DT_fromStates; //Y := n.s
        //reach := n.s
        BDD reach = DT_fromStates, glue; //"glue"是将前缀(prefix)和循环(loop/period)连接在一起的一组状态
        glue = Zp.and(Z); //Y ∧ Z = ⊥
        while (glue.isZero()) {
            //18) Y是之前没有访问过的新的后继集合 Y := (Y ◦ R) ∧ ¬reach;
            Zp = DT.succ(Zp.id()).and(reach.not());
            //19) "reach"是之前已经访问过的状态集合 reach := reach ∨ Y ;
            reach = reach.id().or(Zp);
            glue = Zp.and(Z);
        }
        //现在"glue"是将前缀(prefix)和循环(loop/period)连接在一起的状态集合

        //恢复新的迁移限制
        for (BDD addedTransRestriction : addedTransRestrictions) {
            DT.restrictTrans(addedTransRestriction.id());
        }

        //Z := Y ∧ Z; scc := ⊥; t := ⊥;
        BDD scc = Env.FALSE(), scc2, t = Env.FALSE(), t2;
        //21-24) 选择一个公平SCC scc, 该scc包含了Z中的t状态且拥有最少数量的状态
        while (!glue.isZero()) {
            //22) t′ := choose(Z); Z := Z ∧ ¬t′;
            t2 = glue.satOne(specAllBDDVarSet, false);
            //scc′ := T∗ ◦ t′;
            glue = glue.id().and(t2.not()); // glue = glue - {t2}
            //23)
            scc2 = DT.allPred(t2); //DT.allSucc(t2).and(DT.allPred(t2));
            //24) 如果 scc = ⊥ 或者 |scc′| < |scc|, 则scc := scc′; t := t′;
            if(scc.isZero() || scc2.satCount(specAllBDDVarSet)<scc.satCount(specAllBDDVarSet)) {
                //使用scc存储在当前获得的SCCs中拥有最少数量状态的SCC, 用t存储scc中最接近n.s的状态
                scc = scc2; t = t2;
            }
        }

        //----------------------------------------------------------------------------------
        // 前提: i.scc是集合Z内的公平SCC, 包含最少数量的状态, 可从DT_fromStates到达并最接近DT_fromStates;
        //      ii.t是scc中最接近DT_fromStates的状态.
        // S3(Step 3):
        //----------------------------------------------------------------------------------

        //恢复旧的迁移限制
        while(DT.getTransRestrictionsSize()>oldTransRestrictionsSize) { DT.removeTransRestriction(oldTransRestrictionsSize); }

        //25) 生成从n.s至t的最短路径, t是scc中最接近n.s的状态. prefix := path(n.s, t,R);
        Vector<BDD> prefix = new Vector<>();
        //广度优先算法基于OBDD的实现
        BDD[] path = DT.shortestPath(DT_fromStates, t);
        //26) 放弃"path"的最后一个状态, 因为它是"glued loop"的第一个状态 prefix := prefix − (last(prefix));
        for (int i = 0; i < path.length-1; i++)
            prefix.add(path[i]);

        //----------------------------------------------------------------------------------
        // 前提: "prefix"是从DT_fromStates 至t的最短路径.
        // S4(Step 4): 在scc内从t开始构建一个公平回环路径"period".
        //----------------------------------------------------------------------------------
        //27) 将迁移关系T限制为D在scc的状态空间内的原始迁移关系R. T := R ∧ (scc × scc);
        DT.restrictTrans(scc.id().and(Env.prime(scc)));

        //28) period := (t);
        Vector<BDD> period = new Vector<BDD>();
        period.add(t);

        //29-31) 确保period对于每一条公平性约束包含一个公平状态
        BDD fulfill;
        for (int i=0; i < weakDT.justiceNum(); i++) {
            // Line 12, check if j[i] already satisfied
            //if J ∧ s = ⊥ for all s in period
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
                //消除边, 因为"from"已经在周期中
                for (int j = 1; j < path.length; j++)
                    period.add(path[j]);
            }
        }

        ModuleWithStrongFairness strongDT = (ModuleWithStrongFairness) DT;
        for (int i = 0; i < strongDT.compassionNum(); i++) {
            if (!scc.and(strongDT.pCompassionAt(i)).isZero()) {
                //检查 C (强公平约束)要求 i 是否已经满足
                fulfill = Env.FALSE();
                for (int j = 0; j < period.size(); j++) {
                    fulfill = period.elementAt(j)
                            .and(strongDT.qCompassionAt(i))
                            .satOne(specAllBDDVarSet, false);
                    if (!fulfill.isZero()) {
                        break;
                    }
                }

                if (fulfill.isZero()) {
                    BDD from = period.lastElement();
                    BDD to = scc.and(strongDT.qCompassionAt(i));
                    path = strongDT.shortestPath(from, to);
                    //消除边, 因为"from"已经在周期中
                    for (int j = 1; j < path.length; j++)
                        period.add(path[j]);
                }
            }
        }

        //闭环
        //长度为1的周期可能是公平的, 但period[1]可能不是其自身的后继.常规路径不会添加任何内容.
        //为了解决这种情况, 我们向 _period 添加另一个状态, 现在可以了, 因为period[1]和period[n]不相等.

        if (!period.firstElement().and(period.lastElement()).isZero()) {
            //第一个和最后一个状态已经相等, 所以我们不需要扩展它们来完成一个循环,
            //除非 period 是长度 = 1 的退化(degenerate)情况, 它不是自身的后继.
            if (period.size() == 1) {
                //检查_period[1]是否是它自身的后继
                if (period.firstElement().and(
                        DT.succ(period.firstElement())).isZero()) {
                    //period[1]不是它自身的后继: 增加状态至周期(period)
                    period.add(DT.succ(period.firstElement()).satOne(
                            specAllBDDVarSet, false));

                    //闭环
                    BDD from = period.lastElement();
                    BDD to = period.firstElement();
                    path = DT.shortestPath(from, to);
                    //消除由于 from 和 to 已经在周期中的边
                    for (int i = 1; i < path.length - 1; i++) {
                        period.add(path[i]);
                    }
                }
            }
        } else {
            BDD from = period.lastElement();
            BDD to = period.firstElement();
            path = DT.shortestPath(from, to);
            //消除由于 from 和 to 已经在周期中的边
            for (int i = 1; i < path.length - 1; i++) {
                period.add(path[i]);
            }
        }

        //----------------------------------------------------------------------------------
        // 前提: 现在prefix和period都准备好了, 从period的最后一个元素到period的第一个元素(它的索引是loopNodeIdx)有一条迁移.
        // S5(Step 5): 构造节点路径\pi代表公平路径prefix * (period - {last(period)})^\omega
        //----------------------------------------------------------------------------------
        //ls := prefix ∗ (period − last(period));
        int loopNodeIdx = prefix.size();  //period的第一个节点的索引

        prefix.addAll(period);
        //现在prefix是prefix和period的组合, 并且prefix的第一个元素是状态n.s

        String first_created_edgeId = null;
        //34) 无论路径大小, 都创建一条新路径 pathNum := pathNum + 1;
        createdPathNumber++;

        String pred_nid, cur_nid;
        pred_nid = fromNodeID;

        Vector<String> trunkNodePath = new Vector<>(); //将创建树干(trunk)节点路径
        trunkNodePath.add(fromNodeID);

        for (int i=1; i < prefix.size(); i++) {
            graph.addStateNode(createdPathNumber, i, prefix.get(i));
            cur_nid = createdPathNumber + "." + i;

            trunkNodePath.add(cur_nid);

            String edgeId = pred_nid + "->" + cur_nid;
            graph.addArc(edgeId, pred_nid, cur_nid, true);
            if(first_created_edgeId == null) {
                first_created_edgeId = edgeId;
            }
            pred_nid = cur_nid;
        }

        String to_nodeId=null;
        if(loopNodeIdx == 0)
            to_nodeId = fromNodeID;
        else
            to_nodeId=createdPathNumber+"."+loopNodeIdx;
        graph.addArc(pred_nid+"->"+to_nodeId, pred_nid, to_nodeId, true);

        //恢复旧的迁移限制
        while(DT.getTransRestrictionsSize()>oldTransRestrictionsSize) {
            DT.removeTransRestriction(oldTransRestrictionsSize);
        }


        NodePath nodePath = new NodePath(trunkNodePaths.size(), trunkNodePath, loopNodeIdx, path[0]);
        trunkNodePaths.add(nodePath);

        //36) 注解\pi的第一条边, 元组(\psi,0)
        if(!op.isTemporalOp()){
            //如果规约spec不是主时态子公式, 则需要在调用explainPath(spec...)之前展示
            graph.edgeAddSpec(first_created_edgeId, spec, nodePath, 0, true);
        }

        //-------------------------------------------------------------------------------------
        // S6(Step 6): 根据\psi的语义通过将\psi的不可缺少的算子依附于\pi上,
        // 调用explainPath()方法解释(D^T, \pi) \vDash(满足符) \psi.
        //-------------------------------------------------------------------------------------

        //37) 调用explainPath(\psi, \pi, 0);
        boolean b= explainPath(spec, nodePath, 0);

        //恢复原来的迁移关系限制
        DT.setAllTransRestrictions(oldTransRestrictions);
        if(!testerIsEmpty(tester))
            DT.decompose(tester);

        return b;
    }

    /**
     * 解释在该节点上的所有状态规约, 所有时态规约采用该节点作为路径的第一个节点
     * @param nodeID 节点ID
     * @return boolean
     * @throws ModelCheckAlgException 模型检测算法异常
     * @throws ModelCheckException 模型检测异常
     * @throws SpecException 规约异常
     * @throws SMVParseException SMV解析异常
     * @throws ModuleException 模块异常
     */
    public boolean explainOneNode(String nodeID)
        throws ModelCheckAlgException, ModelCheckException, SpecException, SMVParseException, ModuleException {
        Node node = graph.getNode(nodeID);
        if(node == null) return false;

        //解释该节点上的所有状态规约
        //specNum是n.A中状态公式的数量
        int specNum = node.getAttribute("spriteSpecNumber");
        for(int i=1; i <= specNum; i++){
            Sprite sprite = node.getAttribute("spriteSpec"+i);
            if(sprite == null) return false;
            boolean needExplained = sprite.getAttribute("needExplained");
            boolean explained = sprite.getAttribute("explained");
            if(needExplained && !explained){
                Spec spec = sprite.getAttribute("spec");
                SpecExp se = (SpecExp)spec;
                Operator op = se.getOperator();
                Spec[] child = se.getChildren();

                if(op == Operator.EE)
                    witnessE(child[0], node);
                else if(op == Operator.AND){
                    witness(child[0], node);
                    witness(child[1], node);
                }else if(op == Operator.OR){
                    BDD lc = specBDDMap.get(child[0]);
                    if(lc == null) return false;
                    BDD state = graph.nodeGetBDD(nodeID);
                    if (!state.and(lc).isZero())
                        witness(child[0], node);
                    else
                        witness(child[1], node);
                }

                sprite.setAttribute("explained",true);
            }
        }

        //解释将此节点作为第一个节点的后缀路径上的所有时态公式
        for (Edge edge : node.getEachLeavingEdge()) {
            specNum = edge.getAttribute("spriteSpecNumber");
            for (int i=1; i <= specNum; i++) {
                Sprite sprite = edge.getAttribute("spriteSpec" + i);
                if (sprite == null) return false;
                boolean needExplained = sprite.getAttribute("needExplained");
                boolean explained = sprite.getAttribute("explained");

                if (needExplained && !explained) {
                    Spec spec = sprite.getAttribute("spec");
                    NodePath path = sprite.getAttribute("path");
                    int pos = sprite.getAttribute("pos");
                    explainPath(spec, path, pos);
                    sprite.setAttribute("explained", true);
                }
            }
        }

        return true;
    }

    /**
     * <i>explainPath(&psi;, &pi;, i)</i>
     * <p>
     *     输入：&psi;是CTL* NNF路径公式, 使得(D<sup>T</sup>,&pi;,i) &#8872; &psi;,
     *     其中&pi;是D<sup>T</sup>的一条套索节点路径, i是&pi;上&psi;被解释的位置.
     * </p>
     * <p>
     *     前提：初始时, 规约spec满足!needExpE and needExpT; path^pos 满足 spec<br/>
     *     结果：将必要的可满足的规约spec的子公式依附至后缀路径path^pos的一些节点上
     * </p>
     * @param spec 需要被解释的规约
     * @param path 节点路径
     * @param pos spec被解释在path^pos上, 即开始于逻辑位置pos的路径后缀
     * @return boolean
     * @throws ModelCheckException 模型检测异常
     * @throws SpecException 规约异常
     * @throws ModelCheckAlgException 模型检测算法异常
     * @throws SMVParseException SMV解析异常
     * @throws ModuleException 模型异常
     */
    public boolean explainPath(Spec spec, NodePath path, int pos)
            throws ModelCheckException, SpecException, ModelCheckAlgException, SMVParseException, ModuleException {
        if(path == null) return false;

        int startIDx = path.at(pos); //节点在位置i的索引, j := at(\pi,i)
        String startNID = path.get(startIDx);
        BDD startState = graph.nodeGetBDD(startNID);

        //规约spec的测试器
        //SMVModule tester = specTesterMap.get(spec);

        if(spec.isStateSpec()) {
            //如果规约spec为状态公式, 则\pi[j].F = \pi[j].F ∪ {spec}
            graph.nodeAddSpec(startNID, spec);
        } else {
            //规约不是状态公式
            SpecExp specExp = (SpecExp) spec;
            Operator op = specExp.getOperator();
            Spec[] children = specExp.getChildren();

            if(op == Operator.AND) {
                //如果spec = f and g, 则分别递归调用explainPath(f,\pi,i); 和 explainPath(g,\pi,i);
                boolean b1 = explainPath(children[0], path, pos);
                return b1 && explainPath(children[1], path, pos);
            } else if(op == Operator.OR) {
                //规约spec = f or g
                boolean fNeedExplain = needExpE(children[0]) || needExpT(children[0]);
                Spec p, q;

                if(!fNeedExplain) {
                    p = children[0];
                    q = children[1];
                } else {
                    p = children[1];
                    q = children[0];
                }
                BDD pBDD = specBDDMap.get(p);
                if(pBDD == null) return false;
                //如果\pi[j].s and X(f) != false, 则递归调用explainPath(f,\pi,i);
                if(!startState.and(pBDD).isZero())
                    return explainPath(p, path, pos);
                    //否则递归调用explain(g,\pi,i);
                else
                    return explainPath(q, path, pos);
            } else {
                //规约spec是一个主时态公式 spec = Xf, fUg, fRg, f U a..b g, f R a..b g
                //根据规约在path^pos的语义解释spec.
                if(op == Operator.NEXT) {
                    //如果(spec = Xf) and x_spec and \pi[j].s != false,
                    //则attach(Xf,\pi,i)用于展示, attach(f,\pi,i+1)
                    BDD X = specBDDMap.get(spec);
                    if(X == null) return false;
                    if(startState.and(X).isZero()) return false;

                    int ni = path.at(pos+1);
                    BDD nextState = graph.nodeGetBDD(path.get(ni));
                    BDD Xf = specBDDMap.get(children[0]);
                    if(Xf==null || nextState.and(Xf).isZero()) return false;

                    String firstEdgeID = startNID + "->" +path.get(ni);
                    graph.edgeAddSpec(firstEdgeID, spec, path, pos, true);

                    String eid = path.get(path.at(pos+1)) + "->" + path.get(path.at(pos+2));
                    if(children[0].isStateSpec())
                        graph.nodeAddSpec(path.get(ni), children[0]);
                    else
                        graph.edgeAddSpec(eid, children[0], path, pos+1, false);
                } else if(op == Operator.UNTIL) {
                    //如果(spec = f U g) and x_spec and \pi[j].s != false,
                    //则attach(f U g,\pi,i)用于展示
                    BDD X = specBDDMap.get(spec);
                    if(X == null) return false;
                    BDD Xf = specBDDMap.get(children[0]);
                    if(Xf == null) return false;
                    BDD Xg = specBDDMap.get(children[1]);
                    if(Xg == null) return false;
                    if(startState.and(X).isZero())
                        return false;

                    graph.edgeAddSpec(path.get(path.at(pos)) + "->" + path.get(path.at(pos+1)),
                            spec, path, pos, true);

                    String currentNid;
                    //exp = false 则停止解释 f
                    boolean exp = true;
                    for(int p=pos; ; p++) {
                        currentNid = path.get(path.at(p)); //j := at(\pi,i')
                        BDD currentState = graph.nodeGetBDD(currentNid);
                        if(currentState == null) return false;
                        String eid = path.get(path.at(p)) + "->" + path.get(path.at(p+1));
                        if(!currentState.and(Xg).isZero()) {
                            //如果\pi[j].s and x_spec and X(g) != false, 则attach(g,\pi,i');
                            //满足g
                            if(children[1].isStateSpec())
                                graph.nodeAddSpec(path.get(path.at(p)), children[1]);
                            else
                                graph.edgeAddSpec(eid, children[1], path, p, false);
                            return true;
                        } else if(exp && !currentState.and(Xf).isZero()) {
                            //如果\pi[j].s and x_spec and X(f) != false 且 (\pi[at(\pi,j+1)].s and x_spec != false) and exp,
                            //则attach(f,\pi,i');
                            //满足f
                            if(children[0].isStateSpec())
                                graph.nodeAddSpec(path.get(path.at(p)), children[0]);
                            else
                                graph.edgeAddSpec(eid, children[0], path, p, false);
                            exp = path.needExplainNextPosition(pos, p);
                        }
                    }
                } else if(op == Operator.RELEASES) {
                    BDD X = specBDDMap.get(spec);
                    if(X == null) return false;
                    BDD Xf = specBDDMap.get(children[0]);
                    if(Xf == null) return false;
                    BDD Xg = specBDDMap.get(children[1]);
                    if(Xg == null) return false;
                    if(startState.and(X).isZero()) return false;

                    String firstEdgeID = startNID + "->" + path.get(path.at(pos+1));
                    graph.edgeAddSpec(firstEdgeID, spec, path, pos, true);

                    String currentNid, nextNid;
                    //exp = false 停止解释f
                    boolean exp = true;
                    for(int p = pos; ; p++) {
                        currentNid = path.get(path.at(p));
                        nextNid=path.get(path.at(p+1));
                        BDD currentState = graph.nodeGetBDD(currentNid);
                        if(currentState == null) return false;
                        String eid = currentNid + "->" + nextNid;
                        if(!currentState.and(Xf.and(Xg)).isZero()) {
                            //满足f and g
                            if(children[0].isStateSpec())
                                graph.nodeAddSpec(path.get(path.at(p)), children[0]);
                            else
                                graph.edgeAddSpec(eid, children[0], path, p, false);
                            if(children[1].isStateSpec())
                                graph.nodeAddSpec(path.get(path.at(p)), children[1]);
                            else
                                graph.edgeAddSpec(eid, children[1], path, p, false);
                            return true;
                        } else if(!currentState.and(Xg).isZero()) {
                            //满足g
                            if(children[1].isStateSpec())
                                graph.nodeAddSpec(path.get(path.at(p)), children[1]);
                            else
                                graph.edgeAddSpec(eid, children[1], path, p, false);
                            exp = path.needExplainNextPosition(pos, p);
                        }
                        //如果!exp, 意味着f and g不在路径所有节点上满足, 那么停止解释g
                        if(!exp) return true;
                    }

                } else if(op == Operator.B_UNTIL) {
                    SpecRange range = (SpecRange) children[1];
                    int a = range.getFrom(), b = range.getTo();
                    if(a < 0)
                        throw new ModelCheckException("The lower bound of " + spec + "cannot be less than 0.");
                    if(b < 0)
                        throw new ModelCheckException("The upper bound of " + spec + "cannot be less than 0.");
                    if(a > b)
                        throw new ModelCheckException("The lower bound of " + spec + "cannot be larger than the upper bound.");

                    BDD X= specBDDMap.get(spec);
                    if(X == null || startState.and(X).isZero()) return false;
                    BDD Xf= specBDDMap.get(children[0]);
                    if(Xf==null) return false;
                    BDD Xg= specBDDMap.get(children[2]);
                    if(Xg==null) return false;

                    graph.edgeAddSpec(path.get(path.at(pos))+"->"+path.get(path.at(pos+1)),
                            spec,path,pos,true);

                    String currentNid, nextNid;
                    boolean exp=true;
                    for(int p=pos; p < pos+a && exp; p++){
                        currentNid = path.get(path.at(p));
                        nextNid = path.get(path.at(p+1));
                        BDD currentState = graph.nodeGetBDD(currentNid);
                        if(currentState.and(Xf).isZero()) return false;
                        if(children[0].isStateSpec())
                            graph.nodeAddSpec(currentNid, children[0]);
                        else
                            graph.edgeAddSpec(currentNid+"->"+nextNid, children[0], path, p, false);
                        exp = path.needExplainNextPosition(pos, p);
                    }

                    //展示规约spec的上界和下界
                    graph.nodeAddAnnotation(path.get(path.at(pos+a)),
                            "lower bound "+a+" of "+SpecUtil.simplifySpecString(spec,false));
                    graph.nodeAddAnnotation(path.get(path.at(pos+b)),
                            "upper bound "+b+" of "+SpecUtil.simplifySpecString(spec,false));

                    exp=true;
                    for(int p=pos+a; p <= pos+b; p++){
                        currentNid = path.get(path.at(p));
                        nextNid = path.get(path.at(p+1));
                        BDD curState = graph.nodeGetBDD(currentNid);

                        if(!curState.and(Xg).isZero()){
                            //解释 g
                            if(children[2].isStateSpec())
                                graph.nodeAddSpec(currentNid, children[2]);
                            else
                                graph.edgeAddSpec(currentNid+"->"+nextNid, children[2], path, p, false);
                            return true;
                        }else if(exp && !curState.and(Xf).isZero()){
                            //解释 f
                            if (children[0].isStateSpec())
                                graph.nodeAddSpec(currentNid, children[0]);
                            else
                                graph.edgeAddSpec(currentNid + "->" + nextNid, children[0], path, p, false);
                            exp = path.needExplainNextPosition(pos, p);
                        }
                    }
                    return true;
                } else if(op == Operator.B_RELEASES) {
                    SpecRange range = (SpecRange) children[1];
                    int a = range.getFrom(), b = range.getTo();
                    if(a < 0)
                        throw new ModelCheckException("The lower bound of " + spec + "cannot be less than 0.");
                    if(b < 0)
                        throw new ModelCheckException("The upper bound of " + spec + "cannot be less than 0.");
                    if(a > b)
                        throw new ModelCheckException("The lower bound of " + spec + "cannot be larger than the upper bound.");

                    BDD X = specBDDMap.get(spec);
                    if(X == null || startState.and(X).isZero()) return false;
                    BDD Xf = specBDDMap.get(children[0]);
                    if(Xf==null) return false;
                    BDD Xg = specBDDMap.get(children[2]);
                    if(Xg==null) return false;

                    graph.edgeAddSpec(path.get(path.at(pos))+"->"+path.get(path.at(pos+1)),
                            spec, path, pos, true);

                    //查找 f
                    String currentNid, nextNid;
                    int fPos = -1;
                    boolean exp = true;
                    for(int p=pos; p < pos+a && fPos==-1 && exp; p++){
                        currentNid = path.get(path.at(p));
                        BDD currentState = graph.nodeGetBDD(currentNid);
                        if(!currentState.and(Xf).isZero())
                            fPos = p;
                        exp = path.needExplainNextPosition(pos, p);
                    }
                    if(fPos != -1){
                        //在[0,a-1]间找到 f 并且解释
                        currentNid = path.get(path.at(fPos));
                        nextNid = path.get(path.at(fPos+1));
                        if(children[0].isStateSpec())
                            graph.nodeAddSpec(currentNid, children[0]);
                        else
                            graph.edgeAddSpec(currentNid+"->"+nextNid, children[0], path, fPos, false);
                        return true;
                    }

                    //f在[0,a-1]间总为false
                    //neg_f = !f
                    Spec neg_f = SpecUtil.NNF(new SpecExp(Operator.NOT, children[0]));
                    SMVModule design = (SMVModule)getDesign();
                    int oldDesignVariablesNum = design.getAll_couples().size();
                    SMVModule negTester = null;
                    BDD neg_f_bdd = sat(neg_f, negTester);
                    if(design.getAll_couples().size()>oldDesignVariablesNum){
                        //neg_f的测试器非空, 刷新可行状态集合
                        feasibleStatesForWitnessE = design.feasible();
                    }else {
                        //neg_f的测试器为空
                        if (feasibleStatesForWitnessE == null) feasibleStatesForWitnessE = design.feasible();
                    }
                    neg_f_bdd = neg_f_bdd.and(feasibleStatesForWitnessE);

                    //在[0,a-1]间的所有位置解释!f
                    exp = true;
                    for(int p=pos; p<pos+a && exp; p++){
                        currentNid = path.get(path.at(p));
                        nextNid = path.get(path.at(p+1));
                        BDD currentState = graph.nodeGetBDD(currentNid);
                        if(currentState.and(neg_f_bdd).isZero()) return false;
                        if(neg_f.isStateSpec())
                            graph.nodeAddSpec(currentNid, neg_f);
                        else
                            graph.edgeAddSpec(currentNid+"->"+nextNid, neg_f, path,p, false);
                        exp = path.needExplainNextPosition(pos, p);
                    }

                    //展示规约spec的上界和下界
                    graph.nodeAddAnnotation(path.get(path.at(pos+a)),
                            "lower bound "+a+" of "+SpecUtil.simplifySpecString(spec,false));
                    graph.nodeAddAnnotation(path.get(path.at(pos+b)),
                            "upper bound "+b+" of "+SpecUtil.simplifySpecString(spec,false));

                    exp=true;
                    for(int p=pos+a; p <= pos+b; p++){
                        currentNid = path.get(path.at(p));
                        nextNid = path.get(path.at(p+1));
                        BDD currentState = graph.nodeGetBDD(currentNid);
                        if(!currentState.and(Xf).isZero() && !currentState.and(Xg).isZero()){
                            //解释 f and g
                            if(children[0].isStateSpec())
                                graph.nodeAddSpec(currentNid,children[0]);
                            else
                                graph.edgeAddSpec(currentNid+"->"+nextNid, children[0], path, p, false);
                            if(children[2].isStateSpec())
                                graph.nodeAddSpec(currentNid,children[2]);
                            else
                                graph.edgeAddSpec(currentNid+"->"+nextNid, children[2], path, p, false);
                            return true;
                        }else if(exp && !currentState.and(Xg).isZero()){
                            //解释 g
                            if (children[2].isStateSpec())
                                graph.nodeAddSpec(currentNid, children[2]);
                            else
                                graph.edgeAddSpec(currentNid + "->" + nextNid, children[2], path, p, false);
                            exp = path.needExplainNextPosition(pos,p);
                        }
                    }
                    return true;
                } else
                    return true;
            }
        }
        return true;
    }
    //********************************证据生成********************************/

    @Override
    public AlgResultI preAlgorithm() throws AlgExceptionI, SMVParseException, ModelCheckException, ModuleException {
        SMVModule design = (SMVModule) getDesign();
        design.removeAllIniRestrictions(); //重置初始状态限制
        specBDDMap.clear();
        //通过Map.entrySet遍历value(容量大时推荐使用)
        for(Map.Entry<Spec, SMVModule> entry : specTesterMap.entrySet()) {
            ModuleBDDField[] testerVars = null;
            //当上条规约为某一断言f时, sat(!f), 遇NOT算子会将null的测试器添加至specTesterMap
            //故entry.getValue()有可能为空
            if(entry.getValue() != null) {
                testerVars = entry.getValue().getAllFields();
                for(ModuleBDDField var : testerVars) {
                    Env.all_couples.remove(var);
                }
                design.decompose(entry.getValue());
            }
        }
        specTesterMap.clear();
        design.feasible().free(); //不加此条语句会导致多条规约批量验证时出现问题
        return null;
    }

    @Override
    public AlgResultI doAlgorithm() throws AlgExceptionI, ModelCheckException, ModuleException, SMVParseException, SpecException {
        LoggerUtil.info("model checking ATL*K property: {}", this.property);
        if(this.property.isStateSpec()) { //断言也属于状态公式
            //规约为状态公式, 例如ATL*SPEC  <dc2> (BF 6..13 dc2.paid );
            this.checkProp = SpecUtil.NNF(new SpecExp(Operator.NOT, this.property)); //checkProp = !property
        } //else 待补充(LTL, RTLTL, LDLSere, LDLPath) <<Ag>> = CTL*E, <<\emptyset>> = CTL*A
        else {
            //NOT A f >> E NOT f
            this.checkProp = SpecUtil.NNF(new SpecExp(Operator.EE,
                    new SpecExp(Operator.NOT, property)));
        }

        LoggerUtil.info("the NNF of property is: {}", this.checkProp);

        visibleVars = this.getRelevantVars(getDesign(), checkProp);

        SMVModule checkPropTester = null;
        checkBDD = sat(checkProp, checkPropTester);

        SMVModule design = (SMVModule) getDesign();

        //design.restrictIni(checkBDD); //不需要将checkBDD添加为初始状态
        BDD feasibleStates = design.feasible(); //feasibleStates = fair(D || T)
        //D.\sita & o & fair(D || T), o是checkBDD, D.\sita是模型的初始状态
        BDD result = feasibleStates.and(design.initial()).and(checkBDD);

        if(result.isZero()) {
            //result = false, 即D满足\phi
            return new AlgResultString(true, "*** Property is TRUE ***");
        } else {
            //否则D不满足\phi
            //暂时只有RTCTL*公式有证据生成
            if(checkProp.hasATLsPathOperators() || checkProp.hasObsEpistemicOperators()
                    || checkProp.hasSynEpistemicOperators()) {
                String returnMsg = "";
                returnMsg = "*** Property is NOT VALID ***\n ";
                return new AlgResultString(false, returnMsg);
            }
            //通过Map.entrySet遍历value(容量大时推荐使用)
            for(Map.Entry<Spec, SMVModule> entry : specTesterMap.entrySet()) {
                //当上条规约为某一断言f时, sat(!f), 遇NOT算子会将null的测试器添加至specTesterMap
                //故entry.getValue()有可能为空
                if(entry.getValue() != null) {
                    design.decompose(entry.getValue());
                }
            }
            design.feasible().free();

            graph = new GraphExplainATLStar(this.property, this.checkProp, specTesterMap,this);
            graph.addAttribute("ui.title", graph.getId());

            BDD initial = result.satOne(design.moduleUnprimeVars(), false);
            Node node = graph.addStateNode(1, 0, initial);
            node.setAttribute("ui.class", "initialState");

            boolean ok = witness(this.checkProp, node);

            String returnMsg = "";
            returnMsg = "*** Property is false ***\n ";

            if(ok) {
                new ViewerExplainATLStar(graph);
            }

            //能够实现主线程等待子线程结束效果, 但关闭窗口后主线程代码没有继续往下执行
//            final Thread thread = new Thread(() -> {
//                try {
//                    new ViewerExplainATLStar(graph);
//                } catch (SpecException e) {
//                    e.printStackTrace();
//                }
//            });
//
//            thread.start();
//
//            //等待子线程结束
//            try {
//                thread.join();
//            }catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            return new AlgResultString(false, returnMsg);
        }
    }

    @Override
    public AlgResultI postAlgorithm() throws AlgExceptionI {
        this.getDesign().removeAllTransRestrictions();
        return null;
    }
}
