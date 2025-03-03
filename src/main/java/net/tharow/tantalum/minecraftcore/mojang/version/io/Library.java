/*
 * This file is part of Technic Minecraft Core.
 * Copyright ©2015 Syndicate, LLC
 *
 * Technic Minecraft Core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Technic Minecraft Core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * as well as a copy of the GNU Lesser General Public License,
 * along with Technic Minecraft Core.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tharow.tantalum.minecraftcore.mojang.version.io;

import net.tharow.tantalum.launchercore.TantalumConstants;
import net.tharow.tantalum.launchercore.exception.DownloadException;
import net.tharow.tantalum.utilslib.OperatingSystem;
import net.tharow.tantalum.utilslib.Utils;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static net.tharow.tantalum.launchercore.TantalumConstants.FALLBACK_MIRROR;
@SuppressWarnings({"unused"})
public class Library {

    private static final Pattern GRADLE_PATTERN = Pattern.compile("^([^:@]+):([^:@]+):([^:@]+)(?::([^:@]+))?(?:@([^:@]+))?$");
    private static final Pattern FORGE_MAVEN_ROOT = Pattern.compile("^https://files\\.minecraftforge\\.net/maven/(.+)$");

    // JSON fields
    private String name;
    private List<Rule> rules;
    private Downloads downloads;
    private Map<OperatingSystem, String> natives;
    private ExtractRules extract;
    private String url;

    // Gradle specifier/Maven coordinates
    // groupId:artifactId:version[:classifier][@extension]
    private transient String gradleGroupId;
    private transient String gradleArtifactId;
    private transient String gradleVersion;
    private transient String gradleClassifier;
    private transient String gradleExtension = "jar";

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public Map<OperatingSystem, String> getNatives() {
        return natives;
    }

    public ExtractRules getExtract() {
        return extract;
    }

    private void parseName() {
        // Don't reparse if it's already been parsed
        if (gradleGroupId != null)
            return;

        Matcher m = GRADLE_PATTERN.matcher(name);

        if (!m.matches()) {
            throw new IllegalStateException("Cannot parse empty gradle specifier");
        }

        gradleGroupId = m.group(1);
        gradleArtifactId = m.group(2);
        gradleVersion = m.group(3);
        gradleClassifier = m.group(4);
        String extension = m.group(5);
        if (extension != null && !extension.isEmpty())
            gradleExtension = extension;
    }

    public boolean isForCurrentOS() {
        if (rules == null) {
            return true;
        }
        return Rule.isAllowable(rules, null);
    }

    public boolean hasNatives() {
        return natives != null && !natives.isEmpty();
    }

    public String getArtifactPath() {
        return getArtifactPath(null);
    }

    public String getArtifactPath(String nativeClassifier) {
        if (this.name == null) {
            throw new IllegalStateException("Cannot get artifact path of empty/blank artifact");
        }

        // Make sure the gradle specifier is parsed
        parseName();

        String filename = getArtifactFilename(nativeClassifier);

        return gradleGroupId.replace('.', '/') + '/' + gradleArtifactId + '/' + gradleVersion + '/' + filename;
    }

    public String getArtifactFilename(String nativeClassifier) {
        if (this.name == null) {
            throw new IllegalStateException("Cannot get artifact filename of empty/blank artifact");
        }

        // Make sure the gradle specifier is parsed
        parseName();

        String filename = gradleArtifactId + '-' + gradleVersion;

        // The native classifier overrides the regular classifier
        if (nativeClassifier != null)
            filename += '-' + nativeClassifier;
        else if (gradleClassifier != null && !gradleClassifier.isEmpty())
            filename += '-' + gradleClassifier;

        filename += '.' + gradleExtension;

        return filename;
    }

    public String getArtifactSha1(String nativeClassifier) {
        if (downloads == null)
            return null;

        Artifact artifact;

        if (nativeClassifier != null)
            artifact = downloads.getClassifier(nativeClassifier);
        else
            artifact = downloads.getArtifact();

        if (artifact != null)
            return artifact.getSha1();

        return null;
    }

    public String getDownloadUrl(String path) throws DownloadException {
        Set<String> possibleUrls = new LinkedHashSet<>(8);

        // Check the old-style URL (Forge 1.6, I think?)
        // It acts as a Maven root URL
        if (this.url != null) {
            possibleUrls.add(this.url + path);
        }

        // Check if an artifact URL is specified (downloads -> artifact -> url), only if it doesn't have natives
        // This is a fully specified URL
        if (!hasNatives()) {
            String artifactUrl = null;

            if (downloads != null) {
                Artifact artifact = downloads.getArtifact();

                if (artifact != null)
                    artifactUrl = artifact.getUrl();
            }

            if (artifactUrl != null && !artifactUrl.isEmpty()) {
                // Check if this URL is in Minecraft Forge's Maven repo and add ours as a primary mirror
                Matcher m = FORGE_MAVEN_ROOT.matcher(artifactUrl);
                if (m.matches())
                    possibleUrls.add(TantalumConstants.FORGE_REPO_URL + m.group(1));

                possibleUrls.add(artifactUrl);
            }
        }

        // Check if any fallback mirrors we know of have this library
        // These also work as a Maven root URL
        for (String string : FALLBACK_MIRROR) {
            possibleUrls.add(string + path);
        }

        for (String possibleUrl : possibleUrls) {
            if (Utils.pingHttpURL(possibleUrl)) {
                return possibleUrl;
            }
        }

        throw new DownloadException("Failed to download library " + path + ": no mirror found");
    }

    public boolean isForge() {
        return name.startsWith("net.minecraftforge:forge:") || name.startsWith("net.minecraftforge:minecraftforge:");
    }

    public boolean isLog4j() {
        return name.startsWith("org.apache.logging.log4j:");
    }

    public void setDownloads(Downloads downloads) {
        this.downloads = downloads;
    }
}
