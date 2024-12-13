package com.dicoding.sortify.data.remote.retrofit

import com.dicoding.sortify.data.remote.response.ArticleResponse
import retrofit2.http.GET

interface ApiServiceNews {
    @GET("articles")
    suspend fun getArticles(): ArticleResponse
}