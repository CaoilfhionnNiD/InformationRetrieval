package com.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.io.IOException;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.FlattenGraphFilter;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.miscellaneous.TrimFilter;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterGraphFilter;
import org.apache.lucene.analysis.ngram.NGramTokenFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
//import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.ClassicFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.util.CharsRef;
import org.tartarus.snowball.ext.EnglishStemmer;
import org.apache.lucene.analysis.CharArraySet;

public class CustomAnalyzer extends Analyzer {

        private List<String> stopWords =
        Arrays.asList(
            "a", "an", "and", "are", "as", "at", "be", "but", "by", "for", "if", "in", "into", "is",
            "it", "no", "not", "of", "on", "or", "such", "that", "the", "their", "then", "there",
            "these", "they", "this", "to", "was", "will", "with");

        private CharArraySet ENGLISH_STOP_WORDS_SET = new CharArraySet(stopWords, false);
	private final Path ppath = Paths.get("").toAbsolutePath();

	@Override
	protected TokenStreamComponents createComponents(String s) {
		Tokenizer tokenizer = new StandardTokenizer();
		TokenStream stream = new ClassicFilter(tokenizer);
		stream = new NGramTokenFilter(stream, 2, 3, true);
	//	TokenStream stream = new StandardFilter(tokenizer);

		stream = new WordDelimiterGraphFilter(stream, WordDelimiterGraphFilter.SPLIT_ON_NUMERICS |
            	WordDelimiterGraphFilter.GENERATE_WORD_PARTS | WordDelimiterGraphFilter.GENERATE_NUMBER_PARTS |
           	WordDelimiterGraphFilter.PRESERVE_ORIGINAL , null);

		stream = new FlattenGraphFilter(stream);
		stream = new LowerCaseFilter(stream);
                stream = new TrimFilter(stream);
    // Experiment with the order of the NGramTokenFilter and SynonymGraphFilter
    		//stream = new NGramTokenFilter(stream, 2, 3, true);

    // Adjust the order and configuration of the FlattenGraphFilter and SynonymGraphFilter
    		//stream = new FlattenGraphFilter(new SynonymGraphFilter(stream, wordExpansion(), true));
		/*		stream = new NGramTokenFilter(stream, 2, 3, true);

		stream = new FlattenGraphFilter(new WordDelimiterGraphFilter(stream, WordDelimiterGraphFilter.SPLIT_ON_NUMERICS |
				WordDelimiterGraphFilter.GENERATE_WORD_PARTS | WordDelimiterGraphFilter.GENERATE_NUMBER_PARTS |
				WordDelimiterGraphFilter.PRESERVE_ORIGINAL , null));
//      		stream = new FlattenGraphFilter(new SynonymGraphFilter(stream, wordExpansion(), true));
*/
		stream = new StopFilter(stream, ENGLISH_STOP_WORDS_SET);
		stream = new PorterStemFilter(stream);

		return new TokenStreamComponents(tokenizer, stream);
	}

    private SynonymMap wordExpansion() {
        SynonymMap wordExpansionMap = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(Paths.get(ppath.toString(), "src", "main", "java", "similar_words.txt").toString()))) {
            final SynonymMap.Builder builder = new SynonymMap.Builder(true);
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" - ");
                if (parts.length == 2) {
                    String word = parts[0];
                    String synonym = parts[1];
                    builder.add(new CharsRef(word), new CharsRef(synonym), true);
                }
            }
            wordExpansionMap = builder.build();
        } catch (IOException e) {
            System.out.println("IO ERROR: " + e.getLocalizedMessage() + " occurred when trying to create synonym map");
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getLocalizedMessage() + " occurred when trying to create synonym map");
        }
        return wordExpansionMap;
    }
}

