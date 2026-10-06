package com.orangedigitalcenter.boutiquemobile.data.repository

import androidx.room.withTransaction
import com.orangedigitalcenter.boutiquemobile.data.local.AppDatabase
import com.orangedigitalcenter.boutiquemobile.data.local.entity.LigneVente
import com.orangedigitalcenter.boutiquemobile.data.local.entity.Vente
import com.orangedigitalcenter.boutiquemobile.domain.model.ProduitPlusVendu
import com.orangedigitalcenter.boutiquemobile.domain.repository.VenteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class VenteRepositoryImpl(
    private val database: AppDatabase
) : VenteRepository {

    private val venteDao = database.venteDao()
    private val produitDao = database.produitDao()

    /** Une seule transaction : si une étape échoue, rien n'est enregistré. */
    override suspend fun enregistrerVente(vente: Vente, lignes: List<LigneVente>): Long {
        require(lignes.isNotEmpty()) { "Le panier est vide." }
        require(vente.montantPaye >= 0) { "Le montant payé ne peut pas être négatif." }
        return database.withTransaction {
            val venteId = venteDao.insertVente(vente)
            venteDao.insertLignes(lignes.map { it.copy(venteId = venteId) })
            lignes.forEach { produitDao.decrementerStock(it.produitId, it.quantite) }
            venteId
        }
    }

    override fun getTotalVentes(debut: Long, fin: Long): Flow<Long> =
        venteDao.getEntre(debut, fin).map { liste -> liste.sumOf { it.total } }

    override fun getBenefice(debut: Long, fin: Long): Flow<Long> =
        venteDao.getBeneficeEntre(debut, fin)

    override fun getProduitsPlusVendus(limite: Int): Flow<List<ProduitPlusVendu>> =
        venteDao.getProduitsLesPlusVendus(limite)
}