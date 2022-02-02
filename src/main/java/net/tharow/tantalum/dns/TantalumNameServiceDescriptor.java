package net.tharow.tantalum.dns;

import sun.net.spi.nameservice.NameService;
import sun.net.spi.nameservice.NameServiceDescriptor;

public class TantalumNameServiceDescriptor implements NameServiceDescriptor {
    @Override
    public NameService createNameService() throws Exception {
        return null;
    }

    @Override
    public String getProviderName() {
        return null;
    }

    @Override
    public String getType() {
        return null;
    }
}
