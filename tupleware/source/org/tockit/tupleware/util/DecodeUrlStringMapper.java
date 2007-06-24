package org.tockit.tupleware.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

/**
 * Decodes the input strings as URLs.
 * 
 * This class implements a dynamic mapping from encoded URLs to their
 * decoded version, based on an UTF-8 encoding scheme.
 */
public class DecodeUrlStringMapper implements StringMapper {

	/**
	 * Decodes the input string.
	 * 
	 * @return The input string decoded using the URL decoding rules.
	 */
	public String mapString(String originalString) {
		try {
			return URLDecoder.decode(originalString, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException("Missing encoding 'UTF-8' in Java installation!");
		}
	}

}
