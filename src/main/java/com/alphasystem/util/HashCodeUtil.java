/**
 * 
 */
package com.alphasystem.util;

import java.lang.reflect.Array;

/**
 * @author Syed F Ali
 */
public final class HashCodeUtil {

	// / PRIVATE ///
	private static final int fODD_PRIME_NUMBER = 37;

	/**
	 * An initial value for a <code>hashCode</code>, to which is added
	 * contributions from fields. Using a non-zero value decreases collisons of
	 * <code>hashCode</code> values.
	 */
	public static final int SEED = 23;

	private static int firstTerm(int aSeed) {
		return fODD_PRIME_NUMBER * aSeed;
	}

	/**
	 * booleans.
	 */
	public static int hash(boolean aBoolean) {
		return hash(SEED, aBoolean);
	}

	/**
	 * chars.
	 */
	public static int hash(char aChar) {
		return hash(SEED, aChar);
	}

	/**
	 * doubles.
	 */
	public static int hash(double aDouble) {
		return hash(SEED, aDouble);
	}

	/**
	 * floats.
	 */
	public static int hash(float aFloat) {
		return hash(SEED, aFloat);
	}

	/**
	 * ints.
	 */
	public static int hash(int aInt) {
		return hash(SEED, aInt);
	}

	/**
	 * booleans.
	 */
	public static int hash(int aSeed, boolean aBoolean) {
		return firstTerm(aSeed) + (aBoolean ? 1 : 0);
	}

	/**
	 * chars.
	 */
	public static int hash(int aSeed, char aChar) {
		return firstTerm(aSeed) + (int) aChar;
	}

	/**
	 * doubles.
	 */
	public static int hash(int aSeed, double aDouble) {
		return hash(aSeed, Double.doubleToLongBits(aDouble));
	}

	/**
	 * floats.
	 */
	public static int hash(int aSeed, float aFloat) {
		return hash(aSeed, Float.floatToIntBits(aFloat));
	}

	/**
	 * ints.
	 */
	public static int hash(int aSeed, int aInt) {
		/*
		 * Implementation Note Note that byte and short are handled by this
		 * method, through implicit conversion.
		 */
		return firstTerm(aSeed) + aInt;
	}

	/**
	 * longs.
	 */
	public static int hash(int aSeed, long aLong) {
		return firstTerm(aSeed) + (int) (aLong ^ (aLong >>> 32));
	}

	/**
	 * <code>aObject</code> is a possibly-null object field, and possibly an
	 * array. If <code>aObject</code> is an array, then each element may be a
	 * primitive or a possibly-null object.
	 */
	public static int hash(int aSeed, Object aObject) {
		int result = aSeed;
		if (aObject == null) {
			result = hash(result, 0);
		} else if (!isArray(aObject)) {
			result = hash(result, aObject.hashCode());
		} else {
			int length = Array.getLength(aObject);
			for (int idx = 0; idx < length; ++idx) {
				Object item = Array.get(aObject, idx);
				// recursive call!
				result = hash(result, item);
			}
		}
		return result;
	}

	/**
	 * longs.
	 */
	public static int hash(long aLong) {
		return hash(SEED, aLong);
	}

	/**
	 * @param objects
	 * @return
	 */
	public static int hashAll(Object... objects) {
		return hash(objects);
	}

	public static int hash(Object aObject) {
		return hash(SEED, aObject);
	}

	private static boolean isArray(Object aObject) {
		return aObject.getClass().isArray();
	}
}
