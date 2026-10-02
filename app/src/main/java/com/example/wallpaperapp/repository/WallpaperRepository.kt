package com.example.wallpaperapp.repository

import com.example.wallpaperapp.model.Photo
import com.example.wallpaperapp.network.ApiClients

object WallpaperRepository {

    // سجّل مجاني واحصل على مفتاحك من: https://unsplash.com/developers
    private const val UNSPLASH_ACCESS_KEY = "ضع_مفتاح_Unsplash_هنا"

    // &w=3840 للـ 4K أو &w=7680 للـ 8K (كلما زادت الدقة زاد حجم التحميل)
    private const val RESOLUTION_PARAM = "&w=3840&q=90&fm=jpg"

    suspend fun getNature(): List<Photo> {
        val response = ApiClients.unsplash.search(
            query = "nature landscape wallpaper",
            clientId = UNSPLASH_ACCESS_KEY
        )
        return response.results.map {
            Photo(id = it.id, thumbUrl = it.urls.small, fullUrl = it.urls.raw + RESOLUTION_PARAM)
        }
    }

    suspend fun getRegular(): List<Photo> {
        val response = ApiClients.unsplash.search(
            query = "minimal abstract wallpaper",
            clientId = UNSPLASH_ACCESS_KEY
        )
        return response.results.map {
            Photo(id = it.id, thumbUrl = it.urls.small, fullUrl = it.urls.raw + RESOLUTION_PARAM)
        }
    }

    suspend fun getAnime(): List<Photo> {
        val response = ApiClients.neko.getImages(category = "neko", amount = 20)
        return response.results.mapIndexed { index, r ->
            Photo(id = index.toString(), thumbUrl = r.url, fullUrl = r.url)
        }
    }
}
