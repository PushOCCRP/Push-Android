package com.pushapp.test.interfaces.SyncManager;
import com.pushapp.test.model.Article;

/**
 * Created by christopher on 7/14/16.
 */
public interface ArticleDelegate {
    void didRetrieveArticle(Article article);
}