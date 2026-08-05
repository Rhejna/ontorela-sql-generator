/*
-- =========================================================================== A
Exemple : Drug
Date de création : 2020-03-11
Encodage : UTF-8, sans BOM, fin de ligne Unix (LF)
Plateform : PostgreSQL 9.6+
Responsable : Christina.Khnaisser@USherbrooke.ca
Version : 0.1.0a
Statut : stable
Objet : Schéma source pour tester la construction d'un schéma UHF.
-- =========================================================================== A
*/
--
create extension if not exists "btree_gist";
create schema if not exists drug;
--
create table drug.Patient
(
  code   char(2)       not null,
  pName  varchar(30)   not null,
  weight numeric(2, 2) not null,
  constraint patient_c00 primary key (code)
);

create table drug.Prescription
(
  dPat  char(2)     not null,
  dName varchar(30) not null,
  dDose varchar(5)  not null,
  constraint prescription_cc0 primary key (dPat, dName),
  constraint prescription_cr0 foreign key (dPat) references drug.Patient (code)
);

create table drug.Administration
(
  aPat  char(2)     not null,
  aName varchar(30) not null,
  aDose varchar(5)  not null,
  constraint administration_cc0 primary key (aPat, aName),
  constraint administration_cr0 foreign key (aPat) references drug.Patient (code)
);
--
/** ====================================================================================
Author : Christina.Khnaisser@usherbrooke.ca
Creation date : 2022-02-15
RDBMS : PostgreSQL 13
==================================================================================== */
