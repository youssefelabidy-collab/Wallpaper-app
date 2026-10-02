package com.example.wallpaperapp.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

// ---------- Unsplash (طبيعة + عادية) ----------

data class UnsplashUrls(
    val raw: String,
    val regular: String,
    val small: String
)

data class UnsplashPhoto(
    val id: String,
    val urls: UnsplashUrls
)

data class UnsplashSearchResponse(
    val results: List<UnsplashPhoto>
)

interface UnsplashApi {
    @GET("search/photos")
    suspend fun search(
        @Query("query") query: String,
        @Query("per_page") perPage: Int = 30,
        @Query("client_id") clientId: String
    ): UnsplashSearchResponse
}

// ---------- Nekos.best (أنمي) ----------

data class NekoResult(val url: String)
data class NekoResponse(val results: List<NekoResult>)

interface NekoApi {
    @GET("api/v2/{category}")
    suspend fun getImages(
        @Path("category") category: String,
        @Query("amount") amount: Int = 20
    ): NekoResponse
}

object ApiClients {
    private val gsonFactory = GsonConverterFactory.create()

    val unsplash: UnsplashApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.unsplash.com/")
            .addConverterFactory(gsonFactory)
            .build()
            .create(UnsplashApi::class.java)
    }

    val neko: NekoApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://nekos.best/")
            .addConverterFactory(gsonFactory)
            .build()
            .create(NekoApi::class.java)
    }
}
