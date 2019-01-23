package org.archivemanager.services.net.http;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.archivemanager.data.io.Base64;
import org.archivemanager.data.io.CharArrayBuffer;
import org.archivemanager.services.net.http.HttpProxy;
import org.archivemanager.services.net.http.HttpResponse;


public class RestUtility {
	private static final Logger log = Logger.getLogger(RestUtility.class.getName());	
	public static final int CONN_ERROR = -1;
    public static final int DISCONNECTED = 0;
    public static final int CONNECTING = 1;
    public static final int CONNECTED = 2;
	public static int connection_timeout = 5000000;
	
	static {
	    disableSslVerification();
	}
		
	public static HttpResponse httpGet(String urlStr, Map<String,String> headers) throws IOException {
		HttpResponse response = new HttpResponse();
		response.setUrl(urlStr);
		if(!urlStr.startsWith("http://") && !urlStr.startsWith("https://")) urlStr = "http://" + urlStr;
    	URL url = new URL(urlStr);
    	HttpURLConnection connection = (HttpURLConnection)url.openConnection();    	
    	connection.setConnectTimeout(connection_timeout);
    	//connection.setRequestProperty("Authorization", generateBasicHeader(SkilletApplication.getUsername(), SkilletApplication.getPassword(), false));
    	if(headers != null) {
		   	for(String key : headers.keySet()) {
		   		connection.setRequestProperty(key, headers.get(key));
		   	}
	    }			
		try {
			response.setStatusCode(connection.getResponseCode());
			response.setMessage(connection.getResponseMessage());
			if(connection.getResponseCode() == 301) {
				String location = connection.getHeaderField("Location");
				if(location != null)
					return httpGet(location, headers); 
			} else if(connection.getResponseCode() == 200) {
				//details.setErrorCount(0);
				StringBuffer buffer = new StringBuffer();
				InputStreamReader reader = new InputStreamReader(connection.getInputStream(), "UTF-8");
				int dataIn = reader.read();
				while(dataIn != -1){
				    char theChar = (char)dataIn;
				    buffer.append(theChar);
				    dataIn = reader.read();
				}
				reader.close();
				response.setContent(buffer.toString());				
			} else {
				log.info("http response:"+connection.getResponseCode()+" - "+urlStr);				
			}			
		} catch(ConnectException e) {
			log.info("No authorization to proceed");
			response.setMessage(e.getMessage());
			response.setStatusCode(503);
		}
		return response;
	}
	public static HttpResponse httpPost(String urlStr, Map<String,String> parameters, Map<String,String> headers) throws IOException {
		StringBuilder sb = new StringBuilder();
		for(HashMap.Entry<String, String> e : parameters.entrySet()){
			if(sb.length() > 0){
		          sb.append('&');
		    }
			if(e.getKey() != null && e.getValue() != null) {
				sb.append(URLEncoder.encode(e.getKey(), "UTF-8")).append('=').append(URLEncoder.encode(e.getValue(), "UTF-8"));
			}
		}
		return httpPost(urlStr, sb.toString().getBytes(StandardCharsets.UTF_8), headers);
	}
	public static HttpResponse httpPost(String urlStr, byte[] data, Map<String,String> headers) throws IOException {
		HttpResponse response = new HttpResponse();
		if(!urlStr.startsWith("http://") && !urlStr.startsWith("https://")) urlStr = "http://" + urlStr;
    	if(data == null) data = new byte[0];    	
    	URL url = new URL(urlStr);
    	HttpURLConnection connection = (HttpURLConnection)url.openConnection();
    	connection.setConnectTimeout(connection_timeout);
    	if(headers != null) {
		   	for(String key : headers.keySet()) {
		   		connection.setRequestProperty(key, headers.get(key));
		   	}
	    }
	    try {		    
		    connection.setRequestMethod("POST");
			if(connection.getRequestProperty("Content-Type") == null)
				connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");			
			connection.setRequestProperty("Content-Length", "" + Integer.toString(data.length));
			connection.setRequestProperty("Content-Language", "en-US");				
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setDoOutput(true);
			//Send request
			DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
			wr.write(data);
			wr.flush();
			wr.close();
			int responseCode = connection.getResponseCode();
			if(responseCode == 200) {							
			    //Get Response
				StringBuffer buffer = new StringBuffer();
				InputStreamReader reader = new InputStreamReader(connection.getInputStream(), "UTF-8");
				int dataIn = reader.read();
				while(dataIn != -1){
				    char theChar = (char)dataIn;
				    buffer.append(theChar);
				    dataIn = reader.read();
				}
				reader.close();
				response.setContent(buffer.toString());				
			} else if(connection.getResponseCode() == 403) {
				
			}
			response.setStatusCode(connection.getResponseCode());
			response.setMessage(connection.getResponseMessage());
			Map<String, List<String>> map = connection.getHeaderFields();
			for(Map.Entry<String, List<String>> entry : map.entrySet()) {
				response.getHeaders().put(entry.getKey(), entry.getValue());
			}
	    } catch(ConnectException e) {
	    	response.setStatusCode(500);
			response.setMessage("connection unavailable");
	    } finally {
	    	if(connection != null) {
		   		connection.disconnect(); 
		    }
	    }
	   	return response;
	}
	public static HttpResponse httpGet(String urlStr, HttpProxy proxy, Map<String,String> headers) throws IOException {
		HttpResponse response = new HttpResponse();
		if(!urlStr.startsWith("http://") && !urlStr.startsWith("https://")) urlStr += "http://";
    	URL url = new URL(urlStr);
    	Proxy p = proxy.getType().equals("HTTP") ? 
    			new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy.getHost(), proxy.getPort())) :
    			new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxy.getHost(), proxy.getPort()));
    	HttpURLConnection connection = (HttpURLConnection)url.openConnection(p); 
    	connection.setConnectTimeout(connection_timeout);
    	if(headers != null) {
		   	for(String key : headers.keySet()) {
		   		connection.setRequestProperty(key, headers.get(key));
		   	}
    	} else if(connection.getResponseCode() == 200) {
	    	//details.setErrorCount(0);
			StringBuffer buffer = new StringBuffer();
			InputStreamReader reader = new InputStreamReader(connection.getInputStream(), "UTF-8");
			int dataIn = reader.read();
			while(dataIn != -1){
			    char theChar = (char)dataIn;
			    buffer.append(theChar);
			    dataIn = reader.read();
			}
			reader.close();
			response.setContent(buffer.toString());
			if(connection instanceof HttpURLConnection) {
		       	HttpURLConnection httpConnection = (HttpURLConnection) connection;
		       	int code = httpConnection.getResponseCode();
		       	response.setStatusCode(code);
		    } else if(connection instanceof HttpsURLConnection) {
		       	HttpsURLConnection httpConnection = (HttpsURLConnection) connection;
		       	int code = httpConnection.getResponseCode();
		       	response.setStatusCode(code);
		    }
	    }
		return response;
	}
	
	public static HttpResponse multipartUpload(String requestURL, String fileName, String content, Map<String,String> fields, String charset) throws IOException {
		return multipartUpload(requestURL, fileName, content.getBytes(charset), fields, charset);
	}
	public static HttpResponse multipartUpload(String requestURL, String fileName, byte[] content, Map<String,String> fields, String charset) throws IOException {
		HttpResponse response = new HttpResponse();
		// creates a unique boundary based on time stamp
        String boundary = "===" + System.currentTimeMillis() + "===";         
        URL url = new URL(requestURL);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setConnectTimeout(connection_timeout);
        connection.setUseCaches(false);
	    connection.setDoOutput(true); // indicates POST method
	    connection.setDoInput(true);
	    connection.setRequestProperty("Content-Type", "multipart/form-data; charset=utf-8; boundary=" + boundary);
	    OutputStream outputStream = connection.getOutputStream();
	    PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);
	    //form fields
	    if(fields != null) {
		    for(String fieldName : fields.keySet()) {
		    	writer.append("--" + boundary).append("\r\n");
		        writer.append("Content-Disposition: form-data; name=\"" + fieldName + "\"").append("\r\n");
		        writer.append("Content-Type: text/plain; charset=" + charset).append("\r\n");
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
	        outputStream.write(content);
	        outputStream.flush();       
	        writer.append("\r\n");
	        writer.flush();
	        //finish
	        writer.append("\r\n").flush();
	        writer.append("--" + boundary + "--").append("\r\n");
	        writer.close(); 
	        
	        //Get Response
	        response.setStatusCode(connection.getResponseCode());
			if(connection.getResponseCode() == 403) {
				response.setMessage(connection.getResponseMessage());
				/*
				int errorCount = details.getErrorCount();
				long backoff = details.getIssued_at() + (errorCount * 1000 * 15);
				if(System.currentTimeMillis() > backoff) {
					details = getAuthenticationDetails(true);
					details.setErrorCount(errorCount + 1);
					return multipartUpload(requestURL, fileName, file, fields, charset);
				}
				*/
			} else if(connection.getResponseCode() == 200) {
				//details.setErrorCount(0);				
		        StringBuffer buffer = new StringBuffer();
				InputStreamReader reader = new InputStreamReader(connection.getInputStream(), "UTF-8");
				int dataIn = reader.read();
				while(dataIn != -1){
				    char theChar = (char)dataIn;
				    buffer.append(theChar);
				    dataIn = reader.read();
				}
				reader.close();
				response.setContent(buffer.toString());
		        connection.disconnect();
			}
    	}
        return response;
	}
	
	protected String generateBasicHeader(String username, String password, boolean proxy) {
		StringBuilder tmp = new StringBuilder();
        tmp.append(username);
        tmp.append(":");
        tmp.append((password == null) ? "null" : password);

        byte[] base64password = Base64.encode(tmp.toString().getBytes());

        CharArrayBuffer buffer = new CharArrayBuffer(32);
        /*
        if (proxy) {
            buffer.append(PROXY_AUTH_RESP);
        } else {
            buffer.append(WWW_AUTH_RESP);
        }
        */
        buffer.append("Basic ");
        buffer.append(base64password, 0, base64password.length);
        return buffer.toString();
	}
	
	private static void disableSslVerification() {
	    try {
	        // Create a trust manager that does not validate certificate chains
	        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
	            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	                return null;
	            }
	            @Override
				public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
					
				}
				@Override
				public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
					
				}
	        }};
	        // Install the all-trusting trust manager
	        SSLContext sc = SSLContext.getInstance("SSL");
	        sc.init(null, trustAllCerts, new java.security.SecureRandom());
	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

	        // Create all-trusting host name verifier
	        HostnameVerifier allHostsValid = new HostnameVerifier() {
	            public boolean verify(String hostname, SSLSession session) {
	                return true;
	            }
	        };
	        // Install the all-trusting host verifier
	        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
	    } catch (NoSuchAlgorithmException e) {
	        e.printStackTrace();
	    } catch (KeyManagementException e) {
	        e.printStackTrace();
	    }
	}
}
