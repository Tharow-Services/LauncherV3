

package net.tharow.tantalum.utilslib;

import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class MD5Utils {

    public static String getMD5(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            String md5 = DigestUtils.md5Hex(fis);
            fis.close();
            return md5;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static @NotNull String getMD5(String input){
        return DigestUtils.md5Hex(input);
    }

    public static boolean checkMD5(File file, String md5) {
        return checkMD5(md5, getMD5(file));
    }

    public static boolean checkMD5(String md5, String otherMd5) {
        return md5.equalsIgnoreCase(otherMd5);
    }
}
