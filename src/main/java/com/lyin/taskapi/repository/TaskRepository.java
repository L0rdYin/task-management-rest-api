package com.lyin.taskapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lyin.taskapi.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, Long> {

    Page<Task> findByCompleted(boolean completed, Pageable pageable);
    Page<Task> findByTitleContainingIgnoreCase(String keyword, Pageable pageable);

}
