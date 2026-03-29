-- Habitats biome constraint
ALTER TABLE habitats
    ADD CONSTRAINT chk_habitats_biome
        CHECK (biome IN ('FOREST', 'DESERT', 'OCEAN'));

-- Creatures danger_level constraint
ALTER TABLE creatures
    ADD CONSTRAINT chk_creatures_danger_level
        CHECK (danger_level IN ('LOW', 'MEDIUM', 'HIGH'));

-- Creatures condition constraint
ALTER TABLE creatures
    ADD CONSTRAINT chk_creatures_condition
        CHECK (condition IN ('STABLE', 'QUARANTINED', 'CRITICAL'));