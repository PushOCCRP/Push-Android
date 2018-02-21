package com.pushapp.missourian.interfaces.SyncManager;

import com.pushapp.missourian.model.Article;

import java.util.ArrayList;

import retrofit.RetrofitError;

/**
 * Created by christopher on 7/14/16.
 */
public interface ArticlesDelegate
{
    void didRetrieveArticles(Object articles, ArrayList<String> categories, RetrofitError error);
}
