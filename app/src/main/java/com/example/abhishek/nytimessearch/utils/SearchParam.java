package com.example.abhishek.nytimessearch.utils;

/**
 * Created by abhishek on 10/21/16.
 */

public class SearchParam {

    private String mSearchQuery;
    private int mSearchPage;
    private String mSearchDate;
    private String mSearchOrder;


    public SearchParam(String searchQuery, int searchPage, String searchDate, String searchOrder) {
        mSearchQuery = searchQuery;
        mSearchPage = searchPage;
        mSearchDate = searchDate;
        mSearchOrder = searchOrder;
    }

    public String getSearchQuery() {
        return mSearchQuery;
    }

    public int getSearchPage() {
        return mSearchPage;
    }

    public String getSearchDate() {
        return mSearchDate;
    }

    public String getSearchOrder() {
        return mSearchOrder;
    }
}
