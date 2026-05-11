package com.neonark.backend.service;

import com.neonark.backend.dto.FeedingLookupResponse;
import com.neonark.backend.entity.FeedingSchedule;
import com.neonark.backend.exception.BadRequestException;
import com.neonark.backend.repository.FeedingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedingService {
    private final FeedingRepository feedingRepository;

    public List<FeedingLookupResponse> getFeedingsByTime(String time) {
        LocalTime feedingTime;

        try {
            feedingTime = LocalTime.parse(time);
        } catch (Exception e) {
            throw new BadRequestException("Invalid time format. Use: HH:MM");
        }

        List<FeedingSchedule> schedules = feedingRepository.findByFeedingTimeAndActiveTrue(feedingTime);

        return schedules.stream()
                .map(schedule -> FeedingLookupResponse.builder()
                        .creatureId(schedule.getCreature().getId())
                        .creatureName(schedule.getCreature().getName())
                        .habitatName(schedule.getCreature().getHabitat().getName())
                        .feedingTime(schedule.getFeedingTime().toString())
                        .build()).toList();
    }
}
