

package net.tharow.tantalum.launcher.ui.controls.modpacks;

import net.tharow.tantalum.launcher.ui.LauncherFrame;
import net.tharow.tantalum.launcher.ui.controls.SelectorWidget;
import net.tharow.tantalum.ui.lang.ResourceLoader;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class FindMoreWidget extends SelectorWidget {
    private final JLabel moreLabel;

    public FindMoreWidget(ResourceLoader resources) {
        super(resources);

        initComponents();
        setIsSelected(true);

        setLayout(new GridBagLayout());
        setBorder(new EmptyBorder(5,0,5,0));
        moreLabel = new JLabel(resources.getString("launcher.packselector.more"), JLabel.CENTER);
        moreLabel.setFont(getResources().getFont(ResourceLoader.FONT_OPENSANS, 14));
        moreLabel.setForeground(LauncherFrame.COLOR_DIM_TEXT);
        moreLabel.setIcon(resources.getIcon("arrow_right.png"));
        moreLabel.setHorizontalTextPosition(SwingConstants.LEADING);
        moreLabel.setIconTextGap(8);
        moreLabel.setOpaque(false);
        add(moreLabel, new GridBagConstraints(0, 0, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));
    }

    public void setWidgetData(String text) {
        moreLabel.setText(text);
    }

    public String getWidgetData() {
        return moreLabel.getText();
    }
}
