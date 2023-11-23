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

public class FinancialTimes {

    String docNo;
    String profile;
    Date date;
    String headline;
    String text;
    String pub;
    String page;

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyMMdd");

    public FinancialTimes() {
    }

    public void setDocNo(String docNo) {
        this.docNo = docNo;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public void setDate(String dateString) {
        try {
            Date date = dateFormat.parse(dateString);

            this.date = date;
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setPub(String pub) {
        this.pub = pub;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getDocNo() {
        return this.docNo;
    }

    public String getProfile() {
        return this.profile;
    }

    public String getDate() {
        String dateString = dateFormat.format(this.date);
        return dateString;
    }

    public String getHeadline() {
        return this.headline;
    }

    public String getText() {
        return this.text;
    }

    public String getPub() {
        return this.pub;
    }

    public String getPage() {
        return this.page;
    }

    public void loadFinancialTimesDoc(String doc) {

        Pattern pattern = Pattern.compile("<DOCNO>([\\s\\S]*?)</DOCNO>");
        Matcher matcher = pattern.matcher(doc);

        if (matcher.find()) {
            setDocNo(matcher.group(1).trim());
        } else setDocNo("");

        pattern = Pattern.compile("<PROFILE>([\\s\\S]*?)</PROFILE>");
        matcher = pattern.matcher(doc);

        if (matcher.find()) {
            setProfile(matcher.group(1).trim());
        } else setProfile("");

        pattern = Pattern.compile("<DATE>([\\s\\S]*?)</DATE>");
        matcher = pattern.matcher(doc);

        if (matcher.find()) {
            setDate(matcher.group(1).trim());
        } else {
            System.out.printf("g");
            setDate("");
        }

        pattern = Pattern.compile("<HEADLINE>([\\s\\S]*?)</HEADLINE>");
        matcher = pattern.matcher(doc);

        if (matcher.find()) {
            setHeadline(matcher.group(1).trim());
        } else setHeadline("");

        pattern = Pattern.compile("<TEXT>([\\s\\S]*?)</TEXT>");
        matcher = pattern.matcher(doc);

        if (matcher.find()) {
            setText(matcher.group(1).trim());
        } else setText("");

        pattern = Pattern.compile("<PUB>([\\s\\S]*?)</PUB>");
        matcher = pattern.matcher(doc);

        if (matcher.find()) {
            setPub(matcher.group(1).trim());
        } else setPub("");

        pattern = Pattern.compile("<PAGE>([\\s\\S]*?)</PAGE>");
        matcher = pattern.matcher(doc);

        if (matcher.find()) {
            setPage(matcher.group(1).trim());
        } else setPage("");

    }
}


