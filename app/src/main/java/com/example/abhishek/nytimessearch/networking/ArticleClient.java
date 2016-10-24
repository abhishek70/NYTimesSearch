package com.example.abhishek.nytimessearch.networking;

import android.util.Log;

import com.example.abhishek.nytimessearch.utils.Config;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by abhishek on 10/19/16.
 * This class will manage all networking calls
 */

public class ArticleClient {

    /** Tag for the log messages */
    private static final String LOG_TAG = ArticleClient.class.getSimpleName();


    /** AsyncHttpClient for API Call */
    private AsyncHttpClient client;


    /**
     * Constructor
     */
    public ArticleClient() {
        this.client = new AsyncHttpClient();
    }


    /**
     * Get Articles  in JSON Format
     * @param handler
     */
    public void getArticles(String searchQuery, int searchPage , String searchOrder, String searchDate, JsonHttpResponseHandler handler) {

        String url = Config.NYTIMES_ARTICLE_API_URL;

        // Setting URL parameters
        RequestParams params = new RequestParams("api-key", Config.NYTIMES_ARTICLE_API_KEY);

        // Page
        params.put("page", searchPage);

        // Date
        if(searchDate != "") {
            params.put("begin_date", searchDate);
        }

        // Order
        if(searchOrder != "") {
            params.put("sort", searchOrder);
        }

        // Query
        if(searchQuery != "") {
            params.put("fq", searchQuery);
        }

        // Prep and executing API Call
        client.get(url, params, handler);
    }

}
