package net.tharow.tantalum.launchercore.modpacks.sources;

import net.tharow.tantalum.launchercore.modpacks.MemoryModpackContainer;
import net.tharow.tantalum.launchercore.modpacks.ModpackModel;
import net.tharow.tantalum.rest.io.PackInfo;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Objects;

public class NameFilterPackSource implements IPackSource {
    private final MemoryModpackContainer baseModpacks;
    private final String filterTerms;

    public NameFilterPackSource(MemoryModpackContainer modpacks, String filter) {
        this.baseModpacks = modpacks;
        if(filter != null)
            this.filterTerms = filter.toUpperCase();
        else {this.filterTerms = "Direct List";}
    }

    @Override
    public String getSourceName() {
        return "Installed packs filtered by '" + filterTerms + "'";
    }

    @Override
    public Collection<PackInfo> getPublicPacks() {
        LinkedList<PackInfo> info = new LinkedList<>();

        for (ModpackModel modpack : baseModpacks.getModpacks()) {
            if(Objects.equals(filterTerms, "Direct List")){
                info.add(modpack.getPackInfo());
            }
            if (modpack.getDisplayName().toUpperCase().contains(filterTerms)) {
                info.add(modpack.getPackInfo());
            }
        }

        return info;
    }

    @Override
    public int getPriority(PackInfo info) {
        for (ModpackModel modpack : baseModpacks.getModpacks()) {
            if (modpack.getName().equals(info.getName()))
                return modpack.getPriority();
        }

        return 0;
    }
}
