package com.push.krik.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.push.krik.model.PushImage;

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
