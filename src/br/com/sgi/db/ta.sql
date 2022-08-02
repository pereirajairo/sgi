--
-- File generated with SQLiteStudio v3.2.1 on qui mar 19 11:38:14 2020
--
-- Text encoding used: System
--
PRAGMA foreign_keys = off;
BEGIN TRANSACTION;

-- Table: params_banco_app
CREATE TABLE params_banco_app (id INTEGER PRIMARY KEY AUTOINCREMENT, databasetype STRING (50), driverName STRING (50), serverName STRING (50), mydatabase STRING (50), username STRING (50), password STRING (50), url STRING (50));
INSERT INTO params_banco_app (id, databasetype, driverName, serverName, mydatabase, username, password, url) VALUES (1, 'MYSQL', 'com.mysql.jdbc.Driver', 'localhost', 'digitpro', 'digitpro', 'digitpro', 'jdbc:mysql://localhost/digitpro');

COMMIT TRANSACTION;
PRAGMA foreign_keys = on;
