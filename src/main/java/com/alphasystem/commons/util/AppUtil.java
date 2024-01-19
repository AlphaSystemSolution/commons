package com.alphasystem.commons.util;

import com.alphasystem.commons.SystemException;
import com.alphasystem.commons.util.nio.NIOFileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static java.lang.System.getProperty;

/**
 * @author syali
 */
public class AppUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppUtil.class);

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

    public static Object initObject(String fullQualifiedClassName) throws SystemException {
        return initObject(fullQualifiedClassName, null, null);
    }

    public static Object initObject(String fullQualifiedClassName,
                                    Class<?>[] parameterTypes,
                                    Object[] args) throws SystemException {
        try {
            return initObject(Class.forName(fullQualifiedClassName), parameterTypes, args);
        } catch (ClassNotFoundException ex) {
            throw new SystemException(String.format("Could not initialize class of type \"%s\".", fullQualifiedClassName), ex);
        }
    }

    public static Object initObject(Class<?> clazz, Class<?>[] parameterTypes, Object[] args) throws SystemException {
        try {
            return clazz.getConstructor(parameterTypes).newInstance(args);
        } catch (Exception ex) {
            throw new SystemException(String.format("Could not initialize class of type \"%s\".", clazz.getName()), ex);
        }
    }

    public static Object invokeMethod(Object obj, String methodName) {
        Object value = null;
        final Method method = getMethod(obj, methodName);
        if (method != null) {
            try {
                value = method.invoke(obj);
            } catch (IllegalAccessException | InvocationTargetException e) {
                // ignore
            }
        }
        return value;
    }

    private static Method getMethod(Object obj, String methodName) {
        Method method = null;
        try {
            method = obj.getClass().getMethod(methodName);
        } catch (NoSuchMethodException e) {
            // ignore
        }
        return method;
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

    public static Enumeration<URL> readResources(String resourceName) throws SystemException {
        final Enumeration<URL> resources;
        try {
            resources = classLoader.getResources(convertPath(resourceName));
        } catch (IOException e) {
            throw new SystemException("Could not load resource", e);
        }
        return resources;
    }

    /**
     * Process all resources for given resource.
     *
     * @param resourceName name of system resource, must be a file
     * @param consumer     A function that takes a url and returns some value R
     * @param <R>          Return type of consumer function
     * @return A collection of R
     * @throws SystemException if anything happen during processing
     */
    public static <R> List<R> processResource(String resourceName, Function<URL, R> consumer) throws SystemException {
        LOGGER.debug("Processing resource: {}", resourceName);

        final var results = new ArrayList<R>();
        final var path = Paths.get(resourceName);
        if (Files.exists(path)) {
            // absolute path
            try {
                results.add(consumer.apply(path.toUri().toURL()));
            } catch (MalformedURLException e) {
                throw new SystemException(e);
            }
        } else {
            final var resources = readResources(resourceName);
            while (resources.hasMoreElements()) {
                final var url = resources.nextElement();
                if (Objects.isNull(url)) {
                    LOGGER.warn("Resource not found for: {}", resourceName);
                    throw new SystemException(String.format("Resource not found for: %s.", resourceName));
                }
                results.add(consumer.apply(url));
            }
        }
        return results;
    }


    /**
     * Process recursively given resource according to given "consumer" function.
     *
     * @param resourceName name of system resource, must be a directory
     * @param consumer     A function that takes a path and returns some value R
     * @param <R>          Return type of consumer function
     * @return A collection of R
     * @throws SystemException if anything happen during processing
     */
    public static <R> List<R> processResourceDirectory(String resourceName, Function<Path, R> consumer) throws SystemException {
        LOGGER.debug("Processing resource directory: {}", resourceName);
        final var resources = readResources(resourceName);

        final var results = new ArrayList<R>();
        while (resources.hasMoreElements()) {
            final var url = resources.nextElement();
            if (Objects.isNull(url)) {
                LOGGER.warn("Resource not found for: {}", resourceName);
                throw new SystemException(String.format("Resource not found for: %s.", resourceName));
            }

            LOGGER.debug("Resource URL: {}", url);
            Path path;
            try {
                path = Paths.get(url.toURI());
            } catch (URISyntaxException e) {
                throw new SystemException(e.getMessage(), e);
            } catch (FileSystemNotFoundException ex) {
                // we are running from a jar
                // copy content into temp directory
                try {
                    path = Files.createTempDirectory("alphasystem-");
                    path.toFile().deleteOnExit();
                    NIOFileUtils.copyDir(path, resourceName, AppUtil.class);
                } catch (IOException | URISyntaxException e) {
                    throw new SystemException(e.getMessage(), e);
                }
            }
            results.addAll(processDirectory(path, consumer));
        }
        return results;
    }

    /**
     * Process recursively given directory according to given "consumer" function.
     *
     * @param dir      directory path
     * @param consumer A function that takes a path and returns some value R
     * @param <R>      <R> Return type of consumer function
     * @return A collection of R
     * @throws SystemException if anything happen during processing
     */
    public static <R> List<R> processDirectory(Path dir, Function<Path, R> consumer) throws SystemException {
        if (!Files.isDirectory(dir)) {
            throw new SystemException(String.format("Path \"%s\" is not a directory", dir.getFileName()));
        }
        final List<R> results;
        try (final var stream = Files.walk(dir).sorted().filter(Files::isRegularFile)) {
            results = new ArrayList<>(stream.map(consumer).toList());
        } catch (Exception ex) {
            throw new SystemException("Unable to process.", ex);
        }
        return results;
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
