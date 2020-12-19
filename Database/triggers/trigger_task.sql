-- Task triggers
DROP TRIGGER IF EXISTS task_after_insert_tg;
DROP TRIGGER IF EXISTS task_after_update_tg;
DROP TRIGGER IF EXISTS task_after_delete_tg;

-- validate project assignment
CREATE TRIGGER IF NOT EXISTS task_after_insert_tg
    AFTER INSERT
    ON task
BEGIN
    SELECT 
        CASE
            WHEN (NEW.project_id IS NOT NULL 
            AND NEW.user_id IS NOT NULL 
            AND NEW.user_id NOT IN (
                SELECT user_id 
                FROM role_assignment 
                WHERE user_id = NEW.user_id 
                AND project_id = NEW.project_id))
            THEN RAISE (ABORT, 'User is not assigned to the project.')
            
            WHEN (SELECT EXISTS(
                    SELECT 1 
                    FROM project 
                    WHERE id = NEW.project_id AND selector = 'active')) == 0 
            THEN RAISE (ABORT, 'Cannot add task to finished project.')
        END;
END;

CREATE TRIGGER IF NOT EXISTS task_after_update_tg
    AFTER UPDATE
    ON task
BEGIN
    SELECT 
        CASE
            WHEN (
                NEW.project_id IS NOT NULL 
                AND NEW.user_id IS NOT NULL 
                AND NEW.user_id NOT IN (
                    SELECT user_id 
                    FROM role_assignment 
                    WHERE user_id = NEW.user_id AND project_id = NEW.project_id))
            THEN RAISE (ABORT, 'User is not assigned to the project.')
            
            WHEN (SELECT EXISTS(
                    SELECT 1 
                    FROM project 
                    WHERE id = NEW.project_id AND selector = 'active')) == 0 
            THEN RAISE (ABORT, 'Cannot add task to finished project.')
        END;
END;

-- Task deletion
CREATE TRIGGER IF NOT EXISTS task_after_delete_tg
    AFTER DELETE
    ON task
BEGIN
    DELETE FROM work_time 
    WHERE task_id = OLD.id;
END;