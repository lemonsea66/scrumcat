package com.scrumcat.vo;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProjectVO {

    private Long id;

    private String name;

    private String description;

    private String ownerNickname;

    private List<String> members;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
