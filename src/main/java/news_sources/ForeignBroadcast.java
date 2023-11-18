package com.example.new_sources;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
public class ForeignBroadcast {
    String docNo;
    String HT;
    Date date;
    String headline;
    String text;
    String pub;
    String page;

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");

    public ForeignBroadcast() {
    }

    public void setDocNo(String docNo) {
        this.docNo = docNo;
    }

    public void setHT(String HT) {
        this.HT = HT;
    }

    public void setDate(String dateString) {
        try{
            Date date = dateFormat.parse(dateString);
            this.date = date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setHeadline(String headline) {this.headline = headline;}
    public void setText(String text) {this.text = text;}
    public String getHT() {return this.HT;}
    public String getDocNo() {return this.docNo;}
    public Date getDate() {return this.date;}
    public String getHeadline() {return this.headline;}
    public String getText() {return this.text;}


    public void loadForeignBroadcastDoc(String doc) {

        Pattern pattern = Pattern.compile("<DOCNO>([\\s\\S]*?)</DOCNO>");
        Matcher matcher = pattern.matcher(doc);

        if(matcher.find())
        {
            setDocNo(matcher.group(1).trim());
        }
        else setDocNo("");


        pattern = Pattern.compile("<HT>([\\s\\S]*?)</HT>");
        matcher = pattern.matcher(doc);

        if (matcher.find()) {
            setHT(matcher.group(1).trim());
        } else setHT("");


        pattern = Pattern.compile("<DATE1>([\\s\\S]*?)</DATE1>");
        matcher = pattern.matcher(doc);

        if (matcher.find()) {
            setDate(matcher.group(1).trim());
        } else setDate("");


        pattern = Pattern.compile("<TI>([\\s\\S]*?)</TI>");
        matcher = pattern.matcher(doc);

        if (matcher.find()) {
            setHeadline(matcher.group(1).trim());
        } else setHeadline("");


        pattern = Pattern.compile("<TEXT>([\\s\\S]*?)</TEXT>");
        matcher = pattern.matcher(doc);

        if (matcher.find()) {
            setText(matcher.group(1).trim());
        } else setText("");

    }
}
