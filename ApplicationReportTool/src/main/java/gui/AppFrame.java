package gui;

import database.Query;
import database.Results;
import utils.Credentials;
import utils.DialogHandler;
import utils.ImageLoader;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class AppFrame extends JFrame {

    private Credentials credentials;
    private static final int ROLE = 2;
    private static final int DEPARTMENT = 3;

    public AppFrame() {
        setTitle("SQL project");
        setIconImage(ImageLoader.getImage("icon.png"));
        AppPanel panel = new AppPanel(ImageLoader.getImage("bg.jpg"));
        add(panel);
        pack();
        initKeyListeners();
        setJMenuBar(initMenuBar());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLocationRelativeTo(null);
        setResizable(true);
        setVisible(true);

        login();
    }

    private JMenuBar initMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(initMainMenu());
        menuBar.add(initProjectMenu());
        menuBar.add(initInfoMenu());
        return menuBar;
    }

    private JMenu initMainMenu() {
        JMenu menu = new JMenu("Menu");

        JMenuItem employees = new JMenuItem("Department employees list (1)");
        employees.addActionListener(e -> employeesList());
        menu.add(employees);

        menu.addSeparator();

        JMenuItem timesheet = new JMenuItem("Search for a timesheet (2)");
        timesheet.addActionListener(e -> timesheetList());
        menu.add(timesheet);

        menu.addSeparator();

        JMenuItem filterTimesheet = new JMenuItem("Filter timesheets data (3)");
        filterTimesheet.addActionListener(e -> filterTimesheet());
        menu.add(filterTimesheet);

        menu.addSeparator();

        JMenuItem exit = new JMenuItem("Exit (X)");
        exit.addActionListener(e -> System.exit(0));
        menu.add(exit);

        return menu;
    }

    private JMenu initProjectMenu() {
        JMenu menu = new JMenu("Projects");

        JMenuItem project = new JMenuItem("Show projects");
        project.addActionListener(e -> projectList());
        menu.add(project);

        JMenuItem projectRoles = new JMenuItem("Show role assignments");
        projectRoles.addActionListener(e -> projectRoles());
        menu.add(projectRoles);

        return menu;

    }

    private JMenu initInfoMenu() {
        JMenu menu = new JMenu("Info");

        JMenuItem about = new JMenuItem("Program info (I)");
        about.addActionListener(e -> DialogHandler.showConfirmDialog(this, "SQL project" + '\n' +
                "Version 1.0 " + '\n' + "© 2020", "About"));
        menu.add(about);

        return menu;
    }

    private void initKeyListeners() {
        JFrame frame = this;
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {

            }

            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                switch (keyCode) {
                    case KeyEvent.VK_X -> System.exit(0);
                    case KeyEvent.VK_1 -> employeesList();
                    case KeyEvent.VK_2 -> timesheetList();
                    case KeyEvent.VK_3 -> filterTimesheet();
                    case KeyEvent.VK_I -> DialogHandler.showConfirmDialog(frame, "SQL project" + '\n' +
                            "Version 1.0 " + '\n' + "\u00a9 2020", "About");
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    private void login() {
        this.credentials = DialogHandler.showSignInDialog(this);
        String[] params = new String[]{this.credentials.getLogin(), this.credentials.getPassword()};
        Results results = Query.runQuery(
                /* SELECT */ new String[]{"login", "password", "type", "department_id"},
                /* FROM */ ("user"),
                ("WHERE login is ? and password is ?"),
                null, null, null, params);

        if (results.isEmpty()
                || (!results.getTopResult(ROLE).equals("manager")
                && !results.getTopResult(ROLE).equals("admin"))) {

            login();
        } else {
            this.credentials.setRole(results.getTopResult(ROLE));
            this.credentials.setDepartmentId(results.getTopResult(DEPARTMENT));
        }

    }

    private void employeesList() {
        if (this.credentials.getRole().equals("manager")) {
            DialogHandler.showEmployeesDialog(this, this.credentials.getDepartment_id());
        } else {
            DialogHandler.showEmployeesDialogAdmin(this);
        }
    }

    private void timesheetList() {
        DialogHandler.showTimesheetDialog(this);
    }

    private void filterTimesheet() {
        DialogHandler.showFilteredTimesheetDialog(this);
    }

    private void projectList() {
        DialogHandler.showProjects(this);
    }

    private void projectRoles() {
        DialogHandler.showProjectAssignments(this);
    }
}