/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.archivemanager.util;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;


public class TempFileProvider {
	private final static Logger log = Logger.getLogger(TempFileProvider.class.getName());
    /** 
     * subdirectory in the temp directory where Alfresco temporary files will go 
     */
    public static final String ALFRESCO_TEMP_FILE_DIR = "OpenApps";
    
    /**
     * The prefix for the long life temporary files.
     */
    public static final String ALFRESCO_LONG_LIFE_FILE_DIR = "longLife";

    /** the system property key giving us the location of the temp directory */
    public static final String SYSTEM_KEY_TEMP_DIR = "java.io.tmpdir";

    private static int MAX_RETRIES = 3;

    /**
     * Static class only
     */
    private TempFileProvider()
    {
    }

    /**
     * Get the Java Temp dir e.g. java.io.tempdir
     * 
     * @return Returns the system temporary directory i.e. <code>isDir == true</code>
     */
    public static File getSystemTempDir()
    {
        String systemTempDirPath = System.getProperty(SYSTEM_KEY_TEMP_DIR);
        if (systemTempDirPath == null)
        {
            throw new RuntimeException("System property not available: " + SYSTEM_KEY_TEMP_DIR);
        }
        File systemTempDir = new File(systemTempDirPath);
        return systemTempDir;
    }
    
    /**
     * Get the Alfresco temp dir, by defaut %java.io.tempdir%/Alfresco.  
     * Will create the temp dir on the fly if it does not already exist.
     * 
     * @return Returns a temporary directory, i.e. <code>isDir == true</code>
     */
    public static File getTempDir()
    {
        File systemTempDir = getSystemTempDir();
        // append the Alfresco directory
        File tempDir = new File(systemTempDir, ALFRESCO_TEMP_FILE_DIR);
        // ensure that the temp directory exists
        if (tempDir.exists())
        {
            // nothing to do
        }
        else
        {
            // not there yet
            if (!tempDir.mkdirs())
            {
                throw new RuntimeException("Failed to create temp directory: " + tempDir);
            }
            log.info("Created temp directory: " + tempDir);
        }
        // done
        return tempDir;
    }
    
    /**
     * creates a longer living temp dir.   Files within the longer living 
     * temp dir will not be garbage collected as soon as "normal" temporary files.
     * By default long life temp files will live for for 24 hours rather than 1 hour.
     * <p>
     * Code using the longer life temporary files should be careful to clean up since 
     * abuse of this feature may result in out of memory/disk space errors.
     * @param key can be blank in which case the system will generate a folder to be used by all processes
     * or can be used to create a unique temporary folder name for a particular process.  At the end of the process 
     * the client can simply delete the entire temporary folder.  
     * @return the long life temporary directory
     */
    public static File getLongLifeTempDir(String key)
    {
        /**
         * Long life temporary directories have a prefix at the start of the 
         * folder name.
         */
        String folderName = ALFRESCO_LONG_LIFE_FILE_DIR + "_" + key;
        
        File tempDir = getTempDir();
        
        // append the Alfresco directory
        File longLifeDir = new File(tempDir, folderName);
        // ensure that the temp directory exists
        
        if (longLifeDir.exists())
        {
            log.info("Already exists: " + longLifeDir);
            // nothing to do
            return longLifeDir;
        }
        else
        {
            /**
             * We need to create a temporary directory
             * 
             * We may have a race condition here if more than one thread attempts to create 
             * the temp dir.
             *  
             * mkdirs can't be synchronized
             * See http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=4742723
             */
            for(int retry = 0; retry < MAX_RETRIES; retry++)
            {
                boolean created = longLifeDir.mkdirs();
            
                if (created)
                {
                    // Yes we created the temp dir
                    log.info("Created long life temp directory: " + longLifeDir);
                    return longLifeDir;
                }
                else
                {   
                    if(longLifeDir.exists())
                    {
                        // created by another thread, but that's O.K.  
                        log.info("Another thread created long life temp directory: " + longLifeDir);
                        return longLifeDir;
                    }
                }
            }
        }
        throw new RuntimeException("Failed to create temp directory: " + longLifeDir);
    }
        
    /**
     * Create a temp file in the temp dir.
     * 
     * @return Returns a temp <code>File</code> that will be located in the
     *         <b>Alfresco</b> subdirectory of the default temp directory
     * 
     * @see #ALFRESCO_TEMP_FILE_DIR
     * @see File#createTempFile(java.lang.String, java.lang.String)
     */
    public static File createTempFile(String prefix, String suffix)
    {
        File tempDir = TempFileProvider.getTempDir();
        // we have the directory we want to use
        return createTempFile(prefix, suffix, tempDir);
    }

    /**
     * @return Returns a temp <code>File</code> that will be located in the
     *         given directory
     * 
     * @see #TEMP_FILE_DIR
     * @see File#createTempFile(java.lang.String, java.lang.String)
     */
    public static File createTempFile(String prefix, String suffix, File directory)
    {
        try
        {
            File tempFile = File.createTempFile(prefix, suffix, directory);
            return tempFile;
        } catch (IOException e)
        {
            throw new RuntimeException("Failed to created temp file: \n" +
                    "   prefix: " + prefix + "\n"
                    + "   suffix: " + suffix + "\n" +
                    "   directory: " + directory,
                    e);
        }
    }

}
