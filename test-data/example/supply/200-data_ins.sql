/** ==================================================
Create a valid dataset
====================================================== */
set schema 'supply@V';

-- =========================== SINCE
insert into "supply@V".s_since (sno, since)
values  ('S1 ', 4),
        ('S2 ', 7),
        ('S3 ', 3),
        ('S4 ', 4),
        ('S5 ', 2),
        ('S9 ', 5),
        ('S10', 10),
        ('S11', 11),
        ('S13', 13);

insert into "supply@V".s_status_since (sno, status, since)
values  ('S1 ', 20, 6),
        ('S3 ', 30, 3),
        ('S4 ', 20, 8),
        ('S5 ', 30, 2),
        ('S10', 10, 11),
        ('S11', 11, 11),
        ('S2 ', 5, 7),
        ('S13', 13, 13),
        ('S9 ', 10, 5);

insert into "supply@V".sp_since (sno, pno, since)
values  ('S1 ', 'P1 ', 4),
        ('S1 ', 'P2 ', 5),
        ('S1 ', 'P3 ', 9),
        ('S1 ', 'P4 ', 5),
        ('S1 ', 'P5 ', 4),
        ('S1 ', 'P6 ', 6),
        ('S2 ', 'P1 ', 8),
        ('S2 ', 'P2 ', 9),
        ('S3 ', 'P2 ', 8),
        ('S4 ', 'P5 ', 5);

-- =========================== DURING
insert into "supply@V".s_during (sno, during)
values  ('S2 ', '[2,5)'),
        ('S8 ', '[5,8)'),
        ('S10', '[6,8)'),
        ('S12', '[3,7)'),
        ('S6 ', '[3,5)'),
        ('S6 ', '[6,9)');

insert into "supply@V".s_status_during (sno, status, during)
values  ('S6 ', 5, '[3,5)'),
        ('S10', 20, '[6,8)'),
        ('S12', 1, '[3,7)'),
        ('S6 ', 5, '[6,9)'),
        ('S2 ', 5, '[2,5)'),
        ('S1 ', 15, '[4,6)'),
        ('S4 ', 10, '[4,5)'),
        ('S4 ', 25, '[5,8)'),
        ('S8 ', 30, '[5,8)'),
        ('S10', 30, '[10,11)'),
        ('S10', 12, '[12,15)');

insert into "supply@V".sp_during (sno, pno, during)
values  ('S2 ', 'P1 ', '[2,5)'),
        ('S2 ', 'P2 ', '[3,4)'),
        ('S3 ', 'P5 ', '[5,8)'),
        ('S4 ', 'P2 ', '[6,10)'),
        ('S4 ', 'P4 ', '[4,9)'),
        ('S6 ', 'P3 ', '[3,4)'),
        ('S6 ', 'P3 ', '[5,6)');

-- =========================== UNTIL
insert into "supply@V".s_until (sno, until)
values  ('S7 ', 8),
        ('S8 ', 2),
        ('S9 ', 3),
        ('S10', 4),
        ('S11', 5);

insert into "supply@V".s_status_until (sno, status, until)
values  ('S7 ', 7, 8),
        ('S8 ', 8, 2),
        ('S11', 11, 5),
        ('S10', 20, 4),
        ('S9 ', 10, 3);

insert into "supply@V".sp_until (sno, pno, until)
values  ('S7 ', 'P1 ', 8),
        ('S8 ', 'P1 ', 2),
        ('S9 ', 'P1 ', 3);
		
/** ==================================================
Author : Remi.Letourneau@usherbrooke.ca
Last modified : 2022-04-05
Creation date : 2022-04-05
RDBMS : PostgreSQL 14.1
====================================================== */
