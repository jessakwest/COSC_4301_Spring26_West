-- drop selection
    -- ensures a clean rebuild by removing existing tables, dependencies, functions
    -- prevents conflicts and makes script repeatable
DROP TABLE IF EXISTS warden_clearance_history CASCADE;
DROP TABLE IF EXISTS warden_role_history CASCADE;
DROP TABLE IF EXISTS warden_status_history CASCADE;
DROP TABLE IF EXISTS certifications CASCADE;
DROP TABLE IF EXISTS identifiers CASCADE;
DROP TABLE IF EXISTS wardens CASCADE;

DROP TABLE IF EXISTS role_aliases CASCADE;
DROP TABLE IF EXISTS status_aliases CASCADE;
DROP TABLE IF EXISTS clearance_aliases CASCADE;
DROP TABLE IF EXISTS identifier_type_aliases CASCADE;

DROP TABLE IF EXISTS roles CASCADE;
DROP TABLE IF EXISTS statuses CASCADE;
DROP TABLE IF EXISTS clearance_levels CASCADE;
DROP TABLE IF EXISTS identifier_types CASCADE;

DROP FUNCTION IF EXISTS resolve_lookup(TEXT, TEXT);
DROP FUNCTION IF EXISTS create_warden(TEXT, TEXT, TEXT, TEXT, TEXT, TEXT, TEXT, TEXT, DATE);
DROP FUNCTION IF EXISTS deactivate_warden(INT, DATE);
DROP FUNCTION IF EXISTS terminate_warden(INT, DATE);
DROP FUNCTION IF EXISTS update_status(INT, TEXT, DATE);
DROP FUNCTION IF EXISTS update_role(INT, TEXT, DATE);
DROP FUNCTION IF EXISTS update_clearance(INT, TEXT, DATE);
DROP FUNCTION IF EXISTS view_all_wardens();
DROP FUNCTION IF EXISTS view_warden_by_id(INT);
DROP FUNCTION IF EXISTS view_wardens_by_role(TEXT);
DROP FUNCTION IF EXISTS view_wardens_by_status(TEXT);

-- lookup tables
    -- enforces consistency across the system using foreign keys
    -- allows for controlled expansion without breaking existing data
CREATE TABLE roles (
    role_id SERIAL PRIMARY KEY,
    role_name TEXT NOT NULL UNIQUE
);

CREATE TABLE statuses (
    status_id SERIAL PRIMARY KEY,
    status_name TEXT NOT NULL UNIQUE
);

CREATE TABLE clearance_levels (
    clearance_id SERIAL PRIMARY KEY,
    clearance_name TEXT NOT NULL UNIQUE
);

CREATE TABLE identifier_types (
    id_type_id SERIAL PRIMARY KEY,
    type_name TEXT NOT NULL UNIQUE
);

-- alias tables
    -- map free-text input to values in the lookup tables
    -- supports flexibility while preventing  duplicates, inconsistencies
CREATE TABLE role_aliases (
    alias_name TEXT PRIMARY KEY,
    role_id INT NOT NULL REFERENCES roles(role_id)
);

CREATE TABLE status_aliases (
    alias_name TEXT PRIMARY KEY,
    status_id INT NOT NULL REFERENCES statuses(status_id)
);

CREATE TABLE clearance_aliases (
    alias_name TEXT PRIMARY KEY,
    clearance_id INT NOT NULL REFERENCES clearance_levels(clearance_id)
);

CREATE TABLE identifier_type_aliases (
    alias_name TEXT PRIMARY KEY,
    id_type_id INT NOT NULL REFERENCES identifier_types(id_type_id)
);

-- core table
    -- stores primary identity and contact information
    -- keeps entity minimal, stable by separating static fields from changing attributes
CREATE TABLE wardens (
    warden_id SERIAL PRIMARY KEY,
    first_name TEXT NOT NULL,
    last_name TEXT,
    email TEXT NOT NULL UNIQUE
);

-- identifiers table
    -- enforces global uniqueness for identifier values
    -- supports multiple identifier systems
    -- supports 1 warden to have one per type ID
CREATE TABLE identifiers (
    identifier_id SERIAL PRIMARY KEY,
    warden_id INT NOT NULL REFERENCES wardens(warden_id) ON DELETE CASCADE,
    id_type_id INT NOT NULL REFERENCES identifier_types(id_type_id),
    id_value TEXT NOT NULL,
    UNIQUE(id_type_id, id_value),
    UNIQUE(warden_id, id_type_id)
);

