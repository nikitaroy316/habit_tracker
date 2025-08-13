package com.nikita.habittracker.service;

import com.nikita.habittracker.model.Habit;
import com.nikita.habittracker.model.HabitCheckIn;
import com.nikita.habittracker.repository.HabitCheckInRepository;
import com.nikita.habittracker.repository.HabitRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HabitCheckInService {

    private final HabitCheckInRepository habitCheckInRepository;
    private final HabitRepository habitRepository;

    public HabitCheckInService(HabitCheckInRepository habitCheckInRepository, HabitRepository habitRepository) {
        this.habitCheckInRepository = habitCheckInRepository;
        this.habitRepository = habitRepository;
    }

    public HabitCheckIn saveCheckIn(HabitCheckIn habitCheckIn)
    {
        Habit habit = habitRepository.findById(habitCheckIn.getHabit().getId())
                .orElseThrow(() -> new RuntimeException("Habit not found"));

        HabitCheckIn checkIn = new HabitCheckIn();
        checkIn.setDate(habitCheckIn.getDate());
        checkIn.setHabit(habit);
        checkIn.setStatus(habitCheckIn.getStatus());
        return habitCheckInRepository.save(checkIn);
    }

    public Optional<HabitCheckIn> getCheckInById(int id)
    {
        return habitCheckInRepository.findById(id);
    }

    public List<HabitCheckIn> getCheckInsByHabit(int id)
    {
        return habitCheckInRepository.findByHabitId(id);
    }

    public void deleteCheckIn(int id)
    {
        habitCheckInRepository.deleteById(id);
    }

    public HabitCheckIn saveCheckInForHabit(int habitId, HabitCheckIn habitCheckIn) {
        Habit habit = habitRepository.findById(habitId)
                .orElseThrow(() -> new RuntimeException("Habit not found"));

        HabitCheckIn checkIn = new HabitCheckIn();
        checkIn.setDate(habitCheckIn.getDate());
        checkIn.setHabit(habit);
        checkIn.setStatus(habitCheckIn.getStatus());
        return habitCheckInRepository.save(checkIn);
    }
}
