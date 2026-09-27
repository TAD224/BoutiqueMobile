package com.orangedigitalcenter.boutiquemobile.domain.repository

import com.orangedigitalcenter.boutiquemobile.data.local.entity.Client
import com.orangedigitalcenter.boutiquemobile.data.local.entity.Encaissement
import com.orangedigitalcenter.boutiquemobile.domain.model.ClientAvecDette
import kotlinx.coroutines.flow.Flow

/**
 * Implémentée par le Responsable données (ClientRepositoryImpl, en s'appuyant sur
 * ClientDao + EncaissementDao + VenteDao pour le calcul de la dette).
 * Consommée par le Responsable logique dans ClientViewModel.
 */
interface ClientRepository {
    fun getAllClients(): Flow<List<Client>>
    fun getClientById(id: Long): Flow<Client?>
    fun getClientsAvecDette(): Flow<List<ClientAvecDette>>
    suspend fun ajouterClient(client: Client): Long

    /** Enregistre un paiement partiel ou total de la dette d'un client. */
    suspend fun enregistrerEncaissement(encaissement: Encaissement)
}
