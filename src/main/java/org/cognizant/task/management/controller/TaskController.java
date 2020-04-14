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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "Personal information editor")
@RequestMapping("tasks")
@Controller
public class TaskController {
    @Autowired
    TaskService taskService;

    @ApiOperation(value = "Get all tasks", response = String.class)
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Task>> getTasks() {
        return new ResponseEntity<>(taskService.getAllTasks(), HttpStatus.OK);
    }

    @ApiOperation(value = "Get a specific task", response = String.class)
    @GetMapping(value = "task", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity getTask(@ApiParam(value = "Task's id", required = true)
                                  @RequestParam Long id) {
        try {
            return new ResponseEntity<>(taskService.getTaskById(id), HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Update a specific task", response = String.class)
    @PutMapping(produces = MediaType.ALL_VALUE)
    public ResponseEntity<String> updateTask(@ApiParam(value = "Json representation of the task class - " +
            "id is required, other fields are optional", required = true) @RequestBody TaskUpdateDto taskUpdateDto) {
        try {
            taskService.updateTask(taskUpdateDto);
            return new ResponseEntity<>("The task has been updated!", HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @ApiOperation(value = "Add a task", response = String.class)
    @PostMapping(produces = MediaType.ALL_VALUE)
    public ResponseEntity<String> addTask(@ApiParam(value = "Json representation of the task class, " +
            "id is not necessary to provide", required = true) @RequestBody TaskCreateDto taskCreateDto) {
        String response = "The task has been added!";
        taskService.saveTask(taskCreateDto);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @ApiOperation(value = "Delete an existing task", response = String.class)
    @DeleteMapping(produces = MediaType.ALL_VALUE)
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
