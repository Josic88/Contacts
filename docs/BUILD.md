# Compiler Fossify Contacts personnel

Exécuter les commandes à la racine du dépôt Contacts. La base du fork est `f7254a0845e1227a1e52dd08ebf3cd2bac852d4c` sur `FossifyOrg/Contacts:main`. L'application annonce la version 1.6.0, code 13. La dernière release officielle observée est la 1.6.0 du 30 janvier 2026. Le commit de référence du 24 novembre (heure de Paris) intègre une mise à jour de Fastlane. Les versions ci-dessous correspondent au commit, y compris les évolutions intervenues après la release.

| Outil ou composant | Référence |
| --- | --- |
| JDK utilisé par la CI | Temurin 17 |
| Gradle / Android Gradle Plugin | wrapper 9.8.2 / AGP 9.5.1 |
| SDK de compilation et cible / minimum | 36 / 26 |
| Bibliothèques principales | Fossify Commons 7.1.3, AndroidX Room 2.8.5, SwipeRefreshLayout 1.2.0, ez-vcard 0.12.2, AutoFitTextView et IndicatorFastScroll |
| Génération et analyse | KSP 2.3.12, Detekt 1.23.8 |
| Dépôts de dépendances | Google Maven, Maven Central, JitPack et Maven local selon la configuration officielle |

La résolution initiale télécharge Gradle et les dépendances, notamment depuis le dépôt Maven de Google pour AndroidX et les outils Android. La variante `foss` fonctionne sans compte Google ni services Google sur le téléphone. Android Studio est facultatif. Utiliser un JDK 17 et un Android SDK contenant la plateforme 36 et les Build Tools requis par AGP. Indiquer le chemin du SDK via `ANDROID_HOME` ou `sdk.dir` dans le fichier local `local.properties`. Laisser l'installation d'outils système et l'acceptation des licences au choix du propriétaire.

## Construire l'amont

Depuis un arbre propre sur `main`, avec l'environnement de signature absent, lancer :

```sh
git rev-parse HEAD
./gradlew --no-daemon :app:assembleFossRelease
```

La [compilation de référence](https://github.com/Josic88/Contacts/actions/runs/45999482076) a produit un APK release non signé à partir du commit exact indiqué ci-dessus. Son SHA-256 était `aa202d41430344e3f492cd55b69c525c953957b2bd9efdd920c0a287de29242c`. Cette somme décrit ce build.

## Construire la version personnelle

```sh
git switch personal
./gradlew :app:assembleFossDebug :app:assembleFossRelease
./gradlew :app:testFossDebugUnitTest :app:lintFossDebug :app:assembleFossDebugAndroidTest
```

Gradle écrit l'APK de test signé dans `app/build/outputs/apk/foss/debug/` et la release dans `app/build/outputs/apk/foss/release/`. En l'absence de configuration de signature, seule la release est non signée. L'édition debug utilise `org.fossify.contacts.personal.debug`, la release `org.fossify.contacts.personal`. La procédure de signature durable figure dans [SIGNING.md](SIGNING.md).

Le workflow personnel lance les vérifications à chaque envoi sur `personal`, vérifie le package et le certificat debug, puis exécute les tests instrumentés sur un émulateur AOSP. Les APK restent sur le runner pendant le job. Les journaux contiennent les résultats, les empreintes et le nom du package. Le transfert privé d'un APK compilé localement peut se faire par câble USB en mode fichiers, puis par ouverture du fichier sur le Pixel. L'installation de l'édition officielle n'est pas une étape de la compilation.

[Configuration Gradle de référence](https://github.com/FossifyOrg/Contacts/blob/f7254a0845e1227a1e52dd08ebf3cd2bac852d4c/app/build.gradle.kts) · [Catalogue des dépendances](https://github.com/FossifyOrg/Contacts/blob/f7254a0845e1227a1e52dd08ebf3cd2bac852d4c/gradle/libs.versions.toml)
