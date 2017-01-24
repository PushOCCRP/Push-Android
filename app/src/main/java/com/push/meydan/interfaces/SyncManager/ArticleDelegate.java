package com.push.meydan.interfaces.SyncManager;
import com.push.meydan.model.Article;

/**
 * Created by christopher on 7/14/16.
 */
public interface ArticleDelegate {
    void didRetrieveArticle(Article article);
}