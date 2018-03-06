package com.pushapp.press.interfaces.SyncManager;
import com.pushapp.press.model.Article;

/**
 * Created by christopher on 7/14/16.
 */
public interface ArticleDelegate {
    void didRetrieveArticle(Article article);
}