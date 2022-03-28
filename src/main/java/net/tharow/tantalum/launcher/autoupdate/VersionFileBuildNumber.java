

package net.tharow.tantalum.launcher.autoupdate;

import net.tharow.tantalum.autoupdate.IBuildNumber;
import net.tharow.tantalum.ui.lang.ResourceLoader;
import org.apache.commons.io.IOUtils;

import java.nio.charset.StandardCharsets;

public class VersionFileBuildNumber implements IBuildNumber {
    private final ResourceLoader resources;

    public VersionFileBuildNumber(ResourceLoader resources) {
        this.resources = resources;
    }

    @Override
    public String getBuildNumber() {
        String build = "0";
        try {
            build = IOUtils.toString(resources.getResourceAsStream("/version"), StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return build.substring(0,3);
    }
}
