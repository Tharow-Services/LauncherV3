

package net.tharow.tantalum.launcher.ui.components;

import net.tharow.tantalum.launcher.ui.LauncherFrame;
import net.tharow.tantalum.launcher.ui.listitems.PackBuildItem;
import net.tharow.tantalum.launchercore.install.LauncherDirectories;
import net.tharow.tantalum.launchercore.modpacks.InstalledPack;
import net.tharow.tantalum.launchercore.modpacks.ModpackModel;
import net.tharow.tantalum.ui.controls.LauncherDialog;
import net.tharow.tantalum.ui.controls.RoundedButton;
import net.tharow.tantalum.ui.controls.borders.RoundBorder;
import net.tharow.tantalum.ui.controls.list.AdvancedCellRenderer;
import net.tharow.tantalum.ui.controls.list.SimpleButtonComboUI;
import net.tharow.tantalum.ui.controls.list.popupformatters.RoundedBorderFormatter;
import net.tharow.tantalum.ui.lang.ResourceLoader;
import net.tharow.tantalum.utilslib.DesktopUtils;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.plaf.metal.MetalComboBoxUI;
import java.awt.*;
import java.io.File;
import java.util.Locale;

public class ModpackOptionsDialog extends LauncherDialog {

    private static final int DIALOG_WIDTH = 600;
    private static final int DIALOG_HEIGHT = 380;

    private ModpackModel modpack;
    private final ResourceLoader resources;

    private JTextField installField;
    private JRadioButton recommended;
    private JRadioButton latest;
    private JRadioButton manual;
    private JComboBox manualBuildList;
    private final JFileChooser chooser;

    public ModpackOptionsDialog(Frame owner, LauncherDirectories directories, ModpackModel modpack, ResourceLoader resources) {
        super(owner);

        this.modpack = modpack;
        this.resources = resources;

        chooser = new JFileChooser(directories.getModpacksDirectory());
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        initComponents();
        initValues();
    }

    public void refresh(ModpackModel model) {
        modpack = model;

        //Wipe controls
        this.getContentPane().removeAll();
        this.setLayout(null);

        initComponents();
        initValues();
    }

    protected void selectRecommended() {
        manualBuildList.setBorder(new RoundBorder(LauncherFrame.COLOR_GREY_TEXT, 1, 10));
        manualBuildList.setEnabled(false);
        manualBuildList.setSelectedItem(new PackBuildItem(modpack.getRecommendedBuild(), resources, modpack));
        this.modpack.setBuild(InstalledPack.RECOMMENDED);
    }

    protected void selectLatest() {
        manualBuildList.setBorder(new RoundBorder(LauncherFrame.COLOR_GREY_TEXT, 1, 10));
        manualBuildList.setEnabled(false);
        manualBuildList.setSelectedItem(new PackBuildItem(modpack.getLatestBuild(), resources, modpack));
        this.modpack.setBuild(InstalledPack.LATEST);
    }

    protected void selectManual() {
        if (manualBuildList.getItemCount() == 0)
            return;

        // Nothing is selected, try to set it to the currently installed modpack build
        // (could be an actual build string (like "1.0"), "recommended" or "latest")
        if (manualBuildList.getSelectedItem() == null)
            manualBuildList.setSelectedItem(new PackBuildItem(modpack.getBuild(), resources, modpack));

        // Still nothing is selected, try to fallback to the recommended build
        if (manualBuildList.getSelectedItem() == null)
            manualBuildList.setSelectedItem(new PackBuildItem(modpack.getRecommendedBuild(), resources, modpack));

        // Still nothing is selected, and both the currently installed and recommended builds couldn't be selected,
        // so we try to select the first available one (we already know there's at least 1 build available)
        if (manualBuildList.getSelectedIndex() == -1) {
            manualBuildList.setSelectedIndex(0);
        }

        this.modpack.setBuild(((PackBuildItem) manualBuildList.getSelectedItem()).getBuildNumber());
        manualBuildList.setBorder(new RoundBorder(LauncherFrame.COLOR_BUTTON_BLUE, 1, 10));
        manualBuildList.setEnabled(true);
    }

    protected void buildUpdated() {
        this.modpack.setBuild(((PackBuildItem)manualBuildList.getSelectedItem()).getBuildNumber());
    }

