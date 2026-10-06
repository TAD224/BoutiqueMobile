package com.orangedigitalcenter.boutiquemobile.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.orangedigitalcenter.boutiquemobile.data.local.entity.Produit
import com.orangedigitalcenter.boutiquemobile.domain.model.ProduitPlusVendu
import com.orangedigitalcenter.boutiquemobile.ui.components.*
import com.orangedigitalcenter.boutiquemobile.ui.state.DashboardUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcranDashboard(state: DashboardUiState, modifier: Modifier = Modifier) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Tableau de bord", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(bottom = 24.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    StatCard(
                        icone = Icons.Filled.TrendingUp,
                        titre = "Ventes du jour",
                        valeur = formatGnf(state.ventesDuJour),
                        couleurFond = MaterialTheme.colorScheme.primaryContainer,
                        couleurContenu = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        icone = Icons.Filled.Savings,
                        titre = "Bénéfice du jour",
                        valeur = formatGnf(state.beneficeDuJour),
                        couleurFond = MaterialTheme.colorScheme.tertiaryContainer,
                        couleurContenu = MaterialTheme.colorScheme.onTertiaryContainer,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                Column {
                    SectionLabel("Alertes stock bas")
                    if (state.produitsStockBas.isEmpty()) {
                        Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                            EtatVide("Aucune donnée pour le moment", Icons.Filled.Inventory2)
                        }
                    }
                }
            }
            items(state.produitsStockBas, key = { "stock-${it.id}" }) { produit ->
                LigneStockBas(produit)
            }

            item {
                Column {
                    SectionLabel("Produits les plus vendus")
                    if (state.produitsPlusVendus.isEmpty()) {
                        Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                            EtatVide("Aucune donnée pour le moment", Icons.Filled.BarChart)
                        }
                    }
                }
            }
            items(state.produitsPlusVendus) { produitVendu ->
                LigneProduitVendu(produitVendu)
            }
        }
    }
}

@Composable
private fun LigneStockBas(produit: Produit) {
    Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.errorContainer, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Warning, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(produit.nom, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onErrorContainer)
            }
            Pill(
                "Stock : ${produit.stock}",
                couleurFond = MaterialTheme.colorScheme.error,
                couleurTexte = MaterialTheme.colorScheme.onError
            )
        }
    }
}

@Composable
private fun LigneProduitVendu(produitVendu: ProduitPlusVendu) {
    Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surface, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(32.dp).clip(CircleShape).background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.Star, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer, modifier = Modifier.size(16.dp))
                }
                Spacer(Modifier.width(10.dp))
                Text(produitVendu.nom, style = MaterialTheme.typography.bodyLarge)
            }
            Text("${produitVendu.quantiteVendue} vendus", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}