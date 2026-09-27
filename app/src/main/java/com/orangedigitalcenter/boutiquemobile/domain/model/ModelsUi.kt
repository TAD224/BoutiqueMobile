package com.orangedigitalcenter.boutiquemobile.domain.model

import com.orangedigitalcenter.boutiquemobile.data.local.entity.ModePaiement

/**
 * Ligne d'un panier en cours de saisie, avant d'être transformée en LigneVente
 * lors de l'enregistrement. C'est ce type que manipule l'écran "Nouvelle vente".
 */
data class LigneVentePanier(
    val produitId: Long,
    val nomProduit: String,
    val quantite: Int,
    val prixUnitaire: Long
) {
    val sousTotal: Long get() = quantite * prixUnitaire
}

/**
 * Détail complet d'une vente (vente + lignes + nom du client) prêt à afficher.
 * Évite à l'UI d'avoir à recombiner plusieurs entités elle-même.
 */
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

/**
 * Client avec sa dette déjà calculée, pour l'écran "Clients et dettes".
 */
data class ClientAvecDette(
    val clientId: Long,
    val nom: String,
    val telephone: String?,
    val soldeDu: Long
)
