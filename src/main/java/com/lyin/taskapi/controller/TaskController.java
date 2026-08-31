package com.lyin.taskapi.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

//import com.lyin.taskapi.dto.TaskMapper;
import com.lyin.taskapi.dto.TaskRequest;
import com.lyin.taskapi.dto.TaskResponse;

//import com.lyin.taskapi.entity.Task;
import com.lyin.taskapi.service.TaskService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService){
        this.taskService = taskService;
    }

    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody TaskRequest request){
        TaskResponse response = taskService.createTask(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public Page<TaskResponse> getAllTasks(@RequestParam(required = false) Boolean completed, Pageable pageable){
       if(completed != null){
         return taskService.getTasksByCompleted(completed, pageable);
       }
       return taskService.getAllTasks(pageable);
    }

    @GetMapping("/search")
    public Page<TaskResponse> searchTasks(@RequestParam String keyword, Pageable pageable){
        return taskService.searchTasks(keyword, pageable);
    }

    //@GetMapping("/filter")
    //public Page<Task> getTasksByCompleted(@RequestParam boolean completed, Pageable pageable){
    //    return taskService.getTasksByCompleted(completed, pageable);
    //}

    @GetMapping("/{id}")
    public TaskResponse getTask(@PathVariable Long id){
        return taskService.getTaskById(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id){
        taskService.deleteTask(id);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public TaskResponse updateTask(@PathVariable Long id, @Valid @RequestBody TaskRequest updatedTask){
        return taskService.updateTask(id, updatedTask);
    }

}
