package com.orangedigitalcenter.boutiquemobile.data.repository

import androidx.room.withTransaction
import com.orangedigitalcenter.boutiquemobile.data.local.AppDatabase
import com.orangedigitalcenter.boutiquemobile.data.local.entity.LigneVente
import com.orangedigitalcenter.boutiquemobile.data.local.entity.ModePaiement
import com.orangedigitalcenter.boutiquemobile.data.local.entity.Vente
import com.orangedigitalcenter.boutiquemobile.domain.model.LigneVentePanier
import com.orangedigitalcenter.boutiquemobile.domain.model.VenteDetail
import com.orangedigitalcenter.boutiquemobile.domain.repository.VenteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.Calendar

class VenteRepositoryImpl(
    private val database: AppDatabase
) : VenteRepository {

    private val venteDao = database.venteDao()
    private val produitDao = database.produitDao()
    private val clientDao = database.clientDao()

    override fun getAllVentes(): Flow<List<Vente>> = venteDao.getAll()

    override fun getVenteDetail(venteId: Long): Flow<VenteDetail?> =
        combine(
            venteDao.getById(venteId),
            venteDao.getLignes(venteId),
            produitDao.getAll(),
            clientDao.getAll()
        ) { vente, lignes, produits, clients ->
            if (vente == null) {
                null
            } else {
                val nomsProduits = produits.associate { it.id to it.nom }
                VenteDetail(
                    venteId = vente.id,
                    date = vente.date,
                    nomClient = clients.firstOrNull { it.id == vente.clientId }?.nom,
                    lignes = lignes.map {
                        LigneVentePanier(
                            produitId = it.produitId,
                            nomProduit = nomsProduits[it.produitId] ?: "Produit inconnu",
                            quantite = it.quantite,
                            prixUnitaire = it.prixUnitaire
                        )
                    },
                    total = vente.total,
                    montantPaye = vente.montantPaye,
                    modePaiement = vente.modePaiement
                )
            }
        }

    override fun getVentesDuJour(): Flow<List<Vente>> {
        val (debut, fin) = bornesDuJour()
        return venteDao.getEntre(debut, fin)
    }

    /**
     * Une seule transaction : si une étape échoue, rien n'est enregistré
     * (ni vente, ni lignes, ni baisse de stock).
     * withTransaction est l'équivalent de @Transaction pour du code hors DAO.
     */
    override suspend fun enregistrerVente(
        clientId: Long?,
        lignesPanier: List<LigneVentePanier>,
        montantPaye: Long,
        modePaiement: ModePaiement
    ): Long {
        require(lignesPanier.isNotEmpty()) { "Le panier est vide." }
        require(montantPaye >= 0) { "Le montant payé ne peut pas être négatif." }

        val total = lignesPanier.sumOf { it.sousTotal }

        return database.withTransaction {
            val venteId = venteDao.insertVente(
                Vente(
                    clientId = clientId,
                    date = System.currentTimeMillis(),
                    total = total,
                    montantPaye = montantPaye,
                    modePaiement = modePaiement
                )
            )
            venteDao.insertLignes(
                lignesPanier.map {
                    LigneVente(
                        venteId = venteId,
                        produitId = it.produitId,
                        quantite = it.quantite,
                        prixUnitaire = it.prixUnitaire
                    )
                }
            )
            lignesPanier.forEach { produitDao.decrementerStock(it.produitId, it.quantite) }
            venteId
        }
    }

    override fun getBeneficeDuJour(): Flow<Long> {
        val (debut, fin) = bornesDuJour()
        return venteDao.getBeneficeEntre(debut, fin)
    }

    override fun getProduitsLesPlusVendus(limite: Int): Flow<List<Pair<String, Int>>> =
        venteDao.getProduitsLesPlusVendus(limite).map { liste ->
            liste.map { it.nom to it.quantiteVendue }
        }

    /** Début (00:00:00.000) et fin (23:59:59.999) de la journée en cours, en millisecondes. */
    private fun bornesDuJour(): Pair<Long, Long> {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val debut = cal.timeInMillis
        cal.add(Calendar.DAY_OF_YEAR, 1)
        return debut to (cal.timeInMillis - 1)
    }
}