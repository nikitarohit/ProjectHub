package com.projectmanagement.clientprojectmanagement.repository;

import com.projectmanagement.clientprojectmanagement.model.TaskComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskCommentRepository extends JpaRepository<TaskComment, Long> {

    // All comments for a task, ordered by creation time
    List<TaskComment> findByTaskIdOrderByCreatedAtAsc(Long taskId);
}