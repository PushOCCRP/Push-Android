package com.push.krik.interfaces;

import com.push.krik.model.ArticlePost;

import retrofit.Callback;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * @author Bryan Lamtoo.
 */
public interface RestApi {

    @GET("/articles.json")
    void getArticles(Callback<ArticlePost> response);

    @GET("/articles.json")
    void getArticles(@Query("language")String language, Callback<ArticlePost> response);

    @GET("/articles.json")
    void getArticles(@Query("start_date")long start_date,@Query("end_date")long end_date,@Query("pages")int pages,@Query("page_size")int page_size, Callback<ArticlePost> response);

    @GET("/articles.json")
    void getArticles(@Query("start_date")long start_date,@Query("end_date")long end_date,@Query("pages")int pages,@Query("page_size")int page_size, @Query("language")String language ,Callback<ArticlePost> response);

    @GET("/search.json")
    void searchArticles(@Query("q")String query,@Query("start_date")long start_date,@Query("end_date")long end_date,@Query("pages")int pages,@Query("page_size")int page_size,Callback<ArticlePost> response);

    @GET("/search.json")
    void searchArticles(@Query("q")String query,@Query("start_date")long start_date,@Query("end_date")long end_date,@Query("pages")int pages,@Query("page_size")int page_size,@Query("language")String language,Callback<ArticlePost> response);

    @GET("/article.json")
    void getArticle(@Query("id")String id, Callback<ArticlePost> response);

    @GET("/article.json")
    void getArticle(@Query("id")String id,@Query("language")String language, Callback<ArticlePost> response);
}
