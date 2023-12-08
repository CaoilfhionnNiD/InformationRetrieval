package com.example;

import org.apache.lucene.search.BooleanQuery;

public class CustomBooleanQuery {

    private BooleanQuery booleanQuery;
    private String queryNumber;

    public CustomBooleanQuery(BooleanQuery booleanQuery, String queryNumber) {
        this.booleanQuery = booleanQuery;
        this.queryNumber = queryNumber;
    }

    public BooleanQuery getBooleanQuery() {
        return booleanQuery;
    }

    public String getQueryNumber() {
        return queryNumber;
    }
}

