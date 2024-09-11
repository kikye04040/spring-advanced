package org.example.expert.aspect;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@Slf4j
public class AdminApiLoggingAspect {
    private final HttpServletRequest request;
    public AdminApiLoggingAspect(HttpServletRequest request) {
        this.request = request;
    }

    @After("execution(* org.example.expert.domain.comment.controller.CommentAdminController.deleteComment(..)) || " +
            "execution(* org.example.expert.domain.user.controller.UserAdminController.changeUserRole(..))")
    public void logAdminApiAccess(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        String userId = "Unknown";
        if (args.length > 0 && args[0] instanceof Long) {
            userId = args[0].toString();
        }

        // 요청 시각
        LocalDateTime requestTime = LocalDateTime.now();

        // 요청 URL
        String requestUrl = request.getRequestURI();

        // 로그 기록
        log.info("::: Admin API Access Log: 사용자 ID = {}, 요청 시각 = {}, 요청 URL = {} :::",
                userId, requestTime, requestUrl);
    }
}