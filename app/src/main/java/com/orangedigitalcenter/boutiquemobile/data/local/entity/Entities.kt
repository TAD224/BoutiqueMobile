package com.orangedigitalcenter.boutiquemobile.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Mode de paiement d'une vente. CREDIT n'existe pas ici : une vente à crédit
 * est simplement une vente dont montantPaye < total (voir Vente).
 */
enum class ModePaiement { ESPECES, MOBILE_MONEY }

@Entity(tableName = "produits")
data class Produit(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nom: String,
    val prixAchat: Long,   // en GNF
    val prixVente: Long,   // en GNF
    val stock: Int,
    val seuilAlerte: Int
)

@Entity(tableName = "clients")
data class Client(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val nom: String,
    val telephone: String? = null
)

@Entity(
    tableName = "ventes",
    foreignKeys = [
        ForeignKey(
            entity = Client::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("clientId")]
)
data class Vente(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clientId: Long? = null,        // null = vente comptant, client non identifié
    val date: Long,                    // timestamp epoch millis
    val total: Long,                   // en GNF
    val montantPaye: Long,             // en GNF ; si montantPaye < total => dette (vente à crédit)
    val modePaiement: ModePaiement
)

@Entity(
    tableName = "lignes_vente",
    foreignKeys = [
        ForeignKey(
            entity = Vente::class,
            parentColumns = ["id"],
            childColumns = ["venteId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Produit::class,
            parentColumns = ["id"],
            childColumns = ["produitId"],
            onDelete = ForeignKey.RESTRICT
        )
    ],
    indices = [Index("venteId"), Index("produitId")]
)
data class LigneVente(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val venteId: Long,
    val produitId: Long,
    val quantite: Int,
    val prixUnitaire: Long   // copie du prixVente au moment de la vente (garde l'historique)
)

@Entity(
    tableName = "encaissements",
    foreignKeys = [
        ForeignKey(
            entity = Client::class,
            parentColumns = ["id"],
            childColumns = ["clientId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("clientId")]
)
data class Encaissement(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val clientId: Long,
    val montant: Long,   // en GNF
    val date: Long
)
