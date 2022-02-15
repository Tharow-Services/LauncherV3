package net.tharow.tantalum.minecraftcore.mojang.version.io;

import java.util.Map;

@SuppressWarnings({"unused"})
public class Downloads {
    private Artifact artifact;
    private Map<String, Artifact> classifiers;

    public Artifact getArtifact() {
        return artifact;
    }

    public Artifact getClassifier(String key) {
        return classifiers.get(key);
    }

    public void setArtifact(Artifact artifact){
        this.artifact = artifact;
    }
}
