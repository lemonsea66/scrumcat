package com.scrumcat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns(
                        "/api/auth/me",
                        "/api/projects/**",
                        "/api/stories/**",
                        "/api/backlog/**",
                        "/api/sprints/**",
                        "/api/sprint-board/**",
                        "/api/task-board/**",
                        "/api/tasks/**",
                        "/api/burndown/**",
                        "/api/analytics/**",
                        "/api/dashboard/**");
    }
}
