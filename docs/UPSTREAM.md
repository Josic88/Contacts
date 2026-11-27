# Synchroniser le fork

Utiliser le clone qui contient `app/` et `gradlew`. Vérifier que `git status --short` est vide et que les URL de `git remote -v` correspondent au fork et à l'amont. Si ce clone est superficiel, récupérer une fois l'historique avec `git fetch --unshallow upstream` lorsque le réseau est disponible.

```sh
git fetch upstream
git fetch origin
git switch main
git merge --ff-only upstream/main
git rev-parse main upstream/main
```

Les deux empreintes doivent être égales. Une divergence demande d'examiner `git log --left-right main...upstream/main` avant de continuer. Tester la compilation FOSS de `main` conformément à BUILD.md.

```sh
git switch personal
git merge main
```

Sur une branche déjà partagée, une fusion conserve les identifiants des commits publiés. En cas de conflit, examiner en particulier le manifeste, les alias du lanceur, les options de contact et les versions Gradle. Résoudre les fichiers puis utiliser `git add` et `git merge --continue`. Pour abandonner une fusion en conflit, utiliser `git merge --abort`.

Relancer les vérifications de BUILD.md et TESTING.md. Après autorisation de publier, pousser explicitement les branches.

```sh
git push origin main
git push origin personal
```

Le workflow de référence de `ci-foss` extrait un SHA officiel explicite. Pour tester une nouvelle référence en CI, modifier ce SHA sur cette branche et consigner le résultat. L'amont fournit aussi un workflow de release destiné à Fossify (dont Google Play), déclenché sur le marqueur de release de `main`. Vérifier les workflows hérités avant une synchronisation publique. Réserver les éventuels secrets du fork à un environnement de release personnel protégé.
