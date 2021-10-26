package net.tharow.tantalum.discord;

import net.tharow.tantalum.launchercore.modpacks.ModpackModel;

public interface IDiscordApi {
    void retrieveServer(ModpackModel modpack, String serverId, IDiscordCallback callback);
}
