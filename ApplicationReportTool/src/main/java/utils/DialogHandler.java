package utils;

import database.Query;
import database.Results;

import javax.swing.*;
import java.awt.*;

public class DialogHandler {

    private static final String SINGLE_EMPLOYEE = "Single employee";
    private static final String MULTIPLE_EMPLOYEES = "Multiple employees";

    public static Credentials showSignInDialog(JFrame frame) {
        JPanel label = new JPanel(new GridLayout(0, 1, 2, 2));
        label.add(new JLabel("Login", SwingConstants.RIGHT));
        label.add(new JLabel("Password", SwingConstants.RIGHT));

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(label, BorderLayout.WEST);

        JPanel controls = new JPanel(new GridLayout(0, 1, 2, 2));
        JTextField login = new JTextField();
        JPasswordField password = new JPasswordField();

        controls.add(login);
        controls.add(password);

        panel.add(controls, BorderLayout.CENTER);

        int check = JOptionPane.showConfirmDialog(frame, panel, "Sign-in", JOptionPane.OK_CANCEL_OPTION);

        if (check == JOptionPane.CANCEL_OPTION) {
            System.exit(0);
        }

        return new Credentials(login.getText(), new String(password.getPassword()));
    }

    public static void showConfirmDialog(JFrame frame, String message, String title) {

        JOptionPane.showConfirmDialog(frame, message, title, JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);

    }

    private static JDialog setTableDialog(JFrame frame, String title, String[][] rowData, String[] cols) {
        JDialog dialog = new JDialog(frame, title);

        JTable timesheet = new JTable(rowData, cols);
        JScrollPane scrollPane = new JScrollPane(timesheet);

        dialog.add(scrollPane, BorderLayout.CENTER);

        dialog.pack();
        dialog.setLocationRelativeTo(frame);

        return dialog;

    }

    public static void showEmployeesDialog(JFrame frame, String departmentId) {
        String[] cols = new String[]{"id", "first_name", "last_name", "type"};

        Results results = Query.runQuery(
                /* SELECT */cols,
                /* FROM */ ("user"),
                ("WHERE department_id = " + departmentId), null, null, null);

        Results departmentName = Query.runQuery(
                /* SELECT */ new String[]{"name"},
                /* FROM */ ("department"),
                ("WHERE id = " + departmentId), null, null, null);

        String title = ("Employees of department " + departmentName.getTopResult(0));

        JDialog employeesList = setTableDialog(frame, title, results.getRowData(), cols);

        employeesList.setVisible(true);
    }

    public static void showEmployeesDialogAdmin(JFrame frame) {
        String[] cols = new String[]{"id", "first_name", "last_name", "type"};

        Results results = Query.runQuery(
                /* SELECT */cols,
                /* FROM */ ("user"),
                null, null, null, null);

        String title = ("Employees of the company");

        JDialog employeesList = setTableDialog(frame, title, results.getRowData(), cols);

        employeesList.setVisible(true);
    }

    private static String setTimesheetDialog(JFrame frame) {
        JPanel label = new JPanel(new GridLayout(0, 1, 2, 2));
        label.add(new JLabel("User id", SwingConstants.RIGHT));

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(label, BorderLayout.WEST);

        JPanel controls = new JPanel(new GridLayout(0, 1, 2, 2));
        JTextField userId = new JTextField();
        controls.add(userId);

        panel.add(controls, BorderLayout.CENTER);

        int check = JOptionPane.showConfirmDialog(frame, panel, "Search for a timesheet", JOptionPane.OK_CANCEL_OPTION);

        if (check == JOptionPane.CANCEL_OPTION) {
            panel.setVisible(false);
            return null;
        }

        return userId.getText();

    }

    public static void showTimesheetDialog(JFrame frame) {
        String userId = setTimesheetDialog(frame);
        if (userId == null) return;

        String[] cols = new String[]{"employee id", "First name", "Last name", "date", "time", "task"};
        String[] queryCols = new String[]{"u.id", "u.first_name", "u.last_name", "w.date", "w.time", "t.name"};

        Results results = Query.runQuery(
                /* SELECT */ queryCols,
                /* FROM */ ("user u JOIN task t ON u.id = t.user_id JOIN work_time w ON w.task_id = t.id"),
                ("WHERE u.id is \"" + userId + "\""), null, null, null);

        String title = ("Timesheet of user id = " + userId);

        JDialog employeeTimesheet = setTableDialog(frame, title, results.getRowData(), cols);

        employeeTimesheet.setVisible(true);

    }

