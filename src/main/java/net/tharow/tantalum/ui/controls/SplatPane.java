package net.tharow.tantalum.ui.controls;

import javax.swing.*;
import java.awt.*;

public class SplatPane extends JComponent {
    private final JComponent hoverTarget;
    private final Icon image;
    private final int alignment;
    private final int offsetX;
    private final int offsetY;

    public SplatPane(JComponent hoverTarget, Icon image, int alignment, int offsetX, int offsetY) {
        this.hoverTarget = hoverTarget;
        this.image = image;
        this.alignment = alignment;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }

    @Override
    public void paintComponent(Graphics g) {

        int x = hoverTarget.getX();
        int y = hoverTarget.getY();
        int width = hoverTarget.getWidth();
        int height = hoverTarget.getHeight();

        x -= offsetX;
        y -= offsetY;
        width += (offsetX*2);
        height += (offsetY*2);

        int splatWidth = this.image.getIconWidth();
        int splatHeight = this.image.getIconHeight();

        int widthDiff = width - splatWidth;
        int heightDiff = height - splatHeight;

        if (this.alignment == JLabel.NORTH || this.alignment == JLabel.SOUTH)
            x = x + (widthDiff/2);
        if (this.alignment == JLabel.EAST || this.alignment == JLabel.WEST)
            y = y + (heightDiff/2);
        if (this.alignment == JLabel.NORTH_EAST || this.alignment == JLabel.EAST || this.alignment == JLabel.SOUTH_EAST)
            x += widthDiff;
        if (this.alignment == JLabel.SOUTH_EAST || this.alignment == JLabel.SOUTH || this.alignment == JLabel.SOUTH_WEST)
            y += heightDiff;

        g.clipRect(x, y, width, height);
        this.image.paintIcon(this, g, x, y);
    }
}
