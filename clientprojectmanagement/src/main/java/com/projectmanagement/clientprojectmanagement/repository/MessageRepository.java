package com.projectmanagement.clientprojectmanagement.repository;

import com.projectmanagement.clientprojectmanagement.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    // All messages for a project, oldest first (used in chat display)
    List<Message> findByProjectIdOrderByTimestampAsc(Long projectId);

    // All messages for a project, newest first
    List<Message> findByProjectIdOrderByTimestampDesc(Long projectId);

    // Single latest message — used for "last message" preview on dashboard
    Message findTopByProject_IdOrderByTimestampDesc(Long projectId);
}