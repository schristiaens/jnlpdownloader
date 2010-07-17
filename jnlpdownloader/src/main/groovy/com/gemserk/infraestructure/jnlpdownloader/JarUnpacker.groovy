package com.gemserk.infraestructure.jnlpdownloader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarUnpacker
{


	public void copyJarContent(File jarPath, File targetDir) throws IOException
	{
		println "Unpacking $jarPath"
		JarFile jar = new JarFile(jarPath);

		Enumeration<JarEntry> entries = jar.entries();
		while (entries.hasMoreElements())
		{
			JarEntry file = entries.nextElement();

			File f = new File(targetDir, file.getName());

			File parentFile = f.getParentFile();
			parentFile.mkdirs();
			
			if (file.isDirectory())
			{ // if its a directory, create it
				f.mkdir();
				continue;
			}

			InputStream is = null;

			try
			{
				is = jar.getInputStream(file); // get the input stream
				f << is
			}
			finally
			{
				if (is != null)
					is.close();
			}

		}

	}

}