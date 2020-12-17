-- Validate project end and due date.

CREATE TRIGGER IF NOT EXISTS project_insert_tg
    BEFORE INSERT
    ON project
BEGIN
        SELECT
            CASE
                WHEN (NEW.due_date IS NOT NULL AND NEW.due_date < NEW.start_date) THEN
                    RAISE (ABORT, 'Invalid project due date.')
                    
                WHEN (NEW.end_date IS NOT NULL AND NEW.end_date < NEW.start_date) THEN
                    RAISE (ABORT, 'Invalid project end date.')
            END;
END;


CREATE TRIGGER IF NOT EXISTS project_update_tg
    BEFORE UPDATE
    ON project
BEGIN
        SELECT
            CASE
                WHEN (NEW.due_date IS NOT NULL AND NEW.due_date < NEW.start_date) THEN
                    RAISE (ABORT, 'Invalid project due date.')
                    
                WHEN (NEW.end_date IS NOT NULL AND NEW.end_date < NEW.start_date) THEN
                    RAISE (ABORT, 'Invalid project end date.')
            END;
END;