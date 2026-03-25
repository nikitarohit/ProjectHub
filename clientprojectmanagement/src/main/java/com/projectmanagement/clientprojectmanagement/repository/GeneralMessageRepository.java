package com.projectmanagement.clientprojectmanagement.repository;

import com.projectmanagement.clientprojectmanagement.model.GeneralMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GeneralMessageRepository extends JpaRepository<GeneralMessage, Long> {

    // Get all messages for a specific client conversation
    List<GeneralMessage> findByClient_IdOrderByTimestampAsc(Long clientId);
}