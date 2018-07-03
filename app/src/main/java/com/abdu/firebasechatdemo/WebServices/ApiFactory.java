package com.abdu.firebasechatdemo.WebServices;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiFactory {

    public static final String BASE_URL = "https://fcm.googleapis.com/fcm/send/";
    private static OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

    static OkHttpClient client = new OkHttpClient.Builder() .connectTimeout(10000, TimeUnit.SECONDS) .writeTimeout(10000, TimeUnit.SECONDS) .readTimeout(10000, TimeUnit.SECONDS).build();
    private static Retrofit retrofit = null;
    private static Retrofit retrofits = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
