package com.example.abhishek.nytimessearch.models;

import com.example.abhishek.nytimessearch.networking.ArticleClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by abhishekdesai on 10/19/16.
 */

public class Article {

    /** Tag for the log messages */
    private static final String LOG_TAG = Article.class.getSimpleName();

    // Member Variables
    private String mWebUrl, mHeadline, mThumbNail;


    /**
     * Getter for Web URL
     * @return
     */
    public String getWebUrl() {
        return mWebUrl;
    }

    /**
     * Getter for Headline
     * @return
     */
    public String getHeadline() {
        return mHeadline;
    }


    /**
     * Getter for ThumbNail
     * @return
     */
    public String getThumbNail() {
        return mThumbNail;
    }


    /**
     * Constructor
     * @param jsonObject
     */
    private Article(JSONObject jsonObject) {

        try {

            this.mWebUrl    =   jsonObject.getString("web_url");
            this.mHeadline  =   jsonObject.getJSONObject("headline").getString("main");

            JSONArray multimedia = jsonObject.getJSONArray("multimedia");

            if(multimedia.length() > 0) {
                JSONObject multimediaJson = multimedia.getJSONObject(0);
                this.mThumbNail = "http://www.nytimes.com/" + multimediaJson.getString("url");
            } else {
                this.mThumbNail = "";
            }

        }catch (JSONException e) {
            e.printStackTrace();
        }

    }


    /**
     * Factory method for converting Article JSONArray into JSONObject - Deserialize
     * @param jsonArray
     * @return
     */
    public static ArrayList<Article> fromJsonArray(JSONArray jsonArray) {

        ArrayList<Article> results = new ArrayList<>();

        for(int i = 0; i < jsonArray.length(); i++) {

            try {

                results.add(new Article(jsonArray.getJSONObject(i)));

            } catch (JSONException e) {

                e.printStackTrace();

            }

        }

        return results;

    }


}
