package com.theusmadev.coronareminder.data.network

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Url

interface CoronaApiService {

    @Headers("Content-Type: application/json;charset=UTF-8")
    @GET
    fun getCountries(@Url url: String): Call<Map<String,CountryStats>>
}

@Parcelize
data class CoronaStats(
    @SerializedName("confirmed") val confirmed: Long,
    @SerializedName("recovered") val recovered: Long,
    @SerializedName("deaths") val deaths: Long
): Parcelable

@Parcelize
data class CountryStats(
    @SerializedName("All") val data: CoronaStats
): Parcelable