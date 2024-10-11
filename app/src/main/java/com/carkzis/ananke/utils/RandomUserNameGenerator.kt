package com.carkzis.ananke.utils

import kotlin.random.Random

object RandomUserNameGenerator {
    fun generateRandomUserName(): String {
        val adjective = randomCharacterAdjectives.random()
        val animal = randomCharacterAnimals.random()
        val number = Random.nextInt(10_000, 100_000)

        return "$adjective-$animal-$number"
    }

    val randomCharacterAdjectives = listOf(
        "pathetic",
        "calculating",
        "vigorous",
        "willing",
        "subdued",
        "erratic",
        "guarded",
        "lazy",
        "uncovered",
        "angry",
        "pleasant",
        "zesty",
        "ragged",
        "heartbreaking",
        "sad",
        "clever",
        "free",
        "used",
        "merciful",
        "brash",
        "mysterious",
        "boring",
        "offbeat",
        "bustling",
        "financial",
        "worried",
        "quarrelsome",
        "imminent",
        "impartial",
        "economic"
    )

    val randomCharacterAnimals = listOf(
        "panther",
        "jerboa",
        "panda",
        "lizard",
        "skunk",
        "dugong",
        "canary",
        "lynx",
        "hog",
        "hedgehog",
        "wombat",
        "crab",
        "springbok",
        "chicken",
        "jaguar",
        "okapi",
        "wolf",
        "quagga",
        "gorilla",
        "porpoise"
    )
}