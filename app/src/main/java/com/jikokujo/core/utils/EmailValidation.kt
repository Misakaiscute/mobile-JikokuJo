package com.jikokujo.core.utils

fun validateEmail(email: String): Boolean {
    Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+'\'.[a-zA-Z]{2,}$")
        .matchEntire(email)
        ?.let {
            return true
        }
    return false
}