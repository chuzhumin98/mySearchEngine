package test;

import java.awt.List;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
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

import server.superSearcher;
import word2vec.LoadModel;
import word2vec.WordEntry;


public class SearcherWithWord2Vec extends superSearcher {
	private IndexReader reader;
	private IndexSearcher searcher;
	private Analyzer analyzer;
	public static int searchState = 0;
	public static int indexPath = 0;
	public float[] boostsValue = {8, 10, 1, 2};
	public static String[] pathIndex = {"D:/workspace/SearchEngine/index/simpleIKanalyzer"}; //对应的index的位置 
	private Map<String, Float> fieldBoosts;
	public static String[] fieldsName = {"题名", "作者", "摘要", "年"};
	public static String[] fieldsNameEnglish = {"英文篇名", "英文作者", "英文摘要", "年"};
	public String[] myfieldsName = fieldsName; //指向现在需要使用的域的引用
	
	
	/*
	 * 获取
	 */
	public void getFieldsName() {
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
	 * 在arraylist中搜索是否含有该id的doc记录,如果没有则返回-1
	 */
	public int searchScoreDoc(ArrayList<ScoreDoc> mydocs, int doc) {
		int index = -1;
		for (int i = 0; i < mydocs.size(); i++) {
			if (mydocs.get(i).doc == doc) {
				index = i;
				break;
			}
		}
		return index;
	}
	
	/*
	 * 加入word2vec后的权重计算方法
	 */
	public ScoreDoc[] searchWithWord2Vec(String queryString, int maxnum) throws IOException {
		TopDocs results = searchQueryFields(queryString, maxnum);
		ScoreDoc[] docs = results.scoreDocs;
		ArrayList<ScoreDoc> mydocs = new ArrayList<ScoreDoc>();
		for (int i = docs.length-1; i >= 0; i--) {
			int index = this.searchScoreDoc(mydocs, docs[i].doc);
			if (index == -1) { //还不存在当前文档记录时创建一个
				mydocs.add(docs[i]);
			} else { //否则在当前权重上叠加
				mydocs.get(index).score += docs[i].score;
			}
		}
		
		StringReader reader = new StringReader(queryString);  
	    TokenStream ts = analyzer.tokenStream("", reader);  
	    CharTermAttribute term = ts.getAttribute(CharTermAttribute.class); 
	    ArrayList<String> splits = new ArrayList<String>();
		while(ts.incrementToken()){  
			splits.add(term.toString()); 
		}
		LoadModel model = LoadModel.getInstance();
		int size = splits.size(); //切分之后词的数量
		for (int i = 0; i < size; i++) {
			Set<WordEntry> simTerm = model.distance(splits.get(i));
			System.out.println(splits.get(i)+":"+simTerm.toString());
			for (WordEntry item: simTerm) {
				TopDocs simResult = this.searchQueryFields(item.name, maxnum);
				ScoreDoc[] tmpdoc = simResult.scoreDocs;
				for (int k = 0; k < tmpdoc.length; k++) {
					tmpdoc[k].score /= size * Math.exp(10*(1-item.score)); //调整参数就靠这句了
					int index = this.searchScoreDoc(mydocs, tmpdoc[k].doc);
					if (index == -1) { //还不存在当前文档记录时创建一个
						mydocs.add(tmpdoc[k]);
					} else { //否则在当前权重上叠加
						mydocs.get(index).score += tmpdoc[k].score;
					}
				}			
			}
		}
		Collections.sort(mydocs, new Comparator<ScoreDoc>() {
		    public int compare(ScoreDoc s1, ScoreDoc s2) {
		    	if (s2.score > s1.score) {
		    		return 1;
		    	} else {
		    		if (s2.score < s1.score) {
		    			return -1;
		    		} else {
		    			return 0;
		    		}
		    	}
		    }
		}); 
		int sizes = Math.min(maxnum, mydocs.size()); //至多取maxnum个输出
		docs = new ScoreDoc [sizes];
		for (int i = 0; i < sizes; i++) {
			docs[i] = mydocs.get(i);
		}
		return docs;
	}
	
	/*
	 * (non-Javadoc)
	 * @see server.superSearcher#getSearch(int, int, java.lang.String)
	 */
	public void getSearch(int searchState, int analyzerMethod, String query) {
		/*
		 * query:江泽民，非常好地诠释CJK存在一定问题的例子
		 */
		System.out.println("query:"+query);
		//TopDocs results=search.searchQueryOneField("2012", "年", 100);
		TopDocs results;
		if (searchState == -1) {
			results = this.searchQueryTotal(query, 1000);
			hits = results.scoreDocs;
			System.out.println("the result number:"+hits.length);
			for (int i = 0; i < Math.min(hits.length, 5); i++) { // output raw format
				Document doc = this.getDoc(hits[i].doc);
				System.out.println("top "+ (i+1) + ":\n"+doc.get("total")+
						"score:"+hits[i].score);
			}
		} else {
			try {
				hits = this.searchWithWord2Vec(query, 1000);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("the result number:"+hits.length);
			for (int i = 0; i < Math.min(hits.length, 10); i++) { // output raw format
				Document doc = this.getDoc(hits[i].doc);
				System.out.println("top "+ (i+1) + ":\n"+this.toDocString(doc)+
						"score:"+hits[i].score);
			}
		}	
	}
	
	/*
	 * 拼接一下需要高亮的字符串
	 * (non-Javadoc)
	 * @see server.superSearcher#getHighlightQuery(java.lang.String)
	 */
	public String getHighlightQuery(String query) {
		String highlightQuery = query;
		StringReader reader = new StringReader(query);  
	    TokenStream ts = analyzer.tokenStream("", reader);  
	    CharTermAttribute term = ts.getAttribute(CharTermAttribute.class); 
	    ArrayList<String> splits = new ArrayList<String>();
		try {
			while(ts.incrementToken()){  
				splits.add(term.toString()); 
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LoadModel model = LoadModel.getInstance();
		int size = splits.size(); //切分之后词的数量
		for (int i = 0; i < size; i++) {
			Set<WordEntry> simTerm = model.distance(splits.get(i));
			//System.out.println(splits.get(i)+":"+simTerm.toString());
			for (WordEntry item: simTerm) {
				highlightQuery += " "+item.name;		
			}
		}
		return highlightQuery;
	}
	
	/*
	 * 获取高亮后的字符串
	 * (non-Javadoc)
	 * @see server.superSearcher#hightLightString(java.lang.String, java.lang.String)
	 */
	public String hightLightString(String query, String text) {
		String highlightQuery = this.getHighlightQuery(query);
		QueryParser queryParser;
		if (this.searchState == -1) {
			queryParser = new QueryParser(Version.LUCENE_35, "total", analyzer);
		} else {
			queryParser = new org.apache.lucene.queryParser.MultiFieldQueryParser
					(Version.LUCENE_35, myfieldsName, analyzer, fieldBoosts);	
		}
           
        Query myQuery = null;
		try {
			myQuery = queryParser.parse(highlightQuery);
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
	
	public static void main(String[] args) throws IOException{
		SearcherWithWord2Vec search=new SearcherWithWord2Vec(
				SearcherWithWord2Vec.pathIndex[indexPath]);	//找到对应方法的路径
		search.getSearch(0, 0, "药学");
	}
}
