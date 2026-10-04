 package com.orangedigitalcenter.boutiquemobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orangedigitalcenter.boutiquemobile.domain.usecase.ValidationRules
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ClientViewModel(
    private val repo: ClientRepository,
    private val horloge: () -> Long = { System.currentTimeMillis() }
) : ViewModel() {

    private val etat = MutableStateFlow(ClientsDettesUiState())

    val uiState: StateFlow<ClientsDettesUiState> = combine(repo.getClientsAvecDette(), etat) { clients, e ->
        // Les clients les plus endettés en premier
        e.copy(clients = clients.sortedByDescending { it.soldeDu }, chargement = false)
    }.catch { emit(ClientsDettesUiState(chargement = false, erreur = "Impossible de charger les clients.")) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ClientsDettesUiState())

    fun encaisser(client: ClientAvecDette, montantTexte: String) {
        val montant = montantTexte.trim().toLongOrNull()
        val erreur = ValidationRules.validerEncaissement(montant, client.soldeDu)
        if (erreur != null) { etat.update { it.copy(erreur = erreur, encaissementEnregistre = false) }; return }

        viewModelScope.launch {
            try {
                repo.enregistrerEncaissement(Encaissement(0, client.client.id, montant!!, horloge()))
                etat.update { it.copy(erreur = null, encaissementEnregistre = true) }
            } catch (e: Exception) {
                etat.update { it.copy(erreur = "Erreur lors de l'encaissement.") }
            }
        }
    }

    fun ajouterClient(nom: String, telephone: String) {
        if (nom.isBlank()) { etat.update { it.copy(erreur = "Le nom du client est obligatoire.") }; return }
        viewModelScope.launch {
            try { repo.ajouterClient(Client(0, nom.trim(), telephone.trim().ifBlank { null })); etat.update { it.copy(erreur = null) } }
            catch (e: Exception) { etat.update { it.copy(erreur = "Erreur lors de l'ajout du client.") } }
        }
    }

    fun effacerMessages() = etat.update { it.copy(erreur = null, encaissementEnregistre = false) }
}
