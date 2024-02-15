package com.libraryseat.utils;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

class LogFormatter extends Formatter {
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyyMMdd HH:mm:ss.SSS");
    @Override
    public String format(LogRecord record) {
        //创建StringBuilder对象来存放后续需要打印的日志内容
        StringBuilder builder = new StringBuilder();
        //获取时间
        String dateStr = SDF.format(new Date());

        builder.append(dateStr);
        builder.append(" - ");
        //拼接日志级别
        builder.append(record.getLevel()).append(" - ");
        //拼接类名
        builder.append(record.getSourceClassName()).append('.');
        //拼接方法名
        builder.append(record.getSourceMethodName()).append(" - ");
        //拼接日志内容
        builder.append(record.getMessage().trim());
        //日志换行
        builder.append("\n\n");
        return builder.toString();
    }
}

/**
 * 日志记录器类
 */
@Deprecated
public class LogUtil {
    public static void initLogger(Logger logger) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");
            setFile(logger,dateFormat.format(new Date())+".log");
        } catch (IOException e) {
            System.err.println("日志文件初始化失败~日志不能记录到文件");
            e.printStackTrace();
        }
    }

    public static void log(Logger logger, Level level, String msg) {
        StackTraceElement caller = Thread.currentThread().getStackTrace()[2];
        logger.logp(level, caller.getClassName(),caller.getMethodName(),msg);
    }
    public static void log(Logger logger,Throwable e) {
        StackTraceElement[] traceElements = e.getStackTrace();
        StringBuffer message = new StringBuffer(e.getClass().getName() + ':');
        message.append(e.getMessage()).append('\n');
//        boolean flag = false; //指示上一步是否为自带方法
        int internalLines = 0;
        for(StackTraceElement trace:traceElements) {
            //注意排除java自带方法
            String className = trace.getClassName();
            if(className.startsWith("java.")||className.startsWith("sun.")||
                    className.startsWith("javax.")||className.startsWith("org.apache.tomcat")) {
                internalLines++;
            }
            else {
                if (internalLines > 0) {
                    message.append(String.format("<%d internal lines>\n",internalLines));
                    internalLines = 0;
                }
                message.append(trace).append('\n');
            }
        }
        if (internalLines > 0) //注意最后
            message.append(String.format("<%d internal lines>\n",internalLines));
        StackTraceElement caller = Thread.currentThread().getStackTrace()[2];
        logger.logp(Level.SEVERE, caller.getClassName(),caller.getMethodName(),message.toString());
    }
    static void setFile(Logger logger,String file) throws IOException {
        FileHandler fHandler = new FileHandler(file,true);
        fHandler.setFormatter(new LogFormatter());
        logger.addHandler(fHandler);
    }
}
