

package net.tharow.tantalum.launchercore.modpacks;

public interface IModpackContainer {
    void clear();

    void addModpackToContainer(ModpackModel modpack);

    void replaceModpackInContainer(ModpackModel modpack);

    void refreshComplete();
}
