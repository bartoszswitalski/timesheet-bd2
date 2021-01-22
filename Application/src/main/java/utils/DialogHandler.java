package utils;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
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
            String[] cols = {"task_id", "date", "time"};
            String[] vals = {"?", "?", "?"};
            String[] params = {task.getText(), datePicker.getJFormattedTextField().getText(), time.getText()};
            Connect.runInsert("work_time", cols, vals, params);
        }
    }

    private static JDialog setAccountDialog(JFrame frame, User user, String title, String[][] rowData, String[] cols) {
        JDialog dialog = new JDialog(frame, title);
        String[] parameters = new String[]{user.getID()};

        String[] colsUser = new String[]{"first_name", "last_name", "type"};
        Results results = Connect.runQuery(
                /* SELECT */colsUser,
                /* FROM */ new String("user u"),
                new String(" WHERE u.id is ?"), parameters);

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
                String[] new_contact = showContactDialog("");
                if(new_contact != null) {
                    Connect.runInsert("contact_info",
                            new String[]{ "info", "user_id", "type" },
                            new String[]{ "?", "?", "?"},
                            new String[]{new_contact[1], user.getID(), new_contact[0]});
                    model.addRow(new_contact);
                }
            }
        });
        JButton editButton = new JButton("Edit contact");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = contacts.getSelectedRow();
                if(row >= 0){
                    String editVal = contacts.getModel().getValueAt(row, 1).toString();
                    String[] new_contact = showContactDialog(editVal);
                    Connect.runUpdate("contact_info",
                            new String[]{ "info", "type" },
                            new String[]{ "?", "?"},
                            " WHERE info = ?",
                            new String[]{new_contact[1], new_contact[0], editVal});
                    contacts.setValueAt(new_contact[0], row, 0);
                    contacts.setValueAt(new_contact[1], row, 1);
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
                                " WHERE info = ?",
                                new String[]{contacts.getModel().getValueAt(row, 1).toString()});
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
        String[] parameters = new String[]{user.getID()};

        Results results = Connect.runQuery(
                /* SELECT */cols,
                /* FROM */ new String("contact_info ci"),
                new String(" WHERE ci.user_id is ?"), parameters);

        String title = new String("Account");

        JDialog employeesList = setAccountDialog(frame, user, title, results.getRowData(), cols);

        employeesList.setVisible(true);
    }

    public static String[] showContactDialog(String val) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JPanel label = new JPanel(new GridLayout(0, 1, 2, 2));
        label.add(new JLabel("Type:", SwingConstants.RIGHT));
        label.add(new JLabel("Value:", SwingConstants.RIGHT));

        panel.add(label, BorderLayout.WEST);

        String[] parameters = new String[]{};
        JPanel controls = new JPanel(new GridLayout(0, 1, 2, 2));
        Results contact_types = Connect.runQuery(
                new String[]{"type"},
                "contact_info_type", "", parameters);
        DefaultComboBoxModel list = new DefaultComboBoxModel();
        for(int i = 0; i < contact_types.getRowData().length; i++) {
            list.addElement(contact_types.getRowData()[i][0]);
        }
        JComboBox types = new JComboBox(list);
        controls.add(types);

        JTextField value = new JTextField(5);
        value.setText(val);
        controls.add(value);
        panel.add(controls, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(null, panel,
                "Add contact", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            return new String[]{ Objects.requireNonNull(types.getSelectedItem()).toString(), value.getText() };
        }
        return null;
    }

    public static void showAddProjectDialog(JFrame frame) {
        JPanel label = new JPanel(new GridLayout(0, 1, 2, 2));
        label.add(new JLabel("Name", SwingConstants.RIGHT));
        label.add(new JLabel("Start date", SwingConstants.RIGHT));
        label.add(new JLabel("End date", SwingConstants.RIGHT));

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(label, BorderLayout.WEST);

        JPanel controls = new JPanel(new GridLayout(0, 1, 2, 2));
        JTextField name = new JTextField(5);
        controls.add(name);
        SqlDateModel modelStart = new SqlDateModel();
        SqlDateModel modelEnd = new SqlDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl startPanel = new JDatePanelImpl(modelStart, p);
        JDatePanelImpl endPanel = new JDatePanelImpl(modelEnd, p);
        JDatePickerImpl startPicker = new JDatePickerImpl(startPanel, new DateLabelFormatter());
        JDatePickerImpl endPicker = new JDatePickerImpl(endPanel, new DateLabelFormatter());
        controls.add(startPicker);
        controls.add(endPicker);
        panel.add(controls, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(frame, panel,
                "Add new project", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String[] cols = {"name", "start_date", "due_date", "end_date", "selector"};
            String[] vals = {"?", "?", "?", "?", "?"};
            String[] params = {name.getText(), startPicker.getModel().getValue().toString(),
                    endPicker.getModel().getValue().toString(), null, "active"};
            Connect.runInsert("project", cols, vals, params);
            showConfirmDialog(null, "Successfully added new project!", "Success");
        }
    }

    private static JDialog setEditProjectDialog(JFrame frame, String title, String[][] rowData, String[] cols) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        JDialog dialog = new JDialog(frame, title);

        DefaultTableModel model = new DefaultTableModel(rowData, cols);
        JTable projects = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(projects);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JPanel options = new JPanel(new GridLayout(0, 4, 2, 2));
        JButton editButton = new JButton("Edit");
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = projects.getSelectedRow();
                if(row >= 0){
                    showEditSelectedProjDialog(null,
                            projects.getModel().getValueAt(row, 0).toString(),
                            projects.getModel().getValueAt(row, 1).toString());
                } else {
                    DialogHandler.showConfirmDialog(frame,
                            "You did not select any of the contact forms possible!", "Message");
                }
            }
        });
        options.add(editButton, BorderLayout.CENTER);
        JButton addEmpButton = new JButton("Add Employee");
        addEmpButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = projects.getSelectedRow();
                if(row >= 0){

                } else {
                    DialogHandler.showConfirmDialog(frame,
                            "You did not select any of the contact forms possible!", "Message");
                }
            }
        });
        options.add(addEmpButton, BorderLayout.CENTER);
        JButton addTaskButton = new JButton("Add Task");
        addTaskButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = projects.getSelectedRow();
                if(row >= 0){

                } else {
                    DialogHandler.showConfirmDialog(frame,
                            "You did not select any of the contact forms possible!", "Message");
                }
            }
        });
        options.add(addTaskButton, BorderLayout.CENTER);
        JButton finishButton = new JButton("Set as finished");
        finishButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = projects.getSelectedRow();
                if(row >= 0){
                    int check = JOptionPane.showConfirmDialog(null,
                            "Are you sure?", "Finished", JOptionPane.OK_CANCEL_OPTION);

                    if(check == JOptionPane.OK_OPTION) {
                        LocalDateTime now = LocalDateTime.now();
                        String[] cols = {"due_date", "end_date", "selector"};
                        String[] vals = {"?", "?", "?"};
                        String[] params = {null, dtf.format(now), "finished"};
                        Connect.runUpdate("project", cols, vals,
                                " WHERE id = "+"\""+projects.getModel().getValueAt(row, 0)+"\"",
                                params);
                        projects.getModel().setValueAt(null, row, 3);
                        projects.getModel().setValueAt(dtf.format(now), row, 4);
                        projects.getModel().setValueAt("finished", row, 5);
                        showConfirmDialog(null, "Successfully set as finished!", "Success");
                    }
                } else {
                    DialogHandler.showConfirmDialog(frame,
                            "You did not select any of the contact forms possible!", "Message");
                }
            }
        });
        options.add(finishButton, BorderLayout.CENTER);
        dialog.add(options, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(frame);

        return dialog;
    }

    public static void showEditProjectDialog(JFrame frame) {
        String[] cols = new String[]{"id", "name", "start_date", "due_date", "end_date", "selector"};

        String[] parameters = new String[]{};

        Results results = Connect.runQuery(
                /* SELECT */cols,
                /* FROM */ new String("project"),
                "", parameters);

        String title = new String("Edit project");
        JDialog employeesList = setEditProjectDialog(frame, title, results.getRowData(), cols);

        employeesList.setVisible(true);
    }

    public static void showEditSelectedProjDialog(JFrame frame, String id, String projName) {
        JPanel label = new JPanel(new GridLayout(0, 1, 2, 2));
        label.add(new JLabel("Name", SwingConstants.RIGHT));
        label.add(new JLabel("End date", SwingConstants.RIGHT));

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.add(label, BorderLayout.WEST);

        JPanel controls = new JPanel(new GridLayout(0, 1, 2, 2));
        JTextField name = new JTextField(5);
        name.setText(projName);
        controls.add(name);
        SqlDateModel modelEnd = new SqlDateModel();
        Properties p = new Properties();
        p.put("text.today", "Today");
        p.put("text.month", "Month");
        p.put("text.year", "Year");
        JDatePanelImpl endPanel = new JDatePanelImpl(modelEnd, p);
        JDatePickerImpl endPicker = new JDatePickerImpl(endPanel, new DateLabelFormatter());
        controls.add(endPicker);
        panel.add(controls, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(frame, panel,
                "Add new project", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String[] cols = {"due_date"};
            String[] vals = {"?"};
            String[] params = {endPicker.getModel().getValue().toString()};
            Connect.runUpdate("project", cols, vals, " WHERE id = "+id+" ", params);
            showConfirmDialog(null, "Successfully edited project!", "Success");
        }
    }

    public static void showNewEmployeeDialog() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        JPanel rightControls = new JPanel(new GridLayout(0, 1, 2, 2));
        JPanel leftControls = new JPanel(new GridLayout(0, 1, 2, 2));

        rightControls.add(new JLabel("Name:", SwingConstants.LEFT));
        JTextField valueName = new JTextField(10);
        rightControls.add(valueName);
        rightControls.add(new JLabel("Login:", SwingConstants.LEFT));
        JTextField valueLogin = new JTextField(10);
        rightControls.add(valueLogin);
        rightControls.add(new JLabel("Department:", SwingConstants.LEFT));
        String[] parameters = new String[]{};
        Results departs = Connect.runQuery(
                new String[]{"id"},
                "department", "", parameters);
        DefaultComboBoxModel listDeparts = new DefaultComboBoxModel();
        for(int i = 0; i < departs.getRowData().length; i++) {
            listDeparts.addElement(departs.getRowData()[i][0]);
        }
        JComboBox valueDepart = new JComboBox(listDeparts);
        rightControls.add(valueDepart);

        leftControls.add(new JLabel("Last name:", SwingConstants.LEFT));
        JTextField valueSurname = new JTextField(10);
        leftControls.add(valueSurname);
        leftControls.add(new JLabel("Password:", SwingConstants.LEFT));
        JTextField valuePass = new JTextField(10);
        leftControls.add(valuePass);
        leftControls.add(new JLabel("Role:", SwingConstants.LEFT));
        Results roleTypes = Connect.runQuery(
                new String[]{"type"},
                "user_type", "", parameters);
        DefaultComboBoxModel listRoles = new DefaultComboBoxModel();
        for(int i = 0; i < roleTypes.getRowData().length; i++) {
            listRoles.addElement(roleTypes.getRowData()[i][0]);
        }
        JComboBox types = new JComboBox(listRoles);
        leftControls.add(types);

        panel.add(rightControls, BorderLayout.WEST);
        panel.add(leftControls, BorderLayout.CENTER);

        int result = JOptionPane.showConfirmDialog(null, panel,
                "Add employee", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            if(valueName.getText().equals("") || valueSurname.getText().equals("")
                    || valueLogin.getText().equals("") || valuePass.getText().equals("")) {
                showConfirmDialog(null, "Not all fields have been filled up!", "Error");
            } else {
                String[] cols = {"first_name", "last_name", "type", "department_id", "login", "password"};
                String[] vals = {"?", "?", "?", "?", "?", "?"};
                String[] params = {valueName.getText(), valueSurname.getText(), Objects.requireNonNull(types.getSelectedItem()).toString(),
                        Objects.requireNonNull(valueDepart.getSelectedItem()).toString(), valueLogin.getText(), valuePass.getText()};
                Connect.runInsert("user", cols, vals, params);
                showConfirmDialog(null, "Successfully added new employee!", "Success");
            }
        }
    }

    private static JDialog setDeleteEmpDialog(JFrame frame, String title, String[][] rowData, String[] cols) {
        JDialog dialog = new JDialog(frame, title);

        DefaultTableModel model = new DefaultTableModel(rowData, cols);
        JTable accounts = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(accounts);
        dialog.add(scrollPane, BorderLayout.CENTER);

        JPanel options = new JPanel(new GridLayout(0, 1, 2, 2));
        JButton deleteButton = new JButton("Delete account");
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int row = accounts.getSelectedRow();
                if(row >= 0){
                    int check = JOptionPane.showConfirmDialog(null,
                            "Are you sure?", "Delete", JOptionPane.OK_CANCEL_OPTION);

                    if(check == JOptionPane.OK_OPTION) {
                        Connect.runDelete("user",
                                " WHERE id = ?",
                                        new String[]{accounts.getModel().getValueAt(row, 0).toString()});
                        ((DefaultTableModel)accounts.getModel()).removeRow(row);
                    }
                } else {
                    DialogHandler.showConfirmDialog(frame,
                            "You did not select any of the contact forms possible!", "Message");
                }
            }
        });
        options.add(deleteButton, BorderLayout.CENTER);
        dialog.add(options, BorderLayout.SOUTH);

        dialog.pack();
        dialog.setLocationRelativeTo(frame);

        return dialog;
    }

    public static void showDeleteEmpDialog(JFrame frame) {
        String[] cols = new String[]{"id", "first_name", "last_name", "type"};

        String[] parameters = new String[]{};

        Results results = Connect.runQuery(
                /* SELECT */cols,
                /* FROM */ new String("user"),
                "", parameters);

        String title = new String("Delete");
        JDialog employeesList = setDeleteEmpDialog(frame, title, results.getRowData(), cols);

        employeesList.setVisible(true);
    }

    public static void showConfirmDialog(JFrame frame, String message, String title) {
        JOptionPane.showConfirmDialog(frame, message, title,
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
    }
}
