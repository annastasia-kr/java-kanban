import model.Epic;
import model.SubTask;
import model.Task;
import service.Managers;
import service.TaskManager;

public class Main {

    private static void printAllTasks(TaskManager manager) {
        System.out.println("Задачи:");
        for (Task task : manager.getTaskList()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Epic epic : manager.getEpicList()) {
            System.out.println(epic);

            for (SubTask task : manager.getEpicSubTasks(epic)) {
                System.out.println("--> " + task);
            }
        }
        System.out.println("Подзадачи:");
        for (Task subtask : manager.getSubTaskList()) {
            System.out.println(subtask);
        }

        System.out.println("История:");
        for (Task task : manager.getHistory()) {
            System.out.println(task);
        }
    }

    public static void main(String[] args) {
        TaskManager taskManager = Managers.getDefault();
    }
}
