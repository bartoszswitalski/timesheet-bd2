-- Task triggers

DROP TRIGGER IF EXISTS task_insert_tr;
DROP TRIGGER IF EXISTS task_update_tr;

CREATE TRIGGER IF NOT EXISTS task_insert_tr
    AFTER INSERT
    ON task
BEGIN
    SELECT 
        CASE
            WHEN (NEW.user_id NOT IN (SELECT user_id FROM role_assignment WHERE user_id = NEW.user_id AND project_id = NEW.project_id))
            THEN RAISE (ABORT, 'User is not assigned to the project.')
            END;
END;

CREATE TRIGGER IF NOT EXISTS task_update_tr
    AFTER UPDATE
    ON task
BEGIN
    SELECT 
        CASE
            WHEN (NEW.user_id NOT IN (SELECT user_id FROM role_assignment WHERE user_id = NEW.user_id AND project_id = NEW.project_id))
            THEN RAISE (ABORT, 'User is not assigned to the project.')
            END;
END;