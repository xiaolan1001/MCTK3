package cn.edu.hqu.mctk;

import edu.wis.jtlv.env.Env;
import edu.wis.jtlv.env.core.spec.InternalSpecLanguage;
import edu.wis.jtlv.env.module.SMVModule;
import edu.wis.jtlv.env.spec.Spec;
import edu.wis.jtlv.lib.mc.ATLstar.ATLStarModelCheckAlg;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Test;

public class ATLStarModelCheckAlgTest {
    //定义日志记录器对象
    public static final Logger LOGGER = LogManager.getLogger(ATLStarModelCheckAlgTest.class);

    @Test
    public void testATLsCheck() throws Exception {
        Env.loadModule("testcases/mwOven.smv");
        LOGGER.info("========= Loading Modules ==========");
        SMVModule main = (SMVModule) Env.getModule("main");
        main.setFullPrintingMode(true);
        LOGGER.info("========= DONE Loading Modules ==========");

        String toParse = "LTLSPEC  F start -> G heat;";

        Spec[] allSpecs = Env.loadSpecString(toParse);
        LOGGER.info("========= DONE Loading Specs ============");

        assert allSpecs != null;

        for (Spec allSpec : allSpecs) {
            if (allSpec.getLanguage() == InternalSpecLanguage.LTL)
                new ATLStarModelCheckAlg(main, allSpec);
        }
    }
}
