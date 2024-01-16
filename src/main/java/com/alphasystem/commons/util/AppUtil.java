package com.alphasystem.commons.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Objects;

import static java.lang.System.getProperty;

/**
 * @author syali
 */
public class AppUtil {

    public static final String NEW_LINE = System.lineSeparator();
    public static final String TAB = "    ";
    public static final String SEPARATOR = File.separator;
    public static final String USER_DIR = getProperty("user.dir", ".");
    public static final File CURRENT_USER_DIR = new File(USER_DIR);
    public static final String USER_HOME = getProperty("user.home", USER_DIR);
    public static final File USER_TEMP_DIR = new File(getProperty("java.io.tmpdir", USER_HOME));
    public static final File USER_HOME_DIR = new File(USER_HOME);
    private static ClassLoader classLoader = null;

    static {
        classLoader = Thread.currentThread().getContextClassLoader();
    }

    public static InputStream getResourceAsStream(String path) {
        return classLoader.getResourceAsStream(convertPath(path));
    }

    private static String convertPath(String path) {
        path = path.replace('.', '/');
        int lastIndex = path.lastIndexOf('/');
        if (lastIndex >= 0) {
            path = path.substring(0, lastIndex) + "." + path.substring(lastIndex + 1);
        }
        return path;
    }

    public static Enumeration<URL> getResources(String path) {
        try {
            return classLoader.getResources(convertPath(path));
        } catch (IOException e) {
            // logger.error(e.getMessage(), e);
        }
        return null;
    }

    public static URL getResource(String path) {
        return classLoader.getResource(convertPath(path));
    }

    public static String getStackTrace(Throwable exception) {
        final var stackTrace = new StringBuilder();
        if (exception == null) {
            String result = stackTrace.toString();
            // reset the string buffer
            stackTrace.setLength(0);
            return result;
        }
        stackTrace.append(exception.getClass().getName()).append(":").append(exception.getMessage()).append(NEW_LINE);
        StackTraceElement[] ste = exception.getStackTrace();
        for (StackTraceElement stackTraceElement : ste) {
            stackTrace.append("\tat ").append(getStackTraceElement(stackTraceElement)).append(NEW_LINE);
        }
        stackTrace.append(getStackTrace(exception.getCause())).append(NEW_LINE);
        return stackTrace.toString();
    }

    private static String getStackTraceElement(StackTraceElement ste) {
        final var result = new StringBuilder();
        result.append(ste.getClassName()).append(".").append(ste.getMethodName()).append("(");
        if (ste.isNativeMethod()) {
            result.append("Native Method");
        } else {
            String fileName = ste.getFileName();
            int lineNumber = ste.getLineNumber();
            result.append(Objects.requireNonNullElse(fileName, "Unknown Source"));
            if (lineNumber > 0) {
                result.append(":").append(lineNumber);
            }
        }
        result.append(")");
        return result.toString();
    }

    public static URL getUrl(String path) {
        String resName = path.replace('.', '/');
        int lastIndex = resName.lastIndexOf("/");
        if (lastIndex >= 0) {
            resName = resName.substring(0, lastIndex) + "." + resName.substring(lastIndex + 1);
        }
        URL url;
        if (classLoader != null) {
            url = classLoader.getResource(resName);
        } else {
            url = ClassLoader.getSystemResource(resName);
        }
        return url;
    }

    public static boolean isInstanceOf(Class<?> supperClass, Object object) {
        return Objects.nonNull(object) && supperClass.isAssignableFrom(object.getClass());
    }
}
