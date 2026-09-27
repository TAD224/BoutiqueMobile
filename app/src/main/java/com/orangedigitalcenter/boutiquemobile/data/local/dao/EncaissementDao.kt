package com.orangedigitalcenter.boutiquemobile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.orangedigitalcenter.boutiquemobile.data.local.entity.Encaissement
import kotlinx.coroutines.flow.Flow

@Dao
interface EncaissementDao {

    @Query("SELECT * FROM encaissements WHERE clientId = :clientId ORDER BY date DESC")
    fun getParClient(clientId: Long): Flow<List<Encaissement>>

    @Insert
    suspend fun insert(encaissement: Encaissement): Long

    @Query("SELECT COALESCE(SUM(montant), 0) FROM encaissements WHERE clientId = :clientId")
    fun getTotalEncaisseParClient(clientId: Long): Flow<Long>
}
