package com.jikokujo.profile.presentation.auth

sealed class InputException: Exception(){
    class InvalidEmailException(
        override val message: String,
        override val cause: Throwable? = null
    ): InputException()
    class InvalidPasswordException(
        override val message: String,
        override val cause: Throwable? = null
    ): InputException()
    class PasswordsNotMatchingException(
        override val message: String,
        override val cause: Throwable? = null
    ): InputException()
    class MissingFieldException(
        override val message: String,
        override val cause: Throwable? = null
    ): InputException()
}