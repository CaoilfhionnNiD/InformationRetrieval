package com.example.news_sources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.lucene.document.Document;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
public class ForeignBroadcastHandler {
    private static List<Document> fbisDocs = new ArrayList<>();

    public static List<Document> getForeignBroadcastDocs() {
        return fbisDocs;
    }

    public static void loadFilesAndExtractInfoForForeignBroadcast(String directoryPath) {
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        loadFilesAndExtractInfoForForeignBroadcast(file.getAbsolutePath());
                    } else {
                        // Process file
                        ForeignBroadcast foreignBroadcast = new ForeignBroadcast();
                        String fileContent = readFileContent(file.getAbsolutePath());

                        if (fileContent != null && !fileContent.isEmpty()) {
                            foreignBroadcast.loadForeignBroadcastDoc(fileContent);
                            // Extract information and create Lucene Document object
                            org.apache.lucene.document.Document luceneDoc = createLuceneDocument(foreignBroadcast);
                            fbisDocs.add(luceneDoc);
                        }
                    }
                }
            }
        }
    }

    private static String readFileContent(String filePath) {
        StringBuilder content = new StringBuilder();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                content.append(line).append("\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return content.toString();
    }

    private static org.apache.lucene.document.Document createLuceneDocument(ForeignBroadcast foreignBroadcast) {
        Document luceneDoc = new Document();
        luceneDoc.add(new TextField("DocNo", foreignBroadcast.getDocNo(), Field.Store.YES));
        luceneDoc.add(new TextField("Headline", foreignBroadcast.getHeadline(), Field.Store.YES));
        luceneDoc.add(new TextField("Text", foreignBroadcast.getText(), Field.Store.YES));

        return luceneDoc;
    }
}
