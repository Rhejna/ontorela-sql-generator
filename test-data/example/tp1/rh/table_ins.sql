/*
-- =========================================================================== A
-- Insertion des données pour les tables.
-- -----------------------------------------------------------------------------
Produit  : Saphir
Version  : 0.0.0
Statut   : en développement
-- =========================================================================== A
*/
--
set schema 'rh';
--
-- Insérer les rôles
insert into rh.role(code, etiquette, definition)
values ('ETU','Étudiant','Une personne inscrite à un programme d''études'),
       ('CHR','Chercheur','Une personne qui se consacre à la recherche'),
       ('EMP','Employé','Une personne qui a un contrat d''emploi'),
       ('AUX','Auxiliaire de recherche','Une personne qui assiste une personne chercheure'),
       ('STA','Stagiaire','Une personne qui effectue un stage');
--
/*
-- =========================================================================== Z
Contributeurs :
  (CK) Christina.Khnaisser@USherbrooke.ca
  (LL) Luc.Lavoie@USherbrooke.ca

Tâches projetées :

Tâches réalisées :
  2023-08-14 (CK) : Création
-- -----------------------------------------------------------------------------
-- Fin de table_ins.sql.sql
-- =========================================================================== Z
*/