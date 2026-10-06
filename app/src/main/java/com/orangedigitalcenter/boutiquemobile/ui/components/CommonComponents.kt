package com.orangedigitalcenter.boutiquemobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

fun formatGnf(montant: Long): String = "%,d".format(montant).replace(',', ' ') + " GNF"

@Composable
fun SectionLabel(texte: String, modifier: Modifier = Modifier) {
    Text(
        text = texte.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(bottom = 6.dp)
    )
}

@Composable
fun StatCard(
    icone: ImageVector,
    titre: String,
    valeur: String,
    couleurFond: Color,
    couleurContenu: Color,
    modifier: Modifier = Modifier
) {
    Surface(modifier = modifier, shape = RoundedCornerShape(18.dp), color = couleurFond) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier.size(36.dp).clip(CircleShape).background(couleurContenu.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icone, contentDescription = null, tint = couleurContenu, modifier = Modifier.size(18.dp))
            }
            Spacer(Modifier.height(12.dp))
            Text(titre, style = MaterialTheme.typography.bodyMedium, color = couleurContenu.copy(alpha = 0.85f))
            Text(valeur, style = MaterialTheme.typography.titleLarge, color = couleurContenu)
        }
    }
}

@Composable
fun Pill(texte: String, couleurFond: Color, couleurTexte: Color, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(50), color = couleurFond) {
        Text(
            texte,
            style = MaterialTheme.typography.labelMedium,
            color = couleurTexte,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

@Composable
fun Avatar(initiale: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.secondaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            initiale.uppercase(),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
}

@Composable
fun EtatVide(message: String, icone: ImageVector = Icons.Default.Inbox, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            icone, contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.size(40.dp)
        )
        Spacer(Modifier.height(10.dp))
        Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}