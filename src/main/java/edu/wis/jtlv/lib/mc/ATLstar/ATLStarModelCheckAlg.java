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

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/**
 * ATL*模型检测算法
 */
public class ATLStarModelCheckAlg extends ModelCheckAlgI {
    private Spec property;  //待验证的规约
    private BDDVarSet visibleVars; //可观察变量

    private static HashMap<Spec, BDD> specBDDMap = new HashMap<>();
    private static HashMap<Spec, SMVModule> specTesterMap = new HashMap<>();

    private int testerID = 0;

    private int fieldId = 0;

    private Spec checkProp; //实际验证的规约
    private BDD checkBDD; //检测checkProp获得的BDD

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
            //temp = forsome auxVars.(fair(D || T) & !c1)
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

        // return ! forsome (Vars-O_i).temp
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
            //temp = forsome auxVars.(fair(D || T) & c1)
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
     * @param agentList
     * @param trans
     * @param to
     * @param primeVars
     * @return
     * @throws ModelCheckAlgException
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
     * @param agentList
     * @param trans
     * @param to
     * @param primeVars
     * @return
     * @throws ModelCheckAlgException
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
     * @return
     * @throws ModelCheckAlgException
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
