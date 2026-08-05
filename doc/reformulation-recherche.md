# Reformulation recherche — sql-gen / OntoRelα

Ce document reformule le travail réalisé sur `sql-gen` dans une perspective de recherche
appliquée, en complément du README technique. Il a été rédigé après coup pour ce portfolio, sur la
base du code et de la documentation produits pendant le stage (février-juin 2024, GRIIS,
Université de Sherbrooke).

## Question de recherche

Comment générer automatiquement, à partir d'un schéma relationnel dérivé d'une ontologie, du code
SQL correct et portable vers plusieurs dialectes (PostgreSQL, MSSQL), de façon à découpler la
description sémantique/logique d'une base de données de son implémentation physique sur un moteur
donné ?

Cette question s'inscrit dans le projet-cadre OntoRelα, dont l'objectif est de répondre à la
fragmentation des données de santé et de recherche biomédicale au Canada en produisant un modèle
relationnel temporalisé à partir d'une ontologie plutôt que d'un schéma de base de données saisi
manuellement pour chaque projet.

## État de l'art (succinct)

- **Génération de schémas à partir d'ontologies** : la littérature en ingénierie ontologique
  propose plusieurs approches de mapping ontologie → relationnel (ex. approches directes basées
  sur la structure OWL, approches basées sur des règles de normalisation). OntoRelα se distingue en
  visant explicitement un modèle **temporalisé** (historicisation des données), motivé par les
  besoins longitudinaux de la recherche biomédicale.
- **Génération de SQL multidialecte** : des outils génériques existent (ORM tels qu'Hibernate,
  générateurs de migration comme Flyway/Liquibase, ou des couches d'abstraction SQL comme
  jOOQ/SchemaCrawler). Ces outils gèrent en général la portabilité au niveau du langage applicatif,
  mais ne sont pas conçus spécifiquement pour un pipeline ontologie → schéma → SQL dans un contexte
  de standardisation inter-établissements de santé.
- **Constat local ayant motivé ce projet** : au sein des projets existants du GRIIS, la génération
  de SQL par gabarits (templates) avait été dupliquée par copier-coller dans plusieurs bases de
  code, ce qui rendait toute correction coûteuse à propager. Ce constat, documenté dans le brouillon
  de spécification des exigences du projet (`doc/requirements-draft.adoc`), a orienté le travail
  vers une librairie externe plutôt que vers une nouvelle approche de génération inédite.

Ce projet n'a pas eu pour objectif de proposer une contribution théorique nouvelle sur le mapping
ontologie-relationnel (ce volet relève du projet-cadre OntoRelα, hors périmètre de ce dépôt), mais
de fournir la brique d'exécution — la génération SQL effective — nécessaire à la démonstration de
faisabilité de cette chaîne complète.

## Contribution

- Une librairie Java modulaire (`sql-gen`) qui sépare clairement : le chargement de configuration,
  la construction du modèle de schéma (introspection via SchemaCrawler), et la génération de SQL
  par dialecte via une interface commune (`SqlGenerator`) implémentée séparément pour PostgreSQL et
  MSSQL.
- L'élimination de la duplication de code de génération SQL entre plusieurs projets du GRIIS, en
  centralisant la logique de gabarits dans un point unique, versionné et testé.
- Une base testée (JUnit, ~80 % de couverture rapportée) permettant d'envisager l'intégration de
  cette librairie dans le pipeline plus large d'OntoRelα, où elle consomme un schéma relationnel
  déjà dérivé d'une ontologie.

## Limites

- **Validation empirique restreinte** : les tests portent sur des schémas d'exemple (`supply`,
  `drug`, `abstract`, `tp1`) plutôt que sur un corpus représentatif de schémas hospitaliers réels à
  grande échelle ; la généralisabilité à des ontologies de santé complexes n'a pas été démontrée
  formellement dans ce mandat.
- **Couverture dialectale partielle** : le support MSSQL est plus limité que le support PostgreSQL
  (génération EMIRA non implémentée pour MSSQL, dépendance JDBC MSSQL non fonctionnelle telle que
  configurée — voir README).
- **Portée du mandat** : ce projet traite la génération SQL en aval d'un schéma relationnel déjà
  construit ; il ne répond pas, à lui seul, à la question de recherche plus large d'OntoRelα sur la
  dérivation automatique et correcte du schéma relationnel temporalisé à partir de l'ontologie —
  cette partie du problème est traitée ailleurs dans le projet-cadre.
- **Avancement déclaré** : selon le rapport de stage, environ 70-80 % des objectifs initiaux du
  mandat ont été atteints ; certaines opérations (EMIRA MSSQL, authentification intégrée MSSQL)
  restent des travaux en cours plutôt que des fonctionnalités complètes.
