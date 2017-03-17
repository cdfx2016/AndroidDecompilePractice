package cn.finalteam.toolsfinal.logger;

public class LoggerFactory {
    public static LoggerPrinter getFactory(String tag, boolean debug) {
        LoggerPrinter printer = new LoggerPrinter();
        printer.init(tag);
        LogLevel level = LogLevel.NONE;
        if (debug) {
            level = LogLevel.FULL;
        }
        printer.getSettings().methodCount(3).logLevel(level);
        return printer;
    }
}
