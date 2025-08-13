package com.nikita.habittracker.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "profiles")
@Getter
@Setter
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column
    private Long id;

    @NotBlank(message = "firstName cannot be blank")
    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column
    private String bio;

    @JsonIgnore //prevents stack overflow
    @OneToOne(mappedBy = "profile")
    private User user;

    public Profile() {
    }

    public Profile(Long id, String firstName, String lastName, String bio, User user) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.bio = bio;
        this.user = user;
    }

    @Override
    public String toString() {
        return "Profile{" +
                "id=" + id +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", bio='" + bio + '\'' +
                //", user=" + user +
                '}';
    }
}
