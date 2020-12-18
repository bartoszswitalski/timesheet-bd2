import sqlite3
from os import listdir

TABLE_DIRECTORY_PATH = "./"
TRIGGER_DIRECTORY_PATH = "triggers/"
DATATYPE_DIRECTORY_PATH = "data types/"
DATA_DIRECTORY_PATH = "data/"
DATABASE_PATH = "database.db"

def manage_scripts():
    table_paths = [TABLE_DIRECTORY_PATH + x for x in listdir(TABLE_DIRECTORY_PATH) if x.endswith(".sql")]
    trigger_paths = [TRIGGER_DIRECTORY_PATH + x for x in listdir(TRIGGER_DIRECTORY_PATH) if x.endswith(".sql")]
    datatype_paths = [DATATYPE_DIRECTORY_PATH + x for x in listdir(DATATYPE_DIRECTORY_PATH) if x.endswith(".sql")]
    data_paths = [DATA_DIRECTORY_PATH + x for x in listdir(DATA_DIRECTORY_PATH) if x.endswith(".sql")]
    data_paths.sort()
    file_paths = table_paths + trigger_paths + datatype_paths + data_paths

    try:
        sqlite_connection = sqlite3.connect(DATABASE_PATH)
        cursor = sqlite_connection.cursor()
        print("Successfully connected to SQLite")

        execute_scripts(file_paths, cursor)
        cursor.close()

    except sqlite3.Error as err:
        print("Error while connecting to SQLite", err)
    finally:
        if (sqlite_connection):
            sqlite_connection.close()
            print("The SQLite connection is closed")


def execute_scripts(file_paths, cursor):
    try:
        for path in file_paths:
            file = open(path)
            script = file.read()
            cursor.executescript(script)
            print("Executed file: " + path)
    except OSError as err:
        print("Error while executing file: " + path, err)
    except sqlite3.Error as err:
        print("Error while executing file: " + path, err)


manage_scripts()