    public static Results setFilteredTimesheet(String inputYear, String inputMonth, String inputEntries,
                                               String inputNumber, String userId) {

        String[] cols = new String[]{"employee id", "First name", "Last name", "date", "time", "task"};
        String[] select = new String[]{"u.id", "u.first_name", "u.last_name", "w.date", "w.time", "t.name"};
        String from = ("user u JOIN task t ON u.id = t.user_id JOIN work_time w on w.task_id = t.id");
        String where = null;
        String groupBy = null;
        String having = null;
        String orderBy = ("ORDER BY u.id");

        /* Suma godzin */
        if (inputEntries.equals("total")) {
            cols = new String[]{"employee id", "First name", "Last name", "year", "month", "total time"};
            select = new String[]{"u.id", "u.first_name", "u.last_name", "strftime('%Y', w.date)", "strftime('%m', w.date)", "SUM(w.time)"};
            groupBy = ("GROUP BY u.id");

            /* Suma godzin pracy w każdym roku*/
            if (inputYear.equals("all historical data") && inputMonth.equals("yearly")) {
                /* Wszystkich pracowników */
                cols = new String[]{"employee id", "First name", "Last name", "year", "total time"};
                select = new String[]{"u.id", "u.first_name", "u.last_name", "strftime('%Y', w.date)", "SUM(w.time)"};
                groupBy = ("GROUP BY u.id, strftime('%Y', w.date)");

                /* Pojedynczego pracownika */
                if (inputNumber.equals(SINGLE_EMPLOYEE)) where = ("WHERE u.id is \"" + userId + "\"");
            }
            /* Suma godzin pracy w każdym miesiącu*/
            else if (inputYear.equals("all historical data") /* && !inputMonth.equals("yearly")*/) {
                /* Wszystkich pracowników */
                cols = new String[]{"employee id", "First name", "Last name", "month", "total time"};
                select = new String[]{"u.id", "u.first_name", "u.last_name", "strftime('%m', w.date)", "SUM(w.time)"};
                groupBy = ("GROUP BY u.id, strftime('%m', w.date)");

                /* Pojedynczego pracownika */
                if (inputNumber.equals(SINGLE_EMPLOYEE)) where = ("WHERE u.id is \"" + userId + "\"");
            }
            /* Suma godzin pracy w każdym miesiącu danego roku*/
            else if (/*!inputYear.equals("all historical data") &&*/ inputMonth.equals("yearly")) {
                /* Wszystkich pracowników */
                where = ("WHERE strftime('%Y', w.date) = '" + inputYear + "'");
                groupBy = ("GROUP BY u.id, strftime('%m', w.date)");
                /* Pojedynczego pracownika */

                if (inputNumber.equals(SINGLE_EMPLOYEE)) where = ("WHERE strftime('%Y', w.date) = '" + inputYear + "'" +
                        " and u.id is \"" + userId + "\"");
            }
            /* Suma godzin pracy w danym roku i miesiącu*/
            else {
                /* Wszystkich pracowników */
                where = ("WHERE strftime('%Y', w.date) = '" + inputYear + "' " +
                        "and strftime('%m', w.date) = '" + inputMonth + "'");
                groupBy = ("GROUP BY u.id, strftime('%m', w.date)");
                /* Pojedynczego pracownika */

                if (inputNumber.equals(SINGLE_EMPLOYEE)) where = ("WHERE strftime('%Y', w.date) = '" + inputYear + "'" +
                        " and strftime('%m', w.date) = '" + inputMonth + "'" +
                        " and u.id is \"" + userId + "\"");
            }


        }

        /* Pojedyncze wpisy */
        else if (inputEntries.equals("single entries")) {

            /* Pojedyncze wpisy w konkretnym miesiącu i roku */
            if (!inputYear.equals("all historical data") && !inputMonth.equals("yearly")) {
                /* Wszystkich pracowników */
                where = ("WHERE strftime('%Y', w.date) = '" + inputYear + "' " +
                        "and strftime('%m', w.date) = '" + inputMonth + "'");

                /* Pojedynczego pracownika */
                if (inputNumber.equals(SINGLE_EMPLOYEE)) where = ("WHERE u.id is \"" + userId + "\" " +
                        "and strftime('%Y', w.date) = '" + inputYear + "' " +
                        "and strftime('%m', w.date) = '" + inputMonth + "'");
            }
            /* Pojedyczne wpisy w konkretnym roku */
            else if (!inputYear.equals("all historical data") /*&& inputMonth.equals("yearly")*/) {
                /* Wszystkich pracowników */
                where = ("WHERE strftime('%Y', w.date) = '" + inputYear + "'");
                /* Pojedynczego pracownika */
                if (inputNumber.equals(SINGLE_EMPLOYEE)) where = ("WHERE u.id is \"" + userId + "\" " +
                        "and strftime('%Y', w.date) = '" + inputYear + "'");
            }
            /* Pojedyncze wpisy w całej historii w ustalonym miesiącu */
            else if (/*inputYear.equals("all historical data") &&*/ !inputMonth.equals("yearly")) {
                /* Wszystkich pracowników */
                where = ("WHERE strftime('%m', w.date) = '" + inputMonth + "'");
                /* Pojedynczego pracownika */
                if (inputNumber.equals(SINGLE_EMPLOYEE)) where = ("WHERE u.id is \"" + userId + "\" " +
                        "and strftime('%m', w.date) = '" + inputMonth + "'");
            }
            /* Pojedyncze wpisy w całej historii */
            else /* */ {
                /* Wszystkich pracowników */
                // nothing needed
                /* Pojedynczego pracownika */
                if (inputNumber.equals(SINGLE_EMPLOYEE)) where = ("WHERE u.id is \"" + userId + "\"");
            }

        }

        Results results = Query.runQuery(select, from, where, groupBy, having, orderBy);
        results.setCols(cols);

        return results;

    }

