package org.example.expert.domain.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.service.UserAdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserAdminController.class)
public class UserAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserAdminService userAdminService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void changeUserRole_정상적인_요청시_200_응답을_반환한다() throws Exception {
        // given
        Long userId = 1L;
        UserRoleChangeRequest request = new UserRoleChangeRequest("ADMIN");
        doNothing().when(userAdminService).changeUserRole(eq(userId), argThat(arg -> "ADMIN".equals(arg.getRole())));

        // 객체를 JSON 문자열로 변환
        String jsonRequest = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.patch("/admin/users/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk());

        // 커스텀 매처로 역할 필드 검증
        verify(userAdminService, times(1)).changeUserRole(eq(userId), argThat(arg -> "ADMIN".equals(arg.getRole())));
    }
}