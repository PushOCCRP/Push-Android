package com.pushapp.missourian.interfaces.SyncManager;
import com.pushapp.missourian.model.Article;

/**
 * Created by christopher on 7/14/16.
 */
public interface ArticleDelegate {
    void didRetrieveArticle(Article article);
}