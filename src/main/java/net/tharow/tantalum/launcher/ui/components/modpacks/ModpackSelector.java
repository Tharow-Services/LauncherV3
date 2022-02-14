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
import net.tharow.tantalum.launchercore.modpacks.sources.IPackSource;
import net.tharow.tantalum.launchercore.modpacks.sources.NameFilterPackSource;
import net.tharow.tantalum.platform.IPlatformApi;
import net.tharow.tantalum.platform.http.HttpPlatformApi;
import net.tharow.tantalum.platform.io.PlatformPackInfo;
import net.tharow.tantalum.platform.packsources.SearchResultPackSource;
import net.tharow.tantalum.platform.packsources.SinglePlatformSource;
import net.tharow.tantalum.rest.RestfulAPIException;
import net.tharow.tantalum.rest.io.PackInfo;
import net.tharow.tantalum.solder.ISolderApi;
import net.tharow.tantalum.solder.ISolderPackApi;
import net.tharow.tantalum.tantalum.Tantalum;
import net.tharow.tantalum.tantalum.io.Platform;
import net.tharow.tantalum.ui.controls.TintablePanel;
import net.tharow.tantalum.ui.controls.WatermarkTextField;
import net.tharow.tantalum.ui.controls.borders.RoundBorder;
import net.tharow.tantalum.ui.controls.list.SimpleButtonComboUI;
import net.tharow.tantalum.ui.controls.list.SimpleScrollbarUI;
import net.tharow.tantalum.ui.controls.list.popupformatters.RoundedBorderFormatter;
import net.tharow.tantalum.ui.lang.IRelocalizableResource;
import net.tharow.tantalum.ui.lang.ResourceLoader;
import net.tharow.tantalum.utilslib.DesktopUtils;
import net.tharow.tantalum.ui.controls.SimpleDocumentListener;
import net.tharow.tantalum.utilslib.DisabledUntilFutureVersion;
import net.tharow.tantalum.utilslib.Utils;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboPopup;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;
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
    private final IPackSource defaultPackSource;
    private final ImageRepository<ModpackModel> iconRepo;
    private final Tantalum tantalum;
    private final ISolderApi solderApi;

    private JPanel widgetList;
    private ModpackInfoPanel modpackInfoPanel;
    private LauncherFrame launcherFrame;
    private JTextField filterContents;
    private JComboBox<HttpPlatformApi> searchList;
    private Platform selectedPlatform;
    private final FindMoreWidget findMoreWidget;

    private final DocumentFilter documentFilter = new DocumentFilter(){
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
    };

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


    public ModpackSelector(ResourceLoader resources, PackLoader packLoader, IPackSource defaultPackSource, ISolderApi solderApi, Tantalum tantalum, ImageRepository<ModpackModel> iconRepo, TantalumSettings settings) {
        this.resources = resources;
        this.packLoader = packLoader;
        this.iconRepo = iconRepo;
        this.defaultPackSource = defaultPackSource;
        this.tantalum = tantalum;
        this.solderApi = solderApi;
        this.settings = settings;

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

    private void changeWarning(){
        settings.setAcceptedWarnings(true);
        settings.save();
    }

    protected void changeStream() {
        assert searchList.getSelectedItem() != null;
        this.selectedPlatform = (Platform) searchList.getSelectedItem();
        //settings.setBuildStream(((StreamItem) searchList.getSelectedItem()).getStream());
        //settings.save();
    }

    private void initControlValues(){
        for (ActionListener listener : searchList.getActionListeners()) {
            searchList.removeActionListener(listener);
        }
        searchList.removeAllItems();
        tantalum.getApis().forEach((platform)->searchList.addItem(platform));
        searchList.setSelectedIndex(0);
        searchList.addActionListener(e -> changeStream());
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
        filterContents.setBackground(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);
        filterContents.setSelectedTextColor(Color.black);
        filterContents.setSelectionColor(LauncherFrame.COLOR_BUTTON_BLUE);
        filterContents.setCaretColor(LauncherFrame.COLOR_BUTTON_BLUE);
        filterContents.setColumns(20);
        ((AbstractDocument)filterContents.getDocument()).setDocumentFilter(documentFilter);
        filterContents.getDocument().addDocumentListener( new SimpleDocumentListener(this::detectFilterChanges));

        header.add(filterContents, new GridBagConstraints(1,0,1,1,1,0,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,0,3,0), 0, 12));

        searchList = new JComboBox<>();
        searchList.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        searchList.setEditable(false);
        searchList.setBorder(new RoundBorder(LauncherFrame.COLOR_BUTTON_BLUE, 1, 10));
        searchList.setForeground(LauncherFrame.COLOR_BUTTON_BLUE);
        searchList.setBackground(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);
        searchList.setUI(new SimpleButtonComboUI(new RoundedBorderFormatter(new RoundBorder(LauncherFrame.COLOR_BUTTON_BLUE, 1, 0)), resources, LauncherFrame.COLOR_SCROLL_TRACK, LauncherFrame.COLOR_SCROLL_THUMB));
        searchList.setFocusable(false);

        Object child = searchList.getAccessibleContext().getAccessibleChild(0);
        BasicComboPopup popup = (BasicComboPopup)child;
        JList<?> list = popup.getList();
        list.setSelectionForeground(LauncherFrame.COLOR_BUTTON_BLUE);
        list.setSelectionBackground(LauncherFrame.COLOR_FORMELEMENT_INTERNAL);
        list.setBackground(LauncherFrame.COLOR_CENTRAL_BACK_OPAQUE);

        header.add(searchList, new GridBagConstraints(1,1,1,1,1,0,GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(5,0,5,0), 0, 12));

        widgetList = new JPanel();
        widgetList.setOpaque(false);
        widgetList.setLayout(new GridBagLayout());

        JScrollPane scrollPane = new JScrollPane(widgetList, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUI(new SimpleScrollbarUI(LauncherFrame.COLOR_SCROLL_TRACK, LauncherFrame.COLOR_SCROLL_THUMB));
        scrollPane.getVerticalScrollBar().setPreferredSize(new Dimension(10, 9));
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        add(scrollPane, BorderLayout.CENTER);

        widgetList.add(Box.createHorizontalStrut(294), new GridBagConstraints(0, 0, 1, 1, 1, 0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0,0,0,0),0,0));
        widgetList.add(Box.createGlue(), new GridBagConstraints(0, 2, 1, 1, 1.0, 1.0, GridBagConstraints.WEST, GridBagConstraints.BOTH, new Insets(0,0,0,0), 0,0));
        initControlValues();
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
        //Debug.getConfig(defaultPacks);
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
                        PlatformPackInfo updatedInfo = tantalum.getPlatformPackInfo(refreshWidget.getModpack().getName());
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
            sources.add(defaultPackSource);
            defaultPacks.addPassthroughContainer(this);
            packLoader.createRepositoryLoadJob(defaultPacks, sources, null, true);
        }
    }

    public void forceRefresh() {
        lastFilterContents = "THIS IS A TERRIBLE HACK I'M BASICALLY FORCING A REFRESH BUT WITHOUT DOING ANY WORK";
        defaultPacks.clear();
        detectFilterChanges();
        ArrayList<IPackSource> sources = new ArrayList<>(1);
        sources.add(defaultPackSource);
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


    public void loadNewJob(final String searchText) {
        setTintActive(true);
        defaultPacks.removePassthroughContainer(this);

        currentSearchTimer = new Timer(500, e -> {
            String localSearchTag = searchText.trim();

            String encodedSearch;
            ArrayList<IPackSource> sources = new ArrayList<>(2);
            sources.add(new NameFilterPackSource(defaultPacks, localSearchTag));
                encodedSearch  = filterContents.getText();

            try {
                encodedSearch = URLEncoder.encode(encodedSearch, StandardCharsets.UTF_8.toString());
            } catch (UnsupportedEncodingException ignored) {}
            //findMoreUrl = selectedPlatform.getUrl()+"modpacks?q="+encodedSearch;
            findMoreWidget.setWidgetData(resources.getString("launcher.packselector.more"));
            //final Platform platform = (Platform) searchList.getSelectedItem();
            //Utils.getLogger().constructor("Platform "+selectedPlatform.getUrl());
            //final HttpPlatformApi api = tantalum.getPlatformApi(platform);//tantalum.getPlatformApi(selectedPlatform.get());
            final SearchResultPackSource source = new SearchResultPackSource(searchList.getItemAt(searchList.getSelectedIndex()), localSearchTag);
            sources.add(source);
            Utils.logDebug("Whats Currently in the source: ");
            sources.forEach(iPackSource -> Utils.logDebug(iPackSource.getSourceName()));
            //Debug.getConfig(this);
            currentLoadJob = packLoader.createRepositoryLoadJob(ModpackSelector.this, sources, null, false);
        });
        currentSearchTimer.setRepeats(false);
        currentSearchTimer.start();
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
}
