package com.orangedigitalcenter.boutiquemobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.orangedigitalcenter.boutiquemobile.data.local.entity.Client
import com.orangedigitalcenter.boutiquemobile.data.local.entity.ModePaiement
import com.orangedigitalcenter.boutiquemobile.data.local.entity.Produit
import com.orangedigitalcenter.boutiquemobile.domain.model.LigneVentePanier
import com.orangedigitalcenter.boutiquemobile.ui.components.*
import com.orangedigitalcenter.boutiquemobile.ui.state.NouvelleVenteUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcranNouvelleVente(
    state: NouvelleVenteUiState,
    onAjouterProduit: (Produit) -> Unit,
    onChangerQuantite: (produitId: Long, quantite: Int) -> Unit,
    onRetirerProduit: (produitId: Long) -> Unit,
    onSelectionnerClient: (Client?) -> Unit,
    onChoisirModePaiement: (ModePaiement) -> Unit,
    onMontantPayeChange: (String) -> Unit,
    onValiderVente: () -> Unit,
    onVenteAcquittee: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Nouvelle vente", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp).padding(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                SectionLabel("Produits disponibles")
                if (state.produitsDisponibles.isEmpty()) {
                    EtatVide("Aucune donnée pour le moment", Icons.Filled.Inventory2)
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        items(state.produitsDisponibles, key = { it.id }) { produit ->
                            ProduitChip(produit, onClick = { onAjouterProduit(produit) })
                        }
                    }
                }

                Spacer(Modifier.height(18.dp))
                SectionLabel("Panier")
                if (state.panier.isEmpty()) {
                    EtatVide("Touchez un produit ci-dessus pour l'ajouter", Icons.Filled.ShoppingCart)
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(state.panier, key = { it.produit.id }) { ligne ->
                            LignePanierItem(
                                ligne = ligne,
                                onIncrementer = { onChangerQuantite(ligne.produit.id, ligne.quantite + 1) },
                                onDecrementer = { onChangerQuantite(ligne.produit.id, ligne.quantite - 1) },
                                onSupprimer = { onRetirerProduit(ligne.produit.id) }
                            )
                        }
                    }
                }
            }

            Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.primaryContainer) {
                Row(
                    modifier = Modifier.padding(16.dp).fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Total", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text(formatGnf(state.total), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }

            ClientSelector(clients = state.clients, clientSelectionne = state.clientSelectionne, onSelectionnerClient = onSelectionnerClient)

            SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
                SegmentedButton(
                    selected = state.modePaiement == ModePaiement.ESPECES,
                    onClick = { onChoisirModePaiement(ModePaiement.ESPECES) },
                    shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                ) { Text("Espèces") }
                SegmentedButton(
                    selected = state.modePaiement == ModePaiement.MOBILE_MONEY,
                    onClick = { onChoisirModePaiement(ModePaiement.MOBILE_MONEY) },
                    shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                ) { Text("Mobile Money") }
            }

            OutlinedTextField(
                value = state.montantPayeTexte,
                onValueChange = onMontantPayeChange,
                label = { Text("Montant payé (GNF)") },
                placeholder = { Text("Vide = comptant de ${formatGnf(state.total)}") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )

            state.erreur?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }

            Button(
                onClick = onValiderVente,
                enabled = state.panier.isNotEmpty(),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) { Text("Enregistrer la vente") }
        }
    }

    if (state.venteEnregistree) {
        AlertDialog(
            onDismissRequest = onVenteAcquittee,
            shape = RoundedCornerShape(20.dp),
            icon = { Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.tertiary) },
            title = { Text("Vente enregistrée") },
            text = { Text("La vente a bien été enregistrée.") },
            confirmButton = { Button(onClick = onVenteAcquittee, shape = RoundedCornerShape(12.dp)) { Text("OK") } }
        )
    }
}

@Composable
private fun ProduitChip(produit: Produit, onClick: () -> Unit) {
    Surface(onClick = onClick, shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp, modifier = Modifier.width(140.dp)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(produit.nom, style = MaterialTheme.typography.titleMedium, maxLines = 2)
            Spacer(Modifier.height(4.dp))
            Text(formatGnf(produit.prixVente), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
            Text("Stock : ${produit.stock}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun LignePanierItem(ligne: LigneVentePanier, onIncrementer: () -> Unit, onDecrementer: () -> Unit, onSupprimer: () -> Unit) {
    Surface(shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surface, tonalElevation = 1.dp, modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.padding(10.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(ligne.produit.nom, style = MaterialTheme.typography.titleMedium)
                Text(formatGnf(ligne.sousTotal), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            IconButton(onClick = onDecrementer, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.RemoveCircle, contentDescription = "Diminuer") }
            Text("${ligne.quantite}", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(horizontal = 4.dp))
            IconButton(onClick = onIncrementer, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.AddCircle, contentDescription = "Augmenter") }
            IconButton(onClick = onSupprimer, modifier = Modifier.size(32.dp)) { Icon(Icons.Default.Delete, contentDescription = "Retirer", tint = MaterialTheme.colorScheme.error) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClientSelector(clients: List<Client>, clientSelectionne: Client?, onSelectionnerClient: (Client?) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = clientSelectionne?.nom ?: "Comptant (sans client)",
            onValueChange = {},
            readOnly = true,
            label = { Text("Client") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            DropdownMenuItem(text = { Text("Comptant (sans client)") }, onClick = { onSelectionnerClient(null); expanded = false })
            clients.forEach { client ->
                DropdownMenuItem(text = { Text(client.nom) }, onClick = { onSelectionnerClient(client); expanded = false })
            }
        }
    }
}