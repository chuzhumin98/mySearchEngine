package server;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;

public class superSearcher {
	public void getSearch(int searchState, int analyzerMethod, String query) {}
	
	public ScoreDoc[] hits;
	public String getHighlightQuery(String query) {
		return query;
	}

	public Document getDoc(int doc) {
		// TODO Auto-generated method stub
		return null;
	}

	public String hightLightString(String queryString, String text) {
		// TODO Auto-generated method stub
		return text;
	}
}
