package com.push.krik.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.push.krik.model.PushVideo;

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
