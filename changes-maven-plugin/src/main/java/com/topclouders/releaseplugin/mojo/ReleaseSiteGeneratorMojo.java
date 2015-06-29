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

import java.io.File;
import java.io.FileInputStream;
import java.util.Locale;

import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.maven.changes._1_0.ChangesDocument;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import com.topclouders.releaseplugin.template.DefaultTemplateContext;
import com.topclouders.releaseplugin.template.ITemplateContext;
import com.topclouders.releaseplugin.template.TemplateException;
import com.topclouders.releaseplugin.template.TemplateGenerator;
import com.topclouders.releaseplugin.template.ThymeleafTemplateGenerator;
import com.topclouders.releaseplugin.xml.JaxbFactory;

/**
 * A maven Mojo that able to generate channges HTML site from changes.xml.
 *
 * @author <b>Gabor Kokeny</b> Developer at GE Capital <br/>
 *         Mail to :
 *         <a href="mailto:g4b0r.k0k3ny@gmail.com">g4b0r.k0k3ny.gmail.com</a>
 * @version 1.0.0
 */
@Execute(goal = ReleaseSiteGeneratorMojo.GOAL_CHANGES_REPORT, phase = LifecyclePhase.PREPARE_PACKAGE)
@Mojo(name = ReleaseSiteGeneratorMojo.GOAL_CHANGES_REPORT, defaultPhase = LifecyclePhase.PREPARE_PACKAGE, threadSafe = true)
public class ReleaseSiteGeneratorMojo extends AbstractReleaseMojo
{

	static final String JAXB_CONTEXT_PATH = "org.apache.maven.changes._1_0";

	static final String GOAL_CHANGES_REPORT = "changes-report";

	static final String GOAL_HELP = "help";

	/**
	 * This will cause the execution to be run only at the top of a given module
	 * tree. That is, run in the project contained in the same folder where the
	 * mvn execution was launched.
	 *
	 * @since 2.9
	 */
	@Parameter(property = "changes.runOnlyAtExecutionRoot", defaultValue = "true")
	protected boolean runOnlyAtExecutionRoot;

	/**
	 * Location of the file.
	 */
	@Parameter(defaultValue = "${project.build.directory}/classes/site/report", property = "outputDirectory", required = true)
	private File outputDirectory;

	/**
	 * 
	 * Character encoding of the output HTML site
	 */
	@Parameter(defaultValue = "UTF-8", property = "encoding", required = false)
	private String characterEncoding;

	/**
	 * 
	 * The input file that contains the release history
	 */
	@Parameter(defaultValue = "/src/site/changes.xml", property = "inputFilePath", required = false)
	private String inputFilePath;

	/**
	 * 
	 * The extension of output files
	 */
	@Parameter(defaultValue = "html", property = "ouputFileExtentsion", readonly = true, required = false)
	private String ouputFileExtentsion;

	/**
	 * ISO 639-1: two-letter codes, the language of the output HTML site.
	 */
	@Parameter(defaultValue = "en", readonly = true, required = false)
	private String language;

	/**
	 * Fail build when a changes.xml does not exist
	 */
	@Parameter(defaultValue = "false", property = "failOnMissingInputFile", readonly = true, required = false)
	private boolean failOnMissingInputFile;

	/**
	 * The URL of your issue managment system.
	 */
	// TODO make this parameter to required
	@Parameter(property = "issumeManagementUrl", required = false, readonly = true)
	private String issumeManagementUrl;

	/**
	 * Template generator that use for generate template files
	 */
	private final TemplateGenerator templateGenerator;

	/**
	 * 
	 * Instantinate a report generator Maven Mojo with default template
	 * generator.
	 */
	public ReleaseSiteGeneratorMojo()
	{
		this(new ThymeleafTemplateGenerator());
	}

	public ReleaseSiteGeneratorMojo(TemplateGenerator templateGenerator)
	{
		this.templateGenerator = templateGenerator;
	}

