package com.carkzis.ananke.ui.screens.nugame

import com.carkzis.ananke.ui.screens.TextValidator
import com.carkzis.ananke.ui.screens.ValidatorResponse
import com.carkzis.ananke.ui.screens.nugame.NewGameConstants.Companion.MAXIMUM_GAME_DESCRIPTION_LENGTH
import com.carkzis.ananke.ui.screens.nugame.NewGameConstants.Companion.MAXIMUM_GAME_TITLE_LENGTH
import com.carkzis.ananke.ui.screens.nugame.NewGameConstants.Companion.MINIMUM_GAME_TITLE_LENGTH

class NewGameConstants {
    companion object {
        const val MINIMUM_GAME_TITLE_LENGTH = 5
        const val MAXIMUM_GAME_TITLE_LENGTH = 30
        const val MAXIMUM_GAME_DESCRIPTION_LENGTH = 200
    }
}

class NewGameTextValidator(private val validations: List<(String) -> ValidatorResponse>) :
    TextValidator {
    override fun validateText(text: String) : ValidatorResponse {
        validations.forEach { validation ->
            val validationResponse = validation(text)
            if (validationResponse != ValidatorResponse.Pass) {
                return validationResponse
            }
        }
        return ValidatorResponse.Pass
    }

    companion object {
        fun titleValidator(
            minLength: Int = MINIMUM_GAME_TITLE_LENGTH,
            maxLength: Int = MAXIMUM_GAME_TITLE_LENGTH
        ) = NewGameTextValidator(listOf(
            { text -> if (text.isEmpty() && minLength != 0) ValidatorResponse.Fail(
                NewGameValidatorFailure.TITLE_EMPTY.message) else ValidatorResponse.Pass },
            { text -> if (text.length < minLength) ValidatorResponse.Fail(NewGameValidatorFailure.TITLE_TOO_SHORT.message) else ValidatorResponse.Pass },
            { text -> if (text.length > maxLength) ValidatorResponse.Fail(NewGameValidatorFailure.TITLE_TOO_LONG.message) else ValidatorResponse.Pass }
        ))

        fun descriptionValidator(
            maxLength: Int = MAXIMUM_GAME_DESCRIPTION_LENGTH
        ) = NewGameTextValidator(listOf { text ->
                if (text.length > maxLength) {
                    ValidatorResponse.Fail(NewGameValidatorFailure.DESCRIPTION_TOO_LONG.message)
                } else {
                    ValidatorResponse.Pass
                }
        })
    }
}