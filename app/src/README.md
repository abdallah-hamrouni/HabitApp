HabitApp – Application Android en Jetpack Compose

HabitApp est une application Android développée avec Jetpack Compose, permettant de gérer vos habitudes quotidiennes, consulter vos statistiques, votre profil, ainsi que la météo actuelle.

 1. Authentification (Signup & Login)

L’utilisateur doit d’abord créer un compte via l’écran Signup.

Ensuite, il peut se connecter via l’écran Login.

Après une connexion réussie, l’application redirige automatiquement vers l’écran HabitScreen.

 2. Gestion des Habitudes (CRUD complet)

L’écran HabitScreen permet :

 Ajouter une nouvelle habitude

 Modifier une habitude existante

 Supprimer une habitude

 Marquer comme faite / non faite

L’écran contient un formulaire complet avec date, heure, catégorie, nom de l’habitude, etc.

Toutes les données sont stockées via Room Database.

 3. Statistiques des Habitudes (Donut Chart)

L’écran Statistiques affiche :

Le nombre total d’habitudes

Celles qui sont terminées

Celles en attente

Un graphique circulaire (via Canvas + drawArc) représente :

 Habitudes complétées

 Habitudes non complétées

L’affichage est animé avec animateFloatAsState.

 4. Profil Utilisateur

L’écran Profil contient :

L’adresse email de l’utilisateur

Une carte météo utilisant l’API météo via Ktor Client

Température actuelle

Vitesse du vent

 Cet écran inclut aussi un bouton de déconnexion qui :

Réinitialise l’état de connexion

Vide le backstack

Ramène l’utilisateur vers l’écran Login

 5. Météo avec Ktor

L’application utilise Ktor Client pour consommer une API météo et afficher :

Température actuelle

Vitesse du vent

Icône météo simple

 Technologies utilisées

Kotlin

Jetpack Compose

Room Database

Ktor Client

MVVM + Repository

MutableStateFlow / StateFlow

Canvas & drawArc
