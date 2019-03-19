package com.push.occrpnews.interfaces.SyncManager;
import com.push.occrpnews.model.Article;

/**
 * Created by christopher on 7/14/16.
 */
public interface ArticleDelegate {
    void didRetrieveArticle(Article article);
}