-- certifications table
    -- supports multiple certifications per warden
    -- tracks qualifications earned, dates, expiration
CREATE TABLE certifications (
    certification_id SERIAL PRIMARY KEY,
    warden_id INT NOT NULL REFERENCES wardens(warden_id) ON DELETE CASCADE,
    certification_name TEXT NOT NULL,
    date_earned DATE NOT NULL,
    expiration_date DATE,
    is_expired BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE(warden_id, certification_name, date_earned)
);

-- history tables
    -- supports time-based queries and auditing
    -- records changing data, instead of overwriting it
CREATE TABLE warden_status_history (
    id SERIAL PRIMARY KEY,
    warden_id INT NOT NULL REFERENCES wardens(warden_id) ON DELETE CASCADE,
    status_id INT NOT NULL REFERENCES statuses(status_id),
    start_date DATE NOT NULL,
    end_date DATE,
    CHECK (end_date IS NULL OR end_date >= start_date)
);

CREATE TABLE warden_role_history (
    id SERIAL PRIMARY KEY,
    warden_id INT NOT NULL REFERENCES wardens(warden_id) ON DELETE CASCADE,
    role_id INT NOT NULL REFERENCES roles(role_id),
    start_date DATE NOT NULL,
    end_date DATE,
    CHECK (end_date IS NULL OR end_date >= start_date)
);

CREATE TABLE warden_clearance_history (
    id SERIAL PRIMARY KEY,
    warden_id INT NOT NULL REFERENCES wardens(warden_id) ON DELETE CASCADE,
    clearance_id INT NOT NULL REFERENCES clearance_levels(clearance_id),
    start_date DATE NOT NULL,
    end_date DATE,
    CHECK (end_date IS NULL OR end_date >= start_date)
);

-- indexes: active record enforcement
    -- ensures only one current active record per warden_id
    -- prevents conflicting or overlapping states
CREATE UNIQUE INDEX one_active_status
ON warden_status_history(warden_id)
WHERE end_date IS NULL;

CREATE UNIQUE INDEX one_active_role
ON warden_role_history(warden_id)
WHERE end_date IS NULL;

CREATE UNIQUE INDEX one_active_clearance
ON warden_clearance_history(warden_id)
WHERE end_date IS NULL;

-- normalization / resolve function
    -- prevents invalid, inconsistent data from being inserted
    -- uses alias tables to standardize input
    -- converts free-text input into valid IDs
CREATE OR REPLACE FUNCTION resolve_lookup(input TEXT, type TEXT)
RETURNS INT AS $$
DECLARE id INT;
BEGIN
    input := LOWER(TRIM(input));

    IF type = 'role' THEN
        SELECT role_id INTO id FROM roles WHERE role_name = input;
        IF id IS NULL THEN
            SELECT role_id INTO id FROM role_aliases WHERE alias_name = input;
        END IF;

    ELSIF type = 'status' THEN
        SELECT status_id INTO id FROM statuses WHERE status_name = input;
        IF id IS NULL THEN
            SELECT status_id INTO id FROM status_aliases WHERE alias_name = input;
        END IF;

    ELSIF type = 'clearance' THEN
        SELECT clearance_id INTO id FROM clearance_levels WHERE clearance_name = input;
        IF id IS NULL THEN
            SELECT clearance_id INTO id FROM clearance_aliases WHERE alias_name = input;
        END IF;

    ELSIF type = 'id_type' THEN
        SELECT id_type_id INTO id FROM identifier_types WHERE type_name = input;
        IF id IS NULL THEN
            SELECT id_type_id INTO id FROM identifier_type_aliases WHERE alias_name = input;
        END IF;
    END IF;

    IF id IS NULL THEN
        RAISE EXCEPTION 'Invalid %: %', type, input;
    END IF;

    RETURN id;
END;
$$ LANGUAGE plpgsql;

-- create warden function
   -- centralizes logic for onboarding a new warden
   -- ensures required data is created together
   -- enforces normalization, standardization, data integrity
