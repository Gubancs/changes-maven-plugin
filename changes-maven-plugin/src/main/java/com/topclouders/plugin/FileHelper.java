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
public class FileHelper {

	/**
	 * This method will copy resources from the jar file of the current thread
	 * and extract it to the destination folder.
	 * 
	 * 
	 * @param classpathResource
	 * @param destinationDirectory
	 */
	public static void copyResourceToDirectory(String classpathResource, File destinationDirectory) {

		try {
			URL resource = FileHelper.class.getClassLoader().getResource(classpathResource);
			JarURLConnection jarURLConnection = (JarURLConnection) resource.openConnection();

			JarFile jarFile = jarURLConnection.getJarFile();

			/**
			 * Iterate all entries in the jar file.
			 */
			for (Enumeration<JarEntry> e = jarFile.entries(); e.hasMoreElements();) {

				JarEntry jarEntry = e.nextElement();
				String jarEntryName = jarEntry.getName();
				String jarConnectionEntryName = jarURLConnection.getEntryName();

				/**
				 * Extract files only if they match the path.
				 */
				if (jarEntryName.startsWith(jarConnectionEntryName)) {

					String filename = jarEntryName.startsWith(jarConnectionEntryName)
							? jarEntryName.substring(jarConnectionEntryName.length()) : jarEntryName;
					File currentFile = new File(destinationDirectory, filename);

					if (jarEntry.isDirectory()) {
						currentFile.mkdirs();
					} else {
						InputStream is = jarFile.getInputStream(jarEntry);
						OutputStream out = FileUtils.openOutputStream(currentFile);
						IOUtils.copy(is, out);
						is.close();
						out.close();
					}
				}
			}
		} catch (IOException e) {
			throw new RuntimeException("Failed to copy classpath resource", e);
		}

	}

}
