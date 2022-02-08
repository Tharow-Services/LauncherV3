package net.tharow.tantalum.platform;

import java.util.function.Supplier;

public interface IPlatformInfo extends Supplier<String> {

    String getName();

    String getAccessCode();

    String getAccessVerb();

    String getVersion();

    String get();

    boolean isOffline();

    void refresh();

}
