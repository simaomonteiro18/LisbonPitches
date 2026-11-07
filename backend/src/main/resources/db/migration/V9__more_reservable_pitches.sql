UPDATE pitches SET reservable = true, slot_minutes = 60
WHERE name IN ('Arena PlaySports Mem Martins', 'Beloura Soccer');

UPDATE pitches SET reservable = true, slot_minutes = 30
WHERE name = 'Quantum Park';

INSERT INTO opening_hours (pitch_id, day_type, open_time, close_time)
SELECT id, 'WEEKDAY', '17:00', '23:00' FROM pitches WHERE name = 'Arena PlaySports Mem Martins';

INSERT INTO opening_hours (pitch_id, day_type, open_time, close_time)
SELECT id, 'WEEKEND', '10:00', '22:00' FROM pitches WHERE name = 'Arena PlaySports Mem Martins';

INSERT INTO opening_hours (pitch_id, day_type, open_time, close_time)
SELECT id, 'WEEKDAY', '18:00', '23:00' FROM pitches WHERE name = 'Beloura Soccer';

INSERT INTO opening_hours (pitch_id, day_type, open_time, close_time)
SELECT id, 'WEEKEND', '09:00', '21:00' FROM pitches WHERE name = 'Beloura Soccer';

INSERT INTO opening_hours (pitch_id, day_type, open_time, close_time)
SELECT id, 'WEEKDAY', '16:00', '22:00' FROM pitches WHERE name = 'Quantum Park';

INSERT INTO opening_hours (pitch_id, day_type, open_time, close_time)
SELECT id, 'WEEKEND', '09:00', '20:00' FROM pitches WHERE name = 'Quantum Park';