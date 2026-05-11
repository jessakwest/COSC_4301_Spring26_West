package com.neonark.backend.repository;

import com.neonark.backend.entity.FeedingSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;

public interface FeedingRepository extends JpaRepository<FeedingSchedule, Long> {
    List<FeedingSchedule> findByFeedingTimeAndActiveTrue(LocalTime feedingTime);

}
