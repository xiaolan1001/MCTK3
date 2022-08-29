package cn.edu.hqu.mctk;

import edu.wis.jtlv.env.Env;
import edu.wis.jtlv.env.module.SMVModule;
import edu.wis.jtlv.env.spec.Operator;
import edu.wis.jtlv.env.spec.Spec;
import edu.wis.jtlv.env.spec.SpecException;
import edu.wis.jtlv.env.spec.SpecExp;
import edu.wis.jtlv.lib.mc.ATLstar.LoggerUtil;
import edu.wis.jtlv.lib.mc.ATLstar.SpecUtil;
import edu.wis.jtlv.lib.mc.RTCTLs.RTCTLs_ModelCheckAlg;
import edu.wis.jtlv.old_lib.mc.ModelCheckException;
import org.junit.Test;

import java.io.IOException;
import java.util.Scanner;

public class SpecUtilTest {
    /**
     * 测试CTL公式的加载(mux-sem.smv)
     * @throws Exception 异常处理
     */
    @Test
    public void testCTL() throws Exception{
        //不考虑NuSMV格式兼容性, 即新实例创建时不会增加"running"变量
        SMVModule.initModulesWithoutRunningVar();
        //通过文件名加载module
        Env.loadModule("testcases/mux-sem.smv");
        SMVModule main = (SMVModule) Env.getModule("main");

        //为toString程序设置打印模式
        main.setFullPrintingMode(true);
        for (SMVModule m : main.getAllInstances()) {
            m.setFullPrintingMode(true);
        }
        //******************总结******************
        //CTL公式路径量词后要紧接一个时态算子例如EF
        //******************总结******************
        String to_parse = "";
        //to_parse += "SPEC\n" + " !((proc[1].loc = 3) & (proc[2].loc = 3)) \n"; //CTL done
        //to_parse += "SPEC\n" + " !((proc[1].loc = 3) & (proc[2].loc = 2)) \n"; //CTL done
        //to_parse += "SPEC AG proc[1].loc = 3\n";                               //CTL done
        //to_parse += "SPEC AG EF proc[1].loc = 3\n";                            //CTL done
        //to_parse += "SPEC EX proc[2].loc = 2\n";                               //CTL done
        to_parse += "SPEC !EF proc[2].loc = 2\n";                              //CTL done
        //to_parse += "SPEC ! E F proc[2].loc = 2\n";                            //CTL error 'EF'
        Spec[] specs = Env.loadSpecString(to_parse);
        assert (specs != null) && (specs.length > 0);
        for (Spec spec : specs) {
            LoggerUtil.info("spec:{}", SpecUtil.simplifySpecString(spec, false));
        }
    }

