package com.example.news_sources;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class FederalRegister {
	
	String docNo;
	String text;

	private static final Pattern DOCNO_PATTERN = Pattern.compile("<DOCNO>([\\s\\S]*?)</DOCNO>");
//        private static final Pattern HEADLINE_PATTERN = Pattern.compile("<HEADLINE>([\\s\\S]*?)</HEADLINE>");
        private static final Pattern TEXT_PATTERN = Pattern.compile("<TEXT>([\\s\\S]*?)</TEXT>");

	public FederalRegister() {
	}

	public void setDocNo(String docNo) {
	    this.docNo = docNo;
	}

	public void setText(String text) {
    	    this.text = text;
	}

	public String getDocNo() {return this.docNo;}
	public String getText() {return this.text;}

	public void loadFederalRegisterDoc(String doc) {

        	doc = removeComments(doc);
	
		Matcher matcher = DOCNO_PATTERN.matcher(doc);

    		if (matcher.find()) {
        		setDocNo(matcher.group(1).trim());
    		} else {
 			setDocNo("");
		}

		matcher = TEXT_PATTERN.matcher(doc);

    		if (matcher.find()) {
        		setText(matcher.group(1).trim());
    		} else {
    			setText("");
    		}
	}

    private static String removeComments(String inputString) {
        String pattern = "<!--.*?-->";
        String result = inputString.replaceAll(pattern, "");
        return result;
    }
}
