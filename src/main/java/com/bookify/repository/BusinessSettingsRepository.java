package com.bookify.repository;

import com.bookify.entity.BusinessSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 *Data access for BusinessSettings entity
 */
@Repository
public interface BusinessSettingsRepository extends JpaRepository<BusinessSettings, Long> {
}