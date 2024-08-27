package com.pherom.easysaleassignment.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static volatile ApiClient instance;
    private final Retrofit retrofit;
    private static final Object LOCK = new Object();

    private ApiClient(String baseUrl) {
        retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static ApiClient getInstance(String baseUrl) {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new ApiClient(baseUrl);
                }
            }
        }
        return instance;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
