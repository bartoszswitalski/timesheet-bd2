-- User triggers

-- User deletion
CREATE TRIGGER IF NOT EXISTS user_after_delete_tr
    AFTER DELETE
    ON user
BEGIN
    DELETE FROM timesheet WHERE id = OLD.id;
    DELETE FROM contact_info WHERE user_id = OLD.id;
    DELETE FROM role_assignment WHERE user_id = OLD.id;
END;