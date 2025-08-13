package com.nikita.habittracker.controller;

import com.nikita.habittracker.model.Habit;
import com.nikita.habittracker.service.HabitService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/habits")
public class HabitController {

    private HabitService habitService;

    public HabitController(HabitService habitService) {
        this.habitService = habitService;
    }

    @PostMapping("/users/{userId}")
    public ResponseEntity<Habit> createHabit(@PathVariable int userId, @RequestBody Habit habit) {
        Habit savedHabit = habitService.saveHabitForUser(userId, habit);
        return ResponseEntity.ok(savedHabit);
    }


    @PostMapping
    public ResponseEntity<Habit> createHabit(@RequestBody Habit habit)
    {
        return ResponseEntity.ok(habitService.saveHabit(habit));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Habit> getHabitById(@PathVariable int id)
    {
        Optional<Habit> habit = habitService.getHabitsById(id);
        if(habit.isEmpty())
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(habit.get());
    }

    @GetMapping("/users/{id}")
    public List<Habit> getHabitsByUserId(@PathVariable int id)
    {
        List<Habit> habits = habitService.getHabitsByUserId(id);
        return habits;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHabit(@PathVariable int id)
    {
        Optional<Habit> habit = habitService.getHabitsById(id);
        if(habit.isEmpty())
            return ResponseEntity.notFound().build();

        habitService.deleteHabit(id);
        return ResponseEntity.noContent().build();
    }

}
