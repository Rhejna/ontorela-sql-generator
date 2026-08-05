/*
-- =========================================================================== A
Exemple : Abstract
Date de création : 2020-03-11
Encodage : UTF-8, sans BOM, fin de ligne Unix (LF)
Plateform : PostgreSQL 9.6+
Responsable : Christina.Khnaisser@USherbrooke.ca
Version : 0.1.0a
Statut : stasble
Objet : Schéma source pour tester la construction d'un schéma UHF.
-- =========================================================================== A
*/
--
create schema if not exists abstract;
-- Create tables
create table abstract.R0
(
  k0 int         not null,
  a1 char(2)     not null,
  a2 varchar(10) not null,
  constraint r0_cc0 primary key (k0)
);

create table abstract.R1
(
  k0 INT     not null,
  k1 INT     not null,
  a1 CHAR(2) not null,
  constraint r1_cc0 primary key (k0, k1)
);

create table abstract.R01
(
  r0_k0 INT not null,
  r1_k0 INT not null,
  r1_k1 INT not null,
  constraint r01_cc0 primary key (r0_k0, r1_k0, r1_k1),
  constraint r01_fk0 foreign key (r0_k0) references abstract.R0 (k0),
  constraint r01_fk1 foreign key (r1_k0, r1_k1) references abstract.R1 (k0, k1)
);
--
/** ====================================================================================
Author : Christina.Khnaisser@usherbrooke.ca
Creation date : 2022-02-15
RDBMS : PostgreSQL 13
==================================================================================== */