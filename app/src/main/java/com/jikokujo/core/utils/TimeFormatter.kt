package com.jikokujo.core.utils

import android.annotation.SuppressLint

@SuppressLint("DefaultLocale")
fun timeFormatter(atMins: Int, withColon: Boolean = true): String{
    val hours = (atMins / 60)
    val minutes = (atMins % 60)

    return if (withColon){
        String.format("%02d", hours) + ':' + String.format("%02d", minutes)
    } else {
        String.format("%02d", hours) + String.format("%02d", minutes)
    }
}