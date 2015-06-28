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

import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.io.FileUtils;
import org.apache.maven.changes._1_0.ChangesDocument;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Execute;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.thymeleaf.context.Context;
import org.thymeleaf.context.IContext;

/**
 * A maven Mojo that able to generate channges HTML site from changes.xml.
 *
 * @author <b>Gabor Kokeny</b> Developer at GE Capital <br/>
 *         Mail to :
 *         <a href="mailto:g4b0r.k0k3ny@gmail.com">g4b0r.k0k3ny.gmail.com</a>
 * @version 1.0.0
 */
@Execute(goal = ReportGeneratorMojo.GOAL_CHANGES_REPORT, phase = LifecyclePhase.PREPARE_PACKAGE)
@Mojo(name = ReportGeneratorMojo.GOAL_CHANGES_REPORT, defaultPhase = LifecyclePhase.PREPARE_PACKAGE, threadSafe = true)
public class ReportGeneratorMojo extends AbstractMojo {

	static final String JAXB_CONTEXT_PATH = "org.apache.maven.changes._1_0";

	static final String GOAL_CHANGES_REPORT = "changes-report";

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
	 * Reference to the maven project
	 */
	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	private MavenProject mavenProject;

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
	 * Template generator that use for generate template files
	 */
	private final TemplateGenerator templateGenerator;

	/**
	 * 
	 * Instantinate a report generator Maven Mojo with default template
	 * generator.
	 */
	public ReportGeneratorMojo() {
		this.templateGenerator = new TemplateGenerator();
		this.templateGenerator.setCharacterEncoding(this.characterEncoding);
	}

	/**
	 * 
	 * 
	 * @throws MojoExecutionException
	 *             Throw a MojoExecutionException if any error occur
	 */
	@Override
	public void execute() throws MojoExecutionException {
		getLog().info("Genearate RELEASE history report has been started");

		this.execute(this.mavenProject);

		// Copy CSS folder
		FileHelper.copyResourceToDirectory("templates/css", new File(outputDirectory, "css"));
		FileHelper.copyResourceToDirectory("templates/font", new File(outputDirectory, "font"));

		getLog().info("RELEASE history has been generated successfully");
	}

	private void execute(MavenProject mavenProject) throws MojoExecutionException {

		this.generateTemplate(mavenProject);

		Set<MavenProject> moduleArtifacts = this.getChildArtifacts(mavenProject, null);

		for (MavenProject module : moduleArtifacts) {
			this.generateTemplate(module);
		}
	}

	/**
	 * Generate release notes for a MavenProject
	 * 
	 * @param project
	 * @throws MojoExecutionException
	 */
	private void generateTemplate(MavenProject mavenProject) throws MojoExecutionException {
		try {

			File inputFile = this.getInputFile(mavenProject);

			if (mavenProject == null || !inputFile.exists()) {
				getLog().warn(String.format(
						"Could not generate release note for artifact '%s' because the '%s' file does not exist",
						mavenProject.getArtifactId(), inputFilePath));
				return;
			}

			final IContext templateContext = this.prepareTempalteContext(mavenProject);
			String siteHtml = templateGenerator.generateSite(templateContext);

			String fileName = String.format("%s.%s", mavenProject.getArtifactId(), this.ouputFileExtentsion);
			File outputFile = new File(this.outputDirectory.getPath() + "/" + fileName);

			FileUtils.writeStringToFile(outputFile, siteHtml, this.characterEncoding, false);

		} catch (Exception e) {
			throw new MojoExecutionException("Failed to execute generate report", e);
		}

	}

	/**
	 * Prepare template context for maven project
	 * 
	 * @param project
	 * @return
	 * @throws MojoExecutionException
	 */
	private IContext prepareTempalteContext(MavenProject project) throws MojoExecutionException {
		if (project == null) {
			throw new IllegalArgumentException("Failed to preapred tepmlate context because 'project' is null");
		}

		File inputFile = this.getInputFile(project);

		try (FileInputStream fileInputStream = new FileInputStream(inputFile)) {

			Unmarshaller unmarshaller = JaxbFactory.newInstance(JAXB_CONTEXT_PATH).getUnmarshaller();

			ChangesDocument changesDocument = unmarshaller
					.unmarshal(new StreamSource(fileInputStream), ChangesDocument.class).getValue();

			// Prepare the evaluation context
			final Context templateContext = new Context(new Locale(this.language));
			templateContext.setVariable("rootProject", this.mavenProject);
			templateContext.setVariable("changesDocument", changesDocument);
			templateContext.setVariable("project", project);
			Set<MavenProject> artifacts = this.getRelevantArtifacts(this.mavenProject);
			artifacts.add(this.mavenProject);
			templateContext.setVariable("artifacts", artifacts);
			templateContext.setVariable("extension", this.ouputFileExtentsion);

			return templateContext;
		} catch (Exception e) {
			throw new MojoExecutionException(
					String.format("Failed to prepare template context for artifact %s", project.getArtifactId()), e);
		}
	}

	private Set<MavenProject> getRelevantArtifacts(MavenProject mavenProject) {
		Set<MavenProject> modules = this.getChildArtifacts(mavenProject, new Predicate<MavenProject>() {
			@Override
			public boolean test(MavenProject project) {
				File inputFile = new File(project.getBasedir(), ReportGeneratorMojo.this.inputFilePath);
				return inputFile != null && inputFile.exists();
			}
		});

		return modules;
	}

	/**
	 * @param mavenProject
	 * @param predicate
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private Set<MavenProject> getChildArtifacts(MavenProject mavenProject, Predicate<MavenProject> predicate) {
		final List<MavenProject> moduleProjects = new CopyOnWriteArrayList<>(mavenProject.getCollectedProjects());

		for (MavenProject module : moduleProjects) {
			moduleProjects.addAll(this.getChildArtifacts(module, predicate));
		}

		if (predicate == null) {
			return new HashSet<>(moduleProjects);
		} else {
			return moduleProjects.stream().filter(predicate).collect(Collectors.toSet());
		}
	}

	/**
	 * 
	 * @param mavenProject
	 * @return
	 * @throws MojoExecutionException
	 */
	private File getInputFile(MavenProject mavenProject) throws MojoExecutionException {
		File inputFile = new File(mavenProject.getBasedir(), this.inputFilePath);

		if (this.failOnMissingInputFile && (inputFile == null || !inputFile.exists())) {
			throw new MojoExecutionException("Input file %s does not exist for maven project %s", this.inputFilePath,
					mavenProject.getArtifactId());
		}

		return inputFile;
	}

}
