package com.example.machinetask.model

import com.google.gson.annotations.SerializedName

data class Question(
    @SerializedName("answer_id") val answerId: Int,
    @SerializedName("countries") val countries: List<Country>,
    @SerializedName("country_code") val countryCode: String
)

data class Country(
    @SerializedName("country_name") val countryName: String,
    @SerializedName("id") val id: Int
)

data class QuestionsResponse(
    @SerializedName("questions") val questions: List<Question>
)