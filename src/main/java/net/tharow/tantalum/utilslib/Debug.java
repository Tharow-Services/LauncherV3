package net.tharow.tantalum.utilslib;

import net.tharow.tantalum.authlib.AuthlibUser;
import net.tharow.tantalum.authlib.IAuthlibServerInfo;
import net.tharow.tantalum.authlib.io.ServerInfo;
import net.tharow.tantalum.launcher.io.*;
import net.tharow.tantalum.launchercore.auth.IUserType;
import net.tharow.tantalum.launchercore.exception.BuildInaccessibleException;
import net.tharow.tantalum.launchercore.install.LauncherDirectories;
import net.tharow.tantalum.launchercore.modpacks.*;
import net.tharow.tantalum.launchercore.modpacks.sources.IAuthoritativePackSource;
import net.tharow.tantalum.launchercore.modpacks.sources.IPackSource;
import net.tharow.tantalum.minecraftcore.microsoft.auth.MicrosoftUser;
import net.tharow.tantalum.minecraftcore.mojang.auth.MojangUser;
import net.tharow.tantalum.minecraftcore.mojang.auth.io.Profile;
import net.tharow.tantalum.platform.http.HttpPlatformApi;
import net.tharow.tantalum.platform.io.AuthorshipInfo;
import net.tharow.tantalum.platform.io.FeedItem;
import net.tharow.tantalum.platform.io.PlatformPackInfo;
import net.tharow.tantalum.platform.packsources.FeaturedPackSource;
import net.tharow.tantalum.rest.io.Mod;
import net.tharow.tantalum.rest.io.Modpack;
import net.tharow.tantalum.rest.io.PackInfo;
import net.tharow.tantalum.solder.SolderPackSource;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Logger;

public class Debug {



    public static void getObjectConfig(Object object){
        if(object instanceof IUserType userType){getUserConfig(userType);}
        if(object instanceof IStore store){getStoreConfig(store);}
        if(object instanceof MemoryModpackContainer container){getMemoryModpackContainerConfig(container);}
        if(object instanceof ModpackModel model){getModpackModelConfig(model);}
        if(object instanceof PackInfo info){getPackInfoConfig(info);}
        if(object instanceof Platform platform){getPlatformInfo(platform);}
        if(object instanceof Profile profile){getProfile(profile);}
        if(object instanceof ServerInfo serverInfo){getAuthlibServerConfig(serverInfo);}
        if(object instanceof InstalledPack pack){getInstalledPackConfig(pack);}
    }

    public static void getStoreConfig(IStore config){
        if(config instanceof TantalumPlatformStore store){getPlatformStoreConfig(store);}
        if(config instanceof TantalumUserStore store){getUserStoreConfig(store);}
        if(config instanceof TechnicInstalledPackStore store){getInstalledPackStoreConfig(store);}

    }

    private static void getUserStoreConfig(@NotNull TantalumUserStore store){
        config(0,"User Store Configuration");
        store.getSavedUsers().forEach(Debug::getUserConfig);
    }

    private static void getProfile(Profile profile){getProfile(profile,0);}
    private static void getProfile(@NotNull Profile item, int i){
        config(i,"Profile");
        config(i+1,"ID",item.getId());
        config(i+1,"Name",item.getName());
        config(i+1,"Legacy",item.isLegacy());
    }

    private static void getUserConfig(IUserType userType){getUserConfig(userType,0);}
    private static void getUserConfig(IUserType userType, int i){
        if (userType instanceof AuthlibUser user) {
            config(i,"Authlib User");
            config(i+1,"Offline",user.isOffline());
            config(i+1,"Username",user.getUsername());
            config(i+1,"Access Token",user.getAccessToken());
            config(i+1,"Client Token",user.getClientToken());
            config(i+1,"Display Name",user.getDisplayName());
            getProfile(user.getProfile(),i+1);
            config(i+1,"User Properties");
            user.properties().getInternalMap().forEach((o, o2) -> property(o,o2,i+1));
            config(i+1,"Authlib Server Url" +user.getServerUrl());
            getAuthlibServerConfig((ServerInfo) user.getAuthlibServerInfo(),i+1);

        }
        if (userType instanceof MojangUser user) {
            config(i,"Mojang User",user.getDisplayName());
            config(i+1,"Offline",user.isOffline());
            config(i+1,"Username",user.getUsername());
            config(i+1,"Access Token",user.getAccessToken());
            config(i+1,"Client Token",user.getClientToken());
            config(i+1,"Display Name",user.getDisplayName());
            getProfile(user.getProfile(),i+1);
            config(i+1,"User Proprieties");
            user.getProperties().getInternalMap().forEach(Debug::property);

        }
        if (userType instanceof MicrosoftUser user) {
            config(i,"Microsoft User",user.getDisplayName());
            config(i+1,"Offline", user.isOffline());
            config(i+1,"Username",user.getUsername());
            config(i+1,"Access Token",user.getAccessToken().substring(0,12));
            config(i+1,"Display Name",user.getDisplayName());

        }

    }

