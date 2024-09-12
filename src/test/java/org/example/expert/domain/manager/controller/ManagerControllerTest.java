package org.example.expert.domain.manager.controller;

import org.example.expert.config.JwtUtil;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.service.ManagerService;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ManagerControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private ManagerController managerController;

    @Mock
    private ManagerService managerService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthUser authUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(managerController).build();
    }

    @Test
    public void testSaveManager() throws Exception {
        ManagerSaveRequest request = new ManagerSaveRequest();
        Long managerUserId = 1L;

        ManagerSaveResponse response = new ManagerSaveResponse(1L, new UserResponse(1L, "email@example.com"));

        when(managerService.saveManager(any(AuthUser.class), anyLong(), any(ManagerSaveRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/todos/1/managers")
                        .contentType("application/json")
                        .content("{\"managerUserId\":1}")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.user.id").value(1L))
                .andExpect(jsonPath("$.user.email").value("email@example.com"));
    }

    @Test
    public void testGetMembers() throws Exception {
        ManagerResponse response = new ManagerResponse(1L, new UserResponse(1L, "email@example.com"));
        when(managerService.getManagers(anyLong())).thenReturn(List.of(response));

        mockMvc.perform(get("/todos/1/managers")
                        .header("Authorization", "Bearer token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].user.id").value(1L))
                .andExpect(jsonPath("$[0].user.email").value("email@example.com"));
    }

//    @Test
//    void testDeleteManager() throws Exception {
//        ManagerResponse response = new ManagerResponse(1L, new UserResponse(1L, "email@example.com"));
//        when(managerService.getManagers(anyLong())).thenReturn(List.of(response));
//
//        mockMvc.perform(delete("/todos/1/managers")
//                        .header("Authorization", "Bearer token"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id").value(1L))
//                .andExpect(jsonPath("$[0].user.id").value(1L))
//                .andExpect(jsonPath("$[0].user.email").value("email@example.com"));
//
//        verify(managerService, times(1)).deleteManager(userId, todoId, managerId);
//    }
}