    protected void deletePack() {
        int result = JOptionPane.showConfirmDialog(this, resources.getString("modpackoptions.delete.confirmtext"), resources.getString("modpackoptions.delete.confirmtitle"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            modpack.delete();
            dispose();
        }
    }

    protected void resetPack() {
        int result = JOptionPane.showConfirmDialog(this, resources.getString("modpackoptions.reinstall.confirmtext"), resources.getString("modpackoptions.reinstall.confirmtitle"), JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (result == JOptionPane.YES_OPTION) {
            modpack.resetPack();
            refresh(modpack);
        }
    }

    protected void openFolder() {
        if (modpack.getInstalledDirectory() != null && modpack.getInstalledDirectory().exists())
            DesktopUtils.open(modpack.getInstalledDirectory());
    }

    protected void moveFolder() {
        int result = chooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();

            if (file.list().length > 0) {
                JOptionPane.showMessageDialog(this, resources.getString("modpackoptions.move.errortext"), resources.getString("modpackoptions.move.errortitle"), JOptionPane.WARNING_MESSAGE);
                return;
            }

            modpack.setInstalledDirectory(file);
            refresh(modpack);
        }
    }

    private void initComponents() {
        setSize(DIALOG_WIDTH, DIALOG_HEIGHT);
        setLayout(new BorderLayout());

        JPanel header = new JPanel();
        header.setBackground(Color.black);
        header.setLayout(new BoxLayout(header, BoxLayout.LINE_AXIS));
        header.setBorder(BorderFactory.createEmptyBorder(4,8,4,8));
        add(header, BorderLayout.PAGE_START);

        JLabel title = new JLabel(resources.getString("launcher.title.modpackoptions"));
        title.setFont(resources.getFont(ResourceLoader.FONT_RALEWAY, 26));
        title.setBorder(BorderFactory.createEmptyBorder(5,0,5,0));
        title.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        title.setOpaque(false);
        title.setIcon(resources.getIcon("options_cog.png"));
        header.add(title);

        header.add(Box.createHorizontalGlue());

        JButton closeButton = new JButton();
        closeButton.setIcon(resources.getIcon("close.png"));
        closeButton.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        closeButton.setContentAreaFilled(false);
        closeButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeButton.addActionListener(e -> closeDialog());
        closeButton.setFocusPainted(false);
        header.add(closeButton);

        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(LauncherFrame.COLOR_CENTRAL_BACK_OPAQUE);
        centerPanel.setOpaque(true);
        centerPanel.setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
        add(centerPanel, BorderLayout.CENTER);

        centerPanel.setLayout(new GridBagLayout());

        JLabel installFolderLabel = new JLabel(resources.getString("modpackoptions.installfolder.text"));
        installFolderLabel.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        installFolderLabel.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        centerPanel.add(installFolderLabel, new GridBagConstraints(0, 0, 3, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0));

        installField = new JTextField("");
        installField.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        installField.setForeground(LauncherFrame.COLOR_BLUE);
        installField.setBackground(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);
        installField.setHighlighter(null);
        installField.setEditable(false);
        installField.setCursor(null);
        installField.setBorder(new RoundBorder(LauncherFrame.COLOR_BUTTON_BLUE, 1, 8));
        centerPanel.add(installField, new GridBagConstraints(3, 0, 1, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,5,0,5),0,0));

        RoundedButton openFolder = new RoundedButton(resources.getString("modpackoptions.installfolder.open"));
        openFolder.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        openFolder.setContentAreaFilled(false);
        openFolder.setForeground(LauncherFrame.COLOR_BUTTON_BLUE);
        openFolder.setHoverForeground(LauncherFrame.COLOR_BLUE);
        openFolder.setEnabled(modpack.getInstalledDirectory() != null && modpack.getInstalledDirectory().exists());
        if (modpack.getInstalledDirectory() != null && modpack.getInstalledDirectory().exists()) {
            openFolder.setForeground(LauncherFrame.COLOR_BUTTON_BLUE);
            openFolder.setHoverForeground(LauncherFrame.COLOR_BLUE);
        } else {
            openFolder.setForeground(LauncherFrame.COLOR_GREY_TEXT);
        }
        openFolder.addActionListener(e -> openFolder());
        centerPanel.add(openFolder, new GridBagConstraints(4, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,5,0,5), 0,0));

