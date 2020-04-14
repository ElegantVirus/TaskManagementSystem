package org.cognizant.task.management.service;

import lombok.extern.slf4j.Slf4j;
import org.cognizant.task.management.entity.Task;
import org.cognizant.task.management.entity.TaskCreateDto;
import org.cognizant.task.management.entity.TaskUpdateDto;
import org.cognizant.task.management.repository.TaskRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class TaskService {
    final ModelMapper modelMapper = new ModelMapper();

    @Resource
    private TaskRepository taskRepository;

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task getTaskById(Long id) {
        Task task = taskRepository.getFirstById(id);
        checkCondition(
                id,
                task == null,
                "An attempt to get a task with id ",
                " that does not exist has been made",
                "The task with such id does not exist!");
        return task;
    }

    public void deleteTask(Long id) {
        Task task = getTaskById(id);
        checkCondition(
                id,
                task == null,
                "An attempt to delete a task with id ",
                " that does not exist has been made",
                "The task with such id does not exist!"
        );
        List<Task> tasks = taskRepository.getAllByParent(id);
        checkCondition(
                id,
                tasks.size() != 0,
                "An attempt to delete a task with id ",
                " has been made. It is a parent, you monster!",
                "The task has children, it cannot be deleted"
        );
        taskRepository.delete(task);
        log.info("A task with id " + id + " has been deleted.");
    }

    private void checkCondition(
            Long id,
            boolean condition,
            String logString1,
            String logString2,
            String exceptionMessage
    ) {
        if (condition) {
            log.warn(logString1 +
                    id + logString2);
            throw new IllegalArgumentException(exceptionMessage);
        }
    }

    public Task saveTask(TaskCreateDto taskCreateDto) {
        Task task = taskRepository.save(modelMapper.map(taskCreateDto, Task.class));
        log.info("A task has been added.");
        return task;
    }

    public Task updateTask(TaskUpdateDto taskUpdateDto) {
        Long id = taskUpdateDto.getId();
        Task task = getTaskById(id);

        checkCondition(
                id,
                task == null,
                "An attempt to edit a task with id ",
                " that does not exist has been made",
                "The task with such id does not exist!"
        );

        checkChildrenForClosing(taskUpdateDto, id);

        task.merge(modelMapper.map(taskUpdateDto, Task.class));
        Task savedTask = taskRepository.save(task);
        log.info("A task with id " + id + " has been updated.");
        return savedTask;
    }

    private void checkChildrenForClosing(TaskUpdateDto taskUpdateDto, Long id) {
        if (taskUpdateDto.getFinished() != null) {
            if (taskUpdateDto.getFinished()) {
                if (!canBeClosed(id)) {
                    log.warn("An attempt to change the status of a faulty " +
                            "task " + id + " has been made on " + LocalDateTime.now());
                    throw new IllegalArgumentException("The task could not be finished as " +
                            "its sub-tasks are not finished!");

                }
            } else {
                if (hasClosedParent(taskRepository.getFirstById(id).getParent())) {
                    log.warn("An attempt to change the status of a faulty " +
                            "task " + id + " has been made on " + LocalDateTime.now());
                    throw new IllegalArgumentException("The task could not be reopened as " +
                            "its parent task is finished!");

                }
            }
        }
    }

    private boolean hasClosedParent(Long parentId) {
        if (parentId == null) {
            return false;
        } else {
            return taskRepository.getFirstById(parentId).getFinished();
        }
    }

    private boolean canBeClosed(Long id) {
        long openTaskCount = taskRepository.getAllByParent(id)
                .stream()
                .filter(task -> !task.getFinished())
                .count();

        return openTaskCount == 0;
    }
}
