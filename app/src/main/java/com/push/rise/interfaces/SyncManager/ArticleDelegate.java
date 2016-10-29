package com.push.rise.interfaces.SyncManager;
import com.push.rise.model.Article;

/**
 * Created by christopher on 7/14/16.
 */
public interface ArticleDelegate {
    void didRetrieveArticle(Article article);
}