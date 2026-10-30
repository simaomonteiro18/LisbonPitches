ALTER TABLE opening_hours
    ADD COLUMN open_time time,
    ADD COLUMN close_time time;

UPDATE opening_hours
SET open_time = make_time(open_hour, 0, 0),
    close_time = make_time(close_hour % 24, 0, 0);