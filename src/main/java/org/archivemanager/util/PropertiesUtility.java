/*
 * Copyright (C) 2011 Heed Technology Inc.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package org.archivemanager.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Map.Entry;

public class PropertiesUtility {

	
	public static Properties loadFromClasspath(String name) throws FileNotFoundException, IOException {
		URL url =  ClassLoader.getSystemResource("test.properties");
		Properties p = new Properties();
		p.load(new FileInputStream(new File(url.getFile())));
		return p;
	}
	public static Map<String,String> loadFromFilesystem(String file) {
		Properties props = new Properties();
        try {
        	FileInputStream stream = new FileInputStream( new File( file ) );
            try {
            	props.load( stream );
            } finally {
                stream.close();
            }
        } catch(Exception e) {
            throw new IllegalArgumentException( "Unable to load " + file, e );
        }
        Set<Entry<Object,Object>> entries = props.entrySet();
        Map<String,String> stringProps = new HashMap<String,String>();
        for(Entry<Object,Object> entry : entries) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            stringProps.put( key, value );
        }
        return stringProps;
    }
}
