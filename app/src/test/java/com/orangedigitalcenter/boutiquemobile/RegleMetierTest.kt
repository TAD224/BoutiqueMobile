package com.orangedigitalcenter.boutiquemobile
import com.orangedigitalcenter.boutiquemobile.data.local.entity.*
import com.orangedigitalcenter.boutiquemobile.domain.model.*
import com.orangedigitalcenter.boutiquemobile.domain.repository.*
import com.orangedigitalcenter.boutiquemobile.ui.state.*

import com.orangedigitalcenter.boutiquemobile.domain.usecase.*
import org.junit.Assert.*
import org.junit.Test

class RegleMetierTest {
    private val riz = Produit(1, "Riz 25kg", 130000, 150000, 40, 5)
    private val huile = Produit(2, "Huile 5L", 50000, 60000, 10, 5)

    // ---- Panier ----
    @Test fun `total du panier avec plusieurs articles`() {
        var p = PanierRules.ajouter(emptyList(), riz)
        p = PanierRules.changerQuantite(p, 1, 2)          // 2 x 150 000
        p = PanierRules.ajouter(p, huile)                 // + 60 000
        assertEquals(360_000L, PanierRules.total(p))
    }
    @Test fun `ajouter deux fois le meme produit incremente la quantite`() {
        val p = PanierRules.ajouter(PanierRules.ajouter(emptyList(), riz), riz)
        assertEquals(1, p.size); assertEquals(2, p[0].quantite); assertEquals(300_000L, p[0].sousTotal)
    }
    @Test fun `quantite zero retire la ligne`() {
        val p = PanierRules.changerQuantite(PanierRules.ajouter(emptyList(), riz), 1, 0)
        assertTrue(p.isEmpty()); assertEquals(0L, PanierRules.total(p))
    }

    // ---- Dette ----
    @Test fun `dette = credit moins encaissements`() {
        val ventes = listOf(
            Vente(1, 1, 0, 150_000, 50_000, ModePaiement.ESPECES),   // credit 100 000
            Vente(2, 1, 0, 60_000, 60_000, ModePaiement.MOBILE_MONEY) // comptant
        )
        val enc = listOf(Encaissement(1, 1, 30_000, 0))
        assertEquals(70_000L, DetteRules.soldeDu(ventes, enc))
    }
    @Test fun `dette jamais negative`() {
        assertEquals(0L, DetteRules.soldeDu(emptyList(), listOf(Encaissement(1, 1, 5_000, 0))))
    }

    // ---- Validation vente ----
    private val panier = listOf(PanierRules.creerLigne(riz, 1)) // total 150 000
    @Test fun `panier vide refuse`() = assertNotNull(ValidationRules.validerVente(emptyList(), 0, null))
    @Test fun `montant negatif refuse`() = assertEquals("Le montant payé ne peut pas être négatif.", ValidationRules.validerVente(panier, -1, 1))
    @Test fun `credit sans client refuse`() = assertNotNull(ValidationRules.validerVente(panier, 50_000, null))
    @Test fun `credit avec client accepte`() = assertNull(ValidationRules.validerVente(panier, 50_000, 1))
    @Test fun `paiement comptant accepte sans client`() = assertNull(ValidationRules.validerVente(panier, 150_000, null))
    @Test fun `montant superieur au total refuse`() = assertNotNull(ValidationRules.validerVente(panier, 200_000, 1))
    @Test fun `encaissement superieur a la dette refuse`() = assertNotNull(ValidationRules.validerEncaissement(80_000, 70_000))
}
