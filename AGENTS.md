# Fossify Contacts personnel

Ce dépôt contient le fork personnel de Fossify Contacts pour Pixel 7 sous GrapheneOS, utilisé avec Fossify Phone et sans services Google. Communiquer en français. Garder la licence et les avis de l'amont.

- `upstream` désigne `FossifyOrg/Contacts`. `origin` désigne `Josic88/Contacts`. Maintenir `main` identique à l'amont et travailler sur `personal`. Regrouper les commits par objectif (identité, fonctionnalité, tests, documentation).
- Construire la variante `foss` avec le wrapper versionné. Établir une compilation témoin de l'amont avant les changements fonctionnels. Éviter les dépendances supplémentaires et les refactorisations générales. Préserver l'utilisation sans Google, root ou ADB.
- Pour les contacts système, distinguer `Contact.id` (contact brut) et `Contact.contactId` (agrégat Android). Exclure les entrées `isPrivate()` avant toute opération sur le fournisseur système. Tenir compte des doublons groupés uniquement par l'interface Fossify.
- Préférer les API Android déjà disponibles. Conserver les changements dans Contacts tant que le besoin ne démontre pas une modification nécessaire de Commons ou Phone. Suivre le design et les traductions anglaise et française.
- Obtenir l'accord du propriétaire avant de publier du code, un tag, une release ou un APK, d'administrer le poste, de manipuler des secrets, de désinstaller une application ou de supprimer des données. Ne jamais enregistrer de secrets dans Git, un artefact CI, la conversation ou les logs.
- Distinguer compilation, analyse statique, tests automatisés et réception de vrais appels/SMS. Mettre à jour les résultats vérifiés et les limites dans `docs/`. Tester la coexistence avec la version officielle.

Pour reprendre, lire [l'inventaire](docs/CUSTOMIZATIONS.md), [la compilation](docs/BUILD.md), [les tests](docs/TESTING.md), [la synchronisation](docs/UPSTREAM.md) et [la signature](docs/SIGNING.md).
