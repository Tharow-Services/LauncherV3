package net.tharow.tantalum.authlib.io;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import net.tharow.tantalum.authlib.IAuthlibServerInfo;
import net.tharow.tantalum.rest.RestObject;
import net.tharow.tantalum.utilslib.UUID5;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

@SuppressWarnings({"unused"})
public class ServerInfo extends RestObject implements IAuthlibServerInfo {

    private ServerMetaInfo meta;
    private List<String> skinDomains;
    @SerializedName("signaturePublickey") private String signaturePublicKey;
    private transient String serverUrl;
    private transient String clientToken;

    public ServerInfo(){

    }

    public ServerInfo(@NotNull ServerInfo serverInfo){
        this.meta = serverInfo.meta;
        this.skinDomains = serverInfo.skinDomains;
        this.signaturePublicKey = serverInfo.signaturePublicKey;
        this.serverUrl = serverInfo.serverUrl;
    }

    public ServerInfo(@NotNull ServerInfo serverInfo, final String address, final String clientToken){
        this.meta = serverInfo.meta;
        this.skinDomains = serverInfo.skinDomains;
        this.signaturePublicKey = serverInfo.signaturePublicKey;
        this.serverUrl = address;
        this.clientToken = clientToken;
    }

    public void removePBK(){
        this.signaturePublicKey = this.signaturePublicKey.substring(26);
    }

    public void set(@NotNull ServerInfo info){
        this.meta = info.meta;
        this.skinDomains = info.skinDomains;
        //this.signaturePublicKey = info.signaturePublicKey;
        this.serverUrl = info.serverUrl;
        this.clientToken = info.clientToken;
    }

    public ServerInfo(final String name, final String implName, final String implVersion, final String homePage, final String registerPage,
                      final boolean isEmailLogin, final boolean isLegacySkinApi, final boolean isMojangNamespace, final List<String> skinDomains, final String signaturePublicKey ){
        this.meta = new ServerMetaInfo(name, implName, implVersion, homePage, registerPage, isEmailLogin, isLegacySkinApi, isMojangNamespace);
        this.skinDomains = skinDomains;
        this.signaturePublicKey = signaturePublicKey;
    }

    public UUID getUUID(){
        return UUID5.fromUTF8(null, getServerName()+getImplName());
    }

    public String getSignature(){
        return DigestUtils.md5Hex(this.signaturePublicKey == null ?"Fake Public Key":this.signaturePublicKey);
        //return this.signaturePublicKey
    }

    @Override
    public String getServerUrl() {
        return this.serverUrl;
    }

    @Override
    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    @Override
    public IAuthlibServerInfo setApiLocation(String ALI) {
        this.serverUrl = ALI;
        return this;
    }

    public String getClientToken(){return this.clientToken;}

    public void setClientToken(String clientToken) {
        this.clientToken = clientToken;
    }

    @Override
    public String getServerName() {
        return this.meta.getServerName();
    }

    @Override
    public String getImplName() {
        return this.meta.getImplName();
    }

    @Override
    public String getImplVersion() {
        return this.meta.getImplVersion();
    }

    @Override
    public String getHomepage() {
        return this.meta.getHomepage();
    }

    @Override
    public String getRegisterPage() {
        return this.meta.getRegister();
    }

    @Override
    public boolean isEmailLogin() {
        return this.meta.isEmailLogin();
    }

    @Override
    public boolean isLegacySkinApi() {
        return this.meta.isLegacySkinApi();
    }

    @Override
    public boolean isMojangNamespace() {
        return this.meta.isMojangNamespace();
    }

    @Override
    public List<String> getSkinDomains() {
        return this.skinDomains;
    }

    @Override
    public String getPublicKey() {
        return this.signaturePublicKey;
    }

    @Override
    public String toReadable() {
        return null;
    }


    //@Override
    //public String toString(){
    //    return toJson().toString();
    //}


    @Override
    public String toString() {
        return "ServerInfo{" +
                "meta=" + meta +
                ", skinDomains=" + skinDomains +
                ", signaturePublicKey='" + signaturePublicKey + '\'' +
                '}';
    }

    public ServerMetaInfo getMeta() {
        return meta;
    }
}
