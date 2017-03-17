package com.fanyu.boundless.widget.imagepager.photoview.log;

public final class LogManager {
    private static Logger logger = new LoggerDefault();

    public static void setLogger(Logger newLogger) {
        logger = newLogger;
    }

    public static Logger getLogger() {
        return logger;
    }
}
