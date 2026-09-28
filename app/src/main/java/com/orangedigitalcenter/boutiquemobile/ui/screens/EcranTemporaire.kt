package com.orangedigitalcenter.boutiquemobile.ui.screens

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.orangedigitalcenter.boutiquemobile.R

/**
 * Écran provisoire. Le Responsable interface le remplace, écran par écran,
 * par EcranProduits, EcranNouvelleVente, EcranClientsDettes, EcranDashboard.
 */
@Composable
fun EcranTemporaire(@StringRes titre: Int) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = stringResource(titre), style = MaterialTheme.typography.titleLarge)
        Text(text = stringResource(R.string.ecran_a_venir), style = MaterialTheme.typography.bodyLarge)
    }
}
