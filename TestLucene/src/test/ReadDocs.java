package test;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

public class ReadDocs {
	public static boolean hasSet = false;
	public static InputStreamReader isr;
	public static BufferedReader br;
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
	
	public static void main(String[] args) {
		ReadDocs.startRead("import/CNKI_journal_v2.txt");
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
}