    /**
     * 测试RTCTL*的公式加载(mwOven.smv)
     * @throws Exception 异常处理
     */
    @Test
    public void testRTCTLs() throws Exception {
        //通过文件名加载module
        Env.loadModule("testcases/mwOven.smv");
        SMVModule main = (SMVModule) Env.getModule("main");
        //为toString程序设置打印模式
        main.setFullPrintingMode(true);

        //******************总结******************
        //在RTCTL*规约中, []被用于包含一组智能体集合, 即[agent_list]
        //******************总结******************
        String to_parse = "";

        //to_parse += "SPEC !E[TRUE U  EX(start & close & heat & !error)]";            //CTL done

//        to_parse += "SPEC !EG ( (!start & !close & !heat & !error) |  " +
//                "(start & !close & !heat & error) | (start & close & !heat & error) );"; //CTL done

//        to_parse += "SPEC (!EG ( (!start & !close & !heat & !error) |  " +
//                "(start & !close & !heat & error) | (start & close & !heat & error) )) | !EG !heat;"; //CTL done

        //to_parse += "SPEC ! EF (!close & start & (EF heat) & (EG error));";          //CTL done

        //to_parse += "SPEC !E[TRUE BU 3..12 EX(start & close & heat & !error)]";      //CTL(RTCTL) done

        //to_parse += "RTCTL*SPEC A(G((!close & start) -> A(G !heat | F !error)))";    //RTCTL* done

        //to_parse += "RTCTL*SPEC !E(TRUE U (start & close & heat & !error));";        //RTCTL* done

        //to_parse += "RTCTL*SPEC (start -> close);";                                  //RTCTL* done

        //to_parse += "RTCTL*SPEC A G(start -> A F heat);";                            //RTCTL* done

        //to_parse += "RTCTL*SPEC A A(start -> close)";                                //RTCTL* done

        //to_parse += "RTCTL*SPEC E A(start -> close)";                                //RTCTL* done

        //to_parse += "RTCTL*SPEC !A (E X close)";                                     //RTCTL* done

        //to_parse +="RTCTL*SPEC !A close";                                            //RTCTL* error 文法解析问题

        //to_parse +="RTCTL*SPEC !A (E(start | close))";                               //RTCTL* error 文法解析问题

        to_parse += "RTCTL*SPEC E(TRUE BU 0..-1 E X start)";                         //RTCTL* error '-1'

        //to_parse += "RTCTL*SPEC [start -> close];";                                  //RTCTL* error '['

        //to_parse += "RTCTL*SPEC !E[TRUE U (start & close & heat & !error)];";        //RTCTL* error '['

        //to_parse += "ATL*SPEC A(G((!close & start) -> A(G !heat | F !error)))";      //error 无法解析ATL*公式

        //to_parse += "SPEC ! E F (!close & start & E((F heat) & (G error)));";        //CTL error 'EF'

        //to_parse += "SPEC A G( (!close & start) -> A((G !heat) | (F !error)) );";    //CTL error 'AG'

        //加载规约
        Spec[] specs = Env.loadSpecString(to_parse);

        assert (specs != null) && (specs.length > 0);
        for (Spec spec : specs) {
            LoggerUtil.info("{}, spec:{}", spec instanceof SpecExp, SpecUtil.simplifySpecString(spec, false));
        }
    }

    /**
     * 测试RTCTL*的公式加载(bit_transmission.smv)
     * @throws Exception 将异常往上抛
     */
    @Test
    public void testRTCTLs2() throws Exception {
        //通过文件名加载module
        Env.loadModule("testcases/bit_transmission.smv");
        SMVModule main = (SMVModule) Env.getModule("main");
        //为toString程序设置打印模式
        main.setFullPrintingMode(true);

        String toParse = "";
        //toParse += "RTCTL*SPEC A G((receiver.state=r0 | receiver.state=r1) -> A F sender.ack);"; //done
        //toParse += "RTCTL*SPEC !E(G(receiver.state=r0 | receiver.state=r1) -> (F sender.ack));"; //done
        toParse += "RTCTL*SPEC A(F(sender KNOW (receiver.state=r0 | receiver.state=r1)));";      //done
        //toParse += "RTCTL*SPEC A F(sender KNOW (receiver.state=r0 | receiver.state=r1));";       //done

        //加载规约
        Spec[] specs = Env.loadSpecString(toParse);

        assert (specs != null) && (specs.length > 0);
        for (Spec spec : specs) {
            LoggerUtil.info("{}, spec:{}", spec instanceof SpecExp, SpecUtil.simplifySpecString(spec, false));
        }
    }

