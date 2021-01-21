package gui;

import core.Connect;
import utils.DialogHandler;
import utils.ImageLoader;
import utils.User;
import core.Results;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class AppFrame extends JFrame {

    private User user;

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
        menuBar.add(initTimeMenu());
        menuBar.add(initProjectMenu());
        menuBar.add(initUserMenu());
        return menuBar;
    }

    private JMenu initMainMenu() {
        JMenu menu = new JMenu("Menu");

        JMenuItem account = new JMenuItem("My account (A)");
        account.addActionListener(e -> manageAccount());
        menu.add(account);

        menu.addSeparator();

        JMenuItem about = new JMenuItem("About (I)");
        about.addActionListener(e -> DialogHandler.showConfirmDialog(this, "Timesheet Access Tool" + '\n' +
                "Version 1.0 " + '\n' + "© 2021", "About"));
        menu.add(about);

        menu.addSeparator();

        JMenuItem exit = new JMenuItem("Exit (X)");
        exit.addActionListener(e -> System.exit(0));
        menu.add(exit);

        return menu;
    }

    private JMenu initTimeMenu() {
        JMenu menu = new JMenu("Time");

        JMenuItem registerTime = new JMenuItem("Register time (T)");
        registerTime.addActionListener(e -> registerTime());
        menu.add(registerTime);

        return menu;
    }

    private JMenu initProjectMenu() {
        JMenu menu = new JMenu("Projects");

        JMenuItem addProject = new JMenuItem("Add project (P)");
        addProject.addActionListener(e -> addProject());
        menu.add(addProject);

        menu.addSeparator();

        JMenuItem editProject = new JMenuItem("Edit project (E)");
        editProject.addActionListener(e -> editProject());
        menu.add(editProject);

        return menu;
    }

    private JMenu initUserMenu() {
        JMenu menu = new JMenu("Users");

        JMenuItem addUser = new JMenuItem("Add user (U)");
        addUser.addActionListener(e -> addUser());
        menu.add(addUser);

        menu.addSeparator();

        JMenuItem removeUser = new JMenuItem("Remove user (R)");
        removeUser.addActionListener(e -> removeUser());
        menu.add(removeUser);

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
                    case KeyEvent.VK_A -> manageAccount();
                    case KeyEvent.VK_T -> registerTime();
                    case KeyEvent.VK_P -> addProject();
                    case KeyEvent.VK_E -> editProject();
                    case KeyEvent.VK_U -> addUser();
                    case KeyEvent.VK_R -> removeUser();
                    case KeyEvent.VK_I -> DialogHandler.showConfirmDialog(frame, "Timesheet Access Tool" + '\n' +
                            "Version 1.0 " + '\n' + "© 2021", "About");
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    private void login() {
        this.user = DialogHandler.showSignInDialog(this);
        Results results = Connect.runQuery(
                /* SELECT */ new String[]{"id", "login", "password", "type", "department_id"},
                /* FROM */ new String("user"),
                new String("WHERE login is \"" + this.user.getLogin()
                        + "\" and password is \"" + this.user.getPassword() + "\""));

        //this.user.setDepartmentId(results.getTopResult(3));
        this.user.setID(results.getTopResult(0));
        this.user.setRole(results.getTopResult(2));

        if (results.isEmpty()) {
            login();
        }

    }

    private void registerTime() {
        DialogHandler.showTimeRegistrationDialog(this);
    }

    private void addProject() {
        DialogHandler.showConfirmDialog(this, "Action will be implemented in the future",
                "Placeholder");
    }

    private void editProject() {
        DialogHandler.showConfirmDialog(this, "Action will be implemented in the future",
                "Placeholder");
    }

    private void manageAccount() {
        DialogHandler.showAccountDialog(this, user);
    }

    private void addUser() {
        if(user.getRole() != "admin") {
            DialogHandler.showConfirmDialog(this, "You are not allowed to use this function!",
                    "Message");
        } else {
            DialogHandler.showConfirmDialog(this, "Action will be implemented in the future",
                    "Placeholder");
        }
    }

    private void removeUser() {
        if(user.getRole() != "admin") {
            DialogHandler.showConfirmDialog(this, "You are not allowed to use this function!",
                    "Message");
        } else {
            DialogHandler.showConfirmDialog(this, "Action will be implemented in the future",
                    "Placeholder");
        }
    }
}