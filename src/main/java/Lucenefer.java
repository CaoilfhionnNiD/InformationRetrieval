package com.example;

import com.example.HybridSimilarity;
import com.example.CustomAnalyzer;
//import com.kerinb.Lucenefer.*;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
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
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.similarities.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.search.TotalHitCountCollector;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.index.IndexableField;

import org.apache.lucene.index.Term;
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
import java.util.Arrays;

import static com.example.news_sources.FinancialTimesHandler.getFinancialTimesDocs;
import static com.example.news_sources.ForeignBroadcastHandler.getForeignBroadcastDocs;
import static com.example.news_sources.ForeignBroadcastHandler.loadFilesAndExtractInfoForForeignBroadcast;
import static com.example.news_sources.FederalRegisterHandler.loadFilesAndExtractInfoForFederalRegister;
import static com.example.news_sources.FederalRegisterHandler.getFedRegisterDocs;
import static com.example.news_sources.FinancialTimesHandler.loadFilesAndExtractInfoForFinancialTimes;
import static com.example.news_sources.LosAngelesTimesHandler.getLosAngelesTimesDocs;
import static com.example.news_sources.LosAngelesTimesHandler.loadFilesAndExtractInfoForLosAngelesTimes;
import static com.example.QueryIndex.loadQueries;
import org.apache.lucene.search.BooleanQuery;
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
    private final static String absPathToFedRegister = String.format("%s/Assignment Two/fr94",currentRelativePath);
    private final static String absPathToLaTimes = String.format("%s/Assignment Two/latimes",currentRelativePath);
    private final static String absPathToFBIS = String.format("%s/Assignment Two/fbis",currentRelativePath);
    private final static String absPathToIndex = String.format("%s/Index", currentRelativePath);
    private static Analyzer analyzer = null;
    private static Similarity similarityModel = null;
 
    private static int MAX_SEARCH_RESULT = 1000;
     private static List<String> stopWords =
        Arrays.asList(
            "a", "an", "and", "are", "as", "at", "be", "but", "by", "for", "if", "in", "into", "is",
            "it", "no", "not", "of", "on", "or", "such", "that", "the", "their", "then", "there",
            "these", "they", "this", "to", "was", "will", "with");

    private static CharArraySet ENGLISH_STOP_WORDS_SET = new CharArraySet(stopWords, false);

 
    public static void main(String[] args) throws ParseException, IOException {
        //Retrieval model choose
        System.out.println("Please select the type of Similarity:\n 1 for BM25Similarity()\n " +
                "2 for BooleanSimilarity()\n 3 for ClassicSimilarity()\n " +
                "4 for LMDirichletSimilarity()\n 5 for SweetSpotSimilarity()\n 6 for CustomSimilarity()"
        );
        Scanner myObj = new Scanner(System.in);
        String similarityChosen = myObj.nextLine();

	//wordEmbeddingModel = new WordEmbeddingModel("glove.6B.50d.txt");
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
                "5 for WHITESPACEAnalyzer()\n 6 for CUSTOMAnalyzer()\n 7 for EnglishAnalyzer\n"
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
                analyzer = new StopAnalyzer(ENGLISH_STOP_WORDS_SET);
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
                analyzer = new StandardAnalyzer(ENGLISH_STOP_WORDS_SET);
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
	    case "7":
                indexWriter = "English";
                this.analyzer = new EnglishAnalyzer(ENGLISH_STOP_WORDS_SET);
                System.out.println("Selected Analyzer for Index Writer is: EnglishAnalyzer()");

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
        System.out.println("1");
        laTimesDocs= getLosAngelesTimesDocs();
        
	System.out.println("loaded la times documents");

        System.out.println("loading financial times documents");
        loadFilesAndExtractInfoForFinancialTimes(absPathToFinTimes);
        finTimesDocs = getFinancialTimesDocs();
        System.out.println("size of LA times documents: " + laTimesDocs.size());
	Document doc = laTimesDocs.get(545);
	System.out.print(doc.get("DocNo"));

	doc = laTimesDocs.get(546);
        System.out.print(doc.get("DocNo"));
        System.out.println("loading federal register documents");
        loadFilesAndExtractInfoForFederalRegister(absPathToFedRegister);
        fedRegisterDocs = getFedRegisterDocs();
        System.out.println("loaded federal register documents");

	System.out.println("size of financial times documents: " + finTimesDocs.size());
	System.out.println("size of la times documents: " + laTimesDocs.size());
	System.out.println("size of foreign broad documents: " + fbisDocs.size());
	System.out.println("size of fed reg documents: " + fedRegisterDocs.size());
    }

    private static void indexDocuments(Similarity similarity, Analyzer analyzer, Directory directory) {
        IndexWriter indexWriter;
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);

        try {
            indexWriter = new IndexWriter(directory, indexWriterConfig);
            //indexWriter.deleteAll();

            System.out.println("indexing financial times document collection");
            for (Document doc: finTimesDocs) {
	    	indexWriter.addDocument(doc);
	    }
	    //indexWriter.addDocuments(finTimesDocs);

            //System.out.println("indexing federal register document collection");
            //indexWriter.addDocuments(fedRegisterDocs);
	    for (Document doc: fedRegisterDocs) {
                indexWriter.addDocument(doc);
            }

            System.out.println("indexing la times document collection");
            //indexWriter.addDocuments(laTimesDocs);

	    for (Document doc: laTimesDocs) {
                indexWriter.addDocument(doc);
            }

            System.out.println("indexing foreign broadcast information service document collection");
            //indexWriter.addDocuments(fbisDocs);

	    for (Document doc: fbisDocs) {
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

    private static void executeQueries(Directory directory, WordEmbeddingModel wordEmbeddingModel) throws ParseException {
        try {
            IndexReader indexReader = DirectoryReader.open(directory);
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            indexSearcher.setSimilarity(similarityModel);

	    queries = loadQueries(analyzer, wordEmbeddingModel);
	    System.out.println("Size of queries: " + queries.size());
            //QueryParser queryParser = new MultiFieldQueryParser(new String[]{"headline", "text"}, analyzer);
            PrintWriter writer = new PrintWriter(absPathToSearchResults, "UTF-8");

	    int queryNum = 0;
	   for (CustomBooleanQuery customQuery : queries) {
		    // Get the set of results
//		  	System.out.println(query);
                BooleanQuery query = customQuery.getBooleanQuery();

		for (BooleanClause clause : query.clauses()) {
            Query q = clause.getQuery();
            BooleanClause.Occur occur = clause.getOccur();

            System.out.println(String.format("(%s) %s", occur, q.toString()));
        }
		ScoreDoc[] hits = indexSearcher.search(query, 1000).scoreDocs;
		String analyzerName = analyzer.getClass().getName();

                // Print the results
                for (int i = 0; i < hits.length; i++)
                {
                        Document hitDoc = indexSearcher.doc(hits[i].doc);
                        String resultLine = customQuery.getQueryNumber() + "\t" + "0" + "\t" + hitDoc.get("DocNo") + "\t" + (i + 1) + "\t" + hits[i].score + "\t" + analyzerName;
                        writer.println(resultLine);
	    	}	    
		queryNum++;
	    }

        } catch (IOException e) {
            System.out.println("ERROR: an error occurred when instantiating the printWriter!");
            System.out.println(String.format("ERROR MESSAGE: %s", e.getMessage()));
        }
    }



}
