# Tester le renvoi vers la messagerie

## Tests automatisés

Utiliser un émulateur AOSP vierge (API 35 dans la CI) pour les tests instrumentés. Ils créent leurs propres fiches, lisent le fournisseur Android et nettoient uniquement leurs données de test. La commande cible les appareils connectés : utiliser un environnement de test dédié.

```sh
./gradlew :app:lintFossDebug :app:testFossDebugUnitTest
./gradlew :app:connectedFossDebugAndroidTest
```

`VoicemailTest` ouvre une véritable ViewContactActivity, active le menu depuis une fiche brute dont l'ID diffère de l'agrégat, vérifie le champ système et un contact témoin, rouvre la fiche et désactive le renvoi. Un second scénario ouvre une fiche privée et vérifie que le contrôle est indisponible. La CI installe aussi l’APK officiel FOSS 1.6.0 (empreinte vérifiée) à côté du fork sur l’émulateur. Les tests interrogent `PhoneLookup.SEND_TO_VOICEMAIL` pour les deux numéros de l’agrégat et un numéro témoin. Le rapport instrumenté est sous `app/build/reports/androidTests/connected/`. Une tâche Gradle `NO-SOURCE` indique l'absence de tests pour cette tâche.

## Essai téléphonique sur Pixel 7

Prévoir le Pixel, une SIM avec messagerie opérationnelle et un second téléphone avec un numéro présenté. Utiliser une fiche système de test et un autre numéro témoin, dans le même profil Android. Noter la version de GrapheneOS, l'opérateur et la version de l'APK. Accorder au fork l'autorisation Contacts normale pour modifier le champ. Les « Périmètres des contacts » de GrapheneOS peuvent donner une lecture sélective mais bloquent toute écriture. [Guide GrapheneOS](https://grapheneos.org/usage#contact-scopes)

| Essai | Résultat à relever |
| --- | --- |
| Installation | Les deux éditions de Contacts s'ouvrent. Fossify Phone reste le téléphone par défaut. Vérifier les libellés français et anglais et, si utilisée, la couleur d'icône du lanceur. |
| Témoin initial | Renvoi décoché, sonnerie active, mode Ne pas déranger désactivé. L'appel de test sonne. Un rejet manuel permet de vérifier séparément la messagerie de l'opérateur. |
| Activation | Cocher l'option dans le menu de la fiche. Revenir à la liste puis rouvrir la fiche. La coche persiste. |
| Appel réel | Écran verrouillé, appeler ce numéro depuis le second téléphone. Relever sonnerie, vibration, affichage d'appel et délai entendu avant le message d'accueil. Laisser un court message et vérifier sa réception. Refaire l'essai écran allumé et depuis l'autre numéro si le contact en a deux. |
| SMS et contact témoin | Envoyer un vrai SMS depuis le numéro renvoyé et vérifier son arrivée. Appeler depuis le numéro témoin et vérifier que le Pixel sonne. |
| Désactivation | Décocher, rouvrir la fiche puis rappeler. La sonnerie et la possibilité de décrocher doivent revenir. |
| Cas limites | Essayer une fiche privée, des homonymes regroupés par Fossify, deux sources d'un même agrégat, une permission refusée et les périmètres GrapheneOS. Vérifier que l'affichage reflète l'état réel et qu'une fiche homonyme reste intacte. |
| Persistance | Après redémarrage, vérifier l'état et l'appel avec le renvoi activé, puis rétablir le réglage initial sur la fiche de test. |

Aucun accès root, ADB ou service Google n'est nécessaire pour ce protocole sur le Pixel. Ne pas reporter de numéro réel, de nom de contact ni de contenu de SMS dans des journaux publics.

## État des validations

- Compilation du code officiel FOSS réussie dans le [job témoin](https://github.com/Josic88/Contacts/actions/runs/45999482076).
- Le 27 novembre 2026, la [CI du commit d1363ae](https://github.com/Josic88/Contacts/actions/runs/46006456691) a compilé les éditions FOSS debug et release, exécuté Lint et vérifié la signature et le package de l’APK debug. Le runner Android a rapporté `OK (2 tests)` sur Android 15 / API 35. Les deux éditions (officielle 1.6.0 et personnelle debug) étaient installées simultanément.
- Le scénario système a vérifié l’activation, la persistance et la désactivation, les deux numéros vus via PhoneLookup et le contact témoin. Le scénario privé a vérifié l’indisponibilité du réglage. La tâche de tests JVM a indiqué `NO-SOURCE`. La CI ne conserve ni APK ni clé de signature.
- Essais radio sur le Pixel 7 et sur son opérateur à effectuer. Relever les résultats séparément pour les appels, la messagerie, les SMS et la désactivation.
