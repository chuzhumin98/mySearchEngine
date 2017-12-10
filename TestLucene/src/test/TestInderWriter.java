package test;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.RAMDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class TestInderWriter {
	public static void main(String[] args) throws IOException {  
        long startTime = System.currentTimeMillis();  
        System.out.println("*****************检索开始**********************");    
        Directory directory = FSDirectory.open(new File("index"));   
        IKAnalyzer analyzer = new IKAnalyzer();  
        IndexWriter writer = new IndexWriter(directory, analyzer,true, IndexWriter.MaxFieldLength.UNLIMITED);  
        Document doc = new Document();    
        doc.add(new Field("name", "Chenghui", Field.Store.YES,Field.Index.ANALYZED));  
        doc.add(new Field("sex", "我是男生", Field.Store.YES,Field.Index.ANALYZED));  
        doc.add(new Field("dosometing", "I am learning lucene ",Field.Store.YES, Field.Index.ANALYZED));  
        writer.addDocument(doc);  
        writer.close(); // 这里可以提前关闭，因为dictory 写入内存之后 与IndexWriter 没有任何关系了  
  
        IndexSearcher searcher = new IndexSearcher(directory);  
         //Query query = new TermQuery(new Term("dosometing", "lucene"));  
         Query query = new TermQuery(new Term("sex", "男生"));  
         //Query query = new TermQuery(new Term("name", "cheng"));   
          
        TopDocs rs = searcher.search(query, null, 10);  
        long endTime = System.currentTimeMillis();  
        System.out.println("总共花费" + (endTime - startTime) + "毫秒，检索到" + rs.totalHits + "条记录。");  
        for (int i = 0; i < rs.scoreDocs.length; i++) {  
            // rs.scoreDocs[i].doc 是获取索引中的标志位id, 从0开始记录  
            Document firstHit = searcher.doc(rs.scoreDocs[i].doc);  
            System.out.println("name:" + firstHit.getField("name").stringValue());  
            System.out.println("sex:" + firstHit.getField("sex").stringValue());  
            System.out.println("dosomething:" + firstHit.getField("dosometing").stringValue());  
        }  
        writer.close();  
        directory.close();  
        System.out.println("*****************检索结束**********************");  
    }  
}
