package org.archivemanager.services.search.indexing;

import org.archivemanager.models.system.QName;

public class IndexFacet {
	private QName qname;
	private String value;
	private long target;
	
	public IndexFacet(QName qname, String value, long target) {
		this.qname = qname;
		this.value = value;
		this.target = target;
	}

	public QName getQname() {
		return qname;
	}
	public void setQname(QName qname) {
		this.qname = qname;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public long getTarget() {
		return target;
	}
	public void setTarget(long target) {
		this.target = target;
	}	
}
