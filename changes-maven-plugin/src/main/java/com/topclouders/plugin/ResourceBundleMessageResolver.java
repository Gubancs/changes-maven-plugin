package com.topclouders.plugin;

import java.util.Enumeration;
import java.util.Properties;
import java.util.ResourceBundle;

import org.thymeleaf.messageresolver.StandardMessageResolver;

/**
 * Thymeleaf resource bundle message resolver
 * 
 * @author Gabor Kokeny
 * @version 1.0.0
 *
 */
public class ResourceBundleMessageResolver extends StandardMessageResolver {

	private static final String DEFAULT_RESOURCE_BUNDLE_MESSAGE_SOURCE = "messages/messages";

	private ResourceBundle defaultMessages;

	public ResourceBundleMessageResolver() {
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void initializeSpecific() {
		super.initializeSpecific();

		this.defaultMessages = ResourceBundle.getBundle(DEFAULT_RESOURCE_BUNDLE_MESSAGE_SOURCE);
		this.setDefaultMessages(ResourceBundleMessageResolver.convertResourceBundleToProperties(defaultMessages));
	}

	/**
	 * Convert ResourceBundle into a Properties object.
	 *
	 * @param resource
	 *            a resource bundle to convert.
	 * @return Properties a properties version of the resource bundle.
	 */
	private static Properties convertResourceBundleToProperties(ResourceBundle resource) {
		Properties properties = new Properties();

		Enumeration<String> keys = resource.getKeys();
		while (keys.hasMoreElements()) {
			String key = keys.nextElement();
			properties.put(key, resource.getString(key));
		}

		return properties;
	}
}
