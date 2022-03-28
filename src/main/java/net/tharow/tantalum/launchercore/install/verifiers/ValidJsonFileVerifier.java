

package net.tharow.tantalum.launchercore.install.verifiers;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.tharow.tantalum.utilslib.Utils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public class ValidJsonFileVerifier implements IFileVerifier {
    private final Gson validatingGson;

    public ValidJsonFileVerifier(Gson validatingGson) {
        this.validatingGson = validatingGson;
    }

    @Override
    public boolean isFileValid(File file) {
        try {
            String json = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
            JsonObject obj = validatingGson.fromJson(json, JsonObject.class);

            return (obj != null);
        } catch (Exception ex) {
            Utils.getLogger().log(Level.SEVERE, "An exception was raised while verifying " + file.getAbsolutePath() + "- this probably just means the file is invalid, in which case this is not an error:", ex);
        }

        return false;
    }
}
