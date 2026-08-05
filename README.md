# ontorela-sql-generator (sql-gen)

Librairie Java qui génère du code SQL multidialecte (PostgreSQL, MSSQL) à partir d'un schéma
relationnel introspecté ou décrit en configuration, dans le cadre du projet **OntoRelα**.

> **Note de transparence** : ce projet reprend et documente un travail réalisé initialement en
> stage à l'Université de Sherbrooke (GRIIS), réorganisé et nettoyé pour ce portfolio.

## Contexte

Projet mené entre février et juin 2024 au sein du **GRIIS** (Groupe de Recherche
Interdisciplinaire en Informatique de la Santé, Faculté des sciences + Faculté de médecine,
Université de Sherbrooke), sous la responsabilité de **Christina Khnaisser**.

Le projet-cadre plus large, **OntoRelα**, vise à résoudre la fragmentation des données de
santé/recherche biomédicale au Canada en construisant automatiquement un modèle relationnel
temporalisé à partir d'une ontologie (OWL/Protégé). `sql-gen` en est la brique de sortie : elle
prend un schéma relationnel déjà construit et génère le SQL correspondant selon le dialecte cible.

Le besoin métier, tel que documenté dans le projet (voir `doc/requirements-draft.adoc`) : plusieurs
applications du système de santé utilisaient des modules de génération de SQL par gabarits
(templates) qui avaient été copiés-collés dans deux projets séparés — toute correction devait donc
être répétée deux fois. `sql-gen` centralise ce module dans une librairie externe réutilisable par
les deux projets.

**Mon rôle** : SQL Automation Architect — conception et implémentation de la librairie de
génération SQL (architecture, générateurs par dialecte, chargement de configuration, tests).

## Ce que fait le code

- `ConfigurationLoader` (`ca.griis.gen.configuration`) : point d'entrée unique pour charger la
  configuration de connexion/génération depuis un fichier YAML (via SnakeYAML) et établir la
  connexion à la base source.
- `SqlGenerator` (interface/classe abstraite, `ca.griis.gen.generator`) : contrat commun aux
  générateurs de dialecte — création de schéma, de tables, de types définis, de scripts de
  suppression.
- `MSSqlGenerator` / `PostgreSqlGenerator` : implémentations concrètes de `SqlGenerator`, chacune
  associée à son propre `SqlTemplate` (gabarits SQL spécifiques au dialecte).
- `BuildSchema` (`ca.griis.gen.process`) : introspecte un schéma de base de données existant (via
  [SchemaCrawler](https://www.schemacrawler.com/)) et construit le `SchemaModel` (relvars, clés
  référentielles, types) consommé par les générateurs.
- `SqlExecutor` : exécution optionnelle des scripts générés directement sur la base cible.

Architecture modulaire : séparation claire configuration / modèle / génération / exécution, une
classe abstraite par responsabilité de dialecte plutôt qu'un générateur monolithique.

## Stack technique

- Java (Gradle)
- PostgreSQL, MSSQL (JDBC / SchemaCrawler)
- SnakeYAML (configuration)
- JUnit 5
- Protégé / OWL API en amont, dans le projet OntoRelα (hors périmètre de ce dépôt)

## Comment lancer le projet

```bash
./gradlew build
./gradlew test
```

Exécution en ligne de commande (génère les scripts SQL à partir d'une configuration décrivant la
base source et le dialecte cible) :

```bash
./gradlew run --args="test-data/example/supply/configV.yaml"
```

Les fichiers de configuration d'exemple se trouvent dans `test-data/configuration/` et
`test-data/example/*/config*.yaml`. Ce sont des fixtures de test pointant vers des bases locales
(`localhost`) avec des identifiants génériques — à adapter pour un usage réel.

## Résultats obtenus (contexte historique)

D'après le rapport de stage, à la fin du mandat (juin 2024) :

- Librairie réutilisable et testée (JUnit), avec une couverture de test rapportée d'environ 80 %.
- Génération SQL fonctionnelle pour PostgreSQL et MSSQL à partir de schémas décrits en
  configuration.
- Avancement déclaré : environ 70-80 % des objectifs du mandat.

Ces chiffres sont ceux mesurés à l'époque, sur l'environnement de stage (bases de données réelles
du GRIIS) ; ils ne sont pas reproduits automatiquement dans ce dépôt public et sont rapportés ici
à titre de contexte historique, pas de résultat garanti sur le code tel qu'il est aujourd'hui.

## Limites

- La branche MSSQL a une dépendance (`mssql-jdbc_auth`) commentée dans `build.gradle` suite à un
  problème d'empaquetage rencontré pendant le stage (voir le commentaire `FIXME` dans le fichier) —
  la génération MSSQL n'a donc pas été testée de bout en bout avec authentification intégrée.
- La génération des scripts EMIRA (opérations CRUD générées, `generateCreateEMIRAScripts`) n'est
  implémentée que pour PostgreSQL ; l'implémentation MSSQL est un stub (`TODO`).
- Le document `doc/requirements-draft.adoc` est un gabarit de spécification des exigences resté
  partiellement rempli pendant le stage — conservé tel quel comme trace du travail réalisé, pas
  comme documentation de référence à jour.
- Le projet ne couvre que la génération SQL à partir d'un schéma déjà construit ; la dérivation du
  schéma relationnel à partir de l'ontologie fait partie du projet-cadre OntoRelα plus large et
  n'est pas dans ce dépôt.

## Document complémentaire

Voir [`doc/reformulation-recherche.md`](doc/reformulation-recherche.md) pour la question de
recherche, un état de l'art succinct, la contribution du projet et ses limites, formulés dans une
perspective de recherche.
