package org.archivemanager.models.dictionary;

import java.io.Serializable;
import java.util.Comparator;

public class ModelSorter implements Comparator<Model>, Serializable {
	private static final long serialVersionUID = -6428587336087248053L;

	public int compare(Model model1, Model model2) {
		return model1.getQName().getLocalName().compareTo(model2.getQName().getLocalName());
	}
}
