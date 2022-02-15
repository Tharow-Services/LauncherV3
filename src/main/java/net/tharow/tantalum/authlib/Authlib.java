package net.tharow.tantalum.authlib;

import net.tharow.tantalum.authlib.io.AuthServerStore;
import net.tharow.tantalum.launcher.io.GenericStore;
import net.tharow.tantalum.launchercore.logging.Level;
import net.tharow.tantalum.launchercore.logging.Logger;
import net.tharow.tantalum.rest.RestfulAPIException;
import net.tharow.tantalum.utilslib.Utils;
import org.checkerframework.checker.units.qual.A;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.function.BiFunction;

public class Authlib {
    public static final Logger LOGGER = Logger.getLogger("Authlib");
    public static final UUID AUTHLIB_TOKEN = UUID.randomUUID();
    static {
        LOGGER.setParent(Utils.getLogger());
        LOGGER.setLevel(LOGGER.getParent().getLevel());
    }
    public static final String API_LOCATION_NOT_GENERATED = "API_LOCATION_NOT_GENERATED";


    private final AuthServerStore authlibServer;

    public Authlib(File authlibServerStore){
        this.authlibServer = AuthServerStore.load("Authlib-Store",authlibServerStore);
        init();
    }

    public AuthlibServer loadServer(UUID uuid) throws NoSuchElementException {
        if(!this.authlibServer.getNames().contains(uuid)){throw new NoSuchElementException("A Server With That UUID Couldn't Be Found");}
        return this.authlibServer.getMap().get(uuid);
    }

    public AuthlibServer newServer(@NotNull URL url) throws RestfulAPIException {
        final AuthlibServer server = new AuthlibServer(url.toString(), AUTHLIB_TOKEN.toString());
        if(!this.authlibServer.getNames().contains(server.get())){this.authlibServer.put(server);}
        server.init();
        return loadServer(server.get());
    }

    protected void init() {
        synchronized (authlibServer) {
            for (Map.Entry<UUID, AuthlibServer> entry : authlibServer.getMap().entrySet()) {
                UUID uuid = entry.getKey();
                AuthlibServer authlibServer1 = entry.getValue();
                try {
                    authlibServer1.init();
                    authlibServer.getMap().replace(uuid, authlibServer1);
                } catch (RestfulAPIException | SecurityException e) {
                    LOGGER.throwing(e.getClass().getSimpleName(), "init()", e);
                    authlibServer.remove(uuid);

                }
            }
        }

    }

    public String getAuthlibServerUrl(UUID uuid) {
        AuthlibServer authlibServer = null;
        try {
            authlibServer = getAuthlibServer(uuid);
        } catch (RestfulAPIException e) {
            e.printStackTrace();
        }
        assert authlibServer != null;
        return authlibServer.getServerUrl();
    }

    public AuthlibServer getAuthlibServer(UUID uuid) throws NoSuchElementException, RestfulAPIException {
        AuthlibServer server = loadServer(uuid);
        try {
            server.init();
        } catch (RestfulAPIException e) {
            LOGGER.logp(Level.WARNING, this.getClass().getSimpleName(), "loadServer(UUID)", "Server Fail to Init Removing");
            LOGGER.throwing(this.getClass().getCanonicalName(), "loadServer(UUID)", e);
            this.authlibServer.remove(uuid);
            throw e;
        }
        return server;
    }
}
