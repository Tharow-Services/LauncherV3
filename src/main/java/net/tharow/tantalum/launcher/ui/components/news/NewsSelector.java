

package net.tharow.tantalum.launcher.ui.components.news;

import net.tharow.tantalum.github.io.RepoReleasesData;
import net.tharow.tantalum.platform.http.HttpPlatformApi;
import net.tharow.tantalum.platform.io.INewsData;
import net.tharow.tantalum.rest.RestObject;
import net.tharow.tantalum.ui.lang.ResourceLoader;
import net.tharow.tantalum.launcher.settings.TantalumSettings;
import net.tharow.tantalum.launcher.ui.LauncherFrame;
import net.tharow.tantalum.ui.controls.list.SimpleScrollbarUI;
import net.tharow.tantalum.ui.controls.feeds.CountCircle;
import net.tharow.tantalum.launcher.ui.controls.feeds.NewsWidget;
import net.tharow.tantalum.launchercore.image.ImageRepository;
import net.tharow.tantalum.platform.IPlatformApi;
import net.tharow.tantalum.platform.io.AuthorshipInfo;
import net.tharow.tantalum.rest.RestfulAPIException;
import net.tharow.tantalum.utilslib.Utils;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Level;

public class NewsSelector extends JPanel {
    private final ResourceLoader resources;
    private final IPlatformApi platformApi;
    private NewsWidget selectedItem;
    private JPanel widgetHost;
    private final CountCircle circle;
    private final TantalumSettings settings;
    private int newLatestNewsArticle;

    private final NewsInfoPanel panel;

    private final ImageRepository<AuthorshipInfo> avatarRepo;

    public NewsSelector(ResourceLoader resources, NewsInfoPanel panel, IPlatformApi platformApi, ImageRepository<AuthorshipInfo> avatarRepo, CountCircle count, TantalumSettings settings) {
        this.resources = resources;
        this.platformApi = platformApi;
        this.avatarRepo = avatarRepo;
        this.panel = panel;
        this.settings = settings;
        this.circle = count;

        initComponents();
        downloadItems();
    }

    protected void selectNewsItem(NewsWidget widget) {
        if (selectedItem != null)
            selectedItem.setIsSelected(false);
        selectedItem = widget;

        if (selectedItem != null)
            selectedItem.setIsSelected(true);

        panel.setArticle(selectedItem.getArticle());
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(LauncherFrame.COLOR_SELECTOR_BACK);

        widgetHost = new JPanel();
        widgetHost.setOpaque(false);
        widgetHost.setLayout(new GridBagLayout());

        JScrollPane scrollPane = new JScrollPane(widgetHost, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUI(new SimpleScrollbarUI(LauncherFrame.COLOR_SCROLL_TRACK, LauncherFrame.COLOR_SCROLL_THUMB));
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10,10));
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        add(scrollPane, BorderLayout.CENTER);

        GridBagConstraints constraints = new GridBagConstraints(0,0,1,1,1.0,0.0,GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0),0,0);

        constraints.weighty = 1.0;
        widgetHost.add(Box.createGlue(), constraints);
    }

    public void ping() {
        settings.setLatestNewsArticle(newLatestNewsArticle);
        circle.setVisible(false);
    }

    protected void loadNewsItems(INewsData news) {

        int count = 0;
        newLatestNewsArticle = settings.getLatestNewsArticle();
        for (int i = 0;i < news.getArticles().size(); i++) {
            if (news.getArticles().get(i).getId() > settings.getLatestNewsArticle()) {
                count++;

                if (news.getArticles().get(i).getId() > newLatestNewsArticle)
                    newLatestNewsArticle = news.getArticles().get(i).getId();
            }
        }

        if (count > 0) {
            circle.setVisible(true);
            circle.setCount(count);
        } else {
            circle.setVisible(false);
        }

        news.getArticles().sort((o1, o2) -> Long.compare(o2.getDate().getTime(), o1.getDate().getTime()));

        widgetHost.removeAll();

        GridBagConstraints constraints = new GridBagConstraints(0,0,1,1,1.0,0.0,GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0),0,0);

        for (int i = 0; i < news.getArticles().size(); i++) {
            @SuppressWarnings("unchecked") NewsWidget widget = new NewsWidget(resources, news.getArticles().get(i), avatarRepo.startImageJob(news.getArticles().get(i).getAuthorshipInfo()));
            widget.addActionListener(e -> {
                if (e.getSource() instanceof NewsWidget)
                    selectNewsItem((NewsWidget)e.getSource());
            });
            widgetHost.add(widget, constraints);
            constraints.gridy++;

            if (selectedItem == null)
                selectNewsItem(widget);
        }

        constraints.weighty = 1.0;
        widgetHost.add(Box.createGlue(), constraints);
    }

    private void downloadItems() {
        Thread thread = new Thread(() -> {
            try {
                loadNewsItems(RepoReleasesData.getRestObject("https://api.github.com/repos/Tharow-Services/Tantalum-Launcher/releases"));
            } catch (RestfulAPIException ex) {
                Utils.getLogger().log(Level.SEVERE, "Unable to load news\n");
                ex.printStackTrace();
            }
        });

        thread.start();
    }
}
