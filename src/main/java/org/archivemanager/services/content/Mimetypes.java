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
package org.archivemanager.services.content;

public interface Mimetypes {
	public static final String PREFIX_TEXT = "text/";
    public static final String EXTENSION_BINARY = "bin";
    
    public static final String MIMETYPE_TEXT_PLAIN = "text/plain";
    public static final String MIMETYPE_TEXT_MEDIAWIKI = "text/mediawiki";
    public static final String MIMETYPE_TEXT_CSS = "text/css";    
    public static final String MIMETYPE_TEXT_CSV = "text/csv";
    public static final String MIMETYPE_TEXT_JAVASCRIPT = "text/javascript";    
    public static final String MIMETYPE_XML = "text/xml";
    public static final String MIMETYPE_HTML = "text/html";
    public static final String MIMETYPE_XHTML = "application/xhtml+xml";
    public static final String MIMETYPE_PDF = "application/pdf";
    public static final String MIMETYPE_JSON = "application/json";
    public static final String MIMETYPE_WORD = "application/msword";
    public static final String MIMETYPE_EXCEL = "application/vnd.ms-excel";
    public static final String MIMETYPE_BINARY = "application/octet-stream";
    public static final String MIMETYPE_PPT = "application/vnd.ms-powerpoint";
    public static final String MIMETYPE_APP_DWG = "application/dwg";
    public static final String MIMETYPE_IMG_DWG = "image/vnd.dwg";
    
    // Flash
    public static final String MIMETYPE_FLASH = "application/x-shockwave-flash";
    public static final String MIMETYPE_VIDEO_FLV = "video/x-flv";
    public static final String MIMETYPE_APPLICATION_FLA = "application/x-fla";

