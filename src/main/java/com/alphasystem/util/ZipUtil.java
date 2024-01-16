package com.alphasystem.util;

import com.alphasystem.commons.ApplicationException;
import com.alphasystem.commons.BusinessException;
import com.alphasystem.commons.SystemException;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import static com.alphasystem.util.FileUtil.copyOutputStream;
import static java.lang.String.format;
import static org.apache.commons.io.IOUtils.copyLarge;

/**
 * @author Syed Farhan Ali
 */
@SuppressWarnings("ALL")
public class ZipUtil {

    private static final String ZIP_DIR_SEPARATOR = "/";

    public static void archiveFile(File fileToArchive, File archiveFile)
            throws ApplicationException {
        archiveFile(new File[]{fileToArchive}, archiveFile);
    }

    private static void archiveFile(File archiveFile, ZipFileEntry... entries)
            throws ApplicationException {
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(archiveFile));
            for (ZipFileEntry zipFileEntry : entries) {
                archiveFile(zos, zipFileEntry.getFile(), zipFileEntry.getName());
            }
        } catch (FileNotFoundException e) {
            throw new BusinessException(e.getMessage(), e);
        } catch (IOException e) {
            throw new SystemException(e.getMessage(), e);
        } finally {
            if (zos != null) {
                try {
                    zos.close();
                } catch (IOException e) {
                    System.err.println("Failed to close output stream.");
                }
            }
        }
    }

    public static void archiveFile(File[] filesToArchive, File archiveFile)
            throws ApplicationException {
        ZipFileEntry[] entries = getFilesToArchive(filesToArchive);
        archiveFile(archiveFile, entries);
    }

    private static void archiveFile(ZipOutputStream zos, File file, String entryName) throws IOException {
        ZipEntry ze = new ZipEntry(entryName);
        zos.putNextEntry(ze);
        if (entryName.endsWith(ZIP_DIR_SEPARATOR)) {
            return;
        }
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(
                file));
        copyOutputStream(zos, in);
        zos.closeEntry();
        try {
            in.close();
        } catch (Exception e) {
            System.err.println("Failed to close input stream.");
        }
    }

    public static void extractFile(String pathname, String entryName, File outFile) throws ApplicationException {
        ZipFile zipFile = null;
        BufferedOutputStream out = null;
        try {
            zipFile = new ZipFile(pathname);
            ZipEntry ze = zipFile.getEntry(entryName);
            out = new BufferedOutputStream(new FileOutputStream(outFile));
            copyLarge(zipFile.getInputStream(ze), out);
        } catch (IOException e) {
            throw new SystemException(e.getMessage(), e);
        } finally {
            if (zipFile != null) {
                try {
                    zipFile.close();
                } catch (IOException e) {
                    System.err.println("Failed to close zip file.");
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    System.err.println("Failed to close input stream.");
                }
            }
        }
    }

    public static Map<String, ZipEntry> extractZipFile(File outDir, String pathname) {
        final var zipEntries = new HashMap<String, ZipEntry>();
        ZipFile zipFile = null;

        try {
            zipFile = new ZipFile(pathname);
        } catch (IOException ex) {
            //
        }

        if (zipFile == null) {
            System.err.printf("Could not open zip file {%s}%n", pathname);
            return null;
        }
        Enumeration<?> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            zipEntries.put(entry.getName(), entry);
            if (entry.isDirectory()) {
                File parent = new File(outDir, entry.getName());
                parent.mkdirs();
                continue;
            }
            File extractedFile = new File(outDir, entry.getName());

            BufferedOutputStream output = null;
            try {
                File parentFile = extractedFile.getParentFile();
                if (parentFile != null && !parentFile.exists()) {
                    parentFile.mkdirs();
                }
                output = new BufferedOutputStream(new FileOutputStream(
                        extractedFile));

                // copy actual data
                copyLarge(zipFile.getInputStream(entry), output);
            } catch (IOException ex) {
                System.err.printf("Error occurred: %s.%n", ex.getMessage());
            } finally {
                try {
                    if (output != null) {
                        output.close();
                    }
                } catch (Exception e) {
                    //
                }
            }

        } // end of "while"

        try {
            zipFile.close();
        } catch (IOException ex) {
            //
        }

        return zipEntries;
    }

    private static ZipFileEntry[] getFilesToArchive(File[] filesToArchive) {
        List<ZipFileEntry> entries = new ArrayList<ZipFileEntry>();
        for (File file : filesToArchive) {
            if (file.isDirectory()) {
                String entryName = format("%s%s", file.getName(),
                        ZIP_DIR_SEPARATOR);
                entries.add(new ZipFileEntry(file, entryName));
                getFilesToArchive(entries, file, entryName);
            } else {
                entries.add(new ZipFileEntry(file, file.getName()));
            }
        }
        return entries.toArray(new ZipFileEntry[0]);
    }

    private static void getFilesToArchive(List<ZipFileEntry> entries, File dir, String rootName) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                String baseName = format("%s%s", rootName, file.getName());
                if (file.isDirectory()) {
                    String entryName = format("%s%s", baseName, ZIP_DIR_SEPARATOR);
                    entries.add(new ZipFileEntry(file, entryName));
                    getFilesToArchive(entries, file, entryName);
                } else {
                    entries.add(new ZipFileEntry(file, baseName));
                }
            }
        }
    }
}
