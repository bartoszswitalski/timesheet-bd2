-- User triggers
DROP TRIGGER IF EXISTS user_after_delete_tr;

-- User deletion
CREATE TRIGGER IF NOT EXISTS user_after_delete_tr
    AFTER DELETE
    ON user
BEGIN
    DELETE FROM work_time WHERE task_id IN (SELECT id FROM task WHERE user_id = OLD.id);
    DELETE FROM contact_info WHERE user_id = OLD.id;    
    DELETE FROM role_assignment WHERE user_id = OLD.id;

END;