    private static void property(String name, String value){property(name, value, 0);}
    private static void property(String name, String value, int indent){
        config(indent,"Property",name);
        config(indent,"Value",value);

    }


    private static void getPlatformStoreConfig(final @NotNull TantalumPlatformStore store){
        Utils.getLogger().config("Platform Store Configuration: ");
        store.getHosts().forEach(host-> Debug.getPlatformInfo(store.getHostPlatform(host)));
    }

    private static void getPlatformInfo(Platform platform){getPlatformInfo(platform,0);}
    private static void getPlatformInfo(@NotNull Platform item, int i){
        config(i,"Platform Host",item.getHost());
        config(i+1,"Platform Name",item.getName());
        config(i+1,"Platform Build Number",String.valueOf(item.getBuild()));
        config(i+1,"Platform Version",item.getVersion());
        config(i+1,"Platform Url",item.getUrl());
    }

    private static void getInstalledPackStoreConfig(@NotNull TechnicInstalledPackStore store){
        config(0,"Installed Pack Store Configuration");
        store.getInstalledPacks().forEach(Debug::getInstalledPackConfig);
    }

    private static void getInstalledPackConfig(InstalledPack pack){getInstalledPackConfig(pack,0);}
    private static void getInstalledPackConfig(String slug,InstalledPack pack){getInstalledPackConfig(slug,pack,0);}
    private static void getInstalledPackConfig(InstalledPack pack,int indent){getInstalledPackConfig(pack.getName(),pack,indent);}
    private static void getInstalledPackConfig(String slug, @NotNull InstalledPack pack,int indent){
        //Check to make sure that the slugs match
        if(!Objects.equals(slug, pack.getName())){
            Utils.getLogger().severe("Some how the provided slug didn't match the pack we were given");
            return;
        }
        config(indent,"Installed Pack",pack.getName());
        config(indent+1,"Name",pack.getName());
        config(indent+1,"Build",pack.getBuild());
        config(indent+1,"Directory",pack.getDirectory());
    }

    private static void getMemoryModpackContainerConfig(@NotNull MemoryModpackContainer container){
        config(0,"MemoryModpackContainerConfig");
        container.getModpacks().forEach(model -> getModpackModelConfig(model,1));
    }
    //config(i+1,"",item);
    private static void getPackInfoConfig(PackInfo pack){getPackInfoConfig(pack,0);}
    private static void getPackInfoConfig(@NotNull PackInfo item, int i){
        config(i,"Modpack Info",item.getDisplayName());
        config(i+1,"Name",item.getName());
        config(i+1,"Display Name",item.getDisplayName());
        config(i+1,"Description",item.getDescription());
        config(i+1,"Local",item.isLocal());
        config(i+1,"Complete",item.isComplete());
        config(i+1,"Official",item.isOfficial());
        config(i+1,"Server",item.isServerPack());
        config(i+1,"Solder",item.hasSolder());
        config(i+1,"Number of Downloads",item.getDownloads());
        config(i+1,"Number of Runs",item.getRuns());
        config(i+1,"Number of Likes",item.getLikes());
        config(i+1,"Builds");
        config(i+2,"Latest",item.getLatest());
        config(i+2,"Recommended",item.getRecommended());
        if(item.hasSolder()) {
            for (String s : item.getBuilds()) {
                try {
                    config(i + 1, "Build", s);
                    getModpackConfig(item.getModpack(s), i + 2);
                } catch (BuildInaccessibleException | NullPointerException exception) {
                    config(i + 2, "Modpack", "Build Inaccessible");
                }
            }
        }
        if (item.getFeed() != null) {item.getFeed().forEach(feed->getFeedConfig(feed,i+1));}
        else{config(i+1,"Feed","feed is empty");}
    }

