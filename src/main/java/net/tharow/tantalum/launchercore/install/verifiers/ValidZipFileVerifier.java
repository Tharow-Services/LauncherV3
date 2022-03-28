

package net.tharow.tantalum.launchercore.install.verifiers;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

public class ValidZipFileVerifier implements IFileVerifier {
    @Override
    public boolean isFileValid(File file) {
        try (ZipFile zipfile = new ZipFile(file)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
