package org.archivemanager.services.net.http;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.X509TrustManager;

import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;


public class URLConnectionComponent implements HttpComponent {
	private HostnameVerifier allHostsValid;
	
	
	public URLConnectionComponent() {
		// Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
        	public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }
            public void checkClientTrusted(X509Certificate[] certs, String authType) {
            }
            public void checkServerTrusted(X509Certificate[] certs, String authType) {
            }
        } };
        try {
	        // Install the all-trusting trust manager
	        final SSLContext sc = SSLContext.getInstance("SSL");
	        sc.init(null, trustAllCerts, new java.security.SecureRandom());
	        SSLSocketFactory socketFactory = new SSLSocketFactory(sc);
	        HttpsURLConnection.setDefaultSSLSocketFactory(socketFactory);
	        // Create all-trusting host name verifier
	        allHostsValid = new HostnameVerifier() {
	            public boolean verify(String hostname, SSLSession session) {
	                return true;
	            }
	        };
	        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch(Exception e) {
        	e.printStackTrace();
        }
	}
	public HttpResponse download(String urlStr, DownloadListener listener) throws IOException {
		HttpResponse response = new HttpResponse();
		URL url = new URL(urlStr);
    	URLConnection connection = url.openConnection();
    	ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    	int total = 0;
    	int contentLength = connection.getContentLength();
		int nRead;
		byte[] data = new byte[16384];
		while ((nRead = connection.getInputStream().read(data, 0, data.length)) != -1) {
		  buffer.write(data, 0, nRead);
		  total += nRead;
		  listener.update(total, contentLength);
		}
		buffer.flush();
		response.setContentBytes(buffer.toByteArray());
		listener.update(contentLength, contentLength);
		return response;
	}
	public List<HttpResponse> download(List<String> urls, DownloadListener listener) throws IOException {
		List<HttpResponse> responses = new ArrayList<HttpResponse>();
		int totalRead = 0;
		int totalToRead = 0;
		List<URLConnection> connections = new ArrayList<URLConnection>();
		for(String urlStr : urls) {
			URL url = new URL(urlStr);
			URLConnection connection = url.openConnection();
			totalToRead += connection.getContentLength();
			connections.add(connection);
		}
		for(URLConnection connection : connections) {
			HttpResponse response = new HttpResponse();
	    	ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	    	int nRead;
			byte[] data = new byte[16384];
			while ((nRead = connection.getInputStream().read(data, 0, data.length)) != -1) {
			  buffer.write(data, 0, nRead);
			  totalRead += nRead;
			  listener.update(totalRead, totalToRead);
			}
			buffer.flush();
			response.setUrl(connection.getURL().toString());
			response.setContentBytes(buffer.toByteArray());
			responses.add(response);
		}
		listener.update(totalToRead, totalToRead);
		return responses;
	}
	public HttpResponse get(String urlStr, Map<String,String> headers, String encoding) throws IOException {
		//if(!urlStr.startsWith("http://") && !urlStr.startsWith("https://")) urlStr = "http://" + urlStr;
    	URL url = new URL(urlStr);
    	URLConnection connection = url.openConnection();
    	if(headers != null) {
	    	for(String key : headers.keySet()) {
	    		connection.setRequestProperty(key, headers.get(key));
	    	}
    	}
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[16384];
		while ((nRead = connection.getInputStream().read(data, 0, data.length)) != -1) {
		  buffer.write(data, 0, nRead);
		}
		buffer.flush();
		HttpResponse response = new HttpResponse();
		if(encoding != null) {
			String content = buffer.toString(encoding);
			response.setContent(content);
		} else {
			response.setContentBytes(buffer.toByteArray());
		}				
		if(connection instanceof HttpURLConnection) {
        	HttpURLConnection httpConnection = (HttpURLConnection) connection;
        	int code = httpConnection.getResponseCode();
        	if(code == 301 || code == 302) { //redirection
        		String location = httpConnection.getHeaderField("Location");
        		return get(location, headers, encoding);
        	}
        	response.setStatusCode(code);
        } else if(connection instanceof HttpsURLConnection) {
        	

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

        	HttpsURLConnection httpConnection = (HttpsURLConnection) connection;
        	int code = httpConnection.getResponseCode();
        	response.setStatusCode(code);
        }
		return response;
	}
	
	public HttpResponse get(String urlStr, HttpProxy proxy, Map<String,String> headers, String encoding) throws IOException {		
    	if(!urlStr.startsWith("http://") && !urlStr.startsWith("https://")) urlStr += "http://";
    	URL url = new URL(urlStr);
    	Proxy p = proxy.getType().equals("HTTP") ? 
    			new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy.getHost(), proxy.getPort())) :
    			new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxy.getHost(), proxy.getPort()));
    	URLConnection connection = url.openConnection(p);
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[16384];
		while ((nRead = connection.getInputStream().read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}
		buffer.flush();
		HttpResponse response = new HttpResponse();
		response.setUrl(urlStr);
		if(encoding != null) {
			String content = buffer.toString(encoding);
			response.setContent(content);
		} else {
			response.setContentBytes(buffer.toByteArray());
		}
		if(connection instanceof HttpURLConnection) {
        	HttpURLConnection httpConnection = (HttpURLConnection) connection;
        	int code = httpConnection.getResponseCode();
        	if(code == 301 || code == 302) { //redirection
        		String location = httpConnection.getHeaderField("Location");
        		return get(location, proxy, headers, encoding);
        	}
        	response.setStatusCode(code);
        } else if(connection instanceof HttpsURLConnection) {
        	HttpsURLConnection httpConnection = (HttpsURLConnection) connection;
        	int code = httpConnection.getResponseCode();
        	if(code == 301 || code == 302) { //redirection
        		String location = httpConnection.getHeaderField("Location");
        		return get(location, proxy, headers, encoding);
        	}
        	response.setStatusCode(code);
        }
		return response;
	}
	public HttpResponse post(String urlStr, byte[] data, Map<String,String> headers, String encoding) throws IOException {		
    	if(!urlStr.startsWith("http://") && !urlStr.startsWith("https://")) urlStr = "http://" + urlStr;
    	URL url = new URL(urlStr);
    	HttpURLConnection connection = (HttpURLConnection)url.openConnection();
    	connection.setDoOutput(true);
    	connection.setRequestMethod("POST");
    	if(headers != null) {
	    	for(String key : headers.keySet()) {
	    		connection.setRequestProperty(key, headers.get(key));
	    	}
    	}
    	if(data != null) connection.getOutputStream().write(data);
    	
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] responseData = new byte[16384];
		while ((nRead = connection.getInputStream().read(responseData, 0, responseData.length)) != -1) {
		  buffer.write(responseData, 0, nRead);
		}
		buffer.flush();
		HttpResponse response = new HttpResponse();
		response.setUrl(urlStr);
		if(encoding != null) {
			String content = buffer.toString(encoding);
			response.setContent(content);
		} else {
			response.setContentBytes(buffer.toByteArray());
		}
    	if(connection instanceof HttpURLConnection) {
        	HttpURLConnection httpConnection = (HttpURLConnection) connection;
        	int code = httpConnection.getResponseCode();
        	if(code == 301 || code == 302) { //redirection
        		String location = httpConnection.getHeaderField("Location");
        		return post(location, data, headers, encoding);
        	}
        	response.setStatusCode(code);
        } else if(connection instanceof HttpsURLConnection) {
            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

        	HttpsURLConnection httpConnection = (HttpsURLConnection) connection;
        	int code = httpConnection.getResponseCode();
        	if(code == 301 || code == 302) { //redirection
        		String location = httpConnection.getHeaderField("Location");
        		return post(location, data, headers, encoding);
        	}
        	response.setStatusCode(code);
        }
		return response;
	}
	
	public HttpResponse post(String urlStr, byte[] data, HttpProxy proxy, Map<String,String> headers, String encoding) throws IOException {		
    	if(!urlStr.startsWith("http://") && !urlStr.startsWith("https://")) urlStr += "http://";
    	URL url = new URL(urlStr);
    	Proxy p = proxy.getType().equals("HTTP") ? 
    			new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy.getHost(), proxy.getPort())) :
    			new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxy.getHost(), proxy.getPort()));
    	HttpURLConnection connection = (HttpURLConnection)url.openConnection(p);
    	connection.setDoOutput(true);
    	connection.setRequestMethod("POST");
    	if(headers != null) {
	    	for(String key : headers.keySet()) {
	    		connection.setRequestProperty(key, headers.get(key));
	    	}
    	}
    	connection.getOutputStream().write(data);    	
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] responseData = new byte[16384];
		while ((nRead = connection.getInputStream().read(responseData, 0, responseData.length)) != -1) {
		  buffer.write(responseData, 0, nRead);
		}
		buffer.flush();
		HttpResponse response = new HttpResponse();
		response.setUrl(urlStr);
		if(encoding != null) {
			String content = buffer.toString(encoding);
			response.setContent(content);
		} else {
			response.setContentBytes(buffer.toByteArray());
		}
		if(connection instanceof HttpURLConnection) {
        	HttpURLConnection httpConnection = (HttpURLConnection) connection;
        	int code = httpConnection.getResponseCode();
        	if(code == 301 || code == 302) { //redirection
        		String location = httpConnection.getHeaderField("Location");
        		return post(location, data, proxy, headers, encoding);
        	}
        	response.setStatusCode(code);
        } else if(connection instanceof HttpsURLConnection) {
        	HttpsURLConnection httpConnection = (HttpsURLConnection) connection;
        	int code = httpConnection.getResponseCode();
        	if(code == 301 || code == 302) { //redirection
        		String location = httpConnection.getHeaderField("Location");
        		return post(location, data, proxy, headers, encoding);
        	}
        	response.setStatusCode(code);
        }
		return response;
	}
	public HttpResponse multipartUpload(String requestURL, String fileName, byte[] file, Map<String,String> fields, String encoding) throws IOException {
		// creates a unique boundary based on time stamp
        String boundary = "===" + System.currentTimeMillis() + "===";         
        URL url = new URL(requestURL);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setUseCaches(false);
        connection.setDoOutput(true); // indicates POST method
        connection.setDoInput(true);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        OutputStream outputStream = connection.getOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, encoding), true);
        //form fields
        for(String fieldName : fields.keySet()) {
        	 writer.append("--" + boundary).append("\r\n");
             writer.append("Content-Disposition: form-data; name=\"" + fieldName + "\"").append("\r\n");
             writer.append("Content-Type: text/plain; charset=" + encoding).append("\r\n");
             writer.append("\r\n");
             writer.append(fields.get(fieldName)).append("\r\n");
             writer.flush();
        }
        //file
        writer.append("--" + boundary).append("\r\n");
        writer.append("Content-Disposition: form-data; name=\"uploadFile\"; filename=\"" + fileName + "\"").append("\r\n");
        writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append("\r\n");
        writer.append("Content-Transfer-Encoding: binary").append("\r\n");
        writer.append("\r\n");
        writer.flush(); 
        outputStream.write(file);
        outputStream.flush();       
        writer.append("\r\n");
        writer.flush();
        //finish
        writer.append("\r\n").flush();
        writer.append("--" + boundary + "--").append("\r\n");
        writer.close(); 
        
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] responseData = new byte[16384];
		while ((nRead = connection.getInputStream().read(responseData, 0, responseData.length)) != -1) {
		  buffer.write(responseData, 0, nRead);
		}
		buffer.flush();
		HttpResponse response = new HttpResponse();
		response.setUrl(requestURL);
		if(encoding != null) {
			String content = buffer.toString(encoding);
			response.setContent(content);
		} else {
			response.setContentBytes(buffer.toByteArray());
		}		
        // checks server's status code first
        int status = connection.getResponseCode();
        response.setStatusCode(status);
        if (status == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = null;
            while((line = reader.readLine()) != null) {
                if(response.getMessage() != null) response.setMessage(response.getMessage()+" "+line);
                else response.setMessage(line);
            }
            reader.close();
            connection.disconnect();
        } else {
            throw new IOException("Server returned non-OK status: " + status);
        }
        return response;
	}
}
