package com.kerinb.Lucenefer;

import com.kerinb.Lucenefer.HybridSimilarity;
import com.kerinb.Lucenefer.CustomAnalyzer;
import com.kerinb.Lucenefer.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.core.SimpleAnalyzer;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.WhitespaceAnalyzer;
import org.apache.lucene.analysis.morfologik.MorfologikAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.misc.SweetSpotSimilarity;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.TotalHitCountCollector;


import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.document.IntPoint;
import java.util.Scanner;

import static com.kerinb.Lucenefer.FederalRegisterHandler.*;
import static com.kerinb.Lucenefer.FinancialTimesHandler.getFinancialTimesDocs;
import static com.kerinb.Lucenefer.ForeignBroadcastHandler.getForeignBroadcastDocs;
import static com.kerinb.Lucenefer.ForeignBroadcastHandler.loadFilesAndExtractInfoForForeignBroadcast;
import static com.kerinb.Lucenefer.FederalRegisterHandler.loadFilesAndExtractInfoForFederalRegister;
import static com.kerinb.Lucenefer.FederalRegisterHandler.getFedRegisterDocs;
import static com.kerinb.Lucenefer.FinancialTimesHandler.loadFilesAndExtractInfoForFinancialTimes;
import static com.kerinb.Lucenefer.LosAngelesTimesHandler.getLosAngelesTimesDocs;
import static com.kerinb.Lucenefer.LosAngelesTimesHandler.loadFilesAndExtractInfoForLosAngelesTimes;

public class Lucenefer {

    private static List<Document> finTimesDocs = new ArrayList<>();
    private static List<Document> fedRegisterDocs = new ArrayList<>();
    private static List<Document> laTimesDocs = new ArrayList<>();
    private static List<Document> fbisDocs = new ArrayList<>();

    private final static Path currentRelativePath = Paths.get("").toAbsolutePath();
    private final static String absPathToSearchResults = String.format("%s/Files/queryResults", currentRelativePath);

