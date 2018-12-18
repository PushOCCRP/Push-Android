package com.pushapp.press.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.pushapp.press.model.PushImage;

import java.lang.reflect.Type;

public class PushImageSerializer implements JsonSerializer<PushImage> {

    @Override
    public JsonElement serialize(PushImage src, Type typeOfSrc, JsonSerializationContext context){
        final JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("byline", src.getByline());
        jsonObject.addProperty("caption", src.getCaption());
        jsonObject.addProperty("url", src.getUrl());
        /*jsonObject.addProperty("length", src.getLength());
        jsonObject.addProperty("height", src.getHeight());
        jsonObject.addProperty("width", src.getWidth());
        jsonObject.addProperty("start", src.getStart());
*/
        return jsonObject;
    }

}
