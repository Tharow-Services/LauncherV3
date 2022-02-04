/*
 * This file is part of Technic UI Core.
 * Copyright ©2015 Syndicate, LLC
 *
 * Technic UI Core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Technic UI Core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * as well as a copy of the GNU Lesser General Public License,
 * along with Technic UI Core.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.tharow.tantalum.ui.components;

import net.tharow.tantalum.launchercore.logging.BuildLogFormatter;
import net.tharow.tantalum.launchercore.logging.Level;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class ConsoleHandler extends Handler {

    private final Console console;

    public ConsoleHandler(Console console) {
        this.console = console;
    }

    @Override
    public void publish(@NotNull LogRecord record) {
        //formatter.format(record);
        this.console.log(record);
        //this.console.log(formatter.format(record), Level.convert(record.getLevel().getName()));
        //this.console.log(record.getMessage() + '\n', Level.convert(record.getLevel()));
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }
}
