-- Validation of daily work time limit

CREATE TRIGGER IF NOT EXISTS work_time_insert_tg
    BEFORE INSERT
    ON work_time
BEGIN
        SELECT
            CASE
                WHEN ((SELECT SUM(time) FROM work_time WHERE date LIKE NEW.date AND timesheet_id IS NEW.timesheet_id) >= 16) THEN
                    RAISE (ABORT, 'Daily working time limit has been reached.')
                END;
END;


CREATE TRIGGER IF NOT EXISTS work_time_update_tg
    BEFORE UPDATE
    ON work_time
BEGIN
        SELECT
            CASE
                WHEN ((SELECT SUM(time) FROM work_time WHERE date LIKE NEW.date AND timesheet_id IS NEW.timesheet_id) >= 16) THEN
                    RAISE (ABORT, 'Daily working time limit has been reached.')
                END;
END;