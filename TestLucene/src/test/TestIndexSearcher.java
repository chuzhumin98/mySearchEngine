package test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

	private Map<String, Float> fieldBoosts;
	
	/*
	 * 构造函数，初始化解析器等
	 */
	public TestIndexSearcher(String path){
		analyzer = new IKAnalyzer(); //需要使用同样的分词器
		try{
			reader = IndexReader.open(FSDirectory.open(new File(path)));
			searcher = new IndexSearcher(reader);
			fieldBoosts = new HashMap<String, Float>();
			for (int i = 0; i < TestInderWriter.fieldsName.length; i++) {
				fieldBoosts.put(TestInderWriter.fieldsName[i], TestInderWriter.boostsValue[i]);
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
	
	/*
	 * 在某个特定域中搜索结果
	 */
	public TopDocs searchQueryOneField(String queryString,String field,int maxnum){
		try {
			QueryParser qp = new QueryParser(Version.LUCENE_35, field, analyzer);			
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
	 * 在给定的多个域中搜索结果
	 */
	public TopDocs searchQueryFields(String queryString, int maxnum){ //已设置fileNames和fieldBoosts
		try {
			org.apache.lucene.queryParser.MultiFieldQueryParser mfqp = new org.apache.lucene.queryParser.MultiFieldQueryParser
					(Version.LUCENE_35, TestInderWriter.fieldsName, analyzer, fieldBoosts);			
			Query query= mfqp.parse(queryString);
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
	
	private static String toDocString(Document doc) {
		String strings = "";
		for (int i = 0; i < TestInderWriter.fieldsName.length; i++) {
			String fieldName = TestInderWriter.fieldsName[i];
			strings += fieldName + ":" + doc.get(fieldName) + "\n";
		}
		return strings;
	}
	
	public static void main(String[] args){
		TestIndexSearcher search=new TestIndexSearcher("index");	
		System.out.println("query:刘洋");
		//TopDocs results=search.searchQueryOneField("2012", "年", 100);
		TopDocs results = search.searchQueryFields("刘洋", 100);
		ScoreDoc[] hits = results.scoreDocs;
		System.out.println("the result number:"+hits.length);
		for (int i = 0; i < hits.length; i++) { // output raw format
			Document doc = search.getDoc(hits[i].doc);
			System.out.println("top "+ (i+1) + ":\n"+toDocString(doc)+
					"score:"+hits[i].score);
		}
	}
}
