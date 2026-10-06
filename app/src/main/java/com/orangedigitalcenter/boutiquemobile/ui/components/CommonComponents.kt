package com.orangedigitalcenter.boutiquemobile.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

fun formatGnf(montant: Long): String = "%,d".format(montant).replace(',', ' ') + " GNF"

object Spacing {
    val screenPadding = 20.dp
    val sectionGap = 22.dp
    val itemGap = 10.dp
    val cardCorner = 18.dp
}

@Composable
fun SectionLabel(texte: String, modifier: Modifier = Modifier) {
    Text(
        text = texte.uppercase(),
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        modifier = modifier.padding(bottom = 8.dp)
    )
}

/** Carte standard avec vraie ombre portée, utilisée partout pour une apparence cohérente. */
@Composable
fun AppCard(
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    val shape = RoundedCornerShape(Spacing.cardCorner)
    val colors = CardDefaults.cardColors(containerColor = containerColor)
    val elevation = CardDefaults.cardElevation(defaultElevation = 2.dp, pressedElevation = 1.dp)
    if (onClick != null) {
        Card(onClick = onClick, modifier = modifier, shape = shape, colors = colors, elevation = elevation) { content() }
    } else {
        Card(modifier = modifier, shape = shape, colors = colors, elevation = elevation) { content() }
    }
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
    AppCard(modifier = modifier, containerColor = couleurFond) {
        Column(modifier = Modifier.padding(18.dp)) {
            Box(
                modifier = Modifier.size(38.dp).clip(CircleShape).background(couleurContenu.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icone, contentDescription = null, tint = couleurContenu, modifier = Modifier.size(19.dp))
            }
            Spacer(Modifier.height(14.dp))
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
            .size(46.dp)
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
    AppCard(modifier = modifier.fillMaxWidth(), containerColor = MaterialTheme.colorScheme.surfaceVariant) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                icone, contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f),
                modifier = Modifier.size(42.dp)
            )
            Spacer(Modifier.height(10.dp))
            Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun ScreenHeader(titre: String, sousTitre: String? = null, modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(horizontal = Spacing.screenPadding, vertical = 8.dp)) {
        Text(titre, style = MaterialTheme.typography.titleLarge)
        if (sousTitre != null) {
            Text(sousTitre, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}