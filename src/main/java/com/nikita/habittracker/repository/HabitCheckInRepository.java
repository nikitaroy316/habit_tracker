package com.nikita.habittracker.repository;

import com.nikita.habittracker.model.HabitCheckIn;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HabitCheckInRepository extends JpaRepository<HabitCheckIn, Integer> {

    List<HabitCheckIn> findByHabitId(int id);
}
