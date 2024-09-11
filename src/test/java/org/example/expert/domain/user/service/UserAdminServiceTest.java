package org.example.expert.domain.user.service;

import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.dto.request.UserRoleChangeRequest;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class UserAdminServiceTest {

    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private UserAdminService userAdminService;

    @Test
    public void userRole_변경_중_유저를_찾지_못해_에러가_발생한다() {
        // given
        Long userId = 1L;
        UserRoleChangeRequest request = new UserRoleChangeRequest("ADMIN");

        // 유저를 찾지 못함
        given(userRepository.findById(anyLong())).willReturn(Optional.empty());

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> userAdminService.changeUserRole(userId, request));
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    public void userRole_정상적으로_변경한다() {
        // given
        User user = new User("test@example.com", "password", UserRole.USER);
        UserRoleChangeRequest request = new UserRoleChangeRequest("ADMIN");
        given(userRepository.findById(anyLong())).willReturn(Optional.of(user));

        // when
        userAdminService.changeUserRole(1L, request);

        // then
        assertEquals(UserRole.ADMIN, user.getUserRole());
    }
}
