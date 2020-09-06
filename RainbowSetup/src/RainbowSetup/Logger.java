package RainbowSetup;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 12/13/14
 * Time: 6:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class Logger extends java.util.logging.Logger {
    String logFile = "/bukkit_log.log";

    public Logger() {
        super("",null);
    }

    public void info(String message) {
        writeFile("INFO: " + message);
        System.out.println("INFO: " + message);
    }

    public void warning(String message) {
        writeFile("WARN: " + message);
        System.out.println("WARN: " + message);
    }

    public void logError(Throwable e) {
        this.logError(e.getMessage(),e.getStackTrace());
        Throwable cause = e.getCause();
        if (cause != null) {
            writeFile("Caused by:");
            System.out.println("Caused by:");
            logError(cause);
        }
    }

    public void displayStackTrace() {
        try {
            throw new Exception();
        } catch (Exception e) {
            this.logError(e);
        }
    }

    public void logError(String message, StackTraceElement[] elements) {
        writeFile("Exception: " + message);
        System.out.println("Exception: " + message);
        for (int i=0;i<elements.length;i++) {
            StackTraceElement elem = elements[i];
            writeFile(elem.toString());
            System.out.println("\t\t" + elem.toString());
        }
    }

    public void writeFile(String message) {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)));
            //out.println(message);
            out.close();
        } catch (IOException e) {
            System.out.println("Can't write to file! " + e.getMessage());
        }
    }
}
