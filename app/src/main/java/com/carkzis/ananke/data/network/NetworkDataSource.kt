package com.carkzis.ananke.data.network

interface NetworkDataSource {
    suspend fun getUsers(): List<NetworkUser>
}