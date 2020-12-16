PRAGMA foreign_keys = OFF;
DROP TABLE IF EXISTS contact_info;
DROP TABLE IF EXISTS contact_info_type;
DROP TABLE IF EXISTS department;
DROP TABLE IF EXISTS project;
DROP TABLE IF EXISTS role_assignment;
DROP TABLE IF EXISTS role_type;
DROP TABLE IF EXISTS task;
DROP TABLE IF EXISTS task_type;
DROP TABLE IF EXISTS timesheet;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS user_type;
DROP TABLE IF EXISTS work_time;
PRAGMA foreign_keys = ON;

CREATE TABLE contact_info (
    id_info                 INTEGER PRIMARY KEY AUTOINCREMENT,
    info                    VARCHAR2(100) NOT NULL,
    user_id                 NUMBER(8) NOT NULL,
    contact_info_type_type  VARCHAR2(40) NOT NULL,
    CONSTRAINT contact_info_user_fk
        FOREIGN KEY ( user_id )
        REFERENCES use ( id ),
    CONSTRAINT contact_info_contact_info_type_fk
        FOREIGN KEY ( contact_info_type_type )
        REFERENCES contact_info_type ( type )
);

CREATE TABLE contact_info_type (
    type VARCHAR2(40) PRIMARY KEY
);

CREATE TABLE department (
    id_department  INTEGER PRIMARY KEY AUTOINCREMENT,
    name           VARCHAR2(50) NOT NULL
);

CREATE TABLE project (
    id_project  INTEGER PRIMARY KEY AUTOINCREMENT,
    name        VARCHAR2(40) NOT NULL,
    start_date  DATE NOT NULL,
    due_date    DATE,
    end_date    DATE,
    selector    VARCHAR2(8) NOT NULL,
    CONSTRAINT ch_inh_project
        CHECK ( selector IN ( 'Active', 'Finished' ) ),
    CONSTRAINT project_exdep
        CHECK ( ( selector = 'Active'
                    AND end_date IS NULL )
                OR ( selector = 'Finished'
                    AND due_date IS NULL
                    AND end_date IS NOT NULL ) )
);

CREATE TABLE role_assignment (
    user_id             NUMBER(8) NOT NULL,
    project_id_project  NUMBER(8) NOT NULL,
    role_type_type      VARCHAR2(60) NOT NULL,
    CONSTRAINT role_assignment_pk PRIMARY KEY ( user_id,
                                                project_id_project,
                                                role_type_type ),
    CONSTRAINT role_assignment_user_fk
        FOREIGN KEY ( user_id )
        REFERENCES user ( id ),
    CONSTRAINT role_assignment_project_fk
        FOREIGN KEY ( project_id_project )
        REFERENCES project ( id_project ),
    CONSTRAINT role_assignment_role_type_fk
        FOREIGN KEY ( role_type_type )
        REFERENCES role_type ( type )
);

CREATE TABLE role_type (
    type VARCHAR2(60) PRIMARY KEY
);

CREATE TABLE task (
    id_task             INTEGER PRIMARY KEY AUTOINCREMENT,
    name                VARCHAR2(100) NOT NULL,
    project_id_project  NUMBER(8),
    priority            NUMBER(1) NOT NULL,
    description         VARCHAR2(1000),
    task_type_type      VARCHAR2(60) NOT NULL,
    user_id             NUMBER(8),
    CONSTRAINT task_project_fk
        FOREIGN KEY ( project_id_project )
        REFERENCES project ( id_project ),
    CONSTRAINT task_task_type_fk
        FOREIGN KEY ( task_type_type )
        REFERENCES task_type ( type ),
    CONSTRAINT task_user_fk
        FOREIGN KEY ( user_id )
        REFERENCES user ( id )
);

CREATE TABLE task_type (
    type VARCHAR2(60) PRIMARY KEY
);

CREATE TABLE timesheet (
    id_timesheet  INTEGER PRIMARY KEY AUTOINCREMENT,
    user_id       NUMBER(8) NOT NULL,
    CONSTRAINT timesheet_user_fk
        FOREIGN KEY ( user_id )
        REFERENCES user ( id )
);

CREATE UNIQUE INDEX timesheet__idx ON
    timesheet (
        user_id
    ASC );

CREATE TABLE user (
    id                        INTEGER PRIMARY KEY AUTOINCREMENT,
    first_name                VARCHAR2(40) NOT NULL,
    last_name                 VARCHAR2(40) NOT NULL,
    user_type_type            VARCHAR2(50) NOT NULL,
    department_id_department  NUMBER(3) NOT NULL,
    login                     VARCHAR2(20) NOT NULL,
    password                  VARCHAR2(40) NOT NULL,
    CONSTRAINT user_department_fk
        FOREIGN KEY ( department_id_department )
        REFERENCES department ( id_department ),
    CONSTRAINT user_user_type_fk
        FOREIGN KEY ( user_type_type )
        REFERENCES user_type ( type )
);

CREATE TABLE user_type (
    type VARCHAR2(50) PRIMARY KEY
);

CREATE TABLE work_time (
    id_work_time            INTEGER PRIMARY KEY AUTOINCREMENT,
    "date"                  DATE NOT NULL,
    time                    NUMBER NOT NULL,
    task_id_task            NUMBER(8) NOT NULL,
    timesheet_id_timesheet  NUMBER(8) NOT NULL,
    CONSTRAINT work_time_task_fk
        FOREIGN KEY ( task_id_task )
        REFERENCES task ( id_task ),
    CONSTRAINT work_time_timesheet_fk
        FOREIGN KEY ( timesheet_id_timesheet )
        REFERENCES timesheet ( id_timesheet )
);

DROP VIEW IF EXISTS Active_Project;
CREATE VIEW Active_Project ( id_project, name, start_date, due_date ) AS
SELECT
    project.id_project,
    project.name,
    project.start_date,
    project.due_date
FROM
    project
;

DROP VIEW IF EXISTS Finished_Project;
CREATE VIEW Finished_Project ( id_project, name, start_date, end_date ) AS
SELECT
    project.id_project,
    project.name,
    project.start_date,
    project.end_date
FROM
    project 
;
