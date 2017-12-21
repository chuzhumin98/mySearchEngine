package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.cn.smart.SmartChineseAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.util.Version;
import org.mira.lucene.analysis.MIK_CAnalyzer;
import org.wltea.analyzer.lucene.IKAnalyzer;

import com.chenlb.mmseg4j.analysis.ComplexAnalyzer;
import com.chenlb.mmseg4j.analysis.MMSegAnalyzer;
import com.chenlb.mmseg4j.analysis.MaxWordAnalyzer;
import com.chenlb.mmseg4j.analysis.SimpleAnalyzer;
import com.shentong.search.analyzers.PinyinNGramAnalyzer;

public class TestAnalyzerV3 {
	public static void main(String[] args) throws IOException {
		 String filePath = "test.txt";  
	        String news="我们来测试一下分词效果";
	       System.out.println(news);  
	       Analyzer analyzer = null;
	       analyzer = new IKAnalyzer();
	       
	       analyzer = new ComplexAnalyzer();         
	       analyzer = new MMSegAnalyzer(); 
	       analyzer = new SimpleAnalyzer();
	       analyzer = new MaxWordAnalyzer();
	       
	       analyzer = new SmartChineseAnalyzer(Version.LUCENE_35);
	       
	     //analyzer = new PinyinNGramAnalyzer(); //需要Lucene4的
	       
	       StringReader reader = new StringReader(news);  
	       TokenStream ts = analyzer.tokenStream("", reader);  
	       CharTermAttribute term = ts.getAttribute(CharTermAttribute.class);  
	       try {
			while(ts.incrementToken()){  
			       System.out.print(term.toString()+"|");  
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	       analyzer.close();  
	       reader.close();  
	}
}
