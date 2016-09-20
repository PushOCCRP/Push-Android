package com.push.cins.interfaces.SyncManager;

import com.push.cins.model.Article;

import java.util.ArrayList;

/**
 * Created by christopher on 7/14/16.
 */
public interface ArticlesDelegate
{
    void didRetreiveArticles(ArrayList<Article> articles);
}
