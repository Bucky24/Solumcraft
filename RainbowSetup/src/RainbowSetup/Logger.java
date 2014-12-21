package RainbowSetup;

import com.sun.deploy.util.ArrayUtil;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: solum
 * Date: 12/13/14
 * Time: 6:56 PM
 * To change this template use File | Settings | File Templates.
 */
public class Logger {
    String logFile = "/testbed/bukkit_log.log";

    public void info(String message) {
        writeFile("INFO: " + message);
        System.out.println("INFO: " + message);
    }

    public void warning(String message) {
        writeFile("WARN: " + message);
        System.out.println("WARN: " + message);
    }

    public void logError(Exception e) {
        writeFile("Exception: " + e.getMessage());
        System.out.println("Exception: " + e.getMessage());
        StackTraceElement[] elements = e.getStackTrace();
        for (int i=0;i<elements.length;i++) {
            StackTraceElement elem = elements[i];
            writeFile(elem.toString());
            System.out.println("\t\t" + elem.toString());
        }
    }

    public void writeFile(String message) {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(logFile, true)));
            out.println(message);
            out.close();
        } catch (IOException e) {
            System.out.println("Can't write to file! " + e.getMessage());
        }
    }
}
