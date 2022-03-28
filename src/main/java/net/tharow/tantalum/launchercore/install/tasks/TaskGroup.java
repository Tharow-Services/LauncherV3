

package net.tharow.tantalum.launchercore.install.tasks;

import net.tharow.tantalum.launchercore.install.IWeightedTasksQueue;
import net.tharow.tantalum.launchercore.install.InstallTasksQueue;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class TaskGroup implements IWeightedTasksQueue, IInstallTask {
    private final String groupName;
    private final LinkedList<IInstallTask> taskList = new LinkedList<>();
    private final Map<IInstallTask, Float> taskWeights = new HashMap<>();

    private float totalWeight = 0;

    private int taskProgress = 0;
    private String fileName = "";

    public TaskGroup(String name) {
        this.groupName = name;
    }

    @Override
    public String getTaskDescription() {
        return groupName.replace("%s", fileName);
    }

    @Override
    public float getTaskProgress() {

        if (taskList.size() == 0)
            return 0;
        if (totalWeight == 0)
            return 0;

        float completedWeight = 0;
        for (int i = 0; i < taskProgress; i++) {
            IInstallTask task = taskList.get(i);
            if (taskWeights.containsKey(task)) {
                completedWeight += taskWeights.get(task);
            }
        }

        float finishedTasksProgress = (completedWeight / totalWeight);
        IInstallTask currentTask = taskList.get(taskProgress);
        float currentTaskProgress = (currentTask.getTaskProgress() / 100.0f);

        float currentTaskWeight = 1;
        if (taskWeights.containsKey(currentTask))
            currentTaskWeight = taskWeights.get(currentTask);

        currentTaskProgress *= (currentTaskWeight / totalWeight);

        return (finishedTasksProgress + currentTaskProgress) * 100.0f;
    }

    @Override
    public void runTask(InstallTasksQueue queue) throws IOException, InterruptedException {
        while (taskProgress < taskList.size()) {
            if (Thread.interrupted())
                throw new InterruptedException();
            IInstallTask currentTask = taskList.get(taskProgress);
            fileName = currentTask.getTaskDescription();
            currentTask.runTask(queue);
            queue.refreshProgress();
            taskProgress++;
        }
    }

    @Override
    public void addNextTask(IInstallTask task) {
        addNextTask(task, 1);
    }

    @Override
    public void addTask(IInstallTask task) {
        addTask(task, 1);
    }

    public void addNextTask(IInstallTask task, float weight) {
        taskList.addFirst(task);
        taskWeights.put(task, weight);
        totalWeight += weight;
    }

    public void addTask(IInstallTask task, float weight) {
        taskList.addLast(task);
        taskWeights.put(task, weight);
        totalWeight += weight;
    }
}
