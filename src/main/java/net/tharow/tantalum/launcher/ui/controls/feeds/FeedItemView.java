

package net.tharow.tantalum.launcher.ui.controls.feeds;

import net.tharow.tantalum.ui.lang.ResourceLoader;
import net.tharow.tantalum.launcher.ui.LauncherFrame;
import net.tharow.tantalum.launcher.ui.components.news.AuthorshipWidget;
import net.tharow.tantalum.launchercore.image.ImageJob;
import net.tharow.tantalum.platform.io.AuthorshipInfo;
import net.tharow.tantalum.platform.io.FeedItem;

import javax.swing.*;
import java.awt.*;

public class FeedItemView extends JButton {
    private final FeedItem feedItem;

    public FeedItemView(ResourceLoader loader, FeedItem feedItem, ImageJob<AuthorshipInfo> avatar) {
        this.setOpaque(false);
        this.setLayout(new GridBagLayout());
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setContentAreaFilled(false);
        this.setFocusable(false);
        this.setBackground(LauncherFrame.COLOR_FEEDITEM_BACK);
        this.setForeground(LauncherFrame.COLOR_HEADER_TEXT);
        this.setFont(loader.getFont(ResourceLoader.FONT_OPENSANS, 12));

        this.feedItem = feedItem;

        add(Box.createVerticalGlue(), new GridBagConstraints(0,0,3,1, 1.0, 1.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0));

        AuthorshipWidget authorship = new AuthorshipWidget(loader, feedItem.getAuthorship(), avatar);
        add(authorship, new GridBagConstraints(0,1,1,1,0.0,0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0));
        authorship.setBounds(0, 0, getWidth(), getHeight());

        add(Box.createHorizontalGlue(), new GridBagConstraints(1,1,1,1,1.0,0.0,GridBagConstraints.CENTER,GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0));

        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public String getUrl() {
        return feedItem.getUrl();
    }

    private Dimension getCalcSize() {
        return new Dimension(250, 132);
    }

    @Override
    public Dimension getPreferredSize() {
        return getCalcSize();
    }

    @Override
    public Dimension getMinimumSize() {
        return getCalcSize();
    }

    @Override
    public Dimension getMaximumSize() {
        return getCalcSize();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
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

        g2d.setColor(getBackground());
        g2d.fillRoundRect(0, 0, 250, 94, 15, 15);

        Shape oldClip = g2d.getClip();
        g2d.clipRect(3, 2, 245, 90);
        g2d.setFont(getFont());
        g2d.setColor(getForeground());

        drawTextUgly(feedItem.getContent(), g2d);
        g2d.setClip(oldClip);
    }

    private void drawTextUgly(String text, Graphics2D g2)
    {
        // Ugly code to wrap text
        String[] arr = text.split(" ");
        int nIndex = 0;
        int startX = 4;
        int startY = 3;
        int lineSize = g2.getFontMetrics().getHeight();
        int elipsisSize = g2.getFontMetrics().stringWidth("...");

        while ( nIndex < arr.length )
        {
            int nextStartY = startY + lineSize;

            if (nextStartY > 92)
                break;

            int nextEndY = nextStartY + lineSize;

            StringBuilder line = new StringBuilder(arr[nIndex++]);
            int lineWidth = g2.getFontMetrics().stringWidth(line.toString());

            while ( ( nIndex < arr.length ) && (lineWidth < 243) )
            {
                line.append(" ").append(arr[nIndex]);
                nIndex++;

                if (nIndex == arr.length)
                    break;

                lineWidth = g2.getFontMetrics().stringWidth(line+" "+arr[nIndex]);
                if (nextEndY >= 92)
                    lineWidth += elipsisSize;
            }

            if (nextEndY >= 92 && nIndex < arr.length)
                line.append("...");

            g2.drawString(line.toString(), startX, startY + g2.getFontMetrics().getAscent());
            startY = nextStartY;
        }
    }
}
