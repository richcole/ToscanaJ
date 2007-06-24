package org.tockit.tupleware.util;

/**
 * Implements the identity function for the StringMapper interface.
 */
public class IdentityStringMapper implements StringMapper {

	/**
	 * Always returns the input value.
	 */
	public String mapString(String originalString) {
		return originalString;
	}

}
