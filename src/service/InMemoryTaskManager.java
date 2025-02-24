package service;

import exceptions.ManagerTasksTimeIntersectionException;
import model.Epic;
import enumirations.Status;
import model.SubTask;
import model.Task;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.time.ZoneId;

public class InMemoryTaskManager implements TaskManager {
    public static final ZoneId ZONE_ID = ZoneId.of("Europe/Moscow");

    protected Map<Integer, Task> taskMap;
    protected Map<Integer, Epic> epicMap;
    protected Map<Integer, SubTask> subTaskMap;
    protected Integer idCounter;
    private final HistoryManager historyManager;
    private final Set<Task> tasksByPriority;
    private final Set<Long> setOfIntersection;

    public InMemoryTaskManager() {
        taskMap = new HashMap<>();
        epicMap = new HashMap<>();
        subTaskMap = new HashMap<>();
        idCounter = 1;
        historyManager = Managers.getDefaultHistory();
        tasksByPriority = new TreeSet<>(Comparator.comparing(Task::getStartTime));
        setOfIntersection = new HashSet<>();
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
        deleteFromTasksByPriority(taskMap.values());
        historyManager.removeAll(taskMap.keySet());
        taskMap.clear();
    }

    @Override
    public void clearEpicMap() {
        deleteFromTasksByPriority(subTaskMap.values());
        historyManager.removeAll(epicMap.keySet());
        historyManager.removeAll(subTaskMap.keySet());
        epicMap.clear();
        subTaskMap.clear();
    }

    @Override
    public void clearSubTaskMap() {
        deleteFromTasksByPriority(subTaskMap.values());
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
        deleteFromTasksByPriority(Collections.singleton(getTaskById(id)));
        historyManager.remove(id);
        return taskMap.remove(id);
    }

