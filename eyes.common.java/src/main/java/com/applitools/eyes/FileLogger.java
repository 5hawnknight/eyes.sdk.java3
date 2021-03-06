/*
 * Applitools software.
 */
package com.applitools.eyes;

import com.applitools.utils.ArgumentGuard;
import com.applitools.utils.GeneralUtils;

import java.io.*;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Writes log messages to a file.
 */
@SuppressWarnings("UnusedDeclaration")
public class FileLogger implements LogHandler {

    private final boolean isVerbose;
    private final String filename;
    private final boolean append;
    private BufferedWriter fileWriter;

    /**
     * Creates a new FileHandler instance.
     * @param filename The file in which to save the logs.
     * @param append Whether to append the logs if the current file exists,
     *               or to overwrite the existing file.
     * @param isVerbose Whether to handle or ignore verbose log messages.
     */
    public FileLogger(String filename, boolean append, boolean isVerbose) {
        ArgumentGuard.notNullOrEmpty(filename, "filename");
        this.filename = filename;
        this.append = append;
        this.isVerbose = isVerbose;
        fileWriter = null;
    }

    /**
     * See {@link #FileLogger(String, boolean, boolean)}.
     * {@code filename} defaults to {@code eyes.log}, append defaults to
     * {@code true}.
     *
     * @param isVerbose Whether to handle or ignore verbose log messages.
     */
    @SuppressWarnings("UnusedDeclaration")
    public FileLogger(boolean isVerbose) {
        this("eyes.log", true, isVerbose);
    }

    /**
     * Open the log file for writing.
     */
    public void open() {
        try {
            if (fileWriter != null) {
                //noinspection EmptyCatchBlock
                try {
                    fileWriter.close();
                } catch (Exception e) {}
            }
            File file = new File(filename);
            File path = file.getParentFile();
            if (!path.exists()) {
                System.out.println("No Folder");
                boolean success = path.mkdirs();
                System.out.println("Folder created");
            }

            fileWriter = new BufferedWriter(new FileWriter(file, append));
        } catch (IOException e) {
            throw new EyesException("Failed to create log file!", e);
        }
    }

    /**
     * Handle a message to be logged.
     * @param verbose Whether this message is flagged as verbose or not.
     * @param logString The string to log.
     */
    public void onMessage(boolean verbose, String logString) {
        if (fileWriter != null && (!verbose || this.isVerbose)) {

            String currentTime = GeneralUtils.toISO8601DateTime(
                    Calendar.getInstance(TimeZone.getTimeZone("UTC")));

            try {
                fileWriter.write(currentTime + " Eyes: " + logString);
                fileWriter.newLine();
                fileWriter.flush();
            } catch (IOException e) {
                throw new EyesException("Failed to write log to file!", e);
            }
        }
    }

    /**
     * Close the log file for writing.
     */
    public void close() {
        //noinspection EmptyCatchBlock
        try {
            if (fileWriter !=null) {
                fileWriter.close();
            }
        } catch (IOException e) {}
        fileWriter = null;
    }
}
