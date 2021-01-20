package gui;

import javax.swing.*;
import java.awt.*;

public class AppPanel extends JPanel {
    private final Image bgImage;

    public AppPanel(Image bgImage) {
        this.bgImage = bgImage;
        setPreferredSize(new Dimension(1200, 800));
        setOpaque(true);

        setLayout(new BorderLayout());
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        graphics.drawImage(bgImage, 0, 0, null);
    }
}
