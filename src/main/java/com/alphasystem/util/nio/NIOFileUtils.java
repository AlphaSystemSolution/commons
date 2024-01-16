package com.alphasystem.util.nio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.EnumSet;
import java.util.Objects;

import static java.lang.Integer.MAX_VALUE;
import static java.lang.String.format;
import static java.nio.channels.Channels.newChannel;
import static java.nio.file.FileSystems.newFileSystem;
import static java.nio.file.FileVisitOption.FOLLOW_LINKS;
import static java.nio.file.FileVisitResult.CONTINUE;
import static java.nio.file.Files.*;
import static java.nio.file.Paths.get;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * @author sali
 */
public final class NIOFileUtils {

    private static final ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();

    /**
     * Do not let anyone instantiate this class
     */
    private NIOFileUtils() {
    }

    public static void copyDir(Path to, String resourceName, Class<?> resourceClass) throws IOException, URISyntaxException {
        if (to == null) {
            return;
        }
        validateTo(to);
        Path path = get(resourceClass.getProtectionDomain().getCodeSource().getLocation().toURI());
        if (isDirectory(path)) { // we are running from IDE
            final Path from = get(path.toFile().getPath(), resourceName);
            copyDir(from, to);
        } else { // a jar file
            try (FileSystem zipFileSystem = newFileSystem(path, contextClassLoader)) {
                final Path from = zipFileSystem.getPath(format("/%s/", resourceName));
                walkFileTree(from, EnumSet.of(FOLLOW_LINKS), MAX_VALUE, new ZipDirVisitor(from, to));
            }
        }
    }

    public static void copyDir(Path from, Path to) throws IOException {
        validateDir(from);
        validateTo(to);
        walkFileTree(from, EnumSet.of(FOLLOW_LINKS), MAX_VALUE, new CopyDirVisitor(from, to));
    }

    public static void fastCopy(final ReadableByteChannel src, final WritableByteChannel dest) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);

        while (src.read(buffer) != -1) {
            buffer.flip();
            dest.write(buffer);
            buffer.compact();
        }

        buffer.flip();

        while (buffer.hasRemaining()) {
            dest.write(buffer);
        }
    }

    public static void fastCopy(final InputStream src, final OutputStream dest) throws IOException {
        if (src == null || dest == null) {
            return;
        }
        try (ReadableByteChannel inputChannel = newChannel(src);
             WritableByteChannel outputChannel = newChannel(dest)) {
            fastCopy(inputChannel, outputChannel);
        }
    }

    private static void validateDir(Path from) throws IOException {
        if (exists(from)) {
            if (!isDirectory(from)) {
                throw new IOException(format("Destination path{%s} is not a directory.", from));
            }
        }
    }

    private static void validateTo(Path to) throws IOException {
        validateDir(to);
        createDirectories(to);
    }

    /**
     * A visitor that copies directory recursively from a given path.
     */
    private static class CopyDirVisitor extends SimpleFileVisitor<Path> {

        private final Path fromPath;
        private final Path toPath;
        private final StandardCopyOption copyOption;

        public CopyDirVisitor(Path fromPath, Path toPath, StandardCopyOption copyOption) {
            this.fromPath = fromPath;
            this.toPath = toPath;
            this.copyOption = copyOption;
        }

        public CopyDirVisitor(Path fromPath, Path toPath) {
            this(fromPath, toPath, REPLACE_EXISTING);
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            final Path relativePath = fromPath.relativize(dir);
            Path targetPath = toPath.resolve(relativePath);
            if (!exists(targetPath)) {
                createDirectory(targetPath);
            }
            return CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            copy(file, toPath.resolve(fromPath.relativize(file)), copyOption);
            return CONTINUE;
        }
    }

    private static class ZipDirVisitor extends SimpleFileVisitor<Path> {

        private final Path fromPath;
        private final StandardCopyOption copyOption;
        private Path currentDir;

        public ZipDirVisitor(Path fromPath, Path toPath, StandardCopyOption copyOption) {
            this.fromPath = fromPath;
            this.copyOption = copyOption;
            this.currentDir = toPath;
        }

        public ZipDirVisitor(Path fromPath, Path toPath) {
            this(fromPath, toPath, REPLACE_EXISTING);
        }

        @Override
        public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
            final Path relativePath = fromPath.relativize(dir);
            if (!relativePath.toString().isEmpty()) {
                currentDir = get(currentDir.toString(), dir.getFileName().toString());
                if (!exists(currentDir)) {
                    createDirectory(currentDir);
                }
            }
            return CONTINUE;
        }

        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            // remove leading "/" from the fileName
            final String name = file.toString().substring(1);
            try (InputStream inputStream = contextClassLoader.getResourceAsStream(name)) {
                final Path target = get(currentDir.toString(), file.getFileName().toString());
                if (Objects.nonNull(inputStream)) copy(inputStream, target, copyOption);
            }
            return CONTINUE;
        }
    }
}
