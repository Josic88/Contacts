# Signature des APK

La série de mises à jour personnelles doit conserver son applicationId et sa clé de signature. Chaque version distribuée reçoit un versionCode supérieur à celui de la précédente. La clé officielle de Fossify ne fait pas partie du processus.

## Première signature durable

Après autorisation du propriétaire, créer une clé avec le JDK dans un emplacement privé hors dépôt. L'exemple utilise un trousseau PKCS#12, une clé RSA 4096 bits et une validité de 10 000 jours. Vérifier que le chemin choisi est libre. Saisir le mot de passe au prompt interactif.

```sh
umask 077
keytool -genkeypair -keystore /chemin/prive/contacts-personal.p12 \
  -storetype PKCS12 -alias contacts-personal -keyalg RSA -keysize 4096 \
  -validity 10000
```

Conserver le mot de passe dans le gestionnaire de mots de passe du propriétaire. Conserver une sauvegarde chiffrée du trousseau sur un support distinct. Vérifier la sauvegarde en y lisant le certificat avec `keytool -list -v`, puis comparer les empreintes SHA-256. Seule l'empreinte publique peut figurer dans le dépôt.

## Signer une release FOSS

Construire un APK release non signé comme dans BUILD.md, puis utiliser le binaire `apksigner` des Android SDK Build Tools sur l'APK produit par Gradle. Les fichiers de sortie doivent être hors du dépôt. Gradle effectue déjà l'alignement. Toute modification du contenu après signature invalide celle-ci.

```sh
apksigner sign --ks /chemin/prive/contacts-personal.p12 --ks-key-alias contacts-personal \
  --out /chemin/sortie/contacts-personal.apk /chemin/vers/foss-release-unsigned.apk
apksigner verify --verbose --print-certs /chemin/sortie/contacts-personal.apk
sha256sum /chemin/sortie/contacts-personal.apk
```

Comparer l'empreinte du signataire à celle conservée lors de la première signature. Une clé debug, y compris celle générée sur un runner éphémère, convient à un essai. Deux runners peuvent produire des certificats debug différents. Utiliser une release signée avec la clé durable pour conserver les données privées lors des mises à jour. Garder l'édition debug dans son package distinct.

## Distribution ultérieure

Compiler en CI sans secret, rapatrier l'APK, vérifier le commit et signer localement. Après accord de publication, créer un tag personnel sur le commit testé, préparer une GitHub Release en brouillon et y joindre l'APK signé, sa somme SHA-256, l'empreinte du certificat, les sources correspondantes et les résultats de test. Vérifier le fichier téléchargé avant de rendre la release publique. Conserver LICENSE et la source complète de la version publiée.

Si la signature est un jour déplacée en CI, utiliser un environnement GitHub protégé avec approbation, réservé aux tags autorisés. Éviter `secrets: inherit` pour la clé personnelle. Reconstituer le trousseau uniquement sur un runner éphémère et limiter l'artefact au seul APK. Ne confier aucun secret de signature aux builds de PR ou aux workflows hérités.