    /**
     * 测试RTCTL*的公式加载(dc3)
     * @throws Exception 将异常往上抛
     */
    @Test
    public void testRTCTLs3() throws Exception {
        //通过文件名加载module
        Env.loadModule("testcases/dc3.smv");
        SMVModule main = (SMVModule) Env.getModule("main");
        //为toString程序设置打印模式
        main.setFullPrintingMode(true);

        String toParse = "";
        //toParse += "RTCTL*SPEC !dc1.paid -> A( G( (dc1 KNOW (!dc1.paid & !dc2.paid & !dc3.paid)) | " +
        //        "( (dc1 KNOW (dc2.paid | dc3.paid)) & !(dc1 KNOW dc2.paid) & !(dc1 KNOW dc3.paid) ) ) );"; //done

        //toParse += "RTCTL*SPEC A(X (dc1 KNOW dc2.said));"; //done
        //toParse += "RTCTL*SPEC E(F(dc1 KNOW dc2.said));";  //done
        //toParse += "RTCTL*SPEC !E(E( X(dc2.said))) ;";     //done
        //toParse += "RTCTL*SPEC (dc1 KNOW dc2.said);";      //done
        //toParse += "RTCTL*SPEC !dc1.paid -> A( G(dc1 KNOW (!dc1.paid & !dc2.paid & !dc3.paid)));";    //done
        //toParse += "RTCTL*SPEC (G (!dc1.paid -> ((dc1 KNOW (!dc1.paid & !dc2.paid & !dc3.paid)))));"; //done
        //toParse += "RTCTL*SPEC (G (!dc1.paid -> ((dc1 KNOW (!dc1.paid & !dc2.paid & !dc3.paid)) |" +
        //        " ( (dc1 KNOW (dc2.paid | dc3.paid)) & !(dc1 KNOW dc2.paid) & !(dc1 KNOW dc3.paid) ))));";  //done

        //toParse += "RTCTL*SPEC !dc1.paid -> A( G( (dc1 KNOW (!dc1.paid & !dc2.paid & !dc3.paid)) |" +
        //        " ( (dc1 KNOW (dc2.paid | dc3.paid)) & !(dc1 KNOW dc2.paid) & !(dc1 KNOW dc3.paid) ) ) );"; //done

        toParse += "RTCTL*SPEC  <dc1, dc2> (BF 6..13 dc2.paid );"; //RTATL* done
        //toParse += "RTCTL*SPEC <dc1,dc2,dc3,main>  dc1.paid | dc2.paid | dc3.paid;"; //RTATL* done
        //toParse += "RTCTL*SPEC <dc1,main> TRUE U dc1.paid ;"; //RTATL* done

        //加载规约
        Spec[] specs = Env.loadSpecString(toParse);

        assert (specs != null) && (specs.length > 0);
        for (Spec spec : specs) {
            //使用simplifySpecString()方法简化公式, 带有main智能体的公式简化后有点小问题
            LoggerUtil.info("{}, spec:{}", spec instanceof SpecExp, SpecUtil.simplifySpecString(spec, false));
        }
    }

