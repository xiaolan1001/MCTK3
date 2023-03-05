package cn.edu.hqu.mctk;

import edu.wis.jtlv.env.Env;
import edu.wis.jtlv.env.core.spec.InternalSpecLanguage;
import edu.wis.jtlv.env.module.SMVModule;
import edu.wis.jtlv.env.spec.Spec;
import edu.wis.jtlv.lib.mc.ATLsK.ATLsK_ModelCheckAlg;
import edu.wis.jtlv.lib.mc.ATLstar.ATLStarModelCheckAlg;
import edu.wis.jtlv.lib.mc.ATLstar.LoggerUtil;
import org.junit.Test;
import swing.Statistic;

import static swing.MCTKFrame.statistic;

public class ATLStarModelCheckAlgTest {
    //public static Statistic statistic= null;
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
        //toParse += "RTCTL*SPEC !E(TRUE U  E X(start & close & heat & !error));";  //invalid  MCTK3:false
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

        //toParse += "RTCTL*SPEC A X!(start & close & heat & !error);"; //done NuSMV:true
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
        //toParse += "RTCTL*SPEC <>!(G(receiver.state=r0 | receiver.state=r1) -> (F sender.ack));"; //invalid

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

        //bit_transmission_actions.smv没有添加公平性约束, 与MCMAS例子不同
        String toParse = "";
        //toParse += "RTCTL*SPEC A G((r.state=r0 | r.state=r1) -> A F s.ack);";                   //invalid false MCMAS:true
        //toParse += "RTCTL*SPEC <> G((r.state=r0 | r.state=r1) -> <> F s.ack);";                 //invalid false
        //toParse += "RTCTL*SPEC [s,r,main] G((r.state=r0 | r.state=r1) -> [s,r,main] F s.ack);"; //invalid false
        //toParse += "RTCTL*SPEC [s,r] G((r.state=r0 | r.state=r1) -> [s,r] F s.ack);";           //done    false MCMAS:false
        //toParse += "RTCTL*SPEC !<s,r> F!((r.state=r0 | r.state=r1) -> !<s,r> G !s.ack);";       //done    false MCMAS:true


        //toParse += "RTCTL*SPEC !E(G(r.state=r0 | r.state=r1) -> (F s.ack));";            //invalid false MCMAS:false
        //toParse += "RTCTL*SPEC !<s, r, main>(G(r.state=r0 | r.state=r1) -> (F s.ack));"; //done    false
        //toParse += "RTCTL*SPEC !<s, r>(G(r.state=r0 | r.state=r1) -> (F s.ack));";       //done    false
        //toParse += "RTCTL*SPEC [s, r, main]!(G(r.state=r0 | r.state=r1) -> (F s.ack));"; //done    false
        //toParse += "RTCTL*SPEC [s, r]!(G(r.state=r0 | r.state=r1) -> (F s.ack));";       //done    false
        //toParse += "RTCTL*SPEC <>!(G(r.state=r0 | r.state=r1) -> (F s.ack));";           //invalid false
        //toParse += "RTCTL*SPEC ![](G(r.state=r0 | r.state=r1) -> (F s.ack));";           //invalid false

        //toParse += "RTCTL*SPEC <s,r> (TRUE U s.ack);";      //invalid true MCMAS:true
        //toParse += "RTCTL*SPEC <s,r,main> (TRUE U s.ack);"; //done    true MCMAS:true
        //toParse += "RTCTL*SPEC E (TRUE U s.ack);";          //done    true MCMAS:true
        //toParse += "RTCTL*SPEC [main] (TRUE U s.ack);";     //invalid false MCMAS:false
        //toParse += "RTCTL*SPEC [] (TRUE U s.ack);";         //done    true

        //toParse += "RTCTL*SPEC [s,r,main] F FALSE;";        //invalid false
        //toParse += "RTCTL*SPEC [s,r] F FALSE;";             //invalid false
        //toParse += "RTCTL*SPEC <> F FALSE;";                //invalid false
        //toParse += "RTCTL*SPEC A F FALSE;";                 //invalid false

        //toParse += "RTCTL*SPEC [s,r,main] G FALSE";         //invalid false
        //toParse += "RTCTL*SPEC [s,r] G FALSE";              //invalid false MCMAS:false
        //toParse += "RTCTL*SPEC <> G FALSE;";                //invalid false
        //toParse += "RTCTL*SPEC A G FALSE;";                 //invalid false

