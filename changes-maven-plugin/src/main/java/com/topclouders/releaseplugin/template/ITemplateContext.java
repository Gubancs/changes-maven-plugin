package com.topclouders.releaseplugin.template;

import java.util.Locale;
import java.util.Map;

/**
 * 
 * @author Gabor Kokeny
 * @version 1.1.0
 *
 */
public interface ITemplateContext
{

	/**
	 * @return
	 */
	Locale getLocale();

	/**
	 * 
	 * @return
	 */
	Map<String, ?> asMap();

	/**
	 * 
	 * @param key
	 * @param value
	 */
	void add(String key, Object value);

}
