DROP TABLE IF EXISTS warden_role_history CASCADE;
DROP TABLE IF EXISTS warden_status_history CASCADE;
DROP TABLE IF EXISTS certifications CASCADE;
DROP TABLE IF EXISTS identifiers CASCADE;
DROP TABLE IF EXISTS wardens CASCADE;
DROP TABLE IF EXISTS role_aliases CASCADE;
DROP TABLE IF EXISTS status_aliases CASCADE;
DROP TABLE IF EXISTS clearance_aliases CASCADE;
DROP TABLE IF EXISTS identifier_type_aliases CASCADE;
DROP TABLE IF EXISTS identifier_types CASCADE;
DROP TABLE IF EXISTS clearance_levels CASCADE;
DROP TABLE IF EXISTS warden_statuses CASCADE;
DROP TABLE IF EXISTS roles CASCADE;

-- lookup tables
CREATE TABLE roles(
                      role_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                      role_name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE warden_statuses(
                                status_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                status_name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE clearance_levels(
                                 clearance_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                 clearance_name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE identifier_types(
                                 id_type_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                 type_name VARCHAR(255) NOT NULL UNIQUE
);

-- alias tables
CREATE TABLE role_aliases(
                             alias_name VARCHAR(255) PRIMARY KEY,
                             role_id INTEGER NOT NULL REFERENCES roles(role_id)
);

CREATE TABLE status_aliases(
                               alias_name VARCHAR(255) PRIMARY KEY,
                               status_id INTEGER NOT NULL REFERENCES warden_statuses(status_id)
);

CREATE TABLE clearance_aliases(
                                  alias_name VARCHAR(255) PRIMARY KEY,
                                  clearance_id INTEGER NOT NULL REFERENCES clearance_levels(clearance_id)
);

CREATE TABLE identifier_type_aliases(
                                        alias_name VARCHAR(255) PRIMARY KEY,
                                        id_type_id INTEGER NOT NULL REFERENCES identifier_types(id_type_id)
);

-- core
CREATE TABLE wardens(
                        warden_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                        first_name VARCHAR(255) NOT NULL,
                        last_name VARCHAR(255),
                        email VARCHAR(255) NOT NULL UNIQUE,
                        role_id INTEGER NOT NULL REFERENCES roles(role_id),
                        clearance_id INTEGER NOT NULL REFERENCES clearance_levels(clearance_id)
);

CREATE TABLE identifiers(
                            identifier_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                            warden_id INTEGER NOT NULL REFERENCES wardens(warden_id) ON DELETE CASCADE,
                            id_type_id INTEGER NOT NULL REFERENCES identifier_types(id_type_id),
                            id_value VARCHAR(255) NOT NULL,
                            UNIQUE(id_type_id, id_value),
                            UNIQUE(warden_id, id_type_id)
);

CREATE TABLE certifications(
                               certification_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                               warden_id INTEGER NOT NULL REFERENCES wardens(warden_id) ON DELETE CASCADE,
                               certification_name VARCHAR(255) NOT NULL,
                               date_earned DATE NOT NULL,
                               expiration_date DATE,
                               UNIQUE(warden_id, certification_name, date_earned)
);

CREATE TABLE warden_status_history(
                                      status_history_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                      warden_id INTEGER NOT NULL REFERENCES wardens(warden_id) ON DELETE CASCADE,
                                      status_id INTEGER NOT NULL REFERENCES warden_statuses(status_id),
                                      start_date DATE NOT NULL,
                                      end_date DATE
);

CREATE TABLE warden_role_history(
                                    role_history_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
                                    warden_id INTEGER NOT NULL REFERENCES wardens(warden_id) ON DELETE CASCADE,
                                    role_id INTEGER NOT NULL REFERENCES roles(role_id),
                                    start_date DATE NOT NULL,
                                    end_date DATE
);

CREATE UNIQUE INDEX uq_one_active_status_per_warden
    ON warden_status_history(warden_id) WHERE end_date IS NULL;

CREATE UNIQUE INDEX uq_one_active_role_per_warden
    ON warden_role_history(warden_id) WHERE end_date IS NULL;

-- normalization + alias resolution
CREATE OR REPLACE FUNCTION normalize_and_resolve()
RETURNS TRIGGER AS $$
DECLARE
resolved_id INTEGER;
BEGIN
    IF TG_TABLE_NAME = 'roles' THEN
        NEW.role_name := LOWER(NEW.role_name);

    ELSIF TG_TABLE_NAME = 'warden_statuses' THEN
        NEW.status_name := LOWER(NEW.status_name);

    ELSIF TG_TABLE_NAME = 'clearance_levels' THEN
        NEW.clearance_name := LOWER(NEW.clearance_name);

    ELSIF TG_TABLE_NAME = 'identifier_types' THEN
        NEW.type_name := LOWER(NEW.type_name);

    ELSIF TG_TABLE_NAME = 'role_aliases' THEN
        NEW.alias_name := LOWER(NEW.alias_name);

    ELSIF TG_TABLE_NAME = 'status_aliases' THEN
        NEW.alias_name := LOWER(NEW.alias_name);

    ELSIF TG_TABLE_NAME = 'clearance_aliases' THEN
        NEW.alias_name := LOWER(NEW.alias_name);

    ELSIF TG_TABLE_NAME = 'identifier_type_aliases' THEN
        NEW.alias_name := LOWER(NEW.alias_name);

    ELSIF TG_TABLE_NAME = 'wardens' THEN
        NEW.first_name := LOWER(NEW.first_name);
        IF NEW.last_name IS NOT NULL THEN
            NEW.last_name := LOWER(NEW.last_name);
END IF;
        NEW.email := LOWER(NEW.email);

    ELSIF TG_TABLE_NAME = 'certifications' THEN
        NEW.certification_name := LOWER(NEW.certification_name);

    ELSIF TG_TABLE_NAME = 'identifiers' THEN
        NEW.id_value := UPPER(NEW.id_value);
END IF;

RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- attach triggers
CREATE TRIGGER trg_roles BEFORE INSERT OR UPDATE ON roles FOR EACH ROW EXECUTE FUNCTION normalize_and_resolve();
CREATE TRIGGER trg_statuses BEFORE INSERT OR UPDATE ON warden_statuses FOR EACH ROW EXECUTE FUNCTION normalize_and_resolve();
CREATE TRIGGER trg_clearance BEFORE INSERT OR UPDATE ON clearance_levels FOR EACH ROW EXECUTE FUNCTION normalize_and_resolve();
CREATE TRIGGER trg_idtypes BEFORE INSERT OR UPDATE ON identifier_types FOR EACH ROW EXECUTE FUNCTION normalize_and_resolve();

CREATE TRIGGER trg_role_alias BEFORE INSERT OR UPDATE ON role_aliases FOR EACH ROW EXECUTE FUNCTION normalize_and_resolve();
CREATE TRIGGER trg_status_alias BEFORE INSERT OR UPDATE ON status_aliases FOR EACH ROW EXECUTE FUNCTION normalize_and_resolve();
CREATE TRIGGER trg_clearance_alias BEFORE INSERT OR UPDATE ON clearance_aliases FOR EACH ROW EXECUTE FUNCTION normalize_and_resolve();
CREATE TRIGGER trg_idtype_alias BEFORE INSERT OR UPDATE ON identifier_type_aliases FOR EACH ROW EXECUTE FUNCTION normalize_and_resolve();

CREATE TRIGGER trg_wardens BEFORE INSERT OR UPDATE ON wardens FOR EACH ROW EXECUTE FUNCTION normalize_and_resolve();
CREATE TRIGGER trg_certs BEFORE INSERT OR UPDATE ON certifications FOR EACH ROW EXECUTE FUNCTION normalize_and_resolve();
CREATE TRIGGER trg_identifiers BEFORE INSERT OR UPDATE ON identifiers FOR EACH ROW EXECUTE FUNCTION normalize_and_resolve();

-- seed data
INSERT INTO roles (role_name) VALUES
                                  ('admin'), ('field'), ('rift'), ('trainer'), ('astral');

INSERT INTO warden_statuses (status_name) VALUES
                                              ('active'), ('on leave'), ('terminated');

INSERT INTO clearance_levels (clearance_name) VALUES
                                                  ('alpha'), ('omega'), ('beta');

INSERT INTO identifier_types (type_name) VALUES
                                             ('badge'), ('passport'), ('visa');

-- optional aliases
INSERT INTO role_aliases (alias_name, role_id) VALUES
                                                   ('administrator',1), ('field agent',2), ('rift agent',3);

INSERT INTO status_aliases (alias_name, status_id) VALUES
                                                       ('working',1), ('leave',2), ('fired',3);

INSERT INTO clearance_aliases (alias_name, clearance_id) VALUES
                                                             ('a',1), ('o',2), ('b',3);

INSERT INTO identifier_type_aliases (alias_name, id_type_id) VALUES
                                                                 ('id',1), ('pass',2);

-- wardens
INSERT INTO wardens (first_name, last_name, email, role_id, clearance_id) VALUES
                                                                              ('Alice','Carter','alice.carter@example.com',1,2),
                                                                              ('Brian','Lopez','brian.lopez@example.com',2,3),
                                                                              ('Chloe','Nguyen','chloe.nguyen@example.com',1,1),
                                                                              ('David','Kim','david.kim@example.com',3,1),
                                                                              ('Emma','Stone','emma.stone@example.com',2,2),
                                                                              ('Frank','Wright','frank.wright@example.com',1,1),
                                                                              ('Grace','Hall','grace.hall@example.com',2,2),
                                                                              ('Henry','Adams','henry.adams@example.com',1,1),
                                                                              ('Isla','Turner','isla.turner@example.com',3,3),
                                                                              ('Jack','Evans','jack.evans@example.com',2,2);

INSERT INTO identifiers (warden_id, id_type_id, id_value) VALUES
                                                              (1,1,'B-1001'),
                                                              (2,1,'B-1002'),
                                                              (3,2,'NID-2003'),
                                                              (4,1,'B-1004'),
                                                              (5,3,'P-3005'),
                                                              (6,2,'NID-2006'),
                                                              (7,1,'B-1007'),
                                                              (8,3,'P-3008'),
                                                              (9,1,'B-1009'),
                                                              (10,2,'NID-2010');

INSERT INTO certifications (warden_id, certification_name, date_earned, expiration_date) VALUES
                                                                                             (1,'first aid','2022-01-10','2025-01-10'),
                                                                                             (1,'fire safety','2021-05-20','2024-05-20'),
                                                                                             (2,'advanced tracking','2023-03-15','2026-03-15'),
                                                                                             (3,'first aid','2020-07-01','2023-07-01'),
                                                                                             (4,'leadership training','2022-09-10','2025-09-10'),
                                                                                             (4,'crisis management','2023-02-01','2026-02-01'),
                                                                                             (5,'fire safety','2021-11-11','2024-11-11'),
                                                                                             (6,'basic training','2020-01-01','2023-01-01'),
                                                                                             (7,'advanced tracking','2023-06-06','2026-06-06'),
                                                                                             (8,'first aid','2019-04-04','2022-04-04'),
                                                                                             (9,'leadership training','2022-08-08','2025-08-08'),
                                                                                             (10,'fire safety','2023-01-01','2026-01-01');

INSERT INTO warden_status_history (warden_id, status_id, start_date, end_date) VALUES
                                                                                   (1,1,'2022-01-01',NULL),
                                                                                   (2,1,'2021-05-01',NULL),
                                                                                   (3,2,'2023-01-01',NULL),
                                                                                   (4,1,'2020-03-01',NULL),
                                                                                   (5,1,'2022-06-01',NULL),
                                                                                   (6,3,'2023-02-01',NULL),
                                                                                   (7,1,'2021-09-01',NULL),
                                                                                   (8,3,'2020-01-01',NULL),
                                                                                   (9,1,'2022-11-01',NULL),
                                                                                   (10,1,'2023-04-01',NULL);

INSERT INTO warden_role_history (warden_id, role_id, start_date, end_date) VALUES
                                                                               (1,1,'2022-01-01',NULL),
                                                                               (2,2,'2021-05-01',NULL),
                                                                               (3,1,'2023-01-01',NULL),
                                                                               (4,3,'2020-03-01',NULL),
                                                                               (5,2,'2022-06-01',NULL),
                                                                               (6,1,'2020-01-01',NULL),
                                                                               (7,2,'2021-09-01',NULL),
                                                                               (8,1,'2020-01-01',NULL),
                                                                               (9,3,'2022-11-01',NULL),
                                                                               (10,2,'2023-04-01',NULL);

UPDATE warden_status_history SET end_date = '2024-02-12' WHERE warden_id = 6 AND status_id = 3 AND end_date IS NULL;
UPDATE warden_status_history SET end_date = '2024-01-01' WHERE warden_id = 3 AND end_date IS NULL;
INSERT INTO warden_status_history (warden_id, status_id, start_date, end_date)
VALUES (3,2,'2024-01-02',NULL);

UPDATE warden_role_history SET end_date = '2023-12-31' WHERE warden_id = 2 AND end_date IS NULL;
INSERT INTO warden_role_history (warden_id, role_id, start_date, end_date)
VALUES (2,1,'2024-01-01',NULL);