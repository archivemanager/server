/**
* <pre>
* Copyright (c) 2004 EasyAsk Inc. All Rights Reserved.
*
* The Java source code is the confidential and proprietary information
* of EasyAsk Inc. ("Confidential Information").  You shall
* not disclose such Confidential Information and shall use it only in
* accordance with the terms of the license agreement you entered into
* with EasyAsk.
*
* EasyAsk MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF
* THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED
* TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
* PARTICULAR PURPOSE, OR NON-INFRINGEMENT. EasyAsk SHALL NOT BE LIABLE FOR
* ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
* DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES.
*
* @author Patrick McDonald (pmcdonald@easyask.com)
* </pre>
*/
package org.archivemanager.util;
import java.io.*;
import java.net.URL;
import java.net.Authenticator;
import java.util.HashMap;
import java.util.Map;

import org.xml.sax.SAXException;

import javax.xml.parsers.*;

import org.xml.sax.helpers.DefaultHandler;
//import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import org.xml.sax.InputSource;


public class XMLUtility {
    private static DocumentBuilderFactory factory;
	private static SAXParserFactory saxfactory;
	
	/** Mask for hex encoding */
    private static final int MASK = (1 << 4) - 1;
    /** Digits used string encoding */
    private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    /**
	 * From Unicode codes 0x00 to 0x20, only 0x9, 0xA, and 0xD are allowed (tab, newline, and cr)
	 * InvalidUnicodeMap holds all of these.
	 */
	final static Map<String,Character> InvalidUnicodeMap = new HashMap<String,Character>();
	static {
		InvalidUnicodeMap.put("0x0", new Character('\u0000'));
		InvalidUnicodeMap.put("0x1", new Character('\u0001'));
		InvalidUnicodeMap.put("0x2", new Character('\u0002'));
		InvalidUnicodeMap.put("0x3", new Character('\u0003'));
		InvalidUnicodeMap.put("0x4", new Character('\u0004'));
		InvalidUnicodeMap.put("0x5", new Character('\u0005'));
		InvalidUnicodeMap.put("0x6", new Character('\u0006'));
		InvalidUnicodeMap.put("0x7", new Character('\u0007'));
		InvalidUnicodeMap.put("0x8", new Character('\u0008'));
		InvalidUnicodeMap.put("0xb", new Character('\u000B'));
		InvalidUnicodeMap.put("0xc", new Character('\u000C'));
		InvalidUnicodeMap.put("0xe", new Character('\u000E'));
		InvalidUnicodeMap.put("0xf", new Character('\u000F'));
		InvalidUnicodeMap.put("0x10", new Character('\u0010'));
		InvalidUnicodeMap.put("0x11", new Character('\u0011'));
		InvalidUnicodeMap.put("0x12", new Character('\u0012'));
		InvalidUnicodeMap.put("0x13", new Character('\u0013'));
	}
	
