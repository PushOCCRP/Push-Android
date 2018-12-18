package com.pushapp.press.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.pushapp.press.model.Article;
import com.pushapp.press.model.PushImage;
import com.pushapp.press.model.PushVideo;

import java.lang.reflect.Type;

public class ArticleSerializer implements JsonSerializer<Article> {

    @Override
    public JsonElement serialize(Article src, Type typeOfSrc, JsonSerializationContext context){
        final JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("id", src.getId());
        jsonObject.addProperty("headline", src.getHeadline());
        jsonObject.addProperty("description", src.getDescription());
        jsonObject.addProperty("body", src.getBody());
        jsonObject.addProperty("publishDate", src.getPublishDate());
        jsonObject.addProperty("author", src.getAuthor());
        jsonObject.addProperty("organization", src.getOrganization());
        jsonObject.addProperty("language", src.getLanguage());
        JsonArray images = new JsonArray();
        for (PushImage image : src.getImages()){
         images.add(context.serialize(image, PushImage.class));
        }
        jsonObject.add("images", images);

        JsonArray videos = new JsonArray();
        for (PushVideo video : src.getVideos()){
            videos.add(context.serialize(video, PushImage.class));
        }
        jsonObject.add("videos", videos);

        jsonObject.add("header_image", context.serialize(src.getHeaderImage(), PushImage.class));
        JsonArray image_urls = new JsonArray();
        for (String image_url : src.getImageUrls()){
            image_urls.add(image_url);
        }
        jsonObject.add("image_urls",image_urls);
        JsonArray captions = new JsonArray();
        for (String caption : src.getCaptions()){
            image_urls.add(caption);
        }
        jsonObject.add("captions",captions);
        JsonArray photoBylines = new JsonArray();
        for (String photoByline : src.getPhotoBylines()){
            image_urls.add(photoByline);
        }
        jsonObject.add("photoBylines",photoBylines);


        jsonObject.addProperty("url", src.getUrl());

        return jsonObject;
    }

}
