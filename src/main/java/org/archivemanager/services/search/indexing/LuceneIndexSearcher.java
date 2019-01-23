package org.archivemanager.services.search.indexing;

public class LuceneIndexSearcher implements IndexSearcher {
	private org.apache.lucene.search.IndexSearcher searcher;
	
	public LuceneIndexSearcher(org.apache.lucene.search.IndexSearcher searcher) {
		this.searcher = searcher;
	}
	
	public org.apache.lucene.search.IndexSearcher getNativeIndexSearcher() {
		return searcher;
	}
}