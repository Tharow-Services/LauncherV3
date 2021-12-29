/*
 * This file is part of Technic Minecraft Core.
 * Copyright ©2015 Syndicate, LLC
 *
 * Technic Minecraft Core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Technic Minecraft Core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * as well as a copy of the GNU Lesser General Public License,
 * along with Technic Minecraft Core.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tharow.tantalum.minecraftcore.mojang.version.builder.retrievers;

import net.tharow.tantalum.launchercore.install.verifiers.ValidJsonFileVerifier;
import net.tharow.tantalum.launchercore.util.DownloadListener;
import net.tharow.tantalum.minecraftcore.MojangUtils;
import net.tharow.tantalum.minecraftcore.mojang.version.builder.MojangVersionRetriever;
import net.tharow.tantalum.utilslib.Utils;

import java.io.File;
import java.io.IOException;

public class HttpFileRetriever implements MojangVersionRetriever {

    private final String baseUrl;
    private final DownloadListener listener;

    public HttpFileRetriever(String baseUrl, DownloadListener listener) {
        this.baseUrl = baseUrl;
        this.listener = listener;
    }

    @Override
    public void retrieveVersion(File target, String key) throws InterruptedException, IOException {
        String url = baseUrl + key + "/" + key + ".json";
        Utils.downloadFile(url, target.getName(), target.getAbsolutePath(), null, new ValidJsonFileVerifier(MojangUtils.getGson()), listener);
    }
}
