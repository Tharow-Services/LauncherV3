

package net.tharow.tantalum.launcher.ui.components.modpacks;

import net.tharow.tantalum.ui.controls.SimpleMouseListener;
import net.tharow.tantalum.ui.lang.ResourceLoader;
import net.tharow.tantalum.launcher.ui.LauncherFrame;
import net.tharow.tantalum.ui.controls.list.SimpleScrollbarUI;
import net.tharow.tantalum.ui.controls.feeds.StatBox;
import net.tharow.tantalum.launchercore.image.IImageJobListener;
import net.tharow.tantalum.launchercore.image.ImageJob;
import net.tharow.tantalum.launchercore.image.ImageRepository;
import net.tharow.tantalum.launchercore.modpacks.ModpackModel;
import net.tharow.tantalum.utilslib.DesktopUtils;
import net.tharow.tantalum.utilslib.ImageUtils;
import net.tharow.tantalum.utilslib.Utils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.event.*;

public class ModpackDataDisplay extends JPanel implements IImageJobListener<ModpackModel> {
    private final ResourceLoader resources;
    private final ImageRepository<ModpackModel> logoRepo;

    private JPanel statBoxes;

    private JLabel titleLabel;
    private JTextPane description;
    private JButton packImage;

    private StatBox ratings;
    private StatBox runs;
    private StatBox downloads;


    private String packSiteUrl;

    private ModpackModel currentModpack;

    public ModpackDataDisplay(ResourceLoader resources, ImageRepository<ModpackModel> logoRepo) {
        this.resources = resources;
        this.logoRepo = logoRepo;

        initComponents();
    }

    @Override
    public Dimension getPreferredSize() {
        Dimension size = super.getPreferredSize();
        return new Dimension(size.width, 225);
    }

    public void setModpack(@NotNull ModpackModel modpack) {
        this.currentModpack = modpack;
        this.packSiteUrl = modpack.getWebSite();

        if (this.packSiteUrl == null)
            this.packSiteUrl = "https://www.tantalum.tharow.net/discover.html";

        Utils.logDebug("Set modpack: "+this.currentModpack.getDisplayName());
        if (this.currentModpack.getDisplayName() == null) {
            titleLabel.setText("Error");
        } else{
            titleLabel.setText(resources.getString("launcher.packstats.title", this.currentModpack.getDisplayName()));
        }

        description.setText(modpack.getDescription());

        boolean wasVisible = ratings.isVisible();
        ratings.setVisible(!modpack.isOfficial());
        statBoxes.setVisible(!modpack.isOfficial());

        if (wasVisible == modpack.isOfficial()) {
            if (wasVisible)
                statBoxes.remove(ratings);
            else
                statBoxes.add(ratings, 0);
        }
        ratings.setValue(modpack.getLikes());
        downloads.setValue(modpack.getDownloads());
        runs.setValue(modpack.getRuns());

        @SuppressWarnings("unchecked") ImageJob<ModpackModel> job = logoRepo.startImageJob(modpack);
        job.addJobListener(this);
        packImage.setIcon(new ImageIcon(ImageUtils.scaleImage(job.getImage(), 370, 220)));

        EventQueue.invokeLater(() -> {
            description.scrollRectToVisible(new Rectangle(new Dimension(1, 1)));
            repaint();

        });
    }

