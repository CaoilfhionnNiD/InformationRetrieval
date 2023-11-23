package com.kerinb.Lucenefer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;

public class LosAngelesTimes {

        String docNo;
	String docID;
        String date;
	String section;
	String length;
        String headline;
        String text;
	String byline;

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");

        public LosAngelesTimes() {
        }

        public void setDocNo(String docNo) {
            this.docNo = docNo;
        }

        public void setDocID(String docID) {
            this.docID = docID;
        }

        public void setDate(String dateString) {
        	this.date = dateString;
        }

        public void setHeadline(String headline) {
            this.headline = headline;
	}

	public void setText(String text) {
            this.text = text;
        }

        public void setSection(String section) {
            this.section = section;
        }

        public void setLength(String length) {
            this.length = length;
        }
	
	public void setByline(String byline) {
            this.byline = byline;
        }

        public String getDocNo() {return this.docNo;}
        public String getDocID() {return this.docID;}
        public String getDate() {return this.date;}
        public String getHeadline() {return this.headline;}
        public String getText() {return this.text;}
        public String getSection() {return this.section;}
        public String getLength() {return this.length;}
	public String getByline() {return this.byline;}

        public void loadLosAngelesTimesDoc(String doc) {

                Pattern pattern = Pattern.compile("<DOCNO>([\\s\\S]*?)</DOCNO>");
                Matcher matcher = pattern.matcher(doc);

                if(matcher.find())
                {
                        setDocNo(matcher.group(1).trim());
                }
                else setDocNo("");

                pattern = Pattern.compile("<DOCID>([\\s\\S]*?)</DOCID>");
                matcher = pattern.matcher(doc);

                if(matcher.find())
                {
                        setDocID(matcher.group(1).trim());
                }
                else setDocID("");

                 pattern = Pattern.compile("<DATE>([\\s\\S]*?)</DATE>");
                 matcher = pattern.matcher(doc);

                 if (matcher.find()) {
			 String date = matcher.group(1).replaceAll("<P>", "").replaceAll("</P>", "").trim();
		         setDate(date);
                } else setDate("");

                pattern = Pattern.compile("<HEADLINE>([\\s\\S]*?)<HEADLINE/>");
                matcher = pattern.matcher(doc);

                if (matcher.find()) {
			String headline = matcher.group(1).replaceAll("<P>", "").replaceAll("</P>", "").trim();
                        setHeadline(headline);
                } else setHeadline("");

                pattern = Pattern.compile("<TEXT>([\\s\\S]*?)</TEXT>");
                matcher = pattern.matcher(doc);

                if (matcher.find()) {
			String text = matcher.group(1).replaceAll("<P>", "").replaceAll("</P>", "").trim();
                        setText(text);
                } else setText("");

                pattern = Pattern.compile("<SECTION>([\\s\\S]*?)</SECTION>");
                matcher = pattern.matcher(doc);

                if (matcher.find()) {
			String section = matcher.group(1).replaceAll("<P>", "").replaceAll("</P>", "").trim();
                        setSection(section);
                } else setSection("");

                pattern = Pattern.compile("<LENGTH>([\\s\\S]*?)</LENGTH>");
                matcher = pattern.matcher(doc);

                if (matcher.find()) {
			String length = matcher.group(1).replaceAll("<P>", "").replaceAll("</P>", "").trim();
                        setLength(length);
                } else setLength("");


		pattern = Pattern.compile("<BYLINE>([\\s\\S]*?)</BYLINE>");
                matcher = pattern.matcher(doc);

                if (matcher.find()) {
                        String byline = matcher.group(1).replaceAll("<P>", "").replaceAll("</P>", "").trim();
                        setByline(byline);
                } else setByline("");

        }
}

