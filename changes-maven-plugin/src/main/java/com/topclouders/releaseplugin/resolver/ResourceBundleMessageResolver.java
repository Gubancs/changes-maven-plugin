/*
 * Copyright 20015 The Topclouders Hungary Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.topclouders.releaseplugin.resolver;

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
