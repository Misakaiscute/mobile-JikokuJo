package com.jikokujo.profile

import com.jikokujo.profile.utils.Validator
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test

class ValidatorTest {
    @Test
    fun `validatePassword - exactly 8 characters - returns true`() {
        assertTrue(Validator.validatePassword("abcd1234"))
    }

    @Test
    fun `validatePassword - more than 8 characters - returns true`() {
        assertTrue(Validator.validatePassword("abcd12345678"))
    }

    @Test
    fun `validatePassword - 7 characters - returns false`() {
        assertFalse(Validator.validatePassword("abcd123"))
    }

    @Test
    fun `validatePassword - empty string - returns false`() {
        assertFalse(Validator.validatePassword(""))
    }

    @Test
    fun `validatePassword - whitespace counts toward length`() {
        assertTrue(Validator.validatePassword("abc def "))  // 8 chars with spaces
    }


    @Test
    fun `validateEmail - standard email - returns true`() {
        assertTrue(Validator.validateEmail("user@example.com"))
    }

    @Test
    fun `validateEmail - subdomain - returns true`() {
        assertTrue(Validator.validateEmail("user@mail.example.com"))
    }

    @Test
    fun `validateEmail - plus addressing - returns true`() {
        assertTrue(Validator.validateEmail("user+filter@example.com"))
    }

    @Test
    fun `validateEmail - fot addressing - returns true`() {
        assertTrue(Validator.validateEmail("user.name@example.com"))
    }

    @Test
    fun `validateEmail - numeric local part - returns true`() {
        assertTrue(Validator.validateEmail("12345@example.com"))
    }

    @Test
    fun `validateEmail - two letter TLD - returns true`() {
        assertTrue(Validator.validateEmail("user@example.hu"))
    }

    @Test
    fun `validateEmail - missing at sign - returns false`() {
        assertFalse(Validator.validateEmail("userexample.com"))
    }

    @Test
    fun `validateEmail - missing domain - returns false`() {
        assertFalse(Validator.validateEmail("user@"))
    }

    @Test
    fun `validateEmail - missing local part - returns false`() {
        assertFalse(Validator.validateEmail("@example.com"))
    }

    @Test
    fun `validateEmail - missing TLD - returns false`() {
        assertFalse(Validator.validateEmail("user@example"))
    }

    @Test
    fun `validateEmail - single character TLD - returns false`() {
        assertFalse(Validator.validateEmail("user@example.c"))
    }

    @Test
    fun `validateEmail - empty string - returns false`() {
        assertFalse(Validator.validateEmail(""))
    }

    @Test
    fun `validateEmail - spaces in address - returns false`() {
        assertFalse(Validator.validateEmail("user name@example.com"))
    }

    @Test
    fun `validateEmail - double at sign - returns false`() {
        assertFalse(Validator.validateEmail("user@@example.com"))
    }
}