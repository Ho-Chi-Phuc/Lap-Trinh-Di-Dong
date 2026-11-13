package com.example.lab8

import com.example.lab8.data.repository.AuthValidator
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

class AuthValidatorTest {

    @Test
    fun email_blank_isError() = assertNotNull(AuthValidator.validateEmail("  "))

    @Test
    fun email_invalid_isError() {
        assertNotNull(AuthValidator.validateEmail("abc"))
        assertNotNull(AuthValidator.validateEmail("abc@"))
        assertNotNull(AuthValidator.validateEmail("abc@domain"))
    }

    @Test
    fun email_valid_isOk() = assertNull(AuthValidator.validateEmail("user@example.com"))

    @Test
    fun signInPassword_empty_isError() = assertNotNull(AuthValidator.validateSignInPassword(""))

    @Test
    fun newPassword_tooShort_isError() =
        assertNotNull(AuthValidator.validateNewPassword("12345", "12345"))

    @Test
    fun newPassword_mismatch_isError() =
        assertNotNull(AuthValidator.validateNewPassword("123456", "654321"))

    @Test
    fun newPassword_valid_isOk() =
        assertNull(AuthValidator.validateNewPassword("123456", "123456"))
}