CREATE OR REPLACE FUNCTION create_warden(
    p_first TEXT,
    p_last TEXT,
    p_email TEXT,
    p_role TEXT,
    p_status TEXT,
    p_clearance TEXT,
    p_id_type TEXT,
    p_id_value TEXT,
    p_start DATE
) RETURNS INT AS $$
DECLARE wid INT;
DECLARE role_id INT;
DECLARE status_id INT;
DECLARE clearance_id INT;
DECLARE id_type_id INT;
BEGIN
    INSERT INTO wardens(first_name, last_name, email)
    VALUES (LOWER(TRIM(p_first)), LOWER(TRIM(p_last)), LOWER(TRIM(p_email)))
    RETURNING warden_id INTO wid;

    role_id := resolve_lookup(p_role, 'role');
    status_id := resolve_lookup(p_status, 'status');
    clearance_id := resolve_lookup(p_clearance, 'clearance');
    id_type_id := resolve_lookup(p_id_type, 'id_type');

    INSERT INTO identifiers VALUES (DEFAULT, wid, id_type_id, UPPER(TRIM(p_id_value)));

    INSERT INTO warden_role_history VALUES (DEFAULT, wid, role_id, p_start, NULL);
    INSERT INTO warden_status_history VALUES (DEFAULT, wid, status_id, p_start, NULL);
    INSERT INTO warden_clearance_history VALUES (DEFAULT, wid, clearance_id, p_start, NULL);

    RETURN wid;
END;
$$ LANGUAGE plpgsql;

-- deactivate warden function
   -- centralizes logic and enforces consistency
   -- updates status, keeps record/history
CREATE OR REPLACE FUNCTION deactivate_warden(
    p_warden INT,
    p_date DATE
) RETURNS VOID AS $$
DECLARE sid INT;
BEGIN
    sid := resolve_lookup('onleave', 'status');

    UPDATE warden_status_history
    SET end_date = p_date
    WHERE warden_id = p_warden
      AND end_date IS NULL;

    INSERT INTO warden_status_history
    VALUES (DEFAULT, p_warden, sid, p_date, NULL);
END;
$$ LANGUAGE plpgsql;

-- terminate warden function
   -- centralizes logic, enforces consistent termination behavior
   -- updates status, keeps record/history
CREATE OR REPLACE FUNCTION terminate_warden(
    p_warden INT,
    p_date DATE
) RETURNS VOID AS $$
DECLARE sid INT;
BEGIN
    sid := resolve_lookup('terminated', 'status');

    UPDATE warden_status_history
    SET end_date = p_date
    WHERE warden_id = p_warden
      AND end_date IS NULL;

    INSERT INTO warden_status_history
    VALUES (DEFAULT, p_warden, sid, p_date, NULL);
END;
$$ LANGUAGE plpgsql;

-- certification expired function
   -- supports lifecycle tracking of qualifications
   -- keeps historical data by marking as expired without deleting them
CREATE OR REPLACE FUNCTION mark_cert_expired(p_id INT)
RETURNS VOID AS $$
BEGIN
    UPDATE certifications SET is_expired = TRUE WHERE certification_id = p_id;
END;
$$ LANGUAGE plpgsql;

-- functions to update fields
    -- controls the updates to keep data consistency across all tables
CREATE OR REPLACE FUNCTION update_status(
    p_warden INT,
    p_status TEXT,
    p_date DATE
) RETURNS VOID AS $$
DECLARE sid INT;
BEGIN
    sid := resolve_lookup(p_status, 'status');

    UPDATE warden_status_history
    SET end_date = p_date
    WHERE warden_id = p_warden
      AND end_date IS NULL;

    INSERT INTO warden_status_history
    VALUES (DEFAULT, p_warden, sid, p_date, NULL);
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION update_role(
    p_warden INT,
    p_role TEXT,
    p_date DATE
) RETURNS VOID AS $$
DECLARE rid INT;
BEGIN
    rid := resolve_lookup(p_role, 'role');

    UPDATE warden_role_history
    SET end_date = p_date
    WHERE warden_id = p_warden AND end_date IS NULL;

    INSERT INTO warden_role_history
    VALUES (DEFAULT, p_warden, rid, p_date, NULL);
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION update_clearance(
    p_warden INT,
    p_clearance TEXT,
    p_date DATE
) RETURNS VOID AS $$
DECLARE cid INT;
BEGIN
    cid := resolve_lookup(p_clearance, 'clearance');

    UPDATE warden_clearance_history
    SET end_date = p_date
    WHERE warden_id = p_warden
      AND end_date IS NULL;

    INSERT INTO warden_clearance_history
    VALUES (DEFAULT, p_warden, cid, p_date, NULL);
END;
$$ LANGUAGE plpgsql;

-- functions to view
   -- enforces consistency by preventing direct table access
   -- aligns with menu actions
