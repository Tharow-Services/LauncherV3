package net.tharow.tantalum.launchercore.install.verifiers;

import net.tharow.tantalum.utilslib.Utils;

import java.io.File;

public class BlankVerifier implements IFileVerifier {
    @Override
    public boolean isFileValid(File file) {
        Utils.getLogger().config("Blank Verifier Was Called For: "+file.getAbsolutePath());
        return true;
    }
}