    /**
     * 测试NNF函数, NNF作用是指定规约spec生成该规约的否定范式(mwOven.smv)
     * @throws Exception 异常处理
     */
    @Test
    public void testNNF() throws Exception {
        //通过文件名加载module
        Env.loadModule("testcases/mwOven.smv");
        SMVModule main = (SMVModule) Env.getModule("main");
        //为toString程序设置打印模式
        main.setFullPrintingMode(true);

        //******************总结******************
        //CTL规约中路径量词与时态算子一起解析,例如AU、EF等,故不能用NNF函数生成否定范式
        //SpecBDD类对象例如!(start & close)会变为(start=1 -> close=0)
        //******************总结******************
        StringBuilder to_parse = new StringBuilder(); //使用StringBuilder类操作字符串拼接效率更高

        //to_parse.append("RTCTL*SPEC E G TRUE;");                                             //RTCTL* done

        //to_parse.append("RTCTL*SPEC !E(TRUE U (start & close & heat & !error));");           //RTCTL* done

        //to_parse.append("RTCTL*SPEC !(start & close);");                                      //RTCTL* done

        //to_parse.append("RTCTL*SPEC E(TRUE U (start & close & heat & !error));");            //RTCTL* done

        //to_parse.append("LTLSPEC !G !heat;");                                                //LTL done

        //to_parse.append("RTCTL*SPEC A G(start -> A F heat);");                               //RTCTL* done

        //to_parse.append("RTCTL*SPEC !E(TRUE BU 3..12 E X(start & close & heat & !error));");  //RTCTL* done

        //to_parse.append("RTCTL*SPEC E(TRUE BU 3..12 E X start);");                            //RTCTL* done

        //to_parse.append("RTCTL*SPEC E(TRUE BU 0..0 E X start);");                             //RTCTL* done

        //to_parse.append("RTCTL*SPEC E(G E X start);");                                        //RTCTL* done

        //to_parse.append("RTCTL*SPEC E(BG 3..12 E X start);");                                 //RTCTL* done

        //to_parse.append("RTCTL*SPEC !E(BF 3..12 E X start);");                                //RTCTL* done

        //to_parse.append("RTCTL*SPEC A(BG 3..12 start);");                                     //RTCTL* done

        //to_parse.append("RTCTL*SPEC A(BG 3..12 start) -> close;");                            //RTCTL* done

        //to_parse.append("RTCTL*SPEC A(BG 3..12 start) xnor G close;");                        //RTCTL* done

        //to_parse.append("RTCTL*SPEC A(BG 3..12 start) xor ! G close;");                       //RTCTL* done

        //to_parse.append("RTCTL*SPEC E close;");                                               //RTCTL* done

        //to_parse.append("RTCTL*SPEC A A G close;");                                           //RTCTL* done

        to_parse.append("RTCTL*SPEC E(TRUE BU 0..-1 E X start);");                           //RTCTL* done

        //加载规约
        Spec[] specs = Env.loadSpecString(to_parse.toString());

        assert (specs != null) && (specs.length > 0);
        for (Spec spec : specs) {
            LoggerUtil.info("{}, NNF(spec):{}", spec instanceof SpecExp, SpecUtil.simplifySpecString(SpecUtil.NNF(spec), false));
        }
    }

    /**
     * 测试NNF函数(bit_transmission.smv)
     * @throws Exception
     */
    @Test
    public void testNNF2() throws Exception {
        //通过文件名加载module
        Env.loadModule("testcases/bit_transmission.smv");
        SMVModule main = (SMVModule) Env.getModule("main");
        //为toString程序设置打印模式
        main.setFullPrintingMode(true);

        String toParse = "";
        //toParse += "RTCTL*SPEC A G((receiver.state=r0 | receiver.state=r1) -> A F sender.ack);"; //RTCTL* done
        //toParse += "RTCTL*SPEC !E(G(receiver.state=r0 | receiver.state=r1) -> (F sender.ack));"; //RTCTL* done
        //toParse += "RTCTL*SPEC A(F(sender KNOW (receiver.state=r0 | receiver.state=r1)));";      //RTCTL*K done
        toParse += "RTCTL*SPEC A F(sender KNOW (receiver.state=r0 | receiver.state=r1));";       //RTCTL*K done

        //加载规约
        Spec[] specs = Env.loadSpecString(toParse);

        assert (specs != null) && (specs.length > 0);
        for (Spec spec : specs) {
            LoggerUtil.info("{}, NNF(spec):{}", spec instanceof SpecExp, SpecUtil.simplifySpecString(SpecUtil.NNF(spec), false));
        }
    }

