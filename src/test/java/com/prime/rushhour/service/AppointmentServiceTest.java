package com.prime.rushhour.service;

import com.prime.rushhour.entities.Activity;
import com.prime.rushhour.entities.Appointment;
import com.prime.rushhour.entities.Role;
import com.prime.rushhour.entities.User;
import com.prime.rushhour.exception.*;
import com.prime.rushhour.models.AppointmentRequestDTO;
import com.prime.rushhour.models.AppointmentResponseDTO;
import com.prime.rushhour.repository.ActivityRepository;
import com.prime.rushhour.repository.AppointmentRepository;
import com.prime.rushhour.repository.UserRepository;
import com.prime.rushhour.security.MyUserDetails;
import com.prime.rushhour.services.AppointmentService;
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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class AppointmentServiceTest {
    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ActivityRepository activityRepository;

    @InjectMocks
    private AppointmentService appointmentService;

    @Mock
    private ModelMapper modelMapper;

    private User user;
    private MyUserDetails myUserDetails;

    @Before()
    public void setUp() {
        user = new User("Yoli", "Nikolova", "yoli@abv.bg", "yoli9818", Arrays.asList(new Role("ROLE_USER")));
        myUserDetails = Mockito.mock(MyUserDetails.class);
    }

    @Test
    public void getAllAppointmentsByADMIN() {
        Pageable pageable = Mockito.mock(Pageable.class);
        Page<Appointment> appointments = Mockito.mock(Page.class);
        when(appointmentRepository.findAll(any(Pageable.class))).thenReturn(appointments);
        when(myUserDetails.hasRole("ROLE_ADMIN")).thenReturn(true);

        List<AppointmentResponseDTO> listOfAppointments = appointmentService.getAll(pageable, myUserDetails);
        assertEquals(listOfAppointments.size(), appointments.getSize());
    }

    @Test
    public void getAllAppointmentsByUSER() {
        Pageable pageable = Mockito.mock(Pageable.class);
        Page<Appointment> appointments = Mockito.mock(Page.class);
        when(appointmentRepository.findAllByUserId(anyInt(), any(Pageable.class))).thenReturn(appointments);
        when(myUserDetails.hasRole("ROLE_ADMIN")).thenReturn(false);

        List<AppointmentResponseDTO> listOfAppointments = appointmentService.getAll(pageable, myUserDetails);
        assertEquals(listOfAppointments.size(), appointments.getSize());
    }

    @Test
    public void getAppointmentByIdSuccess() {
        Appointment app = new Appointment(LocalDateTime.of(2021, 5, 5, 14, 15), LocalDateTime.of(2021, 05, 05, 16, 45), Arrays.asList(new Activity("Fitness"), new Activity("Yoga")));
        AppointmentResponseDTO appResponse = new AppointmentResponseDTO(LocalDateTime.of(2021, 5, 5, 14, 15), LocalDateTime.of(2021, 05, 05, 16, 45), Arrays.asList(new Activity("Fitness"), new Activity("Yoga")));

        when(appointmentRepository.findById(anyInt())).thenReturn(Optional.of(app));
        Mockito.when(modelMapper.map(any(), any())).thenReturn(appResponse);
        app.setUser(user);
        app.getUser().setId(myUserDetails.getId());

        AppointmentResponseDTO response = appointmentService.getById(1, myUserDetails);
        assertEquals(app.getId(), response.getId());
    }

    @Test(expected = AppointmentNotFoundException.class)
    public void getAppointmentByIdThrowExceptionNotFound() {
        when(appointmentRepository.findById(anyInt())).thenThrow(AppointmentNotFoundException.class);
        appointmentService.getById(1, myUserDetails);
    }

    @Test(expected = ForbiddenException.class)
    public void getAppointmentByIdThrowExceptionForbidden() {
        Appointment app = new Appointment(LocalDateTime.of(2021, 5, 5, 14, 15), LocalDateTime.of(2021, 05, 05, 16, 45), Arrays.asList(new Activity("Fitness"), new Activity("Yoga")));

        when(appointmentRepository.findById(anyInt())).thenReturn(Optional.of(app));
        when(myUserDetails.hasRole("ROLE_ADMIN")).thenReturn(false);
        app.setUser(user);
        app.getUser().setId(2);
        appointmentService.getById(1, myUserDetails);
    }

    @Test
    public void addAppointmentSuccess() {
        AppointmentResponseDTO appResponse = new AppointmentResponseDTO(LocalDateTime.of(2021, 6, 5, 14, 15), LocalDateTime.of(2021, 6, 05, 16, 45), Arrays.asList(new Activity("Fitness")));
        AppointmentRequestDTO appRequest = new AppointmentRequestDTO(LocalDateTime.of(2021, 6, 5, 14, 15), Arrays.asList("Fitness"));
        Appointment app = new Appointment(LocalDateTime.of(2021, 6, 5, 14, 15), LocalDateTime.of(2021, 6, 05, 16, 45), Arrays.asList(new Activity("Fitness")));
        Activity activity = new Activity("Fitness", 60, 5.00);

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        app.setUser(user);
        when(activityRepository.findByName(anyString())).thenReturn(Optional.of(activity));
        when(appointmentRepository.findAllByUserId(anyInt())).thenReturn(Arrays.asList(app));
        Mockito.when(modelMapper.map(any(), any())).thenReturn(appResponse);

        AppointmentResponseDTO response = appointmentService.add(appRequest, 2);
        assertEquals(response.getId(), app.getId());
    }

    @Test(expected = ActivityNotFoundException.class)
    public void addAppointmentThrowActivityNotFound() {
        AppointmentRequestDTO appRequest = new AppointmentRequestDTO(LocalDateTime.of(2021, 5, 5, 14, 15), Arrays.asList("Fitness"));
        Appointment app = new Appointment(LocalDateTime.of(2021, 5, 5, 14, 15), LocalDateTime.of(2021, 05, 05, 16, 45), Arrays.asList(new Activity("Fitness")));

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        app.setUser(user);
        when(activityRepository.findByName(anyString())).thenReturn(Optional.empty());

        appointmentService.add(appRequest, 2);
    }

    @Test(expected = AppointmentExistsException.class)
    public void addAppointmentThrowExistingAppointment() {
        AppointmentRequestDTO appRequest = new AppointmentRequestDTO(LocalDateTime.of(2021, 4, 3, 14, 15), Arrays.asList("Fitness"));
        Appointment app = new Appointment(LocalDateTime.of(2021, 4, 3, 14, 15), LocalDateTime.of(2021, 4, 3, 16, 45), Arrays.asList(new Activity("Fitness")));
        Activity activity = new Activity("Fitness", 60, 5.00);


        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        app.setUser(user);
        app.setId(2);
        when(activityRepository.findByName(anyString())).thenReturn(Optional.of(activity));
        when(appointmentRepository.findAllByUserId(anyInt())).thenReturn(Arrays.asList(app));

        appointmentService.add(appRequest, 2);
    }

    @Test
    public void updateAppointmentByIdByADMIN() {
        AppointmentResponseDTO appResponse = new AppointmentResponseDTO(LocalDateTime.of(2021, 6, 5, 14, 15), LocalDateTime.of(2021, 6, 05, 16, 45), Arrays.asList(new Activity("Fitness")));
        AppointmentRequestDTO appRequest = new AppointmentRequestDTO(LocalDateTime.of(2021, 6, 5, 14, 15), Arrays.asList("Fitness"));
        Appointment oldAppointment = new Appointment(LocalDateTime.of(2021, 6, 5, 14, 15), LocalDateTime.of(2021, 6, 05, 16, 45), Arrays.asList(new Activity("Yoga")));
        Appointment newAppointment = new Appointment(LocalDateTime.of(2021, 6, 5, 14, 15), LocalDateTime.of(2021, 6, 05, 16, 45), Arrays.asList(new Activity("Fitness")));
        Activity activity = new Activity("Fitness", 60, 5.00);

        when(appointmentRepository.findById(anyInt())).thenReturn(Optional.of(oldAppointment));
        appRequest.setUserId(2);
        when(myUserDetails.hasRole("ROLE_ADMIN")).thenReturn(true);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        oldAppointment.setUser(user);
        when(activityRepository.findByName(anyString())).thenReturn(Optional.of(activity));
        when(appointmentRepository.findAllByUserId(anyInt())).thenReturn(Arrays.asList(newAppointment));
        Mockito.when(modelMapper.map(any(), any())).thenReturn(appResponse);

        AppointmentResponseDTO response = appointmentService.updateById(appRequest, 2, myUserDetails);
        assertEquals(response.getId(), oldAppointment.getId());
        assertNotSame(oldAppointment.getActivities().get(0), response.getActivities().get(0));
    }

    @Test(expected = UserNotFoundException.class)
    public void updateAppointmentByIdByADMINThrowUserNotFound() {
        AppointmentRequestDTO appRequest = new AppointmentRequestDTO(LocalDateTime.of(2021, 5, 5, 14, 15), Arrays.asList("Fitness"));
        Appointment oldAppointment = new Appointment(LocalDateTime.of(2021, 5, 5, 14, 15), LocalDateTime.of(2021, 05, 05, 16, 45), Arrays.asList(new Activity("Yoga")));

        when(appointmentRepository.findById(anyInt())).thenReturn(Optional.of(oldAppointment));
        appRequest.setUserId(2);
        when(myUserDetails.hasRole("ROLE_ADMIN")).thenReturn(true);
        when(userRepository.findById(anyInt())).thenThrow(UserNotFoundException.class);

        appointmentService.updateById(appRequest, 2, myUserDetails);
    }

    @Test(expected = AppointmentNotFoundException.class)
    public void updateAppointmentByIdThrowAppNotFound() {
        AppointmentRequestDTO appRequest = new AppointmentRequestDTO(LocalDateTime.of(2021, 5, 5, 14, 15), Arrays.asList("Fitness"));

        when(appointmentRepository.findById(anyInt())).thenThrow(AppointmentNotFoundException.class);

        appointmentService.updateById(appRequest, 2, myUserDetails);
    }

    @Test(expected = ForbiddenException.class)
    public void updateAppointmentByIdThrowForbiddenForUpdate() {
        AppointmentRequestDTO appRequest = new AppointmentRequestDTO(LocalDateTime.of(2021, 5, 5, 14, 15), Arrays.asList("Fitness"));
        Appointment oldAppointment = new Appointment(LocalDateTime.of(2021, 5, 5, 14, 15), LocalDateTime.of(2021, 05, 05, 16, 45), Arrays.asList(new Activity("Fitness")));

        when(appointmentRepository.findById(anyInt())).thenReturn(Optional.of(oldAppointment));
        appRequest.setUserId(2);
        when(myUserDetails.hasRole("ROLE_ADMIN")).thenReturn(false);

        appointmentService.updateById(appRequest, 2, myUserDetails);
    }

    @Test
    public void updateAppointmentByIdByUSER() {
        AppointmentResponseDTO appResponse = new AppointmentResponseDTO(LocalDateTime.of(2021, 6, 5, 14, 15), LocalDateTime.of(2021, 6, 05, 16, 45), Arrays.asList(new Activity("Fitness")));
        AppointmentRequestDTO appRequest = new AppointmentRequestDTO(LocalDateTime.of(2021, 6, 5, 14, 15), Arrays.asList("Fitness"));
        Appointment oldAppointment = new Appointment(LocalDateTime.of(2021, 6, 5, 14, 15), LocalDateTime.of(2021, 6, 05, 16, 45), Arrays.asList(new Activity("Yoga")));
        Appointment newAppointment = new Appointment(LocalDateTime.of(2021, 6, 5, 14, 15), LocalDateTime.of(2021, 6, 05, 16, 45), Arrays.asList(new Activity("Fitness")));
        Activity activity = new Activity("Fitness", 60, 5.00);

        when(appointmentRepository.findById(anyInt())).thenReturn(Optional.of(oldAppointment));
        when(myUserDetails.hasRole("ROLE_ADMIN")).thenReturn(false);
        oldAppointment.setUser(user);
        oldAppointment.getUser().setId(myUserDetails.getId());
        when(activityRepository.findByName(anyString())).thenReturn(Optional.of(activity));
        when(appointmentRepository.findAllByUserId(anyInt())).thenReturn(Arrays.asList(newAppointment));
        Mockito.when(modelMapper.map(any(), any())).thenReturn(appResponse);

        AppointmentResponseDTO response = appointmentService.updateById(appRequest, 2, myUserDetails);
        assertEquals(response.getId(), oldAppointment.getId());
        assertNotSame(response.getActivities().get(0), oldAppointment.getActivities().get(0));
    }

    @Test(expected = ForbiddenException.class)
    public void updateAppointmentByIdByUSERThrowForbidden() {
        AppointmentRequestDTO appRequest = new AppointmentRequestDTO(LocalDateTime.of(2021, 5, 5, 14, 15), Arrays.asList("Fitness"));
        Appointment oldAppointment = new Appointment(LocalDateTime.of(2021, 5, 5, 14, 15), LocalDateTime.of(2021, 05, 05, 16, 45), Arrays.asList(new Activity("Fitness")));

        when(appointmentRepository.findById(anyInt())).thenReturn(Optional.of(oldAppointment));
        when(myUserDetails.hasRole("ROLE_ADMIN")).thenReturn(false);
        oldAppointment.setUser(user);
        oldAppointment.getUser().setId(myUserDetails.getId() + 1);

        appointmentService.updateById(appRequest, 2, myUserDetails);
    }

    @Test(expected = ActivityNotFoundException.class)
    public void updateAppointmentByIdThrowActivityNotFound() {
        AppointmentRequestDTO appRequest = new AppointmentRequestDTO(LocalDateTime.of(2021, 5, 5, 14, 15), Arrays.asList("Fitness"));
        Appointment oldAppointment = new Appointment(LocalDateTime.of(2021, 5, 5, 14, 15), LocalDateTime.of(2021, 05, 05, 16, 45), Arrays.asList(new Activity("Fitness")));

        when(appointmentRepository.findById(anyInt())).thenReturn(Optional.of(oldAppointment));
        appRequest.setUserId(2);
        when(myUserDetails.hasRole("ROLE_ADMIN")).thenReturn(true);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        oldAppointment.setUser(user);
        when(activityRepository.findByName(anyString())).thenReturn(Optional.empty());

        appointmentService.updateById(appRequest, 2, myUserDetails);
    }

    @Test(expected = AppointmentExistsException.class)
    public void updateAppointmentByIdThrowExistingApp() {
        AppointmentRequestDTO appRequest = new AppointmentRequestDTO(LocalDateTime.of(2021, 5, 3, 14, 15), Arrays.asList("Yoga"));
        Appointment oldAppointment = new Appointment(LocalDateTime.of(2021, 5, 3, 14, 15), LocalDateTime.of(2021, 5, 3, 16, 45), Arrays.asList(new Activity("Fitness")));
        Appointment newAppointment = new Appointment(LocalDateTime.of(2021, 5, 3, 14, 15), LocalDateTime.of(2021, 5, 3, 16, 45), Arrays.asList(new Activity("Yoga")));
        Activity activity = new Activity("Fitness", 60, 5.00);

        when(appointmentRepository.findById(anyInt())).thenReturn(Optional.of(oldAppointment));
        when(myUserDetails.hasRole("ROLE_ADMIN")).thenReturn(true);
        appRequest.setUserId(2);
        when(userRepository.findById(anyInt())).thenReturn(Optional.of(user));
        oldAppointment.setUser(user);
        oldAppointment.setId(2);
        when(activityRepository.findByName(anyString())).thenReturn(Optional.of(activity));
        when(appointmentRepository.findAllByUserId(anyInt())).thenReturn(Arrays.asList(newAppointment));

        appointmentService.updateById(appRequest, 2, myUserDetails);
    }

    @Test
    public void deleteAppointmentByIdSuccess() {
        Appointment app = new Appointment(LocalDateTime.of(2021, 4, 3, 14, 15), LocalDateTime.of(2021, 4, 3, 16, 45), Arrays.asList(new Activity("Fitness")));

        when(appointmentRepository.findById(anyInt())).thenReturn(Optional.of(app));
        user.setId(myUserDetails.getId());
        app.setUser(user);
        doNothing().when(appointmentRepository).deleteById(anyInt());

        appointmentService.delete(1, myUserDetails);
        verify(appointmentRepository, times(1)).deleteById(1);
    }

    @Test(expected = ForbiddenException.class)
    public void deleteAppointmentByIdThrowForbiddenException() {
        Appointment app = new Appointment(LocalDateTime.of(2021, 4, 3, 14, 15), LocalDateTime.of(2021, 4, 3, 16, 45), Arrays.asList(new Activity("Fitness")));

        when(appointmentRepository.findById(anyInt())).thenReturn(Optional.of(app));
        user.setId(myUserDetails.getId() + 1);
        when(myUserDetails.hasRole("ROLE_ADMIN")).thenReturn(false);
        app.setUser(user);

        appointmentService.delete(1, myUserDetails);
        verify(appointmentRepository, times(0)).deleteById(1);
    }
}
