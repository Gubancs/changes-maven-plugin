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

package com.topclouders.plugin;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.IContext;
import org.thymeleaf.resourceresolver.ClassLoaderResourceResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

/**
 * @author Gabor Kokeny
 * @version 1.0.0
 *
 */
public class TemplateGenerator {

	private static String SITE = "templates/site.html";

	private final TemplateResolver templateResolver;

	private final TemplateEngine templateEngine;

	/**
	 * Create a new instance of template generator wiht default template
	 * resolver.
	 */
	public TemplateGenerator() {
		this.templateResolver = new TemplateResolver();
		this.templateResolver.setResourceResolver(new ClassLoaderResourceResolver());

		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(this.templateResolver);
		this.templateEngine.setMessageResolver(new ResourceBundleMessageResolver());
	}

	public String generateSite(IContext templateContext) {
		return this.templateEngine.process(TemplateGenerator.SITE, templateContext);
	}

	public void setCharacterEncoding(String characterEncoding) {
		this.templateResolver.setCharacterEncoding(characterEncoding);
	}
}
