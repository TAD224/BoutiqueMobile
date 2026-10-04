package com.orangedigitalcenter.boutiquemobile
import com.orangedigitalcenter.boutiquemobile.data.local.entity.*
import com.orangedigitalcenter.boutiquemobile.domain.model.*
import com.orangedigitalcenter.boutiquemobile.domain.repository.*
import com.orangedigitalcenter.boutiquemobile.ui.state.*

import com.orangedigitalcenter.boutiquemobile.data.fake.*
import com.orangedigitalcenter.boutiquemobile.ui.viewmodel.VenteViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.junit.Assert.*

@OptIn(ExperimentalCoroutinesApi::class)
class VenteViewModelTest {
    @Before fun setUp() = Dispatchers.setMain(UnconfinedTestDispatcher())
    @After fun tearDown() = Dispatchers.resetMain()

    private fun creer(): Triple<VenteViewModel, FakeVenteRepository, FakeProduitRepository> {
        val produits = FakeProduitRepository(); val ventes = FakeVenteRepository(produits)
        return Triple(VenteViewModel(ventes, produits, FakeClientRepository(ventes), { 1_000L }), ventes, produits)
    }

    @Test fun `le total se recalcule a chaque changement de quantite`() {
        val (vm, _, _) = creer()
        val riz = vm.uiState.value.produitsDisponibles.first()
        vm.ajouterProduit(riz); assertEquals(150_000L, vm.uiState.value.total)
        vm.changerQuantite(riz.id, 3); assertEquals(450_000L, vm.uiState.value.total)
    }

    @Test fun `validation avec panier vide affiche une erreur sans planter`() {
        val (vm, ventes, _) = creer()
        vm.validerVente()
        assertEquals("Le panier est vide. Ajoutez au moins un produit.", vm.uiState.value.erreur)
        assertTrue(ventes.ventes.value.isEmpty())
    }

    @Test fun `vente enregistree et panier vide ensuite`() {
        val (vm, ventes, _) = creer()
        vm.ajouterProduit(vm.uiState.value.produitsDisponibles.first())
        vm.validerVente()  // champ vide = paye comptant
        assertEquals(1, ventes.ventes.value.size)
        assertTrue(vm.uiState.value.venteEnregistree); assertTrue(vm.uiState.value.panier.isEmpty())
    }

    @Test fun `vente a credit exige un client`() {
        val (vm, ventes, _) = creer()
        vm.ajouterProduit(vm.uiState.value.produitsDisponibles.first())
        vm.onMontantPayeChange("50000"); vm.validerVente()
        assertNotNull(vm.uiState.value.erreur); assertTrue(ventes.ventes.value.isEmpty())
        vm.selectionnerClient(vm.uiState.value.clients.first()); vm.validerVente()
        assertEquals(1, ventes.ventes.value.size)
    }

    @Test fun `stock insuffisant refuse l'ajout`() {
        val (vm, _, _) = creer()
        val huile = vm.uiState.value.produitsDisponibles.first { it.id == 2L } // stock = 2
        vm.ajouterProduit(huile); vm.ajouterProduit(huile); vm.ajouterProduit(huile)
        assertEquals(2, vm.uiState.value.panier.first().quantite); assertNotNull(vm.uiState.value.erreur)
    }
}
