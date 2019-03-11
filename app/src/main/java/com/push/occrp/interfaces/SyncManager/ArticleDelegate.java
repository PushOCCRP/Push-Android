package com.push.occrp.interfaces.SyncManager;
import com.push.occrp.model.Article;

/**
 * Created by christopher on 7/14/16.
 */
public interface ArticleDelegate {
    void didRetrieveArticle(Article article);
}