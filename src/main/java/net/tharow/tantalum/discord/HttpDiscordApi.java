package net.tharow.tantalum.discord;

import net.tharow.tantalum.discord.io.Server;
import net.tharow.tantalum.launchercore.modpacks.ModpackModel;
import net.tharow.tantalum.rest.RestObject;
import net.tharow.tantalum.rest.RestfulAPIException;

import javax.swing.*;
import java.util.concurrent.ExecutionException;

public class HttpDiscordApi implements IDiscordApi {

    private String url;

    public HttpDiscordApi(String url) {
        this.url = url;
    }

    @Override
    public void retrieveServer(final ModpackModel modpack, final String serverId, final IDiscordCallback callback) {
        new SwingWorker<Server, Server>() {
            @Override
            public Server doInBackground() {
                String guildUrl = url + "servers/" + serverId + "/widget.json";

                try {
                    return RestObject.getRestObject(Server.class, guildUrl);
                } catch (RestfulAPIException e) {
                    e.printStackTrace();
                }

                return null;
            }

            @Override
            public void done() {
                try {
                    Server server = get();

                    callback.serverGetCallback(modpack, server);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            }
        }.execute();
    }
}
