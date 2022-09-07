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
import org.graphstream.graph.Node;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.Vector;

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
            specBDD = negC1.and(design.ATLCanEnforceFeasible(agentList)).not();

        } else {
            //specBDD = ! forsome auxVars.(feasibleNegC1 & !c1)
            design.syncComposition(negC1Tester); //同步并行组合
            //feasibleNegC1 = 一组智能体(agentList)强制使得!c1成立的可行状态(feasible states)
            BDD feasibleNegC1 = design.ATLCanEnforceFeasible(agentList);
            BDDVarSet auxVars = testerGetAuxVars(negC1Tester);
            specBDD = feasibleNegC1.and(negC1).exist(auxVars).not();
        }
        return specBDD;
    }

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
            design.decompose(c1Tester);
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

    //********************************证据生成********************************/
    /**
     * <p>
     *     前提：node满足spec, 且spec是一个状态公式<br/>
     *     从节点node生成spec的证据
     * </p>
     * @param spec 规约
     * @param node 节点
     * @return
     * @throws ModelCheckAlgException
     * @throws ModelCheckException
     * @throws SpecException
     * @throws SMVParseException
     * @throws ModuleException
     */
    public boolean witness(Spec spec, Node node)
            throws ModelCheckAlgException, ModelCheckException, SpecException, SMVParseException, ModuleException {
        if(node == null) return false;
        BDD state = node.getAttribute("BDD");

        if(!needExpE(spec)) {
            //规约是一个由!, and ,or和AA组成的状态公式, 且!严格限制在断言之前
            graph.nodeAddSpec(node.getId(), spec);
        } else {
            //规约是一个由!, and, or, AA, EE组成的状态公式
            SpecExp specExp = (SpecExp) spec;
            Operator op = specExp.getOperator();
            Spec[] children = specExp.getChildren();

            if(op == Operator.AND) {
                witness(children[0], node);
                witness(children[1], node);
            } else if(op == Operator.OR) {
                BDD f = specBDDMap.get(children[0]);
                if(f == null) return false;
                BDD g = specBDDMap.get(children[1]);
                if(g == null) return false;

                if(!state.and(f).isZero() && !needExpE(children[0]))
                    witness(children[0], node);
                else if(!state.and(g).isZero() && !needExpE(children[1]))
                    witness(children[1], node);
                else if(!state.and(f).isZero())
                    witness(children[0], node);
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
     * <p>
     *     前提：spec必须是NNF公式, 公式的逻辑连接词仅为!, and, or, 并且!在断言之前
     * </p>
     * @param spec 规约
     * @return 如果规约由EE和其他连接词and或者or组成, 返回true; 否则返回false
     */
    public static boolean needExpE(Spec spec) {
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
     *     前提：spec必须是NNF公式, 公式的逻辑连接词仅为!, and, or, 并且!在断言之前
     * </p>
     * @param spec 规约
     * @return 如果规约由时态算子和其他连接词and或者or组成, 返回true; 否则返回false
     */
    public static boolean needExpT(Spec spec) {
        if(spec instanceof SpecBDD) return false;
        SpecExp specExp = (SpecExp) spec;
        Operator op = specExp.getOperator();
        Spec[] children = specExp.getChildren();

        //需要被解释的算子
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

    public boolean witnessE(Spec spec, Node node)
            throws SpecException, ModelCheckException, ModelCheckAlgException, SMVParseException, ModuleException {
        if(node == null)return false;

        BDD state = node.getAttribute("BDD");
        int pathNo = node.getAttribute("pathNo");
        int stateNo = node.getAttribute("stateNo");
        if(state==null || state.isZero()) return false;

        SpecExp specExp = (SpecExp) spec;
        Operator op = specExp.getOperator();
        Spec[] children = specExp.getChildren();

        if(!needExpE(spec) && !needExpT(spec)) {
            graph.nodeAddSpec(node.getId(), spec);
        } else if(needExpE(spec)) {
            if(op == Operator.AND) {
                witnessE(children[0], node);
                witnessE(children[1], node);
            } else if(op == Operator.OR) {
                BDD f = specBDDMap.get(children[0]);
                if(f == null) return false;
                BDD g = specBDDMap.get(children[1]);
                if(g == null) return false;

                if(!state.and(f).isZero() && !needExpE(children[0]) && !needExpT(children[0]))
                    witnessE(children[0], node);
                else if(!state.and(g).isZero() && !needExpE(children[1]) && !needExpE(children[1]))
                    witnessE(children[1], node);
                else if(!state.and(f).isZero())
                    witnessE(children[0], node);
                else
                    witnessE(children[1], node);
            } else {
                //spec = E f
                graph.nodeAddSpec(node.getId(), spec);
            }
        } else {
            //!needExpE(spec) && needExpT(spec)
            if(needCrtPath(spec, node))
                lassoPath(spec, node);
            else
                witnessEonNode(spec, node);
        }

        return true;
    }

    /**
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

        int startIDx = path.at(pos);
        String startNID = path.get(startIDx);
        BDD startState = graph.nodeGetBDD(startNID);

        //规约spec的测试器
        SMVModule tester = specTesterMap.get(spec);

        if(spec.isStateSpec()) {
            //规约为状态公式
            graph.nodeAddSpec(startNID, spec);
        } else {
            //规约不是状态公式
            SpecExp specExp = (SpecExp) spec;
            Operator op = specExp.getOperator();
            Spec[] children = specExp.getChildren();

            if(op == Operator.AND) {
                boolean b1 = explainPath(children[0], path, pos);
                return b1 && explainPath(children[1], path, pos);
            } else if(op == Operator.OR) {
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
                if(!startState.and(pBDD).isZero())
                    return explainPath(p, path, pos);
                else
                    return explainPath(q, path, pos);
            } else {
                //规约spec是一个主时态公式 spec = Xf, fUg, fRg, f U a..b g, f R a..b g
                //根据规约在path^pos的语义解释spec.
                if(op == Operator.NEXT) {
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
                        currentNid = path.get(path.at(p));
                        BDD currentState = graph.nodeGetBDD(currentNid);
                        if(currentState == null) return false;
                        String eid = path.get(path.at(p)) + "->" + path.get(path.at(p+1));
                        if(!currentState.and(Xg).isZero()) {
                            //满足g
                            if(children[1].isStateSpec())
                                graph.nodeAddSpec(path.get(path.at(p)), children[1]);
                            else
                                graph.edgeAddSpec(eid, children[1], path, p, false);
                            return true;
                        } else if(exp && !currentState.and(Xf).isZero()) {
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
                        } else if(!currentState.and(Xf).isZero()) {
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

    /**
     * <p>
     *     前提：node满足E spec<br/>
     *     结果：如果有必要创建一个新的套索路径以解释公式spec则返回true;<br/>
     *     如果在节点node上已经足够解释spec则返回false.<br/>
     *     needCrtPath() 被 witnessE() 调用
     * </p>
     * @param spec 规约
     * @param node 节点
     * @return boolean
     */
    private boolean needCrtPath(Spec spec, Node node) {
        if(!needExpE(spec) && !needExpT(spec))
            return false;

        //ExpE、ExpT
        BDD state = graph.nodeGetBDD(node.getId());
        if(state == null) return false;

        SpecExp specExp = (SpecExp) spec;
        Operator op = specExp.getOperator();
        Spec[] children = specExp.getChildren();

        if(op == Operator.AND) {
            return needCrtPath(children[0], node) || needCrtPath(children[1], node);
        } else if(op == Operator.OR) {
            BDD f = specBDDMap.get(children[0]);
            if(f == null) return false;
            BDD g = specBDDMap.get(children[1]);
            if(g == null) return false;

            if(!state.and(f).isZero() && state.and(g).isZero())
                return needCrtPath(children[0], node);
            else if(state.and(f).isZero() && !state.and(g).isZero())
                return needCrtPath(children[1], node);
            else if(!needCrtPath(children[0], node))
                return false;
            else return needCrtPath(children[1], node);
        } else if(op.isTemporalOp()) {
            if(op == Operator.UNTIL) {
                BDD g = specBDDMap.get(children[1]);
                if(!state.and(g).isZero()) {
                    return needCrtPath(children[1], node);
                } else
                    return true;
            } else if(op == Operator.RELEASES) {
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
     * <p>
     *     前提：node满足E spec; !needCrtPath(spec, node).<br/>
     *     结果：如果有必要创建一个新的套索路径以解释公式spec则返回true;
     *     如果在节点node上已经足够解释spec则返回false.
     * </p>
     * @param spec 规约
     * @param node 节点
     * @throws SpecException 向上抛出异常
     */
    private void witnessEonNode(Spec spec, Node node) throws SpecException {
        if(!needExpE(spec) && !needExpT(spec)) {
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
            witnessEonNode(children[0], node);
            witnessEonNode(children[1], node);
        } else if(op == Operator.OR) {
            BDD f = specBDDMap.get(children[0]);
            if(f == null) return;
            BDD g = specBDDMap.get(children[1]);
            if(g == null) return;

            if(!state.and(f).isZero() && state.and(g).isZero())
                witnessEonNode(children[0], node);
            else if(state.and(f).isZero() && !state.and(g).isZero())
                witnessEonNode(children[1], node);
            else if(!needCrtPath(children[0], node))
                witnessEonNode(children[0], node);
            else
                witnessEonNode(children[1], node);
        } else if(op.isTemporalOp()) {
            if(op == Operator.UNTIL) {
                BDD g = specBDDMap.get(children[1]);
                if(!state.and(g).isZero())
                    witnessEonNode(children[1], node);
            } else if(op == Operator.RELEASES) {
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
     * <p>
     *     前提：n.s 满足 E spec<br/>
     *     构造一条套索路径r, 使得r满足于spec;<br/>
     *     r的前缀是最短的, 且r的loop是在具有最少状态数量的SCC内.
     * </p>
     * @param spec
     * @param node
     * @return
     * @throws SpecException
     * @throws ModelCheckException
     * @throws ModelCheckAlgException
     * @throws SMVParseException
     * @throws ModuleException
     */
    private boolean lassoPath(Spec spec, Node node)
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
        // D_fromState is the D-state of the starting node
        BDD D_fromState = fromState.exist(auxBDDVarSet);

        SpecExp specExp = (SpecExp) spec;
        Operator op = specExp.getOperator();

        //(1)构造design = D || T_spec
        SMVModule DT = (SMVModule) getDesign();
        ModuleWithWeakFairness weakDT = null;
        if(DT != null) weakDT = DT;

        BDDVarSet specAuxBDDVarSet = Env.getEmptySet();

        //待完成
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
            String returnMsg = "";
            returnMsg = "*** Property is NOT VALID ***\n ";
            return new AlgResultString(false, returnMsg);
        }
    }

    @Override
    public AlgResultI postAlgorithm() throws AlgExceptionI {
        this.getDesign().removeAllTransRestrictions();
        return null;
    }
}
