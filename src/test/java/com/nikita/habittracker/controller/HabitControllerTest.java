package com.nikita.habittracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikita.habittracker.model.Habit;
import com.nikita.habittracker.service.HabitService;
import com.nikita.habittracker.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser
public class HabitControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HabitService habitService;

    @Autowired
    private ObjectMapper objectMapper;

    private Habit sampleHabit;

    @BeforeEach
    void setUp()
    {
        sampleHabit = new Habit();
        sampleHabit.setId(1);
        sampleHabit.setName("Drink water");
        sampleHabit.setGoalType("Daily");
    }

    @Test
    public void testCreateHabitForUser_success() throws Exception
    {
        int userId = 1;

        when(habitService.saveHabitForUser(eq(userId),any(Habit.class))).thenReturn(sampleHabit);

        mockMvc.perform(post("/api/habits/users/{userId}",userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleHabit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Drink water"))
                .andExpect(jsonPath("$.goalType").value("Daily"));
    }

    @Test
    public void testGetHabitById_success() throws Exception
    {
       when(habitService.getHabitsById(1)).thenReturn(Optional.of(sampleHabit));

       mockMvc.perform(get("/api/habits/{id}",1))
               .andExpect(status().isOk())
               .andExpect(jsonPath("$.name").value("Drink water"));

    }

    @Test
    public void testGetHabitById_notFound() throws Exception
    {
        when(habitService.getHabitsById(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/habits/{id}",1))
                .andExpect(status().isNotFound());

    }

    @Test
    public void testGetHabitByUserId_success() throws Exception
    {
        when(habitService.getHabitsByUserId(1)).thenReturn(List.of(sampleHabit));

        mockMvc.perform(get("/api/habits/users/{id}",1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Drink water"));

    }

    @Test
    public void testDeleteHabitById_success() throws Exception
    {
        when(habitService.getHabitsById(1)).thenReturn(Optional.of(sampleHabit));

        mockMvc.perform(delete("/api/habits/{id}",1))
                .andExpect(status().isNoContent());

        verify(habitService).deleteHabit(1);
    }

    @Test
    public void testDeleteHabitById_notFound() throws Exception
    {
        when(habitService.getHabitsById(1)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/habits/{id}",1))
                .andExpect(status().isNotFound());

        verify(habitService, never()).deleteHabit(anyInt());
    }
}
