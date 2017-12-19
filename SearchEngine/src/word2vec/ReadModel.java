package word2vec;


import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import java.util.Set;
import java.util.TreeSet;
import java.util.Map.Entry;

import net.sf.json.JSONObject;

public class ReadModel {
	public static ReadModel model = null;
	public static final int topNSize = 3; //设置结果的个数
    public HashMap<String, float[]> wordMap = new HashMap<String, float[]>();
    public int words;
    public int size;
    public static ReadModel getInstance() {
    	if (model == null) {
    		model = new ReadModel();
    		try {
				model.loadJavaModelTxt("word2vec/Skipgram_data");
    			//model.loadJavaModelTxt("word2vec/SkipgramSmall_data");
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
	
	
	public Set<WordEntry> distance(String queryWord) {

		float[] center = wordMap.get(queryWord);
		if (center == null) {
			return Collections.emptySet();
		}

		int resultSize = wordMap.size() < topNSize ? wordMap.size() : topNSize;
		TreeSet<WordEntry> result = new TreeSet<WordEntry>();

		double min = Float.MIN_VALUE;
		for (Map.Entry<String, float[]> entry : wordMap.entrySet()) {
			float[] vector = entry.getValue();
			float dist = 0;
			for (int i = 0; i < vector.length; i++) {
				dist += center[i] * vector[i];
			}

			if (dist > min) {
				result.add(new WordEntry(entry.getKey(), dist));
				if (resultSize < result.size()) {
					result.pollLast();
				}
				min = result.last().score;
			}
		}
		result.pollFirst();

		return result;
	}

	/*
	 * 改变模型的存储形式
	 */
	public void saveAsObject(String path) {
		try {
    	    PrintStream out = new PrintStream(new File(path));
    	    ReadModel w1 = ReadModel.getInstance();
   			int count = 0;
   			for (Map.Entry<String, float[]> entry : w1.wordMap.entrySet()) {
   				String term = entry.getKey();
   				float[] vector = entry.getValue();
   				JSONObject json1 = new JSONObject();
   				json1.put("term", term);
   				ArrayList<Float> arl1 = new ArrayList<Float>();
   				for (int i = 0; i < vector.length; i++) {
   					arl1.add(vector[i]);
   				}
   				json1.put("vector", arl1);
   				String strings = json1.toString();
   				out.println(strings);
   				//System.out.println(term+":"+w1.distance(term));
   				count++;
   				if (count % 1000 == 0) {
   					System.out.println("process "+count);
   				}
   			}
   			System.out.println("succeed written to file");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
	}
	
	/*
	 * 存储由word2vec建立的索引
	 */
	public void saveIndex(String path) {
	       try {
	    	    PrintStream out = new PrintStream(new File(path));
	    	    ReadModel w1 = ReadModel.getInstance();
	   			int count = 0;
	   			for (Map.Entry<String, float[]> entry : w1.wordMap.entrySet()) {
	   				String term = entry.getKey();
	   				Set<WordEntry> result = w1.distance(term);
	   				JSONObject json1 = new JSONObject();
	   				json1.put("term", term);
	   				ArrayList<JSONObject> arl1 = new ArrayList<JSONObject>();
	   				for (WordEntry item: result) {
	   					JSONObject json2 = new JSONObject();
	   					json2.put("word", item.name);
	   					json2.put("score", item.score);
	   					arl1.add(json2);
	   				}
	   				json1.put("words", arl1);
	   				String strings = json1.toString();
	   				out.println(strings);
	   				//System.out.println(term+":"+w1.distance(term));
	   				count++;
	   				if (count % 100 == 0) {
	   					System.out.println("process "+count);
	   				}
	   			}
	   			System.out.println("succeed written to file");
	        } catch (IOException e) {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
	        }
		
	}
	
	 //对于大量数据的写入，使用缓冲流BufferedOutputStream类可以提高效率  
    public void saveBinModel(String fileName) {    
        try {  
            DataOutputStream out=new DataOutputStream(  
                                 new BufferedOutputStream(  
                                 new FileOutputStream(fileName)));  
            ReadModel w1 = ReadModel.getInstance();
   			int count = 0;
   			out.writeInt(this.words);
   			out.writeInt(this.size);
   			for (Map.Entry<String, float[]> entry : w1.wordMap.entrySet()) {
   				String term = entry.getKey();
   				float[] vector = entry.getValue();
   				out.writeUTF(term);
   				for (int i = 0; i < vector.length; i++) {
   					out.writeDouble(vector[i]);
   				}
   				count++;
   				if (count % 10000 == 0) {
   					System.out.println("process "+count);
   				}
   			}
   			System.out.println("succeed written to file");
            out.close();  
        } catch (Exception e)  
        {  
            e.printStackTrace();  
        }  
    }  
	
	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis(); 
		ReadModel w1 = ReadModel.getInstance();
		//w1.saveAsObject("word2vec/smallmodel.txt");
		w1.saveBinModel("word2vec/bigmodel.bin");
		System.out.println(w1.distance("中国"));
		System.out.println(w1.distance("改革"));      
		System.out.println(w1.distance("中心"));
		System.out.println(w1.distance("江泽民"));
		System.out.println(w1.distance("记者"));
		long endTime = System.currentTimeMillis();  
        System.out.println("总共花费" + (endTime - startTime) + "毫秒."); 
	}
}
	