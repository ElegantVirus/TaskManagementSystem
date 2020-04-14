package org.cognizant.task.management.service;

import org.cognizant.task.management.entity.Task;
import org.cognizant.task.management.entity.TaskCreateDto;
import org.cognizant.task.management.entity.TaskUpdateDto;
import org.cognizant.task.management.repository.TaskRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@RunWith(SpringRunner.class)
public class TaskServiceIntegrationTest {

    @TestConfiguration
    static class TaskServiceTestContextConfiguration {
        @Bean
        public TaskService taskService() {
            return new TaskService();
        }
    }

    @Autowired
    private TaskService taskService;

    @MockBean
    private TaskRepository taskRepository;
    List<Task> tasks;
    ModelMapper modelMapper;
    TaskCreateDto taskCreateDto;
    TaskUpdateDto taskUpdateDto;
    Task simpleTask;

    @Before
    public void setup() {
        modelMapper = new ModelMapper();
        tasks = populateTasks();
        taskCreateDto = createNewCreateTask();
        simpleTask = modelMapper.map(taskCreateDto, Task.class);
        simpleTask.setId(0L);

        Mockito.when(taskRepository.getFirstById(0L)).thenReturn(simpleTask);
        Mockito.when(taskRepository.getAllByParent(any())).thenReturn(tasks);
        Mockito.when(taskRepository.findAll()).thenReturn(tasks);
    }

    @Test
    public void whenFindById_thenReturnSimpleTask() {
        Task task = taskService.getTaskById(0L);
        assert task.equals(simpleTask);
    }

    @Test
    public void whenFindAll_thenReturnTasks() {
        List<Task> tasks = taskService.getAllTasks();
        assert tasks.size() == 3;
    }

    @Test
    public void whenSaveTask_TaskIsSaved() {
        taskService.saveTask(taskCreateDto);

        doAnswer((i) -> {
            assertEquals(simpleTask, i.getArgument(0));
            return null;
        }).when(taskRepository).save(simpleTask);
    }

    @Test
    public void whenUpdateTask_TaskFails() {
        taskUpdateDto = createNewUpdateTask(true);
        try {
            taskService.updateTask(taskUpdateDto);
        } catch (Exception e) {
            assert e.getMessage().equals("The task could not be finished as its sub-tasks are not finished!");
        }
    }

    @Test
    public void whenUpdateTask_TaskSucceeds() {
        taskUpdateDto = createNewUpdateTask(false);
        taskService.updateTask(taskUpdateDto);

        doAnswer((i) -> {
            assertEquals(tasks, i.getArgument(0));
            return null;
        }).when(taskRepository).getAllByParent(0L);
    }

    @Test
    public void whenUpdateBadId_Fails() {
        taskUpdateDto = createNewUpdateTask(false);
        taskUpdateDto.setId(10L);
        try {
            taskService.updateTask(taskUpdateDto);
        } catch (Exception e) {
            assert e.getMessage().equals("The task with such id does not exist!");
        }
    }

    @Test
    public void whenDeleteHasChildren_Fails() {
        try {
            taskService.deleteTask(0L);
        } catch (Exception e) {
            assert e.getMessage().equals("The task has children, it cannot be deleted");
        }
    }

    private List<Task> populateTasks() {
        List<Task> tasks = new ArrayList<>();
        tasks.add(0, createNewTask("name1", null, false));
        tasks.add(0, createNewTask("name2", 0L, false));
        tasks.add(0, createNewTask("name3", 0L, true));
        return tasks;
    }

    private Task createNewTask(String name, Long parent, Boolean finished) {
        return new Task(0L, name, 1, "test", "human", parent, finished);
    }

    private TaskUpdateDto createNewUpdateTask(Boolean finished) {
        TaskUpdateDto taskUpdateDto = new TaskUpdateDto();
        taskUpdateDto.setId(0L);
        taskUpdateDto.setFinished(finished);
        return taskUpdateDto;
    }

    private TaskCreateDto createNewCreateTask() {
        TaskCreateDto task = new TaskCreateDto();
        task.setName("name1");
        task.setFinished(false);
        task.setAssignee("person");
        task.setGroup("a");
        task.setHoursSpent(2);
        task.setParent(null);
        return task;
    }
}