package com.abdu.firebasechatdemo.WebServices;

import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Url;

public interface Api {

    @POST
    Call<ResponseBody> sendNotification(@Url String url, @Body Map<String, Object> map, @Header("Authorization") String key);

}
