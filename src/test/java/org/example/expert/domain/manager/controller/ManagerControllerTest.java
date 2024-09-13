package org.example.expert.domain.manager.controller;

import org.example.expert.config.AuthUserArgumentResolver;
import org.example.expert.config.JwtUtil;
import org.example.expert.domain.common.dto.AuthUser;
import org.example.expert.domain.manager.dto.request.ManagerSaveRequest;
import org.example.expert.domain.manager.dto.response.ManagerResponse;
import org.example.expert.domain.manager.dto.response.ManagerSaveResponse;
import org.example.expert.domain.manager.service.ManagerService;
import org.example.expert.domain.user.dto.response.UserResponse;
import org.example.expert.domain.user.enums.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ManagerController.class)
public class ManagerControllerTest {

    // @Autowired 원래는 이렇게 쓰는 게 맞지만 resolver 때문에 커스텀이 필요해서 autowired 말고 setup에서 재정의
    private MockMvc mockMvc;

    @Autowired
    private ManagerController managerController;

    @MockBean
    private ManagerService managerService;

    @MockBean   // final 이면 무조건 같이 생성을 해주어야 함 (ManagerController)
    private JwtUtil jwtUtil;

    // @Mock
    // private AuthUser authUser;

    @Mock // any() 모킹하려면 모킹을 위한 선언(목으로 쓰겠다)이 필요해서 넣어준 것
    private AuthUserArgumentResolver authUserArgumentResolver;

    @BeforeEach
    public void setup() {
        // MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(managerController)
                        .setCustomArgumentResolvers(authUserArgumentResolver).build();
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
//    public void testDeleteManager() throws Exception {
//        // AuthUser 객체 모킹 (Mock AuthUser object)
//        AuthUser authUser = mock(AuthUser.class);
//        when(authUser.getId()).thenReturn(1L);  // id 값이 1로 설정되도록 모킹
//
//        // 응답 모킹 (Mock response)
//        ManagerResponse response = new ManagerResponse(1L, new UserResponse(1L, "email@example.com"));
//        when(managerService.getManagers(anyLong())).thenReturn(List.of(response));
//
//        long todoId = 1L;
//        long managerUserId = 1L;
//
//        // MockMvc 요청 실행 (Perform MockMvc request)
//        mockMvc.perform(delete("/todos/1/managers/1")
//                        .header("Authorization", "Bearer token"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$[0].id").value(1L))
//                .andExpect(jsonPath("$[0].user.id").value(1L))
//                .andExpect(jsonPath("$[0].user.email").value("email@example.com"));
//
//        // 서비스 호출 확인 (Verify service method call)
//        verify(managerService, times(1)).deleteManager(1L, todoId, managerUserId);
//    }

    @Test
    public void deleteManager_매니저_삭제에_성공할_시_200_반환() throws Exception {
        // given
        AuthUser authUser = new AuthUser(1L, "test@example.com", UserRole.ADMIN);
        long todoId = 1L;
        long managerUserId = 2L;
        given(authUserArgumentResolver.supportsParameter(any())).willReturn(true);  // 동작 과정에서 필요한 것
        given(authUserArgumentResolver.resolveArgument(any(), any(), any(), any())).willReturn(authUser);

        // System.out.println(authUser.getId());

        // when
        willDoNothing().given(managerService).deleteManager(anyLong(), anyLong(), anyLong());

        // then
        mockMvc.perform(delete("/todos/{todoId}/managers/{managerId}", todoId, managerUserId)
                .header("Authorization", "Bearer token"))
                .andExpect(status().isOk());
                //.andExpect(jsonPath("$[0].id").value(authUser.getId()))
//                .andExpect(jsonPath("$[0].todo.id").value(todoId))
//                .andExpect(jsonPath("$[0].manager.user.id").value(managerUserId));
        // verify(managerService, times(1)).deleteManager(authUser.getId(), todoId, managerUserId);
    }
}
