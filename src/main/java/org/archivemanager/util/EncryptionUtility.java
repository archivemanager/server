package org.archivemanager.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class EncryptionUtility {
	
	
	public static byte[] MD5Bytes(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		MessageDigest md;
		md = MessageDigest.getInstance("MD5");
		md.update(text.getBytes("iso-8859-1"), 0, text.length());
		return md.digest();
	}
	public static String MD5String(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		byte[] md5hash = MD5Bytes(text);
		return convertToHex(md5hash);
	}
	private static String convertToHex(byte[] data) {
	    StringBuffer buf = new StringBuffer();
	    for (int i = 0; i < data.length; i++) {
	      int halfbyte = (data[i] >>> 4) & 0x0F;
	      int two_halfs = 0;
	      do {
	        if ((0 <= halfbyte) && (halfbyte <= 9))
	          buf.append((char) ('0' + halfbyte));
	        else
	          buf.append((char) ('a' + (halfbyte - 10)));
	        halfbyte = data[i] & 0x0F;
	      } while (two_halfs++ < 1);
	    }
	    return buf.toString();
	}
}
