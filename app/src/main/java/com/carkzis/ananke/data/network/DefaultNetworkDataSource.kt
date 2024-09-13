package com.carkzis.ananke.data.network

import javax.inject.Inject

class DefaultNetworkDataSource @Inject constructor() : NetworkDataSource {
    override suspend fun getUsers(): List<NetworkUser> = listOf(
        NetworkUser(id = 1, name = "Zidun"),
        NetworkUser(id = 2, name = "Vivu"),
        NetworkUser(id = 3, name = "Steinur"),
        NetworkUser(id = 4, name = "Garnut"),
        NetworkUser(id = 5, name = "Amarunt"),
        NetworkUser(id = 6, name = "Freyu"),
        NetworkUser(id = 7, name = "Quinu"),
        NetworkUser(id = 8, name = "Eiku"),
    )

}