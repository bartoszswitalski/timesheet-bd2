package utils;

import javax.swing.*;

public class DialogHandler {
    public static void showConfirmDialog(JFrame frame, String message, String title) {
        JOptionPane.showConfirmDialog(frame, message, title,
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE);
    }
}
