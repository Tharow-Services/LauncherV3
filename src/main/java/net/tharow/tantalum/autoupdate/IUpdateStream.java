

package net.tharow.tantalum.autoupdate;

import net.tharow.tantalum.autoupdate.io.StreamVersion;
import net.tharow.tantalum.rest.RestfulAPIException;

public interface IUpdateStream {
    StreamVersion getStreamVersion(String stream) throws RestfulAPIException;

    StreamVersion getStreamVersion() throws RestfulAPIException;
}
