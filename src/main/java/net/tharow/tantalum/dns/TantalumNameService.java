package net.tharow.tantalum.dns;

import sun.net.spi.nameservice.NameService;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class TantalumNameService implements NameService {
    @Override
    public InetAddress[] lookupAllHostAddr(String host) throws UnknownHostException {
        return new InetAddress[0];
    }

    @Override
    public String getHostByAddr(byte[] addr) throws UnknownHostException {
        return null;
    }
}
