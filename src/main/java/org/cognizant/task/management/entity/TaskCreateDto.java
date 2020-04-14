package org.cognizant.task.management.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskCreateDto {
    private String name;
    private Integer hoursSpent = 0;
    private String group;
    private String assignee;
    private Long parentId;
    private Boolean finished;
}
