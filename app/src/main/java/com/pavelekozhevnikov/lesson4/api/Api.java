package com.pavelekozhevnikov.lesson4.api;
import com.pavelekozhevnikov.lesson4.model.GithubUser;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
public interface Api {
    @GET("users/{user}")
    Call<GithubUser> loadUser(@Path("user") String user);

}
