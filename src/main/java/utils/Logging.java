package utils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class Logging {
    final static int debugMode = 3; //bit 1 = write normal, 2 = write exceptions
    final static int writeTo = 3; //bit 1 = println, bit 2 = file
    static BufferedWriter writer;

    static {
        try {
            writer = new BufferedWriter(new FileWriter("log.txt"));
        } catch (IOException e) {
            Logging.exceptionLog(e.getStackTrace());
        }
    }

    public static <E> void log(E msg) {
        if((debugMode & 0x1) > 0 && (writeTo & 0x1) > 0) {
            System.out.println(msg);
        }
        if((debugMode & 0x1) > 0 && (writeTo & 0x2) > 0) {
            try {
                writer.write(msg.toString() + "\r\n");
            } catch (IOException e) {
                Logging.exceptionLog(e.getStackTrace());
            }
        }
    }

    public static <E> void exceptionLog(E msg) {
        if((debugMode & 0x2) > 0 && (writeTo & 0x1) > 0) {
            System.out.println(msg);
        }
        if((debugMode & 0x2) > 0 && (writeTo & 0x2) > 0) {
            try {
                writer.write(msg.toString() + "\r\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void exceptionLog(StackTraceElement[] trace) {
        if((debugMode & 0x2) > 0 && (writeTo & 0x1) > 0) {
            System.out.println(Arrays.toString(trace));
        }
        if((debugMode & 0x2) > 0 && (writeTo & 0x2) > 0) {
            try {
                writer.write(Arrays.toString(trace)+ "\r\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void exceptionLog(Throwable e) {
        if((debugMode & 0x2) > 0 && (writeTo & 0x1) > 0) {
            System.out.println(e.toString());
            System.out.println(Arrays.toString(e.getStackTrace()));
        }
        if((debugMode & 0x2) > 0 && (writeTo & 0x2) > 0) {
            try {
                writer.write(e.toString() + "\r\n");
                writer.write(Arrays.toString(e.getStackTrace())+ "\r\n");
            } catch (IOException ei) {
                ei.printStackTrace();
            }
        }
    }

    public static void flushLog() {
        try {
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
