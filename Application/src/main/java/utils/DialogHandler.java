package utils;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.Properties;
import org.jdatepicker.impl.*;

public class DialogHandler {

    public static User showSignInDialog(JFrame frame) {
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

        return new User(login.getText(), new String(password.getPassword()));
    }

    public static void showTimeRegistrationDialog(JFrame frame) {
        JPanel label = new JPanel(new GridLayout(0, 1, 2, 2));
        label.add(new JLabel("Time", SwingConstants.RIGHT));
        label.add(new JLabel("Date", SwingConstants.RIGHT));
        label.add(new JLabel("Task", SwingConstants.RIGHT));

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(label, BorderLayout.WEST);

        JPanel controls = new JPanel(new GridLayout(0, 1, 2, 2));
        JTextField time = new JTextField(5);
        controls.add(time);
        SqlDateModel model = new SqlDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl datePanel = new JDatePanelImpl(model, p);
        JDatePickerImpl datePicker = new JDatePickerImpl(datePanel, new DateLabelFormatter());
        controls.add(datePicker);
        JTextField task = new JTextField(5);
        controls.add(task);
        panel.add(controls, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(frame, panel,
                "Register time", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            // to be implemented
        }
    }

    public static void showConfirmDialog(JFrame frame, String message, String title) {
        JOptionPane.showConfirmDialog(frame, message, title,
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
    }
}
