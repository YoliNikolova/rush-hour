package com.prime.rushhour.service;

import com.prime.rushhour.entities.Activity;
import com.prime.rushhour.exception.ActivityNotFoundException;
import com.prime.rushhour.models.ActivityDTO;
import com.prime.rushhour.repository.ActivityRepository;
import com.prime.rushhour.services.ActivityService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

@RunWith(MockitoJUnitRunner.class)
public class ActivityServiceTest {
    @Mock
    private ActivityRepository activityRepository;

    @InjectMocks
    private ActivityService activityService;

    @Mock
    private ModelMapper modelMapper;

    private Activity activity;
    private ActivityDTO activityDTO;

    @Before
    public void setUp() {
        activity = new Activity("Fitness", 60, 5.00);
        activityDTO = new ActivityDTO("Fitness", 60, 5.00);
    }

    @Test
    public void addNewActivitySuccess() {
        when(activityRepository.save(any(Activity.class))).thenReturn(activity);
        Mockito.when(modelMapper.map(activityDTO, Activity.class)).thenReturn(activity);
        Mockito.when(modelMapper.map(activity, ActivityDTO.class)).thenReturn(activityDTO);
        ActivityDTO addActivity = activityService.add(activityDTO);

        assertEquals(addActivity.getName(), activity.getName());
    }

    @Test
    public void getActivityByIdSuccess() {
        when(activityRepository.findById(anyInt())).thenReturn(Optional.of(activity));
        Mockito.when(modelMapper.map(any(), any())).thenReturn(activityDTO);
        ActivityDTO activityById = activityService.getById(anyInt());
        assertEquals(activityDTO.getName(), activityById.getName());
        assertEquals(activity.getName(), activityById.getName());
    }

    @Test(expected = ActivityNotFoundException.class)
    public void getActivityByIdThrowException() {
        when(activityRepository.findById(anyInt())).thenThrow(ActivityNotFoundException.class);
        activityService.getById(anyInt());
    }

    @Test
    public void getAllActivitiesSuccess() {
        Pageable pageable = Mockito.mock(Pageable.class);
        Page<Activity> activities = Mockito.mock(Page.class);
        when(activityRepository.findAll(any(Pageable.class))).thenReturn(activities);
        List<ActivityDTO> listOfActivities = activityService.getAll(pageable);
        assertEquals(listOfActivities.size(), activities.getSize());
    }

    @Test
    public void updateActivityByIdSuccess() {
        Mockito.when(modelMapper.map(activityDTO, Activity.class)).thenReturn(activity);
        activity.setId(1);
        when(activityRepository.save(any(Activity.class))).thenReturn(activity);
        when(activityRepository.existsById(1)).thenReturn(true);

        Mockito.when(modelMapper.map(activity, ActivityDTO.class)).thenReturn(activityDTO);
        ActivityDTO updateById = activityService.updateById(activityDTO, 1);

        assertEquals(updateById.getName(), activity.getName());
    }

    @Test(expected = ActivityNotFoundException.class)
    public void updateActivityByIdThrowException() {
        Mockito.when(modelMapper.map(activityDTO, Activity.class)).thenReturn(activity);
        activity.setId(1);
        when(activityRepository.existsById(1)).thenReturn(false);
        activityService.updateById(activityDTO, 1);
        verify(activityRepository, times(0)).save(activity);
    }

    @Test
    public void deleteActivityByIdSuccess() {
        when(activityRepository.existsById(1)).thenReturn(true);
        doNothing().when(activityRepository).deleteById(1);
        activityService.delete(1);
        verify(activityRepository, times(1)).deleteById(1);
    }

    @Test(expected = ActivityNotFoundException.class)
    public void deleteActivityByIdThrowException() {
        when(activityRepository.existsById(1)).thenReturn(false);
        activityService.delete(1);
        verify(activityRepository, times(0)).deleteById(1);
    }
}
