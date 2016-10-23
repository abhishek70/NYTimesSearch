package com.example.abhishek.nytimessearch.activities;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.example.abhishek.nytimessearch.R;
import com.example.abhishek.nytimessearch.adapters.ArticleArrayAdapter;
import com.example.abhishek.nytimessearch.models.Article;
import com.example.abhishek.nytimessearch.networking.ArticleClient;
import com.example.abhishek.nytimessearch.utils.EndlessRecyclerViewScrollListener;
import com.example.abhishek.nytimessearch.utils.SearchParam;
import com.example.abhishek.nytimessearch.utils.SpacesItemDecoration;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

import static com.example.abhishek.nytimessearch.R.id.rvArticles;
import static com.google.android.gms.internal.zzsp.LO;

public class SearchActivity extends AppCompatActivity {

    /** Tag for the log messages */
    private static final String LOG_TAG = SearchActivity.class.getSimpleName();

    // Article client for building url for fetching Article data
    private ArticleClient articleClient;

    // Article List
    private ArrayList<Article> articles;

    // Article Adapter
    private ArticleArrayAdapter articleArrayAdapter;


    // Setting global search query
    private String mSearchQuery = "";

    // Recycler View for holding Articles
    @BindView(R.id.rvArticles) RecyclerView rvArticles;
    @BindView(R.id.swipeRefresh) SwipeRefreshLayout swipeRefresh;
    @BindView(R.id.linlaHeaderProgress) LinearLayout articlesLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ButterKnife.bind(this);

        // Setting up views
        setupViews();

        // Loading swipe container
        loadSwipeRefresh();

        // Clearing Articles
        clearArticles();

        // Set Scroll Event Listener
        setScrollEventListener();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                mSearchQuery = query;

                fetchArticles(0);

                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
         if(id == R.id.action_filters) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Setting up the views
     */
    private void setupViews() {

        articles = new ArrayList<>();
        articleArrayAdapter = new ArticleArrayAdapter(this, articles);
        rvArticles.setAdapter(articleArrayAdapter);

        // Set cols : portrait = 2 & Landscape = 3
        int numCols = 2;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            numCols = 3;
        }

        // Staggered Grid Layout Manager
        StaggeredGridLayoutManager gridLayoutManager =
                new StaggeredGridLayoutManager(numCols, StaggeredGridLayoutManager.VERTICAL);
        rvArticles.setLayoutManager(gridLayoutManager);

        // Adding custom spaces between the cards
        SpacesItemDecoration decoration = new SpacesItemDecoration(16);
        rvArticles.addItemDecoration(decoration);
    }


    /**
     * Swipe Refresh Method
     */
    private void loadSwipeRefresh() {

        // Called when the user swipe
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                clearArticles();
            }
        });

        // Setting Swipe Color Scheme
        swipeRefresh.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light
        );
    }


    private void clearArticles() {

        /*if (articles != null) {

            int clearedCount = articles.size();
            articles.clear();
            articleArrayAdapter.notifyItemRangeRemoved(0, clearedCount);

        } else {

            articles = new ArrayList<>();
            articleArrayAdapter = new ArticleArrayAdapter(this, articles);
            rvArticles.setAdapter(articleArrayAdapter);
        }*/

        fetchArticles(0);
    }

    /**
     * Fetching Articles Async
     */
    private void fetchArticles(final int page) {

        // Initializing Async Client
        articleClient = new ArticleClient();

        Log.d(LOG_TAG + " query ", mSearchQuery);


        articleClient.getArticles(mSearchQuery, page, "", "",  new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();

                if(page == 0) {
                    articlesLoader.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                JSONArray results = null;

                try {

                    results = response.getJSONObject("response").getJSONArray("docs");

                    List<Article> newArticles = new ArrayList<Article>();

                    newArticles.addAll(Article.fromJsonArray(results));

                    if (page == 0) {
                        articles.clear();
                        articles.addAll(newArticles);
                        articleArrayAdapter.notifyDataSetChanged();
                    } else {
                        int nextItemPosition = articles.size();
                        articles.addAll(newArticles);
                        articleArrayAdapter.notifyItemRangeInserted(nextItemPosition, newArticles.size());
                    }

                    //articles.addAll(Article.fromJsonArray(results));

                    //Log.d(LOG_TAG, articles.toString());

                    //articleArrayAdapter.notifyItemRangeChanged(page * 10, 10);
                    //articleArrayAdapter.notifyItemRangeInserted(articleArrayAdapter.getItemCount(), articles.size());
                    //articleArrayAdapter.notifyDataSetChanged();

                } catch (JSONException e) {

                    e.printStackTrace();

                }

                if(page == 0) {
                    // Removing articles loader
                    articlesLoader.setVisibility(View.GONE);
                }


                // Removing swipe refresh
                swipeRefresh.setRefreshing(false);

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                Log.d(LOG_TAG+" On Failure", responseString);
                super.onFailure(statusCode, headers, responseString, throwable);

                // Removing articles loader
                articlesLoader.setVisibility(View.GONE);

                // Removing swipe refresh
                swipeRefresh.setRefreshing(false);
            }


            /**
             * Called on canceling the API Call
             */
            @Override
            public void onCancel() {
                super.onCancel();

                // Removing articles loader
                articlesLoader.setVisibility(View.GONE);

                // Removing swipe refresh
                swipeRefresh.setRefreshing(false);
            }

            /**
             * Called on finishing the API Call
             */
            @Override
            public void onFinish() {
                super.onFinish();

                // Removing articles loader
                articlesLoader.setVisibility(View.GONE);

                // Removing swipe refresh
                swipeRefresh.setRefreshing(false);
            }
        });

    }

    private void setScrollEventListener() {

        rvArticles.addOnScrollListener(new EndlessRecyclerViewScrollListener((StaggeredGridLayoutManager) rvArticles.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.d(LOG_TAG, String.valueOf(page));
                fetchArticles(page);
            }
        });

    }

}
