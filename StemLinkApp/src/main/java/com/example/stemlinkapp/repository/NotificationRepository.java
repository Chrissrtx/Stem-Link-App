package com.example.stemlinkapp.repository;

import com.example.stemlinkapp.domain.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByUserIdOrderByCreatedAtDesc(Long userId);
    List<Notification> findByUserEmailOrderByCreatedAtDesc(String email);
    Page<Notification> findByUserEmailOrderByCreatedAtDesc(String email, Pageable pageable);
}