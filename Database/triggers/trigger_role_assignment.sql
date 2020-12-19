-- Role assignment triggers
DROP TRIGGER IF EXISTS role_assignment_after_insert_tg;
DROP TRIGGER IF EXISTS role_assignment_after_update_tg;

CREATE TRIGGER IF NOT EXISTS role_assignment_after_insert_tg
    AFTER INSERT
    ON role_assignment
BEGIN
    SELECT
        CASE
            WHEN (SELECT EXISTS(
                    SELECT 1 
                    FROM project 
                    WHERE id = NEW.project_id AND selector = 'finished')) > 0 
            THEN RAISE (ABORT, 'Cannot assign a role in a finished project.')
        END;
END;

CREATE TRIGGER IF NOT EXISTS role_assignment_after_update_tg
    AFTER UPDATE
    ON role_assignment
BEGIN
    SELECT
        CASE
            WHEN (SELECT EXISTS(
                    SELECT 1 
                    FROM project 
                    WHERE id = NEW.project_id AND selector = 'finished')) > 0 
            THEN RAISE (ABORT, 'Cannot assign a role in a finished project.')
        END;
END;