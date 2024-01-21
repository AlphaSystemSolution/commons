package com.alphasystem.commons.util;

import java.io.File;

/**
 *
 * @param file  file
 * @param name name of the entry
 */
public record ZipFileEntry(File file, String name) {}