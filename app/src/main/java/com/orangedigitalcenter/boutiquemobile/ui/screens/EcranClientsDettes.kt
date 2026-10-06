package com.orangedigitalcenter.boutiquemobile.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp


data class Client(val id: Long, val nom: String, val telephone: String)
data class ClientAvecDette(val client: Client, val soldeDu: Long)

data class ClientsDettesUiState(
    val clients: List<ClientAvecDette> = emptyList(),
    val chargement: Boolean = false,
    val erreur: String? = null,
    val encaissementEnregistre: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EcranClientsDettes(
    state: ClientsDettesUiState,
    onAjouterClient: (nom: String, telephone: String) -> Unit,
    onEncaisserPaiement: (clientId: Long, montant: Long) -> Unit,
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Clients & Dettes") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter un client")
            }
        },
        modifier = modifier
    ) { paddingValues ->
        if (state.chargement) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                contentPadding = paddingValues,
                modifier = Modifier.fillMaxSize().padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.clients) { clientDette ->
                    ClientItem(
                        clientAvecDette = clientDette,
                        onEncaissementClick = { clientPourEncaissement = it }
                    )
                }
            }
        }

        if (showAddDialog) {
            AjoutClientDialog(
                onDismiss = { showAddDialog = false },
                onConfirm = { nom, tel ->
                    onAjouterClient(nom, tel)
                    showAddDialog = false
                }
            )
        }

        clientPourEncaissement?.let { clientDette ->
            EncaissementDialog(
                clientAvecDette = clientDette,
                onDismiss = { clientPourEncaissement = null },
                onConfirm = { montant ->
                    onEncaisserPaiement(clientDette.client.id, montant)
                }
            )
        }
    }
}

@Composable
fun ClientItem(clientAvecDette: ClientAvecDette, onEncaissementClick: (ClientAvecDette) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                // Utilisation de la nouvelle structure client.nom et client.telephone
                Text(text = clientAvecDette.client.nom, style = MaterialTheme.typography.titleMedium)
                Text(text = clientAvecDette.client.telephone, style = MaterialTheme.typography.bodyMedium)

                val detteColor = if (clientAvecDette.soldeDu > 0) Color.Red else Color(0xFF2E7D32)
                Text(
                    text = if (clientAvecDette.soldeDu > 0) "Dette : ${clientAvecDette.soldeDu} GNF" else "À jour",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    color = detteColor,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (clientAvecDette.soldeDu > 0) {
                Button(onClick = { onEncaissementClick(clientAvecDette) }) {
                    Icon(Icons.Default.Check, contentDescription = "Encaisser", modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Encaisser")
                }
            }
        }
    }
}

@Composable
fun AjoutClientDialog(
    onDismiss: () -> Unit,
    onConfirm: (nom: String, telephone: String) -> Unit
) {
    var nom by remember { mutableStateOf("") }
    var telephone by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Nouveau Client") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = nom,
                    onValueChange = { nom = it },
                    label = { Text("Nom complet") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = telephone,
                    onValueChange = { telephone = it },
                    label = { Text("Numéro de téléphone") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(onClick = { if (nom.isNotBlank()) onConfirm(nom, telephone) }) {
                Text("Ajouter")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}

@Composable
fun EncaissementDialog(
    clientAvecDette: ClientAvecDette,
    onDismiss: () -> Unit,
    onConfirm: (montant: Long) -> Unit
) {
    var montantSaisi by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Encaissement - ${clientAvecDette.client.nom}") }, // Appel avec la nouvelle structure
        text = {
            Column {
                Text("Solde actuel dû : ${clientAvecDette.soldeDu} GNF", modifier = Modifier.padding(bottom = 16.dp))
                OutlinedTextField(
                    value = montantSaisi,
                    onValueChange = { montantSaisi = it },
                    label = { Text("Montant reçu (GNF)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val montant = montantSaisi.toLongOrNull() ?: 0L
                    if (montant > 0) onConfirm(montant)
                }
            ) {
                Text("Valider le paiement")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Annuler") }
        }
    )
}