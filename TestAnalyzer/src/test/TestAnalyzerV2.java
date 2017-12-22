package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.ChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.mira.lucene.analysis.IK_CAnalyzer;
import org.mira.lucene.analysis.MIK_CAnalyzer;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.shentong.search.analyzers.PinyinNGramAnalyzer;

import net.paoding.analysis.analyzer.PaodingAnalyzer;

import com.chenlb.mmseg4j.analysis.ComplexAnalyzer;
import com.chenlb.mmseg4j.analysis.MMSegAnalyzer;  

public class TestAnalyzerV2 {
	@SuppressWarnings("resource")
	public static void main(String[] args) {
	       Analyzer analyzer = null;              
	       
	       analyzer = new StandardAnalyzer();
	      // analyzer = new ChineseAnalyzer();
	       //analyzer = new PaodingAnalyzer(); 
	       //analyzer = new CJKAnalyzer();
	       
	       //analyzer = new MIK_CAnalyzer();
	       //analyzer = new IK_CAnalyzer();
	       System.out.println("for IK_CAnalyzer:");
	       
	       long startTime = System.currentTimeMillis(); 
	       Map<String,Integer> termMap = new HashMap<String, Integer>();
	       int wordNum = 0; //总的词数
	       double wordLenSum = 0.0; //总的词长
	       double termLenSum = 0.0; //总的term长
	       int countDoc = 0;
	       while (true) {
	    	  // String docText = ReadDocs.getTotalInfoPerDoc();
	 	      String docText = ReadDocs.getFieldInfoperDoc("摘要");
	    	   if (docText == null) break;
	    	   
		       TokenStream tokenStream = analyzer.tokenStream(docText, new StringReader(docText));
		       try {
		           Token t;
		           //System.out.println(docText);
		           while ((t = tokenStream.next()) != null)
		           {
		               //System.out.println(t);
		               String term = t.term();
		               if (!termMap.containsKey(term)) {
		            	   termMap.put(term, 1);
		            	   termLenSum += term.length();
		               } else {
		            	   int count = termMap.get(term);
		            	   termMap.put(term, count+1);
		               }
		               wordNum++;
		               wordLenSum += term.length();
		               //System.out.println(t.termLength());
		               //System.out.println(t.term());
		           }
		       } catch (IOException e) {
		           e.printStackTrace();
		       }  
		       countDoc++;
		       if (countDoc % 1000 == 0) {
		    	   System.out.println("process "+countDoc);
		       }
	       }
	       
	       long endTime = System.currentTimeMillis();
	       System.out.println("term num:"+termMap.size());
	       System.out.println("word num:"+wordNum);
	       System.out.println("average word len:"+wordLenSum/(double)wordNum);
	       System.out.println("average term len:"+termLenSum/(double)termMap.size());
	       System.out.println("cost time:"+(endTime-startTime)+"ms");
	}
}
