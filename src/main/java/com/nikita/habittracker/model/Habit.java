package com.nikita.habittracker.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@Entity
@Table(name = "habit")
public class Habit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotBlank(message = "Name is required")
    private String name;

    private String goalType;

    @JsonBackReference("user-habit")
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @JsonManagedReference("habit-habitCheckIn")
    @OneToMany(mappedBy = "habit", cascade = CascadeType.ALL)
    private List<HabitCheckIn> habitCheckIns;

    public Habit(){}

    public Habit(String name, String goalType, User user) {
        this.name = name;
        this.goalType = goalType;
        this.user = user;
    }
}
