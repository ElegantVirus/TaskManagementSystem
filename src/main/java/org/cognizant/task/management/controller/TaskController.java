package org.cognizant.task.management.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.cognizant.task.management.entity.Task;
import org.cognizant.task.management.entity.TaskCreateDto;
import org.cognizant.task.management.entity.TaskUpdateDto;
import org.cognizant.task.management.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "Tasks editor")
@RequestMapping("tasks")
@Controller
public class TaskController {
    @Autowired
    TaskService taskService;

    @ApiOperation(value = "Get all tasks", response = String.class)
    @GetMapping
    public ResponseEntity<List<Task>> getTasks() {
        return new ResponseEntity<>(taskService.getAllTasks(), HttpStatus.OK);
    }

    @ApiOperation(value = "Get a specific task", response = String.class)
    @GetMapping(value = "task")
    public ResponseEntity<?> getTask(@ApiParam(value = "Task's id", required = true)
                                     @RequestParam Long id) {
        try {
            return new ResponseEntity<>(taskService.getTaskById(id), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Update a specific task", response = String.class)
    @PutMapping
    public ResponseEntity<?> updateTask(@ApiParam(value = "Json representation of the task class - " +
            "id is required, other fields are optional", required = true) @RequestBody TaskUpdateDto taskUpdateDto) {
        try {
            Task task = taskService.updateTask(taskUpdateDto);
            return new ResponseEntity<>(task, HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Add a task", response = String.class)
    @PostMapping
    public ResponseEntity<?> addTask(
            @ApiParam(value = "Json representation of the task class without id", required = true)
            @RequestBody TaskCreateDto taskCreateDto
    ) {
        Task task = taskService.saveTask(taskCreateDto);
        return new ResponseEntity<>(task, HttpStatus.OK);
    }

    @ApiOperation(value = "Delete an existing task", response = String.class)
    @DeleteMapping
    public ResponseEntity<String> deleteTask(@ApiParam(value = "Task's id", required = true)
                                             @RequestParam Long id) {
        try {
            taskService.deleteTask(id);
            return new ResponseEntity<>("The task has been deleted successfully!", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
