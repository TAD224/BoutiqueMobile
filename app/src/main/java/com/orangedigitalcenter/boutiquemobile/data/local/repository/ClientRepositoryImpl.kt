package com.orangedigitalcenter.boutiquemobile.data.repository

import com.orangedigitalcenter.boutiquemobile.data.local.dao.ClientDao
import com.orangedigitalcenter.boutiquemobile.data.local.dao.EncaissementDao
import com.orangedigitalcenter.boutiquemobile.data.local.dao.VenteDao
import com.orangedigitalcenter.boutiquemobile.data.local.entity.Client
import com.orangedigitalcenter.boutiquemobile.data.local.entity.Encaissement
import com.orangedigitalcenter.boutiquemobile.domain.model.ClientAvecDette
import com.orangedigitalcenter.boutiquemobile.domain.repository.ClientRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf

class ClientRepositoryImpl(
    private val clientDao: ClientDao,
    private val venteDao: VenteDao,
    private val encaissementDao: EncaissementDao
) : ClientRepository {

    override fun getAllClients(): Flow<List<Client>> = clientDao.getAll()

    override fun getClientById(id: Long): Flow<Client?> = clientDao.getById(id)

    /**
     * Pour chaque client : soldeDu = (somme des total - montantPaye de ses ventes)
     *                                - (somme de ses encaissements).
     * Le Flow se met à jour automatiquement quand une vente, un encaissement
     * ou un client change.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getClientsAvecDette(): Flow<List<ClientAvecDette>> =
        clientDao.getAll().flatMapLatest { clients ->
            if (clients.isEmpty()) {
                flowOf(emptyList())
            } else {
                val flowsParClient = clients.map { client ->
                    combine(
                        venteDao.getSoldeDuParVentes(client.id),
                        encaissementDao.getTotalEncaisseParClient(client.id)
                    ) { dueParVentes, totalEncaisse ->
                        ClientAvecDette(
                            clientId = client.id,
                            nom = client.nom,
                            telephone = client.telephone,
                            soldeDu = dueParVentes - totalEncaisse
                        )
                    }
                }
                combine(flowsParClient) { it.toList() }
            }
        }

    override suspend fun ajouterClient(client: Client): Long = clientDao.insert(client)

    override suspend fun enregistrerEncaissement(encaissement: Encaissement) {
        encaissementDao.insert(encaissement)
    }
}