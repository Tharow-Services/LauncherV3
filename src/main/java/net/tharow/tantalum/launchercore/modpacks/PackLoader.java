

package net.tharow.tantalum.launchercore.modpacks;

import net.tharow.tantalum.launchercore.install.LauncherDirectories;
import net.tharow.tantalum.launchercore.modpacks.sources.IAuthoritativePackSource;
import net.tharow.tantalum.launchercore.modpacks.sources.IInstalledPackRepository;
import net.tharow.tantalum.launchercore.modpacks.sources.IModpackTagBuilder;
import net.tharow.tantalum.launchercore.modpacks.sources.IPackSource;

import java.util.Collection;

public class PackLoader {
    private final IInstalledPackRepository packRepository;
    private final IAuthoritativePackSource authoritativeSource;
    private final LauncherDirectories directories;

    public PackLoader(LauncherDirectories directories, IInstalledPackRepository packStore, IAuthoritativePackSource packInfos) {
        this.packRepository = packStore;
        this.authoritativeSource = packInfos;
        this.directories = directories;
    }

    public LauncherDirectories getDirectories() {
        return directories;
    }

    public IInstalledPackRepository getPackRepository() {
        return packRepository;
    }

    public IAuthoritativePackSource getAuthoritativeSource() {
        return authoritativeSource;
    }

    public PackLoader(PackLoader packLoader, IAuthoritativePackSource iAuthoritativePackSource){
        this.packRepository = packLoader.packRepository;
        this.authoritativeSource = iAuthoritativePackSource;
        this.directories = packLoader.directories;
    }


    public PackLoadJob createRepositoryLoadJob(IModpackContainer container, Collection<IPackSource> packSources, IModpackTagBuilder tagBuilder, boolean doLoadRepository) {
        PackLoadJob job = new PackLoadJob(directories, packRepository, authoritativeSource, packSources, container, tagBuilder, doLoadRepository);
        Thread thread = new Thread(job);
        thread.start();
        return job;
    }
}
