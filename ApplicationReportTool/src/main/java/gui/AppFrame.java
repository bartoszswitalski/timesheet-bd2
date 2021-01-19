package gui;

import utils.Credentials;
import utils.DialogHandler;
import utils.ImageLoader;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class AppFrame extends JFrame {
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
        menuBar.add(initInfoMenu());
        return menuBar;
    }

    private JMenu initMainMenu() {
        JMenu menu = new JMenu("Menu");

        JMenuItem startLocalGame = new JMenuItem("Action 1 (1)");
        startLocalGame.addActionListener(e -> action1());
        menu.add(startLocalGame);

        menu.addSeparator();

        JMenuItem hostOnlineGame = new JMenuItem("Action 2 (2)");
        hostOnlineGame.addActionListener(e -> action2());
        menu.add(hostOnlineGame);

        menu.addSeparator();

        JMenuItem joinOnlineGame = new JMenuItem("Action 3 (3)");
        joinOnlineGame.addActionListener(e -> action3());
        menu.add(joinOnlineGame);

        menu.addSeparator();

        JMenuItem exit = new JMenuItem("Exit (X)");
        exit.addActionListener(e -> System.exit(0));
        menu.add(exit);

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
                    case KeyEvent.VK_1 -> action1();
                    case KeyEvent.VK_2 -> action2();
                    case KeyEvent.VK_3 -> action3();
                    case KeyEvent.VK_I -> DialogHandler.showConfirmDialog(frame, "SQL project" + '\n' +
                            "Version 1.0 " + '\n' + "© 2020", "About");
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });
    }

    private void login() {
        Credentials credentials = DialogHandler.showSignInDialog(this);
    }

    private void action1() {
        DialogHandler.showConfirmDialog(this, "Action will be implemented in the future",
                "Placeholder 1");
    }

    private void action2() {
        DialogHandler.showConfirmDialog(this, "Action will be implemented in the future",
                "Placeholder 2");
    }

    private void action3() {
        DialogHandler.showConfirmDialog(this, "Action will be implemented in the future",
                "Placeholder 3");
    }

}