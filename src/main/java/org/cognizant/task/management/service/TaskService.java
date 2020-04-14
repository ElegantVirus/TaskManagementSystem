package org.cognizant.task.management.service;

import org.cognizant.task.management.entity.Task;
import org.cognizant.task.management.entity.TaskCreateDto;
import org.cognizant.task.management.entity.TaskUpdateDto;
import org.cognizant.task.management.repository.TaskRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class TaskService {
    private static final Logger LOGGER = Logger.getLogger(TaskService.class.getName());
    final ModelMapper modelMapper = new ModelMapper();

    @Resource
    private TaskRepository taskRepository;

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(Long id) {
        Task task = taskRepository.getFirstById(id);
        if (task == null) {
            LOGGER.log(Level.WARNING, "An attempt to get a task with id " +
                    id + " that does not exist has been made");
            throw new IllegalArgumentException("The task with such id does not exist!");
        }
        return task;
    }

    public void deleteTask(Long id) {
        Task task = getTaskById(id);
        if (task == null) {
            LOGGER.log(Level.WARNING, "An attempt to delete a task with id " +
                    id + " that does not exist has been made");
            throw new IllegalArgumentException("The task with such id does not exist!");
        }
        taskRepository.delete(task);
        LOGGER.log(Level.INFO, "A task with id " + id + " has been deleted.");
    }

    public void saveTask(TaskCreateDto taskCreateDto) {
        taskRepository.save(modelMapper.map(taskCreateDto, Task.class));
        LOGGER.log(Level.INFO, "A task has been added.");
    }

    public void updateTask(TaskUpdateDto taskUpdateDto) {
        Long id = taskUpdateDto.getId();
        Task task = getTaskById(id);

        if (task == null) {
            LOGGER.log(Level.WARNING, "An attempt to edit a task with id " +
                    id + " that does not exist has been made");
            throw new IllegalArgumentException("The task with such id does not exist!");
        }

        if (taskUpdateDto.getFinished() != null && taskUpdateDto.getFinished()) {
            if (!canBeUpdated(id)) {
                LOGGER.log(Level.WARNING, "An attempt to change the status of a faulty " +
                        "task " + id + " has been made on " + LocalDateTime.now());
                throw new IllegalArgumentException("The task could not be finished as " +
                        "its sub-tasks are not finished!");

            }
        }

        task.merge(modelMapper.map(taskUpdateDto, Task.class));
        taskRepository.save(task);
        LOGGER.log(Level.INFO, "A task with id " + id + " has been updated.");

    }

    private boolean canBeUpdated(Long id) {
        long openTaskCount = taskRepository.getAllByParentId(id)
                .stream()
                .filter(task -> !task.getFinished())
                .count();

        return openTaskCount == 0;
    }
}
