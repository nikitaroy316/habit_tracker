package com.nikita.habittracker.service;

import com.nikita.habittracker.model.Habit;
import com.nikita.habittracker.model.HabitCheckIn;
import com.nikita.habittracker.model.TaskStatus;
import com.nikita.habittracker.repository.HabitCheckInRepository;
import com.nikita.habittracker.repository.HabitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@WithMockUser
public class HabitCheckInServiceTest {

    @Mock
    private HabitRepository habitRepository;

    @Mock
    private HabitCheckInRepository habitCheckInRepository;

    @InjectMocks
    private HabitCheckInService habitCheckInService;

    private Habit habit;
    private HabitCheckIn habitCheckIn;

    @BeforeEach
    void setUp()
    {
        MockitoAnnotations.openMocks(this);
        habit = new Habit();
        habit.setName("Drink water");
        habit.setGoalType("Daily");

        habitCheckIn = new HabitCheckIn();
        habitCheckIn.setHabit(habit);
        habitCheckIn.setStatus(TaskStatus.IN_PROGRESS);
        habitCheckIn.setDate(LocalDate.now());

    }

    @Test
    public void test_saveCheckIn_success() throws Exception
    {
        when(habitRepository.findById(habit.getId())).thenReturn(Optional.of(habit));
        when(habitCheckInRepository.save(any())).thenReturn(habitCheckIn);

        HabitCheckIn result = habitCheckInService.saveCheckIn(habitCheckIn);

        assertNotNull(result);
        assertEquals(TaskStatus.IN_PROGRESS,result.getStatus());
        verify(habitCheckInRepository).save(any(HabitCheckIn.class));
    }

    @Test
    public void test_saveCheckIn_habitNotFound_throwsException() throws Exception
    {
        when(habitRepository.findById(habit.getId())).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, ()-> habitCheckInService.saveCheckIn(habitCheckIn));

        assertEquals("Habit not found",ex.getMessage());
        verify(habitCheckInRepository,never()).save(any());
    }

    @Test
    public void test_getCheckInById_success() throws Exception
    {
        int id = 1;
        when(habitCheckInRepository.findById(id)).thenReturn(Optional.of(habitCheckIn));

        Optional<HabitCheckIn> result = habitCheckInService.getCheckInById(id);

        assertTrue(result.isPresent());
        assertEquals(TaskStatus.IN_PROGRESS, result.get().getStatus());

    }

    @Test
    public void test_getCheckInById_throwsException() throws Exception
    {
        int id = 1;
        when(habitCheckInRepository.findById(id)).thenReturn(Optional.empty());

        Optional<HabitCheckIn> result = habitCheckInService.getCheckInById(id);

        assertFalse(result.isPresent());

    }

    @Test
    public void test_getCheckInsByHabitId_success() throws Exception
    {
        int id = 1;
        List<HabitCheckIn> habitCheckIns = List.of(habitCheckIn);
        when(habitCheckInRepository.findByHabitId(id)).thenReturn(habitCheckIns);

        List<HabitCheckIn> result = habitCheckInService.getCheckInsByHabit(id);

        assertEquals(1,result.size());
        assertEquals(TaskStatus.IN_PROGRESS, result.get(0).getStatus());

    }

    @Test
    public void test_deleteCheckIn_success() throws Exception
    {
        int id = 1;
        when(habitCheckInRepository.findById(id)).thenReturn(Optional.of(habitCheckIn));
        habitCheckInService.deleteCheckIn(id);

        verify(habitCheckInRepository).deleteById(id);
    }

    @Test
    public void test_saveCheckInForHabit_success() throws Exception
    {
        when(habitRepository.findById(1)).thenReturn(Optional.of(habit));
        when(habitCheckInRepository.save(any())).thenReturn(habitCheckIn);

        HabitCheckIn result = habitCheckInService.saveCheckInForHabit(1,habitCheckIn);

        assertNotNull(result);
        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
        verify(habitCheckInRepository).save(any(HabitCheckIn.class));

    }

    @Test
    public void test_saveCheckInForHabit_habitNotFound_throwsException() throws Exception
    {
        when(habitRepository.findById(1)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, ()-> habitCheckInService.saveCheckInForHabit(1,habitCheckIn));

        assertEquals("Habit not found", ex.getMessage());
        verify(habitCheckInRepository,never()).save(any());

    }
}
