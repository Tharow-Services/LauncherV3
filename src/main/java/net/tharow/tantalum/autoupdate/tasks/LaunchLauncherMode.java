/*
 * This file is part of Technic Launcher Core.
 * Copyright ©2015 Syndicate, LLC
 *
 * Technic Launcher Core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Technic Launcher Core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * as well as a copy of the GNU Lesser General Public License,
 * along with Technic Launcher Core.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tharow.tantalum.autoupdate.tasks;

import net.tharow.tantalum.autoupdate.Relauncher;
import net.tharow.tantalum.launchercore.install.InstallTasksQueue;
import net.tharow.tantalum.launchercore.install.tasks.IInstallTask;

public class LaunchLauncherMode implements IInstallTask {

    private final String description;
    private final Relauncher relauncher;
    private final String launchTarget;
    private final boolean isLegacy;

    public LaunchLauncherMode(String description, Relauncher relauncher, String launchTarget, boolean isLegacy) {
        this.description = description;
        this.relauncher = relauncher;
        this.launchTarget = launchTarget;
        this.isLegacy = isLegacy;
    }

    @Override
    public String getTaskDescription() {
        return description;
    }

    @Override
    public float getTaskProgress() {
        return 0;
    }

    @Override
    public void runTask(InstallTasksQueue queue) {
        String[] args = relauncher.buildLauncherArgs(isLegacy);
        relauncher.launch(launchTarget, args);
    }
}
