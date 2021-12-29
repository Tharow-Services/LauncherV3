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

package net.tharow.tantalum.solder.http;

import net.tharow.tantalum.launchercore.exception.BuildInaccessibleException;
import net.tharow.tantalum.rest.RestObject;
import net.tharow.tantalum.rest.RestfulAPIException;
import net.tharow.tantalum.rest.io.Modpack;
import net.tharow.tantalum.solder.ISolderPackApi;
import net.tharow.tantalum.solder.io.SolderPackInfo;

public class HttpSolderPackApi implements ISolderPackApi {

    private final String baseUrl;
    private final String modpackSlug;
    private final String clientId;
    private final String mirrorUrl;

    protected HttpSolderPackApi(String baseUrl, String modpackSlug, String clientId, String mirrorUrl) throws RestfulAPIException {
        this.baseUrl = baseUrl;
        this.modpackSlug = modpackSlug;
        this.clientId = clientId;
        this.mirrorUrl = mirrorUrl;

        if (mirrorUrl == null)
            throw new RestfulAPIException("A mirror URL could not be retrieved from '" + baseUrl + "modpack'");
    }

    @Override
    public String getMirrorUrl() {
        return mirrorUrl;
    }

    @Override
    public SolderPackInfo getPackInfoForBulk() throws RestfulAPIException {
        return getPackInfo();
    }

    @Override
    public SolderPackInfo getPackInfo() throws RestfulAPIException {
        String packUrl = baseUrl + "modpack/" + modpackSlug + "?cid=" + clientId;
        SolderPackInfo info = RestObject.getRestObject(SolderPackInfo.class, packUrl);
        info.setSolder(this);
        return info;
    }

    @Override
    public Modpack getPackBuild(String build) throws BuildInaccessibleException {
        try {
            String url = baseUrl + "modpack/" + modpackSlug + "/" + build + "?cid=" + clientId;
            Modpack pack = RestObject.getRestObject(Modpack.class, url);

            if (pack != null) {
                return pack;
            }
        } catch (RestfulAPIException e) {
            throw new BuildInaccessibleException(modpackSlug, build, e);
        }

        throw new BuildInaccessibleException(modpackSlug, build);
    }
}
