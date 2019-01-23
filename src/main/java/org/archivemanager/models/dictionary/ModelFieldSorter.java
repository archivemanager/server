package org.archivemanager.models.dictionary;

import java.io.Serializable;
import java.util.Comparator;

public class ModelFieldSorter implements Comparator<ModelField>, Serializable {
	private static final long serialVersionUID = -8808039926288616685L;

	public int compare(ModelField field1, ModelField field2) {
		if(field1.getOrder() == 0 && field2.getOrder() == 0) {
			return field1.getQName().getLocalName().compareTo(field2.getQName().getLocalName());
		} else {
			int order1 = field1.getOrder() > 0 ? field1.getOrder() : 10000;
			int order2 = field2.getOrder() > 0 ? field2.getOrder() : 10000;
			return (order1 > order2 ? 1 : (order1 == order2 ? 0 : -1));
		}
	}
}