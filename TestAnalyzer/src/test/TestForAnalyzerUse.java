package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Token;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.cn.ChineseAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.mira.lucene.analysis.IK_CAnalyzer;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.shentong.search.analyzers.PinyinNGramAnalyzer;

import net.paoding.analysis.analyzer.PaodingAnalyzer;


public class TestForAnalyzerUse {
	public static void main(String[] args) {
	       Analyzer analyzer = null;
	       //analyzer = new IKAnalyzer();  //需要单独使用
	       analyzer = new IK_CAnalyzer();
	       //analyzer = new StandardAnalyzer();
	       //analyzer = new ChineseAnalyzer();
	       //analyzer = new PaodingAnalyzer(); 
	       //analyzer = new CJKAnalyzer();
	       //SentenceTokenizer tmp = null;
	       //analyzer = new PinyinNGramAnalyzer();
	       String docText = "测试一下,真是厉害";
	      
	       TokenStream tokenStream = analyzer.tokenStream(docText, new StringReader(docText));
	       try {
	           Token t;
	           //System.out.println(docText);
	           while ((t = tokenStream.next()) != null)
	           {
	               System.out.println(t);
	               //System.out.println(t.termLength());
	               //System.out.println(t.term());
	           }
	       } catch (IOException e) {
	           e.printStackTrace();
	       } 
	}
}
