-- Timesheet triggers

-- Timesheet deletion
CREATE TRIGGER IF NOT EXISTS timesheet_after_delete_tr
    AFTER DELETE
    ON timesheet
BEGIN
    DELETE FROM work_time WHERE timesheet_id = OLD.id;
END;