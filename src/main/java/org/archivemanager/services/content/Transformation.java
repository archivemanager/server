package org.archivemanager.services.content;



public class Transformation {
	private String name;
	private String mimetype;
	private int width;
	private int height;
	private boolean base64Encode = false;;
	
	
	public Transformation() {}
	public Transformation(String name, String mimetype, int width, int height) {
		this.name = name;
		this.mimetype = mimetype;
		this.width = width;
		this.height = height;
	}
	
	public String getFileName() {
		//return name+"_"+width+"_"+height+getExtension();
		return name;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMimetype() {
		return mimetype;
	}
	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	public String getExtension() {
		if(mimetype.equals(Mimetypes.MIMETYPE_IMAGE_GIF)) return ".gif";
		else if(mimetype.equals(Mimetypes.MIMETYPE_IMAGE_JPEG)) return ".jpg";
		else if(mimetype.equals(Mimetypes.MIMETYPE_PDF)) return ".pdf";
		else if(mimetype.equals(Mimetypes.MIMETYPE_IMAGE_TIF)) return ".tif";
		else if(mimetype.equals(Mimetypes.MIMETYPE_MP3)) return ".mp3";
		else if(mimetype.equals(Mimetypes.MIMETYPE_FLV)) return ".flv";
		else if(mimetype.equals(Mimetypes.MIMETYPE_IMAGE_PNG)) return ".png";
		return "";
	}
	public boolean isBase64Encode() {
		return base64Encode;
	}
	public void setBase64Encode(boolean base64Encode) {
		this.base64Encode = base64Encode;
	}
	
}
