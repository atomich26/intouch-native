package com.diegusmich.intouch.utils

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.diegusmich.intouch.providers.CacheProvider
import id.zelory.compressor.Compressor
import id.zelory.compressor.constraint.format
import id.zelory.compressor.constraint.quality
import id.zelory.compressor.constraint.size
import java.io.File

object FileUtil {

    suspend fun compressImage(ctx: Context, src: File, maxSize : Long) : File {
        return Compressor.compress(ctx, src){
            quality(100)
            format(Bitmap.CompressFormat.JPEG)
            size(maxSize)
        }
    }

    fun fileFromContentUri(ctx: Context, uri : Uri) : File?{
        return ctx.contentResolver.openInputStream(uri)?.let {
            val bytesArray = it.readBytes()
            it.close()

            CacheProvider.newTempFile().apply {
                writeBytes(bytesArray)
            }
        }
    }
}