	/**
	 * 
	 * 
	 * @throws MojoExecutionException
	 *             Throw a MojoExecutionException if any error occur
	 */
	@Override
	public void execute() throws MojoExecutionException
	{
		getLog().info("Genearate RELEASE history report has been started");

		this.execute(this.mavenProject);

		// Copy CSS folder
		// FileHelper.copyResourceToDirectory("templates/css", new
		// File(outputDirectory, "css"));
		// FileHelper.copyResourceToDirectory("templates/font", new
		// File(outputDirectory, "font"));

		getLog().info("RELEASE history has been generated successfully");
	}

	private void execute(MavenProject mavenProject) throws MojoExecutionException
	{

		this.generateTemplate(mavenProject);

		// Set<MavenProject> moduleArtifacts =
		// this.getChildArtifacts(mavenProject, null);
		//
		// for (MavenProject module : moduleArtifacts)
		// {
		// this.generateTemplate(module);
		// }
	}

	/**
	 * Generate release notes for a MavenProject
	 * 
	 * @param mavenProject
	 * @throws TemplateException
	 * @return Return the resolved thymeleaf template
	 */
	private String generateTemplate(MavenProject mavenProject) throws MojoExecutionException
	{
		try
		{

			File inputFile = this.getInputFile(mavenProject);

			if (mavenProject == null || !inputFile.exists())
			{
				getLog().warn(String.format(
						"Could not generate release note for artifact '%s' because the '%s' file does not exist",
						mavenProject.getArtifactId(), inputFilePath));
				return null;
			}

			final ITemplateContext templateContext = this.prepareTempalteContext(mavenProject);
			String siteHtml = templateGenerator.generateSiteTemplate(templateContext);

			String fileName = String.format("%s.%s", mavenProject.getArtifactId(), this.ouputFileExtentsion);
			File outputFile = new File(this.outputDirectory.getPath() + "/" + fileName);

			FileUtils.writeStringToFile(outputFile, siteHtml, this.characterEncoding, false);

			return siteHtml;
		} catch (Exception e)
		{
			throw new MojoExecutionException("Failed to generate template", e);
		}

	}

	/**
	 * Prepare template context for maven project
	 * 
	 * @param project
	 * @return
	 * @throws MojoExecutionException
	 */
	private ITemplateContext prepareTempalteContext(MavenProject project) throws MojoExecutionException
	{
		if (project == null)
		{
			throw new IllegalArgumentException("Failed to preapred tepmlate context because 'project' is null");
		}

		File inputFile = this.getInputFile(project);

		try (FileInputStream fileInputStream = new FileInputStream(inputFile))
		{

			Unmarshaller unmarshaller = JaxbFactory.newInstance(JAXB_CONTEXT_PATH).unmarshaller();

			ChangesDocument changesDocument = unmarshaller
					.unmarshal(new StreamSource(fileInputStream), ChangesDocument.class).getValue();

			// Prepare the evaluation context
			final ITemplateContext templateContext = new DefaultTemplateContext(new Locale(this.language));
			templateContext.add("rootProject", this.mavenProject);
			templateContext.add("changesDocument", changesDocument);
			templateContext.add("project", project);
			templateContext.add("extension", this.ouputFileExtentsion);

			return templateContext;
		} catch (Exception e)
		{
			throw new MojoExecutionException(
					String.format("Failed to prepare template context for artifact %s", project.getArtifactId()), e);
		}
	}

	/**
	 * 
	 * @param mavenProject
	 * @return
	 * @throws MojoExecutionException
	 */
	private File getInputFile(MavenProject mavenProject) throws MojoExecutionException
	{
		File inputFile = new File(mavenProject.getBasedir(), this.inputFilePath);

		if (this.failOnMissingInputFile && (inputFile == null || !inputFile.exists()))
		{
			throw new MojoExecutionException("Input file %s does not exist for maven project %s", this.inputFilePath,
					mavenProject.getArtifactId());
		}

		return inputFile;
	}

}
