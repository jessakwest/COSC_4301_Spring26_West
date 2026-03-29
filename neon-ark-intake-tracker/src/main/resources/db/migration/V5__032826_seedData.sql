-- FIRST seed data for habitats table
INSERT INTO habitats (biome, location, min_temp_c, max_temp_c, created_at)
VALUES
    ('FOREST', 'Sector A - Moss Caverns', 8, 18, NOW()),
    ('DESERT', 'Sector B - Ember Dunes', 30, 48, NOW()),
    ('OCEAN', 'Sector C - Frost Plains', -25, -10, NOW()),
    ('OCEAN', 'Sector D - Abyssal Trench', 2, 6, NOW()),
    ('FOREST', 'Sector E - Skyreach Peaks', -5, 10, NOW()),  -- was MOUNTAIN
    ('FOREST', 'Sector F - Verdant Hollow', 10, 22, NOW()),
    ('DESERT', 'Sector G - Shattered Wastes', 28, 45, NOW()),
    ('OCEAN', 'Sector H - Glacial Expanse', -30, -12, NOW()),
    ('OCEAN', 'Sector I - Coral Expanse', 18, 26, NOW()),
    ('FOREST', 'Sector J - Obsidian Ridge', 0, 15, NOW());  -- was MOUNTAIN


-- THEN seed data for creatures table

INSERT INTO creatures (name, species, danger_level, condition, notes, habitat_id, created_at)
VALUES
-- FOREST
('Nyx', 'Void Fox', 'HIGH', 'QUARANTINED', 'Avoid bright light',
 (SELECT id FROM habitats WHERE location = 'Sector A - Moss Caverns'), NOW()),

('Shadepaw', 'Shadow Lynx', 'MEDIUM', 'QUARANTINED', 'Aggressive in darkness',
 (SELECT id FROM habitats WHERE location = 'Sector A - Moss Caverns'), NOW()),

('Brambleback', 'Forest Boar', 'LOW', 'STABLE', 'Feeds on roots and fungi',
 (SELECT id FROM habitats WHERE location = 'Sector A - Moss Caverns'), NOW()),

('Verdantis', 'Leaf Drake', 'MEDIUM', 'STABLE', 'Photosynthetic scales',
 (SELECT id FROM habitats WHERE location = 'Sector F - Verdant Hollow'), NOW()),

('Willowisp', 'Forest Spirit', 'LOW', 'STABLE', 'Glows faintly at night',
 (SELECT id FROM habitats WHERE location = 'Sector F - Verdant Hollow'), NOW()),

-- DESERT
('Mossclaw', 'Void Stag', 'MEDIUM', 'STABLE', 'Calm but territorial',
 (SELECT id FROM habitats WHERE location = 'Sector B - Ember Dunes'), NOW()),

('Emberling', 'Flame Sprite', 'LOW', 'STABLE', 'Radiates gentle heat',
 (SELECT id FROM habitats WHERE location = 'Sector B - Ember Dunes'), NOW()),

('Dunescourge', 'Sand Wyrm', 'HIGH', 'CRITICAL', 'Burrows rapidly, unpredictable',
 (SELECT id FROM habitats WHERE location = 'Sector B - Ember Dunes'), NOW()),

('Cinderclaw', 'Ash Panther', 'MEDIUM', 'QUARANTINED', 'Leaves burn marks on contact',
 (SELECT id FROM habitats WHERE location = 'Sector G - Shattered Wastes'), NOW()),

('Duststalker', 'Desert Hunter', 'MEDIUM', 'STABLE', 'Camouflages in sand',
 (SELECT id FROM habitats WHERE location = 'Sector G - Shattered Wastes'), NOW()),

('Scorchwing', 'Fire Drake', 'HIGH', 'QUARANTINED', 'Emits intense flames',
 (SELECT id FROM habitats WHERE location = 'Sector G - Shattered Wastes'), NOW()),

-- OCEAN
('Frostveil', 'Ice Serpent', 'HIGH', 'CRITICAL', 'Unstable in warm climates',
 (SELECT id FROM habitats WHERE location = 'Sector C - Frost Plains'), NOW()),

('Glacior', 'Frost Giant', 'HIGH', 'QUARANTINED', 'Requires sub-zero containment',
 (SELECT id FROM habitats WHERE location = 'Sector C - Frost Plains'), NOW()),

('Icelyn', 'Snow Leopard', 'LOW', 'STABLE', 'Highly adaptive predator',
 (SELECT id FROM habitats WHERE location = 'Sector C - Frost Plains'), NOW()),

('Blizzardon', 'Ice Titan', 'HIGH', 'CRITICAL', 'Causes severe storms',
 (SELECT id FROM habitats WHERE location = 'Sector C - Frost Plains'), NOW()),

('Coralyn', 'Reef Whisperer', 'LOW', 'STABLE', 'Communicates with marine life',
 (SELECT id FROM habitats WHERE location = 'Sector D - Abyssal Trench'), NOW()),

('Tidecaller', 'Ocean Siren', 'MEDIUM', 'STABLE', 'Emits hypnotic sounds',
 (SELECT id FROM habitats WHERE location = 'Sector D - Abyssal Trench'), NOW()),

('Deepglow', 'Abyss Jelly', 'LOW', 'STABLE', 'Bioluminescent pulses',
 (SELECT id FROM habitats WHERE location = 'Sector D - Abyssal Trench'), NOW()),

('Seabloom', 'Kelp Guardian', 'LOW', 'STABLE', 'Protective of reefs',
 (SELECT id FROM habitats WHERE location = 'Sector I - Coral Expanse'), NOW()),

('Aqualume', 'Water Serpent', 'MEDIUM', 'STABLE', 'Moves silently underwater',
 (SELECT id FROM habitats WHERE location = 'Sector I - Coral Expanse'), NOW());