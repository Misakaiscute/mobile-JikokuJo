package com.jikokujo.profile.utils

object Validator{
    fun validatePassword(password: String): Boolean{
        return password.length >= 8
    }
    fun validateEmail(email: String): Boolean {
        Regex("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$")
            .matchEntire(email)?.let {
                return true
            }
        return false
    }
}