

package net.tharow.tantalum.launcher.ui.controls;

import net.tharow.tantalum.ui.lang.ResourceLoader;
import net.tharow.tantalum.launcher.ui.LauncherFrame;

import javax.swing.*;

public class SelectorWidget extends JButton {

    private final ResourceLoader resources;

    private boolean isSelected;

    public SelectorWidget(ResourceLoader resources) {
        this.resources = resources;
    }

    protected ResourceLoader getResources() { return resources; }

    protected void initComponents() {
        setBorder(BorderFactory.createEmptyBorder());
        setBackground(LauncherFrame.COLOR_SELECTOR_BACK);
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        setContentAreaFilled(false);
        setOpaque(true);
        setFocusPainted(false);
    }

    public boolean isSelected() { return isSelected; }
    public void setIsSelected(boolean isSelected) {
        this.isSelected = isSelected;
        setBackground(isSelected?LauncherFrame.COLOR_SELECTOR_OPTION:LauncherFrame.COLOR_SELECTOR_BACK);
    }
}