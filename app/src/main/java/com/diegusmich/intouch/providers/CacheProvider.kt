package com.diegusmich.intouch.providers

import android.content.Context
import java.io.File
import java.util.Date
import kotlin.random.Random

object CacheProvider{

    private lateinit var _imagesCache : File
    val images get() = _imagesCache

    fun build(ctx: Context){
        _imagesCache = File(ctx.cacheDir, "images/")
        _imagesCache.mkdir()
    }

    fun newImageTempFile() : File{
        return File(images, "temp_${Random.nextInt()}_${Date().time}.jpg")
    }

    fun clear(){
        _imagesCache.delete()
    }
}