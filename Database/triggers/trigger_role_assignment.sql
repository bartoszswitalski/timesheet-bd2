-- Role assignment triggers
DROP TRIGGER IF EXISTS role_assignment_after_delete_tg;

CREATE TRIGGER IF NOT EXISTS role_assignment_after_delete_tg
    AFTER DELETE
    ON role_assignment
BEGIN
    UPDATE task
    SET user_id = CASE
        WHEN (SELECT EXISTS(SELECT 1 FROM role_assignment WHERE project_id = OLD.project_id)) == 0 THEN NULL
    END
    WHERE user_id = OLD.user_id AND project_id = OLD.project_id;
END;