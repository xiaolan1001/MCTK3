package cn.edu.hqu.mctk;

import edu.wis.jtlv.env.Env;
import edu.wis.jtlv.env.core.spec.InternalSpecLanguage;
import edu.wis.jtlv.env.module.SMVModule;
import edu.wis.jtlv.env.spec.Spec;
import edu.wis.jtlv.lib.mc.ATLstar.ATLStarModelCheckAlg;
import edu.wis.jtlv.lib.mc.ATLstar.LoggerUtil;
import org.junit.Test;

public class ATLStarModelCheckAlgTest {
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
}
