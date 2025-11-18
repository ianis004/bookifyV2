package com.bookify.repository;

import com.bookify.entity.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * ServiceRepository - Data access for Service entity
 */
@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findByActiveTrue();

    List<Service> findByActiveFalse();

    List<Service> findByNameContainingIgnoreCase(String name);
}