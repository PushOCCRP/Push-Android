package com.pushapp.test.interfaces.SyncManager;

import com.pushapp.test.model.Article;

import java.util.ArrayList;

import retrofit.RetrofitError;

/**
 * Created by christopher on 7/14/16.
 */
public interface ArticlesDelegate
{
    void didRetrieveArticles(Object articles, ArrayList<String> categories, RetrofitError error);
}
