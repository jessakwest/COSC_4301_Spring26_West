
-- 1) Add habitat_id column if missing
ALTER TABLE creatures
    ADD COLUMN IF NOT EXISTS habitat_id BIGINT;

-- 2) Create a default habitat for existing creatures (needed before NOT NULL)
INSERT INTO habitats (biome, location, min_temp_c, max_temp_c, created_at)
VALUES ('FOREST', 'Default Habitat', 10, 25, NOW());

-- 3) Assign existing creatures to a habitat if they currently have NULL
UPDATE creatures
SET habitat_id = (SELECT id FROM habitats WHERE location = 'Default Habitat' ORDER BY id LIMIT 1)
WHERE habitat_id IS NULL;

-- 4) Make habitat_id required (matches optional=false and nullable=false)
ALTER TABLE creatures
    ALTER COLUMN habitat_id SET NOT NULL;

-- 5) Add the foreign key constraint (avoid duplicate constraint errors)
DO $$
    BEGIN
        IF NOT EXISTS (
            SELECT 1
            FROM pg_constraint
            WHERE conname = 'fk_creatures_habitats'
        ) THEN
            ALTER TABLE creatures
                ADD CONSTRAINT fk_creatures_habitats
                    FOREIGN KEY (habitat_id) REFERENCES habitats(id);
        END IF;
    END $$;