    private static void getFeedConfig(@NotNull FeedItem item, int i){
        config(i,"Feed");
        config(i+1,"User",item.getUser());
        config(i+1,"Avatar",item.getAvatar());
        config(i+1,"Date",item.getDate());
        config(i+1,"Url",item.getUrl());
        getAuthorShipConfig(item.getAuthorship(),i+1);
        config(i+1,"Content",item.getContent());
    }

    private static void getAuthorShipConfig(@NotNull AuthorshipInfo item, int i){
        config(i,"Author Ship Info");
        config(i+1,"User",item.getUser());
        config(i+1,"Avatar",item.getAvatar());
        config(i+1,"Date",item.getDate());
    }

    private static void getPlatformPackInfo(PlatformPackInfo item){getPlatformPackInfo(item,0);}
    private static void getPlatformPackInfo(@NotNull PlatformPackInfo item, int i)
    {
        if (item.hasError()) {
            config(i,"Has Error");
            config(i,"Modpack Model",item.getError());
            return;
        }
        config(i,"Modpack Model",item.getDisplayName());
        config(i+1,"Name",item.getName());
        config(i+1,"Display Name",item.getDisplayName());
        config(i+1,"Description",item.getDescription());
        config(i+1,"Local Only",item.isLocal());
        config(i+1,"Official",item.isOfficial());
        config(i+1,"Minecraft Version",item.getGameVersion());
        if(item.getForge() != null){config(i+1,"Forge",item.getForge());}
        else{config(i+1,"Forge","Not Forge");}
        config(i+1,"Server",item.isServerPack());
        config(i+1,"Number of Downloads",item.getDownloads());
        config(i+1,"Number of Runs",item.getRuns());
        config(i+1,"Number of Likes",item.getLikes());
        config(i+1,"Solder",item.hasSolder());
        if (item.hasSolder()) {
            config(i+1,"Solder Url",item.getSolder());
        }
        config(i+1,"Builds");
        item.getBuilds().forEach(build->config(i+2,"Build",build));
        if (item.getFeed() != null) {item.getFeed().forEach(feed->getFeedConfig(feed, i+1));}
        else{config(i+1,"There is no feed");}

    }

    private static void getBuilds(PackInfo item, int i){

    }

    private static void getModpackConfig(Modpack item,int i){
        config(i,"Modpack");
        config(i+1,"Minecraft",item.getGameVersion());
        config(i+1,"Java",item.getJava());
        config(i+1,"Memory",item.getMemory());
        if(item.getMods() != null)
            item.getMods().forEach(mod->getModConfig(mod, i+1));
    }
//config(i+1,"",item);
    private static void getModConfig(Mod item, int i){
        config(i,"Mod");
        config(i+1,"Name",item.getName());
        config(i+1,"Version",item.getVersion());
        config(i+1,"MD5",item.getMd5());
        config(i+1,"URL",item.getUrl());
    }


    private static void getModpackModelConfig(ModpackModel model){getModpackModelConfig(model,0);}
    private static void getModpackModelConfig(@NotNull ModpackModel item, int i){
        config(i,"Modpack Model",item.getDisplayName());
        config(i+1,"Name",item.getName());
        config(i+1,"Display Name",item.getDisplayName());
        config(i+1,"Description",item.getDescription());
        config(i+1,"Local Only",item.isLocalOnly());
        config(i+1,"Official",item.isOfficial());
        config(i+1,"Selected",item.isSelected());
        config(i+1,"Server",item.isServerPack());
        config(i+1,"Number of Downloads",item.getDownloads());
        config(i+1,"Number of Runs",item.getRuns());
        config(i+1,"Number of Likes",item.getLikes());
        config(i+1,"Priority",item.getPriority());
        config(i+1,"Has Recommended Update",item.hasRecommendedUpdate());
        if (item.getInstalledPack() != null) {
            getInstalledPackConfig(item.getInstalledPack(),i+1);
            config(i+1,"Installed Version",item.getInstalledVersion());
            config(i+1,"Installed Directory",item.getInstalledDirectory());
            config(i+1,"Directories");
            config(i+2,"Bin",item.getBinDir());
            config(i+2,"Cache",item.getCacheDir());
            config(i+2,"Config",item.getConfigDir());
            config(i+2,"Saves",item.getSavesDir());
            config(i+2,"Mods",item.getModsDir());
            config(i+2,"Core Mods",item.getCoremodsDir());
            config(i+2,"Resources",item.getResourcesDir());
        }else {
            config(i+1,"Installed Pack Info","Not Installed");
        }
        if(item.getTags() != null){config(i+1,"Tags");
            item.getTags().forEach(tag->config(i+2,"Tag",tag));}
        else{config(i+1,"Tags","Doesn't Have Any Tags");}
        config(i+1,"Builds");
        config(i+2,"Current",item.getBuild());
        config(i+2,"Latest",item.getLatestBuild());
        config(i+2,"Recommended",item.getRecommendedBuild());
        item.getBuilds().forEach(build->config(i+2,"Build",build));
        if (item.getFeed() != null) {item.getFeed().forEach(feed->getFeedConfig(feed, i+1));}
        else{config(i+1,"There is no feed");}
        getPackInfoConfig(item.getPackInfo(),i+1);

    }

