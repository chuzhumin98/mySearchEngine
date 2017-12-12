package word2vec;


import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import java.util.Set;
import java.util.TreeSet;

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
				//model.loadJavaModelTxt("word2vec/Skipgram_data");
    			model.loadJavaModelTxt("word2vec/SkipgramSmall_data");
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

	
	public static void main(String[] args) throws IOException {
		ReadModel w1 = ReadModel.getInstance();
		System.out.println(w1.distance("中国"));
        
        System.out.println(w1.distance("改革"));
        
        System.out.println(w1.distance("中心"));
        
        System.out.println(w1.distance("江泽民"));
        
        System.out.println(w1.distance("记者"));
	}
}
	