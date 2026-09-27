package com.orangedigitalcenter.boutiquemobile.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.orangedigitalcenter.boutiquemobile.data.local.entity.Produit
import kotlinx.coroutines.flow.Flow

@Dao
interface ProduitDao {

    @Query("SELECT * FROM produits ORDER BY nom ASC")
    fun getAll(): Flow<List<Produit>>

    @Query("SELECT * FROM produits WHERE id = :id")
    fun getById(id: Long): Flow<Produit?>

    @Query("SELECT * FROM produits WHERE stock <= seuilAlerte ORDER BY stock ASC")
    fun getStockBas(): Flow<List<Produit>>

    @Insert
    suspend fun insert(produit: Produit): Long

    @Update
    suspend fun update(produit: Produit)

    @Delete
    suspend fun delete(produit: Produit)

    @Query("UPDATE produits SET stock = stock - :quantite WHERE id = :produitId")
    suspend fun decrementerStock(produitId: Long, quantite: Int)
}
