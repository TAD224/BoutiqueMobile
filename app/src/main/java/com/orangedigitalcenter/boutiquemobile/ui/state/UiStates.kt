package com.orangedigitalcenter.boutiquemobile.ui.state

import com.orangedigitalcenter.boutiquemobile.data.local.entity.ModePaiement
import com.orangedigitalcenter.boutiquemobile.data.local.entity.Produit
import com.orangedigitalcenter.boutiquemobile.domain.model.ClientAvecDette
import com.orangedigitalcenter.boutiquemobile.domain.model.LigneVentePanier

/**
 * Chaque écran observe un StateFlow<XxxUiState> exposé par son ViewModel.
 * Le Responsable interface peut construire les écrans avec des valeurs factices
 * de ces classes, sans attendre que le ViewModel soit branché sur les vraies données.
 */

// Écran "Produits et stock"
data class ProduitsUiState(
    val produits: List<Produit> = emptyList(),
    val produitsStockBas: List<Produit> = emptyList(),
    val chargement: Boolean = false,
    val erreur: String? = null
)

// Écran "Nouvelle vente"
data class NouvelleVenteUiState(
    val produitsDisponibles: List<Produit> = emptyList(),
    val panier: List<LigneVentePanier> = emptyList(),
    val clientId: Long? = null,
    val modePaiement: ModePaiement = ModePaiement.ESPECES,
    val montantPaye: String = "",
    val total: Long = 0,
    val venteEnregistree: Boolean = false,
    val erreur: String? = null
)

// Écran "Clients et dettes"
data class ClientsDettesUiState(
    val clients: List<ClientAvecDette> = emptyList(),
    val chargement: Boolean = false
)

// Écran "Tableau de bord"
data class DashboardUiState(
    val nombreVentesDuJour: Int = 0,
    val beneficeDuJour: Long = 0,
    val produitsStockBas: List<Produit> = emptyList(),
    val produitsLesPlusVendus: List<Pair<String, Int>> = emptyList()
)
