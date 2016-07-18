package com.softdesign.devintensive.data.network;

import com.softdesign.devintensive.data.network.req.UserLoginRec;
import com.softdesign.devintensive.data.network.res.UserListRes;
import com.softdesign.devintensive.data.network.res.UserModelRes;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface RestService {
    @POST("login")
    Call<UserModelRes> loginUser(@Body UserLoginRec req);

    // load photo on server
//    @Multipart
//    @POST("user/(userId)/publicValues/profilePhoto")
//    Call<UploadPhotoRes> uploadPhoto(@Part("userId") String userId,
//                                     @Part MultipartBody.Part file);

    @GET("user/list?orderBy=rating")
    Call<UserListRes> getUserList();
}