        RoundedButton moveFolder = new RoundedButton(resources.getString("modpackoptions.installfolder.move"));
        moveFolder.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        moveFolder.setContentAreaFilled(false);
        moveFolder.setForeground(LauncherFrame.COLOR_BUTTON_BLUE);
        moveFolder.setHoverForeground(LauncherFrame.COLOR_BLUE);
        moveFolder.setEnabled(modpack.getInstalledDirectory() != null && modpack.getInstalledDirectory().exists());
        if (modpack.getInstalledDirectory() != null && modpack.getInstalledDirectory().exists()) {
            moveFolder.setForeground(LauncherFrame.COLOR_BUTTON_BLUE);
            moveFolder.setHoverForeground(LauncherFrame.COLOR_BLUE);
        } else {
            moveFolder.setForeground(LauncherFrame.COLOR_GREY_TEXT);
        }
        moveFolder.addActionListener(e -> moveFolder());
        centerPanel.add(moveFolder, new GridBagConstraints(5, 0, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,5), 0,0));

        centerPanel.add(Box.createVerticalStrut(15), new GridBagConstraints(0, 1, 6, 1, 1, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0));

        JLabel buildSelectLabel = new JLabel(resources.getString("modpackoptions.version.text"));
        buildSelectLabel.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        buildSelectLabel.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        centerPanel.add(buildSelectLabel, new GridBagConstraints(0, 2, 6, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(0,0,0,0),0,0));

        recommended = new JRadioButton(resources.getString("modpackoptions.version.recommended"));
        recommended.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        recommended.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        recommended.setIcon(resources.getIcon("radio_deselected.png"));
        recommended.setSelectedIcon(resources.getIcon("radio_selected.png"));
        recommended.setFocusPainted(false);
        recommended.setOpaque(false);
        recommended.addActionListener(e -> selectRecommended());
        centerPanel.add(recommended, new GridBagConstraints(1, 3, 5, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(0,0,0,0), 0,0));

        latest = new JRadioButton(resources.getString("modpackoptions.version.latest"));
        latest.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        latest.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        latest.setIcon(resources.getIcon("radio_deselected.png"));
        latest.setSelectedIcon(resources.getIcon("radio_selected.png"));
        latest.setFocusPainted(false);
        latest.setOpaque(false);
        latest.addActionListener(e -> selectLatest());
        centerPanel.add(latest, new GridBagConstraints(1, 4, 5, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(0,0,0,0), 0, 0));

        manual = new JRadioButton(resources.getString("modpackoptions.version.manual"));
        manual.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        manual.setForeground(LauncherFrame.COLOR_WHITE_TEXT);
        manual.setIcon(resources.getIcon("radio_deselected.png"));
        manual.setSelectedIcon(resources.getIcon("radio_selected.png"));
        manual.setFocusPainted(false);
        manual.setOpaque(false);
        manual.addActionListener(e -> selectManual());
        centerPanel.add(manual, new GridBagConstraints(1, 5, 5, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(0,0,0,0),0,0));

        ButtonGroup versionType = new ButtonGroup();
        versionType.add(recommended);
        versionType.add(latest);
        versionType.add(manual);

        centerPanel.add(Box.createHorizontalStrut(20), new GridBagConstraints(0, 6, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0,0));
        centerPanel.add(Box.createHorizontalStrut(20), new GridBagConstraints(1, 6, 1, 1, 0, 0, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0,0));

        manualBuildList = new JComboBox();

        if (System.getProperty("os.name").toLowerCase(Locale.ENGLISH).contains("mac")) {
            manualBuildList.setUI(new MetalComboBoxUI());
        }

        AdvancedCellRenderer renderer = new AdvancedCellRenderer();
        renderer.setUnselectedBackgroundColor(LauncherFrame.COLOR_CENTRAL_BACK_OPAQUE);
        renderer.setUnselectedForegroundColor(LauncherFrame.COLOR_BUTTON_BLUE);
        renderer.setSelectedForegroundColor(LauncherFrame.COLOR_BUTTON_BLUE);
        renderer.setSelectedBackgroundColor(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);

        manualBuildList.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        manualBuildList.setEditable(false);
        manualBuildList.setForeground(LauncherFrame.COLOR_BUTTON_BLUE);
        manualBuildList.setBackground(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);
        //noinspection unchecked
        manualBuildList.setRenderer(renderer);
        manualBuildList.setUI(new SimpleButtonComboUI(new RoundedBorderFormatter(new RoundBorder(LauncherFrame.COLOR_BUTTON_BLUE, 1, 0)), resources, LauncherFrame.COLOR_SCROLL_TRACK, LauncherFrame.COLOR_SCROLL_THUMB));
        manualBuildList.setFocusable(false);
        manualBuildList.addActionListener(e -> buildUpdated());

        Object child = manualBuildList.getAccessibleContext().getAccessibleChild(0);
        BasicComboPopup popup = (BasicComboPopup)child;
        JList list = popup.getList();
        list.setSelectionForeground(LauncherFrame.COLOR_BUTTON_BLUE);
        list.setSelectionBackground(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);
        list.setBackground(LauncherFrame.COLOR_CENTRAL_BACK_OPAQUE);

        centerPanel.add(manualBuildList, new GridBagConstraints(2, 6, 4, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.VERTICAL, new Insets(5,0,0,0), 0,0));

        centerPanel.add(Box.createGlue(), new GridBagConstraints(0, 7, 6, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0));

        JPanel bottomButtons = new JPanel();
        bottomButtons.setOpaque(false);
        bottomButtons.setLayout(new BoxLayout(bottomButtons, BoxLayout.LINE_AXIS));

        RoundedButton deletePack = new RoundedButton(resources.getString("modpackoptions.delete.text"));
        deletePack.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        deletePack.setContentAreaFilled(false);
        if (modpack.getInstalledDirectory() != null) {
            deletePack.setForeground(LauncherFrame.COLOR_BUTTON_BLUE);
            deletePack.setHoverForeground(LauncherFrame.COLOR_BLUE);
        } else {
            deletePack.setForeground(LauncherFrame.COLOR_GREY_TEXT);
        }
        deletePack.addActionListener(e -> deletePack());
        deletePack.setEnabled(modpack.getInstalledDirectory() != null);
        bottomButtons.add(deletePack);

        bottomButtons.add(Box.createHorizontalGlue());

        RoundedButton resetPack = new RoundedButton(resources.getString("modpackoptions.reinstall.text"));
        resetPack.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        resetPack.setContentAreaFilled(false);
        if (modpack.getInstalledDirectory() != null) {
            resetPack.setForeground(LauncherFrame.COLOR_BUTTON_BLUE);
            resetPack.setHoverForeground(LauncherFrame.COLOR_BLUE);
        } else {
            resetPack.setForeground(LauncherFrame.COLOR_GREY_TEXT);
        }
        resetPack.addActionListener(e -> resetPack());
        resetPack.setEnabled(modpack.getInstalledDirectory() != null);
        bottomButtons.add(resetPack);

        centerPanel.add(bottomButtons, new GridBagConstraints(0, 8, 6, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0,0,0,0),0,0));
    }

    private void initValues() {
        File installDir = modpack.getInstalledDirectory();
        if (installDir == null) {
            installField.setText(resources.getString("modpackoptions.installfolder.none"));
        } else {
            installField.setText(installDir.getAbsolutePath());
        }

        for (String build : modpack.getBuilds()) {
            //noinspection unchecked
            manualBuildList.insertItemAt(new PackBuildItem(build, resources, modpack), 0);
        }

        if (manualBuildList.getItemCount() == 0) {
            recommended.setEnabled(false);
            latest.setEnabled(false);
            manual.setEnabled(false);
            recommended.setSelected(true);
            //noinspection unchecked
            manualBuildList.addItem(new PackBuildItem(resources.getString("modpackoptions.version.missing"), resources, modpack));
            manualBuildList.setEnabled(false);
            return;
        }

        String build = modpack.getBuild();

        if (build.equals(InstalledPack.RECOMMENDED)) {
            recommended.setSelected(true);
            selectRecommended();
        } else if (build.equals(InstalledPack.LATEST)) {
            latest.setSelected(true);
            selectLatest();
        } else {
            manual.setSelected(true);
            selectManual();
        }
    }

    protected void closeDialog() {
        dispose();
    }
}
