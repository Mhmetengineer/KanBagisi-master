package com.example.kanbagisi.service;

public class UtilsApi {

    public static final String BASE_URL_API =
            "https://api.netgsm.com.tr/";

    public static ApiService getAPIService(){
        return RetrofitClient.getClient(BASE_URL_API).create(ApiService.class);
    }
}
