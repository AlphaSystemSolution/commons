package com.alphasystem.util;

import com.alphasystem.ApplicationException;
import com.alphasystem.BusinessException;
import com.alphasystem.SystemException;

import javax.swing.*;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.awt.*;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

import static com.alphasystem.util.AppUtil.XMLGregorianCalendarDateFormat.*;
import static com.alphasystem.util.IdGenerator.nextId;
import static java.lang.String.format;
import static java.lang.System.getProperty;
import static java.nio.file.FileSystems.newFileSystem;
import static java.nio.file.Files.createTempDirectory;
import static java.nio.file.Files.write;
import static java.nio.file.Paths.get;
import static javax.xml.datatype.DatatypeConstants.FIELD_UNDEFINED;

/**
 * @author syali
 */
public class AppUtil {

    public static final String NEW_LINE = System.getProperty("line.separator");
    public static final String TAB = "    ";
    public static final String SEPARATOR = File.separator;
    public static final String USER_DIR = getProperty("user.dir", ".");
    public static final File CURRENT_USER_DIR = new File(USER_DIR);
    public static final String USER_HOME = getProperty("user.home", USER_DIR);
    public static final File USER_TEMP_DIR = new File(getProperty(
            "java.io.tmpdir", USER_HOME));
    public static final File USER_HOME_DIR = new File(USER_HOME);
    private static ClassLoader classLoader = null;

    static {
        classLoader = Thread.currentThread().getContextClassLoader();
    }

