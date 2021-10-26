package net.tharow.tantalum.minecraftcore.tharow.java;

import com.google.gson.annotations.SerializedName;
import net.tharow.tantalum.utilslib.JavaUtils;
import net.tharow.tantalum.utilslib.OperatingSystem;

import java.util.List;
import java.util.Map;

public class JavaRuntimes {
    @SerializedName("linux")
    private Map<String, List<JavaRuntime>> linux64;
    @SerializedName("linux-i386")
    private Map<String, List<JavaRuntime>> linux32;

    @SerializedName("mac-os")
    private Map<String, List<JavaRuntime>> mac;

    @SerializedName("windows-x64")
    private Map<String, List<JavaRuntime>> windows64;
    @SerializedName("windows-x86")
    private Map<String, List<JavaRuntime>> windows32;

    public Map<String, List<JavaRuntime>> getRuntimesForCurrentOS() {
        switch (OperatingSystem.getOperatingSystem()) {
            case WINDOWS:
                if (JavaUtils.is64Bit()) {
                    return windows64;
                }

                return windows32;
            case OSX:
                return mac;
            case LINUX:
                if (JavaUtils.is64Bit()) {
                    return linux64;
                }

                return linux32;
        }

        return null;
    }

    public JavaRuntime getRuntimeForCurrentOS(String runtimeName) {
        Map<String, List<JavaRuntime>> availableRuntimes = getRuntimesForCurrentOS();

        if (availableRuntimes == null) return null;

        // For some reason every runtime is a list with a single entry
        return availableRuntimes.get(runtimeName).get(0);
    }
}
