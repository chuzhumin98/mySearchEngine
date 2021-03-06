package test;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.chenlb.mmseg4j.analysis.ComplexAnalyzer;

import server.superSearcher;


public class TestIndexSearcher extends superSearcher {
	private IndexReader reader;
	private IndexSearcher searcher;
	private Analyzer analyzer;
	public int analyzerMethod = 1;
	public int searchState = 0; //中文还是英文域
	public float[] boostsValue = {8, 10, 1, 2};
	public static String[] pathIndex = {"D:/workspace/SearchEngine/index/simpleIKanalyzer",
			"D:/workspace/SearchEngine/index/simpleStandardAnalyzer",
			"D:/workspace/SearchEngine/index/simpleCJKAnalyzer",
			"D:/workspace/SearchEngine/index/simpleComplexAnalyzer", 
			"D:/workspace/SearchEngine/index/simpleStandardAnalyzerEnglish",
			"D:/workspace/SearchEngine/index/simpleToy"}; //对应的index的位置 
	private Map<String, Float> fieldBoosts;
	public static String[] myfieldsName; //指向现在需要使用的域的引用
	
	
	/*
	 * 获取
	 */
	public void getFieldsName() {
		switch (searchState) {
		case 0:
			myfieldsName = TestIndexWriter.fieldsName;
			break;
		case 1:
			myfieldsName = TestIndexWriter.fieldsNameEnglish;
			break;
		default:
			myfieldsName = TestIndexWriter.fieldsName;
		}
	}
	
	/*
	 * 获取对应的分词器
	 */
	private Analyzer getAnalyzer() {
		Analyzer tmp = null;
		if (searchState == -1) {
			tmp = new IKAnalyzer();
		}
		if (searchState == 0) { //简单中文查询
			switch (analyzerMethod) {
	        case 0:
	        	tmp = new IKAnalyzer(); 
	        	break;
	        case 1:
	        	tmp = new StandardAnalyzer(Version.LUCENE_35);
	        	break;
	        case 2:
	        	tmp = new CJKAnalyzer(Version.LUCENE_35);
	        	break;
	        case 3:
	        	tmp = new ComplexAnalyzer(); 
	        	break;
	        default:
	        	tmp = new IKAnalyzer(); 
	        }
		}
		if (searchState == 1) { //简单英文查询
        	tmp = new StandardAnalyzer(Version.LUCENE_35);
		}
		return tmp;
	}
	
