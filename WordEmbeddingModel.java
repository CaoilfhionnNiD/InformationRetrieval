package com.kerinb.Lucenefer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class WordEmbeddingModel {

    private final Map<String, float[]> wordEmbeddings;

    public WordEmbeddingModel(String filePath) {
        this.wordEmbeddings = loadModel(filePath);
    }

    private Map<String, float[]> loadModel(String filePath) {
        Map<String, float[]> embeddings = new HashMap<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ( (line = reader.readLine()) != null) {
	        String[] parts = line.split("\\s+");
                String word = parts[0];
		float[] vector = new float[parts.length - 1];
                for (int i = 1; i < parts.length; i++) {
                    try {
                        vector[i - 1] = Float.parseFloat(parts[i]);
                    } catch (NumberFormatException e) {
                    }
                }
                embeddings.put(word, vector);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return embeddings;
    }

    public boolean hasWord(String word) {
        return wordEmbeddings.containsKey(word);
    }

    public float[] getEmbedding(String word) {
        return wordEmbeddings.get(word);
    }

    public String findSimilarTerm(String inputTerm, float[] targetEmbedding) {
        String mostSimilarTerm = null;
        double maxSimilarity = Double.MIN_VALUE;

        for (Map.Entry<String, float[]> entry : wordEmbeddings.entrySet()) {
            String term = entry.getKey();
            float[] embedding = entry.getValue();

	    if (!term.equals(inputTerm)) {
            	double similarity = cosineSimilarity(targetEmbedding, embedding);

            	if (similarity > maxSimilarity && similarity != 0) {
                	maxSimilarity = similarity;
                	mostSimilarTerm = term;
            }
	  }
        }
	System.out.println(inputTerm);
	System.out.println(mostSimilarTerm);
	System.out.println("-------------------------");
        return mostSimilarTerm;
    }

        private double cosineSimilarity(float[] vector1, float[] vector2) {

	    if (vector1.length != vector2.length) {
   		 throw new IllegalArgumentException("Vector lengths do not match");
	    } else{
	
	double dotProduct = 0.0;
        double magnitude1 = 0.0;
        double magnitude2 = 0.0;

        for (int i = 0; i < vector1.length; i++) {
            dotProduct += vector1[i] * vector2[i];
            magnitude1 += Math.pow(vector1[i], 2);
            magnitude2 += Math.pow(vector2[i], 2);
        }

        magnitude1 = Math.sqrt(magnitude1);
        magnitude2 = Math.sqrt(magnitude2);

        if (magnitude1 == 0.0 || magnitude2 == 0.0) {
            return 0.0;
        }

        return dotProduct / (magnitude1 * magnitude2);
	    }
    }

//    public static void main(String[] args) {
        // Example usage
  //      String modelPath = "cc.en.300.vec";
    //    FastTextModel fastTextModel = new FastTextModel(modelPath);

      //  String wordToCheck = "information";
//        if (fastTextModel.hasWord(wordToCheck)) {
//            float[] embedding = fastTextModel.getEmbedding(wordToCheck);
            // Use the embedding as needed
//            System.out.println("Embedding for " + wordToCheck + ": " + java.util.Arrays.toString(embedding));
  //      } else {
      //      System.out.println(wordToCheck + " is not in the fastText model's vocabulary.");
    //    }
   // }
}


