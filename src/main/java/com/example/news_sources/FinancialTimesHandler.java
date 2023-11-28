package com.example.news_sources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;

public class FinancialTimesHandler {
    private static List<Document> finTimesDocs = new ArrayList<>();

    private static final Pattern DOC_PATTERN = Pattern.compile("<DOC>([\\s\\S]*?)</DOC>");

    public static List<Document> getFinancialTimesDocs() {
        return finTimesDocs;
    }

    public static void loadFilesAndExtractInfoForFinancialTimes(String directoryPath) {
        File directory = new File(directoryPath);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        loadFilesAndExtractInfoForFinancialTimes(file.getAbsolutePath());
                    } else {
                        String fileContent = readFileContent(file.getAbsolutePath());

                        if (fileContent != null && !fileContent.isEmpty()) {

                            Matcher matcher = DOC_PATTERN.matcher(fileContent);

                            while (matcher.find()) {
                                String match = matcher.group();
                                FinancialTimes financialTimes = new FinancialTimes();
                                financialTimes.loadFinancialTimesDoc(match);

                                Document luceneDoc = createLuceneDocument(financialTimes);
                                finTimesDocs.add(luceneDoc);
                            }
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

    private static org.apache.lucene.document.Document createLuceneDocument(FinancialTimes financialTimes) {
        Document luceneDoc = new Document();

        luceneDoc.add(new TextField("DocNo", financialTimes.getDocNo(), Field.Store.YES));
        luceneDoc.add(new TextField("Headline", financialTimes.getHeadline(), Field.Store.YES));
        luceneDoc.add(new TextField("Text", financialTimes.getText(), Field.Store.YES));
        return luceneDoc;
    }

}
