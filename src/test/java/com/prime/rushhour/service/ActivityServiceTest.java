package com.prime.rushhour.service;

import com.prime.rushhour.entities.Activity;
import com.prime.rushhour.models.ActivityDTO;
import com.prime.rushhour.models.UserResponseDTO;
import com.prime.rushhour.repository.ActivityRepository;
import com.prime.rushhour.repository.UserRepository;
import com.prime.rushhour.services.ActivityService;
import com.prime.rushhour.services.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;

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

    @Test
    public void addNeeActivitySuccess(){
        Activity activity = new Activity("Fitness",60,5.00);
        ActivityDTO activityDTO = new ActivityDTO("Fitness",60,5.00);

        when(activityRepository.save(any(Activity.class))).thenReturn(activity);
        Mockito.when(modelMapper.map(any(), any())).thenReturn(activity);
        Mockito.when(modelMapper.map(any(), any())).thenReturn(activityDTO);
        ActivityDTO addActivity = activityService.add(new ActivityDTO());

       assertEquals(addActivity.getName(),activity.getName());
    }

    @Test
    public void getActivityByIdSuccess(){
        Activity activity = new Activity("Fitness",60,5.00);
        ActivityDTO activityDTO = new ActivityDTO("Fitness",60,5.00);

        when(activityRepository.findById(anyInt())).thenReturn(Optional.of(activity));
        Mockito.when(modelMapper.map(any(), any())).thenReturn(activityDTO);
        ActivityDTO activityById = activityService.getById(anyInt());
        assertEquals(activityDTO.getName(), activityById.getName());
        assertEquals(activity.getName(), activityById.getName());
    }

}
