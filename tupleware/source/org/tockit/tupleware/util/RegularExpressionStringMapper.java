package org.tockit.tupleware.util;

import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/**
 * Implements a string mapper by using regular expression based replacement.
 *
 * At construction time an object of this class gets a {@link Properties}
 * instance that contains regular expressions as keys and matching replacement
 * strings as values. All keys are then iterated through and if the key interpreted
 * as regular expression matches the input, then all matches are replaced by the
 * value given in the map. 
 * 
 * This implies that if no pattern matches, the original input is returned.
 * 
 * Behaviour of this class in the case that multiple keys match the input is
 * undefined unless the order is insignificant. 
 * TODO: it would be better to do things in a defined sequence
 */
public class RegularExpressionStringMapper implements StringMapper {
	private final Properties regExpMap;

	public RegularExpressionStringMapper(Properties regExpMap) {
		this.regExpMap = regExpMap;
	}

	/**
	 * Maps the original string onto its replacement.
	 * 
	 * @see RegularExpressionStringMapper
	 */
	public String mapString(String originalString) {
		String result = originalString;
		Iterator<Map.Entry<Object,Object>> mapEntries = regExpMap.entrySet().iterator();
		while (mapEntries.hasNext()) {
			Map.Entry<Object,Object> entry = mapEntries.next();
			result = result.replaceAll((String)entry.getKey(), (String)entry.getValue());
		}
		return result;
	}

}
