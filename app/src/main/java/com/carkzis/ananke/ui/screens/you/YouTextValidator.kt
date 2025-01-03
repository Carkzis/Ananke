package com.carkzis.ananke.ui.screens.you

import com.carkzis.ananke.utils.TextValidator
import com.carkzis.ananke.utils.ValidatorResponse

class YouConstants {
    companion object {
        const val MINIMUM_CHARACTER_NAME_LENGTH = 3
        const val MAXIMUM_CHARACTER_NAME_LENGTH = 20
        const val MAXIMUM_BIO_LENGTH = 200
    }
}

class YouTextValidator(private val validations: List<(String) -> ValidatorResponse>): TextValidator {
    override fun validateText(text: String): ValidatorResponse {
        validations.forEach { validation ->
            val validationResponse = validation(text)
            if (validationResponse != ValidatorResponse.Pass) {
                return validationResponse
            }
        }
        return ValidatorResponse.Pass
    }

    companion object {
        fun characterNameValidator(
            minLength: Int = YouConstants.MINIMUM_CHARACTER_NAME_LENGTH,
            maxLength: Int = YouConstants.MAXIMUM_CHARACTER_NAME_LENGTH
        ) = YouTextValidator(
            listOf(
                { text -> if (text.length < minLength) ValidatorResponse.Fail(YouValidatorFailure.NAME_TOO_SHORT.message) else ValidatorResponse.Pass },
                { text -> if (text.length > maxLength) ValidatorResponse.Fail(YouValidatorFailure.NAME_TOO_LONG.message) else ValidatorResponse.Pass }
            )
        )

        fun characterBioValidator(
            maxLength: Int = YouConstants.MAXIMUM_BIO_LENGTH
        ) = YouTextValidator(
            listOf(
                { text -> if (text.length > maxLength) ValidatorResponse.Fail(YouValidatorFailure.BIO_TOO_LONG.message) else ValidatorResponse.Pass }
            )
        )
    }
}