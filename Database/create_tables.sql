PRAGMA foreign_keys = OFF;
DROP TABLE IF EXISTS contact_info;
DROP TABLE IF EXISTS contact_info_type;
DROP TABLE IF EXISTS department;
DROP TABLE IF EXISTS project;
DROP TABLE IF EXISTS role_assignment;
DROP TABLE IF EXISTS role_type;
DROP TABLE IF EXISTS task;
DROP TABLE IF EXISTS task_type;
/*DROP TABLE IF EXISTS timesheet;*/
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS user_type;
DROP TABLE IF EXISTS work_time;
PRAGMA foreign_keys = ON;

CREATE TABLE contact_info (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    info        VARCHAR NOT NULL,
    user_id     INTEGER NOT NULL,
    type        VARCHAR NOT NULL,
    CONSTRAINT contact_info_user_fk
        FOREIGN KEY ( user_id )
        REFERENCES user ( id ),
    CONSTRAINT contact_info_contact_info_type_fk
        FOREIGN KEY ( type )
        REFERENCES contact_info_type ( type )
);

CREATE TABLE contact_info_type (
    type        VARCHAR PRIMARY KEY
);

CREATE TABLE department (
    id          INTEGER PRIMARY KEY AUTOINCREMENT,
    name        VARCHAR NOT NULL UNIQUE
);

CREATE TABLE project (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    name            VARCHAR NOT NULL UNIQUE,
    start_date      DATE NOT NULL,
    due_date        DATE,
    end_date        DATE,
    selector        VARCHAR NOT NULL,
    CONSTRAINT ch_inh_project
        CHECK ( selector IN ( 'active', 'finished' ) ),
    CONSTRAINT project_exdep
        CHECK ( ( selector = 'active'
                    AND end_date IS NULL )
                OR ( selector = 'finished'
                    AND due_date IS NULL
                    AND end_date IS NOT NULL ) )
    CONSTRAINT valid_start_date 
        CHECK (LENGTH(start_date) <= 10 AND DATE(start_date, '+0 days') IS start_date)
    CONSTRAINT valid_end_date 
        CHECK (LENGTH(end_date) <= 10 AND DATE(end_date, '+0 days') IS end_date)
    CONSTRAINT valid_due_date 
        CHECK (LENGTH(due_date) <= 10 AND DATE(due_date, '+0 days') IS due_date)
);

CREATE TABLE role_assignment (
    user_id             INTEGER NOT NULL,
    project_id          INTEGER NOT NULL,
    role_type           VARCHAR NOT NULL,
    CONSTRAINT role_assignment_pk PRIMARY KEY ( user_id,
                                                project_id,
                                                role_type ),
    CONSTRAINT role_assignment_user_fk
        FOREIGN KEY ( user_id )
        REFERENCES user ( id ),
    CONSTRAINT role_assignment_project_fk
        FOREIGN KEY ( project_id )
        REFERENCES project ( id ),
    CONSTRAINT role_assignment_role_type_fk
        FOREIGN KEY ( role_type )
        REFERENCES role_type ( type )
);

CREATE TABLE role_type (
    type VARCHAR2(60) PRIMARY KEY
);

CREATE TABLE task (
    id                  INTEGER PRIMARY KEY AUTOINCREMENT,
    name                VARCHAR,
    project_id          INTEGER,
    priority            INTEGER,
    description         VARCHAR,
    type                VARCHAR NOT NULL,
    user_id             INTEGER,
    status              VARCHAR,
    CONSTRAINT task_project_fk
        FOREIGN KEY ( project_id )
        REFERENCES project ( id ),
    CONSTRAINT task_task_type_fk
        FOREIGN KEY ( type )
        REFERENCES task_type ( type ),
    CONSTRAINT task_user_fk
        FOREIGN KEY ( user_id )
        REFERENCES user ( id ),
    CONSTRAINT priority_is_numeric
        CHECK (typeof(priority) = 'integer' OR typeof(priority) = 'real' OR typeof(priority) = NULL),
    CONSTRAINT status_is_finished_or_active
        CHECK (status = 'active' OR status = 'finished')
);

CREATE TABLE task_type (
    type VARCHAR2(60) PRIMARY KEY
);

--CREATE TABLE timesheet (
--    id            INTEGER PRIMARY KEY AUTOINCREMENT,
--    user_id       NUMBER(8) NOT NULL,
--    CONSTRAINT timesheet_user_fk
--        FOREIGN KEY ( user_id )
--        REFERENCES user ( id )
--);

/*CREATE UNIQUE INDEX timesheet__idx ON
    timesheet (
        user_id
    ASC );*/

CREATE TABLE user (
    id                        INTEGER PRIMARY KEY AUTOINCREMENT,
    first_name                VARCHAR NOT NULL,
    last_name                 VARCHAR NOT NULL,
    type                      VARCHAR NOT NULL,
    department_id             INTEGER NOT NULL,
    login                     VARCHAR NOT NULL UNIQUE,
    password                  VARCHAR NOT NULL,
    CONSTRAINT user_department_fk
        FOREIGN KEY ( department_id )
        REFERENCES department ( id ),
    CONSTRAINT user_user_type_fk
        FOREIGN KEY ( type )
        REFERENCES user_type ( type )
);

CREATE TABLE user_type (
    type VARCHAR2(50) PRIMARY KEY
);

CREATE TABLE work_time (
    id                      INTEGER PRIMARY KEY AUTOINCREMENT,
    date                    DATE NOT NULL,
    time                    INTEGER NOT NULL,
    task_id                 INTEGER NOT NULL,
    CONSTRAINT work_time_task_fk
        FOREIGN KEY ( task_id )
        REFERENCES task ( id ),
    CONSTRAINT valid_date 
        CHECK (LENGTH(date) <= 10 AND DATE(date, '+0 days') IS date)
    CONSTRAINT time_is_numeric
        CHECK (typeof(time) = 'integer' OR typeof(time) = 'real')
    CONSTRAINT time_is_positive
        CHECK (time > 0)
);

DROP VIEW IF EXISTS Active_Project;
CREATE VIEW Active_Project ( id, name, start_date, due_date ) AS
SELECT
    project.id,
    project.name,
    project.start_date,
    project.due_date
FROM
    project
WHERE project.selector = 'active'
;

DROP VIEW IF EXISTS Finished_Project;
CREATE VIEW Finished_Project ( id, name, start_date, end_date ) AS
SELECT
    project.id,
    project.name,
    project.start_date,
    project.end_date
FROM
    project 
WHERE project.selector = 'finished'
;