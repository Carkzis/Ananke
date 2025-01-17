package com.carkzis.ananke.utils

fun String.capitalise(): String {
    return this.replaceFirstChar { it.uppercase() }
}