package com.nikita.habittracker.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.scheduling.config.Task;

import java.time.LocalDate;
import java.util.Enumeration;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "habitCheckIn")
public class HabitCheckIn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull(message = "Date is required")
    private LocalDate date;

    public HabitCheckIn(LocalDate date, TaskStatus status, Habit habit) {
        this.date = date;
        this.status = status;
        this.habit = habit;
    }

    @Enumerated(EnumType.STRING)
    private TaskStatus status;

    @JsonBackReference("habit-habitCheckIn")
    @ManyToOne
    @JoinColumn(name = "habit_id")
    private Habit habit;

    public HabitCheckIn(){}

    public HabitCheckIn(LocalDate date, Habit habit) {
        this.date = date;
        this.habit = habit;
    }
}
