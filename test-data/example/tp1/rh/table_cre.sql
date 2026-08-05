/*
-- =========================================================================== A
-- Création des tables du schéma Ressource humaines.
-- -----------------------------------------------------------------------------
Produit  : Saphir
Version  : 0.0.0
Statut   : en développement
-- =========================================================================== A
*/
--
set schema 'rh';
--
create domain idP as int2;
comment on domain idP is
  $$ Le domaine pour une clé artificielle pour une personne $$;

create domain nom as varchar(60);
comment on domain nom is $$ Le domaine pour le nom, prénom $$;

create type personne_statut as enum ('interne', 'externe');
comment on type personne_statut is
  $$ Le type du statut d'une personne au sein du groupe. \n
  Une personne interne appartient à la même organisation du groupe de recherche. \n
  Une personne externe n'appartient pas à la même organisation du groupe de recherche. $$;
--
create table Personne
(
  idP            idP             not null,
  nom            nom             not null,
  prenom         nom             not null,
  date_naissance date            not null,
  statut         personne_statut not null,
  arrive         date            not null,
  depart         date            not null,
  constraint personne_cc00 primary key (idP)
);
comment on table Personne is
  $$ La personne identifiée par "idP" est décrite par son nom "nom", prénom "prenom,
     date de naissance "date_naissance", le type de statut au sein du groupe "statut",
     la date d'arrivée "arrive" et la date de départ "depart"
  $$;
comment on column Personne.nom is $$ Le nom de la personne $$;
comment on column Personne.prenom is $$ Le prénom de la personne $$;
comment on column Personne.date_naissance is $$ La date de naissance de la personne $$;
comment on column Personne.statut is $$ Le statut de la personne $$;
comment on column Personne.arrive is $$ La date d'arrivée de la personne $$;
comment on column Personne.depart is $$ La date de départ de la personne $$;
--
create domain courriel as varchar(60);
comment on domain courriel is $$ Le domaine pour un courriel $$;

create table Personne_courriel
(
  personne idP      not null,
  courriel courriel not null,
  primaire boolean  not null,
  constraint personne_courriel_cc00 primary key (personne, courriel),
  constraint personne_courriel_cc02 unique (courriel),
  constraint personne_courriel_cr01 foreign key (personne) references Personne (idP)
);
comment on table Personne_courriel is
  $$ Le courriel "courriel" de la personne "idP" est "primaire" $$;
comment on column Personne_courriel.courriel is $$ Le courriel de la personne $$;
comment on column Personne_courriel.primaire is
  $$ Le courriel est primaire lorsqu'il est utilisé comme moyen principal de contact $$;
--
create domain cip as char(8)
  check (value similar to '[a-z]{4}[0-9]{4}');
comment on domain cip is
  $$ Le code d'identification personnel est formé des deux premières lettres du nom
  suivi de deux premières lettres du prénom et 4 chiffres aléatoires $$;
create table Personne_cip
(
  personne idP not null,
  cip      cip not null,
  constraint personne_cip_cc00 primary key (personne),
  constraint personne_cip_cc01 unique (cip),
  constraint personne_cip_cr01 foreign key (personne) references Personne (idP)
);
comment on table Personne_cip is
  $$ La personne "personne" possède un code d'identification personnel "cip" $$;
comment on column Personne_cip.cip is $$ Le code d'identification personnel $$;
--
create domain matricule as varchar(8)
  check (value similar to '[0-9]{6,8}');
comment on domain matricule is
  $$ un matricule est formé de 8 chiffres $$;
create type matricule_type as enum ('etudiant', 'employe');
comment on type matricule_type is
  $$ La personne peut être identifiée par un matricule étudiant ou/et un matricule employé  $$;
create table Personne_matricule
(
  personne  idP            not null,
  matricule matricule      not null,
  type      matricule_type not null,
  constraint personne_matricule_cc00 primary key (matricule),
  constraint personne_matricule_cc01 unique (personne, type),
  constraint personne_matricule_cr01 foreign key (personne) references Personne (idP)
);
comment on table Personne_matricule is
  $$ Le matricule "matricule" de la personne "personne" est de type "type" $$;
--
create domain code_projet varchar(6);
comment on domain code_projet is $$ Le domaine pour un code de projet de 6 caractères $$;
create table Projet
(
  code        code_projet not null,
  nom         nom         not null,
  description text        not null,
  debut       date        not null,
  fin         date        not null,
  constraint projet_cc00 primary key (code),
  constraint projet_cc01 unique (nom)
);
comment on table Projet is
  $$ Le projet identifié par un code "code" est connu sous le nom "nom" et est décrit par "description"
  de "debut" à "fin" $$;
comment on column Projet.code is $$ Le code du projet $$;
comment on column Projet.nom is $$ Le nom du projet $$;
comment on column Projet.description is $$ La description du projet $$;
comment on column Projet.debut is $$ La date de début du projet $$;
comment on column Projet.fin is $$ La date de fin présumée $$;
--
create domain code_role as char(3);
comment on domain code_role is $$ Le domaine pour un code de role $$;
--
create table Role
(
  code       code_role   not null,
  etiquette  varchar(30) not null,
  definition text        not null,
  constraint d_role_cc00 primary key (code),
  constraint d_role_cc01 unique (etiquette)
);
comment on table Role is
  $$ Le rôle identifiée par "code" et une étiquette "etiquette" est définis par "definition" $$;
comment on column Role.code is $$ Le code d'un rôle $$;
comment on column Role.etiquette is $$ L'étiquette d'un rôle $$;
--
create table Participation
(
  personne idP         not null,
  projet   code_projet not null,
  role     code_role   not null,
  debut    date        not null,
  fin      date        not null,
  constraint participation_cc00 primary key (personne, projet, role),
  constraint participation_cr01 foreign key (personne) references Personne (idP),
  constraint participation_cr02 foreign key (projet) references Projet (code),
  constraint participation_cr03 foreign key (role) references Role (code)
);
comment on table Participation is
  $$ La personne "personne" participate au projet "projet" avec le role "role" de "debut" à "fin" $$;
comment on column Participation.debut is $$ La date de début d'une participation $$;
comment on column Participation.fin is $$ La date de fin présumée d'une participation $$;
--
create table Attribution
(
  ressource rml.idR not null,
  personne  idP     not null,
  debut     date    not null,
  fin       date    not null,
  constraint attribution_cc00 primary key (ressource, personne, debut),
  constraint attribution_cc01 unique (ressource),
  constraint attribution_cr01 foreign key (ressource) references rml.Ressource (idR),
  constraint attribution_cr02 foreign key (personne) references Personne (idP),
  constraint attribution_debut exclude using gist (daterange(debut, fin) with &&)
);
comment on table Attribution is
  $$ La personne "personne" est attribué une ressource "ressource" durant de "debut" à "fin" $$;
comment on column Attribution.debut is $$ La date de début de l'attribution $$;
comment on column Attribution.fin is $$ La date de fin de l'attribution $$;
--
/*
-- =========================================================================== Z
Contributeurs :
  (CK) Christina.Khnaisser@USherbrooke.ca
  (LL) Luc.Lavoie@USherbrooke.ca

Tâches projetées :

Tâches réalisées :
  2023-07-23 (CK) : Création
-- -----------------------------------------------------------------------------
-- Fin de table_cre.sql
-- =========================================================================== Z
*/