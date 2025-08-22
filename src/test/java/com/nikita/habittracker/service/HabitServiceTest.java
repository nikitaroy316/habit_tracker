package com.nikita.habittracker.service;

import com.nikita.habittracker.exception.InformationNotFoundException;
import com.nikita.habittracker.model.*;
import com.nikita.habittracker.repository.HabitRepository;
import com.nikita.habittracker.repository.UserRepository;
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
public class HabitServiceTest {

    @Mock
    private HabitRepository habitRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private HabitService habitService;

    @BeforeEach
    void setUp()
    {
        MockitoAnnotations.openMocks(this);
    }

    private User createUser()
    {
        User user = new User();
        user.setEmail("test@gmail.com");
        user.setPassword("test123");
        return user;
    }

    private Habit createHabit()
    {
        Habit habit = new Habit();
        habit.setName("Drink water");
        habit.setGoalType("Daily");

        HabitCheckIn habitCheckIn = new HabitCheckIn();
        habitCheckIn.setDate(LocalDate.now());

        habit.setHabitCheckIns(List.of(habitCheckIn));
        return habit;
    }

    @Test
    public void test_saveHabitForUser_userExists_savesHabit() throws Exception
    {
        User user = createUser();
        Habit habit = createHabit();

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(habitRepository.save(any(Habit.class))).thenReturn(habit);

        Habit result = habitService.saveHabitForUser(1,habit);

        assertEquals(habit.getName(),result.getName());
        assertEquals(user,result.getUser());
        verify(habitRepository).save(habit);

    }

    @Test
    public void test_saveHabitForUser_userNotFound_throwsException() throws Exception
    {
        Habit habit = createHabit();

        when(userRepository.findById(1)).thenReturn(Optional.empty());

        Exception ex = assertThrows(RuntimeException.class, ()-> {
            habitService.saveHabitForUser(1,habit);
        });

        assertEquals("User not found",ex.getMessage());

        verify(habitRepository,never()).save(any());

    }

    @Test
    public void test_saveHabit_validHabit_savesHabit() throws Exception
    {
        Habit habit = createHabit();
        when(habitRepository.save(habit)).thenReturn(habit);

        Habit result = habitService.saveHabit(habit);

        assertEquals(habit.getName(),result.getName());
        verify(habitRepository).save(habit);
    }

    @Test
    public void test_saveHabit_invalidHabit_throwsException() throws Exception
    {
        assertThrows(NullPointerException.class, () -> {
            habitService.saveHabit(null);
        });
    }

    @Test
    public void test_getHabitById_success() throws Exception
    {
        int habitId = 1;
        Habit habit = createHabit();
        when(habitRepository.findById(habitId)).thenReturn(Optional.of(habit));

        Optional<Habit> result = habitService.getHabitsById(habitId);

        assertTrue(result.isPresent());
        assertEquals("Drink water",result.get().getName());
    }

    @Test
    public void test_getHabitByUserId_success() throws Exception
    {
        int userId = 1;
        List<Habit> habits = List.of(createHabit());
        when(habitRepository.findHabitsByUserId(userId)).thenReturn(habits);

        List<Habit> result = habitService.getHabitsByUserId(userId);

        assertEquals(1,result.size());
        assertEquals("Drink water",result.get(0).getName());
    }

    @Test
    public void test_getHabitByUserId_throwsException() throws Exception
    {
        int userId = 1;

        when(habitRepository.findHabitsByUserId(userId)).thenReturn(List.of());

        List<Habit> result = habitService.getHabitsByUserId(userId);

        assertEquals(0,result.size());

    }

    @Test
    public void test_getAllHabits_success() throws Exception
    {
        List<Habit> habits = List.of(createHabit());
        when(habitRepository.findAll()).thenReturn(habits);

        List<Habit> result = habitService.getAll();

        assertEquals(1,result.size());
        assertEquals("Drink water",result.get(0).getName());
    }

    @Test
    public void test_deleteHabit_success() throws Exception
    {
        int id = 1;
        when(habitRepository.existsById(id)).thenReturn(true);
        habitService.deleteHabit(id);

        verify(habitRepository).deleteById(id);
    }

    @Test
    public void test_deleteHabit_throwsException() throws Exception
    {
        when(habitRepository.existsById(1)).thenReturn(false);
       InformationNotFoundException ex = assertThrows(InformationNotFoundException.class, () -> habitService.deleteHabit(1));

       assertEquals("Habit with id 1 not found.",ex.getMessage());
        verify(habitRepository,never()).deleteById(1);
    }

}
