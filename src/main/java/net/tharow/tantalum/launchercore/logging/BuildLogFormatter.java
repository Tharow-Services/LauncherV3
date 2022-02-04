/*
 * This file is part of Technic Launcher Core.
 * Copyright ©2015 Syndicate, LLC
 *
 * Technic Launcher Core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Technic Launcher Core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * as well as a copy of the GNU Lesser General Public License,
 * along with Technic Launcher Core.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tharow.tantalum.launchercore.logging;

import net.tharow.tantalum.utilslib.Utils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public class BuildLogFormatter extends Formatter {
    private final SimpleDateFormat date;
    private final String launcherBuild;
    private static final String DefaultStartChar = " [";
    private static final String DefaultEndChar = "]";
    public static final String NO_LAUNCHER_BUILD = "NO_LAUNCHER_BUILD";

    public BuildLogFormatter(String launcherBuild) {
        date = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        this.launcherBuild = launcherBuild;
    }

    @Override
    public String format(@NotNull LogRecord record) {
        StringBuilder builder = new StringBuilder();
        final boolean isDate = (!launcherBuild.equals(NO_LAUNCHER_BUILD));
        if(isDate){
            builder.append(builderRecord(launcherBuild,"[B#", null));
            builder.append(builderRecord(date.format(record.getMillis())));
        }
        if (record.getLoggerName() != null){
            if (!record.getLoggerName().equals(Utils.getLogger().getName())){

                if(!record.getSourceClassName().endsWith("Logger")){
                    builder.append(builderRecord(record.getLoggerName()));
                    builder.append(builderRecord(record.getSourceMethodName()));
                    builder.append(builderRecord(record.getSourceMethodName(),isDate));
                }else {
                    builder.append(builderRecord(record.getLoggerName(),isDate));
                }

            }else {
                builder.append(builderRecord("Launcher", isDate));
            }

        } else {
            builder.append(builderRecord("System", isDate));
        }
        if(isDate){builder.append(builderRecord(record.getLevel().getName().toUpperCase(Locale.ENGLISH), null, "] "));}

        builder.append(formatMessage(record));
        builder.append('\n');

        if (record.getThrown() != null) {
            StringWriter writer = new StringWriter();
            record.getThrown().printStackTrace(new PrintWriter(writer));
            builder.append(writer);
        }

        return builder.toString();
    }

    public String builderRecord(@NotNull String thing, final boolean isDate){
        if(isDate){
            return builderRecord(thing);
        } else {
            return builderRecord(thing, "[", "] ");
        }
    }
    public String builderRecord(@NotNull String thing){return builderRecord(thing,null,null);}
    public String builderRecord(@NotNull String thing, @Nullable String start, @Nullable String end){
        return ((start != null) ? start : DefaultStartChar) +
                thing +
                ((end != null) ? end : DefaultEndChar);
    }
}