package com.nikita.habittracker.service;

import com.nikita.habittracker.exception.InformationNotFoundException;
import com.nikita.habittracker.model.Habit;
import com.nikita.habittracker.model.User;
import com.nikita.habittracker.repository.HabitRepository;
import com.nikita.habittracker.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class HabitService {

    private final HabitRepository habitRepository;
    private final UserRepository userRepository;

    public HabitService(HabitRepository habitRepository, UserRepository userRepository) {
        this.habitRepository = habitRepository;
        this.userRepository = userRepository;
    }

    public Habit saveHabitForUser(int userId, Habit habit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        habit.setUser(user);

        if (habit.getHabitCheckIns() != null) {
            habit.getHabitCheckIns().forEach(checkIn -> checkIn.setHabit(habit));
        }

        return habitRepository.save(habit);
    }


    public Optional<Habit> getHabitsById(int id)
    {
        return habitRepository.findById(id);
    }

    public List<Habit> getHabitsByUserId(int id)
    {
        return habitRepository.findHabitsByUserId(id);
    }

    public Habit saveHabit(Habit habit)
    {
        if (habit.getHabitCheckIns() != null) {
            habit.getHabitCheckIns().forEach(checkIn -> checkIn.setHabit(habit));
        }
        return habitRepository.save(habit);
    }

    public List<Habit> getAll()
    {
        return habitRepository.findAll();
    }

    public void deleteHabit(int id)
    {
        if (!habitRepository.existsById(id)) {
            throw new InformationNotFoundException("Habit with id " + id + " not found.");
        }
         habitRepository.deleteById(id);
    }
}
