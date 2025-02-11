

package net.tharow.tantalum.launcher.ui.components.discover;

import net.tharow.tantalum.launcher.ui.components.modpacks.ModpackSelector;
import net.tharow.tantalum.launchercore.TantalumConstants;
import net.tharow.tantalum.launchercore.install.LauncherDirectories;
import net.tharow.tantalum.platform.IPlatformApi;
import net.tharow.tantalum.ui.controls.TiledBackground;
import net.tharow.tantalum.ui.lang.ResourceLoader;
import net.tharow.tantalum.utilslib.Utils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.w3c.dom.Document;
import org.xhtmlrenderer.event.DocumentListener;
import org.xhtmlrenderer.resource.XMLResource;
import org.xhtmlrenderer.simple.XHTMLPanel;
import org.xhtmlrenderer.swing.DelegatingUserAgent;
import org.xhtmlrenderer.swing.FSMouseListener;
import org.xhtmlrenderer.swing.ImageResourceLoader;
import org.xhtmlrenderer.swing.SwingReplacedElementFactory;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DiscoverInfoPanel extends TiledBackground {

    final private XHTMLPanel panel;
    final private LauncherDirectories directories;
    final private ResourceLoader resources;
    private ActionListener loadListener = null;

    public DiscoverInfoPanel(final ResourceLoader loader, String discoverUrl, final IPlatformApi platform, final LauncherDirectories directories, final ModpackSelector modpackSelector) {
        super(loader.getImage("background_repeat2.png"));

        this.directories = directories;
        this.resources = loader;

        if (discoverUrl == null)
            discoverUrl = TantalumConstants.DISCOVER_URL;

        final String runnableAccessDiscover = discoverUrl;

        setLayout(new BorderLayout());
        this.panel = new XHTMLPanel();
        panel.setFont(resources.getFont(ResourceLoader.FONT_OPENSANS, 16));
        panel.setDefaultFontFromComponent(true);
        panel.addDocumentListener(new DocumentListener() {
            private boolean hasReloaded = false;

            @Override
            public void documentStarted() {

            }

            @Override
            public void documentLoaded() {
                triggerLoadListener();
            }

            @Override
            public void onLayoutException(Throwable throwable) {
                throwable.printStackTrace();

                if (!hasReloaded) {
                    hasReloaded = true;

                    EventQueue.invokeLater(() -> panel.setDocument(getDiscoverDocumentFromResource(), runnableAccessDiscover));
                }
            }

            @Override
            public void onRenderException(Throwable throwable) {
                throwable.printStackTrace();

                if (!hasReloaded) {
                    hasReloaded = true;

                    EventQueue.invokeLater(() -> panel.setDocument(getDiscoverDocumentFromResource(), runnableAccessDiscover));
                }
            }
        });

        for (Object listener : panel.getMouseTrackingListeners()) {
            panel.removeMouseTrackingListener((FSMouseListener)listener);
        }
        panel.addMouseTrackingListener(new DiscoverLinkListener(platform, modpackSelector));

        final DelegatingUserAgent uac = new DelegatingUserAgent();
        ImageResourceLoader imageLoader = new DiscoverResourceLoader();
        imageLoader.setRepaintListener(panel);
        imageLoader.clear();
        uac.setImageResourceLoader(imageLoader);
        panel.getSharedContext().getTextRenderer().setSmoothingThreshold(6.0f);
        panel.getSharedContext().setUserAgentCallback(uac);

        SwingReplacedElementFactory factory = new SwingReplacedElementFactory(panel, imageLoader);
        factory.reset();
        panel.getSharedContext().setReplacedElementFactory(factory);
        panel.getSharedContext().setFontMapping("Raleway", resources.getFont(ResourceLoader.FONT_RALEWAY, 12));

        EventQueue.invokeLater(() -> {
            try {
                File localCache = new File(directories.getCacheDirectory(), "discover.html");
                panel.setDocument(getDiscoverDocument(runnableAccessDiscover, localCache), runnableAccessDiscover);
            } catch (Exception ex) {
                //Can't load document from internet- don't beef
                ex.printStackTrace();

                triggerLoadListener();
            }
        });

        add(panel, BorderLayout.CENTER);
    }

    public void setLoadListener(ActionListener listener) {
        this.loadListener = listener;
    }

    protected void triggerLoadListener() {
        final ActionListener deferredListener = loadListener;
        if (deferredListener != null) {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    deferredListener.actionPerformed(new ActionEvent(this, 0, "loaded"));
                }
            });
            loadListener = null;
        }
    }

    public Document getDiscoverDocument(String url, File localCache) {
        //Attempt to retrieve the discover page from the live site, then a local cache, then an internal resource
        Document doc = getDiscoverDocumentFromLiveSite(url, localCache);
        if (doc != null)
            return doc;

        if (localCache.exists()) {
            doc = getDiscoverDocumentFromLocalCache(localCache);
            if (doc != null)
                return doc;
        }

        return getDiscoverDocumentFromResource();
    }

    public Document getDiscoverDocumentFromLiveSite(String url, File localCache) {
        try {
            HttpURLConnection conn = Utils.openHttpConnection(new URL(url));
            InputStream stream = conn.getInputStream();
            byte[] data = IOUtils.toByteArray(stream);

            Document doc = XMLResource.load(new ByteArrayInputStream(data)).getDocument();
            if (doc != null) {
                FileUtils.writeByteArrayToFile(localCache, data);
                return doc;
            }
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public Document getDiscoverDocumentFromLocalCache(File localCache) {
        try {
            return XMLResource.load(FileUtils.openInputStream(localCache)).getDocument();
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public Document getDiscoverDocumentFromResource() {
        return XMLResource.load(resources.getResourceAsStream("/discoverFallback.html")).getDocument();
    }
}