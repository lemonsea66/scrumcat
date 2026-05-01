package com.scrumcat.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class ProjectRequest {

    @NotBlank(message = "项目名称不能为空")
    private String name;

    private String description;

    private String ownerNickname;

    private List<String> members;
}
