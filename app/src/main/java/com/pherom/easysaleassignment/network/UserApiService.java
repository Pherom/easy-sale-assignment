package com.pherom.easysaleassignment.network;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UserApiService {

    @GET("/users")
    Call<UserPage> getUserPage(@Query("page") int pageNum);

    @GET("/users/{Id}")
    Call<UserDto> getUser(@Path("Id") int id);
}
