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

package net.tharow.tantalum.launcher.launch;

import net.tharow.tantalum.launcher.settings.StartupParameters;
import net.tharow.tantalum.launcher.settings.TantalumSettings;
import net.tharow.tantalum.launcher.ui.LauncherFrame;
import net.tharow.tantalum.launcher.ui.components.FixRunDataDialog;
import net.tharow.tantalum.launchercore.TantalumConstants;
import net.tharow.tantalum.launchercore.exception.BuildInaccessibleException;
import net.tharow.tantalum.launchercore.exception.CacheDeleteException;
import net.tharow.tantalum.launchercore.exception.DownloadException;
import net.tharow.tantalum.launchercore.exception.PackNotAvailableOfflineException;
import net.tharow.tantalum.launchercore.install.InstallTasksQueue;
import net.tharow.tantalum.launchercore.install.LauncherDirectories;
import net.tharow.tantalum.launchercore.install.ModpackInstaller;
import net.tharow.tantalum.launchercore.install.Version;
import net.tharow.tantalum.launchercore.install.tasks.*;
import net.tharow.tantalum.launchercore.install.verifiers.MD5FileVerifier;
import net.tharow.tantalum.launchercore.install.verifiers.ValidZipFileVerifier;
import net.tharow.tantalum.launchercore.launch.java.IJavaVersion;
import net.tharow.tantalum.launchercore.launch.java.JavaVersionRepository;
import net.tharow.tantalum.launchercore.modpacks.ModpackModel;
import net.tharow.tantalum.launchercore.modpacks.RunData;
import net.tharow.tantalum.launchercore.modpacks.resources.PackResourceMapper;
import net.tharow.tantalum.launchercore.util.DownloadListener;
import net.tharow.tantalum.launchercore.util.LaunchAction;
import net.tharow.tantalum.minecraftcore.install.tasks.*;
import net.tharow.tantalum.minecraftcore.launch.LaunchOptions;
import net.tharow.tantalum.minecraftcore.launch.MinecraftLauncher;
import net.tharow.tantalum.minecraftcore.mojang.version.MojangVersion;
import net.tharow.tantalum.minecraftcore.mojang.version.MojangVersionBuilder;
import net.tharow.tantalum.minecraftcore.mojang.version.builder.FileVersionBuilder;
import net.tharow.tantalum.minecraftcore.mojang.version.builder.MojangVersionRetriever;
import net.tharow.tantalum.minecraftcore.mojang.version.builder.retrievers.HttpFileRetriever;
import net.tharow.tantalum.minecraftcore.mojang.version.builder.retrievers.ZipFileRetriever;
import net.tharow.tantalum.minecraftcore.mojang.version.chain.ChainVersionBuilder;
import net.tharow.tantalum.rest.io.Modpack;
import net.tharow.tantalum.rest.io.PackInfo;
import net.tharow.tantalum.ui.lang.ResourceLoader;
import net.tharow.tantalum.utilslib.Memory;
import net.tharow.tantalum.utilslib.OperatingSystem;
import net.tharow.tantalum.utilslib.Utils;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;
import java.util.zip.ZipException;

public class Installer {
    protected final ModpackInstaller<MojangVersion> installer;
    protected final MinecraftLauncher launcher;
    protected final TantalumSettings settings;
    protected final PackResourceMapper packIconMapper;
    protected final StartupParameters startupParameters;
    protected final LauncherDirectories directories;
    protected final Object cancelLock = new Object();
    protected boolean isCancelledByUser = false;

    private Thread runningThread;
    private LauncherUnhider launcherUnhider;

    public Installer(StartupParameters startupParameters, LauncherDirectories directories, ModpackInstaller installer, MinecraftLauncher launcher, TantalumSettings settings, PackResourceMapper packIconMapper) {
        //noinspection unchecked
        this.installer = installer;
        this.launcher = launcher;
        this.settings = settings;
        this.packIconMapper = packIconMapper;
        this.startupParameters = startupParameters;
        this.directories = directories;
    }

    public void cancel() {
        Utils.getLogger().info("User pressed cancel button.");
        synchronized (cancelLock) {
            isCancelledByUser = true;
        }
        runningThread.interrupt();
    }

