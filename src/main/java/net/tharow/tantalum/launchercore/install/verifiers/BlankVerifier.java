package net.tharow.tantalum.launchercore.install.verifiers;

import java.io.File;

public class BlankVerifier implements IFileVerifier {
    @Override
    public boolean isFileValid(File file) {
        return true;
    }
}
