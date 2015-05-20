package com.alphasystem.util;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author sali
 * 
 */
public class IdGenerator {

	private static IdGenerator instance;

	/** Format an id created by this class as a hex string. */
	public static String format(int id) {
		final char[] r = new char[8];
		for (int p = 7; 0 <= p; p--) {
			final int h = id & 0xf;
			r[p] = h < 10 ? (char) ('0' + h) : (char) ('a' + (h - 10));
			id >>= 4;
		}
		return new String(r);
	}

	public synchronized static IdGenerator getInstance() {
		if (instance == null) {
			instance = new IdGenerator();
		}
		return instance;
	}

	private static short hi16(final int in) {
		return (short) ( //
		((in >>> 24 & 0xff)) | //
		((in >>> 16 & 0xff) << 8) //
		);
	}

	private static short lo16(final int in) {
		return (short) ( //
		((in >>> 8 & 0xff)) | //
		((in & 0xff) << 8) //
		);
	}

	/** A very simple bit permutation to mask a simple incrementer. */
	static int mix(final int in) {
		short v0 = hi16(in);
		short v1 = lo16(in);
		v0 += ((v1 << 2) + 0 ^ v1) + (salt ^ (v1 >>> 3)) + 1;
		v1 += ((v0 << 2) + 2 ^ v0) + (salt ^ (v0 >>> 3)) + 3;
		return result(v0, v1);
	}

	public static String nextId() {
		IdGenerator generator = IdGenerator.getInstance();
		return IdGenerator.format(generator.next()).toUpperCase();
	}

	private static int result(final short v0, final short v1) {
		return ((v0 & 0xff) << 24) | //
				(((v0 >>> 8) & 0xff) << 16) | //
				((v1 & 0xff) << 8) | //
				((v1 >>> 8) & 0xff);
	}

	/* For testing only. */
	static int unmix(final int in) {
		short v0 = hi16(in);
		short v1 = lo16(in);
		v1 -= ((v0 << 2) + 2 ^ v0) + (salt ^ (v0 >>> 3)) + 3;
		v0 -= ((v1 << 2) + 0 ^ v1) + (salt ^ (v1 >>> 3)) + 1;
		return result(v0, v1);
	}

	private final AtomicInteger gen;

	private static final int salt = 0x9e3779b9;

	IdGenerator() {
		gen = new AtomicInteger(new Random().nextInt());
	}

	/** Produce the next identifier. */
	public int next() {
		return mix(gen.getAndIncrement());
	}
}
