package net.tharow.tantalum.discord;

import net.tharow.tantalum.discord.io.Server;
import net.tharow.tantalum.launchercore.modpacks.ModpackModel;

public interface IDiscordCallback {
    void serverGetCallback(ModpackModel pack, Server server);
}
