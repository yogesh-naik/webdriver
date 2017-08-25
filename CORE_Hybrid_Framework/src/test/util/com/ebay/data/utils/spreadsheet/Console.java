package com.ebay.data.utils.spreadsheet;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Class to handle customized logging in the console.
 * 
 * <pre>
 * <b>Sample Usage:</b>
 * Console.log("Log this line");
 * Console.logBlankLines(2);
 * Console.log("Log this line too");
 * 
 * <b>OutPut:</b>
 * 06-03@12:10 45-811 :  Log this line
 * 
 * 
 * 06-03@12:10 45-811 :  Log this line too
 * </pre>
 * 
 * @author arkannan (Aravind Kannan)
 * 
 */
public class Console {
  private static String FRAMEWORK = "Breeze";
  private static String DATE_FORMAT = "MM-dd@HH:mm-ss";
  
  public enum LogType {
    INFO, WARNING, FATAL, PLAIN_WITHOUT_TIMESTAMP;
  }

  /**
   * Clean logs into the console
   * 
   * @param msg
   */
  public static void log(LogType logType, final String msg)
      throws RuntimeException {
    if (logType.equals(LogType.PLAIN_WITHOUT_TIMESTAMP)) {
      System.out.println(msg);
    } else {
      String logDetails = String.format("[%s-%s]%s: %s",
          FRAMEWORK,
          logType.toString(),
          new SimpleDateFormat(DATE_FORMAT).format(new Date()),
          msg);
      if (logType.equals(LogType.INFO)) {
        System.out.println(logDetails);
      } else {
        System.err.println(logDetails);
      }
    }

  }

  public static void log(LogType logType, final String msg, Object o)
      throws RuntimeException {
    if (logType.equals(LogType.PLAIN_WITHOUT_TIMESTAMP)) {
      System.out.println(o.getClass().getSimpleName() + "|" + msg);
    } else {
      String logDetails = String.format("[%s-%s]%s|%s: %s",
          FRAMEWORK,
          logType.toString(),
          new SimpleDateFormat(DATE_FORMAT).format(new Date()),
          o.getClass().getSimpleName(),
          msg);
      if (logType.equals(LogType.INFO)) {
        System.out.println(logDetails);
      } else {
        System.err.println(logDetails);
      }
    }
  }

  public static void debug(String msg, Object o) {
    if ("ON".equals(System.getProperty("debug"))) {
      String logDetails = String.format("[%s-%s]%s|%s: %s",
          FRAMEWORK,
          "DEBUG",
          new SimpleDateFormat(DATE_FORMAT).format(new Date()),
          o.getClass().getSimpleName(),
          msg);
      System.out.println(logDetails);
    }
  }

  public static void debug(String msg) {
    if ("ON".equals(System.getProperty("debug"))) {
      String logDetails = String.format("[%s-%s]%s: %s",
          FRAMEWORK,
          "DEBUG",
          new SimpleDateFormat(DATE_FORMAT).format(new Date()),
          msg);
      System.out.println(logDetails);
    }
  }

  /**
   * Logs blank lines in console
   */
  public static void logBlankLines(int noOfBlankLines) throws RuntimeException {
    for (int i = 0; i < noOfBlankLines; i++) {
      System.out.println();
    }
  }

}