    @Override
    public Epic deleteEpicById(int id) {
        if (epicMap.containsKey(id)) {
            List<Integer> subTasksId = epicMap.get(id).getSubTasksId();
            for (Integer subTaskId : subTasksId) {
                deleteFromTasksByPriority(Collections.singleton(getSubTaskById(subTaskId)));
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
            epic.setStartTime(calculateEpicStartTime(epic).orElse(null));
            epic.setDuration(calculateEpicDuration(epic));
            epic.setEndTime(calculateEpicEndTime(epic).orElse(null));
            deleteFromTasksByPriority(Collections.singleton(getSubTaskById(id)));
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
        if (task.getStartTime() != null && !task.getDuration().isZero()) {
            try {
                addToTasksByPriority(task);
            } catch (ManagerTasksTimeIntersectionException e) {
                throw new RuntimeException(e);
            }
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
        if (subTask.getStartTime() != null && !subTask.getDuration().isZero()) {
            try {
                addToTasksByPriority(subTask);
            } catch (ManagerTasksTimeIntersectionException e) {
                throw new RuntimeException(e);
            }
        }
        subTask.setId(idCounter);
        if (epicMap.containsKey(subTask.getEpicId())) {
            SubTask createdSubTask = new SubTask(subTask);
            subTaskMap.put(idCounter, createdSubTask);

            Epic epic =  epicMap.get(subTask.getEpicId());
            epic.addToSubTasksId(idCounter);
            epic.setStatus(calculateEpicStatus(epic));
            epic.setStartTime(calculateEpicStartTime(epic).orElse(null));
            epic.setDuration(calculateEpicDuration(epic));
            epic.setEndTime(calculateEpicEndTime(epic).orElse(null));
            idCounter++;
            return subTask;
        } else {
            return null;
        }
    }

    @Override
    public Task updateTask(Task task) {
        if (taskMap.containsKey(task.getId())) {
            updateTasksByPriority(taskMap.get(task.getId()), task);
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
            updateTasksByPriority(subTaskMap.get(subTask.getId()), subTask);
            SubTask existingSubTask = new SubTask(subTask);
            subTaskMap.put(existingSubTask.getId(), existingSubTask);
            if (epicMap.containsKey(existingSubTask.getEpicId())) {
                Epic epic = epicMap.get(existingSubTask.getEpicId());
                epic.setStatus(calculateEpicStatus(epic));
                epic.setStartTime(calculateEpicStartTime(epic).orElse(null));
                epic.setDuration(calculateEpicDuration(epic));
                epic.setEndTime(calculateEpicEndTime(epic).orElse(null));
            }
        }
        return subTask;
    }

    @Override
    public List<SubTask> getEpicSubTasks(Epic epic) {
        ArrayList<SubTask> epicSubTasks = new ArrayList<>();
        if (epicMap.containsKey(epic.getId())) {
            epic.getSubTasksId()
                    .forEach(id -> epicSubTasks.add(getSubTaskById(id)));
        }
        return epicSubTasks;
    }

    @Override
    public List<Task> getHistory() {
        return new ArrayList<>(historyManager.getHistory());
    }

    @Override
    public int getIdCounter() {
        return idCounter;
    }

    protected Status calculateEpicStatus(Epic epic) {
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

    protected Optional<LocalDateTime> calculateEpicStartTime(Epic epic) {
        List<Integer> subTasksId = epic.getSubTasksId();
        if (subTasksId.isEmpty()) {
            return Optional.empty();
        } else {
            return subTasksId.stream()
                    .map(subTaskId -> subTaskMap.get(subTaskId).getStartTime())
                    .filter(Objects::nonNull)
                    .min(LocalDateTime::compareTo);
        }
    }

    protected Optional<LocalDateTime> calculateEpicEndTime(Epic epic) {
        List<Integer> subTasksId = epic.getSubTasksId();
        if (subTasksId.isEmpty()) {
            return Optional.empty();
        } else {
            return subTasksId.stream()
                    .map(subTaskId -> subTaskMap.get(subTaskId).getEndTime())
                    .filter(Objects::nonNull)
                    .max(LocalDateTime::compareTo);
        }
    }

    protected Duration calculateEpicDuration(Epic epic) {
        List<Integer> subTasksId = epic.getSubTasksId();
        if (subTasksId.isEmpty()) {
            return Duration.ZERO;
        } else {
           return subTasksId.stream()
                   .map(subTaskId -> subTaskMap.get(subTaskId).getDuration())
                   .reduce(Duration.ZERO, Duration::plus);
        }
    }

    @Override
    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(tasksByPriority);
    }

    private boolean hasIntersection(Task task) {
        LocalDateTime startTime = task.getStartTime();
        LocalDateTime endTime = task.getEndTime();
        long ceilEpochMinutesToStartTime = (long)Math.ceil(startTime.atZone(ZONE_ID).toEpochSecond() / 60.0) / 15;
        long ceilEpochMinutesToEndTime =  (long)Math.ceil(endTime.atZone(ZONE_ID).toEpochSecond() / 60.0) / 15;
        for (long i = ceilEpochMinutesToStartTime; i <= ceilEpochMinutesToEndTime; i++) {
            if (setOfIntersection.contains(i)) {
                return true;
            }
        }
        return false;
    }

    private void deleteFromTasksByPriority(Collection<? extends Task> tasks) {
        for (Task task : tasks) {
            if (tasksByPriority.contains(task)) {
                tasksByPriority.remove(task);
                freeIntersectionsSlots(task);
            }
        }
    }

    private void freeIntersectionsSlots(Task task) {
        long slotForStartTime = (long)Math.ceil(task.getStartTime().atZone(ZONE_ID).toEpochSecond() / 60.0) / 15;
        long slotForEndTime =  (long)Math.ceil(task.getEndTime().atZone(ZONE_ID).toEpochSecond() / 60.0) / 15;
        for (long i = slotForStartTime; i <= slotForEndTime; i++) {
            setOfIntersection.remove(i);
        }
    }


    private void addToTasksByPriority(Task task) throws ManagerTasksTimeIntersectionException {
        if (hasIntersection(task)) {
            throw new ManagerTasksTimeIntersectionException("На данное время уже запланирована задача");
        }
        tasksByPriority.add(task);
        takeIntersectionsSlots(task);
    }


    private void takeIntersectionsSlots(Task task) {
        long slotForStartTime = (long)Math.ceil(task.getStartTime().atZone(ZONE_ID).toEpochSecond() / 60.0) / 15;
        long slotForEndTime =  (long)Math.ceil(task.getEndTime().atZone(ZONE_ID).toEpochSecond() / 60.0) / 15;
        for (long i = slotForStartTime; i <= slotForEndTime; i++) {
            setOfIntersection.add(i);
        }
    }

    private void updateTasksByPriority(Task task, Task updatedTask) {
        if (task.getStartTime() != null && !task.getDuration().isZero()) {
            deleteFromTasksByPriority(Collections.singleton(task));
        }
        if (updatedTask.getStartTime() != null && !updatedTask.getDuration().isZero()) {
            try {
                addToTasksByPriority(updatedTask);
            } catch (ManagerTasksTimeIntersectionException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
