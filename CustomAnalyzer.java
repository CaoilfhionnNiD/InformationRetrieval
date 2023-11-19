package com.kerinb.IR_proj2_group14.RankAndAnalyzerFiles;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

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
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.synonym.SynonymGraphFilter;
import org.apache.lucene.analysis.synonym.SynonymMap;
import org.apache.lucene.util.CharsRef;
import org.tartarus.snowball.ext.EnglishStemmer;

public class CustomAnalyzer extends Analyzer {

	private final Path ppath = Paths.get("").toAbsolutePath();

	@Override
	protected TokenStreamComponents createComponents(String s) {
		final Tokenizer tokenizer = new StandardTokenizer();
		TokenStream stream = new StandardFilter(tokenizer);
		stream = new LowerCaseFilter(stream);
		stream = new TrimFilter(stream);
		stream = new NGramTokenFilter(stream, 2, 3);

		stream = new FlattenGraphFilter(new WordDelimiterGraphFilter(stream, WordDelimiterGraphFilter.SPLIT_ON_NUMERICS |
				WordDelimiterGraphFilter.GENERATE_WORD_PARTS | WordDelimiterGraphFilter.GENERATE_NUMBER_PARTS |
				WordDelimiterGraphFilter.PRESERVE_ORIGINAL , null));
		stream = new FlattenGraphFilter(new SynonymGraphFilter(stream, wordExpansion(), true));
		stream = new StopFilter(stream, StandardAnalyzer.ENGLISH_STOP_WORDS_SET);
		stream = new PorterStemFilter(stream);
		return new TokenStreamComponents(tokenizer, stream);
	}


	private SynonymMap wordExpansion() {
		SynonymMap wordExpansionMap = new SynonymMap(null, null, 0);
		try {
			final SynonymMap.Builder builder = new SynonymMap.Builder(true);

			// 读取并添加来自 similar_word.txt 的同义词
			BufferedReader reader = new BufferedReader(new FileReader(ppath + "/src/com/kerinb/IR_proj2_group14/RankAndAnalyzerFiles/similar_words.txt"));
			String line;
			while ((line = reader.readLine()) != null) {
				String[] parts = line.split(" - ");
				if (parts.length == 2) {
					String word = parts[0];
					String synonym = parts[1];
					builder.add(new CharsRef(word), new CharsRef(synonym), true);
				}
			}
			reader.close();
			wordExpansionMap = builder.build();
		} catch (Exception e) {
			System.out.println("ERROR: " + e.getLocalizedMessage() + "occurred when trying to create synonym map");
		}
		return wordExpansionMap;
	}
}
