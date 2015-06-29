package com.topclouders.releaseplugin.template;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * 
 * @author Gabor Kokeny
 * @version 1.1.0
 *
 */
public class DefaultTemplateContext implements ITemplateContext
{
	private final Locale locale;

	private final Map<String, Object> map = new HashMap<>();

	public DefaultTemplateContext(Locale locale)
	{
		this.locale = locale;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Locale getLocale()
	{
		return this.locale;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<String, ?> asMap()
	{
		return this.map;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(String key, Object value)
	{
		this.map.put(key, value);
	}

}
