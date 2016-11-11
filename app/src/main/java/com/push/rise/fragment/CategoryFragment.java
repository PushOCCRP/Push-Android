package com.push.rise.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.github.ksoichiro.android.observablescrollview.ObservableScrollViewCallbacks;
import com.github.ksoichiro.android.observablescrollview.ScrollState;
import com.push.rise.CategoryActivity;
import com.push.rise.DetailPostActivity;
import com.push.rise.HomeActivity;
import com.push.rise.R;
import com.push.rise.adapter.PostFragmentAdapter;
import com.push.rise.adapter.PostListAdapter;
import com.push.rise.model.Article;
import com.push.rise.util.AnalyticsManager;

import java.util.ArrayList;


/**
 * Created by christopher on 10/29/16.
 */

public class CategoryFragment extends Fragment implements ObservableScrollViewCallbacks {

    private PostListAdapter mListAdapter;
    public ArrayList<Article> articles;

    private View mRootView;
    private ListView mListView;

    public CategoryFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mRootView = inflater.inflate(R.layout.fragment_category, container, false);
        mListView = (ListView)mRootView.findViewById(R.id.category_lists);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                PostFragmentAdapter.postItems = articles;

                Article article = articles.get(position);
                Intent i = new Intent(getActivity().getApplicationContext(), DetailPostActivity.class);

                i.putExtra("postPosition", articles.indexOf(article));
                i.putExtra("postTitle", article.getHeadline());
                i.putExtra("description", article.getDescription());


                startActivity(i);
                AnalyticsManager.logContentView(article.getHeadline(),
                        "Article Opened", Integer.toString(article.getId()));
            }
        });

        Intent i = getActivity().getIntent();

        articles = (ArrayList<Article>) i.getExtras().get("articles");
        setArticles(articles);

        return mRootView;
    }

    public void setArticles(ArrayList<Article> articles) {
        this.articles = articles;

        mListAdapter = new PostListAdapter(getContext(), R.layout.list_news_item, articles);
        mListView.setAdapter(mListAdapter);
    }

    @Override
    public void onScrollChanged(int scrollY, boolean firstScroll,
                                boolean dragging) {
    }

    @Override
    public void onDownMotionEvent() {
    }

    @Override
    public void onUpOrCancelMotionEvent(ScrollState scrollState) {
    }

}
