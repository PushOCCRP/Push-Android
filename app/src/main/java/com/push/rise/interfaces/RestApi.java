package com.push.rise.interfaces;

import com.push.rise.model.ArticlePost;

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
    void getArticles(Callback<ArticlePost> response, @Query("categories")boolean categories);

    @GET("/articles.json")
    void getArticles(@Query("language")String language, Callback<ArticlePost> response);

    @GET("/articles.json")
    void getArticles(@Query("language")String language, @Query("categories")boolean categories, Callback<ArticlePost> response);

    @GET("/articles.json")
    void getArticles(@Query("start_date")long start_date,@Query("end_date")long end_date,@Query("pages")int pages,@Query("page_size")int page_size, Callback<ArticlePost> response);

    @GET("/articles.json")
    void getArticles(@Query("categories")boolean categories, @Query("start_date")long start_date,@Query("end_date")long end_date,@Query("pages")int pages,@Query("page_size")int page_size, Callback<ArticlePost> response);

    @GET("/articles.json")
    void getArticles(@Query("start_date")long start_date,@Query("end_date")long end_date,@Query("pages")int pages,@Query("page_size")int page_size, @Query("language")String language ,Callback<ArticlePost> response);

    @GET("/articles.json")
    void getArticles(@Query("categories")boolean categories,@Query("start_date")long start_date,@Query("end_date")long end_date,@Query("pages")int pages,@Query("page_size")int page_size, @Query("language")String language ,Callback<ArticlePost> response);

    @GET("/search.json")
    void searchArticles(@Query("q")String query,@Query("start_date")long start_date,@Query("end_date")long end_date,@Query("pages")int pages,@Query("page_size")int page_size,Callback<ArticlePost> response);

    @GET("/search.json")
    void searchArticles(@Query("q")String query,@Query("start_date")long start_date,@Query("end_date")long end_date,@Query("pages")int pages,@Query("page_size")int page_size,@Query("language")String language,Callback<ArticlePost> response);

    @GET("/article.json")
    void getArticle(@Query("id")String id, Callback<ArticlePost> response);

    @GET("/article.json")
    void getArticle(@Query("id")String id,@Query("language")String language, Callback<ArticlePost> response);
}
