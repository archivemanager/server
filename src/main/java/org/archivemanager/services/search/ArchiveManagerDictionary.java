package org.archivemanager.services.search;
import java.util.Calendar;

import org.archivemanager.services.search.dictionary.AllResultsDefinition;
import org.archivemanager.services.search.dictionary.TermDefinition;
import org.archivemanager.services.search.parsing.BaseDictionary;
import org.springframework.stereotype.Component;


@Component
public class ArchiveManagerDictionary extends BaseDictionary {

	public ArchiveManagerDictionary() {
		//Year only definitions
		int numPeriods = 200;
		for(int i=numPeriods; i >= 0; i--) {
			Calendar now = Calendar.getInstance();
			now.roll(Calendar.YEAR, -i);
			now.set(Calendar.MONTH, 11);
			now.set(Calendar.DAY_OF_MONTH, 31);
			long end = now.getTimeInMillis();
			now.set(Calendar.MONTH, 0);
			now.set(Calendar.DAY_OF_MONTH, 1);
			long start = now.getTimeInMillis();			
			addDefinition(new TermDefinition(String.valueOf(now.get(Calendar.YEAR)), "date_expression_:["+start+" TO "+end+"]"));
		}
				
		addDefinition(new AllResultsDefinition("All Results"));
		
		//CategoryDefinitionDictionaryPlugin categories = new CategoryDefinitionDictionaryPlugin();
		//categories.setApplicationContext(ctx);
		//collectionDictionary.addPlugin(categories);
		
	}
}