    /**
     * 测试NNF函数(dc3.smv)
     * @throws Exception
     */
    @Test
    public void testNNF3() throws Exception {
        //通过文件名加载module
        Env.loadModule("testcases/dc3.smv");
        SMVModule main = (SMVModule) Env.getModule("main");
        //为toString程序设置打印模式
        main.setFullPrintingMode(true);

        String toParse = "";
        //toParse += "RTCTL*SPEC !dc1.paid -> A( G( (dc1 KNOW (!dc1.paid & !dc2.paid & !dc3.paid)) | " +
        //        "( (dc1 KNOW (dc2.paid | dc3.paid)) & !(dc1 KNOW dc2.paid) & !(dc1 KNOW dc3.paid) ) ) );"; //done

        //toParse += "RTCTL*SPEC A(X (dc1 KNOW dc2.said));"; //done
        //toParse += "RTCTL*SPEC E(F(dc1 KNOW dc2.said));";  //done
        //toParse += "RTCTL*SPEC !E(E( X(dc2.said))) ;";     //done
        //toParse += "RTCTL*SPEC (dc1 KNOW dc2.said);";      //done
        //toParse += "RTCTL*SPEC !dc1.paid -> A( G(dc1 KNOW (!dc1.paid & !dc2.paid & !dc3.paid)));";    //done
        //toParse += "RTCTL*SPEC (G (!dc1.paid -> ((dc1 KNOW (!dc1.paid & !dc2.paid & !dc3.paid)))));"; //done
        //toParse += "RTCTL*SPEC (G (!dc1.paid -> ((dc1 KNOW (!dc1.paid & !dc2.paid & !dc3.paid)) |" +
        //        " ( (dc1 KNOW (dc2.paid | dc3.paid)) & !(dc1 KNOW dc2.paid) & !(dc1 KNOW dc3.paid) ))));";  //done

        //toParse += "RTCTL*SPEC !dc1.paid -> A( G( (dc1 KNOW (!dc1.paid & !dc2.paid & !dc3.paid)) |" +
        //        " ( (dc1 KNOW (dc2.paid | dc3.paid)) & !(dc1 KNOW dc2.paid) & !(dc1 KNOW dc3.paid) ) ) );"; //done

        //toParse += "RTCTL*SPEC  <dc1, dc2> (BF 6..13 dc2.paid );"; //RTATL* done
        //toParse += "RTCTL*SPEC <dc1,dc2,dc3,main>  dc1.paid | dc2.paid | dc3.paid;"; //RTATL* wrong <A>是一元算子
        //toParse += "RTCTL*SPEC <dc1,dc2,dc3,main>  (dc1.paid | dc2.paid | dc3.paid);"; //RTATL* done
        //toParse += "RTCTL*SPEC <dc1,main> (TRUE U dc1.paid) ;"; //RTATL* done
        //toParse += "RTCTL*SPEC <dc1,main> TRUE U dc1.paid ;";   //RTATL* wrong <dc1,main> TRUE, <A>的优先级比U高
        //toParse += "RTCTL*SPEC [dc1,main] (G dc1.paid) ;";      //RTATL* done
        toParse += "RTCTL*SPEC ![dc1,main] (G dc1.paid);";       //RTATL* done

        //加载规约
        Spec[] specs = Env.loadSpecString(toParse);

        assert (specs != null) && (specs.length > 0);
        for (Spec spec : specs) {
            LoggerUtil.info("{}, NNF(spec):{}", spec instanceof SpecExp, SpecUtil.NNF(spec));
        }
    }

    /**
     * 比较SpecUtil类中的NNF()方法和RTCTLs_ModelCheckAlg类中的NNF()方法(mwOven.smv)
     * @throws Exception 异常处理
     */
    @Test
    public void testNNFCompare() throws Exception {
        //通过文件名加载module
        Env.loadModule("testcases/mwOven.smv");
        SMVModule main = (SMVModule) Env.getModule("main");
        //为toString程序设置打印模式
        main.setFullPrintingMode(true);

        StringBuilder toParse = new StringBuilder("RTCTL*SPEC E(TRUE BU 0..0 E X start);");

        //加载规约
        Spec[] specs = Env.loadSpecString(toParse.toString());

        assert (specs != null) && (specs.length > 0);
        for (Spec spec : specs) {
            LoggerUtil.info("NNF(spec):{}",
                    SpecUtil.simplifySpecString(SpecUtil.NNF(spec), false)); //E[E[X start=1]]

            LoggerUtil.info("RTCTLs_ModelCheckAlg.NNF(spec):{}",
                    SpecUtil.simplifySpecString(RTCTLs_ModelCheckAlg.NNF(spec), false)); //E[0..0]
        }
    }
}