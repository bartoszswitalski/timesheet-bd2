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

-- Project deletion
CREATE TRIGGER IF NOT EXISTS project_after_delete_tg
    AFTER DELETE
    ON project
BEGIN
    DELETE FROM role_assignment WHERE project_id = OLD.id;
    DELETE FROM task WHERE project_id = OLD.id;
END;
