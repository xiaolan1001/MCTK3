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

        to_parse += "RTCTL*SPEC !A close";                                           //RTCTL* error

        //to_parse += "RTCTL*SPEC E(TRUE BU 0..-1 E X start)";                         //RTCTL* error '-1'

        //to_parse += "RTCTL*SPEC [start -> close];";                                  //RTCTL* error '['

        //to_parse += "RTCTL*SPEC !E[TRUE U (start & close & heat & !error)];";        //RTCTL* error '['

        //to_parse += "ATL*SPEC A(G((!close & start) -> A(G !heat | F !error)))";      //error

        //to_parse += "SPEC ! E F (!close & start & E((F heat) & (G error)));";        //CTL error 'EF'

        //to_parse += "SPEC A G( (!close & start) -> A((G !heat) | (F !error)) );";    //CTL error 'AG'

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

        //******************总结******************
        //CTL规约中路径量词与时态算子一起解析,例如AU、EF等,故不能用NNF函数生成否定范式
        //SpecBDD类对象例如!(start & close)会变为(start=1 -> close=0)
        //******************总结******************
        String to_parse = "";

        //to_parse += "RTCTL*SPEC E G TRUE;";                                              //RTCTL* done

        //to_parse += "RTCTL*SPEC !E(TRUE U (start & close & heat & !error));";            //RTCTL* done

        //to_parse += "RTCTL*SPEC !(start & close)";                                       //RTCTL* done

        //to_parse += "RTCTL*SPEC E(TRUE U (start & close & heat & !error));";             //RTCTL* done

        //to_parse += "LTLSPEC !G !heat;";                                                 //LTL done

        //to_parse += "RTCTL*SPEC A G(start -> A F heat);";                                //RTCTL* done

        //to_parse += "RTCTL*SPEC !E(TRUE BU 3..12 E X(start & close & heat & !error))";   //RTCTL* done

        //to_parse += "RTCTL*SPEC E(TRUE BU 3..12 E X start)";                             //RTCTL* done

        //to_parse += "RTCTL*SPEC E(TRUE BU 0..0 E X start)";                              //RTCTL* done

        //to_parse += "RTCTL*SPEC E(G E X start)";                                         //RTCTL* done

        //to_parse += "RTCTL*SPEC E(BG 3..12 E X start)";                                  //RTCTL* done

        //to_parse += "RTCTL*SPEC !E(BF 3..12 E X start)";                                 //RTCTL* done

        //to_parse += "RTCTL*SPEC A(BG 3..12 start)";                                      //RTCTL* done

        //to_parse += "RTCTL*SPEC A(BG 3..12 start) -> close";                             //RTCTL* done

        //to_parse += "RTCTL*SPEC A(BG 3..12 start) xnor G close";                         //RTCTL* done

        //to_parse += "RTCTL*SPEC A(BG 3..12 start) xor ! G close";                        //RTCTL* done

        //to_parse += "RTCTL*SPEC E close";                                                //RTCTL* done

        //to_parse += "RTCTL*SPEC A A G close";                                            //RTCTL* done

        //加载规约
        Spec[] specs = Env.loadSpecString(to_parse);

        assert (specs != null) && (specs.length > 0);
        for (Spec spec : specs) {
            LoggerUtil.info("{}, NNF(spec):{}", spec instanceof SpecExp, SpecUtil.simplifySpecString(SpecUtil.NNF(spec), false));
            //LoggerUtil.info("{}, NNF(spec):{}", spec instanceof SpecExp, SpecUtil.simplifySpecString(RTCTLs_ModelCheckAlg.NNF(spec), false));
        }
    }
}