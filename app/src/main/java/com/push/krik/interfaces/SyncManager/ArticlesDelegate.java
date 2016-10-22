package com.push.krik.interfaces.SyncManager;

import com.push.krik.model.Article;

import java.util.ArrayList;

/**
 * Created by christopher on 7/14/16.
 */
public interface ArticlesDelegate
{
    void didRetreiveArticles(ArrayList<Article> articles);
}
