package com.logistics.utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static BufferedWriter fileWriter = null;
    private static final Object lock = new Object(); // For thread safety

    // Log levels
    private static final int INFO_LEVEL = 1;
    private static final int WARN_LEVEL = 2;
    private static final int ERROR_LEVEL = 3;

    // Current logging level
    private static int currentLogLevel = INFO_LEVEL;

    /**
     * Initializes file logging.
     *
     * @param logFilePath The path to the log file.
     */
    public static void initializeFileLogging(String logFilePath) {
        try {
            fileWriter = new BufferedWriter(new FileWriter(logFilePath, true));
            info("File logging initialized. Writing logs to: " + logFilePath);
        } catch (IOException e) {
            error("Failed to initialize file logging: " + e.getMessage());
        }
    }

    /**
     * Logs an informational message.
     *
     * @param message The message to log.
     */
    public static void info(String message) {
        if (currentLogLevel <= INFO_LEVEL) {
            log("INFO", message);
        }
    }

    /**
     * Logs a warning message.
     *
     * @param message The message to log.
     */
    public static void warn(String message) {
        if (currentLogLevel <= WARN_LEVEL) {
            log("WARN", message);
        }
    }

    /**
     * Logs an error message.
     *
     * @param message The message to log.
     */
    public static void error(String message) {
        if (currentLogLevel <= ERROR_LEVEL) {
            log("ERROR", message);
        }
    }

    /**
     * Logs a message to the console and optionally to a file.
     *
     * @param level   The log level (e.g., INFO, WARN, ERROR).
     * @param message The log message.
     */
    private static void log(String level, String message) {
        String formattedMessage = formatLogMessage(level, message);
        System.out.println(formattedMessage);

        synchronized (lock) { // Ensure thread safety
            if (fileWriter != null) {
                try {
                    fileWriter.write(formattedMessage);
                    fileWriter.newLine();
                    fileWriter.flush();
                } catch (IOException e) {
                    System.err.println("ERROR: Failed to write log to file: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Formats a log message with a timestamp and log level.
     *
     * @param level   The log level (e.g., INFO, WARN, ERROR).
     * @param message The log message.
     * @return A formatted log message.
     */
    private static String formatLogMessage(String level, String message) {
        String timestamp = LocalDateTime.now().format(formatter);
        return String.format("[%s] [%s]: %s", timestamp, level, message);
    }

    /**
     * Sets the current log level.
     *
     * @param level The desired log level (1 = INFO, 2 = WARN, 3 = ERROR).
     */
    public static void setLogLevel(int level) {
        currentLogLevel = level;
    }

    /**
     * Closes the file writer if file logging is enabled.
     */
    public static void closeFileLogging() {
        synchronized (lock) {
            if (fileWriter != null) {
                try {
                    fileWriter.close();
                    info("File logging closed.");
                } catch (IOException e) {
                    System.err.println("ERROR: Failed to close log file: " + e.getMessage());
                }
            }
        }
    }
}
