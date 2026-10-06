package com.orangedigitalcenter.boutiquemobile.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.orangedigitalcenter.boutiquemobile.BoutiqueApplication
import com.orangedigitalcenter.boutiquemobile.R
import com.orangedigitalcenter.boutiquemobile.data.local.repository.ClientRepositoryImpl
import com.orangedigitalcenter.boutiquemobile.data.local.repository.ProduitRepositoryImpl
import com.orangedigitalcenter.boutiquemobile.data.local.repository.VenteRepositoryImpl
import com.orangedigitalcenter.boutiquemobile.ui.screens.EcranClientsDettes
import com.orangedigitalcenter.boutiquemobile.ui.screens.EcranDashboard
import com.orangedigitalcenter.boutiquemobile.ui.screens.EcranNouvelleVente
import com.orangedigitalcenter.boutiquemobile.ui.screens.EcranProduitsStock
import com.orangedigitalcenter.boutiquemobile.ui.viewmodel.BoutiqueViewModelFactory
import com.orangedigitalcenter.boutiquemobile.ui.viewmodel.ClientViewModel
import com.orangedigitalcenter.boutiquemobile.ui.viewmodel.DashboardViewModel
import com.orangedigitalcenter.boutiquemobile.ui.viewmodel.ProduitViewModel
import com.orangedigitalcenter.boutiquemobile.ui.viewmodel.VenteViewModel

object Routes {
    const val DASHBOARD = "dashboard"
    const val PRODUITS = "produits"
    const val NOUVELLE_VENTE = "nouvelle_vente"
    const val CLIENTS_DETTES = "clients_dettes"
}

private data class Destination(
    val route: String,
    @StringRes val label: Int,
    val icon: ImageVector
)

private val destinations = listOf(
    Destination(Routes.DASHBOARD, R.string.nav_dashboard, Icons.Filled.Home),
    Destination(Routes.PRODUITS, R.string.nav_produits, Icons.AutoMirrored.Filled.List),
    Destination(Routes.NOUVELLE_VENTE, R.string.nav_nouvelle_vente, Icons.Filled.ShoppingCart),
    Destination(Routes.CLIENTS_DETTES, R.string.nav_clients_dettes, Icons.Filled.Person)
)

/**
 * Squelette de navigation (barre du bas + 4 écrans), branché sur la vraie base Room
 * via BoutiqueApplication.database, offline-first.
 */
@Composable
fun BoutiqueApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val app = LocalContext.current.applicationContext as BoutiqueApplication
    val database = app.database

    val produitRepo = remember { ProduitRepositoryImpl(database.produitDao()) }
    val clientRepo = remember { ClientRepositoryImpl(database.clientDao(), database.venteDao(), database.encaissementDao()) }
    val venteRepo = remember { VenteRepositoryImpl(database) }
    val factory = remember { BoutiqueViewModelFactory(produitRepo, clientRepo, venteRepo) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                destinations.forEach { destination ->
                    NavigationBarItem(
                        selected = currentRoute == destination.route,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(destination.icon, contentDescription = null) },
                        label = { Text(stringResource(destination.label)) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.DASHBOARD,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.DASHBOARD) {
                val viewModel: DashboardViewModel = viewModel(factory = factory)
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                EcranDashboard(state = state)
            }
            composable(Routes.PRODUITS) {
                val viewModel: ProduitViewModel = viewModel(factory = factory)
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                EcranProduitsStock(
                    state = state,
                    onRechercheChange = viewModel::onRechercheChange,
                    onEnregistrerProduit = viewModel::sauvegarderProduit,
                    onResetEnregistrement = viewModel::effacerMessages
                )
            }
            composable(Routes.NOUVELLE_VENTE) {
                val viewModel: VenteViewModel = viewModel(factory = factory)
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                EcranNouvelleVente(
                    state = state,
                    onAjouterProduit = viewModel::ajouterProduit,
                    onChangerQuantite = viewModel::changerQuantite,
                    onRetirerProduit = viewModel::retirerProduit,
                    onSelectionnerClient = viewModel::selectionnerClient,
                    onChoisirModePaiement = viewModel::choisirModePaiement,
                    onMontantPayeChange = viewModel::onMontantPayeChange,
                    onValiderVente = viewModel::validerVente,
                    onVenteAcquittee = viewModel::venteAcquittee
                )
            }
            composable(Routes.CLIENTS_DETTES) {
                val viewModel: ClientViewModel = viewModel(factory = factory)
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                EcranClientsDettes(
                    state = state,
                    onAjouterClient = viewModel::ajouterClient,
                    onEncaisserPaiement = viewModel::encaisser,
                    onResetEncaissement = viewModel::effacerMessages
                )
            }
        }
    }
}