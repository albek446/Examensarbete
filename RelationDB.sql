DROP TABLE IF EXISTS Data2 CASCADE;
DROP TABLE IF EXISTS Data1 CASCADE;
DROP TABLE IF EXISTS Data CASCADE;
DROP TABLE IF EXISTS Patient CASCADE;
DROP TABLE IF EXISTS Bed CASCADE;
DROP TABLE IF EXISTS Module CASCADE;
DROP TABLE IF EXISTS Parameter CASCADE;

CREATE TABLE Parameter (id INT AUTO_INCREMENT,
                        name VARCHAR(50),
                        category VARCHAR(50),
                        unit VARCHAR(50),
                        round VARCHAR(50),
                        min FLOAT,
                        max FLOAT,
                        high FLOAT,
                        low FLOAT,
                        CONSTRAINT pk_Parameter PRIMARY KEY(id));


CREATE TABLE Module (id INT AUTO_INCREMENT,
                     name VARCHAR(10),
                     CONSTRAINT pk_Module PRIMARY KEY(id));

CREATE TABLE Bed (id INT AUTO_INCREMENT,
                  module INT,
                  name VARCHAR(10),
                  CONSTRAINT pk_Bed PRIMARY KEY(id),
                  CONSTRAINT fk_module FOREIGN KEY (module) REFERENCES Module(id) ON DELETE CASCADE);

CREATE TABLE Patient (id INT UNIQUE,
                      bed INT,
                      name VARCHAR(100),
                      socialSecurityNumber VARCHAR(20),                     
                      sex VARCHAR(1),                      
                      CONSTRAINT pk_Patient PRIMARY KEY(id),
                      CONSTRAINT fk_patientBed FOREIGN KEY (bed) REFERENCES Bed(id) ON DELETE CASCADE);

CREATE TABLE Data (id INT AUTO_INCREMENT,
                   parameterId INT,
                   bed INT,
                   date BIGINT,
                   CONSTRAINT pk_Data PRIMARY KEY(id),
                   CONSTRAINT fk_parameterId FOREIGN KEY (parameterId) REFERENCES Parameter(id) ON DELETE CASCADE,
                   CONSTRAINT fk_dataBed FOREIGN KEY (bed) REFERENCES Bed(id) ON DELETE CASCADE);

CREATE TABLE Data1 (dataId INT,                    
                    value FLOAT,
                    CONSTRAINT fk_dataid FOREIGN KEY (dataId) REFERENCES Data(id) ON DELETE CASCADE);

CREATE TABLE Data2 (dataId INT,
                    value VARBINARY(255),                    
                    CONSTRAINT fk_dataid2 FOREIGN KEY (dataId) REFERENCES Data(id) ON DELETE CASCADE);
