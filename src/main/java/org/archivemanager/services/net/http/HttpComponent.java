package org.archivemanager.services.net.http;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface HttpComponent {

	HttpResponse get(String urlStr, Map<String,String> headers, String encoding) throws IOException;
	HttpResponse get(String urlStr, HttpProxy proxy, Map<String,String> headers, String encoding) throws IOException;
	
	HttpResponse post(String urlStr, byte[] data, Map<String,String> headers, String encoding) throws IOException;
	HttpResponse post(String urlStr, byte[] data, HttpProxy proxy, Map<String,String> headers, String encoding) throws IOException;
	
	HttpResponse download(String urlStr, DownloadListener listener) throws IOException;
	List<HttpResponse> download(List<String> urls, DownloadListener listener) throws IOException;
}
