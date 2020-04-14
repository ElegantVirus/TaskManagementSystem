package org.cognizant.task.management.repository;

import org.cognizant.task.management.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {
    Task getFirstById(Long id);

    List<Task> getAllByparent(Long parent);
}
