package com.example.news_sources;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForeignBroadcast {
    String docNo;
    String headline;
    String text;

    private static final Pattern DOCNO_PATTERN = Pattern.compile("<DOCNO>([\\s\\S]*?)</DOCNO>");
    private static final Pattern TEXT_PATTERN = Pattern.compile("<TEXT>([\\s\\S]*?)</TEXT>");
    private static final Pattern TI_PATTERN = Pattern.compile("<TI>([\\s\\S]*?)</TI>");

    public ForeignBroadcast() {
    }

    public void setDocNo(String docNo) {
        this.docNo = docNo;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDocNo() {
        return this.docNo;
    }

    public String getHeadline() {
        return this.headline;
    }

    public String getText() {
        return this.text;
    }

    public void loadForeignBroadcastDoc(String doc) {

        Matcher matcher = DOCNO_PATTERN.matcher(doc);

        if (matcher.find()) {
            setDocNo(matcher.group(1).trim());
        } else {
            setDocNo("");

        }

        matcher = TI_PATTERN.matcher(doc);

        if (matcher.find()) {
            setHeadline(matcher.group(1).trim());
        } else {
            setHeadline("");
        }

        matcher = TEXT_PATTERN.matcher(doc);

        if (matcher.find()) {
            setText(matcher.group(1).trim());
        } else {
            setText("");
        }
    }
}
