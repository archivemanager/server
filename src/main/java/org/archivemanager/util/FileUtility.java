package org.archivemanager.util;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.archivemanager.services.content.Mimetypes;


public class FileUtility {

	
	/**
     * Removes the extension from a filename.
     * @param filename  the filename to query, null returns null
     * @return the filename minus the extension
     */
    public static String removeExtension(String filename) {
        if (filename == null) {
            return null;
        }
        int extensionPos = filename.lastIndexOf('.');
        int lastUnixPos = filename.lastIndexOf('/');
        int lastWindowsPos = filename.lastIndexOf('\\');
        int lastSeparator = Math.max(lastUnixPos, lastWindowsPos);       
        int index = (lastSeparator > extensionPos ? -1 : extensionPos);
        if (index == -1) {
            return filename;
        } else {
            return filename.substring(0, index);
        }
    }
	/**
	 * Return a filename's extension.
	 * @return the extension, or null if one cannot be detected
	 */
	public static String getExtension(String filename) {
		if (filename == null) {
			return null;
		}
		// get extension
		int pos = filename.lastIndexOf('.');
		if (pos == -1 || pos == filename.length() - 1)
			return null;
		String ext = filename.substring(pos + 1, filename.length());
		ext = ext.toLowerCase();
		return ext;
	}
	public static String readToString(File file) throws Exception {
		StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        char[] buf = new char[1024];
        int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        return fileData.toString();
	}
	public static File readToFile(InputStream in, String extension) throws Exception {
		File temp = File.createTempFile(java.util.UUID.randomUUID().toString(), extension);
		temp.deleteOnExit();
		OutputStream out = new FileOutputStream(temp);
		IOUtility.pipe(in, out);
		in.close();
		out.flush();
		out.close();
	    return temp;
	}
	@SuppressWarnings("resource")
	public static void writeToFile(File sourceFile, File destFile) throws IOException {
	    if(!destFile.exists()) {
	        destFile.createNewFile();
	    }
	    FileChannel source = null;
	    FileChannel destination = null;
	    try {
	        source = new FileInputStream(sourceFile).getChannel();
	        destination = new FileOutputStream(destFile).getChannel();
	        destination.transferFrom(source, 0, source.size());
	    }
	    finally {
	        if(source != null) {
	            source.close();
	        }
	        if(destination != null) {
	            destination.close();
	        }
	    }
	}
	public static void writeToFile(String content, String fileName) throws Exception {
		File file = new File(fileName);
		ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes());
		OutputStream out = new FileOutputStream(file);
		IOUtility.pipe(in, out);
	}
	public static void writeToFile(String content, File file) throws Exception {
		ByteArrayInputStream in = new ByteArrayInputStream(content.getBytes());
		OutputStream out = new FileOutputStream(file);
		IOUtility.pipe(in, out);
	}
	public static void writeToFile(byte[] content, File file) throws Exception {
		ByteArrayInputStream in = new ByteArrayInputStream(content);
		OutputStream out = new FileOutputStream(file);
		IOUtility.pipe(in, out);
	}
	public static boolean isImage(String fileName) {
		String mimetype = getMimetype(fileName);
        if(mimetype.equals(Mimetypes.MIMETYPE_IMAGE_GIF) || 
        		mimetype.equals(Mimetypes.MIMETYPE_IMAGE_JPEG) || 
        		mimetype.equals(Mimetypes.MIMETYPE_IMAGE_TIF) ||
        		mimetype.equals(Mimetypes.MIMETYPE_IMAGE_PNG))
        	return true;
		return false;
	}
	public static boolean isVideo(String fileName) {
		String mimetype = getMimetype(fileName);
		return mimetype.equals(Mimetypes.MIMETYPE_AVI) || 
				mimetype.equals(Mimetypes.MIMETYPE_AVI) || 
				mimetype.equals(Mimetypes.MIMETYPE_MOV) ||
				mimetype.equals(Mimetypes.MIMETYPE_MP4) ||
				mimetype.equals(Mimetypes.MIMETYPE_MP3) ||
				mimetype.equals(Mimetypes.MIMETYPE_MPEG) ||
				mimetype.equals(Mimetypes.MIMETYPE_WAV) ||
				mimetype.equals(Mimetypes.MIMETYPE_FLV);
	}
	public static boolean hasChildren(File file) {
		if(!file.isDirectory()) return false;
		String[] dirs = file.list();
		return dirs.length > 0;
	}
	public static boolean hasChildFiles(File file) {
		if(!file.isDirectory()) return false;
		File[] dirs = file.listFiles(new FileFilter() {
		   	@Override
			public boolean accept(File f) {
		   		if(!f.isDirectory()) return true;
				return false;
			}
		});
		return dirs.length > 0;
	}
	public static boolean hasChildDirectories(File file) {
		if(!file.isDirectory()) return false;
		File[] dirs = file.listFiles(new FileFilter() {
		   	@Override
			public boolean accept(File f) {
				if(f.isDirectory()) return true;
				return false;
			}
		});
		return dirs.length > 0;
	}
	public static boolean isFile(String url) {
		String fileName = url.toLowerCase();
		if(fileName.contains("?") || fileName.endsWith(".gif") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") ||
			fileName.endsWith(".pdf") || fileName.endsWith(".tif") || fileName.endsWith(".tiff") || fileName.endsWith(".mp3") || fileName.endsWith(".mp4") ||
			fileName.endsWith(".flv") || fileName.endsWith(".html") || fileName.endsWith(".htm") || fileName.endsWith(".xml") || fileName.endsWith(".xls") ||
			fileName.endsWith(".ppt") || fileName.endsWith(".doc") || fileName.endsWith(".css") || fileName.endsWith(".js"))
				return true;			
		return false;
	}
	public static String getMimetype(String name) {
		if(name != null) {
			String fileName = name.toLowerCase();
			if(fileName.contains("?")) fileName = fileName.substring(0, fileName.indexOf("?"));
			if(fileName.endsWith(".gif")) return Mimetypes.MIMETYPE_IMAGE_GIF;
			if(fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) return Mimetypes.MIMETYPE_IMAGE_JPEG;
			if(fileName.endsWith(".png")) return Mimetypes.MIMETYPE_IMAGE_PNG;
			if(fileName.endsWith(".pdf")) return Mimetypes.MIMETYPE_PDF;
			if(fileName.endsWith(".tif") || fileName.endsWith(".tiff")) return Mimetypes.MIMETYPE_IMAGE_TIF;
			if(fileName.endsWith(".mp3")) return Mimetypes.MIMETYPE_MP3;
			if(fileName.endsWith(".mp4")) return Mimetypes.MIMETYPE_MP4;
			if(fileName.endsWith(".flv")) return Mimetypes.MIMETYPE_FLV;
			if(fileName.endsWith(".html") || fileName.endsWith(".htm")) return Mimetypes.MIMETYPE_HTML;
			if(fileName.endsWith(".xml")) return Mimetypes.MIMETYPE_XML;
			if(fileName.endsWith(".xls")) return Mimetypes.MIMETYPE_EXCEL;
			if(fileName.endsWith(".ppt")) return Mimetypes.MIMETYPE_PPT;
			if(fileName.endsWith(".doc")) return Mimetypes.MIMETYPE_WORD;
			if(fileName.endsWith(".css")) return Mimetypes.MIMETYPE_TEXT_CSS;
			if(fileName.endsWith(".js")) return Mimetypes.MIMETYPE_TEXT_JAVASCRIPT;
			else return Mimetypes.MIMETYPE_HTML;
		}
		return "";
	}
	public static String getFileSize(long size) {
	    if(size <= 0) return "0";
	    final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
	    int digitGroups = (int) (Math.log10(size)/Math.log10(1024));
	    return new DecimalFormat("#,##0.#").format(size/Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}
	public static List<File> listFiles(File dir) {
		if(dir == null) return new ArrayList<File>(0);
	    List<File> fileTree = new ArrayList<File>();
	    for (File entry : dir.listFiles()) {
	        if (entry.isFile() && !entry.getName().startsWith("~")) fileTree.add(entry);
	        else fileTree.addAll(listFiles(entry));
	    }
	    return fileTree;
	}
	public static boolean deleteRecursive(File path) throws FileNotFoundException{
        if (!path.exists()) throw new FileNotFoundException(path.getAbsolutePath());
        boolean ret = true;
        if (path.isDirectory()){
            for (File f : path.listFiles()){
                ret = ret && FileUtility.deleteRecursive(f);
            }
        }
        return ret && path.delete();
    }
}
