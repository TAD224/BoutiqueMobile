package com.orangedigitalcenter.boutiquemobile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.orangedigitalcenter.boutiquemobile.data.local.entity.LigneVente
import com.orangedigitalcenter.boutiquemobile.data.local.entity.Vente
import kotlinx.coroutines.flow.Flow

@Dao
interface VenteDao {

    @Query("SELECT * FROM ventes ORDER BY date DESC")
    fun getAll(): Flow<List<Vente>>

    @Query("SELECT * FROM ventes WHERE id = :id")
    fun getById(id: Long): Flow<Vente?>

    @Query("SELECT * FROM ventes WHERE date BETWEEN :debut AND :fin ORDER BY date DESC")
    fun getEntre(debut: Long, fin: Long): Flow<List<Vente>>

    @Query("SELECT * FROM lignes_vente WHERE venteId = :venteId")
    fun getLignes(venteId: Long): Flow<List<LigneVente>>

    @Insert
    suspend fun insertVente(vente: Vente): Long

    @Insert
    suspend fun insertLignes(lignes: List<LigneVente>)

    // Dette d'un client = somme des (total - montantPaye) sur toutes ses ventes
    @Query("SELECT COALESCE(SUM(total - montantPaye), 0) FROM ventes WHERE clientId = :clientId")
    fun getSoldeDuParVentes(clientId: Long): Flow<Long>
}
