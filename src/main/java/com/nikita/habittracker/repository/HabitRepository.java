package com.nikita.habittracker.repository;

import com.nikita.habittracker.model.Habit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HabitRepository extends JpaRepository<Habit,Integer> {
    List<Habit> findHabitsByUserId(int id);


}
