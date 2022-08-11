package cn.edu.hqu.mctk;

import edu.wis.jtlv.lib.mc.ATLstar.LoggerUtil;
import org.junit.Test;

public class LoggerUtilTest {
    @Test
    public void testOne() {
        LoggerUtil.trace("trace");
        LoggerUtil.debug("debug");
        LoggerUtil.info("info"); //日志最低输出级别为info
        LoggerUtil.warn("warn");
        LoggerUtil.error("error");
        LoggerUtil.fatal("fatal");
    }
}
