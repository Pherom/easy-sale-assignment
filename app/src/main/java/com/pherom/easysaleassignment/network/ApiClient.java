package com.pherom.easysaleassignment.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static volatile ApiClient instance;
    private Retrofit retrofit;
    private static final Object LOCK = new Object();

    private ApiClient() {
        retrofit = new Retrofit.Builder()
                .baseUrl("https://reqres.in/api")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static ApiClient getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new ApiClient();
                }
            }
        }
        return instance;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }
}
