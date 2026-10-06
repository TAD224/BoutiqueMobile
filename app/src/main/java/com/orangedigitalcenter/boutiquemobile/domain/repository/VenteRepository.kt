package com.orangedigitalcenter.boutiquemobile.domain.repository

import com.orangedigitalcenter.boutiquemobile.data.local.entity.LigneVente
import com.orangedigitalcenter.boutiquemobile.data.local.entity.Vente
import com.orangedigitalcenter.boutiquemobile.domain.model.ProduitPlusVendu
import kotlinx.coroutines.flow.Flow

interface VenteRepository {
    suspend fun enregistrerVente(vente: Vente, lignes: List<LigneVente>): Long
    fun getTotalVentes(debut: Long, fin: Long): Flow<Long>
    fun getBenefice(debut: Long, fin: Long): Flow<Long>
    fun getProduitsPlusVendus(limite: Int): Flow<List<ProduitPlusVendu>>
}