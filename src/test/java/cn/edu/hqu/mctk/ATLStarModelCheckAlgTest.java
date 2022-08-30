package cn.edu.hqu.mctk;

import edu.wis.jtlv.env.Env;
import edu.wis.jtlv.env.core.spec.InternalSpecLanguage;
import edu.wis.jtlv.env.module.SMVModule;
import edu.wis.jtlv.env.spec.Spec;
import edu.wis.jtlv.lib.mc.ATLstar.ATLStarModelCheckAlg;
import edu.wis.jtlv.lib.mc.ATLstar.LoggerUtil;
import org.junit.Test;

public class ATLStarModelCheckAlgTest {
    /**
     * 使用RTATL*模型检测算法验证LTL公式(testcases/mwOven.smv)
     * @throws Exception 异常处理
     */
    @Test
    public void testLTLCheck() throws Exception {
        //通过文件名加载module
        Env.loadModule("testcases/mwOven.smv");
        SMVModule main = (SMVModule) Env.getModule("main");
        //为toString程序设置打印模式
        main.setFullPrintingMode(true);

        StringBuilder to_parse = new StringBuilder();

        //to_parse.append("LTLSPEC start;"); //invalid
        //to_parse.append("LTLSPEC  F start -> G heat;"); //invalid
        to_parse.append("LTLSPEC TRUE;"); //done

        //加载规约
        Spec[] specs = Env.loadSpecString(to_parse.toString());

        assert (specs != null) && (specs.length > 0);
        for (Spec spec : specs) {
            if (spec.getLanguage() == InternalSpecLanguage.LTL) {
                ATLStarModelCheckAlg checker = new ATLStarModelCheckAlg(main, spec);
                checker.preAlgorithm();
                LoggerUtil.info(checker.doAlgorithm().resultString());
                checker.postAlgorithm();
            }
        }
    }

    /**
     * 使用RTATL*模型检测算法验证RTCTL*公式(testcases/mwOven.smv)
     * @throws Exception 异常处理
     */
    @Test
    public void testRTCTLsCheck() throws Exception {
        Env.loadModule("testcases/mwOven.smv");
        LoggerUtil.info("========= Loading Modules ==========");
        SMVModule main = (SMVModule) Env.getModule("main");
        main.setFullPrintingMode(true);
        LoggerUtil.info("========= DONE Loading Modules ==========");

        String toParse = "";
        //toParse += "RTCTL*SPEC !E(TRUE U  E X(start & close & heat & !error));";  //invalid  false
        //toParse += "RTCTL*SPEC A(G((!close & start) -> A(G !heat | F !error)));"; //done true
        //toParse += "RTCTL*SPEC ! E(X start);";                                    //invalid false
        //toParse += "RTCTL*SPEC ! E(F start);";                                    //invalid false
        //toParse += "RTCTL*SPEC A( G start);";                                     //invalid false
        //toParse += "RTCTL*SPEC F start -> G heat;";                               //invalid false
        //toParse += "RTCTL*SPEC !E( !start U E( TRUE U start));";                  //invalid false
        //toParse += "RTCTL*SPEC !E( (X close) U start);";                          //invalid false
        //toParse += "RTCTL*SPEC F!E((X start) R heat);";                           //done true
        //toParse += "RTCTL*SPEC !E( X start U (F error));";                        //invalid false
        //toParse += "RTCTL*SPEC !E( start U X error);";                            //invalid false
        //toParse += "RTCTL*SPEC !E((E X !start) U error);";                        //invalid false
        //toParse += "RTCTL*SPEC !E(TRUE U (F error));";                            //invalid false
        //toParse += "RTCTL*SPEC A( (X start) U !(X error));";                      //done true
        //toParse += "RTCTL*SPEC A start;";                                         //invalid false
        toParse += "RTCTL*SPEC A(start U E(heat));";                              //invalid true
        //toParse += "RTCTL*SPEC !E( X E(F heat) );";                               //invalid false
        //toParse += "RTCTL*SPEC (X start) & (TRUE U X start);";                    //invalid false

        //toParse += "RTCTL*SPEC A G(start -> A F heat);";                          //done true
        //toParse += "RTCTL*SPEC start;";                                           //invalid false
        //toParse += "RTCTL*SPEC TRUE;";                                            //done true

        Spec[] allSpecs = Env.loadSpecString(toParse);
        LoggerUtil.info("========= DONE Loading Specs ============");

        assert (allSpecs != null) && (allSpecs.length > 0);

        for (Spec spec : allSpecs) {
            if (spec.getLanguage() == InternalSpecLanguage.RTCTLs) {
                ATLStarModelCheckAlg checker = new ATLStarModelCheckAlg(main, spec);
                checker.preAlgorithm();
                LoggerUtil.info(checker.doAlgorithm().resultString());
                checker.postAlgorithm();
            }
        }
    }

    /**
     * 使用RTATL*模型检测算法验证RTCTL*公式(testcases/bit_transmission.smv)
     * @throws Exception 异常处理
     */
    @Test
    public void testRTCTLsCheck2() throws Exception {
        //通过文件名加载module
        Env.loadModule("testcases/bit_transmission.smv");
        SMVModule main = (SMVModule) Env.getModule("main");
        //为toString程序设置打印模式
        main.setFullPrintingMode(true);

        String toParse = "";
        //toParse += "RTCTL*SPEC A G((receiver.state=r0 | receiver.state=r1) -> A F sender.ack);"; //invalid
        toParse += "RTCTL*SPEC !E(G(receiver.state=r0 | receiver.state=r1) -> (F sender.ack));"; //invalid

        //加载规约
        Spec[] specs = Env.loadSpecString(toParse);

        assert (specs != null) && (specs.length > 0);
        for (Spec spec : specs) {
            if (spec.getLanguage() == InternalSpecLanguage.RTCTLs) {
                ATLStarModelCheckAlg checker = new ATLStarModelCheckAlg(main, spec);
                checker.preAlgorithm();
                LoggerUtil.info(checker.doAlgorithm().resultString());
                checker.postAlgorithm();
            }
        }
    }

