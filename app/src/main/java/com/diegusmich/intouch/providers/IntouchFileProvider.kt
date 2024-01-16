package com.diegusmich.intouch.providers

import androidx.core.content.FileProvider
import com.diegusmich.intouch.R

class IntouchFileProvider: FileProvider(R.xml.file_paths){

    companion object {
        const val AUTHORITY = "com.diegusmich.fileprovider"
    }
}