package org.tockit.tupleware.util;

/**
 * Interface for mapping strings onto other strings.
 * 
 * This is similar to a Map<String,String>, only that the implementations
 * can create the new strings dynamically.
 */
public interface StringMapper {
	/**
	 * Map an original string onto its image.
	 * 
	 * Implementations are supposed to handle null values gracefully, e.g. by returning
	 * null or a different value for the null value.
	 * 
	 * @param originalString The original string that should be mapped. Can be null.
	 * @return The mapped string, i.e. the image of the parameter. Can be null.
	 */
	String mapString(String originalString);
}
