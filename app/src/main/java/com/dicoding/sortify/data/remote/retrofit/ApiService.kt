package com.dicoding.sortify.data.remote.retrofit

import com.dicoding.sortify.data.remote.response.ArticleResponse
import com.dicoding.sortify.data.remote.response.ScanResponse
import okhttp3.MultipartBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @GET("articles")
    suspend fun getArticles(): ArticleResponse

    @Multipart
    @POST("predict")
    suspend fun uploadPredict(
        @Part file : MultipartBody.Part,
    ): ScanResponse
}
