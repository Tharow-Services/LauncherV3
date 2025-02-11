

package net.tharow.tantalum.launcher.ui.components.news;

import net.tharow.tantalum.ui.controls.SimpleMouseListener;
import net.tharow.tantalum.ui.lang.ResourceLoader;
import net.tharow.tantalum.launcher.ui.LauncherFrame;
import net.tharow.tantalum.ui.controls.RoundedButton;
import net.tharow.tantalum.ui.controls.list.SimpleScrollbarUI;
import net.tharow.tantalum.launchercore.image.ImageRepository;
import net.tharow.tantalum.platform.io.AuthorshipInfo;
import net.tharow.tantalum.platform.io.NewsArticle;
import net.tharow.tantalum.utilslib.DesktopUtils;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class NewsInfoPanel extends JPanel implements PropertyChangeListener {
    private final ResourceLoader resources;
    private final ImageRepository<AuthorshipInfo> avatarRepo;

    JTextPane newsText;
    JScrollPane newsScroller;
    AuthorshipWidget authorshipInfo;
    JLabel title;

    private String url = "";

    public NewsInfoPanel(ResourceLoader resources, ImageRepository<AuthorshipInfo> avatarRepo) {

        this.resources = resources;
        this.avatarRepo = avatarRepo;

        initComponents();
    }

    public void setArticle(NewsArticle article) {
        if (article == null) {
            newsText.setText("");
            url = "";
            return;
        }

        title.setText(article.getTitle());
        url = article.getUrl();
        newsText.setText("<html><head><style type=\"text/css\">a{color:#309aeb}body{font-family:\"" + newsText.getFont().getFamily() + "\";color:#D0D0D0}</style></head><body>"+
            article.getContent() +
            "</body></html>");

        //noinspection unchecked
        authorshipInfo.setAuthorshipInfo(article.getAuthorshipInfo(), avatarRepo.startImageJob(article.getAuthorshipInfo()));

        EventQueue.invokeLater(() -> newsText.scrollRectToVisible(new Rectangle(0, 0, 1, 1)));
    }

    protected void visitCurrentItem() {
        if (url != null && !url.equals("")) {
            DesktopUtils.browseUrl(url);
        }
    }

    private void initComponents() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(20,20,18,16));
        setBackground(LauncherFrame.COLOR_CENTRAL_BACK_OPAQUE);

        title = new JLabel("");
        title.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        title.setFont(resources.getFont(ResourceLoader.FONT_RALEWAY, 26));
        title.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        title.addMouseListener(new SimpleMouseListener(this::visitCurrentItem));

        add(title, new GridBagConstraints(0, 0, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(0, 0, 0, 0), 0, 0));

        authorshipInfo = new AuthorshipWidget(resources);
        add(authorshipInfo, new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 8, 0), 0, 0));

        newsText = new JTextPane();
        newsText.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        newsText.setOpaque(false);
        newsText.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        newsText.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        newsText.setEditable(false);
        newsText.setHighlighter(null);
        newsText.setAlignmentX(LEFT_ALIGNMENT);
        newsText.setContentType("text/html");
        newsText.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                if (e.getURL() != null)
                    DesktopUtils.browseUrl(e.getURL().toString());
            }
        });
        newsText.addComponentListener(new ComponentListener() {
            @Override
            public void componentResized(ComponentEvent e) {
                NewsInfoPanel.this.revalidate();
            }

            @Override
            public void componentMoved(ComponentEvent e) {

            }

            @Override
            public void componentShown(ComponentEvent e) {

            }

            @Override
            public void componentHidden(ComponentEvent e) {

            }
        });

        newsScroller = new JScrollPane(newsText, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        newsScroller.getVerticalScrollBar().setUI(new SimpleScrollbarUI(LauncherFrame.COLOR_SCROLL_TRACK, LauncherFrame.COLOR_SCROLL_THUMB));
        newsScroller.getVerticalScrollBar().setPreferredSize(new Dimension(10, 10));
        newsScroller.setBorder(BorderFactory.createEmptyBorder());
        newsScroller.setMaximumSize(new Dimension(32000,900));
        newsScroller.setOpaque(false);
        newsScroller.getViewport().setOpaque(false);

        JPanel newsTextPanel = new JPanel();
        newsTextPanel.setLayout(new BoxLayout(newsTextPanel, BoxLayout.PAGE_AXIS));
        newsTextPanel.setOpaque(false);
        newsTextPanel.add(newsScroller);
        newsTextPanel.add(Box.createVerticalGlue());

        add(newsTextPanel, new GridBagConstraints(0, 2, 2, 1, 1.0, 1.0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0, 0, 10, 0), 0, 0));

        add(Box.createGlue(), new GridBagConstraints(0, 3, 1, 1, 1.0, 0.0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        RoundedButton discussButton = new RoundedButton(resources.getString("launcher.news.discuss"));
        discussButton.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        discussButton.setBorder(BorderFactory.createEmptyBorder(5, 17, 10, 17));
        discussButton.setForeground(LauncherFrame.COLOR_BUTTON_BLUE);
        discussButton.setHoverForeground(LauncherFrame.COLOR_BLUE);
        discussButton.setAlignmentX(RIGHT_ALIGNMENT);
        discussButton.setContentAreaFilled(false);
        discussButton.addActionListener(e -> visitCurrentItem());
        add(discussButton, new GridBagConstraints(1, 3, 1, 1, 0.0, 0.0, GridBagConstraints.SOUTHEAST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        invalidate();
    }
}
