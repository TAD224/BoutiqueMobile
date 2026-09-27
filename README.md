# Boutique Mobile

Application Android (Kotlin) qui aide les petits commerçants de quartier à suivre leur
stock, leurs ventes — y compris les ventes à crédit — et leur bénéfice réel, sans dépendre
d'une connexion internet.

Projet réalisé dans le cadre de la formation en développement mobile Kotlin de l'Orange
Digital Center.

## Fonctionnalités du MVP

- **Produits et stock** : ajout, modification, alerte de stock bas
- **Nouvelle vente** : panier multi-articles, total automatique, vente à crédit rattachée à un client
- **Clients et dettes** : suivi des soldes dus, encaissement partiel ou total
- **Tableau de bord** : ventes du jour, bénéfice, produits les plus vendus, stock bas

L'application fonctionne entièrement hors ligne : toutes les données sont stockées
localement avec Room et survivent à la fermeture de l'application.

## Architecture

MVVM (Interface → ViewModel → Repository → DAO/Room), avec le Repository placé derrière
une interface pour permettre de remplacer plus tard les données locales par une vraie API
sans toucher à l'interface utilisateur.

Le détail des contrats (entités, DAO, interfaces Repository, UiState) et la répartition du
travail entre les 4 rôles de l'équipe sont documentés dans [`CONTRATS.md`](./CONTRATS.md).

## Installation

1. Cloner le dépôt :
   ```bash
   git clone https://github.com/<organisation>/boutique-mobile.git
   ```
2. Ouvrir le dossier dans Android Studio (Giraffe ou plus récent recommandé).
3. Laisser Gradle synchroniser les dépendances.
4. Lancer l'application sur un émulateur ou un appareil réel (`Run ▶`).

Aucune clé API ni configuration réseau n'est nécessaire : l'application est offline-first.

## Captures d'écran

| Produits et stock | Nouvelle vente | Clients et dettes | Tableau de bord |
|---|---|---|---|
| *(à ajouter)* | *(à ajouter)* | *(à ajouter)* | *(à ajouter)* |

## Équipe

| Rôle | Membre |
|---|---|
| Chef de projet et intégration | *(ton nom)* |
| Responsable données | |
| Responsable interface | |
| Responsable logique et qualité | |

## Licence

Projet pédagogique — Orange Digital Center.
