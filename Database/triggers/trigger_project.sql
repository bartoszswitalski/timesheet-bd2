-- Project triggers
DROP TRIGGER IF EXISTS project_insert_tg;
DROP TRIGGER IF EXISTS project_update_tg;
DROP TRIGGER IF EXISTS project_after_delete_tg;
DROP TRIGGER IF EXISTS project_after_update_finished_tg;

-- Validate project end and due date.
CREATE TRIGGER IF NOT EXISTS project_insert_tg
    AFTER INSERT
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
    AFTER UPDATE
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

-- Project update when Finished
CREATE TRIGGER IF NOT EXISTS project_after_update_finished_tg
    AFTER UPDATE
    ON project
BEGIN
    UPDATE task
    SET status = CASE
        WHEN ((SELECT EXISTS(SELECT 1 FROM task WHERE project_id = OLD.id)) AND NEW.selector = 'finished') == 1 THEN 'finished' END
    WHERE project_id = OLD.id;
END;
