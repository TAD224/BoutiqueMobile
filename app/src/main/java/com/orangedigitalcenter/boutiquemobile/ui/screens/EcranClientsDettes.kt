package com.orangedigitalcenter.boutiquemobile.ui.screens

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
import com.orangedigitalcenter.boutiquemobile.domain.model.ClientAvecDette
import com.orangedigitalcenter.boutiquemobile.ui.components.*
import com.orangedigitalcenter.boutiquemobile.ui.state.ClientsDettesUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcranClientsDettes(
    state: ClientsDettesUiState,
    onAjouterClient: (nom: String, telephone: String) -> Unit,
    onEncaisserPaiement: (client: ClientAvecDette, montantTexte: String) -> Unit,
    onResetEncaissement: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var clientPourEncaissement by remember { mutableStateOf<ClientAvecDette?>(null) }

    LaunchedEffect(state.encaissementEnregistre) {
        if (state.encaissementEnregistre) {
            clientPourEncaissement = null
            onResetEncaissement()
        }
    }

    val totalDette = state.clients.sumOf { it.soldeDu }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }, shape = CircleShape) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter un client")
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = modifier
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(paddingValues),
            verticalArrangement = Arrangement.spacedBy(Spacing.itemGap),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item { ScreenHeader("Clients & dettes", "${state.clients.size} client(s)") }

            if (totalDette > 0) {
                item {
                    AppCard(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = Spacing.screenPadding)
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp).fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total des dettes", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onErrorContainer)
                            Text(formatGnf(totalDette), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onErrorContainer)
                        }
                    }
                }
            }

            if (state.chargement) {
                item { CircularProgressIndicator(modifier = Modifier.padding(horizontal = Spacing.screenPadding)) }
            } else if (state.clients.isEmpty()) {
                item {
                    Box(modifier = Modifier.padding(horizontal = Spacing.screenPadding)) {
                        EtatVide("Aucune donnée pour le moment", Icons.Filled.People)
                    }
                }
            } else {
                items(state.clients, key = { it.client.id }) { clientDette ->
                    Box(modifier = Modifier.padding(horizontal = Spacing.screenPadding)) {
                        ClientItem(clientAvecDette = clientDette, onEncaissementClick = { clientPourEncaissement = it })
                    }
                }
            }
            state.erreur?.let { erreur ->
                item { Text(erreur, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(horizontal = Spacing.screenPadding)) }
            }
        }
        if (showAddDialog) {
            AjoutClientDialog(onDismiss = { showAddDialog = false }, onConfirm = { nom, tel -> onAjouterClient(nom, tel); showAddDialog = false })
        }
        clientPourEncaissement?.let { clientDette ->
            EncaissementDialog(
                clientAvecDette = clientDette,
                onDismiss = { clientPourEncaissement = null },
                onConfirm = { montantTexte -> onEncaisserPaiement(clientDette, montantTexte) }
            )
        }
    }
}

@Composable
fun ClientItem(clientAvecDette: ClientAvecDette, onEncaissementClick: (ClientAvecDette) -> Unit) {
    val aDette = clientAvecDette.soldeDu > 0
    AppCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Avatar(initiale = clientAvecDette.client.nom.take(1))
                Spacer(Modifier.width(14.dp))
                Column {
                    Text(clientAvecDette.client.nom, style = MaterialTheme.typography.titleMedium)
                    Text(clientAvecDette.client.telephone ?: "Pas de téléphone", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            if (aDette) {
                Column(horizontalAlignment = Alignment.End) {
                    Pill(formatGnf(clientAvecDette.soldeDu), couleurFond = MaterialTheme.colorScheme.errorContainer, couleurTexte = MaterialTheme.colorScheme.onErrorContainer)
                    Spacer(Modifier.height(6.dp))
                    TextButton(onClick = { onEncaissementClick(clientAvecDette) }) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Encaisser")
                    }
                }
            } else {
                Pill("À jour", couleurFond = MaterialTheme.colorScheme.tertiaryContainer, couleurTexte = MaterialTheme.colorScheme.onTertiaryContainer)
            }
        }
    }
}

@Composable
fun AjoutClientDialog(onDismiss: () -> Unit, onConfirm: (nom: String, telephone: String) -> Unit) {
    var nom by remember { mutableStateOf("") }
    var telephone by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(22.dp),
        title = { Text("Nouveau client") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(value = nom, onValueChange = { nom = it }, label = { Text("Nom complet") }, singleLine = true, shape = RoundedCornerShape(12.dp))
                OutlinedTextField(value = telephone, onValueChange = { telephone = it }, label = { Text("Numéro de téléphone") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), singleLine = true, shape = RoundedCornerShape(12.dp))
            }
        },
        confirmButton = { Button(onClick = { if (nom.isNotBlank()) onConfirm(nom, telephone) }, shape = RoundedCornerShape(12.dp)) { Text("Ajouter") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } }
    )
}

@Composable
fun EncaissementDialog(clientAvecDette: ClientAvecDette, onDismiss: () -> Unit, onConfirm: (montantTexte: String) -> Unit) {
    var montantSaisi by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(22.dp),
        title = { Text("Encaissement · ${clientAvecDette.client.nom}") },
        text = {
            Column {
                Text("Solde dû : ${formatGnf(clientAvecDette.soldeDu)}", modifier = Modifier.padding(bottom = 14.dp), color = MaterialTheme.colorScheme.error)
                OutlinedTextField(value = montantSaisi, onValueChange = { montantSaisi = it }, label = { Text("Montant reçu (GNF)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, shape = RoundedCornerShape(12.dp))
            }
        },
        confirmButton = { Button(onClick = { if (montantSaisi.isNotBlank()) onConfirm(montantSaisi) }, shape = RoundedCornerShape(12.dp)) { Text("Valider le paiement") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Annuler") } }
    )
}