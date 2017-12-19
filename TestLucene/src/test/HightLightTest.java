package test;

import java.io.IOException;  
import java.io.StringReader;  
  
import org.apache.lucene.analysis.Analyzer;  
import org.apache.lucene.analysis.TokenStream;  
import org.apache.lucene.document.Document;  
import org.apache.lucene.document.Field;  
import org.apache.lucene.index.CorruptIndexException;  
import org.apache.lucene.index.IndexWriter;  
import org.apache.lucene.index.IndexWriterConfig;  
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
import org.apache.lucene.store.Directory;  
import org.apache.lucene.store.LockObtainFailedException;  
import org.apache.lucene.store.RAMDirectory;  
import org.apache.lucene.util.Version;  
import org.wltea.analyzer.lucene.IKAnalyzer;  
  
public class HightLightTest {  
  
    static Directory dir = new RAMDirectory();  
    static Analyzer analyzer = new IKAnalyzer();  
    static String []bookNames = {"java开发手册","深入java开发","java基础","程序设计java开发","java案例精讲","hadoop项目实例汇总"};  
    public static void main(String []args)throws Exception {  
          
        index();  
        TopDocs topDocs = searcher("bookName","java");  
        System.out.println("共有记录"+topDocs.totalHits+"条");  
        System.out.println("-----------------------------------------------");  
        display(topDocs);  
        System.out.println("-----------------------------------------------");  
        highLightDisplay(topDocs,"java");  
    }  
      
    //把查询到的图书进行显示，并把关键字进行高亮显示  
    public static void highLightDisplay(TopDocs topDocs,String keyWords) throws CorruptIndexException, IOException, ParseException, InvalidTokenOffsetsException{  
          
        IndexSearcher searcher = new IndexSearcher(dir);  
        QueryParser queryParser = new QueryParser(Version.LUCENE_34,"bookName", analyzer);  
        Query query = queryParser.parse(keyWords);  
        ScoreDoc [] scoreDoc = topDocs.scoreDocs;  
          
        SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<font color='red'>", "</font>");      
        Highlighter highlighter = new Highlighter(simpleHTMLFormatter,new QueryScorer(query));  
        highlighter.setTextFragmenter(new SimpleFragmenter(1024));   
        for(int i=0;i<scoreDoc.length;i++){  
            Document doc = searcher.doc(scoreDoc[i].doc);  
            String text = doc.get("bookName");  
              
            TokenStream tokenStream = analyzer.tokenStream("bookName",new StringReader(text));     
            String highLightText = highlighter.getBestFragment(tokenStream, text);  
              
            System.out.println(highLightText);  
        }  
        searcher.close();  
    }  
      
    //把查询到的图书进行输出  
    public static void display(TopDocs topDocs) throws CorruptIndexException, IOException{  
          
        IndexSearcher searcher = new IndexSearcher(dir);  
        ScoreDoc [] scoreDoc = topDocs.scoreDocs;  
        for(int i=0;i<scoreDoc.length;i++){  
            Document doc = searcher.doc(scoreDoc[i].doc);  
            System.out.println(doc.get("bookName"));  
        }  
        searcher.close();  
    }  
      
    //按照关键字查询图书  
    public static TopDocs searcher(String fieldName,String keyWords) throws CorruptIndexException, IOException, ParseException{  
          
        IndexSearcher searcher = new IndexSearcher(dir);  
        QueryParser queryParser = new QueryParser(Version.LUCENE_34,fieldName,analyzer);  
        Query query = queryParser.parse(keyWords);  
        TopDocs topDocs = searcher.search(query, Integer.MAX_VALUE);  
        searcher.close();  
        return topDocs;  
    }  
      
    //对图书名称进行索引  
    public static void index() throws CorruptIndexException, LockObtainFailedException, IOException{  
          
        IndexWriter index = new IndexWriter(dir,new IndexWriterConfig(Version.LUCENE_34, analyzer));  
        for(int i=0;i<bookNames.length;i++){  
            Document doc = new Document();  
            doc.add(new Field("bookName",bookNames[i],Field.Store.YES,Field.Index.ANALYZED));  
            index.addDocument(doc);  
        }  
        index.optimize();  
        index.close();  
    }  
      
}  
