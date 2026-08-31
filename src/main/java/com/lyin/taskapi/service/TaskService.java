package com.lyin.taskapi.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lyin.taskapi.dto.TaskRequest;
import com.lyin.taskapi.dto.TaskResponse;

//import com.lyin.taskapi.entity.Task;

public interface TaskService {

    TaskResponse createTask(TaskRequest request);
    Page<TaskResponse> getAllTasks(Pageable pageable);
    Page<TaskResponse> getTasksByCompleted(boolean completed, Pageable pageable);
    Page<TaskResponse> searchTasks(String keyword, Pageable pageable);
    TaskResponse getTaskById(Long id);
    TaskResponse updateTask(Long id, TaskRequest updatedTask);
    void deleteTask(Long id);

}
