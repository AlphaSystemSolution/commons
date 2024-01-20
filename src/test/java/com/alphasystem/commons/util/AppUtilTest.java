package com.alphasystem.commons.util;

import com.alphasystem.commons.SystemException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class AppUtilTest {

    private static final String RESOURCE_DIR_NAME = "files";
    private static final String RESOURCE_NAME = "file1.txt";

    @Test
    public void testProcessDirectory() {
        try {
            final var results = AppUtil.processResourceDirectory(RESOURCE_DIR_NAME, AppUtilTest::readLines);

            final var fileNames = results.stream().map(fileInfo -> fileInfo.name).toList();
            final var expectedFileNames = Arrays.asList("file1.txt", "file2.txt", "sub-dir/file1.txt");
            Assertions.assertIterableEquals(expectedFileNames, fileNames);

            final var actualLines = results.stream().map(fileInfo -> fileInfo.lines).flatMap(List::stream).toList();
            final var expectedLines = Arrays.asList("File1 Line1", "File1 Line2", "File2 Line1", "File2 Line2", "File3 Line1", "File3 Line2");
            Assertions.assertIterableEquals(expectedLines, actualLines);
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testProcessNonExistingDirectory() {
        try {
            final var results = AppUtil.processResourceDirectory("does-not-exists", AppUtilTest::readLines);
            Assertions.assertTrue(results.isEmpty());
        } catch (SystemException e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testProcessFileAsDirectory() {
        try {
            final var results = AppUtil.processResourceDirectory(String.format("%s.%s", RESOURCE_DIR_NAME, RESOURCE_NAME), AppUtilTest::readLines);
            Assertions.assertTrue(results.isEmpty());
        } catch (SystemException e) {
            final var message = e.getMessage();
            Assertions.assertEquals(String.format("Path \"%s\" is not a directory", RESOURCE_NAME), message);
        }
    }

    @Test
    public void testProcessResource() {
        try {
            final var results = AppUtil.processResource(String.format("%s.%s", RESOURCE_DIR_NAME, RESOURCE_NAME), AppUtilTest::readLines);
            Assertions.assertEquals(1, results.size());
            final var fileInfo = results.get(0);
            final var expected = new FileInfo(RESOURCE_NAME, Arrays.asList("File1 Line1", "File1 Line2"));
            Assertions.assertEquals(expected, fileInfo);
        } catch (SystemException e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testProcessDirectoryAsFile() {
        try {
            AppUtil.processResource(RESOURCE_DIR_NAME, AppUtilTest::readLines);
            Assertions.fail();
        } catch (SystemException e) {
            final var message = e.getMessage();
            Assertions.assertEquals(String.format("Resource \"%s\" is a directory",RESOURCE_DIR_NAME), message);
        } catch (RuntimeException e) {
            Assertions.assertEquals("Is a directory", e.getMessage());
        }
    }

    private static FileInfo readLines(Path path) {
        final var pathName = path.toString();
        final var indexOfResource = pathName.indexOf(RESOURCE_DIR_NAME) + RESOURCE_DIR_NAME.length() + 1;
        var fileName = "";
        if (indexOfResource >= pathName.length()){
            fileName = path.getFileName().toString();
        } else {
            fileName = pathName.substring(indexOfResource);
        }
        try {
            return new FileInfo(fileName, Files.readAllLines(path));
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static FileInfo readLines(URL url) {
        try {
            return readLines(Paths.get(url.toURI()));
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private record FileInfo(String name, List<String> lines) {}
}
