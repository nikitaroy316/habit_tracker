package com.nikita.habittracker.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nikita.habittracker.model.HabitCheckIn;
import com.nikita.habittracker.model.TaskStatus;
import com.nikita.habittracker.service.HabitCheckInService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser
@AutoConfigureMockMvc
@SpringBootTest
public class HabitCheckInControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private HabitCheckInService habitCheckInService;

    @Autowired
    private ObjectMapper objectMapper;

    private HabitCheckIn sampleHabitCheckin;

    @BeforeEach
    void setUp()
    {
        sampleHabitCheckin = new HabitCheckIn();
        sampleHabitCheckin.setStatus(TaskStatus.IN_PROGRESS);
        sampleHabitCheckin.setDate(LocalDate.now());
        sampleHabitCheckin.setId(1);

    }

    @Test
    public void testCreateCheckInForHabit_success() throws Exception
    {
        int habitId=1;

        when(habitCheckInService.saveCheckInForHabit(eq(habitId),any(HabitCheckIn.class))).thenReturn(sampleHabitCheckin);

        mockMvc.perform(post("/api/checkins/habits/{habitId}",habitId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(sampleHabitCheckin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    public void testCreateCheck_success() throws Exception
    {
        when(habitCheckInService.saveCheckIn(any(HabitCheckIn.class))).thenReturn(sampleHabitCheckin);

        mockMvc.perform(post("/api/checkins")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(sampleHabitCheckin)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    @Test
    public void testGetCheckInByHabitId_success() throws Exception
    {
        when(habitCheckInService.getCheckInsByHabit(1)).thenReturn(List.of(sampleHabitCheckin));

        mockMvc.perform(get("/api/checkins/habit/{id}",1))
                .andExpect(status().isOk())
                .andExpect(jsonPath(("$[0].status")).value(TaskStatus.IN_PROGRESS.toString()));
    }

    @Test
    public void testGetCheckInByHabitId_returnsEmptyList() throws Exception
    {
        when(habitCheckInService.getCheckInsByHabit(2)).thenReturn(List.of());

        mockMvc.perform(get("/api/checkins/habit/{id}",2))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

    }

    @Test
    public void testDeleteCheckIn_success() throws Exception
    {
        when(habitCheckInService.getCheckInById(1)).thenReturn(Optional.of(sampleHabitCheckin));

        mockMvc.perform(delete("/api/checkins/{id}",1))
                .andExpect(status().isNoContent());

        verify(habitCheckInService).deleteCheckIn(1);
    }

    @Test
    public void testDeleteCheckIn_notFound() throws Exception
    {
        when(habitCheckInService.getCheckInById(1)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/checkins/{id}",1))
                .andExpect(status().isNotFound());

        verify(habitCheckInService,never()).deleteCheckIn(anyInt());
    }
}
