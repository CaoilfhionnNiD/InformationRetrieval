package com.example;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.analysis.morfologik.MorfologikAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.misc.SweetSpotSimilarity;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.analysis.CharArraySet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.search.BooleanQuery;
import java.util.Scanner;
import java.util.Arrays;

import static com.example.news_sources.FederalRegisterHandler.getFedRegisterDocs;
import static com.example.news_sources.FederalRegisterHandler.loadFilesAndExtractInfoForFederalRegister;
import static com.example.news_sources.FinancialTimesHandler.getFinancialTimesDocs;
import static com.example.news_sources.ForeignBroadcastHandler.getForeignBroadcastDocs;
import static com.example.news_sources.ForeignBroadcastHandler.loadFilesAndExtractInfoForForeignBroadcast;
import static com.example.news_sources.FinancialTimesHandler.loadFilesAndExtractInfoForFinancialTimes;
import static com.example.news_sources.LosAngelesTimesHandler.getLosAngelesTimesDocs;
import static com.example.news_sources.LosAngelesTimesHandler.loadFilesAndExtractInfoForLosAngelesTimes;
import static com.example.QueryIndex.loadQueries;

public class Lucenefer {

    private static List<Document> finTimesDocs = new ArrayList<>();
    private static List<Document> fedRegisterDocs = new ArrayList<>();
    private static List<Document> laTimesDocs = new ArrayList<>();
    private static List<Document> fbisDocs = new ArrayList<>();

    private static List<CustomBooleanQuery> queries = new ArrayList<>();
    private static WordEmbeddingModel wordEmbeddingModel;

    private final static Path currentRelativePath = Paths.get("").toAbsolutePath();
    private final static String absPathToSearchResults = String.format("%s/queryResults", currentRelativePath);
    private final static String absPathToFinTimes = String.format("%s/Assignment Two/ft", currentRelativePath);
    private final static String absPathToFedRegister = String.format("%s/Assignment Two/fr94", currentRelativePath);
    private final static String absPathToLaTimes = String.format("%s/Assignment Two/latimes", currentRelativePath);
    private final static String absPathToFBIS = String.format("%s/Assignment Two/fbis", currentRelativePath);
    private final static String absPathToIndex = String.format("%s/Index", currentRelativePath);
    private static Analyzer analyzer = null;
    private static Similarity similarityModel = null;

    private static int MAX_SEARCH_RESULT = 1000;
    private static List<String> stopWords = Arrays.asList(
            "a", "an", "and", "are", "as", "at", "be", "but", "by", "for", "if", "in", "into", "is",
            "it", "no", "not", "of", "on", "or", "such", "that", "the", "their", "then", "there",
            "these", "they", "this", "to", "was", "will", "with");

    private static CharArraySet ENGLISH_STOP_WORDS_SET = new CharArraySet(stopWords, false);

    public static void main(String[] args) throws ParseException, IOException {
        System.out.println("Please select the type of Similarity:\n 1 for BM25Similarity()\n " +
                "2 for CustomSimilarity()");
        Scanner myObj = new Scanner(System.in);
        String similarityChosen = myObj.nextLine();
        String similarity = null;

        switch (similarityChosen) {
            case "1":
                similarity = "BM25";
                similarityModel = new BM25Similarity();
                System.out.println("Selected Retrieval Model is: BM25Similarity()");
                break;
            case "2":
                similarity = "HybridSimilarity";
                similarityModel = new HybridSimilarity();
                System.out.println("Selected Retrieval Model is: HybridSimilarity()");
                break;
            default:
                similarity = "BM25";
                similarityModel = new BM25Similarity();
                System.out.println("Selected Default Retrieval Model: BM25Similarity()");
        }

        System.out.println("Please select the type of Analyzer for Index Writer:\n 1 for EnglishAnalyzer()\n " +
                "2 for CUSTOMAnalyzer()\n");

        Scanner myObjIndex = new Scanner(System.in);
        String analyzerChosen = myObjIndex.nextLine();

        String indexWriter = null;
        switch (analyzerChosen) {
            case "1":
                indexWriter = "ENGLISH";
                analyzer = new EnglishAnalyzer(ENGLISH_STOP_WORDS_SET);
                System.out.println("Selected Analyzer for Index Writer is: EnglishAnalyzer()");

                break;
            case "2":
                indexWriter = "CUSTOM";
                analyzer = new CustomAnalyzer();
                System.out.println("Selected Analyzer for Index Writer is: CUSTOMAnalyzer()");
                break;
            default:
                indexWriter = "CUSTOM";
                analyzer = new CustomAnalyzer();
                System.out.println("Selected Default Analyzer for Index Writer is: CUSTOMAnalyzer()");

                myObj.close();
                myObjIndex.close();
        }

        System.out.println(String.format("Ranking model: %s\t Analyzer:%s", similarity, indexWriter));

        Directory directory;

        if (!new File(absPathToIndex).exists()) {
            directory = FSDirectory.open(Paths.get(absPathToIndex));
            loadDocs();
            indexDocuments(similarityModel, analyzer, directory);
        } else {
            System.out.println("Using previously loaded data!");
            directory = FSDirectory.open(Paths.get(absPathToIndex));
        }
        System.out.println("loading and executing queries");
        wordEmbeddingModel = new WordEmbeddingModel("glove.6B.50d.txt");
        executeQueries(directory, wordEmbeddingModel);
        analyzer.close();
        try {
            directory.close();
        } catch (IOException e) {
            System.out.println("ERROR: an error occurred when closing the directory!");
            System.out.println(String.format("ERROR MESSAGE: %s", e.getMessage()));
        }

    }

