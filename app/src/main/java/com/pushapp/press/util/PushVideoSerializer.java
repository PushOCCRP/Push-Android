package com.pushapp.press.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.pushapp.press.model.PushVideo;

import java.lang.reflect.Type;

public class PushVideoSerializer implements JsonSerializer<PushVideo> {

    @Override
    public JsonElement serialize(PushVideo src, Type typeOfSrc, JsonSerializationContext context){
        final JsonObject jsonObject = new JsonObject();
        //
        jsonObject.addProperty("youtubeId", src.getYoutubeId());

        return jsonObject;
    }

}
