package com.orangedigitalcenter.boutiquemobile.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import com.orangedigitalcenter.boutiquemobile.data.local.entity.Produit
import com.orangedigitalcenter.boutiquemobile.ui.components.*
import com.orangedigitalcenter.boutiquemobile.ui.state.ProduitsUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcranProduitsStock(
    state: ProduitsUiState,
    onRechercheChange: (String) -> Unit,
    onEnregistrerProduit: (id: Long, nom: String, prixAchatTxt: String, prixVenteTxt: String, stockTxt: String, seuilTxt: String) -> Unit,
    onResetEnregistrement: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    var produitEdition by remember { mutableStateOf<Produit?>(null) }

    LaunchedEffect(state.produitEnregistre) {
        if (state.produitEnregistre) {
            showDialog = false
            onResetEnregistrement()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Produits", style = MaterialTheme.typography.titleLarge) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { produitEdition = null; showDialog = true }, shape = CircleShape) {
                Icon(Icons.Default.Add, contentDescription = "Nouveau produit")
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { paddingValues ->
        Column(
            modifier = Modifier.fillMaxSize().padding(paddingValues).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            OutlinedTextField(
                value = state.recherche,
                onValueChange = onRechercheChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Rechercher un produit...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )
            if (state.chargement) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (state.produits.isEmpty()) {
                EtatVide("Aucune donnée pour le moment", Icons.Filled.Inventory2)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp), contentPadding = PaddingValues(bottom = 90.dp)) {
                    items(state.produits, key = { it.id }) { produit ->
                        ProduitItem(produit = produit, onModifierClick = { produitEdition = it; showDialog = true })
                    }
                }
            }
            state.erreur?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }
        }
        if (showDialog) {
            ProduitFormDialog(
                produit = produitEdition,
                onDismiss = { showDialog = false },
                onConfirm = { nom, prixAchatTxt, prixVenteTxt, stockTxt, seuilTxt ->
                    onEnregistrerProduit(produitEdition?.id ?: 0L, nom, prixAchatTxt, prixVenteTxt, stockTxt, seuilTxt)
                }
            )
        }
    }
}

@Composable
fun ProduitItem(produit: Produit, onModifierClick: (Produit) -> Unit) {
    val stockBas = produit.stock <= produit.seuilAlerte
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp,
        modifier = Modifier.fillMaxWidth().clickable { onModifierClick(produit) }
    ) {
        Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Avatar(initiale = produit.nom.take(1))
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(produit.nom, style = MaterialTheme.typography.titleMedium)
                    Text(formatGnf(produit.prixVente), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Pill(
                    texte = if (stockBas) "Stock bas · ${produit.stock}" else "Stock ${produit.stock}",
                    couleurFond = if (stockBas) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.surfaceVariant,
                    couleurTexte = if (stockBas) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(6.dp))
                IconButton(onClick = { onModifierClick(produit) }, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Edit, contentDescription = "Modifier", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

@Composable
fun ProduitFormDialog(
    produit: Produit?,
    onDismiss: () -> Unit,
    onConfirm: (nom: String, prixAchatTxt: String, prixVenteTxt: String, stockTxt: String, seuilTxt: String) -> Unit
) {
    var nom by remember { mutableStateOf(produit?.nom ?: "") }
    var prixAchat by remember { mutableStateOf(produit?.prixAchat?.toString() ?: "") }
    var prixVente by remember { mutableStateOf(produit?.prixVente?.toString() ?: "") }
    var stock by remember { mutableStateOf(produit?.stock?.toString() ?: "") }
    var seuil by remember { mutableStateOf(produit?.seuilAlerte?.toString() ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        title = { Text(if (produit == null) "Ajouter un produit" else "Modifier le produit") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = nom, onValueChange = { nom = it }, label = { Text("Nom du produit") }, singleLine = true, shape = RoundedCornerShape(12.dp))
                OutlinedTextField(value = prixAchat, onValueChange = { prixAchat = it }, label = { Text("Prix d'achat (GNF)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, shape = RoundedCornerShape(12.dp))
                OutlinedTextField(value = prixVente, onValueChange = { prixVente = it }, label = { Text("Prix de vente (GNF)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, shape = RoundedCornerShape(12.dp))
                OutlinedTextField(value = stock, onValueChange = { stock = it }, label = { Text("Quantité en stock") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, shape = RoundedCornerShape(12.dp))
                OutlinedTextField(value = seuil, onValueChange = { seuil = it }, label = { Text("Seuil d'alerte") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, shape = RoundedCornerShape(12.dp))
            }
        },
        confirmButton = {
            Button(onClick = { if (nom.isNotBlank()) onConfirm(nom, prixAchat, prixVente, stock, seuil) }, shape = RoundedCornerShape(12.dp)) {
                Text("Enregistrer")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } }
    )
}