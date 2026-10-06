package com.orangedigitalcenter.boutiquemobile.ui.viewmodel
import com.orangedigitalcenter.boutiquemobile.data.local.entity.*
import com.orangedigitalcenter.boutiquemobile.domain.model.*
import com.orangedigitalcenter.boutiquemobile.domain.repository.*
import com.orangedigitalcenter.boutiquemobile.ui.state.*

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orangedigitalcenter.boutiquemobile.domain.usecase.ValidationRules
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProduitViewModel(private val repo: ProduitRepository) : ViewModel() {

    private val recherche = MutableStateFlow("")
    private val etat = MutableStateFlow(ProduitsUiState())

    val uiState: StateFlow<ProduitsUiState> = combine(
        repo.getAllProduits(), repo.getProduitsStockBas(), recherche, etat
    ) { produits, stockBas, q, e ->
        e.copy(
            produits = produits.filter { it.nom.contains(q.trim(), ignoreCase = true) },
            produitsStockBas = stockBas,
            recherche = q,
            chargement = false
        )
    }.catch { emit(ProduitsUiState(chargement = false, erreur = "Impossible de charger les produits.")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProduitsUiState())

    fun onRechercheChange(texte: String) { recherche.value = texte }

    /** id = 0 pour un ajout, sinon modification. Les champs numériques arrivent en texte (formulaire). */
    fun sauvegarderProduit(
        id: Long, nom: String, prixAchatTxt: String, prixVenteTxt: String, stockTxt: String, seuilTxt: String
    ) {
        val pa = prixAchatTxt.trim().toLongOrNull(); val pv = prixVenteTxt.trim().toLongOrNull()
        val st = stockTxt.trim().toIntOrNull(); val se = seuilTxt.trim().toIntOrNull()
        val erreur = ValidationRules.validerProduit(nom, pa, pv, st, se)
        if (erreur != null) { etat.update { it.copy(erreur = erreur, produitEnregistre = false) }; return }

        viewModelScope.launch {
            try {
                val p = Produit(id, nom.trim(), pa!!, pv!!, st!!, se!!)
                if (id == 0L) repo.ajouterProduit(p) else repo.modifierProduit(p)
                etat.update { it.copy(erreur = null, produitEnregistre = true) }
            } catch (e: Exception) {
                etat.update { it.copy(erreur = "Erreur lors de l'enregistrement du produit.") }
            }
        }
    }

    fun effacerMessages() { etat.update { it.copy(erreur = null, produitEnregistre = false) } }
}
