package edu.wis.jtlv.lib.mc.ATLstar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 日志工具类
 */
public class LoggerUtil {
    // 本日志类名
    private final static String logClassName = LoggerUtil.class.getName();

    //私有化构造方法
    private LoggerUtil() {
    }

    /**
     * 获取最原始被调用的堆栈信息
     * @return 堆栈信息
     */
    private static StackTraceElement getCaller() {
        //获取堆栈信息
        StackTraceElement[] traceElements = Thread.currentThread().getStackTrace();
        if(traceElements.length <= 0) {
            return null;
        }

        //最原始被调用的堆栈信息
        StackTraceElement caller = null;

        //循环便利到日志类标识
        boolean isEachLogFlag = false;

        //遍历堆栈信息，获取最原始被调用的方法信息
        for (StackTraceElement element : traceElements) {
            //遍历到日志类
            if(element.getClassName().equals(logClassName)) {
                isEachLogFlag = true;
            }

            //下一个非日志类的堆栈，就是最原始被调用的方法
            if(isEachLogFlag) {
                if (!element.getClassName().equals(logClassName)) {
                    caller = element;
                    break;
                }
            }
        }
        return caller;
    }

    /**
     * 自动匹配请求类名，生成logger对象
     * @return Logger对象
     */
    private static Logger logger() {
        //最原始被调用的堆栈对象
        StackTraceElement caller = LoggerUtil.getCaller();
        //空堆栈处理
        if(caller == null) {
            return LogManager.getLogger(LoggerUtil.class);
        }

        return LogManager.getLogger(caller.getClassName());
    }

    //封装后的方法
    //TRACE
    public static void trace(String message) {
        logger().trace(message);
    }
    public static void trace(String message, Throwable exception) {
        logger().trace(message, exception);
    }
    public static void trace(String message, Object object) {
        logger().trace(message, object);
    }
    public static void trace(String message, Object... object) {
        logger().trace(message, object);
    }

    //DEBUG
    public static void debug(String message) {
        logger().debug(message);
    }
    public static void debug(String message, Throwable exception) {
        logger().debug(message, exception);
    }
    public static void debug(String message, Object object) {
        logger().debug(message, object);
    }
    public static void debug(String message, Object... object) {
        logger().debug(message, object);
    }

    //INFO
    public static void info(String message) {
        logger().info(message);
    }
    public static void info(String message, Throwable exception) {
        logger().info(message, exception);
    }
    public static void info(String message, Object object) {
        logger().info(message, object);
    }
    public static void info(String message, Object... object) {
        logger().info(message, object);
    }

    //WARN
    public static void warn(String message) {
        logger().warn(message);
    }
    public static void warn(String message, Throwable exception) {
        logger().warn(message, exception);
    }
    public static void warn(String message, Object object) {
        logger().warn(message, object);
    }
    public static void warn(String message, Object... object) {
        logger().warn(message, object);
    }

    //ERROR
    public static void error(String message) {
        logger().error(message);
    }
    public static void error(String message, Throwable exception) {
        logger().error(message, exception);
    }
    public static void error(String message, Object object) {
        logger().error(message, object);
    }
    public static void error(String message, Object... object) {
        logger().error(message, object);
    }

    //FATAL
    public static void fatal(String message) {
        logger().fatal(message);
    }
    public static void fatal(String message, Throwable exception) {
        logger().fatal(message, exception);
    }
    public static void fatal(String message, Object object) {
        logger().fatal(message, object);
    }
    public static void fatal(String message, Object... object) {
        logger().fatal(message, object);
    }
}
