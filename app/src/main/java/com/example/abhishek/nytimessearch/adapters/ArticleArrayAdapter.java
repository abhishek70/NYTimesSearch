package com.example.abhishek.nytimessearch.adapters;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.example.abhishek.nytimessearch.R;
import com.example.abhishek.nytimessearch.activities.ArticleDetailActivity;
import com.example.abhishek.nytimessearch.activities.SearchActivity;
import com.example.abhishek.nytimessearch.helpers.CustomTabActivityHelper;
import com.example.abhishek.nytimessearch.models.Article;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.google.android.gms.internal.zznu.iv;

/**
 * Created by abhishek on 10/19/16.
 */

public class ArticleArrayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /** Tag for the log messages */
    private static final String LOG_TAG = ArticleArrayAdapter.class.getSimpleName();


    public static final int WITHOUT_THUMBNAIL = 0;
    public static final int WITH_THUMBNAIL = 1;

    /** Member Variables */
    // Store a member variable for the articles
    private List<Article> mArticles;

    // Store the context for easy access
    private Context mContext;


    /**
     * Pass in the articles array into the constructor
     * @param context
     * @param articles
     */
    public ArticleArrayAdapter(Context context, List<Article> articles) {
        mArticles = articles;
        mContext = context;
    }

    /**
     * Getter for Articles
     * @return
     */
    public List<Article> getArticles() {
        return mArticles;
    }

    /**
     * Getter for Context
     * @return
     */
    public Context getContext() {
        return mContext;
    }



    /**
     * View Holder Class
     */
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Views
        @BindView(R.id.ivImage) ImageView ivImage;
        @BindView(R.id.tvHeadline) TextView tvHeadline;


        /**
         * View Holder Constructor
         * @param itemView
         */
        ViewHolder(View itemView) {

            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int pos = getLayoutPosition();
            Article article = mArticles.get(pos);
            Log.d(LOG_TAG + " On Click", article.getHeadline());

            /*Intent intent = new Intent(getContext(), ArticleDetailActivity.class);
            intent.putExtra("ArticleData", article);
            getContext().startActivity(intent);*/

            String url = article.getWebUrl();
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();

            Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_action_share);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, url);


            int requestCode = 100;

            PendingIntent pendingIntent = PendingIntent.getActivity(getContext(),
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            // set toolbar color
            builder.setToolbarColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

            builder.addDefaultShareMenuItem();

            // Map the bitmap, text, and pending intent to this icon
            // Set tint to be true so it matches the toolbar color
            builder.setActionButton(bitmap, "Share Link", pendingIntent, true);

            CustomTabsIntent customTabsIntent = builder.build();
            //customTabsIntent.launchUrl(getContext(), Uri.parse(url));

            CustomTabActivityHelper.openCustomTab((SearchActivity)getContext(), customTabsIntent, Uri.parse(url),
                    new CustomTabActivityHelper.CustomTabFallback() {
                        @Override
                        public void openUri(Activity activity, Uri uri) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            activity.startActivity(intent);
                        }
                    });
        }
    }



    /**
     * View Holder Class for handling only headline
     */
    class ViewHeadlineHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // Views
        @BindView(R.id.tvHeadline) TextView tvHeadline;


        /**
         * View Holder Constructor
         * @param itemView
         */
        ViewHeadlineHolder(View itemView) {

            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            int pos = getLayoutPosition();
            Article article = mArticles.get(pos);
            Log.d(LOG_TAG + " On Click", article.getHeadline());

            /*Intent intent = new Intent(getContext(), ArticleDetailActivity.class);
            intent.putExtra("ArticleData", article);
            getContext().startActivity(intent);*/

            String url = article.getWebUrl();
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();

            Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_action_share);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, url);


            int requestCode = 200;

            PendingIntent pendingIntent = PendingIntent.getActivity(getContext(),
                    requestCode,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            // set toolbar color
            builder.setToolbarColor(ContextCompat.getColor(getContext(), R.color.colorAccent));

            builder.addDefaultShareMenuItem();

            // Map the bitmap, text, and pending intent to this icon
            // Set tint to be true so it matches the toolbar color
            builder.setActionButton(bitmap, "Share Link", pendingIntent, true);

            CustomTabsIntent customTabsIntent = builder.build();
            //customTabsIntent.launchUrl(getContext(), Uri.parse(url));

            CustomTabActivityHelper.openCustomTab((SearchActivity)getContext(), customTabsIntent, Uri.parse(url),
                    new CustomTabActivityHelper.CustomTabFallback() {
                        @Override
                        public void openUri(Activity activity, Uri uri) {
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            activity.startActivity(intent);
                        }
                    });
        }
    }


    @Override
    public int getItemViewType(int position) {

        return mArticles.get(position).getViewType();

    }

    /**
     * Usually involves inflating a layout from XML and returning the holder
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case WITH_THUMBNAIL:
                    View withThumbNailView = inflater.inflate(R.layout.item_article, parent, false);
                    viewHolder = new ViewHolder(withThumbNailView);
                break;
            case WITHOUT_THUMBNAIL:
                    View withoutThumbNailView = inflater.inflate(R.layout.item_article_headline, parent, false);
                    viewHolder = new ViewHeadlineHolder(withoutThumbNailView);
                break;
        }

        return viewHolder;

        /*Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_article, parent, false);

        // Return a new holder instance
        return new ViewHolder(contactView);*/
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // Article Object
        Article article = mArticles.get(position);

        int viewType = article.getViewType();

        switch (viewType) {

            case WITH_THUMBNAIL:

                    ViewHolder withThumbNailHolder = (ViewHolder) holder;
                    layoutWithThumbNail(withThumbNailHolder, article);

                break;

            case WITHOUT_THUMBNAIL:

                    ViewHeadlineHolder withoutThumbNailHolder = (ViewHeadlineHolder) holder;
                    layoutWithoutThumbNail(withoutThumbNailHolder, article);

                break;
        }

    }

    /**
     * Involves populating data into the item through holder for layout with thumbnail
     * @param holder
     * @param article
     */
    private void layoutWithThumbNail(ViewHolder holder, Article article) {

        // Article Headline
        TextView tvHeadline = holder.tvHeadline;
        tvHeadline.setText(article.getHeadline());

        // Article Image using Picasso
        ImageView ivImage = holder.ivImage;
        ivImage.setImageResource(0);
        String thumbnail = article.getThumbNail();

        if(thumbnail != "") {
            Picasso.with(getContext())
                    .load(thumbnail)
                    .into(ivImage);
        }
    }


    /**
     * Involves populating data into the item through holder for layout without thumbnail
     * @param holder
     * @param article
     */
    private void layoutWithoutThumbNail(ViewHeadlineHolder holder, Article article) {

        // Article Headline
        TextView tvHeadline = holder.tvHeadline;
        tvHeadline.setText(article.getHeadline());
    }


    /**
     * Articles Array Size
     * @return
     */
    @Override
    public int getItemCount() {

        return mArticles.size();
    }


}
