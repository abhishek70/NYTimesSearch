package com.example.abhishek.nytimessearch.networking;

import com.example.abhishek.nytimessearch.utils.Config;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by abhishek on 10/19/16.
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
    public void getArticles(int page, JsonHttpResponseHandler handler) {

        String url = Config.NYTIMES_ARTICLE_API_URL;

        // Setting URL parameters
        RequestParams params = new RequestParams("api-key", Config.NYTIMES_ARTICLE_API_KEY);

        params.put("page", page);

        // Prep and executing API Call
        client.get(url, params, handler);
    }

}
