package com.example.abhishek.nytimessearch.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextClock;
import android.widget.TextView;

import com.example.abhishek.nytimessearch.R;
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

public class ArticleArrayAdapter extends RecyclerView.Adapter<ArticleArrayAdapter.ViewHolder> {

    /** Tag for the log messages */
    private static final String LOG_TAG = ArticleArrayAdapter.class.getSimpleName();

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

//        ImageView ivImage;
//        TextView tvHeadline;

        /**
         * View Holder Constructor
         * @param itemView
         */
        ViewHolder(View itemView) {

            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);

//            ivImage     = (ImageView) itemView.findViewById(R.id.ivImage);
//            tvHeadline  = (TextView) itemView.findViewById(R.id.tvHeadline);

        }

        @Override
        public void onClick(View view) {
            int pos = getLayoutPosition();
            Article article = mArticles.get(pos);

        }
    }

    /**
     * Usually involves inflating a layout from XML and returning the holder
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View contactView = inflater.inflate(R.layout.item_article, parent, false);

        // Return a new holder instance
        return new ViewHolder(contactView);
    }

    /**
     * Involves populating data into the item through holder
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        // Article Object
        Article article = mArticles.get(position);

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
     * Articles Array Size
     * @return
     */
    @Override
    public int getItemCount() {
        return mArticles.size();
    }


}
