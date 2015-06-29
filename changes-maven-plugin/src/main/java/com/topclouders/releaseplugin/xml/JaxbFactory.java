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
package com.topclouders.releaseplugin.xml;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

/**
 * Thread safe Jaxb unmarshaller factory class
 * 
 * @author Gabor Kokeny
 * @version 1.0.0
 * 
 */
public final class JaxbFactory {

	private static final Map<String, JaxbFactory> singletonMap = new HashMap<>();

	private final ThreadLocal<Unmarshaller> unmarshaller = new ThreadLocal<>();

	private final String contextPath;

	private JaxbFactory(String contextPath) {
		this.contextPath = contextPath;
	}

	/**
	 * Obtain a new instance of a <tt>JAXBContext</tt> class.
	 * 
	 * @param contextPath
	 *            list of java package names that contain schema derived classes
	 * @return Return an instance of a JaxbFactory
	 */
	public static synchronized JaxbFactory newInstance(String contextPath) {
		JaxbFactory jaxb = singletonMap.get(contextPath);
		if (jaxb == null) {
			jaxb = new JaxbFactory(contextPath);
			singletonMap.put(contextPath, jaxb);
		}
		return jaxb;
	}

	/**
	 * Gets/Creates an unmarshaller (thread-safe)
	 * 
	 * @throws JAXBException
	 */
	public Unmarshaller unmarshaller() throws JAXBException {
		Unmarshaller um = unmarshaller.get();
		if (um == null) {
			JAXBContext jc = JAXBContext.newInstance(this.contextPath);
			um = jc.createUnmarshaller();
			unmarshaller.set(um);
		}
		return um;
	}
}
