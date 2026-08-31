package com.lyin.taskapi.entity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "tasks")
public class Task {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title cannot exceed 100 characters")
    @Column(nullable = false)
    private String title;
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;

    //Constructor
    public Task(){
        this.createdAt = LocalDateTime.now();
    }

    //Settters
    public void setTitle(String title){
        this.title = title;
    }
    public void setDescription(String description){
        this.description = description;
    }
    public void setCompleted(boolean completed){
        this.completed = completed;
    }
    //Getters
    public Long getId(){
        return id;
    }
    public String getTitle(){
        return title;
    }
    public String getDescription(){
        return description;
    }
    public boolean isCompleted(){
        return completed;
    }
    public LocalDateTime getCreatedAt(){
        return createdAt;
    }


    
}
