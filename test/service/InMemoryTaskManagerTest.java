package service;

import org.junit.jupiter.api.BeforeEach;

class InMemoryTaskManagerTest extends AbstractTaskManagerTest<InMemoryTaskManager> {

    @Override
    @BeforeEach
    void beforeEach() {
        taskManager = new InMemoryTaskManager();
    }
}