package com.orangedigitalcenter.boutiquemobile.domain.repository

import com.orangedigitalcenter.boutiquemobile.data.local.entity.Produit
import kotlinx.coroutines.flow.Flow

/**
 * Implémentée par le Responsable données (ProduitRepositoryImpl, en s'appuyant sur ProduitDao).
 * Consommée par le Responsable logique dans ProduitViewModel.
 */
interface ProduitRepository {
    fun getAllProduits(): Flow<List<Produit>>
    fun getProduitById(id: Long): Flow<Produit?>
    fun getProduitsStockBas(): Flow<List<Produit>>
    suspend fun ajouterProduit(produit: Produit): Long
    suspend fun modifierProduit(produit: Produit)
    suspend fun supprimerProduit(produit: Produit)
}
