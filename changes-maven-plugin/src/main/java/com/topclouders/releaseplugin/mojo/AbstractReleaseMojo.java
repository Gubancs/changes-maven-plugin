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

package com.topclouders.releaseplugin.mojo;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

/**
 * 
 * 
 * @author Gabor Kokeny
 * @version 1.1.0
 *
 */
abstract class AbstractReleaseMojo extends AbstractMojo
{

	/**
	 * The Maven Session.
	 *
	 */
	@Component
	protected MavenSession mavenSession;

	/**
	 * Reference to the maven project
	 */
	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	protected MavenProject mavenProject;

	/**
	 * 
	 * Instantinate a new release MOJO
	 */
	public AbstractReleaseMojo()
	{

	}

	/**
	 * 
	 * @return
	 */
	protected boolean isExecutionRoot()
	{
		getLog().debug("Root Folder:" + mavenSession.getExecutionRootDirectory());
		getLog().debug("Current Folder:" + this.mavenProject.getBasedir().getAbsolutePath());

		boolean result = mavenSession.getExecutionRootDirectory()
				.equalsIgnoreCase(this.mavenProject.getBasedir().getAbsolutePath());

		if (result)
		{
			getLog().debug("This is the execution root.");
		} else
		{
			getLog().debug("This is NOT the execution root.");
		}

		return result;
	}
}
