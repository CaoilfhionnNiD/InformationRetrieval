package com.kerinb.Lucenefer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.lucene.document.Document;

import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;

public class FederalRegisterHandler {

    private static List<Document> fedRegisterDocs = new ArrayList<>();

    public static List<Document> getFedRegisterDocs() {
        return fedRegisterDocs;
    }

    public static void loadFilesAndExtractInfoForFederalRegister(String directoryPath) {
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        loadFilesAndExtractInfoForFederalRegister(file.getAbsolutePath());
                    } else {
                        // Process file
                        FederalRegister federalRegister = new FederalRegister();
                        String fileContent = readFileContent(file.getAbsolutePath());

                        if (fileContent != null && !fileContent.isEmpty()) {
                            federalRegister.loadFederalRegisterDoc(fileContent);
                            // Extract information and create Lucene Document object
                            org.apache.lucene.document.Document luceneDoc = createLuceneDocument(federalRegister);
                            fedRegisterDocs.add(luceneDoc);
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

    private static org.apache.lucene.document.Document createLuceneDocument(FederalRegister federalRegister) {
        Document luceneDoc = new Document();

        luceneDoc.add(new TextField("DocNo", federalRegister.getDocNo(), Field.Store.YES));
        luceneDoc.add(new TextField("Parent", federalRegister.getParent(), Field.Store.YES));
        luceneDoc.add(new TextField("Text", federalRegister.getText(), Field.Store.YES));

        return luceneDoc;
    }

}
