package com.pushapp.press.model;

import java.util.ArrayList;
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

    @PrimaryKey
    private String category;
    private String language;
    private RealmList<Article> articles;

    public Category(){}

    public Category(String category, String language, RealmList<Article> articles){
        this.category = category;
        this.language = language;
        this.articles = articles;
    }


}
