package com.carkzis.ananke.ui.screens

interface TextValidator {
    fun validateText(text: String) : ValidatorResponse
}

sealed interface ValidatorResponse {
    object Pass: ValidatorResponse
    data class Fail(val failureMessage: String): ValidatorResponse
}