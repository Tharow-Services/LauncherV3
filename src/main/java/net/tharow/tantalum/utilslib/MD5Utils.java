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
