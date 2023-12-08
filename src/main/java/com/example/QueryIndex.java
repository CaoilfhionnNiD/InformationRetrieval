package com.example;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import org.apache.lucene.analysis.Analyzer;

import org.apache.lucene.search.Query;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.BooleanClause;

public class QueryIndex {

	private static String TOPICS_DIRECTORY = "topics";
	private static WordEmbeddingModel wordEmbeddingModel;

	private static List<CustomBooleanQuery> queries = new ArrayList<>();
	private static Analyzer analyzer;

	public static List<CustomBooleanQuery> loadQueries(Analyzer chosenAnalyzer, WordEmbeddingModel wem)
			throws IOException, ParseException {
		analyzer = chosenAnalyzer;
		wordEmbeddingModel = wem;
		parseNonStandardXml(TOPICS_DIRECTORY);
		return queries;
	}

	private static void parseNonStandardXml(String filePath) throws IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			StringBuilder xmlContent = new StringBuilder();
			String line;

			while ((line = reader.readLine()) != null) {
				xmlContent.append(line.trim());
			}

			Pattern topPattern = Pattern.compile("<top>.*?</top>");
			Matcher matcher = topPattern.matcher(xmlContent.toString());

			while (matcher.find()) {
				String topElement = matcher.group();
				extractInformation(topElement);
			}
		}
	}

	private static void extractInformation(String topElement) {

		String number = extractTagContent(topElement, "num");
		String title = extractTagContent(topElement, "title");
		String description = extractTagContent(topElement, "desc");
		String narrative = extractTagContent(topElement, "narr");

		try {
			CustomBooleanQuery query = createQuery(number, title, description, narrative);
			queries.add(query);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	private static CustomBooleanQuery createQuery(String number, String title, String description, String narrative)
			throws ParseException {

		String expandedTitle = expandQuery(title);
		// String expandedDescription = expandQuery(description);

		QueryParser queryParser = new MultiFieldQueryParser(new String[] { "Headline", "Text" }, analyzer);

		Query originalTitleQuery = queryParser.parse(title);

		Query expandedTitleQuery = queryParser.parse(QueryParser.escape(expandedTitle));
		Query descriptionQuery = queryParser.parse(QueryParser.escape(description));

		BooleanQuery.Builder booleanQueryBuilder = new BooleanQuery.Builder();
		booleanQueryBuilder.add(new BoostQuery(originalTitleQuery, (float) 2.0f), BooleanClause.Occur.MUST);
		booleanQueryBuilder.add(new BoostQuery(descriptionQuery, (float) 1.5f), BooleanClause.Occur.MUST);

		booleanQueryBuilder.add(new BoostQuery(expandedTitleQuery, 1.0f), BooleanClause.Occur.SHOULD);
		BooleanQuery booleanQuery = booleanQueryBuilder.build();

		return new CustomBooleanQuery(booleanQuery, number);
	}

	private static String expandQuery(String input) {
		String[] terms = input.split("\\s+");

		StringBuilder expandedQuery = new StringBuilder();
		for (int i = 0; i < terms.length; i++) {
			String term = terms[i];
			expandedQuery.append(term).append("^2.0 ");
			if (wordEmbeddingModel.hasWord(term)) {
				float[] embedding = wordEmbeddingModel.getEmbedding(term);
				String similarTerm = wordEmbeddingModel.findSimilarTerm(term, embedding);
				if (!similarTerm.equals(term)) {
					expandedQuery.append(similarTerm).append("^1.0 ");
				}
			}
		}
		return expandedQuery.toString().trim();

	}

	private static String extractTagContent(String element, String tagName) {
		Pattern pattern = Pattern.compile("<" + tagName + ">(.*?)<");
		Matcher matcher = pattern.matcher(element);

		String content;
		if (matcher.find()) {
			content = matcher.group(1).trim();
		} else
			content = "";
		return content.replaceAll("^\\w+:\\s*", "").trim();
	}

}
