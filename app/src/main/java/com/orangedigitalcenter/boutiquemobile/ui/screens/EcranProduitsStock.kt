package com.orangedigitalcenter.boutiquemobile.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp


data class Produit(val id: Long, val nom: String, val prix: Long, val stock: Int)

data class ProduitsUiState(
    val produits: List<Produit> = emptyList(),
    val produitsStockBas: List<Produit> = emptyList(),
    val recherche: String = "",
    val chargement: Boolean = false,
    val erreur: String? = null,
    val produitEnregistre: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcranProduitsStock(
    state: ProduitsUiState,
    onRechercheChange: (String) -> Unit,
    onEnregistrerProduit: (id: Long?, nom: String, prix: Long, stock: Int) -> Unit,
    onResetEnregistrement: () -> Unit, // Pour réinitialiser le booléen après fermeture
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var produitEdition by remember { mutableStateOf<Produit?>(null) }

    // Écoute le changement d'état du ViewModel pour fermer le dialogue
    LaunchedEffect(state.produitEnregistre) {
        if (state.produitEnregistre) {
            showDialog = false
            onResetEnregistrement()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gestion des Produits") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                produitEdition = null
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Nouveau produit")
            }
        },
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Ajout du champ de recherche mentionné dans le contrat
            OutlinedTextField(
                value = state.recherche,
                onValueChange = onRechercheChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Rechercher un produit...") },
                singleLine = true
            )

            if (state.chargement) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(state.produits) { produit ->
                        ProduitItem(
                            produit = produit,
                            onModifierClick = {
                                produitEdition = it
                                showDialog = true
                            }
                        )
                    }
                }
            }
        }

        if (showDialog) {
            ProduitFormDialog(
                produit = produitEdition,
                onDismiss = { showDialog = false },
                onConfirm = { nom, prix, stock ->
                    onEnregistrerProduit(produitEdition?.id, nom, prix, stock)
                }
            )
        }
    }
}

@Composable
fun ProduitItem(produit: Produit, onModifierClick: (Produit) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onModifierClick(produit) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = produit.nom, style = MaterialTheme.typography.titleMedium)
                Text(text = "Prix : ${produit.prix} GNF", style = MaterialTheme.typography.bodyMedium)
                val stockColor = if (produit.stock <= 5) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                Text(
                    text = "En stock : ${produit.stock}",
                    style = MaterialTheme.typography.bodySmall,
                    color = stockColor
                )
            }
            IconButton(onClick = { onModifierClick(produit) }) {
                Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun ProduitFormDialog(
    produit: Produit?,
    onDismiss: () -> Unit,
    onConfirm: (nom: String, prix: Long, stock: Int) -> Unit
) {
    var nom by remember { mutableStateOf(produit?.nom ?: "") }
    var prix by remember { mutableStateOf(produit?.prix?.toString() ?: "") }
    var stock by remember { mutableStateOf(produit?.stock?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (produit == null) "Ajouter un produit" else "Modifier le produit") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nom,
                    onValueChange = { nom = it },
                    label = { Text("Nom du produit") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = prix,
                    onValueChange = { prix = it },
                    label = { Text("Prix unitaire (GNF)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
                OutlinedTextField(
                    value = stock,
                    onValueChange = { stock = it },
                    label = { Text("Quantité en stock") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val prixParsed = prix.toLongOrNull() ?: 0L
                    val stockParsed = stock.toIntOrNull() ?: 0
                    if (nom.isNotBlank()) onConfirm(nom, prixParsed, stockParsed)
                }
            ) {
                Text("Enregistrer")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}