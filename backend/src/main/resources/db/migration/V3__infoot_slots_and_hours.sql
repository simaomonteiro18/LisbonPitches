UPDATE pitches SET slot_minutes = 30 WHERE name = 'InFoot';

UPDATE opening_hours
SET close_time = '01:00'
WHERE day_type = 'WEEKDAY'
  AND pitch_id = (SELECT id FROM pitches WHERE name = 'InFoot');