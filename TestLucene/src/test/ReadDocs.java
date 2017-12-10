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
	 * 仅读取某个文档中的题名内容
	 */
	public static String getNameInfoperDoc() {
		if (!hasSet) {
			ReadDocs.startRead("import/CNKI_journal_v2.txt");
		}
		String totalString = ReadDocs.getTotalInfoPerDoc();
		if (totalString == null) {
			return null;
		}
		String total = ""; //所有的题名信息
		String forCompare = "<" + "题名";
		BufferedReader br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(totalString.getBytes(Charset.forName("utf8"))), Charset.forName("utf8")));  
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
	
	public static void main(String[] args) {
		ReadDocs.startRead("import/CNKI_journal_v2.txt");
		while (true) {
			String name = ReadDocs.getNameInfoperDoc();
			if (name == null) {
				break;
			} else {
				System.out.println(name);
			}
		}
		/*int count = 0;
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
		System.out.println("total docs num:"+count);  */
	} 
}
