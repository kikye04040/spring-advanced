package org.example.expert.domain.comment.controller;

import org.example.expert.domain.comment.service.CommentAdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CommentAdminController.class)
public class CommentAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentAdminService commentAdminService;

    @Test
    public void deleteComment_정상적인_요청시_200_응답을_반환한다() throws Exception {
        // given
        Long commentId = 1L;
        doNothing().when(commentAdminService).deleteComment(commentId);

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.delete("/admin/comments/{commentId}", commentId))
                .andExpect(status().isOk());

        verify(commentAdminService, times(1)).deleteComment(commentId);
    }
}