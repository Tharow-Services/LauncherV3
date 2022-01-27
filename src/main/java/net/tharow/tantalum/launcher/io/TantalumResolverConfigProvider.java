package net.tharow.tantalum.launcher.io;

import org.xbill.DNS.config.PropertyResolverConfigProvider;

import java.net.InetSocketAddress;

public class TantalumResolverConfigProvider extends PropertyResolverConfigProvider {



    public TantalumResolverConfigProvider() {

    }

    @Override
    protected void addNameserver(InetSocketAddress server) {
        super.addNameserver(server);
    }

    @Override
    public void initialize() {
        super.initialize();
    }
}
