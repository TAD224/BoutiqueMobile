package com.orangedigitalcenter.boutiquemobile.domain.repository

import com.orangedigitalcenter.boutiquemobile.data.local.entity.Client
import com.orangedigitalcenter.boutiquemobile.data.local.entity.Encaissement
import com.orangedigitalcenter.boutiquemobile.domain.model.ClientAvecDette
import kotlinx.coroutines.flow.Flow

interface ClientRepository {
    fun getAllClients(): Flow<List<Client>>
    fun getClientsAvecDette(): Flow<List<ClientAvecDette>>
    suspend fun ajouterClient(client: Client): Long
    suspend fun enregistrerEncaissement(encaissement: Encaissement)
}