package com.projectmanagement.clientprojectmanagement.repository;

import com.projectmanagement.clientprojectmanagement.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProjectRepository extends JpaRepository<Project , Long> {
    List<Project> findByClientId(Long clientId);

    long countByStatus(String status);
}
