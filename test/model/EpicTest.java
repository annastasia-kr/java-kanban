package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.Managers;
import service.TaskManager;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    TaskManager taskManager;
    Epic epicFromTaskManager;
    Epic epicFromTaskManagerForCompare;

    @BeforeEach
    void beforeEach() {
        taskManager = Managers.getDefault();

        Epic createdEpic = taskManager.createEpic(new Epic("Эпик", "Подготовка к защите"));
        Epic epicForCompare = taskManager.createEpic(new Epic("Эпик", "Подготовка к защите"));

        epicFromTaskManager = taskManager.getEpicById(createdEpic.getId());
        epicFromTaskManagerForCompare = taskManager.getEpicById(epicForCompare.getId());
    }

    @Test
    void shouldBeNotEquals() {
        assertNotEquals(epicFromTaskManager, epicFromTaskManagerForCompare);
    }

    @Test
    void shouldBeEquals() {
        epicFromTaskManager.setId(epicFromTaskManagerForCompare.getId());

        assertEquals(epicFromTaskManager, epicFromTaskManagerForCompare);
    }

}