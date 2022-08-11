package edu.wis.jtlv.lib.mc.ATLstar;

import edu.wis.jtlv.env.Env;
import edu.wis.jtlv.env.spec.*;
import edu.wis.jtlv.old_lib.mc.ModelCheckException;

public class SpecUtil {
    //私有化构造方法，不让外界创建本工具类对象
    private SpecUtil() {
    }

    //成员方法定义为静态，方便调用

    /**
     * 返回规约spec的否定范式，包括如下算子：(不可对CTL和RTCTL公式使用, 语法解析时将EU等作为一个操作符)
     * <ul>
     * <li>逻辑连接词 NOT, AND, OR;
     * <li>时态算子 NEXT, FINALLY, GLOBALLY, UNTIL, RELEASES;
     * <li>有界时态算子 B_FINALLY a..b, B_GLOBALLY a..b, B_UNTIL a..b, B_RELEASES a..b 其中(0 < a < b)或(a=0 & b>0);
     * <li>路径量词 EE, AA, CAN_ENFORCE, CANNOT_AVOID;
     * <li>认知模态.
     * </ul>
     * @param spec 规约
     * @return 规约的否定范式
     * @throws ModelCheckException ModelCheck异常
     * @throws SpecException Spec异常
     */
    public static Spec NNF(Spec spec) throws ModelCheckException, SpecException {
        LoggerUtil.info("spec:{}",spec);
        if(!(spec instanceof SpecExp)) return spec;

        SpecExp propExp = (SpecExp) spec;
        Operator op = propExp.getOperator();
        Spec[] children = propExp.getChildren();

        //一元算子NEXT; 路径量词EE, AA; 策略量词CAN_ENFORCE, CANNOT_AVOID
        if(op==Operator.NEXT
                || op==Operator.EE
                || op==Operator.AA
                || op==Operator.CAN_ENFORCE
                || op==Operator.CANNOT_AVOID) {
            return new SpecExp(op, NNF(children[0]));
        }

        //二元算子IMPLIES, XOR(异或), IFF/XNOR(当前仅当/同或,ab+a'b')
        if(op == Operator.IMPLIES) {
            //c1 IMPLIES c2 >> NOT c1 OR c2
            return new SpecExp(Operator.OR, NNF(new SpecExp(Operator.NOT, children[0])), NNF(children[1]));
        }
        if(op==Operator.IFF || op==Operator.XNOR) {
            //c1 IFF/XNOR c2 >> (c1 AND c2) OR (NOT c1 AND NOT c2)
            Spec tempSpec1 = new SpecExp(Operator.AND, NNF(children[0]), NNF(children[1]));
            Spec tempSpec2 = new SpecExp(Operator.AND,
                    NNF(new SpecExp(Operator.NOT, children[0])),
                    NNF(new SpecExp(Operator.NOT, children[1])));
            return new SpecExp(Operator.OR, tempSpec1, tempSpec2);
        }
        if(op == Operator.XOR) {
            //c1 XOR c2 >> NOT (c1 IFF c2)
            return NNF(new SpecExp(Operator.NOT,
                    new SpecExp(Operator.IFF, children[0], children[1])));
        }

        //二元算子AND, OR; 时态算子UNTIL, RELEASES
        if(op==Operator.AND
                || op==Operator.OR
                || op==Operator.UNTIL
                || op==Operator.RELEASES) {
            return new SpecExp(op, NNF(children[0]), NNF(children[1]));
        }

        //时态算子FINALLY c1 >> true UNTIL c1
        if(op == Operator.FINALLY) {
            return new SpecExp(Operator.UNTIL, getTrueSpec(), NNF(children[0]));
        }

        //时态算子GLOBALLY c1 >> NOT (FINALLY NOT c1) >> NOT(true UNTIL NOT c1) >> (false RELEASE c1)
        if (op == Operator.GLOBALLY) {
            return new SpecExp(Operator.RELEASES, getFalseSpec(), NNF(children[0]));
        }

        //以下考虑有界时态算子
        //FINALLY a..b f
        if(op == Operator.B_FINALLY) {
            SpecRange range = (SpecRange) children[0];
            int a = range.getFrom();
            int b = range.getTo();

            if(a < 0) {
                //a < 0, 有界时态算子的下界不应小于0
                LoggerUtil.error("有界时态算子F a..b f的下界a小于0.");
                throw new ModelCheckException("The lower bound of " + spec + " cannot be less than 0.");
            }
            //a >= 0, 有界时态算子的下界大于等于0时
            if(b >= 0) {
                //b >= 0
                if(a > b) {
                    //a > b, 下界不应大于上界
                    LoggerUtil.error("有界时态算子F a..b f的下界a大于上界b.");
                    throw new ModelCheckException("The lower bound of " + spec + " must be no lager than the upper bound.");
                }
                //0 <= a <= b
                if(a==0 && b==0) {
                    //a = b = 0, 即 f
                    return NNF(children[1]);
                } else {
                    //0<a<=b; 0<a<b; 0<=a<b. FINALLY a..b f (b>0) >> true UNTIL a..b f
                    return new SpecExp(Operator.B_UNTIL, getTrueSpec(), range, NNF(children[1]));
                }
            } else {
                //a>=0 and b < 0(infinite), FINALLY a..b f (b<0) >> true UNTIL a..a (FINALLY f)
                Spec tempSpec = NNF(new SpecExp(Operator.FINALLY, children[1]));
                SpecRange newRange = new SpecRange(a, a);

                return new SpecExp(Operator.B_UNTIL, getTrueSpec(), newRange, tempSpec);
            }
        }
        //GLOBALLY a..b f
        if(op == Operator.B_GLOBALLY) {
            SpecRange range = (SpecRange) children[0];
            int a = range.getFrom();
            int b = range.getTo();

            if(a < 0) {
                //a < 0, 有界时态算子的下界不应小于0
                LoggerUtil.error("有界时态算子G a..b f的下界a小于0.");
                throw new ModelCheckException("The lower bound of " + spec + " cannot be less than 0.");
            }
            //a >= 0, 有界时态算子的下界大于等于0时
            if(b >= 0) {
                //b >= 0
                if(a > b) {
                    //a > b, 下界不应大于上界
                    LoggerUtil.error("有界时态算子G a..b f的下界a大于上界b.");
                    throw new ModelCheckException("The lower bound of " + spec +
                            " must be no lager than the upper bound.");
                }
                //0 <= a <= b
                if(a==0 && b==0) {
                    //a = b = 0, 即 f
                    return NNF(children[1]);
                } else {
                    //0<a<=b; 0<a<b; 0<=a<b. GLOBALLY a..b f (b>0) >> NOT FINALLY a..b NOT f
                    //>> NOT(true UNTIL a..b NOT f) >> false RELEASES a..b f
                    return new SpecExp(Operator.B_RELEASES, getFalseSpec(), range, NNF(children[1]));
                }
            } else {
                //a>=0 and b < 0(infinite), GLOBALLY a..b f (b<0) >> GLOBALLY a..a (GLOBALLY f)
                //>> FINALLY a..a (GLOBALLY f) >> true UNTIL a..a (GLOBALLY f)
                Spec tempSpec = NNF(new SpecExp(Operator.GLOBALLY, children[1]));
                SpecRange newRange = new SpecRange(a, a);

                return new SpecExp(Operator.B_UNTIL, getTrueSpec(), newRange, tempSpec);
            }
        }

        //有界时态算子B_UNTIL, B_RELEASES, f U/R a..b g
        if(op==Operator.B_UNTIL || op==Operator.B_RELEASES) {
            LoggerUtil.info("B_UNTIL/B_RELEASES, f:{}, U/R:{}, g:{}",
                    children[0], children[1], children[2]);

            SpecRange range = (SpecRange) children[1];
            int a = range.getFrom();
            int b = range.getTo();

            if(a < 0) {
                //a < 0, 有界时态算子的下界不应小于0
                LoggerUtil.error("有界时态算子f U/R a..b g的下界a小于0.");
                throw new ModelCheckException("The lower bound of " + spec + " cannot be less than 0.");
            }
            //a >= 0, 有界时态算子的下界大于等于0时
            if(b >= 0) {
                //b >= 0
                if(a > b) {
                    //a > b, 下界不应大于上界
                    LoggerUtil.error("有界时态算子f U/R a..b g的下界a大于上界b.");
                    throw new ModelCheckException("The lower bound of " + spec +
                            " must be no lager than the upper bound.");
                }
                //0 <= a <= b
                if(a==0 && b==0) {
                    //a = b = 0, 即 g
                    return NNF(children[2]);
                } else {
                    //0<a<=b; 0<a<b; 0<=a<b. f U/R a..b g (b>0)
                    return new SpecExp(op, NNF(children[0]), range, NNF(children[2]));
                }
            } else {
                //a>=0 and b < 0(infinite), f U/R a..b g (b<0) >> f U/R a..a (f U/R g)
                Spec tempSpec1 = NNF(children[0]);
                Spec tempSpec2 = NNF(children[2]);
                Spec tempSpec3 = new SpecExp(op, tempSpec1, tempSpec2);
                SpecRange newRange = new SpecRange(a, a);

                return new SpecExp(op, tempSpec1, newRange, tempSpec3);
            }
        }

        //认知模态算子
        if(op==Operator.KNOW
                || op==Operator.NKNOW
                || op==Operator.SKNOW
                || op==Operator.NSKNOW) {
            return new SpecExp(op, children[0], NNF(children[1]));
        }

        //NOT
        if(op == Operator.NOT) {
            Spec f = children[0];
            if(!(f instanceof SpecExp))
                return spec;

            SpecExp specExp = (SpecExp) f;
            Operator fOp = specExp.getOperator();
            Spec[] fChildren = specExp.getChildren();

            //NOT NOT c1 >> c1
            if(fOp == Operator.NOT) {
                return NNF(fChildren[0]);
            }

            //NOT(c1 AND c2) >> NOT c1 OR NOT c2
            if(fOp == Operator.AND) {
                return new SpecExp(Operator.OR, NNF(new SpecExp(Operator.NOT, fChildren[0])),
                        NNF(new SpecExp(Operator.NOT, fChildren[1])));
            }
            //NOT(c1 OR c2) >> NOT c1 AND NOT c2
            if(fOp == Operator.OR) {
                return new SpecExp(Operator.AND, NNF(new SpecExp(Operator.NOT, fChildren[0])),
                        NNF(new SpecExp(Operator.NOT, fChildren[1])));
            }

            //NOT(c1 IMPLIES c2) >> c1 AND NOT c2
            if(fOp == Operator.IMPLIES) {
                return new SpecExp(Operator.AND, NNF(fChildren[0]),
                        NNF(new SpecExp(Operator.NOT, fChildren[1])));
            }
            //a xor b = a'b + ab'
            //NOT(c1 IFF/XNOR c2) >> (NOT c1 AND c2) OR (c1 AND NOT c2)
            if(fOp==Operator.IFF || fOp==Operator.XNOR) {
                Spec tempSpec1 = new SpecExp(Operator.AND,
                        NNF(new SpecExp(Operator.NOT, fChildren[0])), NNF(fChildren[1]));
                Spec tempSpec2 = new SpecExp(Operator.AND,
                        NNF(fChildren[0]), NNF(new SpecExp(Operator.NOT, fChildren[1])));
                return new SpecExp(Operator.OR, tempSpec1, tempSpec2);
            }
            //NOT(c1 XOR c2) >> c1 IFF c2
            if(fOp == Operator.XOR) {
                return new SpecExp(Operator.IFF, NNF(fChildren[0]), NNF(fChildren[1]));
            }

            //路径量词E和A
            //NOT EE c1 >> AA NOT c1
            if(fOp == Operator.EE) {
                return new SpecExp(Operator.AA, NNF(new SpecExp(Operator.NOT, fChildren[0])));
            }
            //NOT AA c1 >> EE NOT c1
            if(fOp == Operator.AA) {
                return new SpecExp(Operator.EE, NNF(new SpecExp(Operator.NOT, fChildren[0])));
            }

            //NOT(CAN_ENFORCE c1) >> CANNOT_AVOID NOT c1
            if(fOp == Operator.CAN_ENFORCE) {
                fChildren[fChildren.length-1] = NNF(new SpecExp(Operator.NOT, fChildren[fChildren.length-1]));
                return new SpecExp(Operator.CANNOT_AVOID, fChildren);
            }
            //NOT(CANNOT_AVOID) c1 >> CAN_ENFORCE NOT c1
            if(fOp == Operator.CANNOT_AVOID) {
                fChildren[fChildren.length-1] = NNF(new SpecExp(Operator.NOT, fChildren[fChildren.length-1]));
                return new SpecExp(Operator.CAN_ENFORCE, fChildren);
            }

            //NOT NEXT c1 >> NEXT NOT c1
            if(fOp == Operator.NEXT) {
                return new SpecExp(fOp, NNF(new SpecExp(Operator.NOT, fChildren[0])));
            }

            //NOT(FINALLY f) >> NOT(true UNTIL f) >> false RELEASES NOT f
            if(fOp == Operator.FINALLY) {
                Spec negC1 = NNF(new SpecExp(Operator.NOT, fChildren[0]));
                return new SpecExp(Operator.RELEASES, getFalseSpec(), negC1);
            }
            //NOT(G f) >> true UNTIL NOT f
            if(fOp == Operator.GLOBALLY) {
                Spec negC1 = NNF(new SpecExp(Operator.NOT, fChildren[0]));
                return new SpecExp(Operator.UNTIL, getTrueSpec(), negC1);
            }

            //NOT(FINALLY a..b f)
            if(fOp == Operator.B_FINALLY) {
                SpecRange range = (SpecRange) fChildren[0];
                int a = range.getFrom();
                int b = range.getTo();

                if(a < 0) {
                    //a < 0, 有界时态算子的下界不应小于0
                    LoggerUtil.error("有界时态算子F a..b f的下界a小于0.");
                    throw new ModelCheckException("The lower bound of " + spec + " cannot be less than 0.");
                }
                //a >= 0, 有界时态算子的下界大于等于0时
                if(b >= 0) {
                    //b >= 0
                    if(a > b) {
                        //a > b, 下界不应大于上界
                        LoggerUtil.error("有界时态算子F a..b f的下界a大于上界b.");
                        throw new ModelCheckException("The lower bound of " + spec + " must be no lager than the upper bound.");
                    }
                    //0 <= a <= b
                    if(a==0 && b==0) {
                        //a = b = 0, 即NOT f
                        return NNF(new SpecExp(Operator.NOT, fChildren[1]));
                    } else {
                        //0<a<=b; 0<a<b; 0<=a<b. NOT(FINALLY a..b f) (b>0) >> false RELEASES a..b NOT f
                        return new SpecExp(Operator.B_RELEASES, getFalseSpec(), range, NNF(new SpecExp(Operator.NOT, fChildren[1])));
                    }
                } else {
                    //a>=0 and b < 0(infinite), NOT(FINALLY a..b f) (b<0) >> NOT(true UNTIL a..a (FINALLY f))
                    //>> false RELEASES a..a NOT(FINALLY f) >> false RELEASES a..a (false RELEASES NOT f)
                    Spec tempSpec = NNF(new SpecExp(Operator.RELEASES, getFalseSpec(), NNF(new SpecExp(Operator.NOT, fChildren[1]))));
                    SpecRange newRange = new SpecRange(a, a);

                    return new SpecExp(Operator.B_RELEASES, getFalseSpec(), newRange, tempSpec);
                }
            }
            //NOT(GLOBALLY a..b f)
            if(fOp == Operator.B_GLOBALLY) {
                SpecRange range = (SpecRange) fChildren[0];
                int a = range.getFrom();
                int b = range.getTo();

                if(a < 0) {
                    //a < 0, 有界时态算子的下界不应小于0
                    LoggerUtil.error("有界时态算子G a..b f的下界a小于0.");
                    throw new ModelCheckException("The lower bound of " + spec + " cannot be less than 0.");
                }
                //a >= 0, 有界时态算子的下界大于等于0时
                if(b >= 0) {
                    //b >= 0
                    if(a > b) {
                        //a > b, 下界不应大于上界
                        LoggerUtil.error("有界时态算子G a..b f的下界a大于上界b.");
                        throw new ModelCheckException("The lower bound of " + spec +
                                " must be no lager than the upper bound.");
                    }
                    //0 <= a <= b
                    if(a==0 && b==0) {
                        //a = b = 0, 即NOT f
                        return NNF(new SpecExp(Operator.NOT, fChildren[1]));
                    } else {
                        //0<a<=b; 0<a<b; 0<=a<b. NOT(GLOBALLY a..b f) (b>0) >> FINALLY a..b NOT f
                        //>> true UNTIL a..b NOT f
                        return new SpecExp(Operator.B_UNTIL,
                                getTrueSpec(), range, NNF(new SpecExp(Operator.NOT, fChildren[1])));
                    }
                } else {
                    //a>=0 and b < 0(infinite), NOT(GLOBALLY a..b f) (b<0) >> NOT(GLOBALLY a..a (GLOBALLY f))
                    //>> FINALLY a..a (FINALLY NOT f) >> true UNTIL a..a (true UNTIL NOT f)
                    Spec tempSpec = NNF(new SpecExp(Operator.UNTIL,
                            getTrueSpec(), new SpecExp(Operator.NOT, fChildren[1])));
                    SpecRange newRange = new SpecRange(a, a);

                    return new SpecExp(Operator.B_UNTIL, getTrueSpec(), newRange, tempSpec);
                }
            }

            //NOT(f UNTIL g) >> NOT f RELEASES NOT g
            if(fOp == Operator.UNTIL) {
                return new SpecExp(Operator.RELEASES,
                        NNF(new SpecExp(Operator.NOT, fChildren[0])), NNF(new SpecExp(Operator.NOT, fChildren[1])));
            }
            //NOT(f RELEASES g) >> NOT f UNTIL g
            if(fOp == Operator.RELEASES) {
                return new SpecExp(Operator.UNTIL,
                        NNF(new SpecExp(Operator.NOT, fChildren[0])), NNF(new SpecExp(Operator.NOT, fChildren[1])));
            }

            //NOT(f UNTIL a..b g)
            if(fOp == Operator.B_UNTIL) {
                LoggerUtil.info("B_UNTIL, f:{}, U:{}, g:{}",
                        fChildren[0], fChildren[1], fChildren[2]);

                SpecRange range = (SpecRange) fChildren[1];
                int a = range.getFrom();
                int b = range.getTo();

                if(a < 0) {
                    //a < 0, 有界时态算子的下界不应小于0
                    LoggerUtil.error("有界时态算子f U a..b g的下界a小于0.");
                    throw new ModelCheckException("The lower bound of " + spec + " cannot be less than 0.");
                }
                //a >= 0, 有界时态算子的下界大于等于0时
                if(b >= 0) {
                    //b >= 0
                    if(a > b) {
                        //a > b, 下界不应大于上界
                        LoggerUtil.error("有界时态算子f U a..b g的下界a大于上界b.");
                        throw new ModelCheckException("The lower bound of " + spec +
                                " must be no lager than the upper bound.");
                    }
                    //0 <= a <= b
                    if(a==0 && b==0) {
                        //a = b = 0, 即 NOT g
                        return NNF(new SpecExp(Operator.NOT, fChildren[2]));
                    } else {
                        //0<a<=b; 0<a<b; 0<=a<b. NOT(f UNTIL a..b g) (b>0) >> NOT f RELEASES a..b NOT g
                        return new SpecExp(Operator.B_RELEASES,
                                NNF(new SpecExp(Operator.NOT, fChildren[0])), range, NNF(new SpecExp(Operator.NOT, fChildren[2])));
                    }
                } else {
                    //a>=0 and b < 0(infinite), NOT(f UNTIL a..b g) (b<0) >> NOT f RELEASES a..b NOT g
                    //>> NOT f RELEASES a..a (NOT f RELEASES NOT g)
                    Spec tempSpec1 = NNF(new SpecExp(Operator.NOT, fChildren[0]));
                    Spec tempSpec2 = NNF(new SpecExp(Operator.NOT, fChildren[2]));
                    Spec tempSpec3 = new SpecExp(Operator.RELEASES, tempSpec1, tempSpec2);
                    SpecRange newRange = new SpecRange(a, a);

                    return new SpecExp(Operator.B_RELEASES, tempSpec1, newRange, tempSpec3);
                }
            }
            //NOT(f RELEASES a..b g)
            if(fOp == Operator.B_RELEASES) {
                LoggerUtil.info("B_RELEASES, f:{}, R:{}, g:{}",
                        fChildren[0], fChildren[1], fChildren[2]);

                SpecRange range = (SpecRange) fChildren[1];
                int a = range.getFrom();
                int b = range.getTo();

                if(a < 0) {
                    //a < 0, 有界时态算子的下界不应小于0
                    LoggerUtil.error("有界时态算子f R a..b g的下界a小于0.");
                    throw new ModelCheckException("The lower bound of " + spec + " cannot be less than 0.");
                }
                //a >= 0, 有界时态算子的下界大于等于0时
                if(b >= 0) {
                    //b >= 0
                    if(a > b) {
                        //a > b, 下界不应大于上界
                        LoggerUtil.error("有界时态算子f R a..b g的下界a大于上界b.");
                        throw new ModelCheckException("The lower bound of " + spec +
                                " must be no lager than the upper bound.");
                    }
                    //0 <= a <= b
                    if(a==0 && b==0) {
                        //a = b = 0, 即 NOT g
                        return NNF(new SpecExp(Operator.NOT, fChildren[2]));
                    } else {
                        //0<a<=b; 0<a<b; 0<=a<b. NOT(f RELEASES a..b g) (b>0) >> NOT f UNTIL a..b NOT g
                        return new SpecExp(Operator.B_UNTIL, NNF(new SpecExp(Operator.NOT, fChildren[0])),
                                range, NNF(new SpecExp(Operator.NOT, fChildren[2])));
                    }
                } else {
                    //a>=0 and b < 0(infinite), NOT(f RELEASES a..b g) (b<0) >> NOT f UNTIL a..b NOT g
                    //>> NOT f UNTIL a..a (NOT f UNTIL NOT g)
                    Spec tempSpec1 = NNF(new SpecExp(Operator.NOT, fChildren[0]));
                    Spec tempSpec2 = NNF(new SpecExp(Operator.NOT, fChildren[2]));
                    Spec tempSpec3 = new SpecExp(Operator.UNTIL, tempSpec1, tempSpec2);
                    SpecRange newRange = new SpecRange(a, a);

                    return new SpecExp(Operator.B_UNTIL, tempSpec1, newRange, tempSpec3);
                }
            }

            if(fOp == Operator.KNOW) {
                //NOT(i KNOW c1) >> i NKNOW NOT c1
                return new SpecExp(Operator.NKNOW, fChildren[0], NNF(new SpecExp(Operator.NOT, fChildren[1])));
            }
            if(fOp == Operator.NKNOW) {
                //NOT(i NKNOW c1) >> i KNOW NOT c1
                return new SpecExp(Operator.NKNOW, fChildren[0], NNF(new SpecExp(Operator.NOT, fChildren[1])));
            }
            if(fOp == Operator.SKNOW) {
                //NOT(i SKNOW c1) >> i NSKNOW NOT c1
                return new SpecExp(Operator.NSKNOW, fChildren[0], NNF(new SpecExp(Operator.NOT, fChildren[1])));
            }
            if(fOp == Operator.NSKNOW) {
                //NOT(i NSKNOW c1) >> i SKNOW NOT c1
                return new SpecExp(Operator.SKNOW, fChildren[0], NNF(new SpecExp(Operator.NOT, fChildren[1])));
            }
        }
        throw new ModelCheckException("Failed to construct the Negation Normal Form of " + spec);
    }

    /**
     *
     * @return TRUE 规约
     */
    public static Spec getTrueSpec() {
        Spec[] specs = Env.loadSpecString("LTLSPEC TRUE ;");

        assert (specs != null) && (specs.length > 0);

        return specs[0];
    }

    /**
     *
     * @return FALSE 规约
     */
    public static Spec getFalseSpec() {
        Spec[] specs = Env.loadSpecString("LTLSPEC FALSE ;");

        assert (specs != null) && (specs.length > 0);

        return specs[0];
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
        if(specStr == null) return "";
        String res = specStr.replaceAll("main.", "");
        if (delTrue) {
            res = res.replace("#[TRUE], \n", "");
            res = res.replace("#[TRUE]", "");
            res = res.replace("TRUE, \n", "");
            res = res.replace("TRUE", "");
        }
        return res;
    }
}
