/**
 * 
 */
package com.alphasystem.util;

import static com.alphasystem.util.Utils.USER_HOME_DIR;
import static java.lang.String.format;
import static java.lang.System.setProperty;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import com.alphasystem.ApplicationException;
import com.alphasystem.SystemException;

/**
 * @author sali
 * 
 */
public class CertificateInstaller {

	private static class SavingTrustManager implements X509TrustManager {

		private final X509TrustManager tm;
		private X509Certificate[] chain;

		SavingTrustManager(X509TrustManager tm) {
			this.tm = tm;
		}

		public void checkClientTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
			throw new UnsupportedOperationException();
		}

		public void checkServerTrusted(X509Certificate[] chain, String authType)
				throws CertificateException {
			this.chain = chain;
			tm.checkServerTrusted(chain, authType);
		}

		public X509Certificate[] getAcceptedIssuers() {
			throw new UnsupportedOperationException();
		}
	}

	public static final String DEFAULT_KEY_STORE_TYPE = "JKS";

	private static final String DEFAULT_JKS_FILE_NAME = "default.jks";

	public static final File DEFAULT_JKS_FILE = new File(USER_HOME_DIR,
			DEFAULT_JKS_FILE_NAME);

	private static final int DEFAULT_SSL_PORT = 443;

	/**
	 * @param ks
	 * @return
	 * @throws ApplicationException
	 */
	private static SavingTrustManager createTrustManager(KeyStore ks)
			throws ApplicationException {
		SavingTrustManager tm = null;
		TrustManagerFactory tmf;
		try {
			tmf = TrustManagerFactory.getInstance(TrustManagerFactory
					.getDefaultAlgorithm());
			tmf.init(ks);
			X509TrustManager defaultTrustManager = (X509TrustManager) tmf
					.getTrustManagers()[0];
			tm = new SavingTrustManager(defaultTrustManager);
		} catch (Exception e) {
			throw new SystemException(e.getMessage(), e);
		}
		return tm;
	}

	/**
	 * @param ks
	 * @param tm
	 * @param host
	 * @param port
	 * @return
	 * @throws ApplicationException
	 */
	private static SSLSocket getSslContext(KeyStore ks, TrustManager tm,
			String host, int port) throws ApplicationException {
		SSLSocket socket = null;
		try {
			SSLContext context = SSLContext.getInstance("TLS");
			context.init(null, new TrustManager[] { tm }, null);
			SSLSocketFactory factory = context.getSocketFactory();
			System.out.println("Opening connection to " + host + ":" + port
					+ "...");
			socket = (SSLSocket) factory.createSocket(host, port);
			socket.setSoTimeout(10000);
		} catch (Exception e) {
			throw new SystemException(e.getMessage(), e);
		}
		return socket;
	}

	public static void initialize(char[] passphrase)
			throws IllegalArgumentException, ApplicationException {
		initialize(DEFAULT_JKS_FILE, passphrase);
	}

	public static void initialize(File certificateFile, char[] passphrase)
			throws IllegalArgumentException, ApplicationException {
		initialize(certificateFile, DEFAULT_KEY_STORE_TYPE, passphrase);
	}

	public static void initialize(File certificateFile, String keyStoreType,
			char[] passphrase) throws IllegalArgumentException,
			ApplicationException {
		if (!certificateFile.exists()) {
			KeyStore ks = loadKeyStore(null, passphrase);
			saveKeyStore(certificateFile, ks, passphrase);
		}
		setProperty("javax.net.ssl.trustStore",
				certificateFile.getAbsolutePath());
		setProperty("javax.net.ssl.trustStoreType", keyStoreType);
		setProperty("javax.net.ssl.trustStorePassword", new String(passphrase));
	}

	public static void install(File certificateFile, String host, int port,
			char[] passphrase) throws ApplicationException {
		if (port == -1) {
			port = DEFAULT_SSL_PORT;
		}
		KeyStore ks = loadKeyStore(certificateFile, passphrase);
		SavingTrustManager tm = createTrustManager(ks);

		if (!isTrusted(ks, tm, host, port)) {
			X509Certificate[] chain = tm.chain;
			if (chain == null || chain.length < 0) {
				System.out.println("Could not obtain server certificate chain");
				return;
			}

			for (int i = 0; i < chain.length; i++) {
				X509Certificate cert = chain[i];
				String alias = host + "-" + (i + 1);
				try {
					ks.setCertificateEntry(alias, cert);
				} catch (KeyStoreException e) {
					throw new SystemException(e.getMessage(), e);
				}
			}
			saveKeyStore(certificateFile, ks, passphrase);
		}
	}

	public static void install(String host, char[] passphrase)
			throws ApplicationException {
		install(DEFAULT_JKS_FILE, host, DEFAULT_SSL_PORT, passphrase);
	}

	public static boolean isTrusted(KeyStore ks, String host, int port)
			throws ApplicationException {
		return isTrusted(ks, null, host, port);
	}

	/**
	 * @param ks
	 * @param tm
	 * @param host
	 * @param port
	 * @return
	 * @throws ApplicationException
	 */
	public static boolean isTrusted(KeyStore ks, TrustManager tm, String host,
			int port) throws ApplicationException {
		if (port == -1) {
			port = DEFAULT_SSL_PORT;
		}
		boolean result = false;
		if (tm == null) {
			tm = createTrustManager(ks);
		}
		SSLSocket socket = getSslContext(ks, tm, host, port);
		try {
			System.out.println("Starting SSL handshake...");
			socket.startHandshake();
			System.out.println();
			System.out.println("No errors, certificate is already trusted");
			result = true;
		} catch (SSLException e) {
			System.out.println();
			e.printStackTrace(System.out);
		} catch (IOException e) {
			System.out.println();
			e.printStackTrace(System.out);
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
			}
		}
		return result;
	}

	/**
	 * @param certificateFile
	 * @param passphrase
	 * @return
	 * @throws IllegalArgumentException
	 * @throws ApplicationException
	 */
	public static KeyStore loadKeyStore(File certificateFile, char[] passphrase)
			throws IllegalArgumentException, ApplicationException {
		InputStream in = null;
		KeyStore ks = null;
		if (certificateFile != null) {
			if (!certificateFile.isFile()) {
				throw new IllegalArgumentException(format(
						"File \"%s\" is not a file.",
						certificateFile.getAbsolutePath()));
			}
			if (certificateFile.exists()) {
				try {
					in = new FileInputStream(certificateFile);
				} catch (FileNotFoundException e) {
					return null;
				}
			}
		}
		try {
			ks = KeyStore.getInstance(DEFAULT_KEY_STORE_TYPE);
			ks.load(in, passphrase);
		} catch (Exception e) {
			throw new SystemException(e.getMessage(), e);
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}
		return ks;
	}

	/**
	 * @param certificateFile
	 * @param ks
	 * @param passphrase
	 * @throws ApplicationException
	 * 
	 */
	public static void saveKeyStore(File certificateFile, KeyStore ks,
			char[] passphrase) throws ApplicationException {
		BufferedOutputStream out = null;
		try {
			out = new BufferedOutputStream(
					new FileOutputStream(certificateFile));
			ks.store(out, passphrase);
		} catch (Exception e) {
			throw new SystemException(e.getMessage(), e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

}
