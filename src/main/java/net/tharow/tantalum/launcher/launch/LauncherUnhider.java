

package net.tharow.tantalum.launcher.launch;

import net.tharow.tantalum.launcher.settings.TantalumSettings;
import net.tharow.tantalum.launcher.ui.LauncherFrame;
import net.tharow.tantalum.launchercore.launch.ProcessExitListener;
import net.tharow.tantalum.launchercore.util.LaunchAction;

import java.awt.*;

public class LauncherUnhider implements ProcessExitListener {

    private final TantalumSettings settings;
    private final LauncherFrame frame;
    private boolean hasExited = false;

    public LauncherUnhider(TantalumSettings settings, LauncherFrame frame) {
        this.settings = settings;
        this.frame = frame;
    }

    @Override
    public void onProcessExit() {
        LaunchAction action = settings.getLaunchAction();
        if (action == null || action == LaunchAction.HIDE) {
            frame.setVisible(true);
        }

        hasExited = true;
        EventQueue.invokeLater(frame::launchCompleted);
    }

    public boolean hasExited() {
        return hasExited;
    }
}