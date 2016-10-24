/**
 *
 * This activity will load the News Article with search and filters functionality
 *
 */

package com.example.abhishek.nytimessearch.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
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
import com.example.abhishek.nytimessearch.fragments.FiltersFragment;
import com.example.abhishek.nytimessearch.models.Article;
import com.example.abhishek.nytimessearch.networking.ArticleClient;
import com.example.abhishek.nytimessearch.utils.EndlessRecyclerViewScrollListener;
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

import com.example.abhishek.nytimessearch.networking.CheckNetwork;




public class SearchActivity extends AppCompatActivity implements FiltersFragment.OnFragmentInteractionListener {

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


    // Flag for keeping the network connectivity
    boolean isInternetAvailable = true;


    // Recycler View for holding Articles
    @BindView(R.id.rvArticles) RecyclerView rvArticles;

    // Swipe refresh
    @BindView(R.id.swipeRefresh) SwipeRefreshLayout swipeRefresh;

    // Progress Loader
    @BindView(R.id.linlaHeaderProgress) LinearLayout articlesLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Binding views
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
    protected void onResume() {

        super.onResume();

        // Registering the network receiver
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(networkReceiver, filter);
    }


    @Override
    protected void onPause() {

        super.onPause();

        // UnRegistering the network receiver
        unregisterReceiver(networkReceiver);
    }


    /*
     * Method for Receiving the Network State
     */
    private BroadcastReceiver networkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent bufferIntent) {
            String status = CheckNetwork.getConnectivityStatusString(context);
            if(status.equals("WIFI") || status.equals("MOBILE")) {
                isInternetAvailable = true;
            } else if(status.equals("No Connection")) {
                isInternetAvailable = false;
            }
        }
    };

    /**
     * Setting up the menu options
     * Added Search View to the Action Bar
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);

        // Generating the Search View
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        // Search View Text Listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            /**
             * Called when the user submit the search query
             * @param query
             * @return
             */
            @Override
            public boolean onQueryTextSubmit(String query) {

                // Setting global search query
                mSearchQuery = query;

                // Calling Fetch Articles
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

    /**
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
         if(id == R.id.action_filters) {

             // Loading the Filter Settings
             loadFilterFragment(item);

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
                articlesLoader.setVisibility(View.GONE);
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

        fetchArticles(0);
    }

    /**
     * Fetching Articles Async
     */
    private void fetchArticles(final int page) {

        // Check for the internet connection
        if(isInternetAvailable) {

            // Initializing Async Client
            articleClient = new ArticleClient();

            String sortOrderStr;

            // Fetching Data from the Shared Preferences
            SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);

            Log.d(LOG_TAG, String.valueOf(sharedPref.getAll()));
            Log.d(LOG_TAG, mSearchQuery);

            // Date
            String beginDate = sharedPref.getString("beginDate", "").replace("-", "");

            // Order
            int sortOrder = sharedPref.getInt("sortOrder", 0);
            if (sortOrder == 0) {
                sortOrderStr =  "newest";
            } else {
                sortOrderStr =  "oldest";
            }

            // Query
            String searchQuery = newsDeskFilters();
            if (searchQuery != null) {
                if (mSearchQuery != "") {
                    mSearchQuery += " AND " + searchQuery;
                } else {
                    mSearchQuery = searchQuery;
                }
            }

            if(page == 0 && articles.size() == 0) {
                articlesLoader.setVisibility(View.VISIBLE);
            }

            // Fetching all Articles based on the Filter if available
            articleClient.getArticles(mSearchQuery, page, sortOrderStr, beginDate,  new JsonHttpResponseHandler() {

                @Override
                public void onStart() {
                    super.onStart();
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


        } else {

            // Removing articles loader
            articlesLoader.setVisibility(View.GONE);

            // Removing swipe refresh
            swipeRefresh.setRefreshing(false);

            // Showing Snackbar for showing "No Connection"
            final Snackbar snackBar = Snackbar.make(findViewById(android.R.id.content), R.string.no_connection, Snackbar.LENGTH_INDEFINITE);
            snackBar.setAction(R.string.retry, new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    clearArticles();

                    snackBar.dismiss();
                }
            }).setActionTextColor(getResources().getColor(R.color.colorWarning)).show();

        }

    }

    /**
     * Called when the user scrolls
     */
    private void setScrollEventListener() {

        rvArticles.addOnScrollListener(new EndlessRecyclerViewScrollListener((StaggeredGridLayoutManager) rvArticles.getLayoutManager()) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.d(LOG_TAG, String.valueOf(page));
                fetchArticles(page);
            }
        });

    }

    /**
     * Loading the filter fragment
     * @param menuItem
     */
    private void loadFilterFragment(MenuItem menuItem) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FiltersFragment filtersFragment = FiltersFragment.newInstance("filters");
        filtersFragment.show(fragmentManager, "fragment_filters");

    }

    // Called when the user save the filters from the filter fragment
    @Override
    public void onFragmentInteraction() {
        clearArticles();
    }


    /**
     * Fetching and formatting the news Desk Search Queries
     * @return
     */
    private String newsDeskFilters() {
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        Boolean newsDeskArts = sharedPref.getBoolean("newsDeskArts", false);
        Boolean newsDeskFashion = sharedPref.getBoolean("newsDeskFashion", false);
        Boolean newsDeskSports = sharedPref.getBoolean("newsDeskSports", false);

        if (!newsDeskArts && !newsDeskFashion && !newsDeskSports) {
            return null;
        }

        String query = "news_desk:(";
        if (newsDeskArts) {
            query += "\"Arts\" ";
        }
        if (newsDeskFashion) {
            query += "\"Fashion\" ";
        }
        if (newsDeskSports) {
            query += "\"Sports\" ";
        }

        query = query.substring(0, query.length() - 1);
        query += ")";

        return query;
    }
}
