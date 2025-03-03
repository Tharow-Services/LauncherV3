

package net.tharow.tantalum.launcher.ui.components;

import net.tharow.tantalum.autoupdate.IBuildNumber;
import net.tharow.tantalum.launcher.LauncherMain;
import net.tharow.tantalum.launcher.settings.StartupParameters;
import net.tharow.tantalum.launcher.ui.InstallerFrame;
import net.tharow.tantalum.minecraftcore.launch.WindowType;
import net.tharow.tantalum.ui.listitems.javaversion.Best64BitVersionItem;
import net.tharow.tantalum.ui.listitems.javaversion.DefaultVersionItem;
import net.tharow.tantalum.ui.listitems.javaversion.JavaVersionItem;
import net.tharow.tantalum.launchercore.launch.java.IJavaVersion;
import net.tharow.tantalum.launchercore.launch.java.JavaVersionRepository;
import net.tharow.tantalum.launchercore.launch.java.source.FileJavaSource;
import net.tharow.tantalum.launchercore.launch.java.version.FileBasedJavaVersion;
import net.tharow.tantalum.ui.controls.TooltipWarning;
import net.tharow.tantalum.ui.controls.lang.LanguageCellRenderer;
import net.tharow.tantalum.ui.controls.list.SimpleButtonComboUI;
import net.tharow.tantalum.ui.controls.list.popupformatters.RoundedBorderFormatter;
import net.tharow.tantalum.ui.lang.IRelocalizableResource;
import net.tharow.tantalum.ui.lang.ResourceLoader;
import net.tharow.tantalum.launcher.settings.TantalumSettings;
import net.tharow.tantalum.launcher.ui.LauncherFrame;
import net.tharow.tantalum.ui.controls.LauncherDialog;
import net.tharow.tantalum.ui.controls.RoundedButton;
import net.tharow.tantalum.ui.controls.borders.RoundBorder;
import net.tharow.tantalum.ui.controls.tabs.SimpleTabPane;
import net.tharow.tantalum.ui.listitems.LanguageItem;
import net.tharow.tantalum.launcher.ui.listitems.OnLaunchItem;
import net.tharow.tantalum.launcher.ui.listitems.StreamItem;
import net.tharow.tantalum.launchercore.util.LaunchAction;
import net.tharow.tantalum.utilslib.DesktopUtils;
import net.tharow.tantalum.utilslib.Memory;
import net.tharow.tantalum.utilslib.OperatingSystem;
import net.tharow.tantalum.ui.controls.SimpleDocumentListener;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentListener;
import javax.swing.event.HyperlinkEvent;
import javax.swing.filechooser.FileFilter;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.metal.MetalComboBoxUI;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Locale;
import java.util.Objects;

public class OptionsDialog extends LauncherDialog implements IRelocalizableResource {

    private static final int DIALOG_WIDTH = 830;
    private static final int DIALOG_HEIGHT = 564;

    private final TantalumSettings settings;

    private boolean hasShownStreamInfo = false;
    private ResourceLoader resources;
    private final JavaVersionRepository javaVersions;
    private final FileJavaSource fileJavaSource;
    private final IBuildNumber buildNumber;

    private final DocumentListener javaArgsListener = new SimpleDocumentListener(this::changeJavaArgs);
    private final DocumentListener socksProxyHostListener = new SimpleDocumentListener(this::changeSocksProxyHost);
    private final DocumentListener socksProxyPortListener = new SimpleDocumentListener(this::changeSocksProxyPort);
    private final DocumentListener HTTPProxyHostListener = new SimpleDocumentListener(this::changeHTTPProxyHost);
    private final DocumentListener HTTPProxyPortListener = new SimpleDocumentListener(this::changeHTTPProxyPort);
    private final DocumentListener platformURLListener = new SimpleDocumentListener(this::changePlatformURL);
    private final DocumentListener discoverURLListener = new SimpleDocumentListener(this::changeDiscoverURL);
    private final DocumentListener defaultSolderListener = new SimpleDocumentListener(this::changeSolderURL);
    private final DocumentListener dimensionListener = new SimpleDocumentListener(this::changeWindowDimensions);
    private final DocumentListener wrapperCommandListener = new SimpleDocumentListener(this::changeWrapperCommand);


    JComboBox<JavaVersionItem> versionSelect;
    JComboBox<Memory> memSelect;
    JTextArea javaArgs;
    JComboBox<StreamItem> streamSelect;
    JComboBox<OnLaunchItem> launchSelect;
    JComboBox<LanguageItem> langSelect;
    JTextField installField;
    JTextField clientId;
    JCheckBox showConsole;
    JCheckBox launchToModpacks;
    final StartupParameters params;
    Component ramWarning;
    JCheckBox askFirstBox;
    JComboBox<String> useStencil;
    JComboBox<String> windowSelect;
    JTextField widthInput;
    JTextField heightInput;
    JTextField wrapperCommand;
    JCheckBox useMojangJava;
    JCheckBox useHTTPProxy;
    JCheckBox useSocksProxy;
    JCheckBox useDNSOnly;
    JCheckBox useTorRelay;
    JTextField socksProxyHost;
    JTextField socksProxyPort;
    JTextField HTTPProxyHost;
    JTextField HTTPProxyPort;
    JTextField platformURL;
    JTextField discoverURL;
    JTextField solderURL;

    private final boolean showAdvancedOptions;

    public OptionsDialog(final Frame owner, final TantalumSettings settings, final ResourceLoader resourceLoader, final StartupParameters params, final JavaVersionRepository javaVersions, final FileJavaSource fileJavaSource, final IBuildNumber buildNumber) {
        super(owner);

        this.settings = settings;
        this.params = params;
        this.javaVersions = javaVersions;
        this.fileJavaSource = fileJavaSource;
        this.buildNumber = buildNumber;
        this.showAdvancedOptions = settings.getAdvOptions();
        relocalize(resourceLoader);
    }

    protected void closeDialog() {
        resources.unregisterResource(this);
        dispose();
    }


    protected void changeUseSocksProxy() {
        settings.setUseSocksProxy(useSocksProxy.isSelected());
        settings.save();
        socksProxyHost.setEnabled(useSocksProxy.isSelected());
        socksProxyPort.setEnabled(useSocksProxy.isSelected());
    }

    protected void changeSocksProxyHost(){
        //System.setProperty("proxyHost",proxyHost.getText().trim());
        settings.setSocksProxyHost(socksProxyHost.getText().trim());
        settings.save();
    }

    protected void changeSocksProxyPort(){
        settings.setSocksProxyPort(Integer.parseInt(socksProxyPort.getText().trim()));
        settings.save();
    }

    protected void changeUseTorRelay(){
        settings.setUseTorRelay(useTorRelay.isSelected());
        settings.save();
    }
    //HTTP Proxy
    protected void changeUseHTTPProxy() {
        settings.setUseHTTPProxy(useHTTPProxy.isSelected());
        settings.save();
        HTTPProxyHost.setEnabled(useHTTPProxy.isSelected());
        HTTPProxyPort.setEnabled(useHTTPProxy.isSelected());
    }

    protected void changeHTTPProxyHost(){
        //System.setProperty("proxyHost",proxyHost.getText().trim());
        settings.setHTTPProxyHost(HTTPProxyHost.getText().trim());
        settings.save();
    }

    protected void changeHTTPProxyPort(){
        settings.setHTTPProxyPort(Integer.parseInt(HTTPProxyPort.getText().trim()));
        settings.save();
    }

    //Custom Dns
    protected void changeUseDNSOnly(){
        settings.setUseDNSOnly(useDNSOnly.isSelected());
        settings.save();
    }


    protected void changeSolderURL(){
        settings.setSolderURL(solderURL.getText().trim());
        settings.save();
    }

    protected void changePlatformURL(){
        settings.setPlatformURL(platformURL.getText().trim());
        settings.save();
    }

    protected void changeDiscoverURL(){
        settings.setDiscoverURL(discoverURL.getText().trim());
        settings.save();
    }

    protected void changeJavaArgs() {
        settings.setJavaArgs(javaArgs.getText().trim());
        settings.save();
    }

    protected void changeWrapperCommand() {
        settings.setWrapperCommand(wrapperCommand.getText().trim());
        settings.save();
    }

