package com.example.wallpaperapp.util

import android.app.WallpaperManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

enum class WallpaperTarget { HOME, LOCK, BOTH }

object WallpaperSetter {

    private suspend fun downloadBitmap(url: String): Bitmap = withContext(Dispatchers.IO) {
        val connection = URL(url).openConnection()
        connection.connectTimeout = 15000
        connection.readTimeout = 15000
        connection.connect()
        BitmapFactory.decodeStream(connection.getInputStream())
    }

    suspend fun apply(context: Context, url: String, target: WallpaperTarget) {
        val bitmap = downloadBitmap(url)
        val wm = WallpaperManager.getInstance(context)
        withContext(Dispatchers.IO) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                when (target) {
                    WallpaperTarget.HOME ->
                        wm.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM)
                    WallpaperTarget.LOCK ->
                        wm.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
                    WallpaperTarget.BOTH -> {
                        wm.setBitmap(bitmap, null, true, WallpaperManager.FLAG_SYSTEM)
                        wm.setBitmap(bitmap, null, true, WallpaperManager.FLAG_LOCK)
                    }
                }
            } else {
                wm.setBitmap(bitmap)
            }
        }
    }
}
