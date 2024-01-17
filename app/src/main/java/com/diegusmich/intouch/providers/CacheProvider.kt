package com.diegusmich.intouch.providers

import android.content.Context
import java.io.File
import java.security.MessageDigest
import java.util.Date
import kotlin.random.Random

object CacheProvider{

    private lateinit var _dir : File
    val dir get() = _dir

    fun build(ctx: Context){
        _dir = File(ctx.cacheDir, "temp/")
        _dir.mkdir()
    }

    fun newTempFile(name: String? = null) : File{
        val fixedName = name ?: "object_${Date().time}_${Random.nextInt()}"
        return File(dir, fixedName)
    }

    fun clear(){
        _dir.delete()
    }
}