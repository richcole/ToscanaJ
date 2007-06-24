package org.tockit.tupleware.util;

/**
 * Executes two string mappings in a row.
 */
public class JoinedStringMapper implements StringMapper {

	private final StringMapper first;
	private final StringMapper second;

	public JoinedStringMapper(StringMapper first, StringMapper second) {
		this.first = first;
		this.second = second;
	}
	
	/**
	 * Returns the result of the first mapping mapped by the second.
	 */
	public String mapString(String originalString) {
		return second.mapString(first.mapString(originalString));
	}

}
