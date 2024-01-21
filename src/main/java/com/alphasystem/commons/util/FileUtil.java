/**
 *
 */
package com.alphasystem.commons.util;

import com.alphasystem.commons.SystemException;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @author sali
 */
public class FileUtil {

    /**
     * Copy from given output stream from input stream.
     *
     * @param out Source stream
     * @param in  Destination stream
     * @throws IOException if anything goes wrong
     */
    public static void copyOutputStream(OutputStream out, InputStream in)
            throws IOException {
        // Create a buffer for reading the files
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
    }

    /**
     * Download file from gievn URL.
     *
     * @param url  Given URL
     * @param dest Destination file
     * @throws SystemException Wraps other exceptions
     */
    public static void download(String url, File dest) throws SystemException {
        try {
            download(new URL(url), dest);
        } catch (MalformedURLException e) {
            throw new SystemException(e.getMessage(), e);
        }
    }

    /**
     * Download file from gievn URL.
     *
     * @param url  Given URL
     * @param dest Destination file
     * @throws SystemException Wraps other exceptions
     */
    public static void download(String url, String dest) throws SystemException {
        download(url, new File(dest));
    }

    /**
     * Download file from gievn URL.
     *
     * @param url  Given URL
     * @param dest Destination file
     * @throws SystemException Wraps other exceptions
     */
    public static void download(URL url, File dest) throws SystemException {
        BufferedReader in = null;
        PrintWriter writer = null;
        try {
            URLConnection urlConnection = url.openConnection();
            writer = new PrintWriter(new BufferedWriter(new FileWriter(dest)));
            in = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));
            String line = in.readLine();
            while (line != null) {
                writer.println(line);
                line = in.readLine();
            }
        } catch (IOException e) {
            throw new SystemException(e.getMessage(), e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    //
                }
            }
            if (writer != null) {
                writer.close();
            }
        }
    }
}