    private static void getAuthlibServerConfig(ServerInfo info){getAuthlibServerConfig(info,0);}
    private static void getAuthlibServerConfig(@NotNull ServerInfo item, int i){
        config(i,"Authlib Authentication Server");
        config(i+1,"Server Name",item.getServerName());
        config(i+1,"Implementation Name",item.getImplName());
        config(i+1,"Implementation Version",item.getImplVersion());
        config(i+1,"Home Page Url",item.getHomepage());
        config(i+1,"Register Page Url",item.getRegisterPage());
        config(i+1,"Supports Non Email Login",!item.isEmailLogin());
        config(i+1,"Uses Legacy Skin Api",item.isLegacySkinApi());
        config(i+1,"Uses Mojang Name Space",item.isMojangNamespace());
        config(i+1,"Skin Domains",item.getSkinDomains());
        if(item.getPublicKey() == null){
            config(i+1,"Public key","Is Null");
        } else{
            config(i+1,"Public Key",item.getPublicKey().substring(0,12));
        }

    }

    private static void config(int level, String name){
        config(level,name,"");
    }

    private static void config(int level, String name, Object valued)
    {
        String value = String.valueOf(valued);
        StringBuilder indent = new StringBuilder();
        indent.append("  ".repeat(level));
        if (value != null) {
            Utils.getLogger().config(indent.append(name).append(": ").append(value).toString());
        } else {
            Utils.getLogger().config(indent.append(name).append(":").toString());
        }

    }
    public static void getInstalledConfig(final IAuthoritativePackSource source, final TechnicInstalledPackStore store){
        getConfig(store);
        store.getInstalledPacks().forEach((String name, InstalledPack pack)-> getPackInfoConfig(source.getCompletePackInfo(source.getPackInfo(pack))));
    }

    public static void getPackSourceConfig(final IAuthoritativePackSource source, final IPackSource source2){
        source2.getPublicPacks().forEach(pack->getPackInfoConfig(source.getCompletePackInfo(pack)));
    }


    /*
    public static void getInstalledConfig(final PackLoader packLoader){
        final var installedPackRepository = packLoader.getPackRepository();
        final var authoritativeSource = packLoader.getAuthoritativeSource();
        final var directories = packLoader.getDirectories();
        final var platformApi = authoritativeSource.
        ArrayList<IPackSource> sources = new ArrayList<>(2);
        sources.add(new FeaturedPackSource("http://platform.test"));
        sources.add(new SolderPackSource("http://solder.almuramc.com/api/"))
        store.getPackNames().forEach(packName->{
            var container = new MemoryModpackContainer();

        });
    }*/
    //Start of Get Config Methods//
    public static void getConfig(IUserType userType){getUserConfig(userType);}
    public static void getConfig(IStore store){getStoreConfig(store);}
    public static void getConfig(MemoryModpackContainer container){getMemoryModpackContainerConfig(container);}
    public static void getConfig(IModpackContainer container){getMemoryModpackContainerConfig((MemoryModpackContainer) container);}
    public static void getConfig(ModpackModel model){getModpackModelConfig(model);}
    public static void getConfig(PackInfo info){getPackInfoConfig(info);}
    public static void getConfig(Platform platform){getPlatformInfo(platform);}
    public static void getConfig(Profile profile){getProfile(profile);}
    public static void getConfig(ServerInfo serverInfo){getAuthlibServerConfig(serverInfo);}
    public static void getConfig(InstalledPack pack){getInstalledPackConfig(pack);}
    public static void getConfig(TantalumPlatformStore store){getPlatformStoreConfig(store);}
    public static void getConfig(TantalumUserStore store){getUserStoreConfig(store);}
    public static void getConfig(TechnicInstalledPackStore store){getInstalledPackStoreConfig(store);}
    //End of get Config Methods//




}
