package word2vec;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.TreeSet;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

public class LoadModel {
	public static LoadModel model = null;
	public static final int topNSize = 3; //设置结果的个数
    public HashMap<String, float[]> wordMap = new HashMap<String, float[]>();
    public int words;
    public int size;
    public static LoadModel getInstance() {
    	if (model == null) {
    		model = new LoadModel();
    		try {
				//model.loadJavaModelTxt("word2vec/Skipgram_data");
    			model.loadModelBin("word2vec/smallmodel.bin");
				System.out.println("succeed load skip-gram model!");
			} catch (Exception e) {
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
	public void loadModelTxt(String path) throws IOException {
		try (Scanner input = new Scanner(new File(path))) {
			float vector = 0;

			String key = null;
			float[] value = null;
			while (input.hasNextLine()) {
				String inputLine = input.nextLine();
				JSONObject json1 = JSONObject.fromObject(inputLine);
				key = json1.getString("term");
				double len = 0;
				JSONArray jsar1= JSONArray.fromObject(json1.get("vector"));
				size = jsar1.size();
				value = new float[size];
				for (int j = 0; j < size; j++) {
					vector = (float) jsar1.getDouble(j);
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
			this.words = wordMap.size();
			System.out.println("words:"+words);
			System.out.println("size:"+size);
		}
	}
	
	   /** 
     * 对二进制文件比较常见的类有FileInputStream，DataInputStream 
     * BufferedInputStream等。类似于DataOutputStream，DataInputStream 
     * 也提供了很多方法用于读入布尔型、字节、字符、整形、长整形、短整形、 
     * 单精度、双精度等数据。 
     */  
    @SuppressWarnings("deprecation")
	public void loadModelBin(String fileName) {  
        int sum=0;  
        try {  
            DataInputStream in=new DataInputStream(  
                               new BufferedInputStream(  
                               new FileInputStream(fileName)));  
            float vector = 0;

			String key = null;
			float[] value = null;
			String inputLine;
			this.words = in.readInt();
			this.size = in.readInt();
			for (int i = 0; i < words; i++) {
				key = in.readUTF();
				value = new float[size];
				double len = 0;
				for (int j = 0; j < size; j++) {
					vector = (float) in.readDouble();
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
			this.words = wordMap.size();
			System.out.println("words:"+words);
			System.out.println("size:"+size); 
            in.close();  
        } catch (Exception e)  
        {  
            e.printStackTrace();  
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
	
	public static void main(String[] args) throws IOException {
		long startTime = System.currentTimeMillis();
		LoadModel w1 = LoadModel.getInstance();

		System.out.println(w1.distance("中国"));
		System.out.println(w1.distance("改革"));      
		System.out.println(w1.distance("中心"));
		System.out.println(w1.distance("江泽民"));
		System.out.println(w1.distance("记者"));
		long endTime = System.currentTimeMillis();  
        System.out.println("总共花费" + (endTime - startTime) + "毫秒."); 
	}

}