    private final static String absPathToFinTimes = String.format("%s/DataSet/ft", currentRelativePath);
    private final static String absPathToFedRegister = String.format("%s/DataSet/fr94",currentRelativePath);
    private final static String absPathToLaTimes = String.format("%s/DataSet/latimes",currentRelativePath);
    private final static String absPathToFBIS = String.format("%s/DataSet/fbis",currentRelativePath);
    private final static String absPathToIndex = String.format("%s/Index", currentRelativePath);
    private static Analyzer analyzer = null;
    private static Similarity similarityModel = null;
    public static void main(String[] args) throws ParseException, IOException {
        //Retrieval model choose
        System.out.println("Please select the type of Similarity:\n 1 for BM25Similarity()\n " +
                "2 for BooleanSimilarity()\n 3 for ClassicSimilarity()\n " +
                "4 for LMDirichletSimilarity()\n 5 for SweetSpotSimilarity()\n 6 for CustomSimilarity()"
        );
        Scanner myObj = new Scanner(System.in);
        String similarityChosen = myObj.nextLine();

        String similarity = null;
        //Retrieval model choose
        switch (similarityChosen) {
            case "1":
                similarity = "BM25";
                similarityModel = new BM25Similarity();
                System.out.println("Selected Retrieval Model is: BM25Similarity()");
                break;
            case "2": {
                similarity = "BOOLEAN";
                similarityModel = new BooleanSimilarity();
                System.out.println("Selected Retrieval Model is: BooleanSimilarity()");
                break;
            }
            case "3":
                similarity = "CLASSIC";
                similarityModel = new ClassicSimilarity();
                System.out.println("Selected Retrieval Model is: ClassicSimilarity()");
                break;
            case "4":
                similarity = "LM_DIRICHLET";
                similarityModel = new LMDirichletSimilarity();
                System.out.println("Selected Retrieval Model is: LMDirichletSimilarity()");
                break;
            case "5":
                similarity = "SWEET_SPOT";
                similarityModel = new SweetSpotSimilarity();
                System.out.println("Selected Retrieval Model is: Sweet_SpotSimilarity()");
                break;
            case "6":
                similarity = "HybridSimilarity";
                similarityModel = new HybridSimilarity();
                System.out.println("Selected Retrieval Model is: HybridSimilarity()");
                break;
            default:
                similarity = "BM25";
                similarityModel = new BM25Similarity();
                System.out.println("Selected Default Retrieval Model: BM25Similarity()");
        }

        //Analyzer choose
        System.out.println("Please select the type of Analyzer for Index Writer:\n 1 for MORFOLOGICAnalyzer()\n " +
                "2 for STOPAnalyzer()\n 3 for SIMPLEAnalyzer()\n 4 for STANDARDAnalyzer()\n " +
                "5 for WHITESPACEAnalyzer()\n 6 for CUSTOMAnalyzer()\n"
        );
        Scanner myObjIndex = new Scanner(System.in);
        String analyzerChosen = myObjIndex.nextLine();

        String indexWriter = null;
        //Analyzer choose
        switch (analyzerChosen) {
            case "1":
                indexWriter = "MORFOLOGIC";
                analyzer = new MorfologikAnalyzer();
                System.out.println("Selected Analyzer for Index Writer is: MORFOLOGICAnalyzer()");
                break;
            case "2": {
                indexWriter = "STOP";
                analyzer = new StopAnalyzer(StandardAnalyzer.ENGLISH_STOP_WORDS_SET);
                System.out.println("Selected Analyzer for Index Writer is: STOPAnalyzer()");
                break;
            }
            case "3":
                indexWriter = "SIMPLE";
                analyzer = new SimpleAnalyzer();
                System.out.println("Selected Analyzer for Index Writer is: SimpleAnalyzer()");
                break;
            case "4":
                indexWriter = "STANDARD";
                analyzer = new StandardAnalyzer(StandardAnalyzer.ENGLISH_STOP_WORDS_SET);
                System.out.println("Selected Analyzer for Index Writer is: STANDARDAnalyzer()");
                break;

            case "5":
                indexWriter = "WHITESPACE";
                analyzer = new WhitespaceAnalyzer();
                System.out.println("Selected Analyzer for Index Writer is: WHITESPACEAnalyzer()");
                break;
            case "6":
                indexWriter = "CUSTOM";
                analyzer = new CustomAnalyzer();
                System.out.println("Selected Analyzer for Index Writer is: CUSTOMAnalyzer()");
                break;

            default:
                indexWriter = "CUSTOM";
                analyzer = new CustomAnalyzer();
                System.out.println("Selected Default Analyzer for Index Writer is: CUSTOMAnalyzer()");
        }



        System.out.println(String.format("Ranking model: %s\t Analyzer:%s", similarity, indexWriter));


        Directory directory;

        // so we don't need to parse &^ index everytime
        // THEREFORE everytime we want to test we need to delete the index
        // in terminal use rm -rf /Index/ to delete the index dir.
        if(!new File(absPathToIndex).exists()){
            directory = FSDirectory.open(Paths.get(absPathToIndex));
            loadDocs();
            indexDocuments(similarityModel, analyzer, directory);
        } else {
            System.out.println("Using previously loaded data!");
            directory = FSDirectory.open(Paths.get(absPathToIndex));
        }
        System.out.println("loading and executing queries");
        //executeQueries(directory);
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
        System.out.println("1");
        laTimesDocs= getLosAngelesTimesDocs();
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
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig();
        indexWriterConfig.setSimilarity(similarity).setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        try {
            indexWriter = new IndexWriter(directory, indexWriterConfig);
            indexWriter.deleteAll();

            System.out.println("indexing financial times document collection");
            indexWriter.addDocuments(finTimesDocs);

            System.out.println("indexing federal register document collection");
            indexWriter.addDocuments(fedRegisterDocs);

            System.out.println("indexing la times document collection");
            indexWriter.addDocuments(laTimesDocs);

            System.out.println("indexing foreign broadcast information service document collection");
            indexWriter.addDocuments(fbisDocs);

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
    private static void executeQueries(Directory directory) throws ParseException {
        try {
            IndexReader indexReader = DirectoryReader.open(directory);
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            indexSearcher.setSimilarity(similarityModel);


            QueryParser queryParser = new MultiFieldQueryParser(new String[]{"headline", "text"}, analyzer);
            PrintWriter writer = new PrintWriter(absPathToSearchResults, "UTF-8");


        } catch (IOException e) {
            System.out.println("ERROR: an error occurred when instantiating the printWriter!");
            System.out.println(String.format("ERROR MESSAGE: %s", e.getMessage()));
        }
    }



}
