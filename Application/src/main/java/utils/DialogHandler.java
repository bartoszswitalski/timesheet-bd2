package utils;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Properties;

import core.Connect;
import core.Results;
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

    private static JDialog setAccountDialog(JFrame frame, User user, String title, String[][] rowData, String[] cols) {
        JDialog dialog = new JDialog(frame, title);

        String[] colsUser = new String[]{"first_name", "last_name", "type"};
        Results results = Connect.runQuery(
                /* SELECT */colsUser,
                /* FROM */ new String("user u"),
                new String("WHERE u.id is \"" + user.getID() + "\""));

        JPanel label = new JPanel(new GridLayout(0, 3, 2, 2));
        label.add(new JLabel("Name: " + results.getTopResult(0), SwingConstants.LEFT));
        label.add(new JLabel("Surname: " + results.getTopResult(1), SwingConstants.CENTER));
        label.add(new JLabel("Role: " + results.getTopResult(2), SwingConstants.RIGHT));
        label.add(new JLabel("Contact", SwingConstants.LEFT));
        dialog.add(label, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(rowData, cols);
        JTable contacts = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(contacts);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JPanel options = new JPanel(new GridLayout(0, 3, 2, 2));
        JButton addButton = new JButton("Add contact");
        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
        JButton editButton = new JButton("Edit contact");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = contacts.getSelectedRow();
                if(row >= 0){

                } else {
                    DialogHandler.showConfirmDialog(frame,
                            "You did not select any of the contact forms possible!", "Message");
                }
            }
        });
        JButton deleteButton = new JButton("Delete contact");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = contacts.getSelectedRow();
                if(row >= 0){
                    int check = JOptionPane.showConfirmDialog(null,
                            "Are you sure?", "Delete", JOptionPane.OK_CANCEL_OPTION);

                    if(check == JOptionPane.OK_OPTION) {
                        Connect.runDelete("contact_info",
                                "WHERE info = \""
                                        + contacts.getModel().getValueAt(row, 1).toString() + "\"");
                        ((DefaultTableModel)contacts.getModel()).removeRow(row);
                    }
                } else {
                    DialogHandler.showConfirmDialog(frame,
                            "You did not select any of the contact forms possible!", "Message");
                }
            }
        });
        options.add(addButton);
        options.add(editButton);
        options.add(deleteButton);
        dialog.add(options, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(frame);

        return dialog;
    }

    public static void showAccountDialog(JFrame frame, User user) {
        String[] cols = new String[]{"type", "info"};

        Results results = Connect.runQuery(
                /* SELECT */cols,
                /* FROM */ new String("contact_info ci"),
                new String("WHERE ci.user_id is \"" + user.getID() + "\""));

        String title = new String("Account");

        JDialog employeesList = setAccountDialog(frame, user, title, results.getRowData(), cols);

        employeesList.setVisible(true);
    }

    public static void showContactDialog() {
        JPanel label = new JPanel(new GridLayout(0, 1, 2, 2));
        label.add(new JLabel("Type:", SwingConstants.RIGHT));
        label.add(new JLabel("Value:", SwingConstants.RIGHT));

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(label, BorderLayout.WEST);

        JPanel controls = new JPanel(new GridLayout(0, 1, 2, 2));
        DefaultComboBoxModel list = new DefaultComboBoxModel();
        JComboBox types = new JComboBox(list);
        JTextField value = new JTextField(5);
        controls.add(value);
        panel.add(controls, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(null, panel,
                "Add contact", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            // to be implemented
        }
    }

    public static void showConfirmDialog(JFrame frame, String message, String title) {
        JOptionPane.showConfirmDialog(frame, message, title,
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
    }
}
