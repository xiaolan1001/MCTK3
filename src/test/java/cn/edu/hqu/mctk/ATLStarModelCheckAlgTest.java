package cn.edu.hqu.mctk;

import edu.wis.jtlv.env.Env;
import edu.wis.jtlv.env.core.spec.InternalSpecLanguage;
import edu.wis.jtlv.env.module.SMVModule;
import edu.wis.jtlv.env.spec.Spec;
import edu.wis.jtlv.lib.mc.ATLsK.ATLsK_ModelCheckAlg;
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

        //to_parse.append("LTLSPEC start;"); //invalid NuSMV:false
        //to_parse.append("LTLSPEC  F start -> G heat;"); //invalid NuSMV:false
        to_parse.append("LTLSPEC TRUE;"); //done NuSMV:true

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
     * 使用RTATL*模型检测算法验证RTCTL公式(testcases/BUTest.smv)
     * @throws Exception 异常处理
     */
    @Test
    public void testRTCTLCheck() throws Exception {
        //通过文件名加载module
        Env.loadModule("testcases/BUTest.smv");
        SMVModule main = (SMVModule) Env.getModule("main");
        //为toString程序设置打印模式
        main.setFullPrintingMode(true);

        String toParse = "";
        //toParse += "RTCTL*SPEC E (a=1 BU 2..10 a=200);";     //invalid MCTK3:false
		//toParse += "RTCTL*SPEC A BF 3..8 a=3;";              //invalid MCTK3:false
		//toParse += "RTCTL*SPEC E BF 6..10 a=4;";             //invalid MCTK3:false
		//toParse += "RTCTL*SPEC A BG 6..10 a=1;";             //invalid MCTK3:false
     	//toParse += "RTCTL*SPEC A F a=30;";                   //invalid MCTK3:false
		//toParse += "RTCTL*SPEC A F a=3;";                    //done MCTK3:true
        //toParse += "RTCTL*SPEC E (a=1 U a=2);";              //invalid MCTK3:false
		//toParse += "RTCTL*SPEC A (a=10 BU 3..12 a=3);";      //invalid MCTK3:false
        //toParse += "RTCTL*SPEC A X a=2;";                    //invalid MCTK3:false
        //toParse += "RTCTL*SPEC A F a=2;";                    //done MCTK3:true
        //toParse += "RTCTL*SPEC A (a=1 BU 6..13 a=3);";       //invalid MCTK3:false
        //toParse += "RTCTL*SPEC A BG 3..10 a=3;";             //invalid MCTK3:false
        //toParse += "RTCTL*SPEC a=2 -> E BF 6..10 a=20;";     //invalid MCTK3:false
        //toParse += "RTCTL*SPEC a=1 -> A BF 6..10 a=2;";      //done MCTK3:true

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
     * 使用RTATL*模型检测算法验证RTCTL公式(testcases/mwOven.smv)
     * @throws Exception 异常处理
     */
    @Test
    public void testRTCTLCheck2() throws Exception {
        //通过文件名加载module
        Env.loadModule("testcases/mwOven.smv");
        SMVModule main = (SMVModule) Env.getModule("main");
        //为toString程序设置打印模式
        main.setFullPrintingMode(true);

        String toParse = "";

        //toParse += "RTCTL*SPEC !E(TRUE BU 3..12 E X(start & close & heat & !error));"; //invalid MCTK3:false
        //toParse += "RTCTL*SPEC E BG  3..16 ( A G(!start & !close & !heat & !error) |  " +
        //        "E X(start & !close & !heat & error) | (start & close & !heat & error) );"; //invalid MCTK3:false
        toParse += "RTCTL*SPEC A BG 13..18 (start -> A F heat);"; //done MCTK3:true

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
     * 使用RTATL*模型检测算法验证RTCTL公式(testcases/traffic.smv)
     * @throws Exception 异常处理
     */
    @Test
    public void testRTCTLCheck3() throws Exception {
        //通过文件名加载module
        Env.loadModule("testcases/traffic.smv");
        SMVModule main = (SMVModule) Env.getModule("main");
        //为toString程序设置打印模式
        main.setFullPrintingMode(true);

        String toParse = "";

        //toParse += "RTCTL*SPEC A BG 1000..1270 nsLight=GREEN;"; //运行时间过长
        //toParse += "RTCTL*SPEC A BF 10..126 nsLight=GREEN;";       //invalid MCTK3:false
        //toParse += "RTCTL*SPEC A BF 10..127 nsLight=GREEN;";       //done MCTK3:true
        //toParse += "RTCTL*SPEC A G (ewLight=GREEN -> E(ewLight=RED BU 10..127 ewLight=GREEN));"; //invalid MCTK3:false
        //toParse += "RTCTL*SPEC A F ewLight=GREEN;"; //done MCTK3:true

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
        toParse += "RTCTL*SPEC !E(TRUE U  E X(start & close & heat & !error));";  //invalid  MCTK3:false
        //toParse += "RTCTL*SPEC A(G((!close & start) -> A(G !heat | F !error)));"; //done MCTK3:true
        //toParse += "RTCTL*SPEC ! E(X start);";                                    //invalid MCTK3:false
        //toParse += "RTCTL*SPEC ! E(F start);";                                    //invalid MCTK3:false
        //toParse += "RTCTL*SPEC A( G start);";                                     //invalid MCTK3:false
        //toParse += "RTCTL*SPEC F start -> G heat;";                               //invalid MCTK3:false
        //toParse += "RTCTL*SPEC !E( !start U E( TRUE U start));";                  //invalid MCTK3:false
        //toParse += "RTCTL*SPEC !E( (X close) U start);";                          //invalid MCTK3:false
        //toParse += "RTCTL*SPEC F!E((X start) R heat);";                           //done MCTK3:true
        //toParse += "RTCTL*SPEC !E( X start U (F error));";                        //invalid MCTK3:false
        //toParse += "RTCTL*SPEC !E( start U X error);";                            //invalid MCTK3:false
        //toParse += "RTCTL*SPEC !E((E X !start) U error);";                        //invalid MCTK3:false
        //toParse += "RTCTL*SPEC !E(TRUE U (F error));";                            //invalid MCTK3:false
        //toParse += "RTCTL*SPEC A( (X start) U !(X error));";                      //done MCTK3:true
        //toParse += "RTCTL*SPEC A start;";                                         //invalid MCTK3:false
        //toParse += "RTCTL*SPEC A(start U E(heat));";                              //invalid MCTK3:true
        //toParse += "RTCTL*SPEC A(start U E F(heat));";                            //done NuSMV:true
        //toParse += "RTCTL*SPEC !E( X E(F heat) );";                               //invalid MCTK3:false
        //toParse += "RTCTL*SPEC (X start) & (TRUE U X start);";                    //invalid MCTK3:false

        //toParse += "RTCTL*SPEC A G(start -> A F heat);";                          //done MCTK3:true
        //toParse += "RTCTL*SPEC start;";                                           //invalid MCTK3:false
        //toParse += "RTCTL*SPEC TRUE;";                                            //done MCTK3:true

        //****************ATL*规约*****************/
        //toParse += "RTCTL*SPEC <> start;"; //invalid 将策略量词替换为路径量词:invalid
        //toParse += "RTCTL*SPEC <> G(start -> <> F heat);"; //done 将策略量词替换为路径量词:done

        //toParse += "RTCTL*SPEC A F heat;"; //done NuSMV:true
        //toParse += "RTCTL*SPEC <> F heat;"; //done

        toParse += "RTCTL*SPEC A X!(start & close & heat & !error);"; //done NuSMV:true
        //toParse += "RTCTL*SPEC <> X!(start & close & heat & !error);"; //done
        //****************ATL*规约*****************/

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
//        for (Spec spec : allSpecs) {
//            if (spec.getLanguage() == InternalSpecLanguage.RTCTLs) {
//                ATLsK_ModelCheckAlg checker = new ATLsK_ModelCheckAlg(main, spec);
//                checker.preAlgorithm();
//                LoggerUtil.info(checker.doAlgorithm().resultString());
//                checker.postAlgorithm();
//            }
//        }
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
        //toParse += "RTCTL*SPEC A G((receiver.state=r0 | receiver.state=r1) -> A F sender.ack);"; //invalid NuSMV:false
        //toParse += "RTCTL*SPEC <> G((receiver.state=r0 | receiver.state=r1) -> <> F sender.ack);"; //invalid

        //toParse += "RTCTL*SPEC !E(G(receiver.state=r0 | receiver.state=r1) -> (F sender.ack));"; //invalid MCTK3:false
        toParse += "RTCTL*SPEC <>!(G(receiver.state=r0 | receiver.state=r1) -> (F sender.ack));"; //invalid

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
        //toParse += "RTCTL*SPEC A G((r.state=r0 | r.state=r1) -> A F s.ack);"; //done MCMAS:true
        //toParse += "RTCTL*SPEC <> G((r.state=r0 | r.state=r1) -> <> F s.ack);"; //invalid

        //toParse += "RTCTL*SPEC !E(G(r.state=r0 | r.state=r1) -> (F s.ack));"; //invalid MCMAS:false
        //toParse += "RTCTL*SPEC !<s, r, main>(G(r.state=r0 | r.state=r1) -> (F s.ack));"; //done
        //toParse += "RTCTL*SPEC [s, r, main]!(G(r.state=r0 | r.state=r1) -> (F s.ack));"; //done
        //toParse += "RTCTL*SPEC <>!(G(r.state=r0 | r.state=r1) -> (F s.ack));"; //invalid
        //toParse += "RTCTL*SPEC ![](G(r.state=r0 | r.state=r1) -> (F s.ack));"; //invalid

        //toParse += "RTCTL*SPEC <s,r> (TRUE U s.ack);"; //done
        //toParse += "RTCTL*SPEC [s,r,main] F FALSE;"; //done
        //toParse += "RTCTL*SPEC <> F FALSE;"; //invalid
        //toParse += "RTCTL*SPEC [s,r,main] G FALSE"; //done
        //toParse += "RTCTL*SPEC <> G FALSE;"; //invalid 将策略量词替换为路径量词:invalid

        //toParse += "RTCTL*SPEC E F(E G((r.state=r0 | r.state=r1) & !s.ack));"; //invalid MCMAS:false

        //************KNOW算子****************/
        //toParse += "RTCTL*SPEC s KNOW (r.state=r0 | r.state=r1);"; //invalid MCMAS:false
        toParse += "RTCTL*SPEC A(F(s KNOW (r.state=r0 | r.state=r1)));"; //invalid MCMAS:true 两者所用模型并不尽然相同
        //toParse += "RTCTL*SPEC A F(s.ack -> (s KNOW (r.state=r0 | r.state=r1)));"; //done MCMAS:true
        //toParse += "RTCTL*SPEC !E(TRUE BU 10..15 ((s.bit=1 & s.ack) -> (s KNOW (r.state=r0))));"; //invalid mcmas不能验证含有界算子公式
        //toParse += "RTCTL*SPEC A G((s.bit=1 & s.ack) -> (s KNOW (r.state=r0)));"; //done MCMAS:false
        //************KNOW算子****************/

        //toParse += "RTCTL*SPEC  <s, r> G((r.state=r0 | r.state=r1) -> A F s.ack);"; //done MCMAS:true
        //toParse += "RTCTL*SPEC  <s, r> ((r.state=r0 | r.state=r1) -> A F s.ack);"; //done MCMAS不能验证ATL*公式

        //toParse += "RTCTL*SPEC  <s, r, main> ((r.state=r0 | r.state=r1) -> A F s.ack);"; //done
        //toParse += "RTCTL*SPEC  E ((r.state=r0 | r.state=r1) -> A F s.ack);"; //done

        //toParse += "RTCTL*SPEC  <> F((r.state=r0 | r.state=r1) -> A F s.ack);"; //done
        //toParse += "RTCTL*SPEC  A F((r.state=r0 | r.state=r1) -> A F s.ack);"; //done MCMAS:true
        //toParse += "RTCTL*SPEC  <> F((r.state=r0 | r.state=r1) -> <> F s.ack);"; //done

        //************SKNOW算子****************/
        //toParse += "RTCTL*SPEC !E BG 10..15 ((s.bit=1 & s.ack) -> (s SKNOW (r.state=r0)));"; //error 暂时还未编写SKNOW算法
        //toParse += "RTCTL*SPEC A BG 10..15 (s SKNOW (r.state=r0));"; //error 暂时还未编写SKNOW算法
        //************SKNOW算子****************/

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
//        for (Spec spec : specs) {
//            if (spec.getLanguage() == InternalSpecLanguage.RTCTLs) {
//                ATLsK_ModelCheckAlg checker = new ATLsK_ModelCheckAlg(main, spec);
//                checker.preAlgorithm();
//                LoggerUtil.info(checker.doAlgorithm().resultString());
//                checker.postAlgorithm();
//            }
//        }
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
