package com.marielm.flickrsearch.util;

public class ImageUrlUtil {

    private static final String baseUrl = "https://farm%s.staticflickr.com/%s/%s_%s_z.jpg";

    public static String getUrl(int farmId, String server, String id, String secret) {
        return String.format(baseUrl, String.valueOf(farmId), server, id, secret);
    }
}
