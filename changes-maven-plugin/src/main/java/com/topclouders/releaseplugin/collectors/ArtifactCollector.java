package com.topclouders.releaseplugin.collectors;

import java.util.Set;

import org.apache.maven.project.MavenProject;

/**
 * 
 * 
 * @author Gabor Kokeny
 * @version 1.1.0
 *
 */
public interface ArtifactCollector
{

	Set<MavenProject> collect();
}
