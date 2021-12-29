/*
 * This file is part of Technic Launcher Core.
 * Copyright ©2015 Syndicate, LLC
 *
 * Technic Launcher Core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Technic Launcher Core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * as well as a copy of the GNU Lesser General Public License,
 * along with Technic Launcher Core.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tharow.tantalum.autoupdate.http;

import net.tharow.tantalum.autoupdate.IUpdateStream;
import net.tharow.tantalum.autoupdate.io.StreamVersion;
import net.tharow.tantalum.rest.RestObject;
import net.tharow.tantalum.rest.RestfulAPIException;

import java.net.URL;

public class HttpUpdateStream implements IUpdateStream {
    private final URL baseUrl;

    public HttpUpdateStream(URL baseUrl) {
        this.baseUrl = baseUrl;
    }

    @Override
    public StreamVersion getStreamVersion(String stream) throws RestfulAPIException {
        return getStreamVersion();
    }

    @Override
    public StreamVersion getStreamVersion() throws RestfulAPIException {
        return RestObject.getRestObject(StreamVersion.class, baseUrl.toString());
    }
}
