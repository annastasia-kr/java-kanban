package manager;

import tasks.Epic;
import tasks.Status;
import tasks.SubTask;
import tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    HashMap<Integer, Task> taskMap;
    HashMap<Integer, Epic> epicMap;
    HashMap<Integer, SubTask> subTaskMap;
    Integer idCounter;

    public TaskManager() {
        taskMap = new HashMap<>();
        epicMap = new HashMap<>();
        subTaskMap = new HashMap<>();
        idCounter = 0;
    }

    public ArrayList<Task> getTaskList() {
        return new ArrayList<>(taskMap.values());
    }

    public ArrayList<Epic> getEpicList() {
        return new ArrayList<>(epicMap.values());
    }

    public ArrayList<SubTask> getSubTaskList() {
        return new ArrayList<>(subTaskMap.values());
    }

    public void clearTaskMap() {
        taskMap.clear();
    }

    public void clearEpicMap() {
        epicMap.clear();
        subTaskMap.clear();
    }

    public void clearSubTaskMap() {
        subTaskMap.clear();
        for (Epic epic : epicMap.values()) {
            epic.getSubTasksId().clear();
        }
    }

    public Task getTaskById (int id) {
        if (!taskMap.containsKey(id)) {
            return null;
        }
        Task task = new Task(taskMap.get(id));
        return task;
    }

    public Epic getEpicById(int id) {
        if (!epicMap.containsKey(id)) {
            return null;
        }
        Epic epic = new Epic(epicMap.get(id));
        return epic;
    }

    public SubTask getSubTaskById(int id) {
        if (!subTaskMap.containsKey(id)) {
            return null;
        }
        SubTask subTask = new SubTask(subTaskMap.get(id));
        return subTask;
    }

    public Task deleteTaskById(int id) {
        return taskMap.remove(id);
    }

    public Epic deleteEpicById(int id) {
        if (epicMap.containsKey(id)) {
            ArrayList<Integer> subTasksId = epicMap.get(id).getSubTasksId();
            for (Integer subTaskId : subTasksId) {
                subTaskMap.remove(subTaskId);
            }
            return epicMap.remove(id);
        }
        return null;
    }

    public SubTask deleteSubTaskById(int id) {
        if (subTaskMap.containsKey(id)) {
            int epicId = subTaskMap.get(id).getEpicId();
            Epic epic = epicMap.get(epicId);
            epic.removeFromSubTasksId(id);
            epic.setStatus(calculateEpicStatus(epic));
            return subTaskMap.remove(id);
        }
        return null;
    }

    public Task createTask(Task task) {
        if (task.equals(null)) {
            return null;
        }
        task.setId(idCounter);
        Task createdTask = new Task(task);
        taskMap.put(idCounter, createdTask);
        idCounter++;
        return task;
    }

    public Epic createEpic(Epic epic) {
        if (epic.equals(null)) {
            return null;
        }
        epic.setId(idCounter);
        epic.setStatus(calculateEpicStatus(epic));
        Epic createdEpic = new Epic(epic);
        epicMap.put(idCounter, createdEpic);
        idCounter++;
        return epic;
    }

    public Status calculateEpicStatus(Epic epic) {
        ArrayList<Integer> subTasksId = epic.getSubTasksId();
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

    public SubTask createSubTask(SubTask subTask) {
        if (subTask.equals(null)) {
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

    public Task updateTask(Task task) {
        if (taskMap.containsKey(task.getId())) {
            Task existingTask = taskMap.get(task.getId());
            existingTask.setName(task.getName());
            existingTask.setDescription(task.getDescription());
            existingTask.setStatus(task.getStatus());
        }
        return task;
    }

    public Epic updateEpic(Epic epic) {
        if (epicMap.containsKey(epic.getId())) {
            Epic existingEpic = epicMap.get(epic.getId());
            existingEpic.setName(epic.getName());
            existingEpic.setDescription(epic.getDescription());
        }
        return epic;
    }

    public SubTask updateSubTask(SubTask subTask) {
        if (subTaskMap.containsKey(subTask.getId())) {
            SubTask existingSubTask = subTaskMap.get(subTask.getId());
            existingSubTask.setName(subTask.getName());
            existingSubTask.setDescription(subTask.getDescription());
            existingSubTask.setStatus(subTask.getStatus());
            if (epicMap.containsKey(existingSubTask.getEpicId())) {
                Epic epic = epicMap.get(existingSubTask.getEpicId());
                epic.setStatus(calculateEpicStatus(epic));
            }
        }
        return subTask;
    }
}
