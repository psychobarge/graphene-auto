# Spec : tuiles d'appel de contacts

Statut : validée par l'utilisateur, questions ouvertes tranchées · 2026-11-16 · aucune ligne de code écrite

## Le besoin

Depuis l'écran de la voiture, appeler un proche en **un seul appui**, sans ouvrir l'app Téléphone, sans chercher dans la liste et sans confirmation. Chaque contact choisi devient une tuile du menu principal (le tiroir d'apps), à côté de l'app Téléphone.

## Ce qui existe déjà et sera réutilisé

| Besoin | Déjà dans le code |
|---|---|
| Passer l'appel | `placeCall()` (`car/PhoneDialerScreen.kt`) : `TelecomManager.placeCall`, demande CALL_PHONE, renseigne `CarCalls.outgoingCall` pour que la pastille d'appel affiche le nom |
| Suivre / raccrocher | `CarCalls` + la pastille d'appel de la barre du bas (`NavCallPill`) |
| Une tuile dans le menu | `Entry` + `componentId` + `launcherOrder()` (`car/CarLauncher.kt`) |
| Épingler dans la barre | `CarSettings.togglePin` / `pinnedApps`, 3 places max (`MAX_PINNED`) |
| Ordre choisi par le conducteur | `CarSettings.appOrder`, boutons Gauche/Droite du menu d'appui long (`AppMenu`) |
| Masquer | `CarSettings.setAppHidden` (désépingle aussi, changement pas encore commité) |
| Pas de téléphonie | `LauncherMode.canCall()` (masque déjà l'app Téléphone) |
| Demander une permission depuis la voiture | `PermissionActivity.ask()` : la boîte de dialogue s'ouvre sur le téléphone |
| Lire les contacts | `loadContacts()`, `CarCalls.lookUpName()` |

Pas d'`ACTION_CALL` ni d'`ACTION_DIAL` : le commentaire de `placeCall` explique qu'Android refuse d'ouvrir l'activité d'appel du système sur l'écran de la voiture, et le numéroteur s'ouvrirait sur le téléphone, hors de portée du conducteur. Les tuiles passent donc par `placeCall`, comme le clavier et la liste de contacts.

## Décisions prises (avec l'utilisateur)

1. **Placement** : groupe Téléphone du menu, juste après l'app Téléphone. Épinglables dans la barre du bas comme n'importe quelle app.
2. **Choix des contacts** : sur le téléphone uniquement, dans les réglages de la voiture. Rien à configurer en roulant.
3. **Appui simple** : l'appel part tout de suite, sans carte de confirmation.
4. **Plusieurs numéros** : le numéro est fixé quand on ajoute le contact. Une tuile = un numéro ; deux tuiles possibles pour la même personne (Mobile et Travail).
5. **Maximum** : 8 tuiles.
6. **Badge** combiné sur la tuile : oui.
7. **Contact supprimé** : la tuile est retirée d'elle-même au scan suivant.
8. **Ordre** : les tuiles viennent après l'app Téléphone.
9. **Faire connaître** : une ligne dans l'Aide et une entrée dans Quoi de neuf.

## Choisir les contacts (téléphone)

Dans les réglages côté téléphone, groupe **Apps** (`CarGroup.APPS`, écran qui contient déjà la liste des apps à afficher), une carte **Contacts** au-dessus de la liste des apps :

- Une ligne par tuile : photo ou initiale, nom, type et numéro (« Mobile · 06 12 … »).
- **Ajouter un contact** ouvre le sélecteur de numéro d'Android (`ACTION_PICK` sur `CommonDataKinds.Phone.CONTENT_URI`). Ce sélecteur renvoie un numéro précis, pas un contact : c'est ce qui fixe le numéro (décision 4). Il donne un accès ponctuel à la ligne choisie, donc **l'ajout marche sans READ_CONTACTS**.
- Retirer : une icône Supprimer sur la ligne.
- Réordonner : flèches Haut/Bas sur la ligne. L'ordre ici est l'ordre initial des tuiles dans le groupe Téléphone ; un déplacement fait dans la voiture (Gauche/Droite) passe par-dessus, comme pour les apps.
- **Maximum 8 tuiles**. Au-delà, le bouton Ajouter est grisé avec « 8 contacts au maximum ».
- Sur un appareil sans téléphonie (`canCall() == false`), la carte est remplacée par une ligne grisée « Cet appareil ne peut pas passer d'appels ».

Ce qu'on enregistre par tuile (dans `CarSettings`, nouvelle clé, même stockage que les épingles) : la clé de recherche du contact (`LOOKUP_KEY`), l'id de la ligne du numéro (`Phone._ID`), et une copie du nom affiché, du numéro et de son type. La copie sert quand READ_CONTACTS est refusée : on ne peut alors ni lire le contact ni savoir s'il existe encore.

À l'ajout, si READ_CONTACTS est accordée mais que le numéro choisi n'est pas lisible (contact hors de la portée des Contact Scopes de GrapheneOS), la ligne l'indique tout de suite : « Graphene Auto ne voit pas ce contact : ajoutez-le à ses Contact Scopes, sinon la tuile sera retirée au prochain démarrage. »

## La tuile (voiture)

- **Identifiant** : `contact:<id de la ligne du numéro>`, nouveau préfixe à côté de `gearslip:` et `messages:`. Les épingles, l'ordre et le masquage fonctionnent sur cet identifiant sans autre changement.
- **Image** : la photo du contact, découpée en rond, à la taille des icônes d'app (52 dp). Sans photo ou sans READ_CONTACTS : un disque coloré avec l'initiale, comme `ContactRow` dans l'app Téléphone.
- **Petit badge** combiné (icône Appel) en bas à droite de l'image, à l'endroit du badge rouge « ne marche pas », pour qu'on lise « appeler X » et pas « ouvrir l'app X ».
- **Libellé** : le nom, sur 2 lignes max comme les apps. Si la même personne a deux tuiles, on ajoute le type : « Maman · Travail ».
- Les photos sont lues au moment du scan, gardées en mémoire comme les icônes d'apps (`LauncherCache`), jamais écrites sur le disque.

## Appui simple

1. S'il y a déjà un appel (`CarCalls.state` = `Active`) : pas de nouvel appel, toast « Un appel est déjà en cours ». (Un appel qui sonne prend tout l'écran via `CallOverlay`, donc la tuile n'est pas touchable à ce moment-là.)
2. Sans CALL_PHONE : toast « Autorisez les appels sur le téléphone » et `PermissionActivity.ask(CALL_PHONE)`. Pas d'appel automatique une fois la permission donnée : le conducteur rappuie.
3. Sinon : `placeCall(numéro)`. La pastille d'appel apparaît dans la barre du bas, avec le nom. On reste sur l'écran où on était.

## Appui long

Le menu d'app existant (`AppMenu`), avec les mêmes boutons que pour une app : **Épingler / Désépingler**, **Gauche / Droite** (dans le menu), **Annuler**. Le titre de la carte montre la photo/initiale, le nom et le numéro, pour vérifier ce que la tuile appelle. Pas de bouton « Fermer » (rien ne tourne) et pas de « Retirer » : les retraits se font sur le téléphone, comme le choix.

## Avec le masquage, les épingles et l'ordre

- **Masquage** : inutile pour un contact, puisque le retirer revient au même. Les contacts n'apparaissent donc pas dans la liste « apps affichées » ; on retire la tuile dans la carte Contacts. Retirer une tuile la désépingle et la sort de `appOrder`, comme `setAppHidden` le fait pour les apps.
- **Épingles** : une tuile de contact prend une des 3 places de la barre, partagées avec les apps. Dans la barre elle montre la photo/initiale ; appui = appel, appui long = le même menu.
- **Ordre** : `launcherOrder` les range dans le groupe Téléphone, après l'app Téléphone, dans l'ordre de la carte Contacts (pas alphabétique). Les déplacements Gauche/Droite faits dans la voiture s'appliquent ensuite comme pour les apps.
- **Barre des apps ouvertes** : une tuile de contact n'est jamais « ouverte », elle n'y apparaît pas.
- **Assistant vocal** : pas de changement dans cette version (« appelle Maman » cherche déjà dans tous les contacts).

## Permissions

| Permission | Pour quoi | Si refusée |
|---|---|---|
| CALL_PHONE | Passer l'appel (`placeCall`) | Les tuiles restent affichées ; l'appui demande la permission sur le téléphone (voir Appui simple). |
| READ_CONTACTS | Photo, mise à jour du nom/numéro, savoir si le contact existe encore | Les tuiles marchent avec la copie enregistrée : initiale au lieu de la photo, nom et numéro tels qu'à l'ajout. Aucune tuile n'est retirée, puisqu'on ne peut pas savoir si le contact existe encore. Aucune demande à l'ouverture. |

Les deux sont déjà déclarées dans le manifeste et demandées par le guide d'installation (`SetupGuideScreen`). Rien de nouveau à déclarer.

GrapheneOS **Contact Scopes** : si READ_CONTACTS est limitée à certains contacts, Android répond comme si elle était accordée, et un contact hors de la portée ressemble exactement à un contact supprimé. Il sera donc retiré au scan suivant. C'est pourquoi l'écran d'ajout prévient tout de suite quand le contact choisi n'est pas visible (voir Choisir les contacts).

## Cas limites

| Cas | Comportement |
|---|---|
| Contact supprimé (ou hors des Contact Scopes) | Au scan suivant (début de session voiture, ou ouverture de la carte Contacts), et seulement si READ_CONTACTS est accordée : la tuile est retirée, désépinglée et sortie de `appOrder`, comme quand on la retire à la main. Sans message. |
| Numéro supprimé du contact (la ligne `Phone._ID` n'existe plus) | Même chose : une tuile appelle un numéro, ce numéro n'existe plus, la tuile est retirée. |
| Numéro modifié dans le contact (même ligne, autre valeur) | La tuile prend le nouveau numéro au scan suivant. |
| Nom ou photo modifiés | Repris au prochain scan (début de session voiture, comme les apps). |
| Pas de téléphonie (tablette, autoradio Android en mode lanceur) | Aucune tuile de contact, comme l'app Téléphone (`canCall()`). Les réglages sont gardés, pour le jour où l'appareil peut appeler. |
| Appel déjà en cours | Toast, pas de deuxième appel (voir Appui simple). |
| Deux SIM, « Demander à chaque fois » | Android affiche le choix de SIM sur le téléphone, pas dans la voiture. Hors périmètre : on laisse faire Android et on le note dans l'aide. |
| Mode avion / pas de réseau | Telecom refuse ou l'appel échoue ; Graphene Auto ne fait rien de plus que pour le clavier (l'appel ne part pas, pas de pastille). |
| Appui par erreur | L'appel part. La pastille de la barre permet de raccrocher tout de suite. C'est le prix du « un seul appui » choisi. |

## Hors périmètre (cette version)

- Ajouter ou retirer une tuile depuis la voiture.
- Choisir le numéro au moment de l'appel.
- SMS ou message vocal depuis une tuile.
- Favoris Android (étoile) synchronisés automatiquement.
- Priorité aux tuiles dans la commande vocale « appelle X ».

## Faire connaître la fonction

- **Aide** (`ui/HelpScreen.kt`) : une ligne « Appeler un proche en un appui : ajoutez-le dans Réglages › Apps › Contacts ; sa tuile apparaît dans le groupe Téléphone du menu de la voiture. »
- **Quoi de neuf** (`WhatsNewScreen` côté téléphone, `CarWhatsNew` côté voiture) : une entrée pour la version qui livre la fonction, avec le même texte en plus court.

## Questions ouvertes

Aucune : toutes tranchées le 2026-11-16 (voir Décisions prises, points 5 à 9).
