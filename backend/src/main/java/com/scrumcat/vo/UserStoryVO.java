package com.scrumcat.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserStoryVO {

    private Long id;

    private String title;

    private String description;

    private BigDecimal storyPoint;

    private Integer priority;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
