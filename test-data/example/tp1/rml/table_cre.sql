/*
-- =========================================================================== A
-- Création des tables du schéma Ressources matériels et logiciels.
-- -----------------------------------------------------------------------------
Produit  : Saphir
Version  : 0.0.0
Statut   : en développement
-- =========================================================================== A
*/
--
set schema 'rml';
--
create domain idR as char(4);
comment on domain idR is
  $$ Le domaine pour une clé artificielle d'une ressource de 4 caractères $$;
--
create table Ressource
(
  idR idR,
  acquisition date not null,
  constraint ressources_cc00 primary key (idR)
);
comment on table Ressource is
  $$ La ressource matérielle ou logicielle identifiée par "idR" est acquise à la date "acquisition" $$;
comment on column Ressource.acquisition is $$ La date d'acquisition de la ressource $$;
--
create domain code_cle varchar(4)
  check ( value similar to '[A-Z]{2}[0-9]{2}');
comment on domain code_cle is
  $$ Le domaine du code d'une clé de une chaine de caractère 4 caractères
  qui débute par 2 lettres en majuscules suivie par 2 chiffres $$;
create table Cle
(
  idR    idR    not null,
  code   code_cle not null,
  numero char(2)  not null,
  constraint cle_cc00 primary key (idR),
  constraint cle_cc02 unique (code, numero),
  constraint cle_idR_check check (idR  similar to 'C[0-9]{3}'),
  constraint cle_numero_check check (numero  similar to '[0-9]{2}'),
  constraint cle_cr01 foreign key (idR) references Ressource (idR)
);
comment on table Cle is
  $$ La clé identifiée par "idR" a le type du clé "code" et le "numero" de l'exemplaire $$;
--
create domain numero_salle varchar(10);
comment on domain numero_salle is
  $$ Le domaine d'un numéro de salle d'un chaine de caractère d'au plus 10 caractères
  qui débute par une lettre du batiment suivie par un chiffre du batiment, un trait d'union, 4 chiffres,
  un trait d'union et 2 chiffres pour la numéro de la porte.
  $$;
create table Salle
(
  numero          numero_salle not null,
  nbPlaceEffective integer      not null,
  constraint salle_cc00 primary key (numero)
);
comment on table Salle is
  $$ La salle de travail "numero" possède un nombre de place effective "nbPlaceEffectif" $$;
--
create table OuverturePorte
(
  cle_code  code_cle     not null,
  cle_numero char(2)  not null,
  salle numero_salle not null,
  constraint cle_salle_cc00 primary key (cle_code, cle_numero, salle),
  constraint cle_salle_cr01 foreign key (cle_code, cle_numero) references Cle (code, numero),
  constraint cle_salle_cr02 foreign key (salle) references Salle (numero)
);
comment on table OuverturePorte is
  $$ La clé "cle_code" et "cle_numero" ouvre la salle "salle" $$;
--
create domain numero_bureau varchar(6);
comment on domain numero_bureau is
  $$ Le domaine d'un numéro du bureau d'un chaine de caractère d'au plus 6 caractères $$;
create table Bureau
(
  idR    idR           not null,
  numero numero_bureau not null,
  salle  numero_salle  not null,
  constraint bureau_cc00 primary key (idR),
  constraint bureau_cc01 unique (numero, salle),
  constraint bureau_cr01 foreign key (salle) references Salle (numero),
  constraint bureau_cr02 foreign key (idR) references Ressource (idR)
);
comment on table Bureau is
  $$ Le bureau de travail identifié par "idR" et "numero" est localisé dans la salle "salle" $$;
--
create domain code_ordinateur varchar(14);
comment on domain code_ordinateur is
  $$ Le domaine du code interne d'un ordinateur d'un chaine de caractère d'au plus 14 caractères $$;
create table Ordinateur
(
  idR             idR             not null,
  code            code_ordinateur not null,
  portable        boolean         not null,
  modele          varchar(40)     not null,
  marque          varchar(40)     not null,
  processeur      varchar(40)     not null,
  memoire         integer         not null,
  espace_disque   integer         not null,
  carte_graphique varchar(40)     not null,
  constraint ordinateur_cc00 primary key (idR),
  constraint ordinateur_cc01 unique (code),
  constraint ordinateur_cr01 foreign key (idR) references Ressource (idR)
);
comment on table Ordinateur is
  $$ L'ordinateur identifié par "idR" est un portable "portable" et identifié par le code interne "code" et décrit
  par le nom du modèle "modele", le nom de la marque "marque", le processeur "processeur",
  la capacité de la mémoire "memoire" en GB, la capacité du disque "espace_disque" en GB et
  la carte graphique "carte_graphique"
  $$;
--
/*
-- =========================================================================== Z
Contributeurs :
  (CK) Christina.Khnaisser@USherbrooke.ca
  (LL) Luc.Lavoie@USherbrooke.ca

Tâches projetées :

Tâches réalisées :
  2023-07-31 (CK) : Création
-- -----------------------------------------------------------------------------
-- Fin de table_cre.sql
-- =========================================================================== Z
*/