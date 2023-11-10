package ie.tcd.dalyc24;

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

public class QueryIndex
{

	// the location of the search index
	private static String TOPICS_DIRECTORY = "../topics";
	
	// Limit the number of search results we get
	private static int MAX_RESULTS = 10;
	
	public static void main(String[] args) throws IOException, ParseException
	{
		parseNonStandardXml(TOPICS_DIRECTORY);
	}

	private static void parseNonStandardXml(String filePath) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder xmlContent = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                xmlContent.append(line.trim());
            }

            // Use regular expressions to extract information
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
        Query query = createQuery(title, description, narrative);
        System.out.println("Query: " + query.toString());
    	} catch (ParseException e) {
          e.printStackTrace();
    	}
    }

    private static Query createQuery(String title, String description, String narrative) throws ParseException {
        String query = String.format("title:\"%s\"^2.0 OR description:\"%s\"^1.5 OR narrative:\"%s\"", title, description, narrative);
       
	QueryParser parser = new QueryParser("title", new StandardAnalyzer());
        return parser.parse(expandedQuery);
    }
    
    private static String extractTagContent(String element, String tagName) {
        Pattern pattern = Pattern.compile("<" + tagName + ">(.*?)<");
        Matcher matcher = pattern.matcher(element);

        String contentWithPrefix =  matcher.find() ? matcher.group(1).trim() : "";
	return contentWithPrefix.replaceAll("^\\w+:\\s*", "").trim();
    }
	
}
