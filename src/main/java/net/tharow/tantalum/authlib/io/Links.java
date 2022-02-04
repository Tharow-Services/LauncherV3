package net.tharow.tantalum.authlib.io;

import com.google.gson.JsonObject;

public class Links {
    private String homepage;
    private String register;

    public String getHomepage() {
        return this.homepage;
    }

    public String getRegister() {
        return this.register;
    }

    public Links() {
    }

    public Links(final String homepage, final String register) {
        this.homepage = homepage;
        this.register = register;
    }

    @Override
    public String toString() {
        return "Links{" +
                "homepage='" + homepage + '\'' +
                ", register='" + register + '\'' +
                '}';
    }
}