    private void initComponents() {
        BorderLayout packFeatureLayout = new BorderLayout();
        packFeatureLayout.setHgap(10);
        this.setLayout(packFeatureLayout);
        this.setOpaque(false);

        JPanel imagePanel = new JPanel();
        imagePanel.setOpaque(false);
        imagePanel.setAlignmentX(RIGHT_ALIGNMENT);
        imagePanel.setAlignmentY(TOP_ALIGNMENT);
        imagePanel.setBorder(BorderFactory.createEmptyBorder());
        imagePanel.setPreferredSize(new Dimension(370, 220));
        this.add(imagePanel, BorderLayout.LINE_START);

        packImage = new JButton(resources.getIcon("modpack/ModImageFiller.png"));
        packImage.setIcon(resources.getIcon("modpack/ModImageFiller.png"));
        packImage.setAlignmentX(RIGHT_ALIGNMENT);
        packImage.setPreferredSize(new Dimension(370, 220));
        packImage.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        packImage.setBorder(BorderFactory.createEmptyBorder());
        packImage.setContentAreaFilled(false);
        packImage.setFocusPainted(false);
        packImage.addActionListener(e -> DesktopUtils.browseUrl(packSiteUrl));
        imagePanel.add(packImage);

        JPanel packInfoPanel = new JPanel();
        packInfoPanel.setLayout(new GridBagLayout());
        packInfoPanel.setOpaque(false);
        packInfoPanel.setAlignmentY(TOP_ALIGNMENT);
        packInfoPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        this.add(packInfoPanel, BorderLayout.CENTER);

        statBoxes = new JPanel();
        statBoxes.setLayout(new GridLayout(1, 3, 5, 0));
        statBoxes.setOpaque(false);
        statBoxes.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));

        ActionListener listener = e -> DesktopUtils.browseUrl(packSiteUrl);

        ratings = new StatBox(resources, resources.getString("launcher.packstats.ratings"), null);
        ratings.setBackground(LauncherFrame.COLOR_LIKES_BACK);
        ratings.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        ratings.addActionListener(listener);
        statBoxes.add(ratings);

        downloads = new StatBox(resources, resources.getString("launcher.packstats.downloads"), null);
        downloads.setBackground(LauncherFrame.COLOR_FEEDITEM_BACK);
        downloads.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        downloads.addActionListener(listener);
        statBoxes.add(downloads);

        runs = new StatBox(resources, resources.getString("launcher.packstats.runs"), null);
        runs.setBackground(LauncherFrame.COLOR_FEEDITEM_BACK);
        runs.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        runs.addActionListener(listener);
        statBoxes.add(runs);

        packInfoPanel.add(statBoxes, new GridBagConstraints(0, 2, 2, 1, 0.0, 0.0, GridBagConstraints.SOUTH, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        packInfoPanel.add(Box.createHorizontalGlue(), new GridBagConstraints(2, 2, 1, 1, 1.0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        titleLabel = new JLabel(resources.getString("launcher.packstats.title", "Modpack"));
        titleLabel.setFont(resources.getFont(ResourceLoader.FONT_RALEWAY, 24, Font.BOLD));
        titleLabel.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        titleLabel.setHorizontalAlignment(SwingConstants.LEFT);
        titleLabel.setHorizontalTextPosition(SwingConstants.LEFT);
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        titleLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        titleLabel.addMouseListener(new SimpleMouseListener(()->DesktopUtils.browseUrl(packSiteUrl+"/about")));
        packInfoPanel.add(titleLabel, new GridBagConstraints(0,0,4,1,1.0,0.0,GridBagConstraints.NORTH, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0),0,0));

        description = new JTextPane();
        description.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 14));
        description.setOpaque(false);
        description.setEditable(false);
        description.setHighlighter(null);
        description.setAlignmentX(LEFT_ALIGNMENT);
        description.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        description.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        MutableAttributeSet attributes = new SimpleAttributeSet(description.getParagraphAttributes());
        StyleConstants.setLineSpacing(attributes, StyleConstants.getLineSpacing(attributes)*1.3f);
        description.setParagraphAttributes(attributes, true);
        description.addMouseListener(new SimpleMouseListener(()->DesktopUtils.browseUrl(packSiteUrl+"/about")));
        description.setBorder(BorderFactory.createEmptyBorder(8,8,8,8));

        JScrollPane scrollPane = new JScrollPane(description, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUI(new SimpleScrollbarUI(LauncherFrame.COLOR_SCROLL_TRACK, LauncherFrame.COLOR_SCROLL_THUMB));
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10,10));

        JPanel scrollHostPanel = new JPanel();
        scrollHostPanel.setBackground(LauncherFrame.COLOR_FEED_BACK);
        scrollHostPanel.setLayout(new BorderLayout());
        scrollHostPanel.add(scrollPane, BorderLayout.CENTER);

        scrollPane.getVerticalScrollBar().addAdjustmentListener(e -> ModpackDataDisplay.this.repaint());

        packInfoPanel.add(scrollHostPanel, new GridBagConstraints(0,1,4,1,1.0,1.0,GridBagConstraints.NORTH,GridBagConstraints.BOTH, new Insets(5,0,0,0),0,0));
    }

    @Override
    public void jobComplete(ImageJob<ModpackModel> job) {
        if (job.getJobData() == currentModpack) {
            packImage.setIcon(new ImageIcon(ImageUtils.scaleImage(job.getImage(), 370, 220)));
        }
    }
}
