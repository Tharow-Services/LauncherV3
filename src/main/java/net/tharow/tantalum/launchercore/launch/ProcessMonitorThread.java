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

package net.tharow.tantalum.launchercore.launch;

import net.tharow.tantalum.launchercore.logging.Level;
import net.tharow.tantalum.launchercore.logging.Logger;
import net.tharow.tantalum.utilslib.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.logging.LogRecord;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ProcessMonitorThread extends Thread {

    private final GameProcess process;
    private final Logger logger;
    private static final Pattern MC_PATTERN = Pattern.compile("(\\[(?<Date>\\d\\d\\:\\d\\d\\:\\d\\d)\\]) \\[(?<Source>[^\\/]*)\\/(?<Level>[^\\]]*)\\]: (\\[(?<Special>[^\\]]*)\\]: )?(\\[(?<Method>[^\\]]*)\\])?(?<Message>.*)");
    private static final String LOGGER_NAME = "Minecraft";


    public ProcessMonitorThread(GameProcess process) {
        super("ProcessMonitorThread");
        this.process = process;
        this.logger = Logger.getLogger(LOGGER_NAME);
        this.logger.setParent(Utils.getLogger());
    }

    public void run() {
        InputStreamReader reader = new InputStreamReader(this.process.getProcess().getInputStream());
        InputStreamReader streamReader = new InputStreamReader(this.process.getProcess().getErrorStream());
        //OutputStreamWriter writer = new OutputStreamWriter(this.process.getProcess().getOutputStream());
        BufferedReader buf1 = new BufferedReader(streamReader);
        BufferedReader buf = new BufferedReader(reader);

        String line;
        String errorLine = null;

        {
            try {
                while (((line = buf.readLine()) != null) || ((errorLine = buf1.readLine()) != null)) {
                    //System.out.println(" " + line);
                    LogRecord record;
                    //record.setSourceClassName(this.getName());
                    if (line != null) {
                        final Matcher mc = MC_PATTERN.matcher(line);
                        if (mc.matches()) {
                            record = new LogRecord(Level.INFO_BOLD, "");
                            record.setSourceClassName(mc.group("Source"));
                            record.setSourceMethodName(mc.group("Method"));
                            record.setLevel(Level.convertMC(mc.group("Level")));
                            record.setMessage(mc.group("Message"));
                            record.setLoggerName(LOGGER_NAME);
                            logger.log(record);
                        } else {
                            logger.log(Level.INFO, line.trim());
                            //logger.warning("Process Monitor Thread Didn't Match Minecraft Regex");
                        }
                    }
                    if (errorLine != null) {
                        logger.log(Level.SEVERE, errorLine.trim());
                    }


                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            } finally {
                try {
                    buf.close();
                    buf1.close();
                } catch (IOException ex) {
    					logger.log(Level.SEVERE, null, ex);
                } finally {
                    try {
                        process.getProcess().waitFor();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        System.out.println("Process exited with error code " + process.getProcess().exitValue());

        if (process.getExitListener() != null) {
            process.getExitListener().onProcessExit();
        }
    }
}
