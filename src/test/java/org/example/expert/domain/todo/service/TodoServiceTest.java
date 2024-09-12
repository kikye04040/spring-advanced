package org.example.expert.domain.todo.service;

import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.todo.dto.request.TodoSaveRequest;
import org.example.expert.domain.todo.dto.response.TodoResponse;
import org.example.expert.domain.todo.dto.response.TodoSaveResponse;
import org.example.expert.domain.todo.entity.Todo;
import org.example.expert.domain.todo.repository.TodoRepository;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.client.WeatherClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TodoServiceTest {

    @Mock
    private TodoRepository todoRepository;

    @Mock
    private WeatherClient weatherClient;

    @InjectMocks
    private TodoService todoService;

    @Test
    public void todo_저장_시_성공적으로_저장된다() {
        // given
        AuthUser authUser = new AuthUser(1L, "a@a.com", UserRole.USER);
        User user = new User("a@a.com", "password", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L); // Set ID for User

        String weather = "Sunny";
        TodoSaveRequest todoSaveRequest = new TodoSaveRequest("Test Title", "Test Contents");
        Todo todo = new Todo(
                todoSaveRequest.getTitle(),
                todoSaveRequest.getContents(),
                weather,
                user
        );
        ReflectionTestUtils.setField(todo, "id", 1L); // Set ID for Todo

        given(weatherClient.getTodayWeather()).willReturn(weather);
        given(todoRepository.save(any(Todo.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        TodoSaveResponse response = todoService.saveTodo(authUser, todoSaveRequest);

        // then
        assertNotNull(response);
        assertEquals(todoSaveRequest.getTitle(), response.getTitle());
        assertEquals(todoSaveRequest.getContents(), response.getContents());
        assertEquals(weather, response.getWeather());
        assertEquals(user.getId(), response.getUser().getId());
    }

    @Test
    public void todo_목록_조회에_성공한다() {
        // given
        User user = new User("user1@example.com", "password", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L); // Set ID for User

        Todo todo = new Todo("Title", "Contents", "Sunny", user);
        ReflectionTestUtils.setField(todo, "id", 1L); // Set ID for Todo
        ReflectionTestUtils.setField(todo, "createdAt", LocalDateTime.now().minusDays(1));
        ReflectionTestUtils.setField(todo, "modifiedAt", LocalDateTime.now());

        Page<Todo> todoPage = new PageImpl<>(List.of(todo), PageRequest.of(0, 10), 1);

        given(todoRepository.findAllByOrderByModifiedAtDesc(any(Pageable.class))).willReturn(todoPage);

        // when
        List<TodoResponse> responses = todoService.getTodos(1, 10).getContent();

        // then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        TodoResponse response = responses.get(0);
        assertEquals(todo.getId(), response.getId());
        assertEquals(todo.getTitle(), response.getTitle());
        assertEquals(todo.getContents(), response.getContents());
        assertEquals(todo.getWeather(), response.getWeather());
        assertEquals(user.getId(), response.getUser().getId());
    }

    @Test
    public void todo_조회_시_존재하지_않으면_InvalidRequestException_발생() {
        // given
        long todoId = 1L;
        given(todoRepository.findByIdWithUser(todoId)).willReturn(Optional.empty());

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> todoService.getTodo(todoId));
        assertEquals("Todo not found", exception.getMessage());
    }

    @Test
    public void todo_조회_시_정상적으로_조회된다() {
        // given
        long todoId = 1L;
        User user = new User("user1@example.com", "password", UserRole.USER);
        ReflectionTestUtils.setField(user, "id", 1L); // Set ID for User

        Todo todo = new Todo("Title", "Contents", "Sunny", user);
        ReflectionTestUtils.setField(todo, "id", todoId);
        ReflectionTestUtils.setField(todo, "createdAt", LocalDateTime.now().minusDays(1));
        ReflectionTestUtils.setField(todo, "modifiedAt", LocalDateTime.now());

        given(todoRepository.findByIdWithUser(todoId)).willReturn(Optional.of(todo));

        // when
        TodoResponse response = todoService.getTodo(todoId);

        // then
        assertNotNull(response);
        assertEquals(todo.getId(), response.getId());
        assertEquals(todo.getTitle(), response.getTitle());
        assertEquals(todo.getContents(), response.getContents());
        assertEquals(todo.getWeather(), response.getWeather());
        assertEquals(user.getId(), response.getUser().getId());
    }
}
