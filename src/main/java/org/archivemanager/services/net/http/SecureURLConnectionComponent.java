package org.archivemanager.services.net.http;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Authenticator;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.X509TrustManager;

import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;


public class SecureURLConnectionComponent implements HttpComponent {
	private HostnameVerifier allHostsValid;
	private String username = "user";
	private String password = "user";
	private Map<String,String> headers = new HashMap<String,String>();
	
	
	public SecureURLConnectionComponent() {
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
	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
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
	public HttpResponse get(String urlStr, Map<String,String> headers, String encoding) throws IOException {
		HttpResponse response = new HttpResponse();
		Authenticator.setDefault(new Authenticator() {
		  protected PasswordAuthentication getPasswordAuthentication() {
				PasswordAuthentication pa = new PasswordAuthentication(username, password.toCharArray());
				return pa;
			}
		});
		if(!urlStr.startsWith("http://") && !urlStr.startsWith("https://")) urlStr = "http://" + urlStr;
    	URL url = new URL(urlStr);
    	HttpURLConnection connection = (HttpURLConnection)url.openConnection();    	
    	if(headers != null) {
	    	for(String key : headers.keySet()) {
	    		connection.setRequestProperty(key, headers.get(key));
	    	}
    	}
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		int nRead;
		byte[] data = new byte[16384];
		try {
			if(connection.getResponseCode() == 200) {
				while ((nRead = connection.getInputStream().read(data, 0, data.length)) != -1) {
				  buffer.write(data, 0, nRead);
				}
				buffer.flush();
				response.setContentBytes(buffer.toByteArray());
				if(connection instanceof HttpURLConnection) {
		        	HttpURLConnection httpConnection = (HttpURLConnection) connection;
		        	int code = httpConnection.getResponseCode();
		        	response.setStatusCode(code);
		        } else if(connection instanceof HttpsURLConnection) {
		            // Install the all-trusting host verifier
		            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		        	HttpsURLConnection httpConnection = (HttpsURLConnection) connection;
		        	int code = httpConnection.getResponseCode();
		        	response.setStatusCode(code);
		        }
			}
		} catch(ConnectException e) {
			System.out.println("unable to connect");
			response.setMessage(e.getMessage());
			response.setStatusCode(503);
		}		
		return response;
	}
	
	public HttpResponse get(String urlStr, HttpProxy proxy, Map<String,String> headers, String encoding) throws IOException {
		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				PasswordAuthentication pa = new PasswordAuthentication(username, password.toCharArray());
				return pa;
			}
		});
    	if(!urlStr.startsWith("http://") && !urlStr.startsWith("https://")) urlStr += "http://";
    	URL url = new URL(urlStr);
    	Proxy p = proxy.getType().equals("HTTP") ? 
    			new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy.getHost(), proxy.getPort())) :
    			new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxy.getHost(), proxy.getPort()));
    	URLConnection connection = url.openConnection(p);    	
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
		HttpResponse response = new HttpResponse(buffer.toString());
		if(connection instanceof HttpURLConnection) {
        	HttpURLConnection httpConnection = (HttpURLConnection) connection;
        	int code = httpConnection.getResponseCode();
        	response.setStatusCode(code);
        } else if(connection instanceof HttpsURLConnection) {
        	HttpsURLConnection httpConnection = (HttpsURLConnection) connection;
        	int code = httpConnection.getResponseCode();
        	response.setStatusCode(code);
        }
		return response;
	}
	public static String excutePost(String targetURL, String urlParameters) {
	    URL url;
	    HttpURLConnection connection = null;  
	    try {
	      //Create connection
	      url = new URL(targetURL);
	      connection = (HttpURLConnection)url.openConnection();
	      connection.setRequestMethod("POST");
	      connection.setRequestProperty("Content-Type", "application/json");				
	      connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
	      connection.setRequestProperty("Content-Language", "en-US");				
	      connection.setUseCaches(false);
	      connection.setDoInput(true);
	      connection.setDoOutput(true);
	      //Send request
	      DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
	      wr.writeBytes(urlParameters);
	      wr.flush();
	      wr.close();
	      //Get Response	
	      InputStream is = connection.getInputStream();
	      BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	      String line;
	      StringBuffer response = new StringBuffer(); 
	      while((line = rd.readLine()) != null) {
	        response.append(line);
	        response.append('\r');
	      }
	      rd.close();
	      return response.toString();
	    } catch (Exception e) {

	      e.printStackTrace();
	      return null;

	    } finally {

	      if(connection != null) {
	        connection.disconnect(); 
	      }
	    }
	  }
	public HttpResponse post(String urlStr, byte[] data, Map<String,String> headers, String encoding) throws IOException {
		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				PasswordAuthentication pa = new PasswordAuthentication (username, password.toCharArray());
				return pa;
			}
		});
    	if(!urlStr.startsWith("http://") && !urlStr.startsWith("https://")) urlStr = "http://" + urlStr;
    	if(data == null) data = new byte[0];
    	
    	URL url = new URL(urlStr);
    	HttpURLConnection connection = (HttpURLConnection)url.openConnection();
    	if(headers != null) {
	    	for(String key : headers.keySet()) {
	    		connection.setRequestProperty(key, headers.get(key));
	    	}
    	}
    	connection = (HttpURLConnection)url.openConnection();
	    connection.setRequestMethod("POST");
	    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");			
	    connection.setRequestProperty("Content-Length", "" + Integer.toString(data.length));
	    connection.setRequestProperty("Content-Language", "en-US");				
	    connection.setUseCaches(false);
	    connection.setDoInput(true);
	    connection.setDoOutput(true);
	      //Send request
	    DataOutputStream wr = new DataOutputStream(connection.getOutputStream ());
		wr.write(data);
		wr.flush ();
		wr.close ();
		
	    //Get Response	
	    InputStream is = connection.getInputStream();
	    BufferedReader rd = new BufferedReader(new InputStreamReader(is));
	    String line;
	    StringBuffer responseBuffer = new StringBuffer(); 
	    while((line = rd.readLine()) != null) {
	    	responseBuffer.append(line);
	    	responseBuffer.append('\r');
	    }
	    rd.close();
    	
		HttpResponse response = new HttpResponse(responseBuffer.toString());
    	if(connection instanceof HttpURLConnection) {
        	HttpURLConnection httpConnection = (HttpURLConnection) connection;
        	int code = httpConnection.getResponseCode();
        	response.setStatusCode(code);
        } else if(connection instanceof HttpsURLConnection) {
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        	HttpsURLConnection httpConnection = (HttpsURLConnection) connection;
        	int code = httpConnection.getResponseCode();
        	response.setStatusCode(code);
        }
    	if(connection != null) {
            connection.disconnect(); 
        }
		return response;
	}
	
	public HttpResponse post(String urlStr, byte[] data, HttpProxy proxy, Map<String,String> headers, String encoding) throws IOException {		
		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				PasswordAuthentication pa = new PasswordAuthentication(username, password.toCharArray());
				return pa;
			}
		});
		if(!urlStr.startsWith("http://") && !urlStr.startsWith("https://")) urlStr += "http://";
    	URL url = new URL(urlStr);
    	Proxy p = proxy.getType().equals("HTTP") ? 
    			new Proxy(Proxy.Type.HTTP, new InetSocketAddress(proxy.getHost(), proxy.getPort())) :
    			new Proxy(Proxy.Type.SOCKS, new InetSocketAddress(proxy.getHost(), proxy.getPort()));
    	HttpURLConnection connection = (HttpURLConnection)url.openConnection(p);
    	connection.setDoOutput(true);
    	connection.setRequestMethod("POST");
    	headers.putAll(this.headers);
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
		HttpResponse response = new HttpResponse(buffer.toString());
		if(connection instanceof HttpURLConnection) {
        	HttpURLConnection httpConnection = (HttpURLConnection) connection;
        	int code = httpConnection.getResponseCode();
        	response.setStatusCode(code);
        } else if(connection instanceof HttpsURLConnection) {
        	HttpsURLConnection httpConnection = (HttpsURLConnection) connection;
        	int code = httpConnection.getResponseCode();
        	response.setStatusCode(code);
        }
		return response;
	}
	public HttpResponse multipartUpload(String requestURL, String fileName, byte[] file, Map<String,String> fields, String charset) throws IOException {
		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				PasswordAuthentication pa = new PasswordAuthentication(username, password.toCharArray());
				return pa;
			}
		});
		// creates a unique boundary based on time stamp
        String boundary = "===" + System.currentTimeMillis() + "===";         
        URL url = new URL(requestURL);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        connection.setUseCaches(false);
        connection.setDoOutput(true); // indicates POST method
        connection.setDoInput(true);
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        OutputStream outputStream = connection.getOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, charset), true);
        //form fields
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
		HttpResponse response = new HttpResponse(buffer.toString());
		
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
	
	public void setUsername(String username) {
		this.username = username;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Map<String, String> getHeaders() {
		return headers;
	}
	public void setHeaders(Map<String, String> headers) {
		this.headers = headers;
	}
	/*
	static class MyCache implements sun.net.www.protocol.http.AuthCache{
	     public void put(String pkey, sun.net.www.protocol.http.AuthCacheValue value){

	     }
	     public sun.net.www.protocol.http.AuthCacheValue get(String pkey, String skey){
	         return null;
	     }
	     public void remove(String pkey, sun.net.www.protocol.http.AuthCacheValue entry){

	     }
	}
	static{
	    sun.net.www.protocol.http.AuthCacheValue.setAuthCache(new MyCache());
	}
	*/
	@Override
	public HttpResponse download(String urlStr, DownloadListener listener) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<HttpResponse> download(List<String> urls, DownloadListener listener) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
}
