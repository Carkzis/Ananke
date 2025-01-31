package com.carkzis.ananke.testdoubles

import com.carkzis.ananke.utils.CharacterNameGenerator

object DuplicatingCharacterNameGenerator: CharacterNameGenerator {
    override fun generateCharacterName() = "Dave"
}