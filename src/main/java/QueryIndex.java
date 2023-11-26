package com.example;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import org.apache.lucene.index.Term;
import org.apache.lucene.index.DirectoryReader;

import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.queryparser.classic.ParseException;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.BooleanClause;

public class QueryIndex
{

	private static String TOPICS_DIRECTORY = "topics";
	private static WordEmbeddingModel wordEmbeddingModel;	

	private static List<BooleanQuery> queries = new ArrayList<>();
	private static int MAX_RESULTS = 10;
	private static Analyzer analyzer;

	public static List<BooleanQuery> loadQueries(Analyzer chosenAnalyzer, WordEmbeddingModel wem) throws IOException, ParseException
	{
		analyzer = chosenAnalyzer;
		wordEmbeddingModel = wem;
		System.out.println("QUERIES");
		//wordEmbeddingModel = new WordEmbeddingModel("glove.6B.50d.txt");
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
        BooleanQuery query = createQuery(title, description, narrative);
	//System.out.println(query.toString());
	queries.add(query);
    	} catch (ParseException e) {
          e.printStackTrace();
    	}
    }

    private static BooleanQuery createQuery(String title, String description, String narrative) throws ParseException {
        
	String expandedTitle = expandQuery(title);
       // String expandedDescription = expandQuery(description);

        String query = String.format("Headline:\"%s\"^2.0 OR Text:\"%s\"^1.5 OR narrative:\"%s\"", expandedTitle, description, narrative);
       
	QueryParser queryParser = new MultiFieldQueryParser(new String[]{"Headline", "Text"}, analyzer);
	Query titleQuery = queryParser.parse(QueryParser.escape(expandedTitle));
	Query descriptionQuery = queryParser.parse(QueryParser.escape(description));
        
	BooleanQuery.Builder booleanQuery = new BooleanQuery.Builder();
	booleanQuery.add(new BoostQuery(titleQuery, (float) 1), BooleanClause.Occur.SHOULD);
	booleanQuery.add(new BoostQuery(descriptionQuery, (float) 2), BooleanClause.Occur.SHOULD);
	return booleanQuery.build();
    }

	private static String expandQuery(String input)
	{
		String[] terms = input.split("\\s+");

		StringBuilder expandedQuery = new StringBuilder();
		for(int i = 0; i < terms.length; i++)
		{
			String term = terms[i];
			expandedQuery.append(term).append(" ");
			if (wordEmbeddingModel.hasWord(term)) {
                		float[] embedding = wordEmbeddingModel.getEmbedding(term);
               		        String similarTerm = wordEmbeddingModel.findSimilarTerm(term, embedding);
                		if (!similarTerm.equals(term)) {
             		     		expandedQuery.append(similarTerm).append(" ");
            			}
            		}
		}
		return expandedQuery.toString().trim();

	}	
    private static String extractTagContent(String element, String tagName) {
        Pattern pattern = Pattern.compile("<" + tagName + ">(.*?)<");
        Matcher matcher = pattern.matcher(element);

        String content;
	if(matcher.find())
	{
		 content = matcher.group(1).trim();
	}
	else content = "";
	return content.replaceAll("^\\w+:\\s*", "").trim();
    }
	
}

