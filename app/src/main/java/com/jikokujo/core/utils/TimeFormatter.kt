package com.jikokujo.core.utils

import android.annotation.SuppressLint

@SuppressLint("DefaultLocale")
fun timeFormatter(atMins: Int, withColon: Boolean = true): String{
    val actualMins = atMins % (24 * 60)
    val hours = (actualMins / 60)
    val minutes = (actualMins % 60)

    return if (withColon){
        String.format("%02d", hours) + ':' + String.format("%02d", minutes)
    } else {
        String.format("%02d", hours) + String.format("%02d", minutes)
    }
}