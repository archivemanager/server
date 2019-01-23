package org.archivemanager.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DataSet {
	private Map<String,Object> data = new HashMap<String,Object>();

	
	public void put(String key, Object value) {
		data.put(key, value);
	}
	public Object get(String key) {
		return data.get(key);
	}
	public Integer getInt(String key) {
		Object obj = data.get(key);
		if(obj != null && obj instanceof Integer)
			return (Integer)obj;
		return 0;
	}
	public Set<String> getKeys() {
		return data.keySet();
	}
	public boolean contains(String key) {
		return data.containsKey(key);
	}
	public Map<String, Object> getData() {
		return data;
	}
	public void setData(Map<String, Object> data) {
		this.data = data;
	}

}
