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

package com.topclouders.releaseplugin.helper;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * 
 * 
 * @author Gabor Kokeny
 * @version 1.0.0
 *
 */
public class FileHelper
{

	private final JarURLConnection jarURLConnection;

	private final JarFile jarFile;

	/**
	 * FileHelper default constructor
	 */
	private FileHelper(JarURLConnection jarURLConnection, JarFile jarFile)
	{
		this.jarURLConnection = jarURLConnection;
		this.jarFile = jarFile;
	}

	/**
	 * This method will copy resources from the jar file of the current thread
	 * and extract it to the destination folder.
	 * 
	 * 
	 * @param resourcePath
	 * @param destination
	 */
	public static void copyResourceToDirectory(String resourcePath, File destination)
	{
		FileHelper.validateArguments(resourcePath, destination);

		try
		{
			URL resource = FileHelper.class.getClassLoader().getResource(resourcePath);
			JarURLConnection jarURLConnection = (JarURLConnection) resource.openConnection();
			JarFile jarFile = jarURLConnection.getJarFile();

			// Create a new file helper
			FileHelper fileHelper = new FileHelper(jarURLConnection, jarFile);
			fileHelper.copyResource(resourcePath, destination);
		} catch (IOException e)
		{
			throw new RuntimeException(String.format("Failed to copy classpath resource to destination directory %s",
					destination.getAbsolutePath()), e);
		}

	}

	private static boolean validateArguments(String resourcePath, File destination)
	{
		if (resourcePath == null || resourcePath.isEmpty())
		{
			throw new IllegalArgumentException("Method parameter 'resourcePath' cannot be null or empty string");
		}

		if (destination == null)
		{
			throw new IllegalArgumentException("Method parameter 'destination' cannot be null");
		} else if (destination.exists() && !destination.isDirectory())
		{
			throw new IllegalArgumentException("Method parameter 'destination' must be a directory");
		}

		return true;
	}

	private void copyResource(String resourcePath, File destination)
	{
		FileHelper.validateArguments(resourcePath, destination);

		// Iterate on jar entries
		for (Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements();)
		{

			JarEntry jarEntry = e.nextElement();

			final String jarEntryName = jarEntry.getName();
			final String jarConnectionEntryName = this.jarURLConnection.getEntryName();

			if (jarEntryName.startsWith(jarConnectionEntryName))
			{
				this.copyJarEntry(jarEntry, destination);
			}

		}

	}

	private String getFileName(final JarEntry jarEntry)
	{
		final String jarEntryName = jarEntry.getName();
		final String connectionEntryName = this.jarURLConnection.getEntryName();

		return jarEntryName.startsWith(connectionEntryName) ? jarEntryName.substring(connectionEntryName.length())
				: jarEntryName;
	}

	private void copyJarEntry(final JarEntry jarEntry, File destinationDirectory)
	{
		final String fileName = this.getFileName(jarEntry);
		final File currentFile = new File(destinationDirectory, fileName);

		if (jarEntry.isDirectory())
		{
			currentFile.mkdirs();
		} else
		{
			try (OutputStream out = FileUtils.openOutputStream(currentFile);
					InputStream is = this.jarFile.getInputStream(jarEntry))
			{
				IOUtils.copy(is, out);
			} catch (Exception e)
			{

			}
		}

	}

}
