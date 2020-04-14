package org.cognizant.task.management.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id")
    private Long id;
    @Column(name = "task_name")
    private String name;
    @Column(name = "hours_spent")
    private Integer hoursSpent = 0;
    @Column(name = "task_group")
    private String group;
    @Column(name = "assignee")
    private String assignee;
    @Column(name = "parent")
    private Long parentId;
    @Column(name = "finished")
    private Boolean finished;

    public void merge(Task other) {
        this.assignee = other.getAssignee() == null ? this.assignee : other.getAssignee();
        this.name = other.getName() == null ? this.name : other.getName();
        this.hoursSpent = other.getHoursSpent() == null ? this.hoursSpent : other.getHoursSpent();
        this.group = other.getGroup() == null ? this.group : other.getGroup();
        this.parentId = other.getParentId() == null ? this.parentId : other.getParentId();
        this.finished = other.getFinished() == null ? this.finished : other.getFinished();
    }
}