        //toParse += "RTCTL*SPEC E F(E G((r.state=r0 | r.state=r1) & !s.ack));"; //done true MCMAS:false

        //************KNOW算子****************/
        //toParse += "RTCTL*SPEC s KNOW (r.state=r0 | r.state=r1);";                 //invalid MCMAS:false
        //toParse += "RTCTL*SPEC A(F(s KNOW (r.state=r0 | r.state=r1)));";           //invalid MCMAS:true 两者所用模型并不尽然相同
        //toParse += "RTCTL*SPEC A F(s.ack -> (s KNOW (r.state=r0 | r.state=r1)));"; //done MCMAS:true
        //toParse += "RTCTL*SPEC !E(TRUE BU 10..15 ((s.bit=1 & s.ack) -> (s KNOW (r.state=r0))));"; //invalid mcmas不能验证含有界算子公式
        //toParse += "RTCTL*SPEC A G((s.bit=1 & s.ack) -> (s KNOW (r.state=r0)));";  //done MCMAS:false
        //************KNOW算子****************/

        toParse += "RTCTL*SPEC  <s, r> G((r.state=r0 | r.state=r1) -> A F s.ack);";       //invalid true MCMAS:true
        //toParse += "RTCTL*SPEC  <s, r, main> G((r.state=r0 | r.state=r1) -> A F s.ack);"; //done    true

        //toParse += "RTCTL*SPEC  <s, r> ((r.state=r0 | r.state=r1) -> A F s.ack);";        //invalid true MCMAS不能验证ATL*公式

        //toParse += "RTCTL*SPEC  <s, r, main> ((r.state=r0 | r.state=r1) -> A F s.ack);";  //done    true
        //toParse += "RTCTL*SPEC  <s, r> ((r.state=r0 | r.state=r1) -> A F s.ack);";        //invalid true
        //toParse += "RTCTL*SPEC  [main] ((r.state=r0 | r.state=r1) -> A F s.ack);";        //done    false
        //toParse += "RTCTL*SPEC  [] ((r.state=r0 | r.state=r1) -> A F s.ack);";            //done    true
        //toParse += "RTCTL*SPEC  E ((r.state=r0 | r.state=r1) -> A F s.ack);";             //done    true

