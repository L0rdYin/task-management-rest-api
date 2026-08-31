package com.lyin.taskapi.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lyin.taskapi.dto.TaskMapper;
import com.lyin.taskapi.dto.TaskRequest;
import com.lyin.taskapi.dto.TaskResponse;


import com.lyin.taskapi.entity.Task;
import com.lyin.taskapi.exception.TaskNotFoundException;
import com.lyin.taskapi.repository.TaskRepository;
import com.lyin.taskapi.service.TaskService;
import org.springframework.stereotype.Service;



@Service
public class TaskServiceImpl implements TaskService{
    private final TaskRepository taskRepository;

    //Constructor Injection
    public TaskServiceImpl(TaskRepository taskRepository){
        this.taskRepository = taskRepository;
    }

    @Override
    public TaskResponse createTask(TaskRequest request){
        Task task = TaskMapper.toEntity(request);

        Task savedTask = taskRepository.save(task);

        return TaskMapper.toResponse(savedTask);
    }

    @Override
    public Page<TaskResponse> getAllTasks(Pageable pageable){
        Page<Task> tasks = taskRepository.findAll(pageable);
        return tasks.map(TaskMapper::toResponse);
    }

    @Override
    public Page<TaskResponse> searchTasks(String keyword, Pageable pageable){
        Page<Task> tasks = taskRepository.findByTitleContainingIgnoreCase(keyword, pageable);
        return tasks.map(TaskMapper::toResponse);
    }

    @Override
    public TaskResponse getTaskById(Long id){
        Task task = taskRepository.findById(id)
        .orElseThrow(() -> new TaskNotFoundException("Task not found"));
        
        return TaskMapper.toResponse(task);   
    }
    @Override
    public TaskResponse updateTask(Long id, TaskRequest updatedTask){
        Task existingTask = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("Task not found."));

        existingTask.setTitle(updatedTask.getTitle());
        existingTask.setDescription(updatedTask.getDescription());
        existingTask.setCompleted(updatedTask.isCompleted());

        Task savedTask = taskRepository.save(existingTask);

        return TaskMapper.toResponse(savedTask);
    }

    @Override
    public void deleteTask(Long id){
        Task task = taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("Task not found."));
        taskRepository.delete(task);
    }

    @Override
    public Page<TaskResponse> getTasksByCompleted(boolean completed, Pageable pageable){
        Page<Task> tasks = taskRepository.findByCompleted(completed, pageable);
        return tasks.map(TaskMapper::toResponse);
    }

}
