package com.jikokujo.core.utils

import android.content.Context
import androidx.annotation.RawRes
import java.io.File
import java.io.FileOutputStream

fun Context.rawFile(@RawRes resId: Int): File = File(cacheDir, "raw_${resources.getResourceEntryName(resId)}").apply {
    if(!exists()) {
        resources.openRawResource(resId).use { input ->
            FileOutputStream(this).use { output ->
                input.copyTo(output)
            }
        }
    }
}