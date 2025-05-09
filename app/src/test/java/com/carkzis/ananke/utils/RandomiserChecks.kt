package com.carkzis.ananke.utils

import junit.framework.TestCase.assertTrue

internal fun assertNameHasExpectedFormat(actualCharacterName: String) {
    val (characterNameAdjective, characterNameAnimal, characterNameNumber) = actualCharacterName
        .split("-", limit = 3)

    assertTrue(
        RandomCharacterNameGenerator.randomCharacterAdjectives.contains(
            characterNameAdjective
        )
    )
    assertTrue(
        RandomCharacterNameGenerator.randomCharacterAnimals.contains(
            characterNameAnimal
        )
    )
    assertTrue(characterNameNumber.toInt() in 10_000 until 100_000)
}

internal fun assertUserHasExpectedFormat(actualUserName: String) {
    val (userPrefix, numberSuffix) = actualUserName
        .split("-", limit = 2)

    assertTrue(
        userPrefix == "User"
    )

    assertTrue(
        numberSuffix.toInt() in 10_000 until 100_000
    )
}