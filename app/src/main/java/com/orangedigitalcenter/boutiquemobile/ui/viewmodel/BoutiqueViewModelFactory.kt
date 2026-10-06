package com.orangedigitalcenter.boutiquemobile.ui.viewmodel
import com.orangedigitalcenter.boutiquemobile.data.local.entity.*
import com.orangedigitalcenter.boutiquemobile.domain.model.*
import com.orangedigitalcenter.boutiquemobile.domain.repository.*
import com.orangedigitalcenter.boutiquemobile.ui.state.*

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/** Branche les dépôts (Fake ou Room) sans toucher aux ViewModels. */
class BoutiqueViewModelFactory(
    private val produitRepo: ProduitRepository,
    private val clientRepo: ClientRepository,
    private val venteRepo: VenteRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T = when (modelClass) {
        ProduitViewModel::class.java -> ProduitViewModel(produitRepo)
        VenteViewModel::class.java -> VenteViewModel(venteRepo, produitRepo, clientRepo)
        ClientViewModel::class.java -> ClientViewModel(clientRepo)
        DashboardViewModel::class.java -> DashboardViewModel(venteRepo, produitRepo)
        else -> throw IllegalArgumentException("ViewModel inconnu : ${modelClass.name}")
    } as T
}
