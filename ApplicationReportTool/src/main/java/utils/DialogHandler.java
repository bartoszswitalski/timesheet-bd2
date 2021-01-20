package utils;

import database.Query;
import database.Results;
import gui.AppFrame;

import javax.swing.*;
import java.awt.*;

public class DialogHandler {

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
                /* FROM */ new String("user"),
                new String("WHERE department_id = " + departmentId));

        Results departmentName = Query.runQuery(
                /* SELECT */ new String[]{"name"},
                /* FROM */ new String("department"),
                new String("WHERE id = " + departmentId));

        String title = new String("Employees of department " + departmentName.getTopResult(0));

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
                /* FROM */ new String("user u JOIN task t ON u.id = t.user_id JOIN work_time w ON w.task_id = t.id"),
                new String("WHERE u.id is \"" + userId + "\""));

        String title = new String("Timesheet of user id = " + userId);

        JDialog employeeTimesheet = setTableDialog(frame, title, results.getRowData(), cols);

        employeeTimesheet.setVisible(true);

    }
}
