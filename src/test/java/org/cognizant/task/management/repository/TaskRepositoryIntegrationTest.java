package org.cognizant.task.management.repository;

import org.cognizant.task.management.entity.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
public class TaskRepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private TaskRepository taskRepository;

    @Test
    public void whenFindById_thenReturnTask() {
        Task existingTask = createNewTask("name", null);
        entityManager.persist(existingTask);
        entityManager.flush();

        Task found = taskRepository.getFirstById(existingTask.getId());
        assert (found.getName().equals("name"));
    }

    @Test
    public void whenFindByparent_thenReturnMany() {
        Task task1 = createNewTask("name", null);
        Task task2 = createNewTask("name1", 0L);
        Task task3 = createNewTask("name", 0L);
        entityManager.persist(task1);
        entityManager.persist(task2);
        entityManager.persist(task3);
        entityManager.flush();

        List<Task> found = taskRepository.getAllByParent(0L);
        assert found.size() == 2;
        assert found.contains(task2);
        assert found.contains(task3);
    }

    private Task createNewTask(String name, Long parent) {
        Task task = new Task();
        task.setName(name);
        task.setFinished(true);
        task.setAssignee("person");
        task.setGroup("a");
        task.setHoursSpent(2);
        task.setParent(parent);

        return task;
    }
}