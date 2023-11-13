package com.example;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import com.example.new_sources.FinancialTimes;
import com.example.new_sources.LosAngelesTimes;

public class ReadDocuments {


 public static void main(String[] args) {

         String filePath = "Assignment Two/ft/ft911/ft911_1";
	 String filePathLA = "Assignment Two/latimes/la112490";

         try (BufferedReader reader = new BufferedReader(new FileReader(filePathLA))) {
              StringBuilder content = new StringBuilder();
              String line;

            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

	Pattern pattern = Pattern.compile("<DOC>([\\s\\S]*?)</DOC>");
        Matcher matcher = pattern.matcher(content);

        while(matcher.find()) {
              //FinancialTimes ft = new FinancialTimes();
	      LosAngelesTimes ft = new LosAngelesTimes();
	      String doc = matcher.group();
              ft.loadLosAngelesTimesDoc(doc.toString());

        	System.out.println("DocNo: " + ft.getDocNo());
		System.out.println("DocID: " + ft.getDocID());
        	System.out.println("Section: " + ft.getSection());
        	System.out.println("Date: " + ft.getDate());
        	System.out.println("Headline: " + ft.getHeadline());
        	System.out.println("Text: " + ft.getText());
        	System.out.println("Length: " + ft.getLength());
	   }
	
	}catch (FileNotFoundException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        }
    }


}
