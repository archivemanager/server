package org.archivemanager.services.search;
import java.util.HashMap;
import java.util.Map;

import org.archivemanager.models.system.QName;


public class SearchResult {
	private Long id;
	private Long source;
	private Long target;
	private Long user;
	private Long created;
	private Long modified;
	private QName qname;
	private String name;
	private float rawScore;
	private int normalizedScore;
	private String classification;
	private Map<String,Object> data = new HashMap<String,Object>();
	
	
	public SearchResult() {}
	public SearchResult(Long id) {
		this.id = id;
	}
	public SearchResult(Long id, QName qname) {
		this.id = id;
		this.qname = qname;
	}
	
	public String getPropertyValueString(QName qname) {
		if(data.containsKey(qname.toString()))
			return (String)data.get(qname.toString());
		return null;
	}
	public Boolean getPropertyValueBoolean(QName qname) {
		if(data.containsKey(qname.toString()))
			return (Boolean)data.get(qname.toString());
		return null;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}	
	public Long getSource() {
		return source;
	}
	public void setSource(Long source) {
		this.source = source;
	}
	public Long getTarget() {
		return target;
	}
	public void setTarget(Long target) {
		this.target = target;
	}
	public Long getUser() {
		return user;
	}
	public void setUser(Long user) {
		this.user = user;
	}	
	public Long getCreated() {
		return created;
	}
	public void setCreated(Long created) {
		this.created = created;
	}
	public Long getModified() {
		return modified;
	}
	public void setModified(Long modified) {
		this.modified = modified;
	}
	public QName getQName() {
		return qname;
	}
	public void setQName(QName qname) {
		this.qname = qname;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public void setData(Map<String, Object> data) {
		this.data = data;
	}
	public Object get(QName key) {
		return data.get(key.toString());
	}
	public Object get(String key) {
		return data.get(key);
	}
	public boolean hasProperty(QName qname) {
		return data.containsKey(qname.toString());
	}
	public boolean hasProperty(String key) {
		return data.containsKey(key);
	}
	public String getString(String key) {
		return String.valueOf(data.get(key));
	}
	public String getString(QName key) {
		return String.valueOf(data.get(key.toString()));
	}
	public Long getLong(QName key) {
		return (Long)data.get(key.toString());
	}
	public Long getLong(String key) {
		return (Long)data.get(key);
	}
	public Integer getInteger(QName key) {
		return (Integer)data.get(key.toString());
	}
	public Integer getInteger(String key) {
		return (Integer)data.get(key);
	}
	public Boolean getBoolean(QName key) {
		return (Boolean)data.get(key.toString());
	}
	public Boolean getBoolean(String key) {
		return (Boolean)data.get(key);
	}
	public void put(String key, String value) {
		data.put(key, value);
	}
	public Map<String,Object> getData() {
		return data;
	}
	
	public float getRawScore() {
		return rawScore;
	}
	public void setRawScore(float score) {
		this.rawScore = score;
	}
	public int getNormalizedScore() {
		return normalizedScore;
	}
	public void setNormalizedScore(int normalizedScore) {
		this.normalizedScore = normalizedScore;
	}
	public String getClassification() {
		return classification;
	}
	public void setClassification(String classification) {
		this.classification = classification;
	}
	
	public Map<String, Object> toMap() {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("id", id);
		map.put("qname", qname.toString());
		//map.put("name", "");
		map.put("rawScore", rawScore);
		map.put("normalizedScore", normalizedScore);
		map.put("classification", classification);
		for(String key : data.keySet()) {
			if(data.get(key) != null && !data.get(key).equals("") && 
					!key.equals("source_assoc") &&  !key.equals("target_assoc"))
				map.put(key, data.get(key));
		}
		return map;
	}
}
