package com.projectmanagement.clientprojectmanagement.repository;

import com.projectmanagement.clientprojectmanagement.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    // All tasks for a project
    List<Task> findByProjectId(Long projectId);

    // All tasks assigned to a developer
    List<Task> findByAssignedToId(Long userId);

    // Tasks for a project filtered by status
    List<Task> findByProjectIdAndStatus(Long projectId, String status);

    // Tasks assigned to a developer filtered by status
    List<Task> findByAssignedToIdAndStatus(Long userId, String status);
}