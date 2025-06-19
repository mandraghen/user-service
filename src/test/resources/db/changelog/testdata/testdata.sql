INSERT INTO address (created, id, updated, version, city, country, postal_code, province, street)
    VALUES ('2025-05-20 11:04:19.622927+00', 1, '2025-05-20 11:04:19.622953+00', 0, 'Casorezzo', 'Italia', '20003', 'MI', 'via Piave');
INSERT INTO customer (address_id, created, id, updated, version, email, first_name, last_name, phone_number, privacy)
    VALUES (1, '2025-05-20 11:04:19.655633+00', 1, '2025-05-20 11:04:19.67133+00', 1, 'salvo@salvo.it', 'Salvo', 'Morabito', '321321231', true);

INSERT INTO address (created, id, updated, version, city, country, postal_code, province, street)
    VALUES ('2025-05-20 11:04:19.622927+00', 2, '2025-05-20 11:04:19.622953+00', 0, 'Milano', 'Italia', '20100', 'MI', 'via Po');

SELECT setval('address_id_seq', 3, true);
SELECT setval('customer_id_seq', 2, true);
