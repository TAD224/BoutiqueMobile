package com.orangedigitalcenter.boutiquemobile.domain.usecase
import com.orangedigitalcenter.boutiquemobile.data.local.entity.*
import com.orangedigitalcenter.boutiquemobile.domain.model.*
import com.orangedigitalcenter.boutiquemobile.domain.repository.*
import com.orangedigitalcenter.boutiquemobile.ui.state.*


/** Règles métier pures (sans Android) : faciles à tester. Tous les montants sont des Long (GNF). */
object PanierRules {

    fun creerLigne(produit: Produit, quantite: Int) =
        LigneVentePanier(produit, quantite, produit.prixVente * quantite)

    fun total(panier: List<LigneVentePanier>): Long = panier.sumOf { it.sousTotal }

    /** Ajoute 1 unité (ou crée la ligne). Sous-total recalculé à chaque changement. */
    fun ajouter(panier: List<LigneVentePanier>, produit: Produit): List<LigneVentePanier> {
        val existante = panier.find { it.produit.id == produit.id }
        return if (existante == null) panier + creerLigne(produit, 1)
        else changerQuantite(panier, produit.id, existante.quantite + 1)
    }

    /** quantite <= 0 : la ligne est retirée. */
    fun changerQuantite(panier: List<LigneVentePanier>, produitId: Long, quantite: Int): List<LigneVentePanier> =
        panier.mapNotNull { l ->
            when {
                l.produit.id != produitId -> l
                quantite <= 0 -> null
                else -> creerLigne(l.produit, quantite)
            }
        }

    fun retirer(panier: List<LigneVentePanier>, produitId: Long) = changerQuantite(panier, produitId, 0)
}

object DetteRules {
    /** Dette = somme des (total - montantPaye) des ventes - encaissements, jamais négative. */
    fun soldeDu(ventes: List<Vente>, encaissements: List<Encaissement>): Long {
        val credit = ventes.sumOf { (it.total - it.montantPaye).coerceAtLeast(0) }
        return (credit - encaissements.sumOf { it.montant }).coerceAtLeast(0)
    }
}

object ValidationRules {

    /** Retourne un message d'erreur en français, ou null si tout est valide. */
    fun validerVente(panier: List<LigneVentePanier>, montantPaye: Long?, clientId: Long?): String? {
        if (panier.isEmpty()) return "Le panier est vide. Ajoutez au moins un produit."
        if (montantPaye == null) return "Veuillez saisir un montant payé valide (chiffres uniquement)."
        if (montantPaye < 0) return "Le montant payé ne peut pas être négatif."
        val total = PanierRules.total(panier)
        if (montantPaye > total) return "Le montant payé ne peut pas dépasser le total."
        if (montantPaye < total && clientId == null) return "Une vente à crédit nécessite de sélectionner un client."
        return null
    }

    fun validerEncaissement(montant: Long?, soldeDu: Long): String? = when {
        montant == null -> "Veuillez saisir un montant valide."
        montant <= 0 -> "Le montant encaissé doit être supérieur à 0."
        montant > soldeDu -> "Le montant dépasse la dette du client."
        else -> null
    }

    fun validerProduit(nom: String, prixAchat: Long?, prixVente: Long?, stock: Int?, seuil: Int?): String? = when {
        nom.isBlank() -> "Le nom du produit est obligatoire."
        prixAchat == null || prixAchat < 0 -> "Le prix d'achat est invalide."
        prixVente == null || prixVente < 0 -> "Le prix de vente est invalide."
        stock == null || stock < 0 -> "La quantité en stock est invalide."
        seuil == null || seuil < 0 -> "Le seuil d'alerte est invalide."
        else -> null
    }
}

/** Début et fin (millisecondes) de la journée contenant [maintenant]. */
fun bornesDuJour(maintenant: Long = System.currentTimeMillis()): Pair<Long, Long> {
    val cal = java.util.Calendar.getInstance().apply {
        timeInMillis = maintenant
        set(java.util.Calendar.HOUR_OF_DAY, 0); set(java.util.Calendar.MINUTE, 0)
        set(java.util.Calendar.SECOND, 0); set(java.util.Calendar.MILLISECOND, 0)
    }
    val debut = cal.timeInMillis
    return debut to (debut + 24L * 60 * 60 * 1000 - 1)
}
