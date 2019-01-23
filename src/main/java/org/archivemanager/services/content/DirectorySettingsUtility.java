package org.archivemanager.services.content;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.UUID;

import org.archivemanager.services.content.data.DirectorySettingsXmlImportHandler;
import org.archivemanager.util.XMLUtility;


public class DirectorySettingsUtility {
		
	
	public static DirectorySettings getDirectorySettings(File directory) {
		DirectorySettingsXmlImportHandler handler = new DirectorySettingsXmlImportHandler();
		File settingsFile = getSettingsFile(directory);
		boolean dirty = false;
		DirectorySettings settings = null;
		if(settingsFile.length() > 0) {
			try {
				XMLUtility.SAXParse(false, settingsFile, handler);
				settings = handler.getDirectorySettings();
			} catch(Exception e) {
				e.printStackTrace();
			}
		} 
		if(settings == null) {
			String uid = getUUID(settingsFile);
			settings = new DirectorySettings();
			settings.setUid(uid);
			settings.setName(directory.getName());
			settings.setPath(directory.getPath());
			dirty = true;
		}
		File[] files = directory.listFiles(DirectorySettingsUtility.fileFilter);		
		for(File file : files) {
			boolean entered = false;
			for(FileSettings fileSettings : settings.getFiles()) {
				if(fileSettings.getPath().equals(file.getPath()) && fileSettings.getName().equals(file.getName())) {
					entered = true;
					fileSettings.setDeleted(false);
				}
			}
			if(!entered) {
				String uid = UUID.randomUUID().toString();
				settings.getFiles().add(new FileSettings(uid, file.getName(), file.getPath(), file.length()));
				dirty = true;
			}
		}
		if(dirty) {
			try {
				StringWriter buff = new StringWriter();
				buff.write("<directory uid='"+settings.getUid()+"'>");
				buff.write("<path><![CDATA["+settings.getPath()+"]]></path>");
				buff.write("<name><![CDATA["+settings.getName()+"]]></name>");
				buff.write("<files>");
				for(FileSettings file : settings.getFiles()) {
					buff.write("<file uid='"+file.getUid()+"' size='"+file.getSize()+"'>");
					buff.write("<path><![CDATA["+file.getPath()+"]]></path>");
					buff.write("<name><![CDATA["+file.getName()+"]]></name>");
				buff.write("</file>");
				}
				buff.write("</files>");
				buff.write("</directory>");
				FileWriter writer = new FileWriter(settingsFile, false);				
				writer.write(buff.toString());
				writer.close();
			} catch(IOException e) {
				e.printStackTrace();
			}
		}
		return settings;
	}
	public static DirectorySettings getDirectorySettings(File directory, String uid) {
		if(directory.exists()) {
			File settingsFile = getSettingsFile(directory);
			if(settingsFile != null && settingsFile.exists()) {
				String settingsUid = getUUID(settingsFile);
				if(settingsUid != null && settingsUid.equals(uid)) {
					return getDirectorySettings(directory);
				} else {
					File[] children = directory.listFiles(directoryFilter);
					for(File child : children) {
						DirectorySettings childSettings = getDirectorySettings(child, uid);
						if(childSettings != null) 
							return childSettings;
					}
				}
			}
		}		
		return null;
	}
	public static FileSettings getFileSettings(File directory, String uid) {
		if(directory.exists()) {
			DirectorySettings settings = getDirectorySettings(directory);
			if(settings != null) {
				for(FileSettings file : settings.getFiles()) {
					if(file.getUid().equals(uid))
						return file;
				}
			}
			File[] children = directory.listFiles(directoryFilter);
			for(File child : children) {
				FileSettings childSettings = getFileSettings(child, uid);
				if(childSettings != null) {
					return childSettings;
				}
			}
		}		
		return null;
	}
	public static String getUUID(File settingsFile) {
		String name = settingsFile.getName();
		String id = name.substring(0, name.length()-4);
		return id;
	}
	public static File getSettingsFile(File parent) {
		File settingsDir = null;
		try {
			settingsDir = new File(parent, ".archive");
			if(!settingsDir.exists()) 
					settingsDir.mkdir();
		} catch(Exception e) {
			e.printStackTrace();
		}
		if(settingsDir != null && settingsDir.exists()) {
			try {
				File[] files = settingsDir.listFiles(fileFilter);
				for(File f : files) {
					if(f.getName().endsWith(".xml")) {
						return f;
					}
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		String uuid = UUID.randomUUID().toString();
		File file = settingsDir.getName().endsWith("/") ? new File(settingsDir, uuid+".xml") : new File(settingsDir, "/"+uuid+".xml");
		try {
			file.createNewFile();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return file;
	}
	
	public static FileFilter directoryFilter = new FileFilter() {
		@Override
		public boolean accept(File file) {
			String fileName = file.getName();
			if(file.isDirectory() && !fileName.startsWith(".archive")) return true;
			return false;
		}
	};
	public static FileFilter fileFilter = new FileFilter() {
		@Override
		public boolean accept(File file) {
			String fileName = file.getName();
			if(file.isFile() && !fileName.equals("Thumbs.db") && !fileName.equals(".DS_Store")) return true;
			return false;
		}
	};
	public static String getFileSize(long size) {
	    if(size <= 0) return "0";
	    final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
	    int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
	    return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}
}
