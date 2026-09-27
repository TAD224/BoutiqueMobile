package com.orangedigitalcenter.boutiquemobile.domain.repository

import com.orangedigitalcenter.boutiquemobile.data.local.entity.ModePaiement
import com.orangedigitalcenter.boutiquemobile.data.local.entity.Vente
import com.orangedigitalcenter.boutiquemobile.domain.model.LigneVentePanier
import com.orangedigitalcenter.boutiquemobile.domain.model.VenteDetail
import kotlinx.coroutines.flow.Flow

/**
 * Implémentée par le Responsable données (VenteRepositoryImpl, en s'appuyant sur
 * VenteDao + ProduitDao pour la décrémentation du stock).
 * Consommée par le Responsable logique dans VenteViewModel et DashboardViewModel.
 */
interface VenteRepository {
    fun getAllVentes(): Flow<List<Vente>>
    fun getVenteDetail(venteId: Long): Flow<VenteDetail?>
    fun getVentesDuJour(): Flow<List<Vente>>

    /**
     * Enregistre une vente complète (panier) en une seule transaction :
     * insère la Vente, ses LigneVente, et décrémente le stock de chaque produit.
     * Retourne l'id de la vente créée.
     */
    suspend fun enregistrerVente(
        clientId: Long?,
        lignesPanier: List<LigneVentePanier>,
        montantPaye: Long,
        modePaiement: ModePaiement
    ): Long

    fun getBeneficeDuJour(): Flow<Long>

    /** Retourne (nomProduit, quantiteVendue) triés du plus vendu au moins vendu. */
    fun getProduitsLesPlusVendus(limite: Int): Flow<List<Pair<String, Int>>>
}
