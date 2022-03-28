

package net.tharow.tantalum.launchercore.launch;

import java.util.List;

public class GameProcess {
    private final List<String> commands;
    private final Process process;
    private ProcessExitListener exitListener;
    private final ProcessMonitorThread monitorThread;

    public GameProcess(List<String> commands, Process process) {
        this.commands = commands;
        this.process = process;

        this.monitorThread = new ProcessMonitorThread(this);
        this.monitorThread.start();
    }

    public ProcessExitListener getExitListener() {
        return exitListener;
    }

    public void setExitListener(ProcessExitListener exitListener) {
        this.exitListener = exitListener;
    }

    public Process getProcess() {
        return process;
    }
}
