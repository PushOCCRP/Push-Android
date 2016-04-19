package com.push.risemd.interfaces;

import com.push.risemd.model.ArticlePost;

import retrofit.Callback;

import retrofit.http.GET;
import retrofit.http.Query;

/**
 * @author Bryan Lamtoo.
 */
public interface RestApi {

    @GET("/articles")
    void getArticles(Callback<ArticlePost> response);

    @GET("/articles")
    void getArticles(@Query("language")String language, Callback<ArticlePost> response);

    @GET("/articles")
    void getArticles(@Query("start_date")long start_date,@Query("end_date")long end_date,@Query("pages")int pages,@Query("page_size")int page_size, Callback<ArticlePost> response);

    @GET("/articles")
    void getArticles(@Query("start_date")long start_date,@Query("end_date")long end_date,@Query("pages")int pages,@Query("page_size")int page_siz,@Query("language")String language ,Callback<ArticlePost> response);

    @GET("/search")
    void searchArticles(@Query("q")String query,@Query("start_date")long start_date,@Query("end_date")long end_date,@Query("pages")int pages,@Query("page_size")int page_size,Callback<ArticlePost> response);

    @GET("/search")
    void searchArticles(@Query("q")String query,@Query("start_date")long start_date,@Query("end_date")long end_date,@Query("pages")int pages,@Query("page_size")int page_size,@Query("language")String language,Callback<ArticlePost> response);

    @GET("/article")
    void getArticle(@Query("id")String id, Callback<ArticlePost> response);

    @GET("/article")
    void getArticle(@Query("id")String id,@Query("language")String language, Callback<ArticlePost> response);
}
