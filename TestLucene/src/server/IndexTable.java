package server;

import java.util.ArrayList;

import org.apache.lucene.search.ScoreDoc;

import net.sf.json.JSONObject;
import test.SearcherWithWord2Vec;
import test.TestIndexSearcher;

public class IndexTable {
	public static IndexTable myTable = null;
	public static superSearcher[] myEngine = new superSearcher [10];
	public ArrayList<JSONObject> tables = new ArrayList<JSONObject>();
	
	public static String[] fieldsName = {"题名", "作者", "摘要", "年"};
	public static String[] fieldsNameEnglish = {"英文篇名", "英文作者", "英文摘要", "年"};
	public static String[] fieldsTotal = {"total"};
	
	
	public IndexTable() {
		/*
		 * index:0
		 * word2vec方法，默认采用IKanalyzer
		 */
		JSONObject json1 = new JSONObject();
		json1.put("searchState", 0); //决定调用的域类型
		json1.put("indexPath", 0); //index所存储的位置
		json1.put("classType", 1); //0表示TestIndexSearcher,1表示SearcherWithWord2Vec
		json1.put("analyzerMethod", 0); //表示采用的分词工具类型
		tables.add(json1);
		
		/*
		 * index:1
		 * toy model
		 */
		json1 = new JSONObject();
		json1.put("searchState", -1); //决定调用的域类型
		json1.put("indexPath", 5); //index所存储的位置
		json1.put("classType", 0); //0表示TestIndexSearcher,1表示SearcherWithWord2Vec
		json1.put("analyzerMethod", 0); //表示采用的分词工具类型
		tables.add(json1);
		
		/*
		 * index:2
		 * IKanalyzer对于普通中文文本的检索
		 */
		json1 = new JSONObject();
		json1.put("searchState", 0); //决定调用的域类型
		json1.put("indexPath", 0); //index所存储的位置
		json1.put("classType", 0); //0表示TestIndexSearcher,1表示SearcherWithWord2Vec
		json1.put("analyzerMethod", 0); //表示采用的分词工具类型
		tables.add(json1);
		
		/*
		 * index:3
		 * StandardAnalyzer对于普通中文文本的检索
		 */
		json1 = new JSONObject();
		json1.put("searchState", 0); //决定调用的域类型
		json1.put("indexPath", 1); //index所存储的位置
		json1.put("classType", 0); //0表示TestIndexSearcher,1表示SearcherWithWord2Vec
		json1.put("analyzerMethod", 1); //表示采用的分词工具类型
		tables.add(json1);
		
		/*
		 * index:4
		 * CJKAnalyzer对于普通中文文本的检索
		 */
		json1 = new JSONObject();
		json1.put("searchState", 0); //决定调用的域类型
		json1.put("indexPath", 2); //index所存储的位置
		json1.put("classType", 0); //0表示TestIndexSearcher,1表示SearcherWithWord2Vec
		json1.put("analyzerMethod", 2); //表示采用的分词工具类型
		tables.add(json1);
		
		/*
		 * index:5
		 * StandardAnalyzer对于普通英文文本的检索
		 */
		json1 = new JSONObject();
		json1.put("searchState", 1); //决定调用的域类型
		json1.put("indexPath", 4); //index所存储的位置
		json1.put("classType", 0); //0表示TestIndexSearcher,1表示SearcherWithWord2Vec
		json1.put("analyzerMethod", 0); //表示采用的分词工具类型
		tables.add(json1);
	}
	
	public static IndexTable getInstance() {
		if (IndexTable.myTable == null) {
			IndexTable.myTable = new IndexTable();
		}
		return myTable;
	}
	
	public ScoreDoc[] getSearchResult(int method, String query) {
		IndexTable mytable = IndexTable.getInstance();
		JSONObject json1 = mytable.tables.get(method);
		//System.out.println(json1.toString());
		if (IndexTable.myEngine[method] == null) {
			//System.out.println("is null");
			int indexPath = json1.getInt("indexPath");
			if (json1.getInt("classType") == 0) {				
				IndexTable.myEngine[method] = new TestIndexSearcher(TestIndexSearcher.pathIndex[indexPath]);
			}
			if (json1.getInt("classType") == 1) {
				//System.out.println("is new");
				IndexTable.myEngine[method] = new SearcherWithWord2Vec(
						SearcherWithWord2Vec.pathIndex[indexPath]);	//找到对应方法的路径
			}
		}
		int searchState = json1.getInt("searchState");
		int analyzerMethod = json1.getInt("analyzerMethod");
		IndexTable.myEngine[method].getSearch(searchState, analyzerMethod, query);
		return IndexTable.myEngine[method].hits;
	}
	
	/*
	 * 获取搜索的域
	 */
	public static String[] getFields(int method) {
		if (method == 1) {
			return IndexTable.fieldsTotal;
		} 
		else if (method == 5) {
			return IndexTable.fieldsNameEnglish;
		}
		else {
			return IndexTable.fieldsName;
		}
	}
	
	public static void main(String[] args) {
		IndexTable table = IndexTable.getInstance();
		table.getSearchResult(4, "江泽民");
	}
}
