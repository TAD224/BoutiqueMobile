package com.orangedigitalcenter.boutiquemobile.ui.viewmodel
import com.orangedigitalcenter.boutiquemobile.data.local.entity.*
import com.orangedigitalcenter.boutiquemobile.domain.model.*
import com.orangedigitalcenter.boutiquemobile.domain.repository.*
import com.orangedigitalcenter.boutiquemobile.ui.state.*

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orangedigitalcenter.boutiquemobile.domain.usecase.bornesDuJour
import kotlinx.coroutines.flow.*

class DashboardViewModel(venteRepo: VenteRepository, produitRepo: ProduitRepository) : ViewModel() {

    private val bornes = bornesDuJour()

    val uiState: StateFlow<DashboardUiState> = combine(
        venteRepo.getTotalVentes(bornes.first, bornes.second),
        venteRepo.getBenefice(bornes.first, bornes.second),
        produitRepo.getProduitsStockBas(),
        venteRepo.getProduitsPlusVendus(5)
    ) { ventes, benefice, stockBas, top ->
        DashboardUiState(ventes, benefice, stockBas, top)
    }.catch { emit(DashboardUiState()) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardUiState())
}
