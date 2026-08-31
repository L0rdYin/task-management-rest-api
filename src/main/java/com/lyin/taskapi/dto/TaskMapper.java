package com.lyin.taskapi.dto;

import com.lyin.taskapi.entity.Task;

public class TaskMapper {
    public static Task toEntity(TaskRequest request){
        Task task = new Task();

        task.setTitle(request.getTitle());
        task.setDescription(request.getDescription());
        task.setCompleted(request.isCompleted());

        return task;
    }

    public static TaskResponse toResponse(Task task){
        TaskResponse response = new TaskResponse();

        response.setId(task.getId());
        response.setTitle(task.getTitle());
        response.setDescription(task.getDescription());
        response.setCompleted(task.isCompleted());
        response.setCreatedAt(task.getCreatedAt());

        return response;
    }
    
}
