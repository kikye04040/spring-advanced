package org.example.expert.domain.todo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.service.TodoService;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class TodoControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private TodoController todoController;

    @Mock
    private TodoService todoService;

    @Mock
    private AuthUser authUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(todoController).build();
    }

    @Test
    public void testSaveTodo() throws Exception {
        TodoSaveRequest request = new TodoSaveRequest("Test Title", "Test Contents");
        TodoSaveResponse response = new TodoSaveResponse(1L, "Test Title", "Test Contents", "Sunny", new UserResponse(1L, "user@example.com"));

        when(authUser.getId()).thenReturn(1L);
        when(todoService.saveTodo(any(AuthUser.class), any(TodoSaveRequest.class))).thenReturn(response);

        mockMvc.perform(post("/todos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.contents").value("Test Contents"))
                .andExpect(jsonPath("$.weather").value("Sunny"))
                .andExpect(jsonPath("$.user.id").value(1L))
                .andExpect(jsonPath("$.user.email").value("user@example.com"));
    }

    @Test
    public void testGetTodos() throws Exception {
        TodoResponse todoResponse = new TodoResponse(
                1L, "Test Title", "Test Contents", "Sunny",
                new UserResponse(1L, "user@example.com"), LocalDateTime.now(), LocalDateTime.now()
        );
        Page<TodoResponse> page = new PageImpl<>(Collections.singletonList(todoResponse), PageRequest.of(0, 10), 1);

        when(todoService.getTodos(anyInt(), anyInt())).thenReturn(page);

        mockMvc.perform(get("/todos")
                        .param("page", "1")
                        .param("size", "10")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1L))
                .andExpect(jsonPath("$.content[0].title").value("Test Title"))
                .andExpect(jsonPath("$.content[0].contents").value("Test Contents"))
                .andExpect(jsonPath("$.content[0].weather").value("Sunny"))
                .andExpect(jsonPath("$.content[0].user.id").value(1L))
                .andExpect(jsonPath("$.content[0].user.email").value("user@example.com"));
    }

    @Test
    public void testGetTodo() throws Exception {
        TodoResponse todoResponse = new TodoResponse(
                1L, "Test Title", "Test Contents", "Sunny",
                new UserResponse(1L, "user@example.com"), LocalDateTime.now(), LocalDateTime.now()
        );

        when(todoService.getTodo(anyLong())).thenReturn(todoResponse);

        mockMvc.perform(get("/todos/1")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("Test Title"))
                .andExpect(jsonPath("$.contents").value("Test Contents"))
                .andExpect(jsonPath("$.weather").value("Sunny"))
                .andExpect(jsonPath("$.user.id").value(1L))
                .andExpect(jsonPath("$.user.email").value("user@example.com"));
    }
}
