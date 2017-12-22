package test;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

public class ReadDocs {
	public static boolean hasSet = false;
	public static InputStreamReader isr;
	public static BufferedReader br;
	/*
	 * 读取内容前的准备工作
	 */
	public static boolean startRead(String path) {
		ReadDocs.hasSet = true;
		try {
			isr = new InputStreamReader(new FileInputStream(path), "utf-8");
			br = new BufferedReader(isr);
			System.out.println("has set for reading docs.");
			return true;
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}		
	}
	
	/*
	 * 读取某个文档中的所有内容
	 */
	public static String getTotalInfoPerDoc() { //全部结果输出时则返回null
		if (!hasSet) {
			ReadDocs.startRead("D:/workspace/SearchEngine/import/CNKI_journal_v2.txt");
		}
		String info = "";
		String temp;
		try {
			temp = br.readLine();
			if (temp == null) {
				return null; //已经没有可读的内容时返回null
			} else if (!temp.equals("<REC>")) {
				info += temp+"\n";
			}
			while (true) {
				temp = br.readLine();
				//System.out.println(temp);
				if (temp == null || temp.equals("<REC>")) { //判断是否结束循环
					break;
				}
				info += temp+"\n";
			}
			return info;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/*
	 * 针对某个特定文档的某个特定域的提取
	 */
	public static String getFieldperDoc(BufferedReader br, String fieldName) {
		String forCompare = "<" + fieldName;
		String total = ""; //所有与该域相关的信息
		while (true) {
			try {
				String temp = br.readLine();
				if (temp == null) {
					break;
				}
				String[] strings = temp.split(">=", 2); //前一部分是名称，后一部分是对应的类别
				if (strings[0].equals(forCompare)) {
					if (strings.length == 2) {
						total += strings[1];
					}		
					break;
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		while (true) {
			String temp = null;
			try {
				temp = br.readLine();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (temp == null) {
				break;
			}
			String[] strings = temp.split(">=", 2); //前一部分是名称，后一部分是对应的类别
			if (strings.length == 2) { //当能被切分开时，表示已经进入了下一部分的内容
				break;
			} else {
				total += temp;
			}
		}
		return total;
	}
	
	/*
	 * 仅读取某个文档中的特定域的内容
	 */
	public static String getFieldInfoperDoc(String fieldName) {
		if (!hasSet) {
			ReadDocs.startRead("D:/workspace/SearchEngine/import/CNKI_journal_v2.txt");
		}
		String totalString = ReadDocs.getTotalInfoPerDoc();
		if (totalString == null) {
			return null;
		}
		BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream
				(totalString.getBytes(Charset.forName("utf8"))), Charset.forName("utf8")));
		String total = ReadDocs.getFieldperDoc(br, fieldName);
		return total;
	}
	
	/*
	 * 仅读取某个文档中的一些特定域的内容
	 */
	public static String[] getFieldsInfoperDoc(String[] fieldsName) {
		// TODO Auto-generated method stub
		if (!hasSet) {
			ReadDocs.startRead("import/CNKI_journal_v2.txt");
		}
		String totalString = ReadDocs.getTotalInfoPerDoc();
		if (totalString == null) {
			return null;
		}
		String[] totals = new String [fieldsName.length];
		for (int i = 0; i < fieldsName.length; i++) {
			BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream
					(totalString.getBytes(Charset.forName("utf8"))), Charset.forName("utf8")));
			totals[i] = ReadDocs.getFieldperDoc(br, fieldsName[i]);
		}
		return totals;
	} 
	
	public static void main(String[] args) {
		try {
			ReadDocs.readWord2Vec();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//ReadDocs.startRead("import/CNKI_journal_v2.txt");
		//ReadDocs.testForFieldsRead();
	}
	
	/*
	 * 检测getTotalInfoPerDoc()方法的正确性
	 */
	private static void testForTotalRead() {
		int count = 0;
		while (true) {
			String temp = ReadDocs.getTotalInfoPerDoc();
			if (temp == null) {
				break;
			} else {
				count++;
				if (count % 5000 == 0) {
					System.out.println("doc "+count);
					//System.out.println(temp);
				}
			}
		}
		System.out.println("total docs num:"+count);  
	}
	
	/*
	 * 检测getFieldInfoperDoc()方法的正确性
	 */
	private static void testForFieldRead() {
		while (true) {
			String name = ReadDocs.getFieldInfoperDoc("题名");
			if (name == null) {
				break;
			} else {
				System.out.println(name);
			}
		}
	}
	
	/*
	 * 检测getFieldsInfoperDoc()方法的正确性
	 */
	private static void testForFieldsRead() {
		for (int i = 0; i < 10; i++) {
			String[] fieldsName = {"题名", "作者", "摘要", "基金", "不存在的"};
			String name[] = ReadDocs.getFieldsInfoperDoc(fieldsName);
			System.out.println("for the result " + (i+1));
			for (int j = 0; j < name.length; j++) {
				System.out.println(fieldsName[j]+":"+name[j]);
			}
		}
	}
	
	public static void readWord2Vec() throws IOException {
		try {
			//isr = new InputStreamReader(new FileInputStream("word2vec/"
			//		+ "news_12g_baidubaike_20g_novel_90g_embedding_64.model"), "ISO-8859-1");
			isr = new InputStreamReader(new FileInputStream("word2vec/"
					+ "Word60.model"), "GBK");
			br = new BufferedReader(isr);
			for (int i = 0; i < 4; i++) {
				System.out.println(br.readLine());
			}
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	
}