    /**
     * 使用RTATL*模型检测算法验证RTCTL*K公式(testcases/bit_transmission_actions.smv)
     * @throws Exception 异常处理
     */
    @Test
    public void testRTCTLsKCheck2() throws Exception {
        //通过文件名加载module
        Env.loadModule("testcases/bit_transmission_actions.smv");
        SMVModule main = (SMVModule) Env.getModule("main");
        //为toString程序设置打印模式
        main.setFullPrintingMode(true);

        String toParse = "";
        //toParse += "RTCTL*SPEC A G((r.state=r0 | r.state=r1) -> A F s.ack);"; //done
        //toParse += "RTCTL*SPEC !E(G(r.state=r0 | r.state=r1) -> (F s.ack));"; //invalid
        //toParse += "RTCTL*SPEC E F(E G((r.state=r0 | r.state=r1) & !s.ack));"; //invalid

        //toParse += "RTCTL*SPEC A(F(s KNOW (r.state=r0 | r.state=r1)));"; //invalid
        //toParse += "RTCTL*SPEC A F(s.ack -> (s KNOW (r.state=r0 | r.state=r1)));"; //done
        //toParse += "RTCTL*SPEC !E(TRUE BU 10..15 ((s.bit=1 & s.ack) -> (s KNOW (r.state=r0))));"; //invalid
        toParse += "RTCTL*SPEC A G((s.bit=1 & s.ack) -> (s KNOW (r.state=r0)));"; //done

        //toParse += "RTCTL*SPEC !E BG 10..15 ((s.bit=1 & s.ack) -> (s SKNOW (r.state=r0)));"; //error 暂时还未编写SKNOW算法
        //toParse += "RTCTL*SPEC A BG 10..15 (s SKNOW (r.state=r0));"; //error 暂时还未编写SKNOW算法

        //加载规约
        Spec[] specs = Env.loadSpecString(toParse);

        assert (specs != null) && (specs.length > 0);
        for (Spec spec : specs) {
            if (spec.getLanguage() == InternalSpecLanguage.RTCTLs) {
                ATLStarModelCheckAlg checker = new ATLStarModelCheckAlg(main, spec);
                checker.preAlgorithm();
                LoggerUtil.info(checker.doAlgorithm().resultString());
                checker.postAlgorithm();
            }
        }
    }

    /**
     * 使用RTATL*模型检测算法验证RTCTL*公式(testcases/dc3.smv)
     * @throws Exception 异常处理
     */
    @Test
    public void testRTCTLsCheck3() throws Exception {
        //通过文件名加载module
        Env.loadModule("testcases/dc3.smv");
        SMVModule main = (SMVModule) Env.getModule("main");
        //为toString程序设置打印模式
        main.setFullPrintingMode(true);

        String toParse = "";
        //toParse += "RTCTL*SPEC !dc1.paid -> A( G( (dc1 KNOW (!dc1.paid & !dc2.paid & !dc3.paid)) | " +
        //        "( (dc1 KNOW (dc2.paid | dc3.paid)) & !(dc1 KNOW dc2.paid) & !(dc1 KNOW dc3.paid) ) ) );";

        //toParse += "RTCTL*SPEC A(X (dc1 KNOW dc2.said));";
        //toParse += "RTCTL*SPEC E(F(dc1 KNOW dc2.said));";
        //toParse += "RTCTL*SPEC !E(E( X(dc2.said))) ;";
        //toParse += "RTCTL*SPEC (dc1 KNOW dc2.said);";
        //toParse += "RTCTL*SPEC !dc1.paid -> A( G(dc1 KNOW (!dc1.paid & !dc2.paid & !dc3.paid)));";
        //toParse += "RTCTL*SPEC (G (!dc1.paid -> ((dc1 KNOW (!dc1.paid & !dc2.paid & !dc3.paid)))));";

		//toParse += "RTCTL*SPEC (G (!dc1.paid -> ((dc1 KNOW (!dc1.paid & !dc2.paid & !dc3.paid)) |" +
        //        " ( (dc1 KNOW (dc2.paid | dc3.paid)) & !(dc1 KNOW dc2.paid) & !(dc1 KNOW dc3.paid) ))));";

		//toParse += "RTCTL*SPEC !dc1.paid -> A( G( (dc1 KNOW (!dc1.paid & !dc2.paid & !dc3.paid)) |" +
        //        " ( (dc1 KNOW (dc2.paid | dc3.paid)) & !(dc1 KNOW dc2.paid) & !(dc1 KNOW dc3.paid) ) ) );";

        toParse += "RTCTL*SPEC  <dc1, dc2> (BF 6..13 dc2.paid );"; //RTATL*
        //toParse += "RTCTL*SPEC <dc1,dc2,dc3,main>  dc1.paid | dc2.paid | dc3.paid;"; //RTATL*
        //toParse += "RTCTL*SPEC <dc1,main> TRUE U dc1.paid ;"; //RTATL*
        //加载规约
        Spec[] specs = Env.loadSpecString(toParse);

        assert (specs != null) && (specs.length > 0);
        for (Spec spec : specs) {
            if (spec.getLanguage() == InternalSpecLanguage.RTCTLs) {
                ATLStarModelCheckAlg checker = new ATLStarModelCheckAlg(main, spec);
                checker.preAlgorithm();
                LoggerUtil.info(checker.doAlgorithm().resultString());
                checker.postAlgorithm();
            }
        }
    }
}
