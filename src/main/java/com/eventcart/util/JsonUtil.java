package com.eventcart.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * Utility for JSON serialization/deserialization using Google Gson.
 */
public final class JsonUtil {

    private static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .serializeNulls()
            .create();

    private JsonUtil() {
        // Utility class
    }

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        return gson.fromJson(json, clazz);
    }

    public static Gson getGson() {
        return gson;
    }
}