    public void justInstall(final ResourceLoader resources, final ModpackModel pack, final String build, final boolean doFullInstall, final LauncherFrame frame, final DownloadListener listener) {
        internalInstallAndRun(resources, pack, build, doFullInstall, frame, listener, false);
    }

    public void installAndRun(final ResourceLoader resources, final ModpackModel pack, final String build, final boolean doFullInstall, final LauncherFrame frame, final DownloadListener listener) {
        internalInstallAndRun(resources, pack, build, doFullInstall, frame, listener, true);
    }

    protected void internalInstallAndRun(final ResourceLoader resources, final ModpackModel pack, final String build, final boolean doFullInstall, final LauncherFrame frame, final DownloadListener listener, final boolean doLaunch) {
        runningThread = new Thread(() -> {
            boolean everythingWorked = false;

            try {
                MojangVersion version;

                InstallTasksQueue<MojangVersion> tasksQueue = new InstallTasksQueue<>(listener);
                MojangVersionBuilder versionBuilder = createVersionBuilder(pack, tasksQueue);

                if (!pack.isLocalOnly() && build != null && !build.isEmpty()) {
                    buildTasksQueue(tasksQueue, resources, pack, build, doFullInstall, versionBuilder);

                    version = installer.installPack(tasksQueue, pack, build);
                } else {
                    version = versionBuilder.buildVersionFromKey(null);

                    if (version != null)
                        pack.initDirectories();
                }

                if (doLaunch) {
                    if (version == null) {
                        throw new PackNotAvailableOfflineException(pack.getDisplayName());
                    }

                    boolean usingMojangJava = version.getJavaVersion() != null && settings.shouldUseMojangJava();

                    JavaVersionRepository javaVersions = launcher.getJavaVersions();
                    Memory memoryObj = Memory.getClosestAvailableMemory(Memory.getMemoryFromId(settings.getMemory()), javaVersions.getSelectedVersion().is64Bit());
                    long memory = memoryObj.getMemoryMB();
                    String versionNumber = javaVersions.getSelectedVersion().getVersionNumber();
                    RunData data = pack.getRunData();

                    if (data != null && !data.isRunDataValid(memory, versionNumber, usingMojangJava)) {
                        FixRunDataDialog dialog = new FixRunDataDialog(frame, resources, data, javaVersions, memoryObj, settings.shouldAutoAcceptModpackRequirements(), usingMojangJava);
                        dialog.setVisible(true);
                        if (dialog.getResult() == FixRunDataDialog.Result.ACCEPT) {
                            memoryObj = dialog.getRecommendedMemory();
                            memory = memoryObj.getMemoryMB();
                            IJavaVersion recommendedJavaVersion = dialog.getRecommendedJavaVersion();
                            javaVersions.selectVersion(recommendedJavaVersion.getVersionNumber(), recommendedJavaVersion.is64Bit());

                            if (dialog.shouldRemember()) {
                                settings.setAutoAcceptModpackRequirements(true);
                            }
                        } else
                            return;
                    }

                    if (!usingMojangJava && RunData.isJavaVersionAtLeast(versionNumber, "1.9")) {
                        int result = JOptionPane.showConfirmDialog(frame, resources.getString("launcher.jverwarning", versionNumber), resources.getString("launcher.jverwarning.title"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                        if (result != JOptionPane.YES_OPTION)
                            return;
                    }

                    LaunchAction launchAction = settings.getLaunchAction();

                    if (launchAction == null || launchAction == LaunchAction.HIDE) {
                        launcherUnhider = new LauncherUnhider(settings, frame);
                    } else
                        launcherUnhider = null;

                    LaunchOptions options = new LaunchOptions(pack.getDisplayName(), packIconMapper.getImageLocation(pack).getAbsolutePath(), settings);
                    launcher.launch(pack, memory, options, launcherUnhider, version);

                    if (launchAction == null || launchAction == LaunchAction.HIDE) {
                        frame.setVisible(false);
                    } else if (launchAction == LaunchAction.NOTHING) {
                        EventQueue.invokeLater(frame::launchCompleted);
                    } else if (launchAction == LaunchAction.CLOSE) {
                        System.exit(0);
                    }
                }

                everythingWorked = true;
            } catch (InterruptedException e) {
                boolean cancelledByUser = false;
                synchronized (cancelLock) {
                    if (isCancelledByUser) {
                        cancelledByUser = true;
                        isCancelledByUser = false;
                    }
                }

                //Canceled by user
                if (!cancelledByUser) {
                    if (e.getCause() != null)
                        Utils.getLogger().info("Cancelled by exception.");
                    else
                        Utils.getLogger().info("Cancelled by code.");
                    e.printStackTrace();
                } else
                    Utils.getLogger().info("Cancelled by user.");
            } catch (PackNotAvailableOfflineException e) {
                JOptionPane.showMessageDialog(frame, e.getMessage(), resources.getString("launcher.installerror.unavailable"), JOptionPane.WARNING_MESSAGE);
            } catch (DownloadException e) {
                JOptionPane.showMessageDialog(frame, resources.getString("launcher.installerror.download", pack.getDisplayName(), e.getMessage()), resources.getString("launcher.installerror.title"), JOptionPane.WARNING_MESSAGE);
            } catch (ZipException e) {
                JOptionPane.showMessageDialog(frame, resources.getString("launcher.installerror.unzip", pack.getDisplayName(), e.getMessage()), resources.getString("launcher.installerror.title"), JOptionPane.WARNING_MESSAGE);
            } catch (CacheDeleteException e) {
                JOptionPane.showMessageDialog(frame, resources.getString("launcher.installerror.cache", pack.getDisplayName(), e.getMessage()), resources.getString("launcher.installerror.title"), JOptionPane.WARNING_MESSAGE);
            } catch (BuildInaccessibleException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(frame, e.getMessage(), resources.getString("launcher.installerror.title"), JOptionPane.WARNING_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (!everythingWorked || !doLaunch) {
                    EventQueue.invokeLater(frame::launchCompleted);
                }
            }
        }) {
            ///Interrupt is being called from a mysterious source, so unless this is a user-initiated cancel
            ///Let's print the stack trace of the interrupter.
            @Override
            public void interrupt() {
                boolean userCancelled = false;
                synchronized (cancelLock) {
                    if (isCancelledByUser)
                        userCancelled = true;
                }

                if (!userCancelled) {
                    Utils.getLogger().info("Mysterious interruption source.");
                    try {
                        //I am a charlatan and a hack.
                        throw new Exception("Grabbing stack trace- this isn't necessarily an error.");
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
                super.interrupt();
            }
        };
        runningThread.start();
    }

    public boolean isCurrentlyRunning() {
        if (runningThread != null && runningThread.isAlive())
            return true;
        return launcherUnhider != null && !launcherUnhider.hasExited();
    }

    public void buildTasksQueue(InstallTasksQueue<? extends MojangVersion> queue, ResourceLoader resources, ModpackModel modpack, String build, boolean doFullInstall, MojangVersionBuilder versionBuilder) throws CacheDeleteException, BuildInaccessibleException {
        PackInfo packInfo = modpack.getPackInfo();
        Modpack modpackData = packInfo.getModpack(build);

        if (modpackData.getGameVersion() == null)
            return;

        String minecraft = modpackData.getGameVersion();
        Version installedVersion = modpack.getInstalledVersion();

        TaskGroup examineModpackData = new TaskGroup(resources.getString("install.message.examiningmodpack"));
        TaskGroup verifyingFiles = new TaskGroup(resources.getString("install.message.verifyingfiles"));
        TaskGroup downloadingMods = new TaskGroup(resources.getString("install.message.downloadmods"));
        TaskGroup installingMods = new TaskGroup(resources.getString("install.message.installmods"));
        TaskGroup checkVersionFile = new TaskGroup(resources.getString("install.message.checkversionfile"));
        TaskGroup installVersionFile = new TaskGroup(resources.getString("install.message.installversionfile"));
        TaskGroup rundataTaskGroup = new TaskGroup(resources.getString("install.message.runData"));
        TaskGroup examineVersionFile = new TaskGroup(resources.getString("install.message.examiningversionfile"));
        TaskGroup grabLibs = new TaskGroup(resources.getString("install.message.grablibraries"));
        TaskGroup checkNonMavenLibs = new TaskGroup(resources.getString("install.message.nonmavenlibs"));
        TaskGroup installingLibs = new TaskGroup(resources.getString("install.message.installlibs"));
        TaskGroup installingMinecraft = new TaskGroup(resources.getString("install.message.installminecraft"));
        TaskGroup examineIndex = new TaskGroup(resources.getString("install.message.examiningindex"));
        TaskGroup verifyingAssets = new TaskGroup(resources.getString("install.message.verifyassets"));
        TaskGroup installingAssets = new TaskGroup(resources.getString("install.message.installassets"));
        TaskGroup examineJava = new TaskGroup("Examining Java runtime...");
        TaskGroup downloadJava = new TaskGroup("Downloading Java runtime...");

        queue.addTask(examineModpackData);
        queue.addTask(verifyingFiles);
        queue.addTask(downloadingMods);
        queue.addTask(installingMods);
        queue.addTask(checkVersionFile);
        queue.addTask(installVersionFile);
        queue.addTask(rundataTaskGroup);
        queue.addTask(examineVersionFile);
        queue.addTask(grabLibs);
        queue.addTask(checkNonMavenLibs);
        queue.addTask(installingLibs);
        queue.addTask(installingMinecraft);
        queue.addTask(examineIndex);
        queue.addTask(verifyingAssets);
        queue.addTask(installingAssets);
        queue.addTask(examineJava);
        queue.addTask(downloadJava);
        if (OperatingSystem.getOperatingSystem() == OperatingSystem.OSX)
            queue.addTask(new CopyDylibJnilibTask(modpack));

        // Add FML libs
        String fmlLibsZip;
        File modpackFmlLibDir = new File(modpack.getInstalledDirectory(), "lib");
        HashMap<String, String> fmlLibs = new HashMap<>();

        switch (minecraft) {
            /*case "1.4", "1.4.1", "1.4.2", "1.4.3", "1.4.4", "1.4.5", "1.4.6", "1.4.7" -> fmlLibsZip = "fml_libs.zip";
            case "1.5" -> {
                fmlLibsZip = "fml_libs15.zip";
                fmlLibs.put("deobfuscation_data_1.5.zip", "dba6d410a91a855f3b84457c86a8132a");
            }
            case "1.5.1" -> {
                fmlLibsZip = "fml_libs15.zip";
                fmlLibs.put("deobfuscation_data_1.5.1.zip", "c4fc2fedba60d920e4c7f9a095b2b883");
            }*/
            case "1.5.2" : {
                fmlLibsZip = "fml_libs15.zip";
                fmlLibs.put("deobfuscation_data_1.5.2.zip", "270d9775872cc9fa773389812cab91fe");
            }
            default : fmlLibsZip = "";
        }

        if (!fmlLibsZip.isEmpty()) {
            verifyingFiles.addTask(new EnsureFileTask<>(new File(directories.getCacheDirectory(), fmlLibsZip), new ValidZipFileVerifier(), modpackFmlLibDir, TantalumConstants.technicFmlLibRepo + fmlLibsZip, installingLibs, installingLibs));
        }

        if (!fmlLibs.isEmpty()) {
            fmlLibs.forEach((name, md5) -> {
                MD5FileVerifier verifier = null;

                if (!md5.isEmpty())
                    verifier = new MD5FileVerifier(md5);

                File cached = new File(directories.getCacheDirectory(), name);
                File target = new File(modpackFmlLibDir, name);

                if (!target.exists() || (verifier != null && !verifier.isFileValid(target)) ) {
                    verifyingFiles.addTask(new EnsureFileTask<>(cached, verifier, null, TantalumConstants.technicFmlLibRepo + name, installingLibs, installingLibs));
                    installingLibs.addTask(new CopyFileTask(cached, target));
                }
            });
        }

        if (doFullInstall) {
            //If we're installing a new version of modpack, then we need to get rid of the existing version.json
            File versionFile = new File(modpack.getBinDir(), "version.json");
            if (versionFile.exists()) {
                if (!versionFile.delete()) {
                    throw new CacheDeleteException(versionFile.getAbsolutePath());
                }
            }

            // Remove bin/install_profile.json, which is used by ForgeWrapper to install Forge in Minecraft 1.13+
            // (and the latest few Forge builds in 1.12.2)
            File installProfileFile = new File(modpack.getBinDir(), "install_profile.json");
            if (installProfileFile.exists()) {
                if (!installProfileFile.delete()) {
                    throw new CacheDeleteException(installProfileFile.getAbsolutePath());
                }
            }

            // Delete all other version JSON files in the bin dir
            File[] binFiles = modpack.getBinDir().listFiles();
            if (binFiles != null) {
                final Pattern minecraftVersionPattern = Pattern.compile("^[0-9]+(\\.[0-9]+)+\\.json$");
                for (File binFile : binFiles) {
                    if (minecraftVersionPattern.matcher(binFile.getName()).matches()) {
                        if (!binFile.delete()) {
                            throw new CacheDeleteException(binFile.getAbsolutePath());
                        }
                    }
                }
            }

            // Remove the runData file between updates/reinstall as well
            File runData = new File(modpack.getBinDir(), "runData");
            if (runData.exists()) {
                if (!runData.delete()) {
                    throw new CacheDeleteException(runData.getAbsolutePath());
                }
            }

            // Remove the bin/modpack.jar file
            // This prevents issues when upgrading a modpack between a version that has a modpack.jar, and
            // one that doesn't. One example of this is updating BareBonesPack from a Forge to a Fabric build.
            File modpackJar = new File(modpack.getBinDir(), "modpack.jar");
            if (modpackJar.exists()) {
                if (!modpackJar.delete()) {
                    throw new CacheDeleteException(modpackJar.getAbsolutePath());
                }
            }

            examineModpackData.addTask(new CleanupAndExtractModpackTask(modpack, modpackData, verifyingFiles, downloadingMods, installingMods));
        }

        if (doFullInstall)
            rundataTaskGroup.addTask(new WriteRundataFile(modpack, modpackData));
        else
            rundataTaskGroup.addTask(new CheckRundataFile(modpack, modpackData, rundataTaskGroup));

        checkVersionFile.addTask(new VerifyVersionFilePresentTask(modpack, minecraft, versionBuilder));
        examineVersionFile.addTask(new HandleVersionFileTask(modpack, directories, checkNonMavenLibs, grabLibs, installingLibs, installingLibs, versionBuilder));
        examineVersionFile.addTask(new EnsureAssetsIndexTask(directories.getAssetsDirectory(), modpack, installingMinecraft, examineIndex, verifyingAssets, installingAssets, installingAssets));
        if(settings.shouldUseMojangJava()){
            examineJava.addTask(new EnsureJavaRuntimeManifestTask(directories.getRuntimesDirectory(), modpack, examineJava, downloadJava));
        }

        if (doFullInstall || (installedVersion != null && installedVersion.isLegacy()))
            installingMinecraft.addTask(new InstallMinecraftIfNecessaryTask(modpack, minecraft, directories.getCacheDirectory()));
    }

    private MojangVersionBuilder createVersionBuilder(ModpackModel modpack, InstallTasksQueue<? extends MojangVersion> tasksQueue) {

        ZipFileRetriever zipVersionRetriever = new ZipFileRetriever(new File(modpack.getBinDir(), "modpack.jar"));
        HttpFileRetriever fallbackVersionRetriever = new HttpFileRetriever(TantalumConstants.technicVersions, tasksQueue.getDownloadListener());

        ArrayList<MojangVersionRetriever> fallbackRetrievers = new ArrayList<>(1);
        fallbackRetrievers.add(fallbackVersionRetriever);

        File versionJson = new File(modpack.getBinDir(), "version.json");

        // This always gets the version.json from the modpack.jar (it ignores "key"), cached as bin/version.json
        FileVersionBuilder zipVersionBuilder = new FileVersionBuilder(versionJson, zipVersionRetriever, fallbackRetrievers);
        // This gets the "key" from bin/$key.json if it exists, otherwise it downloads it from our repo into that location
        FileVersionBuilder webVersionBuilder = new FileVersionBuilder(modpack.getBinDir(), null, fallbackRetrievers);

        return new ChainVersionBuilder(zipVersionBuilder, webVersionBuilder);
    }
}
