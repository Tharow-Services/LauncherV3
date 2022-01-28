package net.tharow.tantalum.launchercore;

import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ComputerInfo {

    public static String USERNAME, OS, PROCESSOR_REVISION, PROCESSOR_ARCHITECTURE, NUMBER_OF_PROCESSORS, PROCESSOR_LEVEL, USERDOMAIN_ROAMINGPROFILE, SESSIONNAME, LOGINSERVER,
    USERDOMAIN, COMPUTERNAME, PROCESSOR_IDENTIFIER;

    static {
        final Map<String, String> environment = System.getenv();
        USERNAME = environment.getOrDefault("USERNAME", "Unknown");
        OS = environment.getOrDefault("OS","Java Virtual Machine");
        PROCESSOR_REVISION = environment.getOrDefault("PROCESSOR_REVISION", System.getProperty("java.version"));
        PROCESSOR_ARCHITECTURE = environment.getOrDefault("PROCESSOR_ARCHITECTURE", "Java");
        NUMBER_OF_PROCESSORS = environment.getOrDefault("NUMBER_OF_PROCESSORS", "Unknown");
        PROCESSOR_LEVEL = environment.getOrDefault("PROCESSOR_LEVEL", "JAVA LEVEL");
        USERDOMAIN_ROAMINGPROFILE = environment.getOrDefault("USERDOMAIN_ROAMINGPROFILE","Not Roaming");
        SESSIONNAME = environment.getOrDefault("SESSIONNAME", "Unknown Session Name");
        LOGINSERVER = environment.getOrDefault("LOGINSERVER", "Unknown Login Server");
        USERDOMAIN = environment.getOrDefault("USERDOMAIN", "Unknown User Domain");
        COMPUTERNAME = environment.getOrDefault("COMPUTERNAME", "Unknown Computer");
        PROCESSOR_IDENTIFIER = environment.getOrDefault("PROCESSOR_IDENTIFIER", System.getProperty("java.version"));
    }

    public static boolean isSchoolEnv(){
        return USERDOMAIN.equals("ucsint.org");
    }

    public static @NotNull String getEnvironment(){
        StringBuilder b = new StringBuilder("[Environment Variables]: ");
        System.getenv().forEach((name, value) -> {
            b.append(name)
                    .append(": ")
                    .append(value)
                    .append("; ");
        });
        return b.toString();
    }
    public static @NotNull String getProperties(){
        StringBuilder b = new StringBuilder("[System Properties]: ");
        System.getProperties().forEach((name, value) -> {
            b.append(name)
                    .append(": ")
                    .append(value)
                    .append("; ");
        });
        return b.toString();
    }



}
