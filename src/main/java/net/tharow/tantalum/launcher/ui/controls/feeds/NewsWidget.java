

package net.tharow.tantalum.launcher.ui.controls.feeds;

import net.tharow.tantalum.ui.lang.ResourceLoader;
import net.tharow.tantalum.launcher.ui.LauncherFrame;
import net.tharow.tantalum.launcher.ui.controls.SelectorWidget;
import net.tharow.tantalum.launchercore.image.IImageJobListener;
import net.tharow.tantalum.launchercore.image.ImageJob;
import net.tharow.tantalum.platform.io.AuthorshipInfo;
import net.tharow.tantalum.platform.io.NewsArticle;
import net.tharow.tantalum.utilslib.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.util.Calendar;

public class NewsWidget extends SelectorWidget implements IImageJobListener<AuthorshipInfo> {
    private final NewsArticle article;
    private final ImageJob<AuthorshipInfo> avatar;
    private JLabel avatarView;

    public NewsWidget(ResourceLoader resources, NewsArticle article, ImageJob<AuthorshipInfo> avatar) {
        super(resources);

        this.article = article;
        this.avatar = avatar;

        avatar.addJobListener(this);

        initComponents();

        setAvatar(avatar);
    }

    protected void initComponents() {
        super.initComponents();
        setBorder(BorderFactory.createEmptyBorder(8,10,8,15));

        avatarView = new JLabel();
        add(avatarView);

        add(Box.createHorizontalStrut(10));

        JLabel text = new JLabel(article.getTitle());
        text.setFont(getResources().getFont(ResourceLoader.FONT_OPENSANS, 14));
        text.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        text.setPreferredSize(new Dimension(200, text.getPreferredSize().height));
        add(text);

        add(Box.createHorizontalGlue());
        add(Box.createHorizontalStrut(5));

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(article.getDate());

        DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT);
        JLabel date = new JLabel(format.format(article.getDate()));
        date.setFont(getResources().getFont(ResourceLoader.FONT_OPENSANS, 12));
        date.setForeground(LauncherFrame.COLOR_DIM_TEXT);
        add(date);
    }

    public NewsArticle getArticle() { return article; }

    @Override
    public void jobComplete(ImageJob<AuthorshipInfo> job) {
        setAvatar(job);
    }

    private void setAvatar(ImageJob<AuthorshipInfo> job) {
        avatarView.setIcon(new ImageIcon(getResources().getCircleClippedImage(ImageUtils.scaleWithAspectWidth(job.getImage(), 32))));
    }
}
