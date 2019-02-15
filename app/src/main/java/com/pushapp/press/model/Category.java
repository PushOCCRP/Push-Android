package com.pushapp.press.model;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class Category extends RealmObject {

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public RealmList<Article> getArticles() {
        return articles;
    }

    public void setArticles(RealmList<Article> articles) {
        this.articles = articles;
    }

    public String getOrderIndex() {
        return orderIndex;
    }

    public void setOrderIndex(String orderIndex) {
        this.orderIndex = orderIndex;
    }

    @PrimaryKey
    public String category;
    public String language;
    public String orderIndex;
    public RealmList<Article> articles;

    public Category(){}

    public Category(String category, String language, RealmList<Article> articles, String orderIndex){
        this.category = category;
        this.language = language;
        this.articles = articles;
        this.orderIndex = orderIndex;
    }

}
