package BukkitBridge;

public class Logger {
    private Plugin plugin;
    private java.util.logging.Logger logger;

    public Logger(Plugin plugin, java.util.logging.Logger logger) {
        this.plugin = plugin;
        this.logger = logger;
    }

    public void logError(Throwable e) {
        this.logError(e.getMessage(),e.getStackTrace());
        Throwable cause = e.getCause();
        if (cause != null) {
            this.logger.warning("Caused by:");
            logError(cause);
        }
    }

    public void logError(String message, Throwable e) {
        this.logError(message + e.getMessage(),e.getStackTrace());
        Throwable cause = e.getCause();
        if (cause != null) {
            this.logger.warning("Caused by:");
            logError(cause);
        }
    }

    public void logError(String message, StackTraceElement[] elements) {
        this.logger.warning("Exception: " + message);
        for (int i=0;i<elements.length;i++) {
            StackTraceElement elem = elements[i];
            this.logger.warning("\t\t" + elem.toString());
        }
    }
}
