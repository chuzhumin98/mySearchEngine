package test;

import java.io.File;
import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class TestIndexSearcher {
	private IndexReader reader;
	private IndexSearcher searcher;
	private Analyzer analyzer;
	private QueryParser qp;
	
	/*
	 * 构造函数，初始化解析器等
	 */
	public TestIndexSearcher(String path){
		analyzer = new IKAnalyzer();
		try{
			reader = IndexReader.open(FSDirectory.open(new File(path)));
			searcher = new IndexSearcher(reader);
			qp = new QueryParser(Version.LUCENE_35, "main", analyzer);
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/*
	 * 在某个特定域中搜索结果
	 */
	public TopDocs searchQuery(String queryString,String field,int maxnum){
		try {
			Query query= qp.parse(queryString);
			query.setBoost(1.0f);
			TopDocs results = searcher.search(query, maxnum);
			return results;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/*
	 * 根据docID获取与之对应的doc
	 */
	public Document getDoc(int docID){
		try{
			return searcher.doc(docID);
		}catch(IOException e){
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args){
		TestIndexSearcher search=new TestIndexSearcher("index");	
		System.out.println("query:工业趋势");
		TopDocs results=search.searchQuery("工业趋势", "main", 1000);
		ScoreDoc[] hits = results.scoreDocs;
		System.out.println("the result number:"+hits.length);
		for (int i = 0; i < hits.length; i++) { // output raw format
			Document doc = search.getDoc(hits[i].doc);
			System.out.println("top "+ (i+1) + ":"+doc.get("main")+
					" score:"+hits[i].score);
		}
	}
}
