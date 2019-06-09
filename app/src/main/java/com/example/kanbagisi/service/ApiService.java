package com.example.kanbagisi.service;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {
    @GET("/sms/send/get/?usercode=8503034248&password=h35710&msgheader=GODEK")
    Call<ResponseBody> sendMessage(@Query("gsmno") String gsmno, @Query("message") String message);
}