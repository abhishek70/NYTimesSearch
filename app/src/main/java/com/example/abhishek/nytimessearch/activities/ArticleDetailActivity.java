package com.example.abhishek.nytimessearch.activities;

import android.annotation.TargetApi;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.abhishek.nytimessearch.R;
import com.example.abhishek.nytimessearch.models.Article;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.google.android.gms.common.api.Status.we;

public class ArticleDetailActivity extends AppCompatActivity {

    /** Tag for the log messages */
    private static final String LOG_TAG = ArticleDetailActivity.class.getSimpleName();



    @BindView(R.id.webview) WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article_detail);

        ButterKnife.bind(this);


        initiateWebView();
    }


    private void initiateWebView() {

        Article article = (Article) getIntent().getParcelableExtra("ArticleData");

        Log.d(LOG_TAG, article.getHeadline());


        // Configure related browser settings
        webView.getSettings().setLoadsImagesAutomatically(true);

        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        // Configure the client to use when opening URLs
        webView.setWebViewClient(new WebBrowser());
        // Load the initial URL
        webView.loadUrl(article.getWebUrl());

        // Enable responsive layout
        webView.getSettings().setUseWideViewPort(true);

        // Zoom out if the content width is greater than the width of the veiwport
        webView.getSettings().setLoadWithOverviewMode(true);


        webView.getSettings().setSupportZoom(true);

        // allow pinch to zooom
        webView.getSettings().setBuiltInZoomControls(true);

        // disable the default zoom controls on the page
        webView.getSettings().setDisplayZoomControls(false);

    }


    // Manages the behavior when URLs are loaded
    private class WebBrowser extends WebViewClient {
        @SuppressWarnings("deprecation")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        @TargetApi(Build.VERSION_CODES.N)
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            view.loadUrl(request.getUrl().toString());
            return true;
        }
    }
}
