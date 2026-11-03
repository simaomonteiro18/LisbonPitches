ALTER TABLE opening_hours
DROP COLUMN IF EXISTS open_hour,
    DROP COLUMN IF EXISTS close_hour,
    ALTER COLUMN open_time SET NOT NULL,
    ALTER COLUMN close_time SET NOT NULL;