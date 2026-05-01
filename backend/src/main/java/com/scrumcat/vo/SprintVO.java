package com.scrumcat.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class SprintVO {

    private Long id;

    private Long projectId;

    private String name;

    private String goal;

    private LocalDate startDate;

    private LocalDate endDate;

    private String status;

    private String ownerNickname;

    private List<String> members;

    private Integer storyCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
