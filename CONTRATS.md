# Contrats — Projet 6 : Boutique Mobile

Ce dossier contient les **contrats** (entités Room, DAO, interfaces Repository, UiState)
validés en équipe avant le développement. Ils servent de point de rendez-vous entre les
4 rôles pour que chacun puisse coder en parallèle sans attendre les autres.

## Chaîne de dépendances du projet

```
Room (entités + DAO)  →  Repository (interface)  →  ViewModel  →  UI (écrans)
   Responsable données         ↑                  Responsable       Responsable
                          implémentée par            logique          interface
                        le Responsable données
```

## Qui utilise quoi

| Fichier | Qui l'implémente | Qui le consomme |
|---|---|---|
| `data/local/entity/Entities.kt` | Responsable données | tout le monde (types de base) |
| `data/local/dao/*.kt` | Responsable données | Responsable données (dans les `RepositoryImpl`) |
| `domain/model/ModelsUi.kt` | — (déjà défini) | Repository, ViewModel, UI |
| `domain/repository/*.kt` | Responsable données (`ProduitRepositoryImpl`, etc.) | Responsable logique (dans les ViewModels) |
| `ui/state/UiStates.kt` | Responsable logique (produit dans le ViewModel) | Responsable interface (observé dans les écrans) |

## Référence détaillée par rôle : fichiers, classes, variables et méthodes

### 🗄️ Responsable données

**Fichier `data/local/entity/Entities.kt`**

| Classe | Champs |
|---|---|
| `enum ModePaiement` | `ESPECES`, `MOBILE_MONEY` |
| `data class Produit` | `id: Long`, `nom: String`, `prixAchat: Long`, `prixVente: Long`, `stock: Int`, `seuilAlerte: Int` |
| `data class Client` | `id: Long`, `nom: String`, `telephone: String?` |
| `data class Vente` | `id: Long`, `clientId: Long?`, `date: Long`, `total: Long`, `montantPaye: Long`, `modePaiement: ModePaiement` |
| `data class LigneVente` | `id: Long`, `venteId: Long`, `produitId: Long`, `quantite: Int`, `prixUnitaire: Long` |
| `data class Encaissement` | `id: Long`, `clientId: Long`, `montant: Long`, `date: Long` |

**Fichier `data/local/dao/ProduitDao.kt`**

| Interface | Méthodes |
|---|---|
| `ProduitDao` | `getAll(): Flow<List<Produit>>`, `getById(id: Long): Flow<Produit?>`, `getStockBas(): Flow<List<Produit>>`, `insert(produit: Produit): Long`, `update(produit: Produit)`, `delete(produit: Produit)`, `decrementerStock(produitId: Long, quantite: Int)` |

**Fichier `data/local/dao/ClientDao.kt`**

| Interface | Méthodes |
|---|---|
| `ClientDao` | `getAll(): Flow<List<Client>>`, `getById(id: Long): Flow<Client?>`, `insert(client: Client): Long`, `update(client: Client)`, `delete(client: Client)` |

**Fichier `data/local/dao/VenteDao.kt`**

| Interface | Méthodes |
|---|---|
| `VenteDao` | `getAll(): Flow<List<Vente>>`, `getById(id: Long): Flow<Vente?>`, `getEntre(debut: Long, fin: Long): Flow<List<Vente>>`, `getLignes(venteId: Long): Flow<List<LigneVente>>`, `insertVente(vente: Vente): Long`, `insertLignes(lignes: List<LigneVente>)`, `getSoldeDuParVentes(clientId: Long): Flow<Long>` |

**Fichier `data/local/dao/EncaissementDao.kt`**

| Interface | Méthodes |
|---|---|
| `EncaissementDao` | `getParClient(clientId: Long): Flow<List<Encaissement>>`, `insert(encaissement: Encaissement): Long`, `getTotalEncaisseParClient(clientId: Long): Flow<Long>` |

**À créer par le Responsable données (pas encore dans les contrats) :**
- `AppDatabase.kt` — classe `@Database` qui déclare les 5 entités et expose les 4 DAO
- `ProduitRepositoryImpl.kt`, `ClientRepositoryImpl.kt`, `VenteRepositoryImpl.kt` — implémentations réelles des interfaces `domain/repository/*.kt`, en s'appuyant sur les DAO ci-dessus

---

### ⚙️ Responsable logique et qualité

**Interfaces qu'il consomme (déjà définies, ne pas modifier seul) :**

Fichier `domain/repository/ProduitRepository.kt`

| Interface | Méthodes |
|---|---|
| `ProduitRepository` | `getAllProduits(): Flow<List<Produit>>`, `getProduitById(id: Long): Flow<Produit?>`, `getProduitsStockBas(): Flow<List<Produit>>`, `ajouterProduit(produit: Produit): Long`, `modifierProduit(produit: Produit)`, `supprimerProduit(produit: Produit)` |

Fichier `domain/repository/ClientRepository.kt`

| Interface | Méthodes |
|---|---|
| `ClientRepository` | `getAllClients(): Flow<List<Client>>`, `getClientById(id: Long): Flow<Client?>`, `getClientsAvecDette(): Flow<List<ClientAvecDette>>`, `ajouterClient(client: Client): Long`, `enregistrerEncaissement(encaissement: Encaissement)` |

Fichier `domain/repository/VenteRepository.kt`

