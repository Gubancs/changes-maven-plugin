package com.topclouders.releaseplugin.template;

/**
 * 
 * 
 * @author Gabor Kokeny
 * @version 1.1.0
 *
 */
public interface TemplateGenerator
{

	static String SITE = "templates/site.html";

	static String DEVELOPER = "templates/developer.html";

	/**
	 * Generate template
	 * 
	 * @param template
	 * @param templateContext
	 * @return
	 */
	public String generate(String template, ITemplateContext templateContext) throws TemplateException;

	/**
	 * Generate site template
	 * 
	 * @param templateContext
	 * @return
	 */
	public String generateSiteTemplate(ITemplateContext templateContext) throws TemplateException;

}
