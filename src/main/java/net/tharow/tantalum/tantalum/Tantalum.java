package net.tharow.tantalum.tantalum;

import net.tharow.tantalum.launchercore.logging.Logger;
import net.tharow.tantalum.launchercore.modpacks.InstalledPack;
import net.tharow.tantalum.launchercore.modpacks.sources.IAuthoritativePackSource;
import net.tharow.tantalum.launchercore.modpacks.sources.IInstalledPackRepository;
import net.tharow.tantalum.rest.io.PackInfo;
import net.tharow.tantalum.tantalum.store.TantalumPlatformStore;
import net.tharow.tantalum.utilslib.Utils;
import org.checkerframework.checker.guieffect.qual.UIType;

public class Tantalum implements IAuthoritativePackSource {

    public static Logger logger = Logger.getLogger("Tantalum Platform Source");
    static {
        logger.setParent(Utils.getLogger());
        logger.setLevel(Utils.getLogger().getLevel());
    }
    private TantalumPlatformStore store;

    private IInstalledPackRepository repository;

    @Override
    public PackInfo getPackInfo(InstalledPack pack) {
        return null;
    }

    @Override
    public PackInfo getCompletePackInfo(PackInfo pack) {
        return null;
    }



}