| Interface | Méthodes |
|---|---|
| `VenteRepository` | `getAllVentes(): Flow<List<Vente>>`, `getVenteDetail(venteId: Long): Flow<VenteDetail?>`, `getVentesDuJour(): Flow<List<Vente>>`, `enregistrerVente(clientId: Long?, lignesPanier: List<LigneVentePanier>, montantPaye: Long, modePaiement: ModePaiement): Long`, `getBeneficeDuJour(): Flow<Long>`, `getProduitsLesPlusVendus(limite: Int): Flow<List<Pair<String, Int>>>` |

**Fichier `domain/model/ModelsUi.kt`** (modèles qu'il manipule dans ses ViewModels)

| Classe | Champs / propriétés |
|---|---|
| `data class LigneVentePanier` | `produitId: Long`, `nomProduit: String`, `quantite: Int`, `prixUnitaire: Long` + calculé `sousTotal: Long` |
| `data class VenteDetail` | `venteId: Long`, `date: Long`, `nomClient: String?`, `lignes: List<LigneVentePanier>`, `total: Long`, `montantPaye: Long`, `modePaiement: ModePaiement` + calculé `dette: Long` |
| `data class ClientAvecDette` | `clientId: Long`, `nom: String`, `telephone: String?`, `soldeDu: Long` |

**Fichier `ui/state/UiStates.kt`** (ce qu'il produit et expose à l'interface via `StateFlow`)

| Classe | Champs |
|---|---|
| `data class ProduitsUiState` | `produits: List<Produit>`, `produitsStockBas: List<Produit>`, `chargement: Boolean`, `erreur: String?` |
| `data class NouvelleVenteUiState` | `produitsDisponibles: List<Produit>`, `panier: List<LigneVentePanier>`, `clientId: Long?`, `modePaiement: ModePaiement`, `montantPaye: String`, `total: Long`, `venteEnregistree: Boolean`, `erreur: String?` |
| `data class ClientsDettesUiState` | `clients: List<ClientAvecDette>`, `chargement: Boolean` |
| `data class DashboardUiState` | `nombreVentesDuJour: Int`, `beneficeDuJour: Long`, `produitsStockBas: List<Produit>`, `produitsLesPlusVendus: List<Pair<String, Int>>` |

**À créer par le Responsable logique (pas encore dans les contrats) :**
- `ProduitViewModel.kt` — expose `StateFlow<ProduitsUiState>`
- `VenteViewModel.kt` — expose `StateFlow<NouvelleVenteUiState>`
- `ClientViewModel.kt` — expose `StateFlow<ClientsDettesUiState>`
- `DashboardViewModel.kt` — expose `StateFlow<DashboardUiState>`

---

### 🎨 Responsable interface

**Classes qu'il observe (déjà définies dans `ui/state/UiStates.kt`, voir tableau ci-dessus) :**
`ProduitsUiState`, `NouvelleVenteUiState`, `ClientsDettesUiState`, `DashboardUiState`

**Écrans à créer (pas encore dans les contrats), un par écran attendu du sujet :**

| Écran à créer | Observe | Contenu attendu |
|---|---|---|
| `EcranProduits.kt` | `ProduitsUiState` | Liste des produits, ajout, modification, indicateur stock bas |
| `EcranNouvelleVente.kt` | `NouvelleVenteUiState` | Panier, choix client (optionnel), mode de paiement |
| `EcranClientsDettes.kt` | `ClientsDettesUiState` | Liste des clients, solde dû, bouton encaissement |
| `EcranDashboard.kt` | `DashboardUiState` | Ventes du jour, bénéfice, stock bas |
| `Navigation.kt` | — | Relie les 4 écrans (NavHost / NavGraph) |

---

### 🧭 Chef de projet et intégration

Ne possède pas de fichier de code propre : coordonne le planning, le dépôt Git (voir plus bas), la fusion des branches et la préparation de l'APK final. Vérifie que chaque rôle respecte les noms de fichiers et de classes listés ci-dessus pour que l'intégration ne casse rien.

## Comment démarrer dès maintenant, sans attendre

- **Responsable données** : commence l'implémentation Room tout de suite (aucune dépendance).
  Crée `AppDatabase`, puis les `*RepositoryImpl` qui implémentent les interfaces ci-dessus.
- **Responsable logique** : code les ViewModels contre les interfaces `ProduitRepository`,
  `ClientRepository`, `VenteRepository`. En attendant que Room soit prêt, utilise une fausse
  implémentation en mémoire (ex. `FakeProduitRepository` avec une `MutableStateFlow` et une
  liste en dur) pour tester le ViewModel de façon autonome.
- **Responsable interface** : construit les écrans à partir des classes `XxxUiState`, avec
  des valeurs factices en `@Preview` ou un ViewModel de test, sans attendre le vrai ViewModel.
- **Chef de projet** : une fois ces contrats validés par les 4 membres, crée les branches Git
  (une branche par rôle/fonctionnalité) — c'est la prochaine étape.

## Règle à respecter par tous

Ne modifiez pas un fichier de ce dossier seul dans votre coin. Ces signatures sont partagées :
si une méthode manque à une interface ou qu'un champ manque à un modèle, on en discute en
groupe et on l'ajoute ensemble, plutôt que de la dupliquer dans une implémentation.