    protected void copyCid() {
        StringSelection selection = new StringSelection(clientId.getText());
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, null);
    }

    protected void changeShowConsole() {
        settings.setShowConsole(showConsole.isSelected());
        LauncherMain.consoleFrame.setVisible(showConsole.isSelected());
        settings.save();
    }

    protected void changeAskFirst() {
        settings.setAutoAcceptModpackRequirements(!askFirstBox.isSelected());
        settings.save();
    }

    protected void changeUseMojangJava() {
        settings.setUseMojangJava(useMojangJava.isSelected());
        settings.save();
    }

    protected void changeLaunchToModpacks() {
        settings.setLaunchToModpacks(launchToModpacks.isSelected());
        settings.save();
    }

    protected void changeJavaVersion() {
        assert versionSelect.getSelectedItem() != null;
        String version = ((JavaVersionItem)versionSelect.getSelectedItem()).getVersionNumber();
        boolean is64 = ((JavaVersionItem)versionSelect.getSelectedItem()).is64Bit();
        javaVersions.selectVersion(version, is64);
        settings.setJavaVersion(version);
        settings.setJavaBitness(is64);
        settings.save();
        rebuildMemoryList();
    }

    protected void selectOtherVersion() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        final String osJavaName = new File(OperatingSystem.getJavaDir()).getName();
        chooser.setFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                if (f.isDirectory())
                    return true;
                return (f.getName().equals(osJavaName));
            }

            @Override
            public String getDescription() {
                return resources.getString("launcheroptions.java.filter", osJavaName);
            }
        });

        int result = chooser.showOpenDialog(this);


        if (result == JFileChooser.APPROVE_OPTION) {
            if (chooser.getSelectedFile() == null || !chooser.getSelectedFile().exists() || !chooser.getSelectedFile().canExecute()) {
                JOptionPane.showMessageDialog(this, resources.getString("launcheroptions.java.badfile"));
                return;
            }

            FileBasedJavaVersion chosenJava = new FileBasedJavaVersion(chooser.getSelectedFile());
            if (!chosenJava.verify()) {
                JOptionPane.showMessageDialog(this, resources.getString("launcheroptions.java.badfile"));
                return;
            }

            IJavaVersion existingVersion = javaVersions.getVersion(chosenJava.getVersionNumber(), chosenJava.is64Bit());
            if (existingVersion.getJavaPath() != null) {
                JOptionPane.showMessageDialog(this, resources.getString("launcheroptions.java.versionexists"));
                return;
            }

            fileJavaSource.addVersion(chosenJava);
            javaVersions.addVersion(chosenJava);
            javaVersions.selectVersion(chosenJava.getVersionNumber(), chosenJava.is64Bit());
            JavaVersionItem item = new JavaVersionItem(chosenJava, resources);
            versionSelect.addItem(item);
            versionSelect.setSelectedItem(item);
            settings.setJavaVersion(chosenJava.getVersionNumber());
            settings.setJavaBitness(chosenJava.is64Bit());
            settings.save();
        }
    }

    protected void changeMemory() {
        settings.setMemory(((Memory) Objects.requireNonNull(memSelect.getSelectedItem())).getSettingsId());
        settings.save();
    }

    protected void changeStream() {
        assert streamSelect.getSelectedItem() != null;
        settings.setBuildStream(((StreamItem) streamSelect.getSelectedItem()).getStream());
        settings.save();

        if (!hasShownStreamInfo) {
            JOptionPane.showMessageDialog(this, resources.getString("launcheroptions.streamchange.text"), resources.getString("launcheroptions.streamchange.title"), JOptionPane.INFORMATION_MESSAGE);

            hasShownStreamInfo = true;
        }
    }

    protected void changeLaunchAction() {
        settings.setLaunchAction(((OnLaunchItem) Objects.requireNonNull(launchSelect.getSelectedItem())).getLaunchAction());
        settings.save();
    }

    protected void changeLanguage() {
        settings.setLanguageCode(((LanguageItem) Objects.requireNonNull(langSelect.getSelectedItem())).getLangCode());
        settings.save();

        resources.setLocale(((LanguageItem) langSelect.getSelectedItem()).getLangCode());
    }

    protected void changeWindowType() {
        switch (windowSelect.getSelectedIndex()) {
            case 0 : settings.setLaunchWindowType(WindowType.DEFAULT); break;
            case 1 : settings.setLaunchWindowType(WindowType.FULLSCREEN); break;
            case 2 :
                settings.setLaunchWindowType(WindowType.CUSTOM);
                changeWindowDimensions();
                break;
        }

        updateDimensionsEnabled();
    }

    protected void changeEnableStencil() {
        settings.setUseStencilBuffer(useStencil.getSelectedIndex() == 0);
    }

    protected void changeWindowDimensions() {
        String widthStr = widthInput.getText();
        String heightStr = heightInput.getText();
        int width = 800;
        int height = 600;

        try {
            width = Integer.parseInt(widthStr);
        } catch (NumberFormatException ex) {
            //Not important
        }

        try {
            height = Integer.parseInt(heightStr);
        } catch (NumberFormatException ex) {
            //Not important
        }

        settings.setLaunchWindowDimensions(width, height);
    }

    protected void reinstall() {
        final InstallerFrame frame = new InstallerFrame(resources, params, settings, getOwner());
        frame.setVisible(true);

        EventQueue.invokeLater(frame::requestFocus);

        this.dispose();
    }

    protected void openLogs() {
        DesktopUtils.open(new File(settings.getTechnicRoot().getAbsolutePath(), "logs"));
    }

    private void initControlValues() {

        javaArgs.getDocument().removeDocumentListener(javaArgsListener);
        javaArgs.setText(settings.getJavaArgs());
        javaArgs.getDocument().addDocumentListener(javaArgsListener);

        socksProxyHost.getDocument().removeDocumentListener(socksProxyHostListener);
        socksProxyHost.setText(settings.getSocksProxyHost());
        socksProxyHost.setEnabled(settings.getUseSocksProxy());
        socksProxyHost.getDocument().addDocumentListener(socksProxyHostListener);

        socksProxyPort.getDocument().removeDocumentListener(socksProxyPortListener);
        socksProxyPort.setText(String.valueOf(settings.getSocksProxyPort()));
        socksProxyPort.setEnabled(settings.getUseSocksProxy());
        socksProxyPort.getDocument().addDocumentListener(socksProxyPortListener);

        HTTPProxyHost.getDocument().removeDocumentListener(HTTPProxyHostListener);
        HTTPProxyHost.setText(settings.getHTTPProxyHost());
        HTTPProxyHost.setEnabled(settings.getUseHTTPProxy());
        HTTPProxyHost.getDocument().addDocumentListener(HTTPProxyHostListener);

        HTTPProxyPort.getDocument().removeDocumentListener(HTTPProxyPortListener);
        HTTPProxyPort.setText(String.valueOf(settings.getHTTPProxyPort()));
        HTTPProxyPort.setEnabled(settings.getUseHTTPProxy());
        HTTPProxyPort.getDocument().addDocumentListener(HTTPProxyPortListener);

        solderURL.getDocument().removeDocumentListener(defaultSolderListener);
        solderURL.setText(settings.getSolderURL());
        solderURL.getDocument().addDocumentListener(defaultSolderListener);

        platformURL.getDocument().removeDocumentListener(platformURLListener);
        platformURL.setText(settings.getPlatformURL());
        platformURL.getDocument().addDocumentListener(platformURLListener);


        wrapperCommand.getDocument().removeDocumentListener(wrapperCommandListener);
        wrapperCommand.setText(settings.getWrapperCommand());
        wrapperCommand.getDocument().addDocumentListener(wrapperCommandListener);

        installField.setText(settings.getTechnicRoot().getAbsolutePath());
        clientId.setText(settings.getClientId());

        for (ActionListener listener : showConsole.getActionListeners())
            showConsole.removeActionListener(listener);
        showConsole.setSelected(settings.getShowConsole());
        showConsole.addActionListener(e -> changeShowConsole());

        for (ActionListener listener : useHTTPProxy.getActionListeners())
            useHTTPProxy.removeActionListener(listener);
        useHTTPProxy.setSelected(settings.getUseHTTPProxy());
        useHTTPProxy.addActionListener(e -> changeUseHTTPProxy());

        for (ActionListener listener : useSocksProxy.getActionListeners())
            useSocksProxy.removeActionListener(listener);
        useSocksProxy.setSelected(settings.getUseSocksProxy());
        useSocksProxy.addActionListener(e -> changeUseSocksProxy());

        for (ActionListener listener : useDNSOnly.getActionListeners())
            useDNSOnly.removeActionListener(listener);
        useDNSOnly.setSelected(settings.getUseDNSOnly());
        useDNSOnly.addActionListener(e -> changeUseDNSOnly());

        for (ActionListener listener : askFirstBox.getActionListeners())
            askFirstBox.removeActionListener(listener);
        askFirstBox.setSelected(settings.shouldAutoAcceptModpackRequirements());
        askFirstBox.addActionListener(e -> changeAskFirst());

        for (ActionListener listener : useMojangJava.getActionListeners())
            useMojangJava.removeActionListener(listener);
        useMojangJava.setSelected(settings.shouldUseMojangJava());
        useMojangJava.addActionListener(e -> changeUseMojangJava());

        for (ActionListener listener : launchToModpacks.getActionListeners())
            launchToModpacks.removeActionListener(listener);
        launchToModpacks.setSelected(settings.getLaunchToModpacks());
        launchToModpacks.addActionListener(e -> changeLaunchToModpacks());

        for (ActionListener listener : useTorRelay.getActionListeners())
            useTorRelay.removeActionListener(listener);
        useTorRelay.setSelected(settings.getUseTorRelay());
        useTorRelay.addActionListener(e -> changeUseTorRelay());

        for (ActionListener listener : versionSelect.getActionListeners())
            versionSelect.removeActionListener(listener);

        versionSelect.removeAllItems();
        versionSelect.addItem(new DefaultVersionItem(javaVersions.getVersion(null, true), resources));

        IJavaVersion best64Bit = javaVersions.getBest64BitVersion();
        if (best64Bit != null)
            versionSelect.addItem(new Best64BitVersionItem(javaVersions.getVersion("64bit", true), resources));

        for (IJavaVersion version : javaVersions.getVersions()) {
            versionSelect.addItem(new JavaVersionItem(version, resources));
        }

        String settingsVersion = settings.getJavaVersion();
        boolean settingsBitness = settings.getJavaBitness();
        if (settingsVersion == null || settingsVersion.isEmpty() || settingsVersion.equals("default"))
            versionSelect.setSelectedIndex(0);
        else if (settingsVersion.equals("64bit"))
            versionSelect.setSelectedIndex(1);
        else {
            for (int i = 2; i < versionSelect.getItemCount(); i++) {
                if ((versionSelect.getItemAt(i)).getVersionNumber().equals(settingsVersion) && (versionSelect.getItemAt(i)).is64Bit() == settingsBitness) {
                    versionSelect.setSelectedIndex(i);
                    break;
                }
            }
        }

        versionSelect.addActionListener(e -> changeJavaVersion());

        rebuildMemoryList();

        for (ActionListener listener : streamSelect.getActionListeners()) {
            streamSelect.removeActionListener(listener);
        }
        streamSelect.removeAllItems();
        streamSelect.addItem(new StreamItem(resources.getString("launcheroptions.build.stable"), "stable"));
        streamSelect.addItem(new StreamItem(resources.getString("launcheroptions.build.beta"), "beta"));
        streamSelect.setSelectedIndex((settings.getBuildStream().equals("beta"))?1:0);
        streamSelect.addActionListener(e -> changeStream());

        for (ActionListener listener : launchSelect.getActionListeners())
            launchSelect.removeActionListener(listener);
        launchSelect.removeAllItems();
        launchSelect.addItem(new OnLaunchItem(resources.getString("launcheroptions.packlaunch.hide"), LaunchAction.HIDE));
        launchSelect.addItem(new OnLaunchItem(resources.getString("launcheroptions.packlaunch.close"), LaunchAction.CLOSE));
        launchSelect.addItem(new OnLaunchItem(resources.getString("launcheroptions.packlaunch.nothing"), LaunchAction.NOTHING));

        switch (settings.getLaunchAction()) {
            case CLOSE : launchSelect.setSelectedIndex(1); break;
            case NOTHING : launchSelect.setSelectedIndex(2); break;
            default : launchSelect.setSelectedIndex(0);
        }
        launchSelect.addActionListener(e -> changeLaunchAction());

        for (ActionListener listener : langSelect.getActionListeners())
            langSelect.removeActionListener(listener);
        langSelect.removeAllItems();

        String defaultLocaleText = resources.getString("launcheroptions.language.default");
        if (resources.isDefaultLocaleSupported()) {
            defaultLocaleText = defaultLocaleText.concat(" (" + resources.getString("launcheroptions.language.unavailable") + ")");
        }

        //noinspection unchecked
        langSelect.setRenderer(new LanguageCellRenderer(resources, null, langSelect.getBackground(), langSelect.getForeground()));
        langSelect.addItem(new LanguageItem(ResourceLoader.DEFAULT_LOCALE, defaultLocaleText, resources));
        for (int i = 0; i < LauncherMain.supportedLanguages.length; i++) {
            langSelect.addItem(new LanguageItem(resources.getCodeFromLocale(LauncherMain.supportedLanguages[i]), LauncherMain.supportedLanguages[i].getDisplayName(LauncherMain.supportedLanguages[i]), resources.getVariant(LauncherMain.supportedLanguages[i])));
        }
        if (!settings.getLanguageCode().equalsIgnoreCase(ResourceLoader.DEFAULT_LOCALE)) {
            Locale loc = resources.getLocaleFromCode(settings.getLanguageCode());

            for (int i = 0; i < LauncherMain.supportedLanguages.length; i++) {
                if (loc.equals(LauncherMain.supportedLanguages[i])) {
                    langSelect.setSelectedIndex(i+1);
                    break;
                }
            }
        }
        langSelect.addActionListener(e -> changeLanguage());

        widthInput.getDocument().removeDocumentListener(dimensionListener);
        heightInput.getDocument().removeDocumentListener(dimensionListener);
        int width = settings.getCustomWidth();
        int height = settings.getCustomHeight();

        width = (width<1)?800:width;
        height = (height<1)?600:height;
        widthInput.setText(Integer.toString(width));
        heightInput.setText(Integer.toString(height));
        widthInput.getDocument().addDocumentListener(dimensionListener);
        heightInput.getDocument().addDocumentListener(dimensionListener);

        for (ActionListener listener : windowSelect.getActionListeners()) {
            windowSelect.removeActionListener(listener);
        }
        windowSelect.removeAllItems();
        windowSelect.addItem(resources.getString("launcheroptions.video.windowSize.default"));
        windowSelect.addItem(resources.getString("launcheroptions.video.windowSize.fullscreen"));
        windowSelect.addItem(resources.getString("launcheroptions.video.windowSize.custom"));
        switch (settings.getLaunchWindowType()) {
            case DEFAULT : windowSelect.setSelectedIndex(0); break;
            case FULLSCREEN : windowSelect.setSelectedIndex(1); break;
            case CUSTOM : windowSelect.setSelectedIndex(2); break;
        }
        windowSelect.addActionListener(e -> changeWindowType());
        updateDimensionsEnabled();

        for (ActionListener listener : useStencil.getActionListeners()) {
            useStencil.removeActionListener(listener);
        }
        useStencil.removeAllItems();
        useStencil.addItem(resources.getString("launcheroptions.video.stencil.enabled"));
        useStencil.addItem(resources.getString("launcheroptions.video.stencil.disabled"));
        if (settings.shouldUseStencilBuffer())
            useStencil.setSelectedIndex(0);
        else
            useStencil.setSelectedIndex(1);
        useStencil.addActionListener(e -> changeEnableStencil());
    }

    private void rebuildMemoryList() {
        for (ActionListener listener : memSelect.getActionListeners())
            memSelect.removeActionListener(listener);

        Container parent = null;
        if (memSelect.getParent() != null) {
            parent = memSelect.getParent();
            parent.remove(memSelect);

            if (ramWarning != null) {
                parent.remove(ramWarning);
                ramWarning = null;
            }
        }

        memSelect.removeAllItems();
        long maxMemory = Memory.getAvailableMemory(javaVersions.getSelectedVersion().is64Bit());
        for (int i = 0; i < Memory.memoryOptions.length; i++) {
            if (Memory.memoryOptions[i].getMemoryMB() <= maxMemory)
                memSelect.addItem(Memory.memoryOptions[i]);
        }

        Memory currentMem = Memory.getMemoryFromId(settings.getMemory());
        Memory availableMem = Memory.getClosestAvailableMemory(currentMem, javaVersions.getSelectedVersion().is64Bit());

        if (currentMem.getMemoryMB() != availableMem.getMemoryMB()) {
            settings.setMemory(availableMem.getSettingsId());
            settings.save();
        }
        memSelect.setSelectedItem(availableMem);
        memSelect.addActionListener(e -> changeMemory());

        if (parent != null) {
            boolean is64Bit = true;
            boolean has64Bit = javaVersions.getBest64BitVersion() != null;

            if (!javaVersions.getSelectedVersion().is64Bit()) {
                is64Bit = false;
            }

            if (is64Bit) {
                parent.add(memSelect, new GridBagConstraints(1, 1, 6, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(8, 16, 8, 80), 0, 16));
            } else {
                parent.add(memSelect, new GridBagConstraints(1, 1, 5, 1, 5, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(8, 16, 8, 0), 0, 16));

                JToolTip toolTip = new JToolTip();
                toolTip.setBackground(LauncherFrame.COLOR_FOOTER);
                toolTip.setForeground(LauncherFrame.COLOR_GREY_TEXT);
                toolTip.setBorder(BorderFactory.createCompoundBorder(new LineBorder(LauncherFrame.COLOR_GREY_TEXT), BorderFactory.createEmptyBorder(5,5,5,5)));
                toolTip.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 14));


                String text;
                Icon icon;

                if (has64Bit) {
                    text = resources.getString("launcheroptions.java.use64bit");
                    icon = resources.getIcon("danger_icon.png");
                } else {
                    text = resources.getString("launcheroptions.java.get64bit");
                    icon = resources.getIcon("warning_icon.png");
                }

                ramWarning = new TooltipWarning(icon, toolTip);
                ((TooltipWarning)ramWarning).setToolTipText(text);
                parent.add(ramWarning, new GridBagConstraints(6, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(8,8,8,80),0,0));
            }
            repaint();
        }
    }

    private void initComponents() {
        setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        setLayout(new BorderLayout());

        JPanel header = new JPanel();
        header.setBackground(Color.black);
        header.setLayout(new BoxLayout(header, BoxLayout.LINE_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        add(header, BorderLayout.PAGE_START);

        JLabel title = new JLabel(resources.getString("launcher.title.options"));
        title.setFont(resources.getFont(ResourceLoader.FONT_RALEWAY, 26));
        title.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        title.setOpaque(false);
        title.setIcon(resources.getIcon("options_cog.png"));
        title.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        header.add(title);

        header.add(Box.createHorizontalGlue());

        JButton closeButton = new JButton();
        closeButton.setIcon(resources.getIcon("close.png"));
        closeButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        closeButton.setContentAreaFilled(false);
        closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeButton.setFocusPainted(false);
        closeButton.addActionListener(e -> closeDialog());
        header.add(closeButton);

        SimpleTabPane centerPanel = new SimpleTabPane();
        centerPanel.setBackground(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);
        centerPanel.setForeground(LauncherFrame.COLOR_GREY_TEXT);
        centerPanel.setSelectedBackground(LauncherFrame.COLOR_BLUE);
        centerPanel.setSelectedForeground(LauncherFrame.COLOR_WHITE_TEXT);
        centerPanel.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 14));
        centerPanel.setOpaque(true);
        add(centerPanel, BorderLayout.CENTER);

        JPanel general = new JPanel();
        general.setBackground(LauncherFrame.COLOR_CENTRAL_BACK_OPAQUE);
        setupGeneralPanel(general);


        JPanel proxy = new JPanel();
        proxy.setBackground(LauncherFrame.COLOR_CENTRAL_BACK_OPAQUE);
        setupProxyPanel(proxy);


        JPanel javaOptions = new JPanel();
        javaOptions.setBackground(LauncherFrame.COLOR_CENTRAL_BACK_OPAQUE);

        setupJavaOptionsPanel(javaOptions);

        JPanel videoOptions = new JPanel();
        videoOptions.setBackground(LauncherFrame.COLOR_CENTRAL_BACK_OPAQUE);

        setupVideoOptionsPanel(videoOptions);

        JPanel about = new JPanel();
        about.setBackground(LauncherFrame.COLOR_CENTRAL_BACK_OPAQUE);

        String linkText = "<a href=\"https://github.com/TechnicPack/\">"+resources.getString("launcheroptions.about.linktext")+"</a>";
        String aboutText = "<html><head><style type=\"text/css\">a{color:#309aeb}body{font-family: "+resources.getFont(ResourceLoader.FONT_OPENSANS, 12).getFamily()+";color:#D0D0D0}</style></head><body>";
        aboutText += "<p>" + resources.getString("launcheroptions.about.copyright", buildNumber.getBuildNumber(), linkText) + "</p>";
        aboutText += "<p>" + resources.getString("launcheroptions.about.romainguy") + "</p>";
        aboutText += "<p>" + resources.getString("launcheroptions.about.summary") + "</p>";

        about.setLayout(new BorderLayout());

        JLabel buildCtrl = new JLabel(resources.getString("launcher.build.text", buildNumber.getBuildNumber(), resources.getString("launcher.build." + settings.getBuildStream())));
        buildCtrl.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        buildCtrl.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 14));
        buildCtrl.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 0));
        about.add(buildCtrl, BorderLayout.SOUTH);

        JTextPane textPane = new JTextPane();
        textPane.setBorder(BorderFactory.createEmptyBorder(0, 24, 9, 24));
        textPane.setOpaque(false);
        textPane.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        textPane.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        textPane.setEditable(false);
        textPane.setHighlighter(null);
        textPane.setAlignmentX(LEFT_ALIGNMENT);
        textPane.setContentType("text/html");
        textPane.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED)
                DesktopUtils.browseUrl(e.getURL().toString());
        });
        MutableAttributeSet attributes = new SimpleAttributeSet(textPane.getParagraphAttributes());
        StyleConstants.setLineSpacing(attributes, StyleConstants.getLineSpacing(attributes) * 1.3f);
        textPane.setParagraphAttributes(attributes, true);

        textPane.setText(aboutText);
        about.add(textPane, BorderLayout.CENTER);

        centerPanel.addTab(resources.getString("launcheroptions.tab.general").toUpperCase(), general);
        centerPanel.addTab(resources.getString("launcheroptions.tab.about").toUpperCase(), about);
        if(showAdvancedOptions) {
            centerPanel.addTab(resources.getString("launcheroptions.tab.java").toUpperCase(), javaOptions);
            centerPanel.addTab(resources.getString("launcheroptions.tab.video").toUpperCase(), videoOptions);
            centerPanel.addTab(resources.getString("launcheroptions.tab.proxy").toUpperCase(), proxy);
        }
        centerPanel.setFocusable(false);
    }

    private void setupProxyPanel(JPanel panel) {

        panel.setLayout(new GridBagLayout());
        Insets blin = new Insets(0, 0, 0, 0);

        JLabel useSocksProxyField = new JLabel(resources.getString("launcheroptions.proxy.useSocks"));
        useSocksProxyField.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        useSocksProxyField.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        panel.add(useSocksProxyField, new GridBagConstraints(0, 3, 1, 1, 0, 0, 17, 0, new Insets(0, 20, 0, 10), 0, 0));

        useSocksProxy = new JCheckBox("", false);
        useSocksProxy.setOpaque(false);
        useSocksProxy.setBorder(BorderFactory.createEmptyBorder());
        useSocksProxy.setIconTextGap(0);
        useSocksProxy.setSelectedIcon(resources.getIcon("checkbox_closed.png"));
        useSocksProxy.setIcon(resources.getIcon("checkbox_open.png"));
        useSocksProxy.setFocusPainted(false);
        panel.add(useSocksProxy, new GridBagConstraints(1, 3, 1, 1, 0, 0, 17, 0, blin, 0, 0));

        JLabel proxyHostLabel = new JLabel(resources.getString("launcheroptions.proxy.host"));
        proxyHostLabel.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        proxyHostLabel.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        panel.add(proxyHostLabel, new GridBagConstraints(2, 3, 1, 1, 0, 0, 17, 0, new Insets(0, 10, 0, 0), 0, 0));

        socksProxyHost = new JTextField("");
        socksProxyHost.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        socksProxyHost.setForeground(LauncherFrame.COLOR_BLUE);
        socksProxyHost.setBackground(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);
        socksProxyHost.setHighlighter(null);
        socksProxyHost.setEditable(true);
        socksProxyHost.setCursor(null);
        socksProxyHost.setBorder(new RoundBorder(LauncherFrame.COLOR_BUTTON_BLUE, 1, 8));
        panel.add(socksProxyHost, new GridBagConstraints(3, 3, 2, 1, 2, 0, 17, 1, new Insets(8, 16, 8, 16), 0, 16));


        JLabel proxyPortLabel = new JLabel(resources.getString("launcheroptions.proxy.port"));
        proxyPortLabel.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        proxyPortLabel.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        panel.add(proxyPortLabel, new GridBagConstraints(5, 3, 1, 1, 0, 0, 17, 0, new Insets(0, 0, 0, 0), 0, 0));

        socksProxyPort = new JTextField("");
        socksProxyPort.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        socksProxyPort.setForeground(LauncherFrame.COLOR_BLUE);
        socksProxyPort.setBackground(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);
        socksProxyPort.setHighlighter(null);
        socksProxyPort.setEditable(true);
        socksProxyPort.setCursor(null);
        socksProxyPort.setBorder(new RoundBorder(LauncherFrame.COLOR_BUTTON_BLUE, 1, 8));
        panel.add(socksProxyPort, new GridBagConstraints(6, 3, 2, 1, 1, 0, 17, 1, new Insets(8, 16, 8, 16), 0, 16));
        // Start Of HTTP Proxy
        JLabel useHTTPProxyField = new JLabel(resources.getString("launcheroptions.proxy.useHTTP"));
        useHTTPProxyField.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        useHTTPProxyField.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        panel.add(useHTTPProxyField, new GridBagConstraints(0, 4, 1, 1, 0, 0, 17, 0, new Insets(0, 20, 0, 10), 0, 0));

        useHTTPProxy = new JCheckBox("", false);
        useHTTPProxy.setOpaque(false);
        useHTTPProxy.setBorder(BorderFactory.createEmptyBorder());
        useHTTPProxy.setIconTextGap(0);
        useHTTPProxy.setSelectedIcon(resources.getIcon("checkbox_closed.png"));
        useHTTPProxy.setIcon(resources.getIcon("checkbox_open.png"));
        useHTTPProxy.setFocusPainted(false);
        panel.add(useHTTPProxy, new GridBagConstraints(1, 4, 1, 1, 0, 0, 17, 0, blin, 0, 0));

        JLabel HTTPHostLabel = new JLabel(resources.getString("launcheroptions.proxy.host"));
        HTTPHostLabel.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        HTTPHostLabel.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        panel.add(HTTPHostLabel, new GridBagConstraints(2, 4, 1, 1, 0, 0, 17, 0, new Insets(0, 10, 0, 0), 0, 0));

        HTTPProxyHost = new JTextField("");
        HTTPProxyHost.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        HTTPProxyHost.setForeground(LauncherFrame.COLOR_BLUE);
        HTTPProxyHost.setBackground(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);
        HTTPProxyHost.setHighlighter(null);
        HTTPProxyHost.setEditable(true);
        HTTPProxyHost.setCursor(null);
        HTTPProxyHost.setBorder(new RoundBorder(LauncherFrame.COLOR_BUTTON_BLUE, 1, 8));
        panel.add(HTTPProxyHost, new GridBagConstraints(3, 4, 2, 1, 2, 0, 17, 1, new Insets(8, 16, 8, 16), 0, 16));

        JLabel HTTPPortLabel = new JLabel(resources.getString("launcheroptions.proxy.port"));
        HTTPPortLabel.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        HTTPPortLabel.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        panel.add(HTTPPortLabel, new GridBagConstraints(5, 4, 1, 1, 0, 0, 17, 0, new Insets(0, 0, 0, 0), 0, 0));

        HTTPProxyPort = new JTextField("");
        HTTPProxyPort.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        HTTPProxyPort.setForeground(LauncherFrame.COLOR_BLUE);
        HTTPProxyPort.setBackground(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);
        HTTPProxyPort.setHighlighter(null);
        HTTPProxyPort.setEditable(true);
        HTTPProxyPort.setCursor(null);
        HTTPProxyPort.setBorder(new RoundBorder(LauncherFrame.COLOR_BUTTON_BLUE, 1, 8));
        panel.add(HTTPProxyPort, new GridBagConstraints(6, 4, 2, 1, 1, 0, 17, 1, new Insets(8, 16, 8, 16), 0, 16));
        //Custom DNS Settings

        JLabel launchToModpacksField = new JLabel(resources.getString("launcheroptions.general.modpacktab"));
        launchToModpacksField.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        launchToModpacksField.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        panel.add(launchToModpacksField, new GridBagConstraints(0, 5, 1, 1, 0, 0, 17, 0, new Insets(0, 20, 0, 10), 0, 0));

        launchToModpacks = new JCheckBox("", false);
        launchToModpacks.setOpaque(false);
        launchToModpacks.setHorizontalAlignment(SwingConstants.RIGHT);
        launchToModpacks.setBorder(BorderFactory.createEmptyBorder());
        launchToModpacks.setIconTextGap(0);
        launchToModpacks.setSelectedIcon(resources.getIcon("checkbox_closed.png"));
        launchToModpacks.setIcon(resources.getIcon("checkbox_open.png"));
        launchToModpacks.setFocusPainted(false);
        panel.add(launchToModpacks, new GridBagConstraints(1, 5, 1, 1, 0, 0, 17, 0, blin, 0, 0));

        JLabel discoverURLField = new JLabel(resources.getString("launcheroptions.proxy.discoverUrl"));
        discoverURLField.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        discoverURLField.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        panel.add(discoverURLField, new GridBagConstraints(2, 5, 1, 1, 0, 0, 17, 0, new Insets(0, 10, 0, 0), 0, 0));

        discoverURL = new JTextField("Set By Startup Prams");
        discoverURL.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        discoverURL.setForeground(LauncherFrame.COLOR_BLUE);
        discoverURL.setBackground(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);
        discoverURL.setHighlighter(null);
        discoverURL.setEditable(false);
        discoverURL.setCursor(null);
        discoverURL.setBorder(new RoundBorder(LauncherFrame.COLOR_BUTTON_BLUE, 1, 8));
        panel.add(discoverURL, new GridBagConstraints(3, 5, 2, 1, 2, 0, 17, 1, new Insets(8, 16, 8, 16), 0, 16));

        JLabel solderURLField = new JLabel(resources.getString("launcheroptions.proxy.solderUrl"));
        solderURLField.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        solderURLField.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        panel.add(solderURLField, new GridBagConstraints(5, 5, 1, 1, 0, 0, 17, 0, new Insets(0, 0, 0, 0), 0, 0));

        solderURL = new JTextField("");
        solderURL.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        solderURL.setForeground(LauncherFrame.COLOR_BLUE);
        solderURL.setBackground(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);
        solderURL.setHighlighter(null);
        solderURL.setEditable(true);
        solderURL.setCursor(null);
        solderURL.setBorder(new RoundBorder(LauncherFrame.COLOR_BUTTON_BLUE, 1, 8));
        panel.add(solderURL, new GridBagConstraints(6, 5, 2, 1, 2, 0, 17, 1, new Insets(8, 16, 8, 16), 0, 16));


        //Other
        JLabel showConsoleField = new JLabel(resources.getString("launcheroptions.general.console"));
        showConsoleField.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        showConsoleField.setForeground(LauncherFrame.COLOR_WHITE_TEXT);

        showConsole = new JCheckBox("", false);
        showConsole.setOpaque(false);
        showConsole.setHorizontalAlignment(SwingConstants.RIGHT);
        showConsole.setBorder(BorderFactory.createEmptyBorder());
        showConsole.setIconTextGap(0);
        showConsole.setSelectedIcon(resources.getIcon("checkbox_closed.png"));
        showConsole.setIcon(resources.getIcon("checkbox_open.png"));
        showConsole.setFocusPainted(false);
        panel.add(showConsoleField, new GridBagConstraints(0, 6, 1, 1, 0, 0, 17, 0, new Insets(0, 20, 0, 10), 0, 0));
        panel.add(showConsole, new GridBagConstraints(1, 6, 1, 1, 0, 0, 17, 0, blin, 0, 0));

        // Other

        JLabel authServerField = new JLabel(resources.getString("launcheroptions.proxy.authServerUrl"));
        authServerField.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        authServerField.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        panel.add(authServerField, new GridBagConstraints(2, 6, 1, 1, 0, 0, 17, 0, new Insets(0, 10, 0, 0), 0, 0));


        JTextField authServerURL = new JTextField("Set by User Profile");
        authServerURL.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        authServerURL.setForeground(LauncherFrame.COLOR_BLUE);
        authServerURL.setBackground(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);
        authServerURL.setHighlighter(null);
        authServerURL.setEditable(false);
        authServerURL.setEnabled(false);
        authServerURL.setCursor(null);
        authServerURL.setBorder(new RoundBorder(LauncherFrame.COLOR_BUTTON_BLUE, 1, 8));
        panel.add(authServerURL, new GridBagConstraints(3, 6, 2, 1, 2, 0, 17, 1, new Insets(8, 16, 8, 16), 0, 16));

        JLabel platformServerField = new JLabel(resources.getString("launcheroptions.proxy.platformUrl"));
        platformServerField.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        platformServerField.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        panel.add(platformServerField, new GridBagConstraints(6, 6, 1, 1, 0, 0, 17, 0, new Insets(0, 0, 0, 0), 0, 0));

        platformURL = new JTextField("");
        platformURL.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        platformURL.setForeground(LauncherFrame.COLOR_BLUE);
        platformURL.setBackground(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);
        platformURL.setHighlighter(null);
        platformURL.setEditable(true);
        platformURL.setCursor(null);
        platformURL.setBorder(new RoundBorder(LauncherFrame.COLOR_BUTTON_BLUE, 1, 8));
        panel.add(platformURL, new GridBagConstraints(7, 6, 2, 1, 2, 0, 17, 1, new Insets(8, 16, 8, 16), 0, 16));



        panel.add(Box.createGlue(), new GridBagConstraints(0, 8, 5, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

    }

    private void setupGeneralPanel(JPanel panel) {

        panel.setLayout(new GridBagLayout());

        JLabel streamLabel = new JLabel(resources.getString("launcheroptions.general.build"));
        streamLabel.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        streamLabel.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        panel.add(streamLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 40, 0, 0), 0, 0));

        // Setup stream select box
        streamSelect = new JComboBox<>();

        if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("mac")) {
            streamSelect.setUI(new MetalComboBoxUI());
        }

        streamSelect.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        streamSelect.setEditable(false);
        streamSelect.setBorder(new RoundBorder(LauncherFrame.COLOR_BUTTON_BLUE, 1, 10));
        streamSelect.setForeground(LauncherFrame.COLOR_BUTTON_BLUE);
        streamSelect.setBackground(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);
        streamSelect.setUI(new SimpleButtonComboUI(new RoundedBorderFormatter(new RoundBorder(LauncherFrame.COLOR_BUTTON_BLUE, 1, 0)), resources, LauncherFrame.COLOR_SCROLL_TRACK, LauncherFrame.COLOR_SCROLL_THUMB));
        streamSelect.setFocusable(false);

        Object child = streamSelect.getAccessibleContext().getAccessibleChild(0);
        BasicComboPopup popup = (BasicComboPopup)child;
        JList<?> list = popup.getList();
        list.setSelectionForeground(LauncherFrame.COLOR_BUTTON_BLUE);
        list.setSelectionBackground(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);
        list.setBackground(LauncherFrame.COLOR_CENTRAL_BACK_OPAQUE);

        panel.add(streamSelect, new GridBagConstraints(1, 0, 2, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(8, 16, 8, 16), 0, 16));

        //Setup language box
        JLabel langLabel = new JLabel(resources.getString("launcheroptions.general.lang"));
        langLabel.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        langLabel.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        panel.add(langLabel, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 40, 0, 0), 0, 0));

        langSelect = new JComboBox<>();

        if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("mac")) {
            langSelect.setUI(new MetalComboBoxUI());
        }

        langSelect.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        langSelect.setEditable(false);
        langSelect.setBorder(new RoundBorder(LauncherFrame.COLOR_BUTTON_BLUE, 1, 10));
        langSelect.setForeground(LauncherFrame.COLOR_BUTTON_BLUE);
        langSelect.setBackground(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);
        langSelect.setUI(new SimpleButtonComboUI(new RoundedBorderFormatter(new RoundBorder(LauncherFrame.COLOR_BUTTON_BLUE, 1, 0)), resources, LauncherFrame.COLOR_SCROLL_TRACK, LauncherFrame.COLOR_SCROLL_THUMB));
        langSelect.setFocusable(false);

        child = langSelect.getAccessibleContext().getAccessibleChild(0);
        popup = (BasicComboPopup)child;
        list = popup.getList();
        list.setSelectionForeground(LauncherFrame.COLOR_BUTTON_BLUE);
        list.setSelectionBackground(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);
        list.setBackground(LauncherFrame.COLOR_CENTRAL_BACK_OPAQUE);

        panel.add(langSelect, new GridBagConstraints(1, 1, 2, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(8, 16, 8, 16), 0, 16));

        //Setup on pack launch box
        JLabel launchLabel = new JLabel(resources.getString("launcheroptions.general.onlaunch"));
        launchLabel.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        launchLabel.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        panel.add(launchLabel, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 40, 0, 0), 0, 0));

        launchSelect = new JComboBox<>();

        if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("mac")) {
            launchSelect.setUI(new MetalComboBoxUI());
        }

        launchSelect.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        launchSelect.setEditable(false);
        launchSelect.setBorder(new RoundBorder(LauncherFrame.COLOR_BUTTON_BLUE, 1, 10));
        launchSelect.setForeground(LauncherFrame.COLOR_BUTTON_BLUE);
        launchSelect.setBackground(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);
        launchSelect.setUI(new SimpleButtonComboUI(new RoundedBorderFormatter(new RoundBorder(LauncherFrame.COLOR_BUTTON_BLUE, 1, 0)), resources, LauncherFrame.COLOR_SCROLL_TRACK, LauncherFrame.COLOR_SCROLL_THUMB));
        launchSelect.setFocusable(false);

        child = launchSelect.getAccessibleContext().getAccessibleChild(0);
        popup = (BasicComboPopup)child;
        list = popup.getList();
        list.setSelectionForeground(LauncherFrame.COLOR_BUTTON_BLUE);
        list.setSelectionBackground(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);
        list.setBackground(LauncherFrame.COLOR_CENTRAL_BACK_OPAQUE);

        panel.add(launchSelect, new GridBagConstraints(1, 2, 2, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(8, 16, 8, 16), 0, 16));

        //Install folder field
        JLabel installLabel = new JLabel(resources.getString("launcheroptions.general.install"));
        installLabel.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        installLabel.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        panel.add(installLabel, new GridBagConstraints(0, 3, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 40, 0, 0), 0, 0));

        installField = new JTextField("");
        installField.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        installField.setForeground(LauncherFrame.COLOR_BLUE);
        installField.setBackground(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);
        installField.setHighlighter(null);
        installField.setEditable(false);
        installField.setCursor(null);
        installField.setBorder(new RoundBorder(LauncherFrame.COLOR_BUTTON_BLUE, 1, 8));
        panel.add(installField, new GridBagConstraints(1, 3, 2, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(8, 16, 8, 16), 0, 16));

        RoundedButton reinstallButton = new RoundedButton(resources.getString("launcheroptions.install.change"));
        reinstallButton.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        reinstallButton.setContentAreaFilled(false);
        reinstallButton.setForeground(LauncherFrame.COLOR_BUTTON_BLUE);
        reinstallButton.setHoverForeground(LauncherFrame.COLOR_BLUE);
        reinstallButton.addActionListener(e -> reinstall());
        panel.add(reinstallButton, new GridBagConstraints(3, 3, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(8, 0, 8, 0), 0, 0));

        //Client ID field
        JLabel clientIdField = new JLabel(resources.getString("launcheroptions.general.id"));
        clientIdField.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        clientIdField.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        panel.add(clientIdField, new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 40, 0, 0), 0, 0));

        clientId = new JTextField("abc123");
        clientId.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        clientId.setForeground(LauncherFrame.COLOR_BLUE);
        clientId.setBackground(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);
        clientId.setHighlighter(null);
        clientId.setEditable(false);
        clientId.setCursor(null);
        clientId.setBorder(new RoundBorder(LauncherFrame.COLOR_BUTTON_BLUE, 1, 8));
        panel.add(clientId, new GridBagConstraints(1, 4, 2, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(8, 16, 8, 16), 0, 16));

        RoundedButton copyButton = new RoundedButton(resources.getString("launcheroptions.id.copy"));
        copyButton.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        copyButton.setContentAreaFilled(false);
        copyButton.setForeground(LauncherFrame.COLOR_BUTTON_BLUE);
        copyButton.setHoverForeground(LauncherFrame.COLOR_BLUE);
        copyButton.addActionListener(e -> copyCid());
        panel.add(copyButton, new GridBagConstraints(3, 4, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(8, 0, 8, 0), 0, 0));

        panel.add(Box.createRigidArea(new Dimension(60, 0)), new GridBagConstraints(4, 3, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0,0,0,0), 0,0));

        //Add show console field
        JLabel useCustomDNSField = new JLabel(resources.getString("launcheroptions.proxy.useDNSOnly"));
        useCustomDNSField.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        useCustomDNSField.setForeground(LauncherFrame.COLOR_WHITE_TEXT);

        useDNSOnly = new JCheckBox("", false);
        useDNSOnly.setOpaque(false);
        useDNSOnly.setBorder(BorderFactory.createEmptyBorder());
        useDNSOnly.setIconTextGap(0);
        useDNSOnly.setSelectedIcon(resources.getIcon("checkbox_closed.png"));
        useDNSOnly.setIcon(resources.getIcon("checkbox_open.png"));
        useDNSOnly.setFocusPainted(false);

        panel.add(useCustomDNSField, new GridBagConstraints(0, 5, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 40, 0, 0), 0, 0));
        panel.add(useDNSOnly, new GridBagConstraints(1, 5, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(16, 16, 0, 0), 0, 0));

        //Add launch to modpacks
        JLabel useTorRelayField = new JLabel(resources.getString("launcheroptions.proxy.useTorRelay"));
        useTorRelayField.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        useTorRelayField.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        panel.add(useTorRelayField, new GridBagConstraints(0,6,1,1,0,0,GridBagConstraints.EAST,GridBagConstraints.NONE,new Insets(10,40,0,0),0,0));

        useTorRelay = new JCheckBox("", false);
        useTorRelay.setOpaque(false);
        useTorRelay.setEnabled(settings.getAdvOptions());
        useTorRelay.setBorder(BorderFactory.createEmptyBorder());
        useTorRelay.setIconTextGap(0);
        useTorRelay.setSelectedIcon(resources.getIcon("checkbox_closed.png"));
        useTorRelay.setIcon(resources.getIcon("checkbox_open.png"));
        useTorRelay.setFocusPainted(false);
        panel.add(useTorRelay, new GridBagConstraints(1, 6, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(16, 16 ,0, 0), 0,0));

        panel.add(Box.createGlue(), new GridBagConstraints(0, 7, 5, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0, 0));

        //Open logs button
        RoundedButton openLogs = new RoundedButton(resources.getString("launcheroptions.general.logs"));
        openLogs.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        openLogs.setContentAreaFilled(false);
        openLogs.setForeground(LauncherFrame.COLOR_BUTTON_BLUE);
        openLogs.setHoverForeground(LauncherFrame.COLOR_BLUE);
        openLogs.setBorder(BorderFactory.createEmptyBorder(5, 17, 10, 17));
        openLogs.addActionListener(e -> openLogs());
        panel.add(openLogs, new GridBagConstraints(0, 8, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 10, 10, 0), 0, 0));
    }

    private void updateDimensionsEnabled() {
        if (windowSelect.getSelectedIndex() == 2) {
            widthInput.setEnabled(true);
            heightInput.setEnabled(true);
            widthInput.setForeground(LauncherFrame.COLOR_BUTTON_BLUE);
            heightInput.setForeground(LauncherFrame.COLOR_BUTTON_BLUE);
            widthInput.setBorder(new RoundBorder(LauncherFrame.COLOR_BUTTON_BLUE, 1, 8));
            heightInput.setBorder(new RoundBorder(LauncherFrame.COLOR_BUTTON_BLUE, 1, 8));
        } else {
            widthInput.setEnabled(false);
            heightInput.setEnabled(false);
            widthInput.setForeground(LauncherFrame.COLOR_GREY_TEXT);
            heightInput.setForeground(LauncherFrame.COLOR_GREY_TEXT);
            widthInput.setBorder(new RoundBorder(LauncherFrame.COLOR_GREY_TEXT, 1, 8));
            heightInput.setBorder(new RoundBorder(LauncherFrame.COLOR_GREY_TEXT, 1, 8));
        }
    }

    private void setupVideoOptionsPanel(JPanel panel) {
        panel.setLayout(new GridBagLayout());

        JLabel streamLabel = new JLabel(resources.getString("launcheroptions.video.windowSize"));
        streamLabel.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        streamLabel.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        panel.add(streamLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 40, 0, 0), 0, 0));

        windowSelect = new JComboBox<>();

        if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("mac")) {
            windowSelect.setUI(new MetalComboBoxUI());
        }

        windowSelect.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        windowSelect.setEditable(false);
        windowSelect.setBorder(new RoundBorder(LauncherFrame.COLOR_BUTTON_BLUE, 1, 10));
        windowSelect.setForeground(LauncherFrame.COLOR_BUTTON_BLUE);
        windowSelect.setBackground(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);
        windowSelect.setUI(new SimpleButtonComboUI(new RoundedBorderFormatter(new RoundBorder(LauncherFrame.COLOR_BUTTON_BLUE, 1, 0)), resources, LauncherFrame.COLOR_SCROLL_TRACK, LauncherFrame.COLOR_SCROLL_THUMB));
        windowSelect.setFocusable(false);

        Object child = windowSelect.getAccessibleContext().getAccessibleChild(0);
        BasicComboPopup popup = (BasicComboPopup)child;
        JList<?> list = popup.getList();
        list.setSelectionForeground(LauncherFrame.COLOR_BUTTON_BLUE);
        list.setSelectionBackground(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);
        list.setBackground(LauncherFrame.COLOR_CENTRAL_BACK_OPAQUE);

        panel.add(windowSelect, new GridBagConstraints(1, 0, 1, 1, 0.5f, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(8, 16, 8, 16), 0, 16));

        JLabel widthLabel = new JLabel(resources.getString("launcheroptions.video.windowSize.width"));
        widthLabel.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        widthLabel.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        panel.add(widthLabel, new GridBagConstraints(2, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        widthInput = new JTextField(3);
        widthInput.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        widthInput.setForeground(LauncherFrame.COLOR_BLUE);
        widthInput.setBackground(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);
        widthInput.setBorder(new RoundBorder(LauncherFrame.COLOR_BUTTON_BLUE, 1, 8));
        widthInput.setCaretColor(LauncherFrame.COLOR_BLUE);
        widthInput.setText("800");
        panel.add(widthInput, new GridBagConstraints(3, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(8, 6, 8, 16), 0, 0));

        JLabel heightLabel = new JLabel(resources.getString("launcheroptions.video.windowSize.height"));
        heightLabel.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        heightLabel.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        panel.add(heightLabel, new GridBagConstraints(4, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 0, 0, 0), 0, 0));

        heightInput = new JTextField(3);
        heightInput.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        heightInput.setForeground(LauncherFrame.COLOR_BLUE);
        heightInput.setBackground(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);
        heightInput.setBorder(new RoundBorder(LauncherFrame.COLOR_BUTTON_BLUE, 1, 8));
        heightInput.setCaretColor(LauncherFrame.COLOR_BLUE);
        heightInput.setText("600");
        panel.add(heightInput, new GridBagConstraints(5, 0, 1, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(8, 6, 8, 16), 0,0));

        //Add show console field
        JLabel useStencilField = new JLabel(resources.getString("launcheroptions.video.stencil"));
        useStencilField.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        useStencilField.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        panel.add(useStencilField, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(10, 40, 0, 0), 0, 0));

        useStencil = new JComboBox<>();

        if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("mac")) {
            useStencil.setUI(new MetalComboBoxUI());
        }

        useStencil.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        useStencil.setEditable(false);
        useStencil.setBorder(new RoundBorder(LauncherFrame.COLOR_BUTTON_BLUE, 1, 10));
        useStencil.setForeground(LauncherFrame.COLOR_BUTTON_BLUE);
        useStencil.setBackground(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);
        useStencil.setUI(new SimpleButtonComboUI(new RoundedBorderFormatter(new RoundBorder(LauncherFrame.COLOR_BUTTON_BLUE, 1, 0)), resources, LauncherFrame.COLOR_SCROLL_TRACK, LauncherFrame.COLOR_SCROLL_THUMB));
        useStencil.setFocusable(false);

        child = useStencil.getAccessibleContext().getAccessibleChild(0);
        popup = (BasicComboPopup)child;
        list = popup.getList();
        list.setSelectionForeground(LauncherFrame.COLOR_BUTTON_BLUE);
        list.setSelectionBackground(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);
        list.setBackground(LauncherFrame.COLOR_CENTRAL_BACK_OPAQUE);
        panel.add(useStencil, new GridBagConstraints(1, 1, 1, 1, 0.5f, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(8, 16, 8, 16), 0, 16));

        JLabel stencilInfo = new JLabel("") {
            @Override
            public Dimension getMaximumSize() {
                return getMinimumSize();
            }

            @Override
            public Dimension getPreferredSize() {
                return getMinimumSize();
            }
        };
        stencilInfo.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 12));
        stencilInfo.setForeground(LauncherFrame.COLOR_WHITE_TEXT);

        stencilInfo.setText("<html><body style=\"font-family:" + stencilInfo.getFont().getFamily() + ";color:#D0D0D0\">" + resources.getString("launcheroptions.video.stencil.info") + "</body></html>");

        panel.add(stencilInfo, new GridBagConstraints(2, 1, 4, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

        panel.add(Box.createHorizontalStrut(60), new GridBagConstraints(7, 1, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(0,0,0,0), 30,0));

        panel.add(Box.createGlue(), new GridBagConstraints(0, 2, 8, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0));
    }

    private void setupJavaOptionsPanel(JPanel panel) {
        panel.setLayout(new GridBagLayout());

        JLabel versionLabel = new JLabel(resources.getString("launcheroptions.java.version"));
        versionLabel.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        versionLabel.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        panel.add(versionLabel, new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 60, 0, 0), 0, 0));

        versionSelect = new JComboBox<>();

        if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("mac")) {
            versionSelect.setUI(new MetalComboBoxUI());
        }

        versionSelect.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        versionSelect.setEditable(false);
        versionSelect.setBorder(new RoundBorder(LauncherFrame.COLOR_BUTTON_BLUE, 1, 10));
        versionSelect.setForeground(LauncherFrame.COLOR_BUTTON_BLUE);
        versionSelect.setBackground(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);
        SimpleButtonComboUI ui = new SimpleButtonComboUI(new RoundedBorderFormatter(new RoundBorder(LauncherFrame.COLOR_BUTTON_BLUE, 1, 0)), resources, LauncherFrame.COLOR_SCROLL_TRACK, LauncherFrame.COLOR_SCROLL_THUMB);
        versionSelect.setUI(ui);
        versionSelect.setFocusable(false);

        Object child = versionSelect.getAccessibleContext().getAccessibleChild(0);
        BasicComboPopup popup = (BasicComboPopup)child;
        JList<?> list = popup.getList();
        list.setSelectionForeground(LauncherFrame.COLOR_BUTTON_BLUE);
        list.setSelectionBackground(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);
        list.setBackground(LauncherFrame.COLOR_CENTRAL_BACK_OPAQUE);

        panel.add(versionSelect, new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(8, 16, 8, 8), 0, 16));

        RoundedButton otherVersionButton = new RoundedButton(resources.getString("launcheroptions.java.otherversion"));
        otherVersionButton.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        otherVersionButton.setContentAreaFilled(false);
        otherVersionButton.setForeground(LauncherFrame.COLOR_BUTTON_BLUE);
        otherVersionButton.setHoverForeground(LauncherFrame.COLOR_BLUE);
        otherVersionButton.addActionListener(e -> selectOtherVersion());
        panel.add(otherVersionButton, new GridBagConstraints(2, 0, 5, 1, 2, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(8, 8, 8, 80), 0, 0));

        JLabel memLabel = new JLabel(resources.getString("launcheroptions.java.memory"));
        memLabel.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        memLabel.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        panel.add(memLabel, new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 60, 0, 0), 0, 0));

        memSelect = new JComboBox<>();

        if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("mac")) {
            memSelect.setUI(new MetalComboBoxUI());
        }

        memSelect.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        memSelect.setEditable(false);
        memSelect.setBorder(new RoundBorder(LauncherFrame.COLOR_BUTTON_BLUE, 1, 10));
        memSelect.setForeground(LauncherFrame.COLOR_BUTTON_BLUE);
        memSelect.setBackground(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);
         ui = new SimpleButtonComboUI(new RoundedBorderFormatter(new RoundBorder(LauncherFrame.COLOR_BUTTON_BLUE, 1, 0)), resources, LauncherFrame.COLOR_SCROLL_TRACK, LauncherFrame.COLOR_SCROLL_THUMB);
        memSelect.setUI(ui);
        memSelect.setFocusable(false);

        child = memSelect.getAccessibleContext().getAccessibleChild(0);
        popup = (BasicComboPopup)child;
        list = popup.getList();
        list.setSelectionForeground(LauncherFrame.COLOR_BUTTON_BLUE);
        list.setSelectionBackground(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);
        list.setBackground(LauncherFrame.COLOR_CENTRAL_BACK_OPAQUE);
        panel.add(memSelect, new GridBagConstraints(1, 1, 6, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(8, 16, 8, 80), 0, 16));

        JLabel argsLabel = new JLabel(resources.getString("launcheroptions.java.arguments"));
        argsLabel.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        argsLabel.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        panel.add(argsLabel, new GridBagConstraints(0, 2, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 60, 0, 0), 0, 0));

        javaArgs = new JTextArea(32, 4);
        javaArgs.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        javaArgs.setForeground(LauncherFrame.COLOR_BUTTON_BLUE);
        javaArgs.setBackground(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);
        javaArgs.setBorder(new RoundBorder(LauncherFrame.COLOR_BUTTON_BLUE, 1, 8));
        javaArgs.setCaretColor(LauncherFrame.COLOR_BUTTON_BLUE);
        javaArgs.setMargin(new Insets(16, 4, 16, 4));
        javaArgs.setLineWrap(true);
        javaArgs.setWrapStyleWord(true);
        javaArgs.setSelectionColor(LauncherFrame.COLOR_BUTTON_BLUE);
        javaArgs.setSelectedTextColor(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);

        panel.add(javaArgs, new GridBagConstraints(1, 2, 6, 2, 0, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(8, 16, 6, 80), 0, 0));

        JLabel wrapperCmdLabel = new JLabel(resources.getString("launcheroptions.java.wrapper"));
        wrapperCmdLabel.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        wrapperCmdLabel.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        panel.add(wrapperCmdLabel, new GridBagConstraints(0, 4, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 60, 0, 0), 0, 0));

        wrapperCommand = new JTextField("");
        wrapperCommand.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        wrapperCommand.setForeground(LauncherFrame.COLOR_BUTTON_BLUE);
        wrapperCommand.setBackground(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);
        wrapperCommand.setBorder(new RoundBorder(LauncherFrame.COLOR_BUTTON_BLUE, 1, 8));
        wrapperCommand.setCaretColor(LauncherFrame.COLOR_BUTTON_BLUE);
        wrapperCommand.setSelectionColor(LauncherFrame.COLOR_BUTTON_BLUE);
        wrapperCommand.setSelectedTextColor(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);
        panel.add(wrapperCommand, new GridBagConstraints(1, 4, 2, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(8, 16, 8, 16), 0, 16));

        JLabel autoApprovalLabel = new JLabel(resources.getString("launcheroptions.java.autoApprove"));
        autoApprovalLabel.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        autoApprovalLabel.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        panel.add(autoApprovalLabel, new GridBagConstraints(0, 5, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));

        askFirstBox = new JCheckBox("", false);
        askFirstBox.setOpaque(false);
        askFirstBox.setHorizontalAlignment(SwingConstants.RIGHT);
        askFirstBox.setBorder(BorderFactory.createEmptyBorder());
        askFirstBox.setIconTextGap(0);
        askFirstBox.setSelectedIcon(resources.getIcon("checkbox_closed.png"));
        askFirstBox.setIcon(resources.getIcon("checkbox_open.png"));
        askFirstBox.setFocusPainted(false);
        panel.add(askFirstBox, new GridBagConstraints(1, 5, 6, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(8, 16, 8, 8), 0, 0));

        JLabel useMojangJavaLabel = new JLabel(resources.getString("launcheroptions.java.useMojangJava"));
        useMojangJavaLabel.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        useMojangJavaLabel.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        panel.add(useMojangJavaLabel, new GridBagConstraints(0, 6, 1, 1, 0, 0, GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0, 20, 0, 0), 0, 0));

        useMojangJava = new JCheckBox("", false);
        useMojangJava.setOpaque(false);
        useMojangJava.setHorizontalAlignment(SwingConstants.RIGHT);
        useMojangJava.setBorder(BorderFactory.createEmptyBorder());
        useMojangJava.setIconTextGap(0);
        useMojangJava.setSelectedIcon(resources.getIcon("checkbox_closed.png"));
        useMojangJava.setIcon(resources.getIcon("checkbox_open.png"));
        useMojangJava.setFocusPainted(false);
        panel.add(useMojangJava, new GridBagConstraints(1, 6, 6, 1, 0, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(8, 16, 8, 8), 0, 0));

        panel.add(Box.createGlue(), new GridBagConstraints(4, 7, 1, 1, 1, 0.5, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0));
    }

    @Override
    public void relocalize(ResourceLoader loader) {
        this.resources = loader;
        this.resources.registerResource(this);

        //Wipe controls
        this.getContentPane().removeAll();
        this.setLayout(null);

        initComponents();
        initControlValues();

        EventQueue.invokeLater(() -> {
            invalidate();
            repaint();
        });
    }
}
