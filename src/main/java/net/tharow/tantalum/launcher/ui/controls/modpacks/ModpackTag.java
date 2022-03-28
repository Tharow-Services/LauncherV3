

package net.tharow.tantalum.launcher.ui.controls.modpacks;

import javax.swing.*;
import java.awt.*;

public class ModpackTag extends JLabel {
    public ModpackTag(String text) {
        super(text);
    }

    @Override
    public Dimension getMinimumSize() { return getPreferredSize(); }

    @Override
    public Dimension getMaximumSize() { return getPreferredSize(); }

    @Override
    public Dimension getPreferredSize() {
        int textSize = getFontMetrics(getFont()).getHeight();
        int textWidth = getFontMetrics(getFont()).stringWidth(getText());
        return new Dimension(textWidth+8, textSize+4);
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;

        g2d.setRenderingHint(
                RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        g2d.setRenderingHint(
                RenderingHints.KEY_FRACTIONALMETRICS,
                RenderingHints.VALUE_FRACTIONALMETRICS_ON);

        g.setColor(getBackground());
        g.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 5, 5);
        g.setColor(getForeground());
        g.setFont(getFont());

        int textY = 1 + ((getHeight()-2 - g2d.getFontMetrics().getHeight()) / 2) + g2d.getFontMetrics().getAscent();

        g.drawString(getText(), 5, textY);
    }
}
