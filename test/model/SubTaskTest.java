package model;

import enumirations.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class SubTaskTest {

    TaskManager taskManager;
    SubTask subTaskFromTaskManager;
    SubTask subTaskFromTaskManagerForCompare;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();

        Epic epic = taskManager.createEpic(new Epic("Эпик", "Подготовка к защите"));

        SubTask createdSubTask = taskManager.createSubTask(new SubTask("Задача_1", "Написать план",
                Status.DONE, epic.getId()));
        SubTask subTaskForCompare = taskManager.createSubTask(new SubTask("Задача_1", "Написать план",
                Status.DONE, epic.getId()));

        subTaskFromTaskManager = taskManager.getSubTaskById(createdSubTask.getId());
        subTaskFromTaskManagerForCompare = taskManager.getSubTaskById(subTaskForCompare.getId());
    }

    @Test
    void shouldBeNotEquals() {
        assertNotEquals(subTaskFromTaskManager, subTaskFromTaskManagerForCompare);
    }

    @Test
    void shouldBeEquals() {
        subTaskFromTaskManager.setId(subTaskFromTaskManagerForCompare.getId());

        assertEquals(subTaskFromTaskManager, subTaskFromTaskManagerForCompare);
    }
}