    public static org.w3c.dom.Document newDOMDocument() {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            org.w3c.dom.Document doc = builder.newDocument();
            return doc;
        } catch (ParserConfigurationException e) { System.err.println(e); }
        return null;
    }
    /*
	public static void outputDOMDocument(org.w3c.dom.Document doc, String file) {
        try {
            OutputFormat format = new OutputFormat(doc);
            format.setLineWidth(65);
            format.setIndenting(true);
            format.setIndent(2);
            com.sun.org.apache.xml.internal.serialize.XMLSerializer serializer = new com.sun.org.apache.xml.internal.serialize.XMLSerializer(new FileOutputStream(new File(file)), format);
            serializer.serialize(doc);
        } catch (IOException e) { System.err.println(e); }
    }
    */
    public static org.w3c.dom.Document getDOMDocumentFromString(byte[] content) {
		try {
			if(factory == null) factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			org.w3c.dom.Document doc = factory.newDocumentBuilder().parse(new ByteArrayInputStream(content));
			return doc;
		} catch (SAXException e) { e.printStackTrace();
		} catch (ParserConfigurationException e) { e.printStackTrace();
		} catch (IOException e) { e.printStackTrace(); }
		return null;
	}
	public static org.w3c.dom.Document getDOMDocumentFromString(String content) {
		try {
			if(factory == null) factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			org.w3c.dom.Document doc = factory.newDocumentBuilder().parse(new InputSource(new StringReader(content)));
			return doc;
		} catch (SAXException e) { e.printStackTrace();
		} catch (ParserConfigurationException e) { e.printStackTrace();
		} catch (IOException e) { e.printStackTrace(); }
		return null;
	}
	public static org.w3c.dom.Document getDOMDocumentFromFileName(String sFileName) {
		try {
			if(factory == null) factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			org.w3c.dom.Document doc = factory.newDocumentBuilder().parse(new File(sFileName));
			return doc;
		} catch (SAXException e) { e.printStackTrace();
		} catch (ParserConfigurationException e) { e.printStackTrace();
		} catch (IOException e) { e.printStackTrace(); }
		return null;
		}
		public static org.w3c.dom.Document getDOMDocumentFromURI(String uri) {
		try {
			if(factory == null) factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			org.w3c.dom.Document doc = factory.newDocumentBuilder().parse(uri);
			return doc;
		} catch (SAXException e) { e.printStackTrace();
		} catch (ParserConfigurationException e) { e.printStackTrace();
		} catch (IOException e) { e.printStackTrace(); }
		return null;
		}
		public static org.w3c.dom.Document getDOMDocumentFromURL(String urlPath, Authenticator auth) {
		try {
			URL url = new URL(urlPath);
			if(auth != null) Authenticator.setDefault(auth);
			if(factory == null) factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(false);
			org.w3c.dom.Document doc = factory.newDocumentBuilder().parse(url.openConnection().getInputStream());
			return doc;
		} catch (SAXException e) { e.printStackTrace();
		} catch (ParserConfigurationException e) { e.printStackTrace();
		} catch (IOException e) { e.printStackTrace(); }
		return null;
	}
	/*
	public static String document2String(org.w3c.dom.Document doc) {
	    try {
          OutputFormat format = new OutputFormat(doc);
          format.setLineWidth(65);
          format.setIndenting(true);
          format.setIndent(2);
          StringWriter writer = new StringWriter();
          com.sun.org.apache.xml.internal.serialize.XMLSerializer serializer = new com.sun.org.apache.xml.internal.serialize.XMLSerializer(writer, format);
          serializer.serialize(doc);
          return writer.getBuffer().toString();
	    } catch (IOException e) { e.printStackTrace(); }
	    return "";
	}
	*/
	public static void SAXParse(boolean validating, String file, DefaultHandler handler) throws Exception {
		if(saxfactory == null) {
           	saxfactory = SAXParserFactory.newInstance();
            saxfactory.setValidating(validating);
        }
        if(file.startsWith("<")) //must be xml doc
           	saxfactory.newSAXParser().parse(new InputSource(new StringReader(file)), handler);
        else if(file.startsWith("http://"))
           	saxfactory.newSAXParser().parse(file, handler);
        else //must be a filename
           	saxfactory.newSAXParser().parse(new File(file), handler);
	}
	
	public static void SAXParse(boolean validating, InputStream stream, DefaultHandler handler) {
		try {
            if(saxfactory == null) {
            	saxfactory = SAXParserFactory.newInstance();
                saxfactory.setValidating(validating);
            }
            Reader reader = new InputStreamReader(stream,"UTF-8");
            saxfactory.newSAXParser().parse(new InputSource(reader), handler);
        } catch (SAXException e1) { e1.printStackTrace();
        } catch (ParserConfigurationException e2) { e2.printStackTrace();
        } catch (IOException e3) { e3.printStackTrace(); }
	}
	
	public static void SAXParse(boolean validating, File file, DefaultHandler handler) {
		try {
            if(saxfactory == null) {
            	saxfactory = SAXParserFactory.newInstance();
                saxfactory.setValidating(validating);
            }
            saxfactory.newSAXParser().parse(file, handler);
        } catch (SAXException e1) { e1.printStackTrace();
        } catch (ParserConfigurationException e2) { e2.printStackTrace();
        } catch (IOException e3) { e3.printStackTrace(); }
	}
	public static String escape(String in) {
		if(in != null) {
			in = in.replace("<", "&lt;");
			in = in.replace("&", "&amp;");
			in = in.replace("'", "&apos;");
			in = in.replace("\"", "&quot;");
			//in = in.replaceAll("%", "&#37;");
			return in;
		}
		return "";
	}
	public static String unescape(String in) {
		if(in != null) {
			in = in.replace("&lt;", "<");
			in = in.replace("&amp;", "&");
			in = in.replace("&apos;", "'");
			in = in.replace("&quot;", "\"");
			//in = in.replaceAll("&#37;", "%");
			return in;
		} 
		return "";
	}
	public static String toXmlData(Object o) {
		if(o == null) return null;
		else return XMLUtility.escape(o.toString());
	}
	public static String toXmlCdata(Object o) {
		if(o == null) return "";
		else return o.toString();
	}
	public static String removeTags(String in) {
		if(in == null) return in;
		String noHTMLString = in.replaceAll("\\<.*?>","");
	    return noHTMLString;
	}

	public static String ISO9075encode(String toEncode) {
        if ((toEncode == null) || (toEncode.length() == 0))
        {
            return toEncode;
        }
        else if (XMLChar.isValidName(toEncode) && (toEncode.indexOf("_x") == -1) && (toEncode.indexOf(':') == -1))
        {
            return toEncode;
        }
        else
        {
            StringBuilder builder = new StringBuilder(toEncode.length());
            for (int i = 0; i < toEncode.length(); i++)
            {
                char c = toEncode.charAt(i);
                // First requires special test
                if (i == 0)
                {
                    if (XMLChar.isNCNameStart(c))
                    {
                        // The first character may be the _ at the start of an
                        // encoding pattern
                        if (matchesEncodedPattern(toEncode, i))
                        {
                            // Encode the first _
                        	ISO9075encode('_', builder);
                        }
                        else
                        {
                            // Just append
                            builder.append(c);
                        }
                    }
                    else
                    {
                        // Encode an invalid start character for an XML element
                        // name.
                    	ISO9075encode(c, builder);
                    }
                }
                else if (!XMLChar.isNCName(c))
                {
                	ISO9075encode(c, builder);
                }
                else
                {
                    if (matchesEncodedPattern(toEncode, i))
                    {
                        // '_' must be encoded
                    	ISO9075encode('_', builder);
                    }
                    else
                    {
                        builder.append(c);
                    }
                }
            }
            return builder.toString();
        }

    }
	/**
     * This method ensures that the output String has only
     * valid XML unicode characters as specified by the
     * XML 1.0 standard. For reference, please see
     * <a href=â€�http://www.w3.org/TR/2000/REC-xml-20001006#NT-Charâ€�>the
     * standard</a>. This method will return an empty
     * String if the input is null or empty.
     *
     * @param in The String whose non-valid characters we want to remove.
     * @return The in String, stripped of non-valid characters.
     */
    public static String stripNonValidXMLCharacters(String in) {
        StringBuffer out = new StringBuffer(); 
        char current; 
        if (in == null || ("".equals(in))) return ""; 
        for (int i = 0; i < in.length(); i++) {
            current = in.charAt(i); 
            if ((current == 0x9) ||
                (current == 0xA) ||
                (current == 0xD) ||
                ((current >= 0x20) && (current <= 0xD7FF)) ||
                ((current >= 0xE000) && (current <= 0xFFFD)) ||
                ((current >= 0x10000) && (current <= 0x10FFFF)))
                out.append(current);
        }
        return out.toString();
    }
    /**
	 * Returns unicode char given its hexadecimal representation as String for XML invalid characters
	 * If this hexadecimal string representation is not belonging to invalid XML character list, returns a single space character : ' '
	 * @param unicode
	 * @return
	 */
	public static char getXMLInvalidUnicodeChar(String unicodeHex){
		char result = ' ';
		//int code = Integer.parseInt(removeChar(unicodeHex,'x'), 16); 
		//char test = (char)code;
		if(InvalidUnicodeMap.containsKey(unicodeHex)){
			result = InvalidUnicodeMap.get(unicodeHex);
		}
		return result;
	}
	
	/**
	 * Returns unicode char given its hexadecimal representation as String
	 * @param unicodeHex
	 * @return matching unicode char
	 */
	public static char getUnicodeChar(String unicodeHex){
		char result = ' ';
		try{
			int code = Integer.parseInt(removeChar(unicodeHex,'x'), 16); 
			result = (char)code;
		}catch (Exception e) {
			
		}
		return result;
	}
	public static String removeChar(String s, char c) {
		String r = "";
		for (int i = 0; i < s.length(); i ++) {
			if (s.charAt(i) != c) r += s.charAt(i);
		}
		return r;
	}
	
	private static void ISO9075encode(char c, StringBuilder builder)
    {
        char[] buf = new char[] { '_', 'x', '0', '0', '0', '0', '_' };
        int charPos = 6;
        do
        {
            buf[--charPos] = DIGITS[c & MASK];
            c >>>= 4;
        }
        while (c != 0);
        builder.append(buf);
    }
	private static boolean matchesEncodedPattern(String string, int position)
    {
        return (string.length() >= position + 6)
                && (string.charAt(position) == '_') && (string.charAt(position + 1) == 'x')
                && isHexChar(string.charAt(position + 2)) && isHexChar(string.charAt(position + 3))
                && isHexChar(string.charAt(position + 4)) && isHexChar(string.charAt(position + 5))
                && (string.charAt(position + 6) == '_');
    }
	private static boolean isHexChar(char c)
    {
        switch (c)
        {
        case '0':
        case '1':
        case '2':
        case '3':
        case '4':
        case '5':
        case '6':
        case '7':
        case '8':
        case '9':
        case 'a':
        case 'b':
        case 'c':
        case 'd':
        case 'e':
        case 'f':
        case 'A':
        case 'B':
        case 'C':
        case 'D':
        case 'E':
        case 'F':
            return true;
        default:
            return false;
        }
    }
}
