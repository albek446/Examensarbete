DROP TABLE IF EXISTS DataTime CASCADE;
DROP TABLE IF EXISTS Data CASCADE;
DROP TABLE IF EXISTS Patient CASCADE;
DROP TABLE IF EXISTS Parameter CASCADE;

CREATE TABLE Parameter (id INT,
                        attribute VARCHAR(30),
                        value VARCHAR(100));                   

CREATE TABLE Patient (id INT,
                      attribute VARCHAR(30),
                      value VARCHAR(100));

CREATE TABLE Data (id INT,
                   attribute VARCHAR(30),
                   value VARCHAR(100));

CREATE TABLE DataTime (dataId INT UNIQUE,
                       date BIGINT,
                       CONSTRAINT pk_DataTime PRIMARY KEY(dataId));