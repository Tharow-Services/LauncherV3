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

package net.tharow.tantalum.launcher.ui.components.modpacks;

import net.tharow.tantalum.launcher.settings.TantalumSettings;
import net.tharow.tantalum.launcher.ui.LauncherFrame;
import net.tharow.tantalum.launcher.ui.controls.modpacks.FindMoreWidget;
import net.tharow.tantalum.launcher.ui.controls.modpacks.ModpackWidget;
import net.tharow.tantalum.launchercore.auth.IAuthListener;
import net.tharow.tantalum.launchercore.auth.IUserType;
import net.tharow.tantalum.launchercore.image.ImageRepository;
import net.tharow.tantalum.launchercore.modpacks.*;
import net.tharow.tantalum.launchercore.modpacks.packinfo.CombinedPackInfo;
import net.tharow.tantalum.launchercore.modpacks.sources.IAuthoritativePackSource;
import net.tharow.tantalum.launchercore.modpacks.sources.IPackSource;
import net.tharow.tantalum.launchercore.modpacks.sources.NameFilterPackSource;
import net.tharow.tantalum.platform.IPlatformApi;
import net.tharow.tantalum.platform.IPlatformSearchApi;
import net.tharow.tantalum.platform.PlatformPackInfoRepository;
import net.tharow.tantalum.platform.http.HttpPlatformApi;
import net.tharow.tantalum.platform.http.HttpPlatformSearchApi;
import net.tharow.tantalum.platform.io.PlatformPackInfo;
import net.tharow.tantalum.platform.packsources.SearchResultPackSource;
import net.tharow.tantalum.platform.packsources.SinglePlatformSource;
import net.tharow.tantalum.rest.RestfulAPIException;
import net.tharow.tantalum.rest.io.PackInfo;
import net.tharow.tantalum.solder.ISolderApi;
import net.tharow.tantalum.solder.ISolderPackApi;
import net.tharow.tantalum.ui.controls.TintablePanel;
import net.tharow.tantalum.ui.controls.WatermarkTextField;
import net.tharow.tantalum.ui.controls.borders.RoundBorder;
import net.tharow.tantalum.ui.controls.list.SimpleScrollbarUI;
import net.tharow.tantalum.ui.lang.IRelocalizableResource;
import net.tharow.tantalum.ui.lang.ResourceLoader;
import net.tharow.tantalum.utilslib.DesktopUtils;
import net.tharow.tantalum.ui.controls.SimpleDocumentListener;
import net.tharow.tantalum.utilslib.Utils;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ModpackSelector extends TintablePanel implements IModpackContainer, IAuthListener, IRelocalizableResource {
    private ResourceLoader resources;
    private final PackLoader packLoader;
    private final IPackSource technicSolder;
    private final ImageRepository<ModpackModel> iconRepo;
    private final IPlatformApi platformApi;
    private final IPlatformSearchApi platformSearchApi;
    private final ISolderApi solderApi;

    private JPanel widgetList;
    private ModpackInfoPanel modpackInfoPanel;
    private LauncherFrame launcherFrame;
    private JTextField filterContents;
    private final FindMoreWidget findMoreWidget;

    private final MemoryModpackContainer defaultPacks = new MemoryModpackContainer();
    private final Map<String, ModpackWidget> allModpacks = new HashMap<>();
    private ModpackWidget selectedWidget;
    private PackLoadJob currentLoadJob;
    private Timer currentSearchTimer;

    private final Pattern slugRegex;
    private final Pattern siteRegex;

    private String lastFilterContents = "";

    private String findMoreUrl;

    private static final int MAX_SEARCH_STRING = 90;
    private final TantalumSettings settings;


    public ModpackSelector(ResourceLoader resources, PackLoader packLoader, IPackSource technicSolder, ISolderApi solderApi, IPlatformApi platformApi, IPlatformSearchApi platformSearchApi, ImageRepository<ModpackModel> iconRepo, TantalumSettings settings) {
        this.resources = resources;
        this.packLoader = packLoader;
        this.iconRepo = iconRepo;
        this.technicSolder = technicSolder;
        this.platformApi = platformApi;
        this.solderApi = solderApi;
        this.platformSearchApi = platformSearchApi;
        this.settings = settings;

        slugRegex = Pattern.compile("^[a-zA-Z0-9-]+$");
        siteRegex = Pattern.compile("^([a-zA-Z0-9-]+)\\.\\d+$");

        findMoreWidget = new FindMoreWidget(resources);
        findMoreWidget.addActionListener(e -> DesktopUtils.browseUrl(findMoreUrl));

        relocalize(resources);
    }
    public ModpackSelector(ModpackSelector modpackSelector, IPlatformApi platformApi){
        this.resources = modpackSelector.resources;
        this.packLoader = modpackSelector.packLoader;
        this.iconRepo = modpackSelector.iconRepo;
        this.technicSolder = modpackSelector.technicSolder;
        this.platformApi = platformApi;
        this.solderApi = modpackSelector.solderApi;
        this.platformSearchApi = modpackSelector.platformSearchApi;
        this.settings = modpackSelector.settings;

        slugRegex = Pattern.compile("^[a-zA-Z0-9-]+$");
        siteRegex = Pattern.compile("^([a-zA-Z0-9-]+)\\.\\d+$");

        findMoreWidget = new FindMoreWidget(resources);
        findMoreWidget.addActionListener(e -> DesktopUtils.browseUrl(findMoreUrl));

        relocalize(resources);
    }

    public void setInfoPanel(ModpackInfoPanel modpackInfoPanel) {
        this.modpackInfoPanel = modpackInfoPanel;
    }

    public void setLauncherFrame(LauncherFrame launcherFrame) {
        this.launcherFrame = launcherFrame;
    }

    public ModpackModel getSelectedPack() {
        if (selectedWidget == null)
            return null;

        return selectedWidget.getModpack();
    }

    public ModpackModel getModel(String model) {
        return allModpacks.get(model).getModpack();
    }

    private void changeWarning(){
        settings.setAcceptedWarnings(true);
        settings.save();
    }

    private void initComponents() {
        setLayout(new BorderLayout());
        setBackground(LauncherFrame.COLOR_SELECTOR_BACK);
        setMaximumSize(new Dimension(287, getMaximumSize().height));

        JPanel header = new JPanel();
        header.setLayout(new GridBagLayout());
        header.setBorder(BorderFactory.createEmptyBorder(4,8,4,4));
        header.setBackground(LauncherFrame.COLOR_SELECTOR_OPTION);
        add(header, BorderLayout.PAGE_START);

        filterContents = new WatermarkTextField(resources.getString("launcher.packselector.filter.hotfix"), LauncherFrame.COLOR_BLUE_DARKER);
        filterContents.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 14));
        filterContents.setBorder(new RoundBorder(LauncherFrame.COLOR_BUTTON_BLUE, 1, 8));
        filterContents.setForeground(LauncherFrame.COLOR_BLUE);

        if(settings.isAcceptedWarnings()){
            filterContents.setBackground(LauncherFrame.COLOR_REQUIREMENT_FAIL);
        } else {
            filterContents.setBackground(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);
        }


        //filterContents.setBackground(LauncherFrame.COLOR_REQUIREMENT_FAIL);
        filterContents.setSelectedTextColor(Color.black);
        filterContents.setSelectionColor(LauncherFrame.COLOR_BUTTON_BLUE);
        filterContents.setCaretColor(LauncherFrame.COLOR_BUTTON_BLUE);
        filterContents.setColumns(20);
        ((AbstractDocument)filterContents.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException
            {
                if(fb.getDocument().getLength() + string.length() <= MAX_SEARCH_STRING)
                {
                    fb.insertString(offset, string, attr);
                }
            }

            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException
            {
                fb.remove(offset, length);
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs)throws BadLocationException
            {
                int finalTextLength = (fb.getDocument().getLength() - length) + text.length();
                if (finalTextLength > MAX_SEARCH_STRING)
                    text = text.substring(0, text.length() - (finalTextLength-MAX_SEARCH_STRING));
                fb.replace(offset, length, text, attrs);
            }
        });
        filterContents.getDocument().addDocumentListener(new SimpleDocumentListener(this::detectFilterChanges));
        JLabel warning = new JLabel("Technic Packs From Search");
        warning.setLabelFor(filterContents);
        warning.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 14).deriveFont(Font.BOLD));
        //jLabel.setBorder(new RoundBorder(LauncherFrame.COLOR_BUTTON_BLUE, 1, 8));
        warning.setForeground(LauncherFrame.COLOR_BLUE);
        warning.setBackground(LauncherFrame.COLOR_BLUE_DARKER);
        warning.setVisible(false);

        //header.add(warning, new GridBagConstraints(1,1,1,2,1,0,17,1,new Insets(10,10,10,10),0,0));

        header.add(filterContents, new GridBagConstraints(1,0,1,1,1,0,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(3,0,3,0), 0, 12));
        widgetList = new JPanel();
        widgetList.setOpaque(false);
        widgetList.setLayout(new GridBagLayout());

        JScrollPane scrollPane = new JScrollPane(widgetList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUI(new SimpleScrollbarUI(LauncherFrame.COLOR_SCROLL_TRACK, LauncherFrame.COLOR_SCROLL_THUMB));
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 10));
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        add(scrollPane, BorderLayout.CENTER);

        widgetList.add(Box.createHorizontalStrut(294), new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0),0,0));
        widgetList.add(Box.createGlue(), new GridBagConstraints(0, 1, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0,0));
    }

    @Override
    public void clear() {
        allModpacks.clear();
        rebuildUI();
    }

    @Override
    public void replaceModpackInContainer(ModpackModel modpack) {
        if (allModpacks.containsKey(modpack.getName()))
            addModpackInternal(modpack);
    }

    @Override
    public void addModpackToContainer(ModpackModel modpack) {
        setTintActive(true);
        addModpackInternal(modpack);
    }

    protected void addModpackInternal(ModpackModel modpack) {
        final ModpackWidget widget = new ModpackWidget(resources, modpack, iconRepo.startImageJob(modpack));

        if (modpack.hasRecommendedUpdate()) {
            widget.setToolTipText(resources.getString("launcher.packselector.updatetip"));
        }
        widget.addActionListener(e -> {
            if (e.getSource() instanceof ModpackWidget)
                selectWidget((ModpackWidget)e.getSource());
        });

        if (widget.getModpack().isSelected()) {
            selectedWidget = widget;
        }

        if (allModpacks.containsKey(modpack.getName()) && allModpacks.get(modpack.getName()).isSelected())
            selectedWidget = widget;

        allModpacks.put(modpack.getName(), widget);

        rebuildUI();

        if (selectedWidget != null) {
            EventQueue.invokeLater(() -> {
                if (widget == selectedWidget)
                    selectWidget(widget);
                else
                    selectedWidget.scrollRectToVisible(new Rectangle(selectedWidget.getSize()));
            });
        }
    }

    @Override
    public void refreshComplete() {
        setTintActive(false);




        if (findMoreWidget.getWidgetData().equals(resources.getString("launcher.packselector.api"))) {
            if (allModpacks.size() == 0) {
                findMoreWidget.setWidgetData(resources.getString("launcher.packselector.badapi"));
                findMoreUrl = "https://www.technicpack.net/";
            } else {
                for(ModpackWidget widget : allModpacks.values()) {
                    findMoreUrl = widget.getModpack().getWebSite();
                    break;
                }
            }
        }

        if (selectedWidget == null || selectedWidget.getModpack() == null || !allModpacks.containsKey(selectedWidget.getModpack().getName())) {
            java.util.List<ModpackWidget> sortedPacks = new LinkedList<>(allModpacks.values());
            sortedPacks.sort((o1, o2) -> {
                int priorityCompare = Integer.compare(o2.getModpack().getPriority(), o1.getModpack().getPriority());
                if (priorityCompare != 0)
                    return priorityCompare;
                else if (o1.getModpack().getDisplayName() == null && o2.getModpack().getDisplayName() == null)
                    return 0;
                else if (o1.getModpack().getDisplayName() == null)
                    return -1;
                else if (o2.getModpack().getDisplayName() == null)
                    return 1;
                else
                    return o1.getModpack().getDisplayName().compareToIgnoreCase(o2.getModpack().getDisplayName());
            });
            if (sortedPacks.size() > 0) {
                selectWidget(sortedPacks.get(0));
            }
        }
    }

    protected void selectWidget(ModpackWidget widget) {
        if (selectedWidget != null)
            selectedWidget.setIsSelected(false);
        selectedWidget = widget;
        selectedWidget.setIsSelected(true);
        selectedWidget.getModpack().select();
        selectedWidget.scrollRectToVisible(new Rectangle(selectedWidget.getSize()));

        if (modpackInfoPanel != null)
            modpackInfoPanel.setModpack(widget.getModpack());

            final ModpackWidget refreshWidget = selectedWidget;
            Thread thread = new Thread("Modpack redownload " + selectedWidget.getModpack().getDisplayName()) {
                @Override
                public void run() {
                    try {
                        PlatformPackInfo updatedInfo = platformApi.getPlatformPackInfo(refreshWidget.getModpack().getName());
                        PackInfo infoToUse = updatedInfo;

                        if (updatedInfo != null && updatedInfo.hasSolder()) {
                            try {
                                ISolderPackApi solderPack = solderApi.getSolderPack(updatedInfo.getSolder(), updatedInfo.getName(), solderApi.getMirrorUrl(updatedInfo.getSolder()));
                                infoToUse = new CombinedPackInfo(solderPack.getPackInfo(), updatedInfo);
                            } catch (RestfulAPIException ignored) {
                            }
                        }

                        if (infoToUse != null)
                            refreshWidget.getModpack().setPackInfo(infoToUse);

                        EventQueue.invokeLater(() -> {
                            if (modpackInfoPanel != null)
                                modpackInfoPanel.setModpackIfSame(refreshWidget.getModpack());

                            if (refreshWidget.getModpack().hasRecommendedUpdate()) {
                                refreshWidget.setToolTipText(resources.getString("launcher.packselector.updatetip"));
                            } else {
                                refreshWidget.setToolTipText(null);
                            }

                            iconRepo.refreshRetry(refreshWidget.getModpack());
                            refreshWidget.updateFromPack(iconRepo.startImageJob(refreshWidget.getModpack()));

                            EventQueue.invokeLater(() -> {
                                revalidate();
                                repaint();
                            });
                        });

                    } catch (RestfulAPIException ex) {
                        ex.printStackTrace();
                    }
                }
            };
            thread.start();
    }

    protected void rebuildUI() {
        if (!EventQueue.isDispatchThread()) {
            EventQueue.invokeLater(this::rebuildUI);
            return;
        }

        GridBagConstraints constraints = new GridBagConstraints(0, 0, 1, 1, 1.0, 0.0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(0,0,0,0), 0,0);

        java.util.List<ModpackWidget> sortedPacks = new LinkedList<>(allModpacks.values());
        sortedPacks.sort((o1, o2) -> {
            int priorityCompare = Integer.compare(o2.getModpack().getPriority(), o1.getModpack().getPriority());
            if (priorityCompare != 0)
                return priorityCompare;
            else if (o1.getModpack().getDisplayName() == null && o2.getModpack().getDisplayName() == null)
                return 0;
            else if (o1.getModpack().getDisplayName() == null)
                return -1;
            else if (o2.getModpack().getDisplayName() == null)
                return 1;
            else
                return o1.getModpack().getDisplayName().compareToIgnoreCase(o2.getModpack().getDisplayName());
        });

        widgetList.removeAll();

        for(ModpackWidget sortedPack : sortedPacks) {
            widgetList.add(sortedPack, constraints);
            constraints.gridy++;
        }

        if (filterContents.getText().length() >= 3) {
            widgetList.add(findMoreWidget, constraints);
        }

        widgetList.add(Box.createHorizontalStrut(294), constraints);
        constraints.gridy++;

        constraints.weighty = 1.0;
        widgetList.add(Box.createGlue(), constraints);

        EventQueue.invokeLater(() -> {
            revalidate();
            repaint();
        });
    }

    @Override
    public void userChanged(IUserType user) {
        if (filterContents.getText().length() > 0)
            filterContents.setText("");
        else
            detectFilterChanges();

        if (user != null) {
            ArrayList<IPackSource> sources = new ArrayList<>(1);
            sources.add(technicSolder);
            defaultPacks.addPassthroughContainer(this);
            packLoader.createRepositoryLoadJob(defaultPacks, sources, null, true);
        }
    }

    public void forceRefresh() {
        lastFilterContents = "THIS IS A TERRIBLE HACK I'M BASICALLY FORCING A REFRESH BUT WITHOUT DOING ANY WORK";
        defaultPacks.clear();
        detectFilterChanges();
        ArrayList<IPackSource> sources = new ArrayList<>(1);
        sources.add(technicSolder);
        packLoader.createRepositoryLoadJob(defaultPacks, sources, null, true);
    }

    public void setFilter(String text) {
        filterContents.setText(text);
        detectFilterChanges();

        if (this.launcherFrame != null)
            this.launcherFrame.selectTab("modpacks");
    }

    protected void detectFilterChanges() {
        cancelJob();

        if (filterContents.getText().length() >= 3) {
            loadNewJob(filterContents.getText());
        } else if (lastFilterContents.length() >= 3) {
            clear();
            defaultPacks.addPassthroughContainer(this);
            for(ModpackModel modpack : defaultPacks.getModpacks()) {
                addModpackToContainer(modpack);
            }
            refreshComplete();
        }

        lastFilterContents = filterContents.getText();
    }

    private void InternalLoadNewJob(final String searchText){
        Utils.ignored = loadNewJob(searchText);
    }

    public PackLoadJob loadNewJob(final String searchText) {
        setTintActive(true);
        defaultPacks.removePassthroughContainer(this);

        currentSearchTimer = new Timer(500, e -> {
            String localSearchTag = searchText.trim();

            String localSearchUrl = searchText.trim();
            if (!localSearchUrl.startsWith("http://") && !localSearchUrl.startsWith("https://"))
                localSearchUrl = "https://" + localSearchTag;

            try {
                URI uri = new URI(localSearchUrl);
                String host = uri.getHost();
                String scheme = uri.getScheme();
                if (host != null && scheme != null && (scheme.equals("http") || scheme.equals("https"))) {
                    String path = uri.getPath();
                    if (path.startsWith("/"))
                        path = path.substring(1);
                    if (path.endsWith("/"))
                        path = path.substring(0, path.length()-1);
                    String[] fragments = path.split("/");

                    if ((fragments.length == 2 && fragments[0].equals("modpack")) || (fragments.length == 3 && fragments[1].equals("modpack"))) {
                        String slug = fragments[fragments.length-1];

                        Matcher siteMatcher = siteRegex.matcher(slug);
                        if (siteMatcher.find()) {
                            slug = siteMatcher.group(1);
                        }

                        Matcher slugMatcher = slugRegex.matcher(slug);
                        if (slugMatcher.find()) {
                            Utils.getLogger().warning("Http Search Site: " + scheme + host + "/" + " Slug: " +slug + " Search text: "+searchText);
                            IPlatformApi tempPlatform = new HttpPlatformApi(scheme + "://" + host + "/");
                            findMoreUrl = localSearchUrl;
                            findMoreWidget.setWidgetData(resources.getString("launcher.packselector.api"));
                            ArrayList<IPackSource> source = new ArrayList<>(1);
                            source.add(new SinglePlatformSource(tempPlatform, solderApi, slug));
                            currentLoadJob = packLoader.createRepositoryLoadJob(ModpackSelector.this, source, null, false);
                            return;
                        }
                    }
                }
            } catch (URISyntaxException ex) {
                //It wasn't a valid URI which is actually fine.
            }

            String encodedSearch;
            ArrayList<IPackSource> sources = new ArrayList<>(2);
            IPlatformSearchApi tempSearchapi = platformSearchApi;
            PackLoader temploader = packLoader;
            boolean doNormal= true;
            if(localSearchTag.charAt(0) == '`'){
                if(!settings.isAcceptedWarnings()){
                if(optionWarning()){
                    doNormal=false;
                    changeWarning();
                } else {
                    filterContents.setText("");
                    return;
                }

                }else {doNormal=false;}
            }

            if(doNormal)  {
                Utils.getLogger().config("Using Normal Search ");
                //findMoreWidget.setWidgetData("Mother Fucker");
                sources.add(new NameFilterPackSource(defaultPacks, localSearchTag));
                encodedSearch  = filterContents.getText();
            } else {
                //findMoreWidget.setWidgetData("Warning Using The Technic Platform\n Search Mode WILL cause issues with modpacks");
                IPlatformApi tempPlatform = new HttpPlatformApi();
                IAuthoritativePackSource tempauth = new PlatformPackInfoRepository(tempPlatform, solderApi);
                tempSearchapi = new HttpPlatformSearchApi();

                temploader = new PackLoader(temploader, tempauth);
                localSearchTag = localSearchTag.substring(1);
                Utils.getLogger().warning("Using Technic Platform For Search May Cause Issues");
                encodedSearch  = filterContents.getText().substring(1);
            }
            encodedSearch = URLEncoder.encode(encodedSearch, StandardCharsets.UTF_8);
            findMoreUrl = "https://www.technicpack.net/modpacks?q="+encodedSearch;
            findMoreWidget.setWidgetData(resources.getString("launcher.packselector.more"));

            sources.add(new SearchResultPackSource(tempSearchapi, localSearchTag));
            currentLoadJob= temploader.createRepositoryLoadJob(ModpackSelector.this, sources, null, false);
        });
        currentSearchTimer.setRepeats(false);
        currentSearchTimer.start();
        return currentLoadJob;
    }

    private void cancelJob() {
        if (currentLoadJob != null)
            currentLoadJob.cancel();

        if (currentSearchTimer != null) {
            currentSearchTimer.stop();
        }
    }

    @Override
    public void relocalize(ResourceLoader loader) {
        this.resources = loader;
        this.resources.registerResource(this);

        this.setOverIcon(resources.getIcon("loader.gif"));
        this.setTintActive(true);

        //Wipe controls
        removeAll();
        this.setLayout(null);

        initComponents();



        EventQueue.invokeLater(() -> {
            invalidate();
            repaint();
        });
    }


    protected boolean optionWarning(){
        int tempint = JOptionPane.showOptionDialog(this.launcherFrame,  "Warning Adding Technic Packs Using Search\nIs known to cause issues use at your own risk\nif Accepted This will not come up again", "Warning Technic Packs", JOptionPane.OK_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE, UIManager.getIcon("OptionPane.warningIcon"), new String[]{"I Understand","Cancel"}, "Cancel");
        switch (tempint){
            case JOptionPane.OK_OPTION -> Utils.getLogger().warning("Ok Option");
            case JOptionPane.NO_OPTION -> Utils.getLogger().warning("No Option");
            case JOptionPane.CANCEL_OPTION -> Utils.getLogger().warning("Cancel Option");
            case JOptionPane.CLOSED_OPTION -> Utils.getLogger().warning("Closed option");
            default -> throw new IllegalStateException("Unexpected value: " + tempint);
        }
        return tempint == JOptionPane.OK_OPTION;
    }
}
