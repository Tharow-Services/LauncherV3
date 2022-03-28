

package net.tharow.tantalum.launchercore.install.verifiers;

import net.tharow.tantalum.utilslib.Utils;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class FileSizeVerifier implements IFileVerifier {
    private final long size;

    public FileSizeVerifier(long size) {
        this.size = size;
    }

    @Override
    public boolean isFileValid(File file) {
        Utils.getLogger().config("File Size Verifier Was Called For "+file.getAbsolutePath());
        return FileUtils.sizeOf(file) == size;
    }
}
