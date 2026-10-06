package com.orangedigitalcenter.boutiquemobile.ui.viewmodel
import com.orangedigitalcenter.boutiquemobile.data.local.entity.*
import com.orangedigitalcenter.boutiquemobile.domain.model.*
import com.orangedigitalcenter.boutiquemobile.domain.repository.*
import com.orangedigitalcenter.boutiquemobile.ui.state.*

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orangedigitalcenter.boutiquemobile.domain.usecase.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class VenteViewModel(
    private val venteRepo: VenteRepository,
    produitRepo: ProduitRepository,
    clientRepo: ClientRepository,
    private val horloge: () -> Long = { System.currentTimeMillis() }
) : ViewModel() {

    private val _uiState = MutableStateFlow(NouvelleVenteUiState())
    val uiState: StateFlow<NouvelleVenteUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { produitRepo.getAllProduits().collect { l -> _uiState.update { it.copy(produitsDisponibles = l) } } }
        viewModelScope.launch { clientRepo.getAllClients().collect { l -> _uiState.update { it.copy(clients = l) } } }
    }

    fun ajouterProduit(produit: Produit) {
        val quantiteActuelle = _uiState.value.panier.find { it.produit.id == produit.id }?.quantite ?: 0
        if (quantiteActuelle + 1 > produit.stock) {
            _uiState.update { it.copy(erreur = "Stock insuffisant pour « ${produit.nom} » (${produit.stock} disponible).") }
            return
        }
        majPanier(PanierRules.ajouter(_uiState.value.panier, produit))
    }

    fun changerQuantite(produitId: Long, quantite: Int) {
        val ligne = _uiState.value.panier.find { it.produit.id == produitId } ?: return
        if (quantite > ligne.produit.stock) {
            _uiState.update { it.copy(erreur = "Stock insuffisant : ${ligne.produit.stock} disponible.") }
            return
        }
        majPanier(PanierRules.changerQuantite(_uiState.value.panier, produitId, quantite))
    }

    fun retirerProduit(produitId: Long) = majPanier(PanierRules.retirer(_uiState.value.panier, produitId))

    fun selectionnerClient(client: Client?) = _uiState.update { it.copy(clientSelectionne = client, erreur = null) }
    fun choisirModePaiement(mode: ModePaiement) = _uiState.update { it.copy(modePaiement = mode) }
    fun onMontantPayeChange(texte: String) = _uiState.update { it.copy(montantPayeTexte = texte, erreur = null) }

    /** Recalcule toujours le total à partir des lignes : jamais figé. */
    private fun majPanier(panier: List<LigneVentePanier>) =
        _uiState.update { it.copy(panier = panier, total = PanierRules.total(panier), erreur = null) }

    fun validerVente() {
        val s = _uiState.value
        // Champ vide = paiement comptant du total
        val montant = if (s.montantPayeTexte.isBlank()) s.total else s.montantPayeTexte.trim().toLongOrNull()
        val erreur = ValidationRules.validerVente(s.panier, montant, s.clientSelectionne?.id)
        if (erreur != null) { _uiState.update { it.copy(erreur = erreur) }; return }

        viewModelScope.launch {
            try {
                val vente = Vente(0, s.clientSelectionne?.id, horloge(), s.total, montant!!, s.modePaiement)
                val lignes = s.panier.map { LigneVente(0, 0, it.produit.id, it.quantite, it.produit.prixVente) }
                venteRepo.enregistrerVente(vente, lignes)
                _uiState.update { NouvelleVenteUiState(produitsDisponibles = it.produitsDisponibles, clients = it.clients, venteEnregistree = true) }
            } catch (e: Exception) {
                _uiState.update { it.copy(erreur = "Impossible d'enregistrer la vente. Réessayez.") }
            }
        }
    }

    fun venteAcquittee() = _uiState.update { it.copy(venteEnregistree = false) }
}
