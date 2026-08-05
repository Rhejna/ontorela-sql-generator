/*
-- =========================================================================== A
Exemple : Supplier-Part
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
create schema supply;
--
create table supply.s
(
  sno    CHAR(3) not null,
  status INT     not null,
  constraint s_ck0 primary key (sno)
);

create table supply.sp
(
  sno CHAR(3) not null,
  pno CHAR(3) not null,
  constraint sp_ck0 primary key (sno, pno),
  constraint sp_fk0 foreign key (sno) references supply.s (sno)
);
--
/** ====================================================================================
Author : Christina.Khnaisser@usherbrooke.ca
Creation date : 2022-02-15
RDBMS : PostgreSQL 13
==================================================================================== */