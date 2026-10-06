package com.orangedigitalcenter.boutiquemobile.data.fake

import com.orangedigitalcenter.boutiquemobile.data.local.entity.*
import com.orangedigitalcenter.boutiquemobile.domain.model.*
import com.orangedigitalcenter.boutiquemobile.domain.repository.*
import com.orangedigitalcenter.boutiquemobile.ui.state.*

import kotlinx.coroutines.flow.*

class FakeProduitRepository : ProduitRepository {
    private val data = MutableStateFlow(listOf(
        Produit(1, "Riz 25kg", 130000, 150000, 40, 5),
        Produit(2, "Huile 5L", 50000, 60000, 2, 5)
    ))
    override fun getAllProduits() = data.asStateFlow()
    override fun getProduitsStockBas() = data.map { l -> l.filter { it.stock <= it.seuilAlerte } }
    override suspend fun ajouterProduit(produit: Produit): Long {
        val id = (data.value.maxOfOrNull { it.id } ?: 0) + 1
        data.value = data.value + produit.copy(id = id); return id
    }
    override suspend fun modifierProduit(produit: Produit) { data.value = data.value.map { if (it.id == produit.id) produit else it } }
    fun decrementer(id: Long, q: Int) { data.value = data.value.map { if (it.id == id) it.copy(stock = it.stock - q) else it } }
    override fun getProduitById(id: Long): Flow<Produit?> =
        data.map { liste -> liste.find { it.id == id } }

    override suspend fun supprimerProduit(produit: Produit) {
        data.value = data.value.filter { it.id != produit.id }
    }
}

class FakeClientRepository(private val ventes: FakeVenteRepository? = null) : ClientRepository {
    private val clients = MutableStateFlow(listOf(Client(1, "Mamadou"), Client(2, "Fatoumata")))
    private val encaissements = MutableStateFlow(emptyList<Encaissement>())
    override fun getAllClients() = clients.asStateFlow()
    override fun getClientsAvecDette(): Flow<List<ClientAvecDette>> {
        val v = ventes?.ventes ?: flowOf(emptyList())
        return combine(clients, v, encaissements) { cl, ve, en ->
            cl.map { c ->
                ClientAvecDette(c, com.orangedigitalcenter.boutiquemobile.domain.usecase.DetteRules.soldeDu(
                    ve.filter { it.clientId == c.id }, en.filter { it.clientId == c.id }))
            }
        }
    }
    override suspend fun ajouterClient(client: Client): Long {
        val id = (clients.value.maxOfOrNull { it.id } ?: 0) + 1
        clients.value = clients.value + client.copy(id = id); return id
    }
    override suspend fun enregistrerEncaissement(encaissement: Encaissement) { encaissements.value = encaissements.value + encaissement }
}

class FakeVenteRepository(private val produits: FakeProduitRepository? = null) : VenteRepository {
    val ventes = MutableStateFlow(emptyList<Vente>())
    val lignes = MutableStateFlow(emptyList<LigneVente>())
    override suspend fun enregistrerVente(vente: Vente, lignes: List<LigneVente>): Long {
        val id = ventes.value.size + 1L
        ventes.value = ventes.value + vente.copy(id = id)
        this.lignes.value = this.lignes.value + lignes.map { it.copy(venteId = id) }
        lignes.forEach { produits?.decrementer(it.produitId, it.quantite) }
        return id
    }
    override fun getTotalVentes(debut: Long, fin: Long) = ventes.map { l -> l.filter { it.date in debut..fin }.sumOf { it.total } }
    override fun getBenefice(debut: Long, fin: Long) = flowOf(0L) // simplifié : le vrai calcul est dans la requête Room
    override fun getProduitsPlusVendus(limite: Int) = flowOf(emptyList<ProduitPlusVendu>())
}