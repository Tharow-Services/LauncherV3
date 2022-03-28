

package net.tharow.tantalum.autoupdate.http;

import net.tharow.tantalum.autoupdate.IUpdateStream;
import net.tharow.tantalum.autoupdate.io.StreamVersion;
import net.tharow.tantalum.launchercore.TantalumConstants;
import net.tharow.tantalum.rest.RestObject;
import net.tharow.tantalum.rest.RestfulAPIException;

import java.net.URL;

public class HttpUpdateStream implements IUpdateStream {

    public HttpUpdateStream() {}

    @Override
    public StreamVersion getStreamVersion(String stream) throws RestfulAPIException {
        return getStreamVersion();
    }

    @Override
    public StreamVersion getStreamVersion() throws RestfulAPIException {
        return RestObject.getRestObject(StreamVersion.class, TantalumConstants.UPDATE_URL);
    }
}
