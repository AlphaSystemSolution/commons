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

    private static final String META_INF_DIR_NAME = "META-INF";
    private static final String FILES_DIR_NAME = "files";

    //private static final String RESOURCE_DIR_NAME = "META-INF/files";
    private static final String RESOURCE_NAME = "file1.txt";

    @Test
    public void testProcessTopLevelDirectory() {

        try {
            final var results = AppUtil.processResourceDirectory(FILES_DIR_NAME, path -> readLines(path, FILES_DIR_NAME));
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
    public void testProcessSubDirectory() {
        try {
            final var dirName = String.format("%s/%s", META_INF_DIR_NAME, FILES_DIR_NAME);
            final var results = AppUtil.processResourceDirectory(dirName, path -> readLines(path, dirName));

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
            final var results = AppUtil.processResourceDirectory("does-not-exists", path -> readLines(path, "does-not-exists"));
            Assertions.assertTrue(results.isEmpty());
        } catch (SystemException e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testProcessFileAsDirectoryFromTopLevel() {
        try {
            final var resourceName = String.format("%s.%s", FILES_DIR_NAME, RESOURCE_NAME);
            final var results = AppUtil.processResourceDirectory(resourceName, path -> readLines(path, FILES_DIR_NAME));
            Assertions.assertTrue(results.isEmpty());
        } catch (SystemException e) {
            final var message = e.getMessage();
            Assertions.assertEquals(String.format("Path \"%s\" is not a directory", RESOURCE_NAME), message);
        }
    }

    @Test
    public void testProcessFileAsDirectoryFromSubDirectory() {
        try {
            final var parentName = String.format("%s/%s", META_INF_DIR_NAME, FILES_DIR_NAME);
            final var resourceName = String.format("%s/%s", parentName, RESOURCE_NAME);
            final var results = AppUtil.processResourceDirectory(resourceName, path -> readLines(path, parentName));
            Assertions.assertTrue(results.isEmpty());
        } catch (SystemException e) {
            final var message = e.getMessage();
            Assertions.assertEquals(String.format("Path \"%s\" is not a directory", RESOURCE_NAME), message);
        }
    }

    @Test
    public void testProcessResourceFromTopLevel() {
        try {
            final var resourceName = String.format("%s/%s", FILES_DIR_NAME, RESOURCE_NAME);
            final var results = AppUtil.processResource(resourceName, url -> readLines(url, FILES_DIR_NAME));
            Assertions.assertEquals(1, results.size());
            final var fileInfo = results.get(0);
            final var expected = new FileInfo(RESOURCE_NAME, Arrays.asList("File1 Line1", "File1 Line2"));
            Assertions.assertEquals(expected, fileInfo);
        } catch (SystemException e) {
            Assertions.fail(e);
        }
    }

    @Test
    public void testProcessResourceFromSubDirectory() {
        try {
            final var parentName = String.format("%s/%s", META_INF_DIR_NAME, FILES_DIR_NAME);
            final var resourceName = String.format("%s/%s", parentName, RESOURCE_NAME);
            final var results = AppUtil.processResource(resourceName, url -> readLines(url, parentName));
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
            AppUtil.processResource(FILES_DIR_NAME, url -> readLines(url, FILES_DIR_NAME));
            Assertions.fail();
        } catch (SystemException e) {
            final var message = e.getMessage();
            Assertions.assertEquals(String.format("Resource \"%s\" is a directory", FILES_DIR_NAME), message);
        } catch (RuntimeException e) {
            Assertions.assertEquals("Is a directory", e.getMessage());
        }
    }

    @Test
    public void testProcessSubDirectoryAsFile() {
        final var resourceName = String.format("%s/%s", META_INF_DIR_NAME, FILES_DIR_NAME);
        try {
            final var fileInfos = AppUtil.processResource(resourceName, url -> readLines(url, resourceName));
            Assertions.assertTrue(fileInfos.isEmpty());
        } catch (SystemException e) {
            final var message = e.getMessage();
            Assertions.assertEquals(String.format("Resource \"%s\" is a directory", resourceName), message);
        } catch (RuntimeException e) {
            Assertions.assertEquals("Is a directory", e.getMessage());
        }
    }

    private static FileInfo readLines(Path path, String parentName) {
        final var pathName = path.toString();
        final var indexOfResource = pathName.indexOf(parentName) + parentName.length() + 1;
        var fileName = "";
        if (indexOfResource >= pathName.length()) {
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

    private static FileInfo readLines(URL url, String parentName) {
        try {
            return readLines(Paths.get(url.toURI()), parentName);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private record FileInfo(String name, List<String> lines) {
    }
}
