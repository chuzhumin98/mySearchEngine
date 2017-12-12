package test;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
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

import net.paoding.analysis.analyzer.PaodingAnalyzer;

public class SearcherWithWord2Vec {
	private IndexReader reader;
	private IndexSearcher searcher;
	private Analyzer analyzer;
	public static int searchState = 0;
	public static int indexPath = 0;
	public static float[] boostsValue = {8, 10, 1, 2};
	public static String[] pathIndex = {"index/simpleIKanalyzer"}; //对应的index的位置 
	private Map<String, Float> fieldBoosts;
	public static String[] fieldsName = {"题名", "作者", "摘要", "年"};
	public static String[] fieldsNameEnglish = {"英文篇名", "英文作者", "英文摘要", "年"};
	public static String[] myfieldsName = fieldsName; //指向现在需要使用的域的引用
	
	/*
	 * 获取
	 */
	public static void getFieldsName() {
		switch (searchState) {
		case 0:
			myfieldsName = fieldsName;
			break;
		case 1:
			myfieldsName = fieldsNameEnglish;
			break;
		default:
			myfieldsName = fieldsName;
		}
	}
	
	/*
	 * 获取对应的分词器
	 */
	private static Analyzer getAnalyzer() {
		Analyzer tmp = null;
		tmp = new IKAnalyzer();
		return tmp;
	}
	
	/*
	 * 构造函数，初始化解析器等
	 */
	@SuppressWarnings("static-access")
	public SearcherWithWord2Vec(String path){
		analyzer = this.getAnalyzer();
		getFieldsName();
		try{
			reader = IndexReader.open(FSDirectory.open(new File(path)));
			searcher = new IndexSearcher(reader);
			fieldBoosts = new HashMap<String, Float>();
			for (int i = 0; i < myfieldsName.length; i++) {
				fieldBoosts.put(myfieldsName[i], boostsValue[i]);
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
			getFieldsName();
			org.apache.lucene.queryParser.MultiFieldQueryParser mfqp = new org.apache.lucene.queryParser.MultiFieldQueryParser
					(Version.LUCENE_35, myfieldsName, analyzer, fieldBoosts);			
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
	 * 将所有内容看成一个域进行搜索
	 */
	public TopDocs searchQueryTotal(String queryString, int maxnum){ 
		return this.searchQueryOneField(queryString, "total", maxnum);
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
		getFieldsName();
		for (int i = 0; i < myfieldsName.length; i++) {
			String fieldName = myfieldsName[i];
			strings += fieldName + ":" + doc.get(fieldName) + "\n";
		}
		return strings;
	}
	
	public static void main(String[] args){
		SearcherWithWord2Vec search=new SearcherWithWord2Vec(
				SearcherWithWord2Vec.pathIndex[indexPath]);	//找到对应方法的路径
		/*
		 * query:江泽民，非常好地诠释CJK存在一定问题的例子
		 */
		System.out.println("query:Application");
		//TopDocs results=search.searchQueryOneField("2012", "年", 100);
		TopDocs results;
		if (searchState == -1) {
			results = search.searchQueryTotal("application", 1000);
			ScoreDoc[] hits = results.scoreDocs;
			System.out.println("the result number:"+hits.length);
			for (int i = 0; i < Math.min(hits.length, 20); i++) { // output raw format
				Document doc = search.getDoc(hits[i].doc);
				System.out.println("top "+ (i+1) + ":\n"+doc.get("total")+
						"score:"+hits[i].score);
			}
		} else {
			results = search.searchQueryFields("工业", 1000);
			ScoreDoc[] hits = results.scoreDocs;
			System.out.println("the result number:"+hits.length);
			for (int i = 0; i < Math.min(hits.length, 100); i++) { // output raw format
				Document doc = search.getDoc(hits[i].doc);
				System.out.println("top "+ (i+1) + ":\n"+toDocString(doc)+
						"score:"+hits[i].score);
			}
		}	 
	}
}