        //toParse += "RTCTL*SPEC  <> F((r.state=r0 | r.state=r1) -> A F s.ack);";           //invalid true
        //toParse += "RTCTL*SPEC  A F((r.state=r0 | r.state=r1) -> A F s.ack);";            //done    true MCMAS:true
        //toParse += "RTCTL*SPEC  <> F((r.state=r0 | r.state=r1) -> <> F s.ack);";          //invalid true

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
     * 本单元测试使用的dc3.smv模型需要修改部分语句(AGENT)后才能使用
     * @throws Exception 异常处理
     */
    @Test
    public void testRTCTLsCheck3() throws Exception {
        //通过文件名加载module
        Env.loadModule("testcases/dc3_act.smv");
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

        //toParse += "RTCTL*SPEC  <dc1, dc2> (BF 6..13 dc2.paid );"; //RTATL*
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

    /**
     * 使用RTATL*模型检测算法验证CTL*公式(testcases/bob_alice_noact.smv)
     * 本单元测试是testATLsKCheck()的辅助测试, 两者应当结合起来测试
     * @throws Exception 异常处理
     */
    @Test
    public void testRTCTLsCheck4() throws Exception {
        //通过文件名加载module
        Env.loadModule("testcases/bob_alice_noact.smv");
        SMVModule main = (SMVModule) Env.getModule("main");
        //为toString程序设置打印模式
        main.setFullPrintingMode(true);

        String toParse = "";
        //本单元测试是与bob_and_alice.smv模型作比较, 使用的模型bob_alice_noact.smv没有动作
        //验证结果均与MCMAS一致
        //toParse += "RTCTL*SPEC E F(bob.count=10);";                   //done MCMAS:true
        //toParse += "RTCTL*SPEC A F(bob.count=10);";                   //invalid MCAMS:false
        //toParse += "RTCTL*SPEC E G(alice.count=10);";                 //invalid MCMAS:false
        //toParse += "RTCTL*SPEC E F(alice.count=10 & bob.count=10);";  //done MCMAS:true
        //toParse += "RTCTL*SPEC E X(bob.count=10);";                   //invalid MCMAS:false
        //toParse += "RTCTL*SPEC A X(bob.count=10);";                   //invalid MCMAS:false

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

    @Test
    public void testRTLsCheck5() throws Exception {
        //通过文件名加载module
        Env.loadModule("testcases/card_games_noact.smv");
        SMVModule main = (SMVModule) Env.getModule("main");
        //为toString程序设置打印模式
        main.setFullPrintingMode(true);

        String toParse = "";
        //toParse += "RTCTL*SPEC  A F(win=TRUE);"; //invalid
        //toParse += "RTCTL*SPEC  E F(win=TRUE);"; //true
        //toParse += "RTCTL*SPEC  F(win=TRUE);";   //invalid
        //toParse += "RTCTL*SPEC  FALSE;";         //invalid

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
     * 使用RTATL*模型检测算法验证ATL*公式(testcases/bob_and_alice.smv)
     * @throws Exception 异常处理
     */
    @Test
    public void testATLsKCheck() throws Exception {
        //通过文件名加载module
        Env.loadModule("testcases/bob_and_alice.smv");
        SMVModule main = (SMVModule) Env.getModule("main");
        //为toString程序设置打印模式
        main.setFullPrintingMode(true);

        String toParse = "";
//        preface + "." + namemain.alice.ACT
//        preface + "." + namemain.bob.ACT
//                -------------------------------------------------
//                main.alice's visible variables:
//        1. main.alice.ACT
//        2. main.alice.count
//        main.alice's invisible variables:
//                -------------------------------------------------
//                main.bob's visible variables:
//        1. main.bob.ACT
//        2. main.bob.count
//        main.bob's invisible variables:
//                -------------------------------------------------
//                main's visible variables:
//        1. main.alice.ACT
//        2. main.alice.count
//        3. main.bob.ACT
//        4. main.bob.count
//        main's invisible variables:
        //[bob,alice] [FALSE R main.bob.count!=10]
        //toParse += "RTCTL*SPEC  <bob, alice> F(bob.count=10);";                    //done true MCMAS:true Time used: 0.044s Memory used: 1910.921875KB Number of BDD variables used: 20 Number of BDD nodes used: 4354
        //<main> [FALSE R main.bob.count!=10] Time used: 0.037s 3198.5625KB Number of BDD variables used: 20 Number of BDD nodes used: 4871
        //toParse += "RTCTL*SPEC  [main] F(bob.count=10);";                          //done true
        //<> [FALSE R main.bob.count!=10] Time used: 0.042s 3185.5703125KB 20 4871
        //toParse += "RTCTL*SPEC  [] F(bob.count=10);";                              //done true
        //[bob,alice,main] [FALSE R main.bob.count!=10] Time used: 0.044s 3186.046875KB 20 4354
        //toParse += "RTCTL*SPEC  <bob, alice, main> F(bob.count=10);";              //done true
//        The negative propperty: A[FALSE R bob.count!=10]
//        Variable Tester1->X is created for TRUE U bob.count=10
//                *** Property is TRUE ***
//        Time used: 0.098s
//        Memory used: 2679.109375KB
//        Number of BDD variables used: 22
//        Number of BDD nodes used: 4058
        //toParse += "RTCTL*SPEC  E F(bob.count=10);";                               //done true MCMAS:true

        //E[FALSE R bob.count!=10]
//        Time used: 0.161s
//        Memory used: 4656.4296875KB
//        Number of BDD variables used: 22
//        Number of BDD nodes used: 3377
        //toParse += "RTCTL*SPEC  A F(bob.count=10);";                               //invalid false MCAMS:false
        //[] [FALSE R main.bob.count!=10] Time used: 0.04s 3185.6015625KB 20 3374
        toParse += "RTCTL*SPEC  <> F(bob.count=10);";                              //invalid false
        //<bob,alice,main> [FALSE R main.bob.count!=10] 0.041s 3185.5625KB 20 4327
        //toParse += "RTCTL*SPEC  [bob,alice,main] F(bob.count=10);";                //invalid false
        //<bob,alice> [FALSE R main.bob.count!=10] 0.041s 3211.4140625KB 20 4327
        //toParse += "RTCTL*SPEC  [bob,alice] F(bob.count=10);";                     //invalid false

        //toParse += "RTCTL*SPEC  <bob, alice> G(alice.count=10);";                  //invalid false MCAMAS:false
        //toParse += "RTCTL*SPEC  <bob,alice,main> G(alice.count=10);";              //invalid false
        //toParse += "RTCTL*SPEC  [main] G(alice.count=10);";                        //done    false
        //toParse += "RTCTL*SPEC  [] G(alice.count=10);";                            //done    false
        //toParse += "RTCTL*SPEC  E G(alice.count=10);";                             //invalid false MCMAS:false

        //toParse += "RTCTL*SPEC  <bob, alice> F(alice.count=10 & bob.count=10);";   //done true MCMAS:true
        //toParse += "RTCTL*SPEC  [main] F(alice.count=10 & bob.count=10);";         //done true
        //toParse += "RTCTL*SPEC  [] F(alice.count=10 & bob.count=10);";             //done true
        //toParse += "RTCTL*SPEC  <bob,alice,main> F(alice.count=10 & bob.count=10);"; //done true
        //toParse += "RTCTL*SPEC  E F(alice.count=10 & bob.count=10);";              //done true MCMAS:true

        //toParse += "RTCTL*SPEC  <bob, alice> X(bob.count=10);";                    //invalid false MCMAS:false
        //toParse += "RTCTL*SPEC  [main] X(bob.count=10);";                          //done    false
        //toParse += "RTCTL*SPEC  [] X(bob.count=10);";                              //done    false
        //toParse += "RTCTL*SPEC  <bob,alice,main> X(bob.count=10);";                //invalid false
        //toParse += "RTCTL*SPEC  E X(bob.count=10);";                               //invalid false MCMAS:false

        //toParse += "RTCTL*SPEC  [bob, alice] X(bob.count=10);";                    //invalid false MCMAS:false
        //toParse += "RTCTL*SPEC  <main> X(bob.count=10);";                          //invalid false
        //toParse += "RTCTL*SPEC  <> X(bob.count=10);";                              //invalid false
        //toParse += "RTCTL*SPEC  A X(bob.count=10);";                               //invalid false MCMAS:false

        //toParse += "RTCTL*SPEC  <bob, alice> F(bob.count!=10 & alice.count!=10);"; //done true MCMAS:true
        //toParse += "RTCTL*SPEC  [main] F(bob.count!=10 & alice.count!=10);";       //done true
        //toParse += "RTCTL*SPEC  [] F(bob.count!=10 & alice.count!=10);";           //done true
        //toParse += "RTCTL*SPEC  <bob,alice,main> F(bob.count!=10 & alice.count!=10);"; //done true
        //toParse += "RTCTL*SPEC  E F(bob.count!=10 & alice.count!=10);";            //done true MCMAS:true

        //toParse += "RTCTL*SPEC  [bob, alice] F(bob.count=10);";                    //invalid false MCMAS:false
        //toParse += "RTCTL*SPEC  <main> F(bob.count=10);";                          //invalid false
        //toParse += "RTCTL*SPEC  <> F(bob.count=10);";                              //invalid false
        //toParse += "RTCTL*SPEC  [bob,alice,main] F(bob.count=10);";                //invalid false
        //toParse += "RTCTL*SPEC  A F(bob.count=10);";                               //invalid false MCMAS:false

        //toParse += "RTCTL*SPEC  [bob, alice] G(bob.count=11);";                    //invalid false
        //toParse += "RTCTL*SPEC  <bob, alice> G(bob.count=11);";                    //invalid false

        //toParse += "RTCTL*SPEC  [bob, alice] (bob.count=11);";                     //invalid false

        //toParse += "RTCTL*SPEC  <bob, alice> (bob.count=10 | alice.count=10);";
        //toParse += "RTCTL*SPEC  <bob, alice, main> (bob.count=0 | alice.count=10);";
        //toParse += "RTCTL*SPEC  alice.count=0 U <bob, alice, main> F (bob.count=10);";

        //**********************************认知算子**********************************/
        //toParse += "RTCTL*SPEC  <bob, alice> (bob K bob.count=9);";
        //toParse += "RTCTL*SPEC  (bob K bob.count=9);";                             //invalid MCMAS:false
        //***************************************************************************/

        Spec[] specs = Env.loadSpecString(toParse);

        //if(statistic==null) statistic=new Statistic();
        //else statistic.beginStatistic(true,false);

        assert (specs != null) && (specs.length > 0);
        for (Spec spec : specs) {
            if (spec.getLanguage() == InternalSpecLanguage.RTCTLs) {
                ATLStarModelCheckAlg checker = new ATLStarModelCheckAlg(main, spec);
                checker.preAlgorithm();
                LoggerUtil.info(checker.doAlgorithm().resultString());
                checker.postAlgorithm();
            }
        }
        //LoggerUtil.info(statistic.getUsedInfo(true,true,true,true));
    }

    @Test
    public void testATLsCheck2() throws Exception {
        //通过文件名加载module
        Env.loadModule("testcases/card_games.smv");
        SMVModule main = (SMVModule) Env.getModule("main");
        //为toString程序设置打印模式
        main.setFullPrintingMode(true);

        String toParse = "";
        //toParse += "RTCTL*SPEC  A F(win=TRUE);";              //invalid false MCMAS:false
        //toParse += "RTCTL*SPEC  <> F(win=TRUE);";             //invalid false
        //toParse += "RTCTL*SPEC  F(win=TRUE);";                //invalid false
        //toParse += "RTCTL*SPEC  [player1,main] F(win=TRUE);"; //invalid    false MCMAS:false
        //toParse += "RTCTL*SPEC  [player1] F(win=TRUE);";      //invalid    false MCMAS:false
        //toParse += "RTCTL*SPEC <main> F(win=TRUE);";          //true true  MCMAS:false

        //toParse += "RTCTL*SPEC  <player1> F(win=FALSE);";      //done true  MCMAS:true
        //toParse += "RTCTL*SPEC  [main] F(win=TRUE);";         //done    false MCMAS:true
        //toParse += "RTCTL*SPEC  !<main> G!(win=TRUE);";       //done    false MCMAS:true
        //toParse += "RTCTL*SPEC  <player1,main> F(win=TRUE);"; //invalid true  MCMAS:true
        //toParse += "RTCTL*SPEC  [] F(win=TRUE);";             //done    true
        //toParse += "RTCTL*SPEC  E F(win=TRUE);";              //done    true

        toParse += "RTCTL*SPEC  [player1] G(win=FALSE);";          //false false MCMAS:false
        //toParse += "RTCTL*SPEC  !<player1> F(win=TRUE);";          //done false MCMAS:false

        //toParse += "RTCTL*SPEC  <player1,main> G(win=FALSE);";


        //toParse += "RTCTL*SPEC  FALSE;";                      //invalid false

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

    @Test
    public void testATLsCheck3() throws Exception {
        //通过文件名加载module
        Env.loadModule("testcases/dc3_act.smv");
        SMVModule main = (SMVModule) Env.getModule("main");
        //为toString程序设置打印模式
        main.setFullPrintingMode(true);

        String toParse = "";

        toParse += "RTCTL*SPEC  <dc1,dc2,dc3>F(number=0 -> (dc1.paid | dc2.paid | dc3.paid));";

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

    @Test
    public void testATLsCheck4() throws Exception {
        //通过文件名加载module
        Env.loadModule("testcases/dc4.smv");
        SMVModule main = (SMVModule) Env.getModule("main");
        //为toString程序设置打印模式
        main.setFullPrintingMode(true);

        String toParse = "";

        toParse += "RTCTL*SPEC  <dc1,dc2,dc3,dc4>F(number=0 -> (dc1.paid | dc2.paid | dc3.paid | dc4.paid));";

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

    @Test
    public void testATLsCheck5() throws Exception {
        //通过文件名加载module
        Env.loadModule("testcases/dc5.smv");
        SMVModule main = (SMVModule) Env.getModule("main");
        //为toString程序设置打印模式
        main.setFullPrintingMode(true);

        String toParse = "";

        toParse += "RTCTL*SPEC  <dc1,dc2,dc3,dc4,dc5>F(number=0 -> (dc1.paid | dc2.paid | dc3.paid | dc4.paid | dc5.paid));";

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

    @Test
    public void testATLsCheck6() throws Exception {
        //通过文件名加载module
        Env.loadModule("testcases/dc6.smv");
        SMVModule main = (SMVModule) Env.getModule("main");
        //为toString程序设置打印模式
        main.setFullPrintingMode(true);

        String toParse = "";

        toParse += "RTCTL*SPEC  <dc1,dc2,dc3,dc4,dc5,dc6>F(number=0 -> (dc1.paid | dc2.paid | dc3.paid | dc4.paid | dc5.paid | dc6.paid));";

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

    @Test
    public void testATLsCheck7() throws Exception {
        //通过文件名加载module
        Env.loadModule("testcases/dc7.smv");
        SMVModule main = (SMVModule) Env.getModule("main");
        //为toString程序设置打印模式
        main.setFullPrintingMode(true);

        String toParse = "";

        toParse += "RTCTL*SPEC  <dc1,dc2,dc3,dc4,dc5,dc6,dc7>F(number=0 -> (dc1.paid | dc2.paid | dc3.paid | dc4.paid | dc5.paid | dc6.paid | dc7.paid));";

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

    @Test
    public void testATLsCheck8() throws Exception {
        //通过文件名加载module
        Env.loadModule("testcases/dc8.smv");
        SMVModule main = (SMVModule) Env.getModule("main");
        //为toString程序设置打印模式
        main.setFullPrintingMode(true);

        String toParse = "";

        toParse += "RTCTL*SPEC  <dc1,dc2,dc3,dc4,dc5,dc6,dc7,dc8>F(number=0 -> (dc1.paid | dc2.paid | dc3.paid | dc4.paid | dc5.paid | dc6.paid | dc7.paid | dc8.paid));";

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

    @Test
    public void testATLsCheck9() throws Exception {
        //通过文件名加载module
        Env.loadModule("testcases/dc9.smv");
        SMVModule main = (SMVModule) Env.getModule("main");
        //为toString程序设置打印模式
        main.setFullPrintingMode(true);

        String toParse = "";

        toParse += "RTCTL*SPEC  <dc1,dc2,dc3,dc4,dc5,dc6,dc7,dc8,dc9>F(number=0 -> (dc1.paid | dc2.paid | dc3.paid | dc4.paid | dc5.paid | dc6.paid | dc7.paid | dc8.paid | dc9.paid));";

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

    @Test
    public void testATLsCheck10() throws Exception {
        //通过文件名加载module
        Env.loadModule("testcases/dc10.smv");
        SMVModule main = (SMVModule) Env.getModule("main");
        //为toString程序设置打印模式
        main.setFullPrintingMode(true);

        String toParse = "";

        toParse += "RTCTL*SPEC  <dc1,dc2,dc3,dc4,dc5,dc6,dc7,dc8,dc9,dc10>F(number=0 -> (dc1.paid | dc2.paid | dc3.paid | dc4.paid | dc5.paid | dc6.paid | dc7.paid | dc8.paid | dc9.paid | dc10.paid));";

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

    @Test
    public void testATLsCheck11() throws Exception {
        //通过文件名加载module
        Env.loadModule("testcases/dc11.smv");
        SMVModule main = (SMVModule) Env.getModule("main");
        //为toString程序设置打印模式
        main.setFullPrintingMode(true);

        String toParse = "";

        toParse += "RTCTL*SPEC  <dc1,dc2,dc3,dc4,dc5,dc6,dc7,dc8,dc9,dc10,dc11>F(number=0 -> (dc1.paid | dc2.paid | dc3.paid | dc4.paid | dc5.paid | dc6.paid | dc7.paid | dc8.paid | dc9.paid | dc10.paid | dc11.paid));";

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

    @Test
    public void testATLsCheck12() throws Exception {
        //通过文件名加载module
        Env.loadModule("testcases/dc12.smv");
        SMVModule main = (SMVModule) Env.getModule("main");
        //为toString程序设置打印模式
        main.setFullPrintingMode(true);

        String toParse = "";

        toParse += "RTCTL*SPEC  <dc1,dc2,dc3,dc4,dc5,dc6,dc7,dc8,dc9,dc10,dc11,dc12>F(number=0 -> (dc1.paid | dc2.paid | dc3.paid | dc4.paid | dc5.paid | dc6.paid | dc7.paid | dc8.paid | dc9.paid | dc10.paid | dc11.paid | dc12.paid));";

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

    @Test
    public void testATLsCheck13() throws Exception {
        //通过文件名加载module
        Env.loadModule("testcases/dc13.smv");
        SMVModule main = (SMVModule) Env.getModule("main");
        //为toString程序设置打印模式
        main.setFullPrintingMode(true);

        String toParse = "";

        toParse += "RTCTL*SPEC  <dc1,dc2,dc3,dc4,dc5,dc6,dc7,dc8,dc9,dc10,dc11,dc12,dc13>F(number=0 -> (dc1.paid | dc2.paid | dc3.paid | dc4.paid | dc5.paid | dc6.paid | dc7.paid | dc8.paid | dc9.paid | dc10.paid | dc11.paid | dc12.paid | dc13.paid));";

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
