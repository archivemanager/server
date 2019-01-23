package org.archivemanager.util;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;



public class HeaderPopulator {
	private static File dir = new File("C:\\Documents and Settings\\Patrick McDonald\\workspace\\ATServer\\src\\web\\org");
	private StringBuffer header = new StringBuffer();
	
	
	public static void main(String[] args) {
		HeaderPopulator pop = new HeaderPopulator();
		pop.loadHeader();
		pop.visitAllFiles(dir);

	}
	protected void process(File file) {
		if(file.getName().endsWith(".java")) {
			System.out.println(file.getAbsoluteFile());
			String fileStr = readFile(file);
			if(!fileStr.startsWith("/*** Archivists' Toolkit(TM) Copyright 2009 Boston University")) {
				String newStr = header + fileStr;
				try {
					BufferedWriter out = new BufferedWriter(new FileWriter(file));
					out.write(newStr);
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}
    public void visitAllFiles(File dir) {
        if (dir.isDirectory()) {
            String[] children = dir.list();
            for (int i=0; i<children.length; i++) {
                visitAllFiles(new File(dir, children[i]));
            }
        } else {
            process(dir);
        }
    }
    protected void loadHeader() {
    	try {
            BufferedReader in = new BufferedReader(new FileReader("C:\\Documents and Settings\\Patrick McDonald\\workspace\\ATServer\\src\\header.txt"));
            String str;
            while((str = in.readLine()) != null) {
                header.append(str);
            }
            in.close();
        } catch (IOException e) {
        }
    }
    protected String readFile(File file) {
    	StringBuffer buff = new StringBuffer();
    	try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String str;
            while((str = in.readLine()) != null) {
                buff.append(str);
            }
            in.close();
        } catch (IOException e) {
        }
        return buff.toString();
    }
}
