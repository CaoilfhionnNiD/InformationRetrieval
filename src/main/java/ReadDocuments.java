package com.example;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import com.example.new_sources.FinancialTimes;
import com.example.new_sources.LosAngelesTimes;
import com.example.new_sources.ForeignBroadcast;
import com.example.new_sources.FederalRegister;

public class ReadDocuments {


 public static void main(String[] args) {

         String filePath = "Assignment Two/ft/ft911/ft911_1";
	 String filePathLA = "Assignment Two/latimes/la112490";
	 String filePathFB = "Assignment Two/fbis/fb396001";
	 String filePathFR = "Assignment Two/fr94/01/fr940104.0";

         try (BufferedReader reader = new BufferedReader(new FileReader(filePathFR))) {
              StringBuilder content = new StringBuilder();
              String line;

            while ((line = reader.readLine()) != null) {
                content.append(line).append("\n");
            }

	Pattern pattern = Pattern.compile("<DOC>([\\s\\S]*?)</DOC>");
        Matcher matcher = pattern.matcher(content);

        while(matcher.find()) {
              //FinancialTimes ft = new FinancialTimes();
	      //ForeignBroadcast ft = new ForeignBroadcast();
	      FederalRegister ft = new FederalRegister();

	      String doc = matcher.group();
              ft.loadFederalRegisterDoc(doc.toString());

        	System.out.println("DocNo: " + ft.getDocNo());
		System.out.println("Parent " + ft.getParent());
         	System.out.println("Text: " + ft.getText());
      	   }
	
	}catch (FileNotFoundException e) {
                e.printStackTrace();
        } catch (IOException e) {
                e.printStackTrace();
        }
    }


}
