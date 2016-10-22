package com.push.krik.interfaces.SyncManager;
import com.push.krik.model.Article;

/**
 * Created by christopher on 7/14/16.
 */
public interface ArticleDelegate {
    void didRetrieveArticle(Article article);
}