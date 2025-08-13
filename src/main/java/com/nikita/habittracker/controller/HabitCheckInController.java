package com.nikita.habittracker.controller;

import com.nikita.habittracker.model.Habit;
import com.nikita.habittracker.model.HabitCheckIn;
import com.nikita.habittracker.repository.HabitCheckInRepository;
import com.nikita.habittracker.service.HabitCheckInService;
import jakarta.transaction.Transactional;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/checkins")
public class HabitCheckInController {

    private HabitCheckInService habitCheckInService;

    public HabitCheckInController(HabitCheckInService habitCheckInService) {
        this.habitCheckInService = habitCheckInService;
    }
    @PostMapping("/habits/{habitId}")
    public ResponseEntity<HabitCheckIn> createHabit(@PathVariable int habitId, @RequestBody HabitCheckIn habitCheckIn) {
        HabitCheckIn saveCheckIn = habitCheckInService.saveCheckInForHabit(habitId, habitCheckIn);
        return ResponseEntity.ok(saveCheckIn);
    }

    @PostMapping
    @Transactional
    public ResponseEntity<HabitCheckIn> createCheckIn(@RequestBody HabitCheckIn habitCheckIn)
    {
        return ResponseEntity.ok(habitCheckInService.saveCheckIn(habitCheckIn));
    }

    @GetMapping("/habit/{id}")
    public List<HabitCheckIn> getCheckInsByHabit(@PathVariable int id)
    {
        return habitCheckInService.getCheckInsByHabit(id);
    }

    @Transactional
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCheckins(@PathVariable int id)
    {
        Optional<HabitCheckIn> checkIn = habitCheckInService.getCheckInById(id);
        if(checkIn.isEmpty())
            return ResponseEntity.notFound().build();

        habitCheckInService.deleteCheckIn(id);
        return ResponseEntity.noContent().build();
    }
}