    public static XMLGregorianCalendar calendarFromDate(Date date,
                                                        XMLGregorianCalendarDateFormat format) {
        if (date == null)
            return null;
        try {
            final GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(date);
            final XMLGregorianCalendar xmlCalendar = DatatypeFactory
                    .newInstance().newXMLGregorianCalendar(cal);
            switch (format) {
                case NO_TIME_TRUNCATION: // Time is unchanged. So it will have
                    // actual time
                    break;
                case TIME_SETTTO_ZERO: // Time part is set to zero
                    xmlCalendar.setTime(0, 0, 0, 0);
                    break;
                case TIME_PART_UNDEFINED: // return Date will not have time part
                    xmlCalendar.setHour(FIELD_UNDEFINED);
                    xmlCalendar.setMinute(FIELD_UNDEFINED);
                    xmlCalendar.setSecond(FIELD_UNDEFINED);
                    break;
                default:
                    break;
            }
            return xmlCalendar;
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static XMLGregorianCalendar calendarFromDateNoTimeTrunc(Date date) {
        return calendarFromDate(date, NO_TIME_TRUNCATION);
    }

    public static XMLGregorianCalendar calendarFromDateWithTimeSetToZero(
            Date date) {
        return calendarFromDate(date, TIME_SETTTO_ZERO);
    }

    public static XMLGregorianCalendar calendarFromDateWithTimeUndefined(
            Date date) {
        return calendarFromDate(date, TIME_PART_UNDEFINED);
    }

    /**
     * @param suffix
     * @return
     * @throws BusinessException
     */
    public static File createTempFile(String suffix) throws BusinessException {
        return createTempFile(suffix, false);
    }

    /**
     * @param suffix
     * @param createSubFolder
     * @return
     * @throws BusinessException
     */
    public static File createTempFile(String suffix, boolean createSubFolder)
            throws BusinessException {
        String tempFileName = nextId() + "-";
        Path parentPath = Paths.get(USER_TEMP_DIR.toURI());
        if (createSubFolder) {
            String subFolderPrefix = nextId();
            try {
                parentPath = createTempDirectory(parentPath, subFolderPrefix);
            } catch (IOException e) {
                throw new BusinessException("TEMP_FILE_NOT_CREATED",
                        format("Unable to create temp directory {%s} under {%s}", subFolderPrefix, parentPath));
            }
        }
        Path tempFile = null;
        try {
            tempFile = Files.createTempFile(parentPath, tempFileName, suffix);
        } catch (IOException e) {
            throw new BusinessException("TEMP_FILE_NOT_CREATED",
                    format("Unable to create temp file {%s}, under {%s}", tempFileName, parentPath));
        }

        return tempFile == null ? null : tempFile.toFile();
    }

    public static Date dateFromCalendar(XMLGregorianCalendar xmlCal) {
        if (xmlCal == null)
            return null;
        return xmlCal.toGregorianCalendar().getTime();
    }

    public static Date dateFromXmlString(String value,
                                         SimpleDateFormat dateFormat) {
        Date date = null;
        try {
            date = dateFormat.parse(value);
            final GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(date);
            final XMLGregorianCalendar xmlCalendar = DatatypeFactory
                    .newInstance().newXMLGregorianCalendar(cal);
            date = dateFromCalendar(xmlCalendar);
        } catch (ParseException e) {
        } catch (DatatypeConfigurationException e) {
        }
        return date;
    }

    /**
     * @param path
     * @return
     * @throws FileNotFoundException
     */
    public static File findFile(String path) throws FileNotFoundException {
        URL url = getUrl(path);

        String fileName = null;
        if (url != null) {
            fileName = url.getFile();
        } else {
            fileName = path;
        }

        try {
            fileName = URLDecoder.decode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        File file = null;
        if (fileName != null) {
            file = new File(fileName);
        }

        if (!file.exists()) {
            throw new FileNotFoundException("File \"" + file.getAbsolutePath()
                    + "\" does not exists.");
        }

        return file;

    }

    public static String generateRandomNumberString(int length) {
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        while (builder.length() < length) {
            int n = random.nextInt(10);
            while (builder.length() == 0 && n == 0) {
                n = random.nextInt(10);
            }
            builder.append(n);
        }
        return builder.toString();
    }

    /**
     * @param path
     * @return
     * @throws FileNotFoundException
     */
    public static String getAbsolutePath(String path)
            throws FileNotFoundException {
        return findFile(path).getAbsolutePath();
    }

    public static ImageIcon getIcon(String imagePath) {
        URL url = getUrl(imagePath);
        return url == null ? null : new ImageIcon(url);
    }

    public static Image getImage(String imagePath) {
        ImageIcon imageIcon = getIcon(imagePath);
        return imageIcon == null ? null : imageIcon.getImage();
    }

    public static int getRandomNumber(int start, int end) {
        return start + (int) ((end - start) * Math.random());
    }

    public static InputStream getResourceAsStream(String path) {
        return classLoader.getResourceAsStream(convertPath(path));
    }

    private static String convertPath(String path) {
        path = path.replace('.', '/');
        int lastIndex = path.lastIndexOf('/');
        path = path.substring(0, lastIndex) + "."
                + path.substring(lastIndex + 1, path.length());
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
        StringBuffer stackTrace = new StringBuffer();
        if (exception == null) {
            String result = stackTrace.toString();
            // reset the string buffer
            stackTrace.setLength(0);
            return result;
        }
        stackTrace.append(exception.getClass().getName() + ":"
                + exception.getMessage() + NEW_LINE);
        StackTraceElement[] ste = exception.getStackTrace();
        for (int i = 0; i < ste.length; i++) {
            stackTrace
                    .append("\tat " + getStackTraceElement(ste[i]) + NEW_LINE);
        }
        stackTrace.append(getStackTrace(exception.getCause())).append(NEW_LINE);
        return stackTrace.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param ste DOCUMENT ME!
     * @return DOCUMENT ME!
     */
    private static String getStackTraceElement(StackTraceElement ste) {
        StringBuffer result = new StringBuffer();
        result.append(ste.getClassName() + "." + ste.getMethodName() + "(");
        if (ste.isNativeMethod()) {
            result.append("Native Method");
        } else {
            String fileName = ste.getFileName();
            int lineNumber = ste.getLineNumber();
            if (fileName == null) {
                result.append("Unknown Source");
            } else {
                result.append(fileName);
            }
            if (lineNumber > 0) {
                result.append(":" + lineNumber);
            }
        }
        result.append(")");
        return result.toString();
    }

    public static URL getUrl(String path) {
        String resName = path.replace('.', '/');
        int lastIndex = resName.lastIndexOf("/");
        resName = resName.substring(0, lastIndex) + "."
                + resName.substring(lastIndex + 1, resName.length());

        URL url = null;
        if (classLoader != null) {
            url = classLoader.getResource(resName);
        } else {
            url = ClassLoader.getSystemResource(resName);
        }
        return url;
    }

    public static Class<?>[] initGenericClass(Class<?> thisClass) {
        Class<?> klass = thisClass;
        List<Class<?>> genericClasses = new ArrayList<Class<?>>();
        while (true) {
            Type type = klass.getGenericSuperclass();
            if (type == null) {
                throw new RuntimeException("Could not find generic type: "
                        + thisClass.getName());
            }
            if (type instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Type[] types = parameterizedType.getActualTypeArguments();
                int length = types.length;
                if (length <= 0) {
                    break;
                } else {
                    for (Type t : types) {
                        boolean isClass = Class.class.isAssignableFrom(t
                                .getClass());
                        if (isClass) {
                            genericClasses.add((Class<?>) t);
                        }
                    }
                    break;
                }
            }
            klass = klass.getSuperclass();
            if (klass == null) {
                throw new RuntimeException("Could not find generic type: "
                        + thisClass.getName());
            }
        }
        return genericClasses.toArray(new Class<?>[0]);
    }

    public static boolean isGivenType(Class<?> supperClass, Object object) {
        return object != null
                && supperClass.isAssignableFrom(object.getClass());
    }

    public static String randomUUID() {
        return randomUUID(false);
    }

    public static String randomUUID(boolean toUpperCase) {
        IdGenerator idGenerator = IdGenerator.getInstance();
        String uuid = IdGenerator.format(idGenerator.next());
        if (toUpperCase) {
            uuid = uuid.toUpperCase();
        }
        return uuid;
    }

    public static String readFile(File file) throws ApplicationException {
        StringBuilder builder = new StringBuilder();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));

            String line = reader.readLine();
            while (line != null) {
                builder.append(line);
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            throw new SystemException(e.getMessage(), e);
        } catch (IOException e) {
            throw new SystemException(e.getMessage(), e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return builder.toString();
    }

    /**
     * @param inFile
     * @return
     * @throws ApplicationException
     */
    public static Object readObject(File inFile) throws ApplicationException {
        BufferedInputStream bin = null;
        XMLDecoder decoder = null;
        Object object = null;
        try {
            bin = new BufferedInputStream(new FileInputStream(inFile));
            decoder = new XMLDecoder(bin);
            object = decoder.readObject();
        } catch (Exception e) {
            throw new SystemException(e.getMessage(), e);
        } finally {
            if (decoder != null) {
                decoder.close();
            }
            if (bin != null) {
                try {
                    bin.close();
                } catch (IOException e) {
                }
            }
        }
        return object;
    }

    /**
     * @param outFile
     * @param object
     * @throws ApplicationException
     */
    public static void writeObject(File outFile, Object object)
            throws ApplicationException {
        BufferedOutputStream bout = null;
        XMLEncoder encoder = null;
        try {
            bout = new BufferedOutputStream(new FileOutputStream(outFile));
            encoder = new XMLEncoder(bout);
            encoder.writeObject(object);
        } catch (Exception ex) {
            throw new SystemException(ex.getMessage(), ex);
        } finally {
            if (encoder != null) {
                encoder.close();
            }
            if (bout != null) {
                try {
                    bout.close();
                } catch (IOException e) {
                }
            }
        }
    }

    public static List<String> readAllLines(String resourceName) throws IOException, URISyntaxException {
        FileSystem fs = null;
        List<String> lines = null;
        try {
            URL url = getResource(resourceName);
            URI uri = url.toURI();
            Path path;
            String[] split = uri.toString().split("!");
            if (split != null && split.length > 1) {
                fs = newFileSystem(URI.create(split[0]), new HashMap());
                path = fs.getPath(split[1]);
            } else {
                path = get(uri);
            }
            lines = Files.readAllLines(path);
        } catch (IOException | URISyntaxException e) {
            throw e;
        } finally {
            if (fs != null) {
                try {
                    fs.close();
                } catch (IOException e) {
                }
            }
        }

        return lines;
    }

    public static String readAllLinesAsString(String resourceName) throws IOException, URISyntaxException {
        List<String> lines = readAllLines(resourceName);
        StringBuilder builder = new StringBuilder();
        builder.append(lines.get(0));
        for (int i = 1; i < lines.size(); i++) {
            builder.append(NEW_LINE).append(lines.get(i));
        }
        return builder.toString();
    }

    public static void copyResources(File destDir, String resourceDir, String resourceName) {
        try {
            Path path = get(destDir.getAbsolutePath(), resourceName);
            List<String> lines = readAllLines(format("%s.%s", resourceDir, resourceName));
            write(path, lines);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static String findCommonPath(String... paths) {
        String commonPath = "";
        String[][] folders = new String[paths.length][];
        for (int i = 0; i < paths.length; i++) {
            folders[i] = paths[i].split("/"); //split on file separator
        }
        for (int j = 0; j < folders[0].length; j++) {
            String thisFolder = folders[0][j]; //grab the next folder name in the first path
            boolean allMatched = true; //assume all have matched in case there are no more paths
            for (int i = 1; i < folders.length && allMatched; i++) { //look at the other paths
                if (folders[i].length < j) { //if there is no folder here
                    allMatched = false; //no match
                    break; //stop looking because we've gone as far as we can
                }
                //otherwise
                allMatched &= folders[i][j].equals(thisFolder); //check if it matched
            }
            if (allMatched) { //if they all matched this folder name
                commonPath += thisFolder + "/"; //add it to the answer
            } else {//otherwise
                break;//stop looking
            }
        }
        return commonPath;
    }

    public static String findRelativePath(String base, String path) {
        return new File(base).toURI().relativize(new File(path).toURI()).getPath();
    }

    public enum XMLGregorianCalendarDateFormat {
        NO_TIME_TRUNCATION, TIME_SETTTO_ZERO, TIME_PART_UNDEFINED
    }

}
