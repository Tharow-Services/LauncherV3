

package net.tharow.tantalum.launcher.autoupdate;

import net.tharow.tantalum.autoupdate.IBuildNumber;
import net.tharow.tantalum.launcher.settings.StartupParameters;

public class CommandLineBuildNumber implements IBuildNumber {
    private final StartupParameters commandLineParams;

    public CommandLineBuildNumber(StartupParameters commandLineParams) {
        this.commandLineParams = commandLineParams;
    }

    @Override
    public String getBuildNumber() {
        return commandLineParams.getBuildNumber();
    }
}
