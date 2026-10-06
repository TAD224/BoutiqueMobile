package com.orangedigitalcenter.boutiquemobile.data.repository

import com.orangedigitalcenter.boutiquemobile.data.local.dao.ProduitDao
import com.orangedigitalcenter.boutiquemobile.data.local.entity.Produit
import com.orangedigitalcenter.boutiquemobile.domain.repository.ProduitRepository
import kotlinx.coroutines.flow.Flow

class ProduitRepositoryImpl(
    private val produitDao: ProduitDao
) : ProduitRepository {

    override fun getAllProduits(): Flow<List<Produit>> = produitDao.getAll()

    override fun getProduitById(id: Long): Flow<Produit?> = produitDao.getById(id)

    override fun getProduitsStockBas(): Flow<List<Produit>> = produitDao.getStockBas()

    override suspend fun ajouterProduit(produit: Produit): Long = produitDao.insert(produit)

    override suspend fun modifierProduit(produit: Produit) = produitDao.update(produit)

    override suspend fun supprimerProduit(produit: Produit) = produitDao.delete(produit)
}