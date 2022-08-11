package cn.edu.hqu.mctk;

import edu.wis.jtlv.env.Env;
import edu.wis.jtlv.env.module.SMVModule;
import edu.wis.jtlv.env.spec.Operator;
import edu.wis.jtlv.env.spec.Spec;
import edu.wis.jtlv.env.spec.SpecException;
import edu.wis.jtlv.env.spec.SpecExp;
import edu.wis.jtlv.lib.mc.ATLstar.LoggerUtil;
import edu.wis.jtlv.lib.mc.ATLstar.SpecUtil;
import edu.wis.jtlv.old_lib.mc.ModelCheckException;
import org.junit.Test;

import java.io.IOException;

public class SpecUtilTest {
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
        String to_parse = "";
        to_parse += "SPEC\n" + " !((proc[1].loc = 3) & (proc[2].loc = 3)) \n"; //CTL done
        to_parse += "SPEC\n" + " !((proc[1].loc = 3) & (proc[2].loc = 2)) \n"; //CTL done
        Spec[] specs = Env.loadSpecString(to_parse);
        assert (specs != null) && (specs.length > 0);
        for (Spec spec : specs) {
            LoggerUtil.info("spec:{}", SpecUtil.simplifySpecString(spec, false));
        }
    }

    @Test
    public void testRTCTLs() throws Exception {
        //通过文件名加载module
        Env.loadModule("testcases/mwOven.smv");
        SMVModule main = (SMVModule) Env.getModule("main");
        //为toString程序设置打印模式
        main.setFullPrintingMode(true);

        String to_parse = "";
        to_parse += "SPEC !E[TRUE U  EX(start & close & heat & !error)]"; // CTL done
        to_parse += "SPEC !E[TRUE BU 3..12 EX(start & close & heat & !error)]"; //CTL done
        to_parse += "RTCTL*SPEC A(G((!close & start) -> A(G !heat | F !error)))"; //RTCTL* done
        //to_parse += "RTCTL*SPEC !E[TRUE U (start & close & heat & !error)];"; //RTCTL* error '['
        //to_parse += "ATL*SPEC A(G((!close & start) -> A(G !heat | F !error)))"; //error

        //加载规约
        Spec[] specs = Env.loadSpecString(to_parse);

        assert (specs != null) && (specs.length > 0);
        for (Spec spec : specs) {
            LoggerUtil.info("{}, spec:{}", spec instanceof SpecExp, SpecUtil.simplifySpecString(spec, false));
        }
    }

    @Test
    public void testNNF() throws Exception {
        //通过文件名加载module
        Env.loadModule("testcases/mwOven.smv");
        SMVModule main = (SMVModule) Env.getModule("main");
        //为toString程序设置打印模式
        main.setFullPrintingMode(true);

        String to_parse = "";
        to_parse += "RTCTL*SPEC E G TRUE;"; // RTCTL* done

        //加载规约
        Spec[] specs = Env.loadSpecString(to_parse);

        assert (specs != null) && (specs.length > 0);
        for (Spec spec : specs) {
            LoggerUtil.info("{}, NNF(spec):{}", spec instanceof SpecExp, SpecUtil.simplifySpecString(SpecUtil.NNF(spec), false));
        }
    }
}