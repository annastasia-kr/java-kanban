import enumirations.Status;
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

        Task task = new Task("Отправить письмо", "Направить ответ по электронной почте", Status.IN_PROGRESS);
        Task task1 = new Task("Провести ВКС", "Произвести демонстрацию нового функционала", Status.NEW);
        taskManager.createTask(task);
        taskManager.createTask(task1);

        Epic epic = new Epic("Заключить договор", "Заключить договор с ООО Софт");
        Epic epic1 = new Epic("Обновить должностные инструкции", "Обновить ДИ отдела сопровождения и разработки");
        taskManager.createEpic(epic);
        taskManager.createEpic(epic1);

        SubTask subTask = new SubTask("Запрос КП", "Получить 3 КП", Status.IN_PROGRESS, epic.getId());
        SubTask subTask1 = new SubTask("ТЗ", "Проверить ТЗ", Status.NEW, epic.getId());
        SubTask subTask2 = new SubTask("Договор", "Направить договор ЮО", Status.NEW, epic.getId());
        taskManager.createSubTask(subTask);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);

        taskManager.getSubTaskById(subTask1.getId());
        System.out.println(taskManager.getHistory());
        taskManager.getTaskById(task.getId());
        System.out.println(taskManager.getHistory());
        taskManager.getSubTaskById(subTask2.getId());
        System.out.println(taskManager.getHistory());
        taskManager.getTaskById(task1.getId());
        System.out.println(taskManager.getHistory());
        taskManager.getSubTaskById(subTask1.getId());
        System.out.println(taskManager.getHistory());
        taskManager.getEpicById(epic.getId());
        System.out.println(taskManager.getHistory());
        taskManager.getTaskById(task.getId());
        System.out.println(taskManager.getHistory());
        taskManager.getSubTaskById(subTask.getId());
        System.out.println(taskManager.getHistory());
        taskManager.getEpicById(epic.getId());
        System.out.println(taskManager.getHistory());
        taskManager.getEpicById(epic1.getId());
        System.out.println(taskManager.getHistory());
        taskManager.getSubTaskById(subTask.getId());
        System.out.println(taskManager.getHistory());
        taskManager.getEpicById(epic1.getId());
        System.out.println(taskManager.getHistory());
        taskManager.getEpicById(epic1.getId());
        System.out.println(taskManager.getHistory());

        taskManager.deleteTaskById(task.getId());
        System.out.println(taskManager.getHistory());

        taskManager.deleteEpicById(epic.getId());
        System.out.println(taskManager.getHistory());
    }
}