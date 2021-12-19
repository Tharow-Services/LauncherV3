package net.tharow.tantalum.solder.io;

import net.tharow.tantalum.rest.RestObject;
import net.tharow.tantalum.solder.ISolderInfo;

public class SolderInfo extends RestObject implements ISolderInfo {
    private String api;
    private String version;
    private String stream;

    @Override
    public String getApi(){return api;}
    @Override
    public String getVersion(){return version;}
    @Override
    public String getStream(){return stream;}

    public String toString(){
        return "SolderInfo{" +
                "name='" + api + '\'' +
                ", version='" + version + '\'' +
                ", stream='" + stream +
                '}';
    }


}