    public static void showFilteredTimesheetDialog(JFrame frame) {
        String[] yearChoices = new String[]{"all historical data", "2017", "2018", "2019", "2020", "2021"};
        String inputYear = (String) JOptionPane.showInputDialog(null,
                "Choose year:", "Filtered timesheet", JOptionPane.QUESTION_MESSAGE,
                null, yearChoices, yearChoices[0]);

        if (inputYear == null) return;

        String[] monthChoices = new String[]{"yearly", "01", "02", "03", "04", "05", "06",
                "07", "08", "09", "10", "11", "12"};
        String inputMonth = (String) JOptionPane.showInputDialog(null,
                "Choose month:", "Filtered timesheet", JOptionPane.QUESTION_MESSAGE,
                null, monthChoices, monthChoices[0]);

        if (inputMonth == null) return;

        String[] outputEntriesNum = new String[]{"single entries", "total"};
        String inputEntries = (String) JOptionPane.showInputDialog(null,
                "Choose output form:", "Filtered timesheet", JOptionPane.QUESTION_MESSAGE,
                null, outputEntriesNum, outputEntriesNum[0]);

        if (inputEntries == null) return;

        String[] numberChoices = new String[]{SINGLE_EMPLOYEE, MULTIPLE_EMPLOYEES};
        String inputNumber = (String) JOptionPane.showInputDialog(null,
                "Choose numerousness:", "Filtered timesheet", JOptionPane.QUESTION_MESSAGE,
                null, numberChoices, numberChoices[0]);

        if (inputNumber == null) return;

        String userId = null;
        if (inputNumber.equals(SINGLE_EMPLOYEE)) {
            userId = (String) JOptionPane.showInputDialog(null,
                    "Type in user id:", "Filtered timesheet", JOptionPane.QUESTION_MESSAGE,
                    null, null, null);
            if (userId == null) return;
        }

        Results results = setFilteredTimesheet(inputYear, inputMonth, inputEntries, inputNumber, userId);

        String title = ("Year=" + inputYear + " Month=" + inputMonth + " Entries=" + inputEntries + " No employees: " + inputNumber);

        JDialog outputTable = setTableDialog(frame, title, results.getRowData(), results.getCols());

        outputTable.setVisible(true);

    }
}
