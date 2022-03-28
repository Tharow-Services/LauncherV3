

package net.tharow.tantalum.launcher.ui.listitems;

import net.tharow.tantalum.launchercore.util.LaunchAction;

public class OnLaunchItem {
    private final String text;
    private final LaunchAction launchAction;

    public OnLaunchItem(String text, LaunchAction action) {
        this.text = text;
        this.launchAction = action;
    }

    public LaunchAction getLaunchAction() { return launchAction; }
    public String toString() { return text; }
}