CREATE OR REPLACE FUNCTION view_all_wardens()
RETURNS TABLE (
    warden_id INT,
    first_name TEXT,
    last_name TEXT,
    email TEXT,
    role TEXT,
    status TEXT,
    clearance TEXT
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        w.warden_id,
        w.first_name,
        w.last_name,
        w.email,
        r.role_name,
        s.status_name,
        c.clearance_name
    FROM wardens w
    LEFT JOIN warden_role_history wrh
        ON w.warden_id = wrh.warden_id AND wrh.end_date IS NULL
    LEFT JOIN roles r ON wrh.role_id = r.role_id
    LEFT JOIN warden_status_history wsh
        ON w.warden_id = wsh.warden_id AND wsh.end_date IS NULL
    LEFT JOIN statuses s ON wsh.status_id = s.status_id
    LEFT JOIN warden_clearance_history wch
        ON w.warden_id = wch.warden_id AND wch.end_date IS NULL
    LEFT JOIN clearance_levels c ON wch.clearance_id = c.clearance_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION view_warden_by_id(p_id INT)
RETURNS TABLE (
    warden_id INT,
    first_name TEXT,
    last_name TEXT,
    email TEXT,
    role TEXT,
    status TEXT,
    clearance TEXT,
    certification_name TEXT,
    date_earned DATE,
    expiration_date DATE,
    is_expired BOOLEAN
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        w.warden_id,
        w.first_name,
        w.last_name,
        w.email,
        r.role_name,
        s.status_name,
        c.clearance_name,
        cert.certification_name,
        cert.date_earned,
        cert.expiration_date,
        cert.is_expired
    FROM wardens w
    LEFT JOIN warden_role_history wrh
        ON w.warden_id = wrh.warden_id AND wrh.end_date IS NULL
    LEFT JOIN roles r ON wrh.role_id = r.role_id
    LEFT JOIN warden_status_history wsh
        ON w.warden_id = wsh.warden_id AND wsh.end_date IS NULL
    LEFT JOIN statuses s ON wsh.status_id = s.status_id
    LEFT JOIN warden_clearance_history wch
        ON w.warden_id = wch.warden_id AND wch.end_date IS NULL
    LEFT JOIN clearance_levels c ON wch.clearance_id = c.clearance_id
    LEFT JOIN certifications cert
        ON w.warden_id = cert.warden_id
    WHERE w.warden_id = p_id;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION view_wardens_by_role(p_role TEXT)
RETURNS TABLE (
    warden_id INT,
    first_name TEXT,
    last_name TEXT,
    email TEXT,
    role TEXT
) AS $$
DECLARE rid INT;
BEGIN
    rid := resolve_lookup(p_role, 'role');

    RETURN QUERY
    SELECT
        w.warden_id,
        w.first_name,
        w.last_name,
        w.email,
        r.role_name
    FROM wardens w
    JOIN warden_role_history wrh
        ON w.warden_id = wrh.warden_id AND wrh.end_date IS NULL
    JOIN roles r ON wrh.role_id = r.role_id
    WHERE wrh.role_id = rid;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION view_wardens_by_status(p_status TEXT)
RETURNS TABLE (
    warden_id INT,
    first_name TEXT,
    last_name TEXT,
    email TEXT,
    status TEXT
) AS $$
DECLARE sid INT;
BEGIN
    sid := resolve_lookup(p_status, 'status');

    RETURN QUERY
    SELECT
        w.warden_id,
        w.first_name,
        w.last_name,
        w.email,
        s.status_name
    FROM wardens w
    JOIN warden_status_history wsh
        ON w.warden_id = wsh.warden_id AND wsh.end_date IS NULL
    JOIN statuses s ON wsh.status_id = s.status_id
    WHERE wsh.status_id = sid;
END;
$$ LANGUAGE plpgsql;

-- seed lookups
   -- pre-populates required standard values
   -- prevents invalid inserts due to missing reference data
   -- ensures system is usable immediately after setup
INSERT INTO roles(role_name) VALUES
('admin'),('field'),('rift'),('trainer'),('astral');

INSERT INTO statuses(status_name) VALUES
('active'),('onleave'),('terminated');

INSERT INTO clearance_levels(clearance_name) VALUES
('alpha'),('omega'),('eclipse');

INSERT INTO identifier_types(type_name) VALUES
('badge'),('passport'),('visa');

-- seed aliases
    -- provides common alternative user inputs
    -- ensures free text menu inputs is correctly resolved without breaking consistency
    -- demonstrates that the alias resolution system is functional
INSERT INTO role_aliases VALUES
('administrator',1),('field agent',2);

INSERT INTO status_aliases VALUES
('on leave',2),('inactive',3);

INSERT INTO clearance_aliases VALUES
('a',1),('o',2);

INSERT INTO identifier_type_aliases VALUES
('id',1),('pass',2);

-- sample data for 10 warden entries
    -- demonstrates the system works end to end
    -- confirms the database builds and runs without error
SELECT create_warden('alice','carter','alice@test.com','admin','active','alpha','badge','B1001','2022-01-01');
SELECT create_warden('brian','lopez','brian@test.com','field','active','omega','badge','B1002','2022-02-01');
SELECT create_warden('chloe',NULL,'chloe@test.com','rift','onleave','alpha','passport','P2001','2023-01-01');
SELECT create_warden('david','kim','david@test.com','trainer','active','eclipse','visa','V3001','2021-03-01');
SELECT create_warden('emma','stone','emma@test.com','field','active','omega','badge','B1005','2022-06-01');
SELECT create_warden('frank','wright','frank@test.com','admin','active','alpha','passport','P2006','2020-01-01');
SELECT create_warden('grace','hall','grace@test.com','trainer','active','omega','badge','B1007','2021-09-01');
SELECT create_warden('henry','adams','henry@test.com','admin','active','alpha','visa','V3008','2020-01-01');
SELECT create_warden('isla','turner','isla@test.com','rift','active','eclipse','badge','B1009','2022-11-01');
SELECT create_warden('jack','evans','jack@test.com','field','active','omega','passport','P2010','2023-04-01');

-- terminate one warden as example
SELECT terminate_warden(6, '2023-01-01');
SELECT deactivate_warden(9, '2023-11-11');

-- sample data for certifications
INSERT INTO certifications (warden_id, certification_name, date_earned, expiration_date)
VALUES
(1,'first aid','2022-01-10','2025-01-10'),
(1,'fire safety','2021-05-20','2024-05-20'),
(2,'advanced tracking','2023-03-15','2026-03-15'),
(3,'first aid','2020-07-01','2023-07-01'),
(4,'leadership training','2022-09-10','2025-09-10');

-- permissions and access control
    -- removes direct, allows read access, allows use of controlled functions
    -- promotes data integrity by preventing users from entering directly into the tables
    -- must be final section to ensure sample data inserts don't fail

REVOKE INSERT, UPDATE, DELETE ON wardens FROM PUBLIC;
REVOKE INSERT, UPDATE, DELETE ON identifiers FROM PUBLIC;
REVOKE INSERT, UPDATE, DELETE ON warden_role_history FROM PUBLIC;
REVOKE INSERT, UPDATE, DELETE ON warden_status_history FROM PUBLIC;
REVOKE INSERT, UPDATE, DELETE ON warden_clearance_history FROM PUBLIC;

GRANT SELECT ON wardens TO PUBLIC;
GRANT SELECT ON identifiers TO PUBLIC;
GRANT SELECT ON certifications TO PUBLIC;
GRANT SELECT ON warden_role_history TO PUBLIC;
GRANT SELECT ON warden_status_history TO PUBLIC;
GRANT SELECT ON warden_clearance_history TO PUBLIC;

GRANT EXECUTE ON FUNCTION create_warden(TEXT, TEXT, TEXT, TEXT, TEXT, TEXT, TEXT, TEXT, DATE) TO PUBLIC;
GRANT EXECUTE ON FUNCTION deactivate_warden(INT, DATE) TO PUBLIC;
GRANT EXECUTE ON FUNCTION terminate_warden(INT, DATE) TO PUBLIC;
GRANT EXECUTE ON FUNCTION update_role(INT, TEXT, DATE) TO PUBLIC;
GRANT EXECUTE ON FUNCTION update_status(INT, TEXT, DATE) TO PUBLIC;
GRANT EXECUTE ON FUNCTION update_clearance(INT, TEXT, DATE) TO PUBLIC;
GRANT EXECUTE ON FUNCTION mark_cert_expired(INT) TO PUBLIC;
GRANT EXECUTE ON FUNCTION view_all_wardens() TO PUBLIC;
GRANT EXECUTE ON FUNCTION view_warden_by_id(INT) TO PUBLIC;
GRANT EXECUTE ON FUNCTION view_wardens_by_role(TEXT) TO PUBLIC;
GRANT EXECUTE ON FUNCTION view_wardens_by_status(TEXT) TO PUBLIC;