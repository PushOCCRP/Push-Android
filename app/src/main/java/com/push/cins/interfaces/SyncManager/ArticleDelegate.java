package com.push.cins.interfaces.SyncManager;
import com.push.cins.model.Article;

/**
 * Created by christopher on 7/14/16.
 */
public interface ArticleDelegate {
    void didRetrieveArticle(Article article);
}