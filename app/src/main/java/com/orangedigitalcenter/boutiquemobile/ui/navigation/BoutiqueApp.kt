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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.orangedigitalcenter.boutiquemobile.R
import com.orangedigitalcenter.boutiquemobile.ui.screens.EcranTemporaire

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
 * Squelette de navigation (barre du bas + 4 écrans).
 * Propriétaire : Responsable interface, qui remplace les EcranTemporaire.
 */
@Composable
fun BoutiqueApp() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

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
            composable(Routes.DASHBOARD) { EcranTemporaire(R.string.nav_dashboard) }
            composable(Routes.PRODUITS) { EcranTemporaire(R.string.nav_produits) }
            composable(Routes.NOUVELLE_VENTE) { EcranTemporaire(R.string.nav_nouvelle_vente) }
            composable(Routes.CLIENTS_DETTES) { EcranTemporaire(R.string.nav_clients_dettes) }
        }
    }
}
