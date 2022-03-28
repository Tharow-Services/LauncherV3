

package net.tharow.tantalum.minecraftcore.launch;

public class LaunchOptions {

    private final String title;
    private final String iconPath;
    private final ILaunchOptions options;

    public LaunchOptions(String title, String iconPath, ILaunchOptions options) {
        this.options = options;
        this.title = title;
        this.iconPath = iconPath;
    }

    public String getTitle() {
        return title;
    }

    public String getIconPath() {
        return iconPath;
    }

    public ILaunchOptions getOptions() {
        return options;
    }

    public void appendToCommands(LaunchCommandCollector commands) {
        if (getTitle() != null) {
            commands.addUnique("--title", title);
        }

        if (options.getLaunchWindowType() == WindowType.FULLSCREEN)
            commands.addUnique("--fullscreen");
        else if (options.getLaunchWindowType() == WindowType.CUSTOM) {
            commands.addUnique("--width", Integer.toString(options.getCustomWidth()));
            commands.addUnique("--height", Integer.toString(options.getCustomHeight()));
        }

        if (getIconPath() != null) {
            commands.addUnique("--icon", getIconPath());
        }
    }

}
