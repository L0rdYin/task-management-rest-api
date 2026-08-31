package com.lyin.taskapi.service.impl;

import java.util.Optional;
import java.util.List;

import com.lyin.taskapi.dto.TaskResponse;
import com.lyin.taskapi.dto.TaskRequest;
import com.lyin.taskapi.entity.Task;
import com.lyin.taskapi.repository.TaskRepository;
import com.lyin.taskapi.exception.TaskNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;



@ExtendWith(MockitoExtension.class)
public class TaskServiceImplTest {

    @Mock
    private TaskRepository taskRepository;

    private TaskServiceImpl taskService;

    @BeforeEach
    void setUp(){
        taskService = new TaskServiceImpl(taskRepository);
    }
    
    @Test
    void getTaskById_shouldReturnTaskResponse(){
        //Arrange
        Task task = new Task();

        task.setTitle("Test Task");
        task.setDescription("Testing JUnit");
        task.setCompleted(false);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        //Act
        TaskResponse response = taskService.getTaskById(1L);

        //Assert
        assertEquals("Test Task", response.getTitle());
        assertEquals("Testing JUnit", response.getDescription());
        assertFalse(response.isCompleted());

    }

    @Test
    void getTaskById_shouldThrowExceptionWhenTaskNotFound(){
        //Arrange 
        when (taskRepository.findById(9999L)).thenReturn(Optional.empty());

        //Act + Assert
        assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById((9999L)));
    }

    @Test
    void createTask_shouldReturnTaskResponse(){
        //Arrange
        TaskRequest request = new TaskRequest();

        request.setTitle("New Task");
        request.setDescription("Testing createTask");
        request.setCompleted(false);

        Task savedTask = new Task();
        savedTask.setTitle("New Task");
        savedTask.setDescription("Testing createTask");
        savedTask.setCompleted(false);

        when(taskRepository.save(org.mockito.ArgumentMatchers.any(Task.class))).thenReturn(savedTask);

        //Act
        TaskResponse response = taskService.createTask(request);

        //Assert
        assertEquals("New Task", response.getTitle());
        assertEquals("Testing createTask", response.getDescription());
        assertFalse(response.isCompleted());

        verify(taskRepository).save(org.mockito.ArgumentMatchers.any(Task.class));

    }

    @Test
    void updateTask_shouldReturnUpdatedTaskResponse(){
        //Arrange 
        Task existingTask = new Task();

        existingTask.setTitle("Old Title");
        existingTask.setDescription("Old Description");
        existingTask.setCompleted(false);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(existingTask));

        TaskRequest updatedTask = new TaskRequest();

        updatedTask.setTitle("Updated Title");
        updatedTask.setDescription("Updated Description");
        updatedTask.setCompleted(true);

        when(taskRepository.save(existingTask)).thenReturn(existingTask);

        //Act
        TaskResponse response = taskService.updateTask(1L, updatedTask);

        //Assert
        assertEquals("Updated Title", response.getTitle());
        assertEquals("Updated Description", response.getDescription());
        assertTrue(response.isCompleted());

        verify(taskRepository).save(existingTask);
    }

    @Test
    void updateTask_shouldThrowExceptionWhenTaskNotFound(){
        //Arrange
        when(taskRepository.findById(9999L)).thenReturn(Optional.empty());

        TaskRequest updatedTask = new TaskRequest();

        updatedTask.setTitle("Updated Title");
        updatedTask.setDescription("Updated Description");
        updatedTask.setCompleted(true);

        //Act + Assert
        assertThrows(TaskNotFoundException.class, () -> taskService.updateTask(9999L, updatedTask));

    }

    @Test
    void deleteTask_shouldDeleteTask(){
        //Arrange
        Task task = new Task();

        task.setTitle("Task to Delete");
        task.setDescription("This task should be deleted");
        task.setCompleted(false);

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        //Act
        taskService.deleteTask(1L);

        //Assert
        verify(taskRepository).delete(task);

    }

    @Test
    void deleteTask_shouldThrowExceptionWhenTaskNotFound(){
        //Arrange
        when(taskRepository.findById(9999L)).thenReturn(Optional.empty());
        //Act + Assert
        assertThrows(TaskNotFoundException.class, () -> taskService.deleteTask(9999L));
    }

    @Test
    void getAllTasks_shouldReturnTaskResponse(){
        //Arrange
        Pageable pageable = PageRequest.of(0, 10);
        
        Task task1 = new Task();
        task1.setTitle("Task One");
        task1.setDescription("First task");
        task1.setCompleted(false);

        Task task2 = new Task();
        task2.setTitle("Task Two");
        task2.setDescription("Second task");
        task2.setCompleted(true);

        Page<Task> taskPage = new PageImpl<>(List.of(task1, task2));

        when(taskRepository.findAll(pageable)).thenReturn(taskPage);

        //Act
        Page<TaskResponse> response = taskService.getAllTasks(pageable);

        //Assert
        assertEquals(2, response.getContent().size());

        assertEquals("Task One", response.getContent().get(0).getTitle());
        assertEquals("First task", response.getContent().get(0).getDescription());
        assertFalse(response.getContent().get(0).isCompleted());

        assertEquals("Task Two", response.getContent().get(1).getTitle());
        assertEquals("Second task", response.getContent().get(1).getDescription());
        assertTrue(response.getContent().get(1).isCompleted());

        verify(taskRepository).findAll(pageable);
    }
    
    @Test
    void getTasksByCompleted_ShouldReturnFilteredTasks(){
        //Arrange
        Pageable pageable = PageRequest.of(0,10);

        Task task1 = new Task();
        task1.setTitle("Completed Task One");
        task1.setDescription("First completed task");
        task1.setCompleted(true);

        Task task2 = new Task();
        task2.setTitle("Completed Task Two");
        task2.setDescription("Second completed task");
        task2.setCompleted(true);

        Page<Task> taskPage = new PageImpl<>(List.of(task1, task2));

        when(taskRepository.findByCompleted(true, pageable)).thenReturn(taskPage);

        // Act
        Page<TaskResponse> response = taskService.getTasksByCompleted(true, pageable);

        // Assert
        assertEquals(2, response.getContent().size());
        
        assertEquals("Completed Task One", response.getContent().get(0).getTitle());
        assertEquals("First completed task", response.getContent().get(0).getDescription());
        assertTrue(response.getContent().get(0).isCompleted());

        assertEquals("Completed Task Two", response.getContent().get(1).getTitle());
        assertEquals("Second completed task", response.getContent().get(1).getDescription());
        assertTrue(response.getContent().get(1).isCompleted());

        verify(taskRepository).findByCompleted(true, pageable);
    }

    @Test
    void searchTasks_shouldReturnMatchingTasks(){
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        String keyword = "java";

        Task task1 = new Task();
        task1.setTitle("Java Spring Boot");
        task1.setDescription("Learning Spring Boot with Java");
        task1.setCompleted(false);
        
        Task task2 = new Task();
        task2.setTitle("Java REST API");
        task2.setDescription("Building a REST API with Java");
        task2.setCompleted(true);

        Page<Task> taskPage = new PageImpl<>(List.of(task1, task2));

        when(taskRepository.findByTitleContainingIgnoreCase(keyword, pageable)).thenReturn(taskPage);

        // Act
        Page<TaskResponse> response = taskService.searchTasks(keyword, pageable);

        // Assert
        assertEquals(2, response.getContent().size());
        
        assertEquals("Java Spring Boot", response.getContent().get(0).getTitle());
        assertEquals("Learning Spring Boot with Java", response.getContent().get(0).getDescription());
        assertFalse(response.getContent().get(0).isCompleted());
        
        assertEquals("Java REST API", response.getContent().get(1).getTitle());
        assertEquals("Building a REST API with Java", response.getContent().get(1).getDescription());
        assertTrue(response.getContent().get(1).isCompleted());

        verify(taskRepository).findByTitleContainingIgnoreCase(keyword, pageable);
    }
}
