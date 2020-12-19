-- Work time triggers
DROP TRIGGER IF EXISTS work_time_after_insert_tg;
DROP TRIGGER IF EXISTS work_time_after_update_tg;

-- Validation of daily work time limit
CREATE TRIGGER IF NOT EXISTS work_time_after_insert_tg
    AFTER INSERT
    ON work_time
BEGIN
        SELECT
            CASE
                WHEN (( SELECT SUM(time) 
                        FROM work_time 
                        WHERE date LIKE NEW.date 
                        AND task_id IS NEW.task_id) >= 16) 
                THEN RAISE (ABORT, 'Daily working time limit has been reached.')
                
                WHEN (SELECT EXISTS(
                        SELECT 1 
                        FROM task 
                        WHERE id = NEW.task_id AND status = 'finished')) > 0 
                THEN RAISE (ABORT, 'Cannot add work time to a finished task.')
            END;
END;


CREATE TRIGGER IF NOT EXISTS work_time_after_update_tg
    AFTER UPDATE
    ON work_time
BEGIN
        SELECT
            CASE
                WHEN (( SELECT SUM(time) 
                        FROM work_time 
                        WHERE date LIKE NEW.date 
                        AND task_id IS NEW.task_id) >= 16) 
                THEN RAISE (ABORT, 'Daily working time limit has been reached.')
                
                WHEN (SELECT EXISTS(
                        SELECT 1 
                        FROM task 
                        WHERE id = NEW.task_id AND status = 'finished')) > 0
                THEN RAISE (ABORT, 'Cannot update work time to a finished task.')
            END;
END;