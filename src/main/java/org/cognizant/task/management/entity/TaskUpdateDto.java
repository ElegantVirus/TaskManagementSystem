package org.cognizant.task.management.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TaskUpdateDto extends TaskCreateDto {
    Long id;
}
