package com.topclouders.releaseplugin.template;

/**
 * 
 * @author Gabor Kokeny
 * @version 1.1.0
 *
 */
public class TemplateException extends Exception
{

	private static final long serialVersionUID = -3352793105612077533L;

	public TemplateException(String message)
	{
		super(message);
	}

	public TemplateException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
