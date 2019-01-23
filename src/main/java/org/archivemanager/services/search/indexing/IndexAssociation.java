package org.archivemanager.services.search.indexing;

import java.util.ArrayList;
import java.util.List;

import org.archivemanager.models.SystemModel;
import org.archivemanager.models.system.QName;

public class IndexAssociation {
	private Long id;
	private QName qname;      
    private List<IndexField> fields = new ArrayList<IndexField>();
    private List<String> messages = new ArrayList<String>();
    
    
    public IndexAssociation() {}
    public IndexAssociation(Long id, QName qname, Long source, Long target) {
		this.id = id;
		this.qname = qname;
		fields.add(new IndexField(SystemModel.SOURCE.toString(), source));
		fields.add(new IndexField(SystemModel.TARGET.toString(), target));
	}
    
    public Long getSource() {
		for(IndexField field : fields) {
			if(field.getName().equals(SystemModel.SOURCE.toString()))
				return (Long)field.getValue();
		}
		return 0L;
	}
	public Long getTarget() {
		for(IndexField field : fields) {
			if(field.getName().equals(SystemModel.TARGET.toString()))
				return (Long)field.getValue();
		}
		return 0L;
	}
		    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public QName getQName() {
		return qname;
	}
	public void setQName(QName qname) {
		this.qname = qname;
	}	
	public QName getQname() {
		return qname;
	}
	public void setQname(QName qname) {
		this.qname = qname;
	}	
	public List<IndexField> getFields() {
		return fields;
	}
	public void setFields(List<IndexField> fields) {
		this.fields = fields;
	}
	public List<String> getMessages() {
		return messages;
	}
	public void setMessages(List<String> messages) {
		this.messages = messages;
	} 
	
}
