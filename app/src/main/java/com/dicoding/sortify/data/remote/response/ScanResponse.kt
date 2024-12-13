package com.dicoding.sortify.data.remote.response

import com.google.gson.annotations.SerializedName

data class ScanResponse(

    @field:SerializedName("confidence")
    val confidence: String,

    @field:SerializedName("class")
    val jsonMemberClass: String,

    @field:SerializedName("recommendations")
    val recommendations: List<RecommendationsItem?>
)

data class RecommendationsItem(

    @field:SerializedName("link")
    val link: String? = null,

    @field:SerializedName("title")
    val title: String? = null
)