package com.example.abhishek.nytimessearch.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.example.abhishek.nytimessearch.R;
import com.example.abhishek.nytimessearch.adapters.ArticleArrayAdapter;
import com.example.abhishek.nytimessearch.models.Article;
import com.example.abhishek.nytimessearch.networking.ArticleClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

import static com.google.android.gms.internal.zzsp.LO;

public class SearchActivity extends AppCompatActivity {

    /** Tag for the log messages */
    private static final String LOG_TAG = SearchActivity.class.getSimpleName();


    // Artcile client for building url for fetching Article data
    private ArticleClient articleClient;

    private ArrayList<Article> articles;

    private ArticleArrayAdapter articleArrayAdapter;

    @BindView(R.id.rvArticles) RecyclerView rvArticles;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        // Setting up views
        setupViews();

        // Fetch Articles
        fetchArticles();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        } else if(id == R.id.action_filters) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Setting up the views
     */
    private void setupViews() {

    }

    /**
     * Fetching Articles Async
     */
    private void fetchArticles() {

        articles = new ArrayList<>();

        // Initializing Async Client
        articleClient = new ArticleClient();

        // Create adapter passing in the sample user data
        articleArrayAdapter = new ArticleArrayAdapter(this, articles);

        // Attach the adapter to the recyclerview to populate items
        rvArticles.setAdapter(articleArrayAdapter);

        rvArticles.setLayoutManager(new LinearLayoutManager(this));

        //
        articleClient.getArticles(0, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                JSONArray results = null;

                try {

                    results = response.getJSONObject("response").getJSONArray("docs");

                    articles.addAll(Article.fromJsonArray(results));

                    Log.d(LOG_TAG, articles.toString());

                    articleArrayAdapter.notifyDataSetChanged();

                } catch (JSONException e) {

                    e.printStackTrace();

                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });


    }

}
