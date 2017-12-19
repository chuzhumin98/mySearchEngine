package test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.wltea.analyzer.lucene.IKAnalyzer;

public class TestForAnalyzerUse {
	public static void main(String[] args) {
		 String filePath = "test.txt";  
	        String news="我们来测试一下分词效果";
	       System.out.println(news);  
	       IKAnalyzer analyzer = new IKAnalyzer(true);  
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
