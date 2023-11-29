package com.example;
import org.apache.lucene.analysis.core.FlattenGraphFilter;
import org.apache.lucene.analysis.miscellaneous.TrimFilter;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilter;
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
import org.apache.lucene.analysis.shingle.ShingleFilter;
import java.io.IOException;
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
//        result = new TrimFilter(result);
//
//        result = new FlattenGraphFilter(new WordDelimiterGraphFilter(result, WordDelimiterGraphFilter.SPLIT_ON_NUMERICS |
//                WordDelimiterGraphFilter.GENERATE_WORD_PARTS | WordDelimiterGraphFilter.GENERATE_NUMBER_PARTS |
//                WordDelimiterGraphFilter.PRESERVE_ORIGINAL , null));
        result = new StopFilter(result, stopWords);
        result = new PorterStemFilter(result);
        //result = new ShingleFilter(result, 2, 2);
        result = new RemoveDuplicatesTokenFilter(result);

        // Add more token filters and configurations as needed

        return new TokenStreamComponents(tokennizer, result);
    }
}
