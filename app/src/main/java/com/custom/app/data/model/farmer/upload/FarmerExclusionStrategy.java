package com.custom.app.data.model.farmer.upload;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class FarmerExclusionStrategy implements ExclusionStrategy {

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return false;
    }

    @Override
    public boolean shouldSkipField(FieldAttributes field) {
        return field.getName().equals("code") || field.getName().equals("isUploaded");
    }

    public Gson getGson() {
        return new GsonBuilder().addSerializationExclusionStrategy(this).create();
    }
}