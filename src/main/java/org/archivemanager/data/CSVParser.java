package org.archivemanager.data;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.archivemanager.services.net.http.HttpComponent;
import org.archivemanager.services.net.http.HttpResponse;
import org.archivemanager.services.net.http.URLConnectionComponent;


public class CSVParser {
	private HttpComponent http = new URLConnectionComponent();
	private List<String[]> data = new ArrayList<String[]>();
	
	
	public void process(String url) throws Exception {
		data.clear();
		HttpResponse response = null;
		try {
			HashMap<String,String> headers = new HashMap<String,String>();
			response = http.get(url, headers, null);
		} catch(Exception e) {
			System.out.println("problem fetching : "+url);
		}
		if(response != null && response.getContent() != null) {
			BufferedReader br = null;
			String line = "";
			String cvsSplitBy = ",";	 
			try {	 
				br = new BufferedReader(new StringReader(new String(response.getContent())));
				while ((line = br.readLine()) != null) {
					data.add(line.split(cvsSplitBy));				}	 
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}		
	}
	public void process(InputStream stream) throws Exception {
		data.clear();
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ",";	 
		try {	 
			br = new BufferedReader(new InputStreamReader(stream));
			while ((line = br.readLine()) != null) {
				data.add(line.split(cvsSplitBy));	
			}	 
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public List<String[]> getData() {
		return data;
	}
	public void setData(List<String[]> data) {
		this.data = data;
	}
	
}
