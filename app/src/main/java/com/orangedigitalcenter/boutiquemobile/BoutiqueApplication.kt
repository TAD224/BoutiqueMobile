package com.orangedigitalcenter.boutiquemobile

import android.app.Application
import com.orangedigitalcenter.boutiquemobile.data.local.AppDatabase

/**
 * Point d'entrée de l'application. Déclarée dans AndroidManifest.xml (android:name).
 * Les RepositoryImpl (Responsable données) pourront récupérer la base ici.
 */
class BoutiqueApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getInstance(this) }
}
