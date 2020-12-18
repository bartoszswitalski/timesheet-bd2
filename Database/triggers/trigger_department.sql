-- Department triggers
DROP TRIGGER IF EXISTS department_after_delete_tr;

-- Department deletion
CREATE TRIGGER IF NOT EXISTS department_after_delete_tr
    AFTER DELETE
    ON department
BEGIN
    DELETE FROM user WHERE department_id = OLD.id;
END;