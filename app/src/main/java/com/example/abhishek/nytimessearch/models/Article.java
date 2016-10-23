package com.example.abhishek.nytimessearch.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.abhishek.nytimessearch.networking.ArticleClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by abhishekdesai on 10/19/16.
 */

public class Article implements Parcelable {

    /** Tag for the log messages */
    private static final String LOG_TAG = Article.class.getSimpleName();


    // Member Variables
    private String mWebUrl, mHeadline, mThumbNail;


    private Article(Parcel in) {
        mHeadline   = in.readString();
        mThumbNail  = in.readString();
        mWebUrl     = in.readString();
    }

    @Override
    public int describeContents() {

        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mHeadline);
        parcel.writeString(mThumbNail);
        parcel.writeString(mWebUrl);

    }


    public static final Creator<Article> CREATOR = new Creator<Article>() {
        public Article createFromParcel(Parcel source) {
            return new Article(source);
        }

        public Article[] newArray(int size) {
            return new Article[size];
        }
    };


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
     * Return the view type to adapter
     * With Thumbnail = 1
     * Without Thumbnail = 0
     * @return
     */
    public int getViewType() {

        if(mThumbNail != "")
            return 1;

        return 0;
    }


    /**
     * Constructor
     * @param jsonObject
     */
    private Article(JSONObject jsonObject) {

        try {

            mWebUrl    =   jsonObject.getString("web_url");
            mHeadline  =   jsonObject.getJSONObject("headline").getString("main");

            JSONArray multimedia = jsonObject.getJSONArray("multimedia");

            if(multimedia.length() > 0) {
                String thumbNail = null;

                for(int j = 0; j < multimedia.length(); j++) {

                    JSONObject multimediaJson = multimedia.getJSONObject(j);

                    if(multimediaJson.getString("subtype") == "thumbnail") {
                        thumbNail = multimediaJson.getString("url");
                        break;
                    }
                }
                //JSONObject multimediaJson = multimedia.getJSONObject(0);
                //this.mThumbNail = "http://www.nytimes.com/" + multimediaJson.getString("url");
                if (thumbNail == null) {
                    thumbNail = multimedia.getJSONObject(0).getString("url");
                }
                mThumbNail = "http://www.nytimes.com/" + thumbNail;
            } else {
                mThumbNail = "";
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
