package com.lyin.taskapi.controller;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.lyin.taskapi.dto.TaskRequest;
import com.lyin.taskapi.dto.TaskResponse;
import com.lyin.taskapi.exception.TaskNotFoundException;
import com.lyin.taskapi.service.TaskService;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;

import com.lyin.taskapi.security.JwtService;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {
   
    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private JwtService jwtService;

    @Autowired
    private MockMvc mockMvc;

    /*@BeforeEach
    void setUp(){
        TaskController taskController = new TaskController(taskService);

        mockMvc = MockMvcBuilders
                    .standaloneSetup(taskController)
                    .setControllerAdvice(new GlobalExceptionHandler())
                    .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                    .build();
    }*/

    @Test
    void createTask_shouldReturn201Created() throws Exception {
        //Arrange
        TaskResponse response = new TaskResponse();

        response.setTitle("New Task");
        response.setDescription("Testing controller");
        response.setCompleted(false);

        when(taskService.createTask(org.mockito.ArgumentMatchers.any(TaskRequest.class))).thenReturn(response);

        String requestJson = """
        {
            "title": "New Task",
            "description": "Testing controller",
            "completed": false
        }
        """;

        // Act
        mockMvc.perform(post("/tasks").contentType("application/json").content(requestJson)).andExpect(status().isCreated()).andExpect(jsonPath("$.title").value("New Task")).andExpect(jsonPath("$.description").value("Testing controller")).andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void createTask_shouldReturn400WhenValidationFails() throws Exception {
        // Arrange
        String requestJson = """
        {
            "title": "",
            "description": "This should fail validation",
            "completed": false
        }
        """;

        // Act + Assert
        mockMvc.perform(
        post("/tasks")
            .contentType("application/json")
            .content(requestJson)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void createTask_shouldReturnValidationErrorResponse() throws Exception {
        String requestJson = """
        {
            "title": "",
            "description": "Valid description",
            "completed": false
        }
        """;

        mockMvc.perform(
            post("/tasks")
                .contentType("application/json")
                .content(requestJson)
            )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.message").value("Validation failed"))
        .andExpect(jsonPath("$.errors.title").value("Title is required"))
        .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void getTask_shouldReturn200() throws Exception{
        TaskResponse response = new TaskResponse();

        response.setTitle("Test Task");
        response.setDescription("Testing GET endpoint");
        response.setCompleted(false);

        when(taskService.getTaskById(1L)).thenReturn(response);

        // Act + Assert
        mockMvc.perform(get("/tasks/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Test Task"))
        .andExpect(jsonPath("$.description").value("Testing GET endpoint"))
        .andExpect(jsonPath("$.completed").value(false));
    }

    @Test
    void getTask_shouldReturn404WhenNotFound() throws Exception{
        //Arrange
        when(taskService.getTaskById(9999L)).thenThrow(new TaskNotFoundException("Task not found."));
        //Act + Assert
        mockMvc.perform(get("/tasks/9999")).andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value(404)).andExpect(jsonPath("$.message").value("Task not found.")).andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void getTask_shouldReturn400WhenIdIsInvalid() throws Exception {
        mockMvc.perform(
            get("/tasks/abc")
        )
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.message").value("Invalid value for parameter: id"))
        .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void getAllTasks_shouldReturn200() throws  Exception{
        //Arrange
        TaskResponse task1 = new TaskResponse();
        task1.setTitle("Task One");
        task1.setDescription("First task");
        task1.setCompleted(false);

        TaskResponse task2 = new TaskResponse();
        task2.setTitle("Task Two");
        task2.setDescription("Second task");
        task2.setCompleted(true);

        Page<TaskResponse> taskPage = new PageImpl<>(List.of(task1, task2));
        when(taskService.getAllTasks(any(Pageable.class))).thenReturn(taskPage);

        //Act + Assert
        mockMvc.perform(get("/tasks").param("page", "0").param("size","10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content[0].title").value("Task One"))
        .andExpect(jsonPath("$.content[0].description").value("First task"))
        .andExpect(jsonPath("$.content[0].completed").value(false))
        .andExpect(jsonPath("$.content[1].title").value("Task Two"))
        .andExpect(jsonPath("$.content[1].description").value("Second task"))
        .andExpect(jsonPath("$.content[1].completed").value(true));     
    }

    @Test
    void getAllTasks_shouldFilterByCompleted() throws Exception{
        //Arrange
        TaskResponse completedTask = new TaskResponse();
        completedTask.setTitle("Completed Task");
        completedTask.setDescription("This task is finished");
        completedTask.setCompleted(true);

        TaskResponse incompleteTask = new TaskResponse();
        incompleteTask.setTitle("Incomplete Task");
        incompleteTask.setDescription("This task is not finished");
        incompleteTask.setCompleted(false);

        Page<TaskResponse> taskPage = new PageImpl<>(List.of(completedTask));

        when(taskService.getTasksByCompleted(eq(true), any(Pageable.class))).thenReturn(taskPage);

        // Act + Assert
        mockMvc.perform(
        get("/tasks")
                .param("completed", "true")
                .param("page", "0")
                .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].title").value("Completed Task"))
        .andExpect(jsonPath("$.content[0].description").value("This task is finished"))
        .andExpect(jsonPath("$.content[0].completed").value(true));

    }

    @Test
    void searchTasks_shouldReturnResults() throws Exception {
        //Arrange 
        TaskResponse task = new TaskResponse();
        task.setTitle("Java Task");
        task.setDescription("Learning Java and Spring Boot");
        task.setCompleted(false);

        Page<TaskResponse> taskPage = new PageImpl<>(List.of(task));

        when(taskService.searchTasks(eq("Java"),any(Pageable.class))).thenReturn(taskPage);

        // Act + Assert
        mockMvc.perform(
            get("/tasks/search")
                .param("keyword", "Java")
                .param("page", "0")
                .param("size", "10")
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content.length()").value(1))
        .andExpect(jsonPath("$.content[0].title").value("Java Task"))
        .andExpect(jsonPath("$.content[0].description").value("Learning Java and Spring Boot"))
        .andExpect(jsonPath("$.content[0].completed").value(false));

    }

    @Test
    void deleteTask_shouldReturn204() throws Exception{
        // Arrange
        doNothing().when(taskService).deleteTask(1L);
        // Act + Assert
        mockMvc.perform(
            delete("/tasks/1")
        )
        .andExpect(status().isNoContent());
        verify(taskService).deleteTask(1L);
    }

    @Test
    void updateTask_shouldReturn200() throws Exception {
        // Arrange
        TaskResponse response = new TaskResponse();
        response.setTitle("Updated Task");
        response.setDescription("Updated description");
        response.setCompleted(true);

        when(taskService.updateTask(
            eq(1L),
            any(TaskRequest.class)
        )).thenReturn(response);
        // Act + Assert
        String requestJson = """
        {
            "title": "Updated Task",
            "description": "Updated description",
            "completed": true
        }
        """;

        mockMvc.perform(
            put("/tasks/1")
                .contentType("application/json")
                .content(requestJson)
        )
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.title").value("Updated Task"))
        .andExpect(jsonPath("$.description").value("Updated description"))
        .andExpect(jsonPath("$.completed").value(true));

        verify(taskService).updateTask(eq(1L), any(TaskRequest.class));
    }

    @Test
    void updateTask_shouldReturn400WhenTitleIsBlank() throws Exception {
        // Arrange
        String requestJson = """
        {
            "title": "",
            "description": "This should fail validation",
            "completed": false
        }
        """;

        // Act + Assert
        mockMvc.perform(
            put("/tasks/1")
            .contentType("application/json")
            .content(requestJson)
        )
        .andExpect(status().isBadRequest());
    }

    @Test
    void getAllTasks_shouldAcceptSorting() throws Exception{
        //Arrange
        Page<TaskResponse> taskPage = new PageImpl<>(List.of());
        
        when(taskService.getAllTasks(any(Pageable.class)))  .thenReturn(taskPage);

        //Act
        mockMvc.perform(get("/tasks").param("sort", "title,asc").param("page", "0").param("size", "10")).andExpect(status().isOk());

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        verify(taskService).getAllTasks(pageableCaptor.capture());

        Pageable pageable = pageableCaptor.getValue();
        assertEquals("title", pageable.getSort().getOrderFor("title").getProperty());
        assertTrue(pageable.getSort().getOrderFor("title").isAscending());
    }

    @Test
    void getAllTasks_shouldAcceptDescendingSorting() throws Exception {
        //Arrange
        Page<TaskResponse> taskPage = new PageImpl<>(List.of());

        when(taskService.getAllTasks(any(Pageable.class))).thenReturn(taskPage);

        //Act
        mockMvc.perform(get("/tasks").param("sort", "title,desc").param("page", "0").param("size", "10")).andExpect(status().isOk());

        //Assert
        ArgumentCaptor<Pageable> pageableCaptor =  ArgumentCaptor.forClass(Pageable.class);
        verify(taskService).getAllTasks(pageableCaptor.capture());

        Pageable pageable = pageableCaptor.getValue();
        assertEquals("title", pageable.getSort().getOrderFor("title").getProperty());
        assertTrue(pageable.getSort().getOrderFor("title").isDescending());
    }

}
