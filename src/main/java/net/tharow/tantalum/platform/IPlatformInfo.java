package net.tharow.tantalum.platform;

import java.util.UUID;
import java.util.function.Supplier;

public interface IPlatformInfo extends Supplier<UUID> {

    String getUrl();

    String getName();

    String getAccessCode();

    String getAccessVerb();

    String getVersion();

    UUID get();

    boolean isOffline();

}
