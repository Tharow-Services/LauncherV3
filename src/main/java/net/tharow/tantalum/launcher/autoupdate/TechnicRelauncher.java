/*
 * This file is part of The Technic Launcher Version 3.
 * Copyright ©2015 Syndicate, LLC
 *
 * The Technic Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Technic Launcher  is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Technic Launcher.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tharow.tantalum.launcher.autoupdate;

import net.tharow.tantalum.autoupdate.IUpdateStream;
import net.tharow.tantalum.autoupdate.Relauncher;
import net.tharow.tantalum.ui.lang.ResourceLoader;
import net.tharow.tantalum.launcher.LauncherMain;
import net.tharow.tantalum.launcher.settings.StartupParameters;
import net.tharow.tantalum.launcher.ui.LauncherFrame;
import net.tharow.tantalum.launchercore.install.InstallTasksQueue;
import net.tharow.tantalum.launchercore.install.LauncherDirectories;
import net.tharow.tantalum.launchercore.install.tasks.IInstallTask;
import net.tharow.tantalum.launchercore.install.tasks.TaskGroup;
import net.tharow.tantalum.ui.controls.installation.SplashScreen;
import net.tharow.tantalum.autoupdate.tasks.*;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;

public class TechnicRelauncher extends Relauncher {

    private final ResourceLoader resources;
    private final StartupParameters parameters;
    private final IUpdateStream updateStream;
    private net.tharow.tantalum.ui.controls.installation.SplashScreen screen = null;

    public TechnicRelauncher(IUpdateStream updateStream, int currentBuild, LauncherDirectories directories, ResourceLoader resources, StartupParameters parameters) {
        super(currentBuild, directories);

        this.resources = resources;
        this.parameters = parameters;
        this.updateStream = updateStream;
    }

    @Override
    protected Class getMainClass() {
        return LauncherMain.class;
    }

    @Override
    public String getUpdateText() {
        return resources.getString("updater.launcherupdate");
    }

    @Override
    public boolean isUpdateOnly() {
        return !parameters.isUpdate();
    }

    @Override
    public boolean canReboot() { return !parameters.isBlockReboot(); }

    @Override
    public boolean isMover() {
        return (parameters.isMover() || parameters.isLegacyMover()) && !parameters.isLegacyLauncher();
    }

    @Override
    public boolean isLauncherOnly() {
        return parameters.isLauncher();
    }

    @Override
    public InstallTasksQueue buildMoverTasks() {
        InstallTasksQueue<Object> queue = new InstallTasksQueue<>(null);

        queue.addTask(new MoveLauncherPackage(resources.getString("updater.mover"), new File(parameters.getMoveTarget()), this));
        queue.addTask(new LaunchLauncherMode(resources.getString("updater.finallaunch"), this, parameters.getMoveTarget(), parameters.isLegacyMover()));

        return queue;
    }

    @Override
    public InstallTasksQueue buildUpdaterTasks() {
        screen = new SplashScreen(resources.getImage("launch_splash.png"), 30);
        Color bg = LauncherFrame.COLOR_FORMELEMENT_INTERNAL;
        screen.getContentPane().setBackground(new Color (bg.getRed(),bg.getGreen(),bg.getBlue(),255));
        screen.getProgressBar().setForeground(Color.white);
        screen.getProgressBar().setBackground(LauncherFrame.COLOR_GREEN);
        screen.getProgressBar().setBackFill(LauncherFrame.COLOR_CENTRAL_BACK_OPAQUE);
        screen.getProgressBar().setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 12));
        screen.pack();
        screen.setLocationRelativeTo(null);
        screen.setVisible(true);

        InstallTasksQueue<Object> queue = new InstallTasksQueue<>(screen.getProgressBar());

        ArrayList<IInstallTask> postDownloadTasks = new ArrayList<>();
        postDownloadTasks.add(new LaunchMoverMode(resources.getString("updater.launchmover"), getTempLauncher(), this));

        TaskGroup downloadFilesGroup = new TaskGroup(resources.getString("updater.downloads"));
        queue.addTask(new EnsureUpdateFolders(resources.getString("updater.folders"), getDirectories()));
        queue.addTask(new QueryUpdateStream(resources.getString("updater.query"), updateStream, downloadFilesGroup, getDirectories(), this, postDownloadTasks));
        queue.addTask(downloadFilesGroup);

        return queue;
    }

    @Override
    public String[] getLaunchArgs() {
        String[] launchArgs = new String[parameters.getArgs().length + 1];
        System.arraycopy(parameters.getArgs(), 0, launchArgs, 0, parameters.getArgs().length);
        launchArgs[parameters.getArgs().length] = "-blockReboot";
        return parameters.getArgs();
    }

    @Override
    public void updateComplete() {
        screen.dispose();
    }
}
