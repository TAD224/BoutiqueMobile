package com.orangedigitalcenter.boutiquemobile.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.orangedigitalcenter.boutiquemobile.data.local.entity.LigneVente
import com.orangedigitalcenter.boutiquemobile.data.local.entity.Vente
import com.orangedigitalcenter.boutiquemobile.domain.model.ProduitPlusVendu
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

    @Query("SELECT COALESCE(SUM(total - montantPaye), 0) FROM ventes WHERE clientId = :clientId")
    fun getSoldeDuParVentes(clientId: Long): Flow<Long>

    @Query(
        """
        SELECT COALESCE(SUM((l.prixUnitaire - p.prixAchat) * l.quantite), 0)
        FROM lignes_vente l
        INNER JOIN ventes v ON v.id = l.venteId
        INNER JOIN produits p ON p.id = l.produitId
        WHERE v.date BETWEEN :debut AND :fin
        """
    )
    fun getBeneficeEntre(debut: Long, fin: Long): Flow<Long>

    @Query(
        """
        SELECT p.nom AS nom, SUM(l.quantite) AS quantiteVendue
        FROM lignes_vente l
        INNER JOIN produits p ON p.id = l.produitId
        GROUP BY p.id
        ORDER BY quantiteVendue DESC
        LIMIT :limite
        """
    )
    fun getProduitsLesPlusVendus(limite: Int): Flow<List<ProduitPlusVendu>>
}