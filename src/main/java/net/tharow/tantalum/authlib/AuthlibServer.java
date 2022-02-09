package net.tharow.tantalum.authlib;

import com.google.common.base.Charsets;
import com.google.gson.annotations.SerializedName;
import io.opencensus.trace.export.SpanData;
import javafx.scene.input.DataFormat;
import net.tharow.tantalum.authlib.io.Links;
import net.tharow.tantalum.authlib.io.ServerInfo;
import net.tharow.tantalum.authlib.io.ServerMetaInfo;
import net.tharow.tantalum.launchercore.logging.Level;
import net.tharow.tantalum.launchercore.logging.Logger;
import net.tharow.tantalum.rest.RestObject;
import net.tharow.tantalum.rest.RestfulAPIException;
import net.tharow.tantalum.utilslib.DoNotSerialize;
import net.tharow.tantalum.utilslib.MD5Utils;
import net.tharow.tantalum.utilslib.UUID5;
import net.tharow.tantalum.utilslib.Utils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.swing.text.DateFormatter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.text.spi.DateFormatProvider;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;


public class AuthlibServer implements IAuthlibServerApi, Supplier<UUID>, IAuthlibServerInfo {

    private transient static final Logger l = Authlib.LOGGER;


    private UUID uuid;
    private String name;
    private String address;
    private String apiAddress = Authlib.API_LOCATION_NOT_GENERATED;
    private String signature;
    private String clientToken = String.valueOf(UUID.randomUUID());
    //private List<String> skinDomains;
    private ServerInfo serverInfo;

    @Override
    public String toString() {
        return "AuthlibServer{" +
                "uuid=" + uuid +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", apiAddress='" + apiAddress + '\'' +
                ", signature='" + signature + '\'' +
                ", clientToken='" + clientToken + '\'' +
                ", serverInfo='" + serverInfo + '\'' +
                '}';
    }

    public AuthlibServer(){

    }

    public final @NotNull AuthlibServer prepare() {
        AuthlibServer authlibServer = new AuthlibServer();
        authlibServer.serverInfo = serverInfo;
        authlibServer.update();
        authlibServer.uuid = uuid;
        authlibServer.apiAddress = apiAddress;
        authlibServer.serverInfo.removePBK();
        return authlibServer;
    }

    public void init() throws RestfulAPIException, SecurityException{
        l.entering(this.getClass(), "init()");
        // Connect and Refresh Connection //
        this.apiAddress = apiAddress.equals(Authlib.API_LOCATION_NOT_GENERATED)?getApiLocation(address):apiAddress;
        final ServerInfo Current = (ServerInfo) getAuthlibServerInfo(this.apiAddress);
        Current.setServerUrl(apiAddress);

        if (!MD5Utils.checkMD5(Current.getSignature(), this.getSignature())) {
            throw new SecurityException("Authlib Server Was Tampered With. Public Signature Changed With Out Notice "+this,new Throwable().fillInStackTrace());
        }
        this.serverInfo = Current;
        this.uuid = this.serverInfo.getUUID();
        this.clientToken = String.valueOf(UUID5.fromUTF8(this.uuid, "Client"));
        this.update();
        l.exiting(this.getClass(), "init()");
    }

    protected void update(){
        this.uuid = this.serverInfo.getUUID();
        this.name = this.serverInfo.getServerName();
        this.address = this.serverInfo.getServerUrl();
        this.signature = this.serverInfo.getSignature();
        //this.serverMeta = this.serverInfo.getMeta();
    }

    public AuthlibServer(String srvUrl, String clientToken) throws RestfulAPIException {
        this.apiAddress = getApiLocation(srvUrl);
        this.serverInfo =RestObject.getRestObject(ServerInfo.class, this.apiAddress);
        this.clientToken = clientToken;
        this.getServerInfo().setServerUrl(srvUrl);
        this.update();
    }

    public AuthlibServer(@NotNull AuthlibServer server, ServerInfo info){
        this.serverInfo = info;
        this.clientToken = this.serverInfo.getClientToken();
        this.signature = this.serverInfo.getSignature();
        this.apiAddress = server.apiAddress;
        this.serverInfo.setServerUrl(server.apiAddress);
    }

    public String getSignature(){
        return this.signature;
    }

    public String getClientToken(){
        return this.clientToken;
    }

    public String getServerUrl(){
        return !Objects.equals(apiAddress, Authlib.API_LOCATION_NOT_GENERATED) ?apiAddress:address;
    }


    @Override
    public IAuthlibServerInfo getServerInfo() {
        return this.serverInfo;
    }

    @Override
    public AuthlibAuthenticator getAuthenticator() {
        return getAuthenticator(this);
    }

    @Contract("_ -> new")
    public static @NotNull AuthlibAuthenticator getAuthenticator(AuthlibServer server) {
        return new AuthlibAuthenticator(server);
    }

    public static @NotNull IAuthlibServerInfo getAuthlibServerInfo(String srvUrl) throws RestfulAPIException{
        IAuthlibServerInfo serverinfo = RestObject.getRestObject(ServerInfo.class, srvUrl);
        serverinfo.setServerUrl(srvUrl);
        return serverinfo;
    }

    public static String getApiLocation(String url) throws RestfulAPIException {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setUseCaches(false);
            connection.setDoOutput(true);
            connection.setDoInput(true);
            connection.setConnectTimeout(15000);
            connection.setReadTimeout(15000);
            connection.setRequestMethod("GET");
            //connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            connection.setRequestProperty("Content-Language", "en-US");

            //String returnable = null;
            String header = url;//(connection.getHeaderField("X-Authlib-Injector-API-Location") != null ? (url + connection.getHeaderField("X-Authlib-Injector-API-Location")) : url);
            connection.disconnect();
            return header;

        } catch (IOException e){
            throw new RestfulAPIException(e.getMessage(),e.getCause());
        }
    }

    /**
     * Gets a result.
     *
     * @return a result
     */
    @Override
    public UUID get() {
        return this.uuid;
    }
    public void set(UUID uuid) {
        this.uuid = uuid;
    }

    @Override
    public void setServerUrl(String serverUrl) {

    }
    // Server Info //
    @Override
    public IAuthlibServerInfo setApiLocation(String ALI) {
        return serverInfo.setApiLocation(ALI);
    }

    @Override
    public String getServerName() {
        return serverInfo.getServerName();
    }

    @Override
    public String getImplName() {
        return serverInfo.getImplName();
    }

    @Override
    public String getImplVersion() {
        return serverInfo.getImplVersion();
    }

    @Override
    public String getHomepage() {
        return serverInfo.getHomepage();
    }

    @Override
    public String getRegisterPage() {
        return serverInfo.getRegisterPage();
    }

    @Override
    public boolean isEmailLogin() {
        return serverInfo.isEmailLogin();
    }

    @Override
    public boolean isLegacySkinApi() {
        return serverInfo.isEmailLogin();
    }

    @Override
    public boolean isMojangNamespace() {
        return serverInfo.isMojangNamespace();
    }

    @Override
    public List<String> getSkinDomains() {
        return serverInfo.getSkinDomains();
    }

    @Override
    public String getPublicKey() {
        return serverInfo.getPublicKey();
    }

    @Override
    public String toReadable() {
        return serverInfo.toReadable();
    }


}