	/*
	 * 构造函数，初始化解析器等
	 */
	@SuppressWarnings("static-access")
	public TestIndexSearcher(String path){
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
	
	public void initSearcher() {
		analyzer = this.getAnalyzer();
		getFieldsName();
		fieldBoosts = new HashMap<String, Float>();
		for (int i = 0; i < myfieldsName.length; i++) {
			fieldBoosts.put(myfieldsName[i], boostsValue[i]);
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
	 * 多域查询，每个域有自己的查询词
	 */
	/*
	 * 在给定的多个域中搜索结果
	 */
	public TopDocs searchQuerysFields(String[] queryString, int maxnum){ //已设置fileNames和fieldBoosts
		try {
			getFieldsName();
			BooleanQuery booleanQuery = new BooleanQuery(); //新建一个布尔查询
			
			int count = 0; //查询需要搜索的域的数量
			for (int i = 0; i < this.myfieldsName.length; i++) {
				if (queryString[i] != null && !queryString[i].equals("")) {
					QueryParser parser1 = new QueryParser(Version.LUCENE_35, this.myfieldsName[i], analyzer); 
			        Query myQuery = parser1.parse(queryString[i]); 
			        booleanQuery.add(myQuery, BooleanClause.Occur.MUST); 
			        myQuery.setBoost(this.boostsValue[i]);
					count++;
				}
			}
			System.out.println("using field:"+count);
			TopDocs results = searcher.search(booleanQuery, maxnum);
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
	
	private String toDocString(Document doc) {
		String strings = "";
		getFieldsName();
		for (int i = 0; i < myfieldsName.length; i++) {
			String fieldName = myfieldsName[i];
			strings += fieldName + ":" + doc.get(fieldName) + "\n";
		}
		return strings;
	}
	
	/*
	 * 整合的方法
	 */
	public void getSearch(int searchState, int analyzerMethod, String query) {
		this.searchState = searchState;
		this.analyzerMethod = analyzerMethod;
		this.initSearcher();
		/*
		 * query:江泽民，非常好地诠释CJK存在一定问题的例子
		 */
		System.out.println("query:"+query);
		//TopDocs results=search.searchQueryOneField("2012", "年", 100);
		TopDocs results;
		if (this.searchState == -1) {
			results = this.searchQueryTotal(query, 1000);
			hits = results.scoreDocs;
			System.out.println("the result number:"+hits.length);
			for (int i = 0; i < Math.min(hits.length, 5); i++) { // output raw format
				Document doc = this.getDoc(hits[i].doc);
				System.out.println("top "+ (i+1) + ":\n"+doc.get("total")+
						"score:"+hits[i].score);
			}
		} else {
			results = this.searchQueryFields(query, 1000);
			hits = results.scoreDocs;
			System.out.println("the result number:"+hits.length);
			for (int i = 0; i < Math.min(hits.length, 10); i++) { // output raw format
				Document doc = this.getDoc(hits[i].doc);
				System.out.println("top "+ (i+1) + ":\n"+this.toDocString(doc)+
						"score:"+hits[i].score);
			}
		}	 
	}
	
	/*
	 * 整合的方法
	 */
	public void getSearch(int searchState, int analyzerMethod, String[] query) {
		this.searchState = searchState;
		this.analyzerMethod = analyzerMethod;
		this.initSearcher();
		/*
		 * query:江泽民，非常好地诠释CJK存在一定问题的例子
		 */
		System.out.println("query:"+query);
		//TopDocs results=search.searchQueryOneField("2012", "年", 100);
		TopDocs results;
		results = this.searchQuerysFields(query, 1000);
		hits = results.scoreDocs;
		System.out.println("the result number:"+hits.length);
		for (int i = 0; i < Math.min(hits.length, 10); i++) { // output raw format
			Document doc = this.getDoc(hits[i].doc);
			System.out.println("top "+ (i+1) + ":\n"+this.toDocString(doc)+
					"score:"+hits[i].score);
		}		 
	}
	
	/*
	 * 高亮结果
	 */
	public void hightLight(String query) { 
        QueryParser queryParser = new QueryParser(Version.LUCENE_35,"bookName", analyzer);  
        Query myQuery = null;
		try {
			myQuery = queryParser.parse(query);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
          
        SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<font color='red'>", "</font>");      
        Highlighter highlighter = new Highlighter(simpleHTMLFormatter,new QueryScorer(myQuery));  
        highlighter.setTextFragmenter(new SimpleFragmenter(1024));   
        for(int i = 0; i < hits.length; i++){  
        	System.out.println("top "+(i+1));
            Document doc = null;
			try {
				doc = searcher.doc(hits[i].doc);
			} catch (CorruptIndexException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}  
			for (int j = 0; j < this.myfieldsName.length; j++) {
				String text = doc.get(this.myfieldsName[j]);  
	              
	            TokenStream tokenStream = analyzer.tokenStream(text,new StringReader(text));     
	            String highLightText = null;
				try {
					highLightText = highlighter.getBestFragment(tokenStream, text);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvalidTokenOffsetsException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}  
	            if (highLightText == null) {
	            	highLightText = text;
	            }
	            System.out.println(this.myfieldsName[j]+":"+highLightText);  
			}
            
        }   
	}
	
	/*
	 * 高亮某一个字符串
	 */
	public String hightLightString(String query, String text) { 
		QueryParser queryParser;
		if (this.searchState == -1) {
			queryParser = new QueryParser(Version.LUCENE_35, "total", analyzer);
		} else {
			queryParser = new org.apache.lucene.queryParser.MultiFieldQueryParser
					(Version.LUCENE_35, myfieldsName, analyzer, fieldBoosts);	
		}
           
        Query myQuery = null;
		try {
			myQuery = queryParser.parse(query);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}  
          
        SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<font color='red'>", "</font>");      
        Highlighter highlighter = new Highlighter(simpleHTMLFormatter,new QueryScorer(myQuery));  
        highlighter.setTextFragmenter(new SimpleFragmenter(1024));   
        TokenStream tokenStream = analyzer.tokenStream(text,new StringReader(text));     
        String highLightText = null;
		try {
			highLightText = highlighter.getBestFragment(tokenStream, text);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidTokenOffsetsException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        if (highLightText == null) {
        	highLightText = text;
        }   
        return highLightText;
	}
	
	public static void main(String[] args){
		TestIndexSearcher search=new TestIndexSearcher(
				TestIndexSearcher.pathIndex[2]);	//找到对应方法的路径
		//search.getSearch(0, 2, "江泽民");
		//search.hightLight("江泽民");
		String[] tmp = {"建筑","张大庆","建筑","2000"};
		search.getSearch(0, 2, tmp);
	}
}
