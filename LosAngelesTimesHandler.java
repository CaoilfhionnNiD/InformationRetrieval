package com.kerinb.Lucenefer;
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
public class LosAngelesTimesHandler {
    private static List<Document> laTimesDocs = new ArrayList<>();


    public static List<Document> getLosAngelesTimesDocs() {
        return laTimesDocs;
    }

    public static void loadFilesAndExtractInfoForLosAngelesTimes(String directoryPath) {
        File directory = new File(directoryPath);
        int i = 0;
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        loadFilesAndExtractInfoForLosAngelesTimes(file.getAbsolutePath());
                    } else {
                        // Process file
                        LosAngelesTimes losAngelesTimes = new LosAngelesTimes();
                        String fileContent = readFileContent(file.getAbsolutePath());

                        if (fileContent != null && !fileContent.isEmpty()) {
                            losAngelesTimes.loadLosAngelesTimesDoc(fileContent);
                            System.out.println(i);
                            i++;
                            // Extract information and create Lucene Document object
                            org.apache.lucene.document.Document luceneDoc = createLuceneDocument(losAngelesTimes);
                            laTimesDocs.add(luceneDoc);
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

    private static org.apache.lucene.document.Document createLuceneDocument(LosAngelesTimes losAngelesTimes) {
        Document luceneDoc = new Document();

        luceneDoc.add(new TextField("DocNo", losAngelesTimes.getDocNo(), Field.Store.YES));
        luceneDoc.add(new TextField("ID", losAngelesTimes.getDocID(), Field.Store.YES));
        luceneDoc.add(new TextField("Date", losAngelesTimes.getDate(), Field.Store.YES));
        luceneDoc.add(new TextField("Headline", losAngelesTimes.getHeadline(), Field.Store.YES));
        luceneDoc.add(new TextField("Text", losAngelesTimes.getText(), Field.Store.YES));
        luceneDoc.add(new TextField("Section", losAngelesTimes.getSection(), Field.Store.YES));
        luceneDoc.add(new TextField("Length", losAngelesTimes.getLength(), Field.Store.YES));
        luceneDoc.add(new TextField("Byline", losAngelesTimes.getByline(), Field.Store.YES));
        return luceneDoc;
    }
}
