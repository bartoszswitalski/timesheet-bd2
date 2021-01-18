package gui;

import javax.swing.*;
import java.awt.*;

public class AppPanel extends JPanel {
    private final Image bgImage;

    public AppPanel(Image bgImage) {
        this.bgImage = bgImage;
        setPreferredSize(new Dimension(bgImage.getWidth(this), bgImage.getHeight(this)));
        setOpaque(true);

        setLayout(new BorderLayout());
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        graphics.drawImage(bgImage, 0, 0, null);
    }
}
