package test;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

public class ReadModel {
	public static ReadModel model = null;
	
    public HashMap<String, float[]> wordMap = new HashMap<String, float[]>();
    public int words;
    public int size;
    public static ReadModel getInstance() {
    	if (model == null) {
    		model = new ReadModel();
    		try {
				model.loadJavaModelTxt("word2vec/Skipgram_data");
				System.out.println("succeed load skip-gram model!");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    	return model;
    }
    
	/**
	 * 加载模型
	 * 
	 * @param path
	 *            模型的路径
	 * @throws IOException
	 */
	public void loadJavaModelTxt(String path) throws IOException {
		try (Scanner input = new Scanner(new File(path))) {
			
			words = input.nextInt();
			size = input.nextInt();
			float vector = 0;

			String key = null;
			float[] value = null;
			for (int i = 0; i < words; i++) {
				double len = 0;
				input.nextLine(); //空行			
				key = input.nextLine();
				//System.out.println(key);
				value = new float[size];
				for (int j = 0; j < size; j++) {
					vector = (float) input.nextDouble();
					//System.out.println(vector);
					len += vector * vector;
					value[j] = vector;
				}

				len = Math.sqrt(len);

				for (int j = 0; j < size; j++) {
					value[j] /= len; //归一化
				}
				wordMap.put(key, value);
			}

		}
	}
	
	public static void main(String[] args) throws IOException {
		ReadModel vec = new ReadModel();
	}
}
	