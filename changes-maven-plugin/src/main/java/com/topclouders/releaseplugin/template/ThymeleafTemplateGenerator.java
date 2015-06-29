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

package com.topclouders.releaseplugin.template;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.resourceresolver.ClassLoaderResourceResolver;
import org.thymeleaf.templateresolver.ITemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

import com.topclouders.releaseplugin.resolver.ResourceBundleMessageResolver;

/**
 * @author Gabor Kokeny
 * @version 1.0.0
 *
 */
public class ThymeleafTemplateGenerator implements TemplateGenerator
{
	private final Logger log = LoggerFactory.getLogger(ThymeleafTemplateGenerator.class);

	private final ITemplateResolver templateResolver;

	private final TemplateEngine templateEngine;

	/**
	 * Create a new instance of template generator wiht default template
	 * resolver.
	 */
	public ThymeleafTemplateGenerator()
	{
		this.templateResolver = this.initializeTemplateResolver();
		this.templateEngine = this.initializeTemplateEngine();
	}

	/**
	 * Allows override for subclasses
	 * 
	 * @return
	 */
	protected ITemplateResolver initializeTemplateResolver()
	{
		TemplateResolver templateResolver = new TemplateResolver();
		templateResolver.setResourceResolver(new ClassLoaderResourceResolver());
		return templateResolver;
	}

	/**
	 * Allows override for subclasses
	 * 
	 * @return
	 */
	protected TemplateEngine initializeTemplateEngine()
	{
		TemplateEngine templateEngine = new TemplateEngine();
		templateEngine.setTemplateResolver(this.templateResolver);
		templateEngine.setMessageResolver(new ResourceBundleMessageResolver());

		return templateEngine;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String generateSiteTemplate(ITemplateContext templateContext) throws TemplateException
	{
		return this.generate(SITE, templateContext);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String generate(String templateName, ITemplateContext templateContext) throws TemplateException
	{
		try
		{
			log.debug("Process template {} with locale {}", templateName, templateContext.getLocale());

			logTemplateContext(templateContext);

			final Context ctx = new Context(templateContext.getLocale(), templateContext.asMap());

			String template = this.templateEngine.process(templateName, ctx);

			log.trace("Processed template: {}", template);

			log.debug("Template processing finished successfully");

			return template;
		} catch (Exception e)
		{
			throw new TemplateException(String.format("Failed to process template '%s'", templateName), e);
		}
	}

	/**
	 * 
	 * @param templateContext
	 */
	private void logTemplateContext(ITemplateContext templateContext)
	{
		if (log.isDebugEnabled())
		{
			log.debug("Template context:");

			Map<String, ?> variables = templateContext.asMap();
			for (String key : variables.keySet())
			{
				log.debug("{} - {}", key, variables.get(key));
			}
		}
	}

}
