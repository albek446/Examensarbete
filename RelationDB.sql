DROP TABLE IF EXISTS Data2 CASCADE;
DROP TABLE IF EXISTS Data1 CASCADE;
DROP TABLE IF EXISTS Data CASCADE;
DROP TABLE IF EXISTS Patient CASCADE;
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

CREATE TABLE Patient (id INT UNIQUE,
                      name VARCHAR(100),
                      socialSecurityNumber VARCHAR(20),                      
                      sex VARCHAR(1),
                      module VARCHAR(5),
                      CONSTRAINT pk_Patient PRIMARY KEY(id));

CREATE TABLE Data (id INT AUTO_INCREMENT,
                   parameterId INT,
                   patientId INT,
                   date BIGINT,
                   CONSTRAINT pk_Data PRIMARY KEY(id),
                   CONSTRAINT fk_parameterId FOREIGN KEY (parameterId) REFERENCES Parameter(id) ON DELETE CASCADE,
                   CONSTRAINT fk_patientId FOREIGN KEY (patientId) REFERENCES Patient(id) ON DELETE CASCADE);

CREATE TABLE Data1 (dataId INT,                    
                    value VARBINARY(255),
                    CONSTRAINT fk_dataid FOREIGN KEY (dataId) REFERENCES Data(id) ON DELETE CASCADE);

CREATE TABLE Data2 (dataId INT,
                    value FLOAT,
                    CONSTRAINT fk_dataid2 FOREIGN KEY (dataId) REFERENCES Data(id) ON DELETE CASCADE);
