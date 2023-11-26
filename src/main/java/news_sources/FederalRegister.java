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
	String parent;
	String text;

	public FederalRegister() {
	}

	public void setDocNo(String docNo) {
	    this.docNo = docNo;
	}

	public void setParent(String parent) {
    	    this.parent = parent;
	}

	public void setText(String text) {
    	    this.text = text;
	}

	public String getDocNo() {return this.docNo;}
	public String getParent() {return this.parent;}
	public String getText() {return this.text;}

	public void loadFederalRegisterDoc(String doc) {

        	doc = removeComments(doc);
	
		Pattern pattern = Pattern.compile("<DOCNO>([\\s\\S]*?)</DOCNO>");
        	Matcher matcher = pattern.matcher(doc);

        	if(matcher.find())
        	{
                	setDocNo(matcher.group(1).trim());
        	}
        	else setDocNo("");
        	
		pattern = Pattern.compile("<PARENT>([\\s\\S]*?)</PARENT>");
                matcher = pattern.matcher(doc);

                if(matcher.find())
                {
                        setParent(matcher.group(1).trim());
                }
                else setParent("");

    		pattern = Pattern.compile("<TEXT>([\\s\\S]*?)</TEXT>");
    		matcher = pattern.matcher(doc);

    		if (matcher.find()) {
        		setText(matcher.group(1).trim());
    		} else setText("");
	
	}

    private static String removeComments(String inputString) {
        String pattern = "<!--.*?-->";
        String result = inputString.replaceAll(pattern, "");
        return result;
    }
}