    private static void loadDocs() throws IOException {
        System.out.println("loading foreign broadcast information service documents");
        loadFilesAndExtractInfoForForeignBroadcast(absPathToFBIS);
        fbisDocs = getForeignBroadcastDocs();
        System.out.println("loaded foreign broadcast information service documents");

        System.out.println("loading la times documents");
        loadFilesAndExtractInfoForLosAngelesTimes(absPathToLaTimes);
        laTimesDocs = getLosAngelesTimesDocs();
        System.out.println("loaded la times documents");

        System.out.println("loading financial times documents");
        loadFilesAndExtractInfoForFinancialTimes(absPathToFinTimes);
        finTimesDocs = getFinancialTimesDocs();
        System.out.println("loaded financial times documents");

        System.out.println("loading federal register documents");
        loadFilesAndExtractInfoForFederalRegister(absPathToFedRegister);
        fedRegisterDocs = getFedRegisterDocs();
        System.out.println("loaded federal register documents");
    }

    private static void indexDocuments(Similarity similarity, Analyzer analyzer, Directory directory) {
        IndexWriter indexWriter;
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);

        try {
            indexWriter = new IndexWriter(directory, indexWriterConfig);

            System.out.println("indexing financial times document collection");
            for (Document doc : finTimesDocs) {
                indexWriter.addDocument(doc);
            }

            System.out.println("indexing federal register document collection");
            for (Document doc : fedRegisterDocs) {
                indexWriter.addDocument(doc);
            }

            System.out.println("indexing la times document collection");
            for (Document doc : laTimesDocs) {
                indexWriter.addDocument(doc);
            }

            System.out.println("indexing foreign broadcast information service document collection");
            for (Document doc : fbisDocs) {
                indexWriter.addDocument(doc);
            }

            try {
                indexWriter.close();
            } catch (IOException e) {
                System.out.println("ERROR: an error occurred when closing the index from the directory!");
                System.out.println(String.format("ERROR MESSAGE: %s", e.getMessage()));
            }

        } catch (IOException e) {
            System.out.println("ERROR: An error occurred when trying to instantiate a new IndexWriter");
            System.out.println(String.format("ERROR MESSAGE: %s", e.getMessage()));
        }
    }

    private static void executeQueries(Directory directory, WordEmbeddingModel wordEmbeddingModel)
            throws ParseException {
        try {
            IndexReader indexReader = DirectoryReader.open(directory);
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            indexSearcher.setSimilarity(similarityModel);

            queries = loadQueries(analyzer, wordEmbeddingModel);
            PrintWriter writer = new PrintWriter(absPathToSearchResults, "UTF-8");

            for (CustomBooleanQuery customQuery : queries) {
                BooleanQuery query = customQuery.getBooleanQuery();

                ScoreDoc[] hits = indexSearcher.search(query, MAX_SEARCH_RESULT).scoreDocs;
                String analyzerName = analyzer.getClass().getName();

                for (int i = 0; i < hits.length; i++) {
                    Document hitDoc = indexSearcher.doc(hits[i].doc);
                    String resultLine = customQuery.getQueryNumber() + "\t" + "0" + "\t" + hitDoc.get("DocNo") + "\t"
                            + (i + 1) + "\t" + hits[i].score + "\t" + analyzerName;
                    writer.println(resultLine);
                }
            }
            writer.close();

        } catch (IOException e) {
            System.out.println("ERROR: an error occurred when instantiating the printWriter!");
            System.out.println(String.format("ERROR MESSAGE: %s", e.getMessage()));
        }
    }

}
