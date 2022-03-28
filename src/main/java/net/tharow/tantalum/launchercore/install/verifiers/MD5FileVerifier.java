

package net.tharow.tantalum.launchercore.install.verifiers;

import net.tharow.tantalum.utilslib.MD5Utils;
import net.tharow.tantalum.utilslib.Utils;

import java.io.File;

public class MD5FileVerifier implements IFileVerifier {
    private final String md5Hash;

    public MD5FileVerifier(String md5Hash) {
        this.md5Hash = md5Hash;
    }

    public boolean isFileValid(File file) {
        if (md5Hash == null || md5Hash.isEmpty())
            return false;

        String resultMD5 = MD5Utils.getMD5(file);

        boolean hashMatches = md5Hash.equalsIgnoreCase(resultMD5);

        if (!hashMatches)
            Utils.getLogger().config("MD5 verification for " + file + " failed. Expected " + md5Hash + ", got " + resultMD5);
        if (md5Hash.equals("MD5 Hash Bypass")) {
            Utils.getLogger().warning("MD5 verification for " + file + " Bypassed Hash: "+resultMD5);
            return true;
        }

        return hashMatches;
    }
}
