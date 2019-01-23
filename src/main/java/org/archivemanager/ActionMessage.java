package org.archivemanager;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class ActionMessage {
	private String username;
	private Long timestamp;
	private Long targetId;
	private String targetUrl = "";
	private String user;
	private int totalRows;
	private int startRow;
	private int endRow;
	private boolean persistent;
	private int status;
	private String message;
	private String action;
	private Long actionId;
	private Map<String,String> parameters = new HashMap<String,String>();
	private List<JsonObject> data = new ArrayList<JsonObject>();
	private JsonObject crawlRequest;	
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	
	public ActionMessage(HttpServletRequest request, HttpServletResponse response) {
		this.request = request;
		this.response = response;
		setTimestamp(System.currentTimeMillis());
	}
	public ActionMessage(HttpServletRequest request, HttpServletResponse response,String username) {
		this.request = request;
		this.response = response;
		this.username = username;
		setTimestamp(System.currentTimeMillis());
	}	
	public ActionMessage() {
		setTimestamp(System.currentTimeMillis());
	}
	
	public ActionMessage(ActionMessage msg) {
		this.action = msg.getAction();
		//this.response = msg.getResponse();
		this.username = msg.getUsername();
		this.actionId = msg.getActionId();
		setTimestamp(System.currentTimeMillis());
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Long getActionId() {
		return actionId;
	}
	public void setActionId(Long actionId) {
		this.actionId = actionId;
	}
	public Long getTargetId() {
		return targetId;
	}
	public void setTargetId(Long targetId) {
		this.targetId = targetId;
	}
	public String getTargetUrl() {
		return targetUrl;
	}
	public void setTargetUrl(String targetUrl) {
		this.targetUrl = targetUrl;
	}
	public int getTotalRows() {
		return totalRows;
	}
	public void setTotalRows(int totalRows) {
		this.totalRows = totalRows;
	}
	public int getStartRow() {
		return startRow;
	}
	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}
	public int getEndRow() {
		return endRow;
	}
	public void setEndRow(int endRow) {
		this.endRow = endRow;
	}
	public Map<String, String> getParameters() {
		return parameters;
	}
	public void setParameters(Map<String, String> parameters) {
		this.parameters = parameters;
	}
	public List<JsonObject> getData() {
		return data;
	}
	public void setData(List<JsonObject> data) {
		this.data = data;
	}
	public Long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}
	public JsonObject getCrawlRequest() {
		return crawlRequest;
	}
	public void setCrawlRequest(JsonObject crawlRequest) {
		this.crawlRequest = crawlRequest;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public boolean isPersistent() {
		return persistent;
	}
	public void setPersistent(boolean persistent) {
		this.persistent = persistent;
	}	
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}	
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public HttpServletRequest getRequest() {
		return request;
	}
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	public HttpServletResponse getResponse() {
		return response;
	}
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}
	
	public void fromJson(String in) {
		JsonReader jsonReader = Json.createReader(new StringReader(in));
		JsonObject jsonObject = jsonReader.readObject();
		JsonNumber timestamp = jsonObject.getJsonNumber("timestamp");
		JsonNumber actionId = jsonObject.getJsonNumber("actionId");
		JsonNumber targetId = jsonObject.getJsonNumber("targetId");
		String targetUrl = jsonObject.getString("targetUrl");
        JsonNumber totalRows = jsonObject.getJsonNumber("totalRows");
        JsonNumber startRow = jsonObject.getJsonNumber("startRow");
        JsonNumber endRow = jsonObject.getJsonNumber("endRow");
        String action = jsonObject.getString("action");
        String message = jsonObject.containsKey("message") ? jsonObject.getString("message") : null;
        JsonNumber status = jsonObject.getJsonNumber("status");
        String user = jsonObject.getString("user");
        
        if(jsonObject.containsKey("username")) setUsername(jsonObject.getString("username"));
        if(actionId != null) setActionId(actionId.longValue());
        if(action != null) setAction(action);
        if(timestamp != null) setTimestamp(timestamp.longValue());
        if(targetId != null) setTargetId(targetId.longValue());
        if(targetUrl != null) setTargetUrl(targetUrl);
        if(status != null) setStatus(status.intValue());
        if(message != null) setMessage(message);
        if(user != null) setUser(user);
		
        setTotalRows(totalRows.intValue());
        setStartRow(startRow.intValue());
        setEndRow(endRow.intValue());
        crawlRequest = jsonObject.getJsonObject("crawlRequest");
        JsonArray dataArr = jsonObject.getJsonArray("data");
		for(int i=0; i < dataArr.size(); i++) {
			JsonObject obj = dataArr.getJsonObject(i);
			data.add(obj);
		}
		JsonArray params = jsonObject.getJsonArray("parameters");
		for(int i=0; i < params.size(); i++) {
			JsonObject obj = params.getJsonObject(i);
			String key = obj.getString("key");
			String val = obj.getString("value");
			parameters.put(key, val);
		}
    	jsonReader.close();
	}
	public String toJson() {
		JsonObjectBuilder builder = Json.createObjectBuilder();		
		builder.add("totalRows", getTotalRows());
		builder.add("startRow", getStartRow());
		builder.add("endRow", getEndRow());
		builder.add("action", getAction());
		builder.add("status", status);
		if(actionId != null) builder.add("actionId", getActionId());
		if(getTargetId() != null) builder.add("targetId", getTargetId());
		if(getTargetUrl() != null) builder.add("targetUrl", getTargetUrl());
		if(getTimestamp() != null) builder.add("timestamp", getTimestamp());
		if(getUsername() != null) builder.add("username", getUsername());
		if(crawlRequest != null) builder.add("crawlRequest", crawlRequest);
		if(message != null) builder.add("message", getMessage());
		if(getUser() != null) builder.add("user", getUser());
		JsonArrayBuilder dataBuilder = Json.createArrayBuilder();
		for(JsonObject obj : data) {			
			dataBuilder.add(obj);
		}
		builder.add("data", dataBuilder);
		JsonArrayBuilder parameterBuilder = Json.createArrayBuilder();
		for(String key : parameters.keySet()) {
			JsonObjectBuilder parameterObjBuilder = Json.createObjectBuilder();
			parameterObjBuilder.add("key", key);
			parameterObjBuilder.add("value", String.valueOf(parameters.get(key)));			
			parameterBuilder.add(parameterObjBuilder.build());
		}
		builder.add("parameters", parameterBuilder);
		return builder.build().toString();
	}
}
