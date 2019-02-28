package fr.devsylone.fallenkingdom.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils
{
	public static void zipFile(File fileToZip, String folderName, ZipOutputStream zipOut)
	{
		zipFile(fileToZip, folderName, zipOut, true);
	}

	public static void zipFile(File fileToZip, String folderName, ZipOutputStream zipOut, final boolean zipZipped)
	{
		if(fileToZip.isHidden())
		{
			return;
		}
		if(fileToZip.isDirectory())
		{
			File[] children = fileToZip.listFiles(new FilenameFilter()
			{

				@Override
				public boolean accept(File dir, String name)
				{
					return !name.endsWith(".jar") && (zipZipped || !name.endsWith(".zip"));
				}
			});
			for(File childFile : children)
			{
				zipFile(childFile, folderName + (!folderName.isEmpty() ? File.separator : "") + fileToZip.getName(), zipOut, zipZipped);
			}
			return;
		}
		if(fileToZip.getName().endsWith(".jar"))
			return;

		FileInputStream fis;
		try
		{
			fis = new FileInputStream(fileToZip);
			ZipEntry zipEntry = new ZipEntry(folderName + File.separator + fileToZip.getName());
			zipOut.putNextEntry(zipEntry);
			byte[] bytes = new byte[4096 * 1024];
			int length;
			while((length = fis.read(bytes)) > 0)
			{
				zipOut.write(bytes, 0, length);
			}
			zipOut.flush();
			zipOut.closeEntry();
			fis.close();
		}catch(FileNotFoundException e)
		{
			e.printStackTrace();
		}catch(IOException e)
		{
			e.printStackTrace();
		}
	}
}
