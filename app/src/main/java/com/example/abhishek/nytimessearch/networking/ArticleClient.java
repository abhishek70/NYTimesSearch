package com.example.abhishek.nytimessearch.networking;

import android.util.Log;

import com.example.abhishek.nytimessearch.utils.Config;
import com.example.abhishek.nytimessearch.utils.SearchParam;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;

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
    public void getArticles(String searchQuery, int searchPage , String searchOrder, String searchDate, JsonHttpResponseHandler handler) {

        String url = Config.NYTIMES_ARTICLE_API_URL;

        // Setting URL parameters
        RequestParams params = new RequestParams("api-key", Config.NYTIMES_ARTICLE_API_KEY);

        Log.d(LOG_TAG + " Page", String.valueOf(searchPage));
        Log.d(LOG_TAG + " Query", searchQuery);
        Log.d(LOG_TAG + " Date", searchDate);
        Log.d(LOG_TAG + " Order", searchOrder);


        params.put("page", searchPage);




        if(searchDate != "") {
            params.put("begin_date", searchDate);
        }

        if(searchOrder != "") {
            params.put("sort", searchOrder);
        }

        if(searchQuery != "") {
            params.put("q", searchQuery);
        }


        // Prep and executing API Call
        client.get(url, params, handler);
    }

}
