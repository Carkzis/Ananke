package com.carkzis.ananke.utils

interface TextValidator {
    fun validateText(text: String) : ValidatorResponse
}

sealed interface ValidatorResponse {
    object Pass: ValidatorResponse
    data class Fail(val failureMessage: String): ValidatorResponse
}