package com.example;

import org.apache.lucene.search.similarities.SimilarityBase;
import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.Explanation;

public class HybridSimilarity extends SimilarityBase {
    private final float k1;
    private final float b;
    private final float lambda;
    private static final float DEFAULT_K1 = 1.2f;
    private static final float DEFAULT_B = 0.75f;
    private static final float DEFAULT_LAMBDA = 0.1f;

    // 无参数的构造函数
    public HybridSimilarity() {
        this(DEFAULT_K1, DEFAULT_B, DEFAULT_LAMBDA);
    }

    public HybridSimilarity(float k1, float b, float lambda) {
        this.k1 = k1;
        this.b = b;
        this.lambda = lambda;
    }

    @Override
    protected float score(BasicStats stats, float freq, float docLen) {
        // BM25计分部分
        float idf = (float) Math.log(1 + (stats.getNumberOfDocuments() - stats.getDocFreq() + 0.5) / (stats.getDocFreq() + 0.5));
        float tf = freq / (freq + k1 * (1 - b + b * docLen / stats.getAvgFieldLength()));
        float bm25Score = idf * tf;

        // LM Jelinek-Mercer计分部分
        float collectionProbability = (float) (stats.getTotalTermFreq() + 1) / (stats.getNumberOfFieldTokens() + 1);
        float documentProbability = freq / docLen;
        float lmScore = (float) Math.log(1 + (lambda * documentProbability + (1 - lambda) * collectionProbability) / collectionProbability);

        // 结合两个模型的得分
        return bm25Score + lmScore; // 这里我们简单地将两个得分相加
    }

    @Override
    public String toString() {
        return "Hybrid BM25 and LM Jelinek-Mercer Similarity";
    }

    @Override
    public Explanation explain(BasicStats stats, int doc, Explanation freq, float docLen) {
        float freqValue = freq.getValue();
        Explanation bm25Expl = Explanation.match(score(stats, freqValue, docLen),
                "BM25 part of the hybrid model");
        Explanation lmjExpl = Explanation.match(score(stats, freqValue, docLen),
                "LM Jelinek-Mercer part of the hybrid model");

        return Explanation.match(
                score(stats, freqValue, docLen),
                "final score combining BM25 and LM Jelinek-Mercer, computed as their sum",
                bm25Expl, lmjExpl);
    }
}
