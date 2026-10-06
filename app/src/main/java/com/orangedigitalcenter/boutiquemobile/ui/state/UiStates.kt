package com.orangedigitalcenter.boutiquemobile.ui.state

import com.orangedigitalcenter.boutiquemobile.data.local.entity.Client
import com.orangedigitalcenter.boutiquemobile.data.local.entity.ModePaiement
import com.orangedigitalcenter.boutiquemobile.data.local.entity.Produit
import com.orangedigitalcenter.boutiquemobile.domain.model.ClientAvecDette
import com.orangedigitalcenter.boutiquemobile.domain.model.LigneVentePanier
import com.orangedigitalcenter.boutiquemobile.domain.model.ProduitPlusVendu

// Écran "Produits et stock"
data class ProduitsUiState(
    val produits: List<Produit> = emptyList(),
    val produitsStockBas: List<Produit> = emptyList(),
    val recherche: String = "",
    val chargement: Boolean = false,
    val erreur: String? = null,
    val produitEnregistre: Boolean = false
)

// Écran "Nouvelle vente"
data class NouvelleVenteUiState(
    val produitsDisponibles: List<Produit> = emptyList(),
    val clients: List<Client> = emptyList(),
    val panier: List<LigneVentePanier> = emptyList(),
    val clientSelectionne: Client? = null,
    val modePaiement: ModePaiement = ModePaiement.ESPECES,
    val montantPayeTexte: String = "",
    val total: Long = 0,
    val erreur: String? = null,
    val venteEnregistree: Boolean = false
)

// Écran "Clients et dettes"
data class ClientsDettesUiState(
    val clients: List<ClientAvecDette> = emptyList(),
    val chargement: Boolean = false,
    val erreur: String? = null,
    val encaissementEnregistre: Boolean = false
)

// Écran "Tableau de bord"
data class DashboardUiState(
    val ventesDuJour: Long = 0,
    val beneficeDuJour: Long = 0,
    val produitsStockBas: List<Produit> = emptyList(),
    val produitsPlusVendus: List<ProduitPlusVendu> = emptyList()
)