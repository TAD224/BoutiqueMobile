package com.orangedigitalcenter.boutiquemobile.domain.model

import com.orangedigitalcenter.boutiquemobile.data.local.entity.Client
import com.orangedigitalcenter.boutiquemobile.data.local.entity.ModePaiement
import com.orangedigitalcenter.boutiquemobile.data.local.entity.Produit

data class LigneVentePanier(
    val produit: Produit,
    val quantite: Int,
    val sousTotal: Long
)

data class ClientAvecDette(
    val client: Client,
    val soldeDu: Long
)

data class ProduitPlusVendu(
    val nom: String,
    val quantiteVendue: Int
)

/** Conservé pour un futur écran de détail de vente ; plus utilisé par VenteRepository pour l'instant. */
data class VenteDetail(
    val venteId: Long,
    val date: Long,
    val nomClient: String?,
    val lignes: List<LigneVentePanier>,
    val total: Long,
    val montantPaye: Long,
    val modePaiement: ModePaiement
) {
    val dette: Long get() = total - montantPaye
}