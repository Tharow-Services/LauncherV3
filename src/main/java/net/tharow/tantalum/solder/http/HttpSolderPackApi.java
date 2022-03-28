

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
