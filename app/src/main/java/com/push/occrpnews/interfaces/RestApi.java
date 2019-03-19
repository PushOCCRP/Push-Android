package com.push.occrpnews.interfaces;

import com.push.occrpnews.model.ArticlePost;
import com.push.occrpnews.model.LoginRequest;

import java.util.HashMap;

import retrofit.Callback;

import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * @author Bryan Lamtoo.
 */
public interface RestApi {

    @GET("/articles.json")
    void getArticles(@Query("installation_uuid")String installation_uuid, @Query("api_key")String api_key, Callback<ArticlePost> response);

    @GET("/articles.json")
    void getArticles(@Query("installation_uuid")String installation_uuid, @Query("api_key")String api_key, Callback<ArticlePost> response, @Query("categories")boolean categories);

    @GET("/articles.json")
    void getArticles(@Query("installation_uuid")String installation_uuid, @Query("api_key")String api_key, @Query("language")String language, Callback<ArticlePost> response);

    @GET("/articles.json")
    void getArticles(@Query("installation_uuid")String installation_uuid, @Query("api_key")String api_key, @Query("language")String language, @Query("categories")boolean categories, Callback<ArticlePost> response);

    @GET("/articles.json")
    void getArticles(@Query("installation_uuid")String installation_uuid, @Query("api_key")String api_key, @Query("start_date")long start_date,@Query("end_date")long end_date,@Query("pages")int pages,@Query("page_size")int page_size, Callback<ArticlePost> response);

    @GET("/articles.json")
    void getArticles(@Query("installation_uuid")String installation_uuid, @Query("api_key")String api_key, @Query("categories")boolean categories, @Query("start_date")long start_date,@Query("end_date")long end_date,@Query("pages")int pages,@Query("page_size")int page_size, Callback<ArticlePost> response);

    @GET("/articles.json")
    void getArticles(@Query("installation_uuid")String installation_uuid, @Query("api_key")String api_key, @Query("start_date")long start_date,@Query("end_date")long end_date,@Query("pages")int pages,@Query("page_size")int page_size, @Query("language")String language ,Callback<ArticlePost> response);

    @GET("/articles.json")
    void getArticles(@Query("installation_uuid")String installation_uuid, @Query("api_key")String api_key, @Query("categories")boolean categories,@Query("start_date")long start_date,@Query("end_date")long end_date,@Query("pages")int pages,@Query("page_size")int page_size, @Query("language")String language ,Callback<ArticlePost> response);

    @GET("/search.json")
    void searchArticles(@Query("installation_uuid")String installation_uuid, @Query("api_key")String api_key, @Query("q")String query,@Query("start_date")long start_date,@Query("end_date")long end_date,@Query("pages")int pages,@Query("page_size")int page_size,Callback<ArticlePost> response);

    @GET("/search.json")
    void searchArticles(@Query("installation_uuid")String installation_uuid, @Query("api_key")String api_key, @Query("q")String query,@Query("start_date")long start_date,@Query("end_date")long end_date,@Query("pages")int pages,@Query("page_size")int page_size,@Query("language")String language,Callback<ArticlePost> response);

    @GET("/article.json")
    void getArticle(@Query("installation_uuid")String installation_uuid, @Query("api_key")String api_key, @Query("id")String id, Callback<ArticlePost> response);

    @GET("/article.json")
    void getArticle(@Query("installation_uuid")String installation_uuid, @Query("api_key")String api_key, @Query("id")String id,@Query("language")String language, Callback<ArticlePost> response);

    @POST("/authenticate")
    void login(@Body()HashMap body, Callback<LoginRequest> response);
    //void login(@Query("installation_uuid")String installation_uuid, @Query("language")String language, @Query("username")String username, @Query("password")String password, Callback<LoginRequest> response);
}
