

package net.tharow.tantalum.launcher.ui.listitems;

import net.tharow.tantalum.launchercore.modpacks.ModpackModel;
import net.tharow.tantalum.ui.lang.ResourceLoader;

public class PackBuildItem {
    private final boolean isRecommended;
    private final boolean isLatest;
    private final String buildNumber;
    private String display;

    public PackBuildItem(String buildNumber, ResourceLoader loader, ModpackModel model) {
        this.buildNumber = buildNumber;
        this.isRecommended = buildNumber.equals(model.getRecommendedBuild());
        this.isLatest = buildNumber.equals(model.getLatestBuild());

        display = buildNumber;

        if (isRecommended)
            display = display + " - " + loader.getString("modpackoptions.build.recommended");
        else if (isLatest)
            display = display + " - " + loader.getString("modpackoptions.build.latest");
    }

    public String getBuildNumber() { return buildNumber; }
    public boolean isRecommended() { return isRecommended; }
    public boolean isLatest() { return isLatest; }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (obj.getClass() != this.getClass())
            return false;
        PackBuildItem item = (PackBuildItem)obj;

        return item.getBuildNumber().equals(buildNumber);
    }

    @Override
    public int hashCode() {
        return getBuildNumber().hashCode();
    }

    @Override
    public String toString() {
        return display;
    }
}
