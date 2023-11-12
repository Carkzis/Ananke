package com.carkzis.ananke.ui.screens

sealed interface ValidatorResponse {
    object Pass: ValidatorResponse
    data class Fail(val failureMessage: String): ValidatorResponse
}

interface TextValidator {
    fun validateText(text: String) : ValidatorResponse
}
class NewGameTextValidator(private val validations: List<(String) -> NewGameValidatorResponse>) : TextValidator {
    override fun validateText(text: String) : ValidatorResponse {
        validations.forEach { validation ->
            val validationResponse = validation(text)
            if (validationResponse != NewGameValidatorResponse.PASS) {
                return ValidatorResponse.Fail(validationResponse.message)
            }
        }
        return ValidatorResponse.Pass
    }
}