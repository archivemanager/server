package org.archivemanager.models.binders;

import org.archivemanager.models.repository.ResultSorter;
import org.jsoup.Jsoup;

public abstract class EntityBinderSupport {
	protected ResultSorter sorter = new ResultSorter();
	
	
	protected String decode(String in) {
		if(in == null) return "";
		return Jsoup.parse(in).text();
	}
	protected String getLanguageLabel(String in) {
		if(in.equals("en")) return "English";
		if(in.equals("ar")) return "Arabic";
		if(in.equals("zh")) return "Chinese";
		if(in.equals("cs")) return "Czech";
		if(in.equals("da")) return "Danish";
		if(in.equals("nl")) return "Dutch";
		if(in.equals("en")) return "English";
		if(in.equals("fi")) return "Finnish";
		if(in.equals("fr")) return "French";
		if(in.equals("de")) return "German";
		if(in.equals("el")) return "Greek";
		if(in.equals("he")) return "Hebrew";
		if(in.equals("hu")) return "Hungarian";
		if(in.equals("is")) return "Icelandic";
		if(in.equals("it")) return "Italian";
		if(in.equals("ja")) return "Japanese";
		if(in.equals("ko")) return "Korean";
		if(in.equals("no")) return "Norwegian";
		if(in.equals("pl")) return "Polish";
		if(in.equals("pt")) return "Portugese";
		if(in.equals("ru")) return "Russian";
		if(in.equals("es")) return "Spanish";
		if(in.equals("sv")) return "Swedish";
		if(in.equals("th")) return "Thai";
		if(in.equals("tr")) return "Turkish";
		if(in.equals("yi")) return "Yiddish";
		return in;
	}
	protected String cleanContentType(String in) {
		if(in == null) return "";
		if(in.equals("audio")) return "Audio";
		if(in.equals("correspondence")) return "Correspondence";
		if(in.equals("financial")) return "Financial Material";
		if(in.equals("legal")) return "Legal Material";
		if(in.equals("manuscript")) return "Manuscript";
		if(in.equals("memorabilia")) return "Memorabilia";
		if(in.equals("photographs")) return "Photographic Material";
		if(in.equals("printed_material")) return "Printed Material";
		if(in.equals("professional")) return "Professional Material";
		if(in.equals("video")) return "Video";
		if(in.equals("research")) return "Research";
		return in;
	}
}
