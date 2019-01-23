/*
 * Copyright (C) 2009 Heed Technology Inc.
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

import java.io.*;


public class IOUtility {

	
	public static void pipe(InputStream in, OutputStream out) throws IOException {
		byte buffer[] = new byte[8192];
		while(true){
			int len = in.read(buffer,0,8192);
			if(len < 0) break;
			out.write(buffer,0,len);
		}
		in.close();
		out.flush();
		out.close();
	}
	public static File[] getFiles(File dir) {
		return dir.listFiles(new FileFilter() {
			public boolean accept(File file) {
				return file.isFile();
			}
		});
	}
	public static String convertStreamToString(InputStream is) {
        StringBuilder sb = new StringBuilder();
        String line = null;
        try {
        	BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
	public static byte[] read(InputStream is) {
		try {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int nRead;
			byte[] data = new byte[16384];
			while ((nRead = is.read(data, 0, data.length)) != -1) {
				buffer.write(data, 0, nRead);
			}
			buffer.flush();
			return buffer.toByteArray();			
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
		return new byte[0];
	}
	public static void writeInt(byte[] array, int i, int offset) {
		array[offset++] = (byte) ((i >>> 24) & 0xFF);
		array[offset++] = (byte) ((i >>> 16) & 0xFF);
		array[offset++] = (byte) ((i >>> 8) & 0xFF);
		array[offset] = (byte) (i & 0xFF);
	}
	public static void writeLong(byte[] array, long i, int offset) {
		array[offset++] = (byte) ((i >>> 56) & 0xFF);
		array[offset++] = (byte) ((i >>> 48) & 0xFF);
		array[offset++] = (byte) ((i >>> 40) & 0xFF);
		array[offset++] = (byte) ((i >>> 32) & 0xFF);
		array[offset++] = (byte) ((i >>> 24) & 0xFF);
		array[offset++] = (byte) ((i >>> 16) & 0xFF);
		array[offset++] = (byte) ((i >>> 8) & 0xFF);
		array[offset] = (byte) ((i & 0xFF));
	}
	public static int readInt(byte[] array, int offset) {
		int i = 0;
		i |= ((int) array[offset++] & 0xFF) << 24;
		i |= ((int) array[offset++] & 0xFF) << 16;
		i |= ((int) array[offset++] & 0xFF) << 8;
		i |= array[offset] & 0xFF;
		return i;
	}
	public static long readLong(byte[] array, int offset) {
		long i = 0;
		i |= ((long) array[offset++] & 0xFF) << 56;
		i |= ((long) array[offset++] & 0xFF) << 48;
		i |= ((long) array[offset++] & 0xFF) << 40;
		i |= ((long) array[offset++] & 0xFF) << 32;
		i |= ((long) array[offset++] & 0xFF) << 24;
		i |= ((long) array[offset++] & 0xFF) << 16;
		i |= ((long) array[offset++] & 0xFF) << 8;
		i |= array[offset] & 0xFF;
		return i;
	}
	public static char readChar(byte[] array, int offset) {
		char i = 0;
		i |= (array[offset++] & 0xFF) << 8;
		i |= array[offset] & 0xFF;
		return i;
	}
	
}
