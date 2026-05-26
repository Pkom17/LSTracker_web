insert into sample_tracker.sample_type values
(nextval('sample_tracker.sample_type_id_seq'),'BI','Bilan Initial',
now()),
(nextval('sample_tracker.sample_type_id_seq'),'BS','Bilan de Suivi', now()),
(nextval('sample_tracker.sample_type_id_seq'),'CV','Charge Virale',
now()),
(nextval('sample_tracker.sample_type_id_seq'),'EID','EID',
now()),
(nextval('sample_tracker.sample_type_id_seq'),'TB','TB',
now()),
(nextval('sample_tracker.sample_type_id_seq'),'HPV','HPV',
now());