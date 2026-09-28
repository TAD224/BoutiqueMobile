package com.orangedigitalcenter.boutiquemobile.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.orangedigitalcenter.boutiquemobile.data.local.dao.ClientDao
import com.orangedigitalcenter.boutiquemobile.data.local.dao.EncaissementDao
import com.orangedigitalcenter.boutiquemobile.data.local.dao.ProduitDao
import com.orangedigitalcenter.boutiquemobile.data.local.dao.VenteDao
import com.orangedigitalcenter.boutiquemobile.data.local.entity.Client
import com.orangedigitalcenter.boutiquemobile.data.local.entity.Encaissement
import com.orangedigitalcenter.boutiquemobile.data.local.entity.LigneVente
import com.orangedigitalcenter.boutiquemobile.data.local.entity.Produit
import com.orangedigitalcenter.boutiquemobile.data.local.entity.Vente

/**
 * Base Room de l'application (offline-first).
 * Propriétaire : Responsable données. Toute modification d'entité => incrémenter
 * `version` et écrire une migration (les schémas sont exportés dans app/schemas).
 * L'enum ModePaiement est gérée nativement par Room (stockée en texte).
 */
@Database(
    entities = [
        Produit::class,
        Client::class,
        Vente::class,
        LigneVente::class,
        Encaissement::class
    ],
    version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun produitDao(): ProduitDao
    abstract fun clientDao(): ClientDao
    abstract fun venteDao(): VenteDao
    abstract fun encaissementDao(): EncaissementDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "boutique_mobile.db"
                ).build().also { instance = it }
            }
    }
}
