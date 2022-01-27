/*
 * This file is part of The Technic Launcher Version 3.
 * Copyright ©2015 Syndicate, LLC
 *
 * The Technic Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * The Technic Launcher  is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with the Technic Launcher.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tharow.tantalum.launcher;

import com.beust.jcommander.JCommander;
import com.sun.org.apache.xerces.internal.dom.DeferredElementNSImpl;
import net.tharow.tantalum.authlib.AuthlibAuthenticator;
import net.tharow.tantalum.autoupdate.IBuildNumber;
import net.tharow.tantalum.autoupdate.Relauncher;
import net.tharow.tantalum.autoupdate.http.HttpUpdateStream;
import net.tharow.tantalum.launcher.autoupdate.CommandLineBuildNumber;
import net.tharow.tantalum.launcher.autoupdate.TechnicRelauncher;
import net.tharow.tantalum.launcher.autoupdate.VersionFileBuildNumber;
import net.tharow.tantalum.launcher.io.*;
import net.tharow.tantalum.launcher.launch.Installer;
import net.tharow.tantalum.launcher.settings.SettingsFactory;
import net.tharow.tantalum.launcher.settings.StartupParameters;
import net.tharow.tantalum.launcher.settings.TantalumSettings;
import net.tharow.tantalum.launcher.settings.migration.IMigrator;
import net.tharow.tantalum.launcher.settings.migration.InitialV3Migrator;
import net.tharow.tantalum.launcher.ui.InstallerFrame;
import net.tharow.tantalum.launcher.ui.LauncherFrame;
import net.tharow.tantalum.launcher.ui.LoginFrame;
import net.tharow.tantalum.launcher.ui.components.discover.DiscoverInfoPanel;
import net.tharow.tantalum.launcher.ui.components.modpacks.ModpackSelector;
import net.tharow.tantalum.launchercore.TantalumConstants;
import net.tharow.tantalum.launchercore.auth.IUserType;
import net.tharow.tantalum.launchercore.auth.UserModel;
import net.tharow.tantalum.launchercore.exception.DownloadException;
import net.tharow.tantalum.launchercore.image.ImageRepository;
import net.tharow.tantalum.launchercore.image.face.MinotarFaceImageStore;
import net.tharow.tantalum.launchercore.image.face.WebAvatarImageStore;
import net.tharow.tantalum.launchercore.install.*;
import net.tharow.tantalum.launchercore.launch.java.JavaVersionRepository;
import net.tharow.tantalum.launchercore.launch.java.source.FileJavaSource;
import net.tharow.tantalum.launchercore.launch.java.source.InstalledJavaSource;
import net.tharow.tantalum.launchercore.logging.BuildLogFormatter;
import net.tharow.tantalum.launchercore.logging.RotatingFileHandler;
import net.tharow.tantalum.launchercore.modpacks.ModpackModel;
import net.tharow.tantalum.launchercore.modpacks.PackLoader;
import net.tharow.tantalum.launchercore.modpacks.resources.PackImageStore;
import net.tharow.tantalum.launchercore.modpacks.resources.PackResourceMapper;
import net.tharow.tantalum.launchercore.modpacks.resources.resourcetype.BackgroundResourceType;
import net.tharow.tantalum.launchercore.modpacks.resources.resourcetype.IModpackResourceType;
import net.tharow.tantalum.launchercore.modpacks.resources.resourcetype.IconResourceType;
import net.tharow.tantalum.launchercore.modpacks.resources.resourcetype.LogoResourceType;
import net.tharow.tantalum.launchercore.modpacks.sources.IAuthoritativePackSource;
import net.tharow.tantalum.launchercore.modpacks.sources.IInstalledPackRepository;
import net.tharow.tantalum.minecraftcore.launch.MinecraftLauncher;
import net.tharow.tantalum.minecraftcore.microsoft.auth.MicrosoftAuthenticator;
import net.tharow.tantalum.minecraftcore.mojang.auth.MojangAuthenticator;
import net.tharow.tantalum.platform.IPlatformApi;
import net.tharow.tantalum.platform.PlatformPackInfoRepository;
import net.tharow.tantalum.platform.cache.ModpackCachePlatformApi;
import net.tharow.tantalum.platform.http.HttpPlatformApi;
import net.tharow.tantalum.platform.io.AuthorshipInfo;
import net.tharow.tantalum.solder.ISolderApi;
import net.tharow.tantalum.solder.SolderPackSource;
import net.tharow.tantalum.solder.cache.CachedSolderApi;
import net.tharow.tantalum.solder.http.HttpSolderApi;
import net.tharow.tantalum.ui.components.Console;
import net.tharow.tantalum.ui.components.ConsoleFrame;
import net.tharow.tantalum.ui.components.ConsoleHandler;
import net.tharow.tantalum.ui.components.LoggerOutputStream;
import net.tharow.tantalum.ui.controls.installation.SplashScreen;
import net.tharow.tantalum.ui.lang.ResourceLoader;
import net.tharow.tantalum.utilslib.*;
import org.apache.commons.io.FileUtils;
import org.joda.time.DateTime;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.*;
import java.util.logging.Handler;
import net.tharow.tantalum.utilslib.logger.Level;
import org.xbill.DNS.*;
import org.xbill.DNS.config.BaseResolverConfigProvider;
import org.xbill.DNS.config.PropertyResolverConfigProvider;
import org.xbill.DNS.hosts.HostsFileParser;

import java.util.logging.Logger;
import java.util.stream.Collectors;

public class LauncherMain {

    public static final boolean DUMP = false;

    public static final Name MasterDNS = Name.fromConstantString("dns.host");
    public static final int MasterDNSPort = 53;

    public static ConsoleFrame consoleFrame;

    public static final Locale[] supportedLanguages = new Locale[] {
            Locale.ENGLISH,
            new Locale("pt","BR"),
            new Locale("pt","PT"),
            new Locale("cs"),
            Locale.GERMAN,
            Locale.FRENCH,
            Locale.ITALIAN,
            new Locale("hu"),
            new Locale("pl"),
            Locale.CHINA,
            Locale.TAIWAN,
            new Locale("nl", "NL"),
            new Locale("sk"),
    };

    private static IBuildNumber buildNumber;

    public static void main(String[] argv) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            Utils.getLogger().log(Level.SEVERE, ex.getMessage(), ex);
        }

        ToolTipManager.sharedInstance().setDismissDelay(Integer.MAX_VALUE);

        StartupParameters params = new StartupParameters(argv);
        try {
            JCommander.newBuilder()
                    .addObject(params)
                    .build()
                    .parse(argv);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        TantalumSettings settings = null;

        try {
            settings = SettingsFactory.buildSettingsObject(Relauncher.getRunningPath(LauncherMain.class), params.isMover());
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
        }

        if (settings == null) {
            ResourceLoader installerResources = new ResourceLoader(null, "net","tharow","tantalum","launcher","resources");
            installerResources.setSupportedLanguages(supportedLanguages);
            installerResources.setLocale(ResourceLoader.DEFAULT_LOCALE);
            InstallerFrame dialog = new InstallerFrame(installerResources, params);
            dialog.setVisible(true);
            return;
        }

        LauncherDirectories directories = new TechnicLauncherDirectories(settings.getTechnicRoot());
        ResourceLoader resources = new ResourceLoader(directories, "net","tharow","tantalum","launcher","resources");
        resources.setSupportedLanguages(supportedLanguages);
        resources.setLocale(settings.getLanguageCode());

        // Sanity check
        checkIfRunningInsideOneDrive(directories.getLauncherDirectory());

        if (params.getBuildNumber() != null && !params.getBuildNumber().isEmpty()) {
            buildNumber = new CommandLineBuildNumber(params);
        } else {
            buildNumber = new VersionFileBuildNumber(resources);
        }

        TantalumConstants.setBuildNumber(buildNumber);

        setupLogging(directories, resources);

        System.setProperty("dns.server","8.8.8.8, localhost:9150, dns.google");
        System.setProperty("sun.net.spi.nameservice.provider.1","dns,dnsjava");

        //Currently Unused
        //String launcherBuild = buildNumber.getBuildNumber();
        int build = -1;

        try {
            build = Integer.parseInt((new VersionFileBuildNumber(resources)).getBuildNumber());
        } catch (NumberFormatException ex) {
            //This is probably a debug build or something, build number is invalid
        }
        Utils.getLogger().info("Current Build Number is " + build);
        // These 2 need to happen *before* the launcher or the updater run, so we have valuable debug information, and so
        // we can properly use websites that use Let's Encrypt (and other current certs not supported by old Java versions)
        if(params.getLogLevel()==6){
            TantalumConstants.setIsDebug(true);
            Utils.getLogger().setLevel(Level.ALL);
        } else{
            TantalumConstants.setIsDebug(false);
            Utils.getLogger().setLevel(Level.getLevel(params.getLogLevel()*100));
        }

        //runProxySetup(settings);

        runStartupDebug(settings, params);
        //injectNewRootCerts();
        injectNewRootCerts(settings.getForceOverrideRootCerts() || params.isOverrideRoots());
        //startLauncher(settings, params, directories, resources);
        Relauncher launcher = new TechnicRelauncher(new HttpUpdateStream(TantalumConstants.UpdateUrl), build, directories, resources, params);
        try {
            if (launcher.runAutoUpdater())
                startLauncher(settings, params, directories, resources);
        } catch (InterruptedException e) {
            //Canceled by user
        } catch (DownloadException e) {
            //JOptionPane.showMessageDialog(null, resources.getString("launcher.updateerror.download", pack.getDisplayName(), e.getMessage()), resources.getString("launcher.installerror.title"), JOptionPane.WARNING_MESSAGE);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    public static void setupDnsJava(final LauncherDirectories directories){
        // Create Host File Parser For DNS Overrides //
        HostsFileParser hostsFileParser = new HostsFileParser(new File(directories.getLauncherAssetsDirectory(), "hosts.conf").toPath());
        try {
            InetSocketAddress mainDNSServer = new InetSocketAddress(hostsFileParser.getAddressForHost(MasterDNS, Type.A).orElse(InetAddress.getLocalHost()),MasterDNSPort);
        } catch (IOException e) {
            Utils.logDebug("There Was an io Error for");
            e.printStackTrace();
        }

        // Make DNSJAVA proterys
        BaseResolverConfigProvider configProvider = new PropertyResolverConfigProvider();

        // Make Configuration
        //ResolverConfig resolverConfig = ResolverConfig.getCurrentConfig();
        //resolverConfig.server();
        //ResolverConfig.getCurrentConfig();

    }

    /*
    public static void runProxySetup(TantalumSettings settings2, File torRelayDir) {
        if(settings2.getUseTorRelay()){
            //Setup Tor Config and other things//
            TorConfig torConfig = TorConfig.createFlatConfig(torRelayDir);
            //TorConfig torConfig = TorConfig.createConfig(torRelayDir,torRelayDir,torRelayDir);
            TorInstaller torInstaller = new JavaTorInstaller(torConfig);
            OnionProxyContext proxyContext = new JavaOnionProxyContext(torConfig, torInstaller, null);
            final OnionProxyManager onionProxyManager = new OnionProxyManager(proxyContext);
            final TorConfigBuilder torConfigBuilder = onionProxyManager.getContext().newConfigBuilder();
            try {
                onionProxyManager.getContext().getInstaller().updateTorConfigCustom(torConfigBuilder.asString());
                onionProxyManager.setup();
                Utils.getLogger().info("List of bridges "+ onionProxyManager.getContext().getSettings().getListOfSupportedBridges());
                onionProxyManager.start();
            } catch (IOException | TimeoutException e) {
                e.printStackTrace();
            }


        }
        if(settings2.getUseDNSOnly()) {
            //Setup http Proxy

            if (settings2.getUseHTTPProxy()) {
                Utils.getLogger().info("HTTP Proxy is currently configured as \n  HTTP Proxy Host: " + settings2.getHTTPProxyHost() + "\n  HTTP Proxy Port: " + settings2.getHTTPProxyPort() + "\n HTTP Proxy Non Proxies Hosts: " + settings2.getHTTPProxyBypassDomains());
                System.setProperty("http.proxyHost", settings2.getHTTPProxyHost());
                System.setProperty("http.proxyPort", String.valueOf(settings2.getHTTPProxyPort()));
                System.setProperty("http.nonProxyHosts", settings2.getHTTPProxyBypassDomains());// Separated by |
            }
            //Setup Socks Proxy
            if (settings2.getUseSocksProxy()) {
                Utils.getLogger().info("Socks Proxy is currently configured as \n  Socks Proxy Host: " + settings2.getSocksProxyHost() + "\n  Socks Proxy Port: " + settings2.getSocksProxyPort() + "\n Socks Proxy Using Version Five: " + settings2.getUseSocksProxyFive());
                System.setProperty("socksProxyHost", settings2.getSocksProxyHost());
                System.setProperty("socksProxyPort", String.valueOf(settings2.getSocksProxyPort()));
                if (!settings2.getUseSocksProxyFive()) {
                    System.setProperty("socksProxyVersion", "4");
                }//Set Socks Version To Four if we aren't using v5
            }
        }
    }
     */

    private static void checkIfRunningInsideOneDrive(File launcherRoot) {
        if (OperatingSystem.getOperatingSystem() != OperatingSystem.WINDOWS) {
            return;
        }

        Path launcherRootPath = launcherRoot.toPath();

        for (String varName : new String[]{"OneDrive", "OneDriveConsumer"}) {
            String varValue = System.getenv(varName);
            if (varValue == null || varValue.isEmpty()) {
                continue;
            }

            Path oneDrivePath = new File(varValue).toPath();

            if (launcherRootPath.startsWith(oneDrivePath)) {
                JOptionPane.showMessageDialog(null, "Technic Launcher cannot run inside OneDrive. Please move it out of OneDrive, in the launcher settings.", "Cannot run inside OneDrive", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static void setupLogging(LauncherDirectories directories, ResourceLoader resources) {
        System.out.println("Setting up logging");
        final Logger logger = Utils.getLogger();
        File logDirectory = new File(directories.getLauncherDirectory(), "logs");
        if (!logDirectory.exists()) {Utils.ignored=logDirectory.mkdir();}
        File logs = new File(logDirectory, "tantalumLauncher_%D.log");
        RotatingFileHandler fileHandler = new RotatingFileHandler(logs.getPath());

        fileHandler.setFormatter(new BuildLogFormatter(buildNumber.getBuildNumber()));

        fileHandler.setLevel(Level.ALL);
        for (Handler h : logger.getHandlers()) {
            logger.removeHandler(h);
        }
        logger.addHandler(fileHandler);
        logger.setUseParentHandlers(false);

        LauncherMain.consoleFrame = new ConsoleFrame(2500, resources.getImage("icon.png"));
        Console console = new Console(LauncherMain.consoleFrame, buildNumber.getBuildNumber());

        logger.addHandler(new ConsoleHandler(console));

        System.setOut(new PrintStream(new LoggerOutputStream(console, Level.WARNING, logger), true));
        System.setErr(new PrintStream(new LoggerOutputStream(console, Level.SEVERE, logger), true));
        org.apache.log4j.Appender rootappender = new org.apache.log4j.ConsoleAppender(new org.apache.log4j.SimpleLayout(), "System.out");
        org.apache.log4j.LogManager.getRootLogger().addAppender(rootappender);
        org.apache.log4j.LogManager.getRootLogger().setLevel(org.apache.log4j.Level.ALL);
        org.apache.log4j.LogManager.getRootLogger().info("Apache Logger Has Initialised");
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Unhandled Exception in " + t, e);
        });
    }

    private static void runStartupDebug(TantalumSettings settings, StartupParameters parameters) {
        // Startup debug messages
        //Try and Get default Platform and Solder Info//
        Utils.getLogger().info("OS: " + System.getProperty("os.name").toLowerCase(Locale.ENGLISH));
        Utils.getLogger().info("Identified as "+ OperatingSystem.getOperatingSystem().getName());
        Utils.getLogger().info("Java: " + System.getProperty("java.version") + " " + JavaUtils.getJavaBitness() + "-bit (" + System.getProperty("os.arch") + ")");
        Utils.getLogger().info("Launcher Build: " + TantalumConstants.getBuildNumber().getBuildNumber());
        Utils.getLogger().log(Level.SEVERE,"This is Severe Level");
        Utils.getLogger().log(Level.WARNING,"This is Warning Level");
        Utils.getLogger().log(Level.INFO, "This is info Level");
        Utils.getLogger().log(Level.CONFIG, "This is Config Level");
        Utils.getLogger().log(Level.DEBUG, "This is Debug Level");
        Utils.getLogger().log(Level.FINE, "This is Fine Level");
        Utils.getLogger().log(Level.FINER, "This is Finer Level");
        Utils.getLogger().log(Level.FINEST, "This is Finest Level");
        final String[] domains = {"tantalum.tharow.net","tantalum-auth.azurewebsites.net","minecraft.net", "session.minecraft.net", "textures.minecraft.net", "libraries.minecraft.net", "authserver.mojang.com", "account.mojang.com", "technicpack.net", "launcher.technicpack.net", "api.technicpack.net", "mirror.technicpack.net", "solder.technicpack.net", "files.minecraftforge.net"};
        for (String domain : domains) {
            try {
                Collection<InetAddress> inetAddresses = Arrays.asList(InetAddress.getAllByName(domain));
                String ips = inetAddresses.stream().map(InetAddress::getHostAddress).collect(Collectors.joining(", "));
                Utils.getLogger().info(domain + " resolves to [" + ips + "]");
            } catch (UnknownHostException ex) {
                Utils.getLogger().log(Level.SEVERE, "Failed to resolve " + domain + ": " + ex);
            }
        }
    }

    private static void injectNewRootCerts() {
        injectNewRootCerts(false);
    }

    @SuppressWarnings("ConstantConditions")
    private static void injectNewRootCerts(boolean forceInjection) {
        // Adapted from Forge installer
        final String javaVersion = System.getProperty("java.version");
        if(forceInjection){Utils.getLogger().warning("Forcing New Root Certificates");}
        if ((javaVersion == null || !javaVersion.startsWith("1.8.0_")) && !forceInjection) {
            Utils.getLogger().log(Level.INFO, "Don't need to inject new root certificates: Java is newer than 8 (" + javaVersion + ")");
            return;
        }

        try {
            if ((Integer.parseInt(javaVersion.substring("1.8.0_".length())) >= 101) && !forceInjection) {
                Utils.getLogger().log(Level.INFO, "Don't need to inject new root certificates: Java 8 is 101+ (" + javaVersion + ")");
                return;
            }
        } catch (final NumberFormatException e) {
            Utils.getLogger().log(Level.WARNING,
                    "Couldn't parse Java version, can't inject new root certs",
                    e);
            return;
        }

        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            final Path ksPath = Paths.get(System.getProperty("java.home"),"lib", "security", "cacerts");
            keyStore.load(Files.newInputStream(ksPath), "changeit".toCharArray());
            Map<String, Certificate> jdkTrustStore = Collections.list(keyStore.aliases()).stream().collect(Collectors.toMap(a -> a, (String alias) -> {
                try {
                    return keyStore.getCertificate(alias);
                } catch (KeyStoreException e) {
                    Utils.getLogger().log(Level.WARNING, "Failed to get certificate", e);
                    return null;
                }
            }));

            KeyStore leKS = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream leKSFile = LauncherMain.class.getResourceAsStream("/net/tharow/tantalum/launcher/resources/technickeystore.jks");
            leKS.load(leKSFile, "technicrootca".toCharArray());
            Map<String, Certificate> leTrustStore = Collections.list(leKS.aliases()).stream().collect(Collectors.toMap(a -> a, (String alias) -> {
                try {
                    return leKS.getCertificate(alias);
                } catch (KeyStoreException e) {
                    Utils.getLogger().log(Level.WARNING, "Failed to get certificate", e);
                    return null;
                }
            }));

            KeyStore mergedTrustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            mergedTrustStore.load(null, new char[0]);
            for (final Map.Entry<String, Certificate> entry : jdkTrustStore.entrySet()) {
                mergedTrustStore.setCertificateEntry(entry.getKey(), entry.getValue());
            }
            for (final Map.Entry<String , Certificate> entry : leTrustStore.entrySet()) {
                mergedTrustStore.setCertificateEntry(entry.getKey(), entry.getValue());
            }

            TrustManagerFactory instance = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            instance.init(mergedTrustStore);
            SSLContext tls = SSLContext.getInstance("TLS");
            tls.init(null, instance.getTrustManagers(), null);
            HttpsURLConnection.setDefaultSSLSocketFactory(tls.getSocketFactory());
            Utils.getLogger().log(Level.INFO, "Injected new root certificates");
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException | KeyManagementException e) {
            Utils.getLogger().log(Level.WARNING, "Failed to inject new root certificates. Problems might happen");
            e.printStackTrace();
        }
    }


    private static void startLauncher(final TantalumSettings settings, StartupParameters startupParameters, final LauncherDirectories directories, ResourceLoader resources) {
        UIManager.put( "ComboBox.disabledBackground", LauncherFrame.COLOR_FORMELEMENT_INTERNAL );
        UIManager.put( "ComboBox.disabledForeground", LauncherFrame.COLOR_GREY_TEXT );
        System.setProperty("xr.load.xml-reader", "org.ccil.cowan.tagsoup.Parser");

       // runProxySetup(settings, directories.getTorRelayDirectory()); //Check and run proxy and custom dns settings

        //Remove all log files older than a week
        new Thread(() -> {
            Iterator<File> files = FileUtils.iterateFiles(new File(directories.getLauncherDirectory(), "logs"), new String[] {"log"}, false);
            while (files.hasNext()) {
                File logFile = files.next();
                if (logFile.exists() && (new DateTime(logFile.lastModified())).isBefore(DateTime.now().minusWeeks(1)))
                    //noinspection ResultOfMethodCallIgnored
                    logFile.delete();
            }
        }).start();

        final SplashScreen splash = new SplashScreen(resources.getImage("launch_splash.png"), 0);
        Color bg = LauncherFrame.COLOR_FORMELEMENT_INTERNAL;
        splash.getContentPane().setBackground(new Color (bg.getRed(),bg.getGreen(),bg.getBlue(),255));
        splash.pack();
        splash.setLocationRelativeTo(null);
        splash.setVisible(true);

        JavaVersionRepository javaVersions = new JavaVersionRepository();
        (new InstalledJavaSource()).enumerateVersions(javaVersions);
        FileJavaSource javaVersionFile = FileJavaSource.load(new File(settings.getTechnicRoot(), "javaVersions.json"));
        javaVersionFile.enumerateVersions(javaVersions);
        javaVersions.selectVersion(settings.getJavaVersion(), settings.getJavaBitness());

        TantalumUserStore users = TantalumUserStore.load(new File(directories.getLauncherDirectory(),"users.json"));
        MojangAuthenticator mojangAuthenticator = new MojangAuthenticator(users.getClientToken());
        AuthlibAuthenticator authlibAuthenticator = new AuthlibAuthenticator(users.getClientToken());
        MicrosoftAuthenticator microsoftAuthenticator = new MicrosoftAuthenticator(new File(directories.getLauncherDirectory(), "oauth"));
        UserModel userModel = new UserModel(users, microsoftAuthenticator, mojangAuthenticator, authlibAuthenticator);



        IModpackResourceType iconType = new IconResourceType();
        IModpackResourceType logoType = new LogoResourceType();
        IModpackResourceType backgroundType = new BackgroundResourceType();

        PackResourceMapper iconMapper = new PackResourceMapper(directories, resources.getImage("icon.png"), iconType);
        ImageRepository<ModpackModel> iconRepo = new ImageRepository<>(iconMapper, new PackImageStore(iconType));
        ImageRepository<ModpackModel> logoRepo = new ImageRepository<>(new PackResourceMapper(directories, resources.getImage("modpack/ModImageFiller.png"), logoType), new PackImageStore(logoType));
        ImageRepository<ModpackModel> backgroundRepo = new ImageRepository<>(new PackResourceMapper(directories, null, backgroundType), new PackImageStore(backgroundType));

        ImageRepository<IUserType> skinRepo = new ImageRepository<>(new TechnicFaceMapper(directories, resources), new MinotarFaceImageStore("https://tantalum-auth.azurewebsites.net/platform/"));

        ImageRepository<AuthorshipInfo> avatarRepo = new ImageRepository<>(new TantalumAvatarMapper(directories, resources), new WebAvatarImageStore());

        ISolderApi httpSolder = new HttpSolderApi(settings.getClientId());
        ISolderApi solder = new CachedSolderApi(directories, httpSolder, 60 * 60);

        TantalumPlatformStore platforms = TantalumPlatformStore.load(new File(directories.getLauncherDirectory(),"platforms.json"));
        if (startupParameters.getPlatformUrl().isEmpty()){
            startupParameters.getPlatformUrl().add(0,"https://api.technicpack.net/");
            startupParameters.getPlatformUrl().add(1,"https://tantalum-auth.azurewebsites.net/platform/");
        }
        HttpPlatformApi httpPlatform = new HttpPlatformApi(platforms);
        //Utils.getLogger().log(Level.INFO, buildNumber.getBuildNumber());
        IPlatformApi platform = new ModpackCachePlatformApi(httpPlatform, 60 * 60, directories);

        IInstalledPackRepository packStore = TechnicInstalledPackStore.load(new File(directories.getLauncherDirectory(), "installedPacks.json"));
        IAuthoritativePackSource packInfoRepository = new PlatformPackInfoRepository(platform, solder);
        //Debug.getInstalledConfig(packInfoRepository, packStore);
        //var source = new SolderPackSource("http://solder.technicpack.net/api/",solder);
        //Debug.getPackSourceConfig(packInfoRepository, source);

        ArrayList<IMigrator> migrators = new ArrayList<>(1);
        migrators.add(new InitialV3Migrator(platform));
        SettingsFactory.migrateSettings(settings, packStore, directories, users, migrators);

        PackLoader packList = new PackLoader(directories, packStore, packInfoRepository);

        String solderUrl;
        if(startupParameters.getSolderUrl() != null){solderUrl = startupParameters.getSolderUrl();}
        else{solderUrl = settings.getSolderURL();}
        ModpackSelector selector = new ModpackSelector(resources, packList, new SolderPackSource(solderUrl, solder), solder, platform, iconRepo, settings);
        //ModpackSelector selector = new ModpackSelector(resources, packList, new FeaturedPackSource("http://platform.test/"), solder, platform, iconRepo, settings);
        selector.setBorder(BorderFactory.createEmptyBorder());
        userModel.addAuthListener(selector);

        resources.registerResource(selector);

        DiscoverInfoPanel discoverInfoPanel = new DiscoverInfoPanel(resources, settings.getDiscoverURL(), platform, directories, selector);

        MinecraftLauncher launcher = new MinecraftLauncher(platform, directories, userModel, javaVersions, buildNumber);
        //noinspection rawtypes
        ModpackInstaller modpackInstaller = new ModpackInstaller(platform, settings.getClientId());
        Installer installer = new Installer(startupParameters, directories, modpackInstaller, launcher, settings, iconMapper);

        final LauncherFrame frame = new LauncherFrame(resources, skinRepo, userModel, settings, selector, iconRepo, logoRepo, backgroundRepo, installer, avatarRepo, platform, directories, packStore, startupParameters, discoverInfoPanel, javaVersions, javaVersionFile, buildNumber);

        //final LauncherFrame frame2 = new LauncherFrame(resources, skinRepo, userModel, settings, selector, iconRepo, logoRepo, backgroundRepo, installer, avatarRepo, platform, directories, packStore, startupParameters, discoverInfoPanel, javaVersions, javaVersionFile, buildNumber);


        userModel.addAuthListener(frame);

        ActionListener listener = e -> {
            splash.dispose();
            if (settings.getLaunchToModpacks())
                frame.selectTab("modpacks");
        };

        discoverInfoPanel.setLoadListener(listener);




        LoginFrame login = new LoginFrame(resources, settings, userModel, skinRepo, startupParameters, javaVersions, buildNumber, javaVersionFile);
        userModel.addAuthListener(login);
        userModel.addAuthListener(user -> {
            if (user == null)
                splash.dispose();
        });

        userModel.startupAuth();

        Utils.sendTracking("runLauncher", "run", buildNumber.getBuildNumber(), settings.getClientId());
    }
}
