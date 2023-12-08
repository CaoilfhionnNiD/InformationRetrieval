package com.example;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.en.EnglishPossessiveFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.miscellaneous.RemoveDuplicatesTokenFilter;
import java.util.Arrays;
import java.util.List;

public class CustomAnalyzer extends Analyzer {

    private final CharArraySet stopWords;

    public CustomAnalyzer() {
        super();
        List<String> stopWordsList = Arrays.asList(
                "a", "an", "and", "are", "as", "at", "be", "but", "by", "for", "if", "in", "into", "is",
                "it", "no", "not", "of", "on", "or", "such", "that", "the", "their", "then", "there",
                "these", "they", "this", "to", "was", "will", "with");
        stopWords = new CharArraySet(stopWordsList, false);
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        Tokenizer tokennizer = new StandardTokenizer();
        TokenStream result = new EnglishPossessiveFilter(tokennizer);

        result = new LowerCaseFilter(result);
        result = new StopFilter(result, stopWords);
        result = new PorterStemFilter(result);
        result = new RemoveDuplicatesTokenFilter(result);

        return new TokenStreamComponents(tokennizer, result);
    }
}