package service;

import model.Epic;
import enumirations.Status;
import model.SubTask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {
    private Map<Integer, Task> taskMap;
    private Map<Integer, Epic> epicMap;
    private Map<Integer, SubTask> subTaskMap;
    private Integer idCounter;
    private HistoryManager historyManager;

    public InMemoryTaskManager() {
        taskMap = new HashMap<>();
        epicMap = new HashMap<>();
        subTaskMap = new HashMap<>();
        idCounter = 1;
        historyManager = Managers.getDefaultHistory();
    }

    @Override
    public List<Task> getTaskList() {
        return new ArrayList<>(taskMap.values());
    }

    @Override
    public List<Epic> getEpicList() {
        return new ArrayList<>(epicMap.values());
    }

    @Override
    public List<SubTask> getSubTaskList() {
        return new ArrayList<>(subTaskMap.values());
    }

    @Override
    public void clearTaskMap() {
        historyManager.removeAll(taskMap.keySet());
        taskMap.clear();
    }

    @Override
    public void clearEpicMap() {
        historyManager.removeAll(epicMap.keySet());
        historyManager.removeAll(subTaskMap.keySet());
        epicMap.clear();
        subTaskMap.clear();
    }

    @Override
    public void clearSubTaskMap() {
        historyManager.removeAll(subTaskMap.keySet());
        subTaskMap.clear();
        for (Epic epic : epicMap.values()) {
            epic.getSubTasksId().clear();
        }
    }

    @Override
    public Task getTaskById(int id) {
        if (!taskMap.containsKey(id)) {
            return null;
        }
        Task findingTask = taskMap.get(id);
        Task task = new Task(findingTask);
        historyManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicById(int id) {
        if (!epicMap.containsKey(id)) {
            return null;
        }
        Epic findingEpic = epicMap.get(id);
        Epic epic = new Epic(findingEpic);
        historyManager.add(findingEpic);
        return epic;
    }

    @Override
    public SubTask getSubTaskById(int id) {
        if (!subTaskMap.containsKey(id)) {
            return null;
        }
        SubTask findingSubTask = subTaskMap.get(id);
        SubTask subTask = new SubTask(findingSubTask);
        historyManager.add(findingSubTask);
        return subTask;
    }

    @Override
    public Task deleteTaskById(int id) {
        historyManager.remove(id);
        return taskMap.remove(id);
    }

    @Override
    public Epic deleteEpicById(int id) {
        if (epicMap.containsKey(id)) {
            List<Integer> subTasksId = epicMap.get(id).getSubTasksId();
            for (Integer subTaskId : subTasksId) {
                subTaskMap.remove(subTaskId);
                historyManager.remove(subTaskId);
            }
            historyManager.remove(id);
            return epicMap.remove(id);
        }
        return null;
    }

    @Override
    public SubTask deleteSubTaskById(int id) {
        if (subTaskMap.containsKey(id)) {
            int epicId = subTaskMap.get(id).getEpicId();
            Epic epic = epicMap.get(epicId);
            epic.removeFromSubTasksId(id);
            epic.setStatus(calculateEpicStatus(epic));
            historyManager.remove(id);
            return subTaskMap.remove(id);
        }
        return null;
    }

    @Override
    public Task createTask(Task task) {
        if (task == null) {
            return null;
        }
        task.setId(idCounter);
        Task createdTask = new Task(task);
        taskMap.put(idCounter, createdTask);
        idCounter++;
        return task;
    }

    @Override
    public Epic createEpic(Epic epic) {
        if (epic == null) {
            return null;
        }
        epic.setId(idCounter);
        epic.setStatus(Status.NEW);
        Epic createdEpic = new Epic(epic);
        epicMap.put(idCounter, createdEpic);
        idCounter++;
        return epic;
    }

    @Override
    public SubTask createSubTask(SubTask subTask) {
        if (subTask == null) {
            return null;
        }
        subTask.setId(idCounter);
        if (epicMap.containsKey(subTask.getEpicId())) {
            SubTask createdSubTask = new SubTask(subTask);
            subTaskMap.put(idCounter, createdSubTask);

            Epic epic =  epicMap.get(subTask.getEpicId());
            epic.addToSubTasksId(idCounter);
            epic.setStatus(calculateEpicStatus(epic));
            idCounter++;
            return subTask;
        } else {
            return null;
        }
    }

    @Override
    public Task updateTask(Task task) {
        if (taskMap.containsKey(task.getId())) {
            Task existingTask = new Task(task);
            taskMap.put(existingTask.getId(), existingTask);
        }
        return task;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        if (epicMap.containsKey(epic.getId())) {
            Epic existingEpic = epicMap.get(epic.getId());
            existingEpic.setName(epic.getName());
            existingEpic.setDescription(epic.getDescription());
        }
        return epic;
    }

    @Override
    public SubTask updateSubTask(SubTask subTask) {
        if (subTaskMap.containsKey(subTask.getId())) {
            SubTask existingSubTask = new SubTask(subTask);
            subTaskMap.put(existingSubTask.getId(), existingSubTask);
            if (epicMap.containsKey(existingSubTask.getEpicId())) {
                Epic epic = epicMap.get(existingSubTask.getEpicId());
                epic.setStatus(calculateEpicStatus(epic));
            }
        }
        return subTask;
    }

    @Override
    public List<SubTask> getEpicSubTasks(Epic epic) {
        ArrayList<SubTask> epicSubTasks = new ArrayList<>();
        if (epicMap.containsKey(epic.getId())) {
            for (Integer id : epic.getSubTasksId()) {
                epicSubTasks.add(getSubTaskById(id));
            }
        }
        return epicSubTasks;
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }

    private Status calculateEpicStatus(Epic epic) {
        List<Integer> subTasksId = epic.getSubTasksId();
        if (subTasksId.isEmpty()) {
            return Status.NEW;
        } else {
            boolean hasNewStatus = false;
            boolean hasDoneStatus = false;
            for (Integer subTaskId : subTasksId) {
                Status currentStatus = subTaskMap.get(subTaskId).getStatus();
                if (currentStatus.equals(Status.IN_PROGRESS)) {
                    epic.setStatus(Status.IN_PROGRESS);
                    return Status.IN_PROGRESS;
                } else if (currentStatus.equals(Status.DONE)) {
                    hasDoneStatus = true;
                } else if (currentStatus.equals(Status.NEW)) {
                    hasNewStatus = true;
                }
            }
            if (hasDoneStatus && !hasNewStatus) {
                return Status.DONE;
            } else if (!hasDoneStatus && hasNewStatus) {
                return Status.NEW;
            } else {
                return Status.IN_PROGRESS;
            }
        }
    }
}