    public static final String MIMETYPE_IMAGE_GIF = "image/gif";
    public static final String MIMETYPE_IMAGE_JPEG = "image/jpeg";
    public static final String MIMETYPE_IMAGE_RGB = "image/x-rgb";
    public static final String MIMETYPE_IMAGE_SVG = "image/svg";
    public static final String MIMETYPE_IMAGE_PNG = "image/png";
    public static final String MIMETYPE_IMAGE_TIF = "image/tif";
    public static final String MIMETYPE_APPLICATION_EPS = "application/eps";
    public static final String MIMETYPE_JAVASCRIPT = "application/x-javascript";
    public static final String MIMETYPE_ZIP = "application/zip";
    public static final String MIMETYPE_OPENSEARCH_DESCRIPTION = "application/opensearchdescription+xml";
    public static final String MIMETYPE_ATOM = "application/atom+xml";
    public static final String MIMETYPE_RSS = "application/rss+xml";
    public static final String MIMETYPE_RFC822 = "message/rfc822";
    public static final String MIMETYPE_OUTLOOK_MSG = "application/vnd.ms-outlook";
    // Open Document
    public static final String MIMETYPE_OPENDOCUMENT_TEXT = "application/vnd.oasis.opendocument.text";
    public static final String MIMETYPE_OPENDOCUMENT_TEXT_TEMPLATE = "application/vnd.oasis.opendocument.text-template";
    public static final String MIMETYPE_OPENDOCUMENT_GRAPHICS = "application/vnd.oasis.opendocument.graphics";
    public static final String MIMETYPE_OPENDOCUMENT_GRAPHICS_TEMPLATE= "application/vnd.oasis.opendocument.graphics-template";
    public static final String MIMETYPE_OPENDOCUMENT_PRESENTATION= "application/vnd.oasis.opendocument.presentation";
    public static final String MIMETYPE_OPENDOCUMENT_PRESENTATION_TEMPLATE= "application/vnd.oasis.opendocument.presentation-template";
    public static final String MIMETYPE_OPENDOCUMENT_SPREADSHEET= "application/vnd.oasis.opendocument.spreadsheet";
    public static final String MIMETYPE_OPENDOCUMENT_SPREADSHEET_TEMPLATE= "application/vnd.oasis.opendocument.spreadsheet-template";
    public static final String MIMETYPE_OPENDOCUMENT_CHART= "application/vnd.oasis.opendocument.chart";
    public static final String MIMETYPE_OPENDOCUMENT_CHART_TEMPLATE= "applicationvnd.oasis.opendocument.chart-template";
    public static final String MIMETYPE_OPENDOCUMENT_IMAGE= "application/vnd.oasis.opendocument.image";
    public static final String MIMETYPE_OPENDOCUMENT_IMAGE_TEMPLATE= "applicationvnd.oasis.opendocument.image-template";
    public static final String MIMETYPE_OPENDOCUMENT_FORMULA= "application/vnd.oasis.opendocument.formula";
    public static final String MIMETYPE_OPENDOCUMENT_FORMULA_TEMPLATE= "applicationvnd.oasis.opendocument.formula-template";
    public static final String MIMETYPE_OPENDOCUMENT_TEXT_MASTER= "application/vnd.oasis.opendocument.text-master";
    public static final String MIMETYPE_OPENDOCUMENT_TEXT_WEB= "application/vnd.oasis.opendocument.text-web";
    public static final String MIMETYPE_OPENDOCUMENT_DATABASE= "application/vnd.oasis.opendocument.database";
    // Open Office
    public static final String MIMETYPE_OPENOFFICE1_WRITER = "application/vnd.sun.xml.writer";
    public static final String MIMETYPE_OPENOFFICE1_CALC = "application/vnd.sun.xml.calc";
    public static final String MIMETYPE_OPENOFFICE1_DRAW = "application/vnd.sun.xml.draw";
    public static final String MIMETYPE_OPENOFFICE1_IMPRESS = "application/vnd.sun.xml.impress";
    // Open XML
    public static final String MIMETYPE_OPENXML_WORDPROCESSING = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    public static final String MIMETYPE_OPENXML_SPREADSHEET = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    public static final String MIMETYPE_OPENXML_PRESENTATION = "application/vnd.openxmlformats-officedocument.presentationml.presentation";
    // Star Office
    public static final String MIMETYPE_STAROFFICE5_DRAW = "application/vnd.stardivision.draw";
    public static final String MIMETYPE_STAROFFICE5_CALC = "application/vnd.stardivision.calc";
    public static final String MIMETYPE_STAROFFICE5_IMPRESS = "application/vnd.stardivision.impress";
    public static final String MIMETYPE_STAROFFICE5_IMPRESS_PACKED = "application/vnd.stardivision.impress-packed";
    public static final String MIMETYPE_STAROFFICE5_CHART = "application/vnd.stardivision.chart";
    public static final String MIMETYPE_STAROFFICE5_WRITER = "application/vnd.stardivision.writer";
    public static final String MIMETYPE_STAROFFICE5_WRITER_GLOBAL = "application/vnd.stardivision.writer-global";
    public static final String MIMETYPE_STAROFFICE5_MATH = "application/vnd.stardivision.math";
    // WordPerfect
    public static final String MIMETYPE_WORDPERFECT = "application/wordperfect";
    // Audio
    public static final String MIMETYPE_MP4 = "audio/mp4";
    public static final String MIMETYPE_MP3 = "audio/mp3";
    public static final String MIMETYPE_MPEG = "audio/mpeg";
    public static final String MIMETYPE_WAV = "audio/x-wav";
    // Video
    public static final String MIMETYPE_AVI = "video/x-msvideo";
    public static final String MIMETYPE_MOV = "video/quicktime";
    public static final String MIMETYPE_VMPEG = "video/mpeg";
    public static final String MIMETYPE_WMV = "video/x-ms-wmv";
    public static final String MIMETYPE_FLV = "video/x-flv";
    public static final String MIMETYPE_STREAM = "application/octet-stream";
    public static final String MIMETYPE_VIDEO_MP4 = "video/mp4";
    
}
