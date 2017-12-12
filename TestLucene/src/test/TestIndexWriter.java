package test;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class TestIndexWriter {
	public static String[] fieldsName = {"题名", "作者", "摘要", "年"};
	public static String[] fieldsNameEnglish = {"英文篇名", "英文作者", "英文摘要", "年"};
	public static int analyzerMethod = 0; //0表示IKAnalyzer,1表示StandardAnalyzer(对中文按字分词),2表示CJKAnalyzer
	public static int indexState = -1; //-1表示toy搜索，即不区分任何标签，0表示简单中文索引，1表示简单英文索引
	public static int indexPath = 5; //对应的index的位置
	public static boolean[] beAnalyzed = {true, true, true, false};
	public static float[] boostsValue = {8, 10, 1, 2};
	public static String[] myfieldsName; //指向现在需要使用的域的引用
	static IndexWriter writer;
	static Analyzer analyzer;
	public static String[] pathIndex = {"index/simpleIKanalyzer",
			"index/simpleStandardAnalyzer", "index/simpleCJKAnalyzer",
			"index/simplePaodingAnalyzer", "index/simpleStandardAnalyzerEnglish",
			"index/simpleToy"}; //对应的index的位置 
	
	/*
	 * 获取对应的分词器
	 */
	private static Analyzer getAnalyzer() {
		Analyzer tmp = null;
		if (indexState == -1) {
			tmp = new IKAnalyzer();
		}
		if (indexState == 0) { //简单中文查询
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
	        	//tmp = new PaodingAnalyzer(); 
	        	break;
	        default:
	        	tmp = new IKAnalyzer(); 
	        }
		}
		if (indexState == 1) { //简单英文查询
        	tmp = new StandardAnalyzer(Version.LUCENE_35);
		}
		return tmp;
	}
	
	/*
	 * 对多域进行建索引
	 */
	public static void indexForFields(Integer count) throws CorruptIndexException, IOException {
		while (true) {
			String results[] = ReadDocs.getFieldsInfoperDoc(myfieldsName);
			//System.out.println(temp);
			if (results == null) {
				break;
			} else {
				Document doc = new Document();  
				for (int i = 0; i < myfieldsName.length; i++) {
					if (beAnalyzed[i]) {
						doc.add(new Field(myfieldsName[i], results[i], Field.Store.YES, Field.Index.ANALYZED));
					} else {
						doc.add(new Field(myfieldsName[i], results[i], Field.Store.YES, Field.Index.NOT_ANALYZED));
					}
				}
				writer.addDocument(doc); 
				count++;
				if (count % 1000 == 0) {
					System.out.println("doc "+count);
					//System.out.println(temp);
				}
			}
		}
	}
	
	public static void indexForToyModel(Integer count) throws CorruptIndexException, IOException {
		while (true) {
			String totalResult = ReadDocs.getTotalInfoPerDoc();
			//System.out.println(temp);
			if (totalResult == null) {
				break;
			} else {
				Document doc = new Document();  
				doc.add(new Field("total", totalResult, Field.Store.YES, Field.Index.ANALYZED));
				writer.addDocument(doc); 
				count++;
				if (count % 1000 == 0) {
					System.out.println("doc "+count);
					//System.out.println(temp);
				}
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws IOException {  
        long startTime = System.currentTimeMillis();  
        System.out.println("*****************检索开始**********************");    
        Directory directory = FSDirectory.open(new File(pathIndex[indexPath]));  
        analyzer = getAnalyzer();  
        System.out.println(analyzer.toString());
        IndexWriterConfig iwc = new IndexWriterConfig(Version.LUCENE_35, analyzer);
        iwc.setOpenMode(OpenMode.CREATE);
        writer = new IndexWriter(directory, iwc);    
        
        ReadDocs.startRead("import/CNKI_journal_v2.txt");
		Integer count = new Integer (0);
		
		if (indexState == -1) {
			TestIndexWriter.indexForToyModel(count);
		} else {
			if (indexState == 0) {
				myfieldsName = TestIndexWriter.fieldsName;
			} 
			if (indexState == 1) {
				myfieldsName = TestIndexWriter.fieldsNameEnglish;
			}
			indexForFields(count);
		}
		
		
		System.out.println("total docs num:"+count); 
        writer.close(); // 这里可以提前关闭，因为dictory 写入内存之后 与IndexWriter 没有任何关系了  
        System.out.println("succeed to be written to files"); 
  
 /*       IndexSearcher searcher = new IndexSearcher(directory);   
         Query query = new TermQuery(new Term("题名", "结构"));  
          
        TopDocs rs = searcher.search(query, null, 10);  
        long endTime = System.currentTimeMillis();  
        System.out.println("总共花费" + (endTime - startTime) + "毫秒，检索到" + rs.totalHits + "条记录。");  
        for (int i = 0; i < rs.scoreDocs.length; i++) {  
            // rs.scoreDocs[i].doc 是获取索引中的标志位id, 从0开始记录  
            Document firstHit = searcher.doc(rs.scoreDocs[i].doc);  
            System.out.println(firstHit.getField("题名").stringValue());   
        }  
        writer.close();  
        directory.close();  
        System.out.println("*****************检索结束**********************");   */
    }  
}
