package com.project.spencerkunkel.photogallery.api;

import com.google.gson.Gson;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class PhotoDeserializer implements com.google.gson.JsonDeserializer<PhotoResponse> {
    @Override
    public PhotoResponse deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json != null ? json.getAsJsonObject() : null;
        return new Gson().fromJson(jsonObject, PhotoResponse.class);
    }
}
