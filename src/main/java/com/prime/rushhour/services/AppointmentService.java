package com.prime.rushhour.services;

import com.prime.rushhour.entities.Activity;
import com.prime.rushhour.entities.Appointment;
import com.prime.rushhour.entities.User;
import com.prime.rushhour.exception.*;
import com.prime.rushhour.models.AppointmentRequestDTO;
import com.prime.rushhour.models.AppointmentResponseDTO;
import com.prime.rushhour.repository.ActivityRepository;
import com.prime.rushhour.repository.AppointmentRepository;
import com.prime.rushhour.repository.UserRepository;
import com.prime.rushhour.security.MyUserDetails;
import org.modelmapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AppointmentService {
    private AppointmentRepository appointmentRepository;
    private UserRepository userRepository;
    private ActivityRepository activityRepository;
    private ModelMapper modelMapper;

    public List<AppointmentResponseDTO> getAll(Pageable paging, MyUserDetails currentUser) {
        if (currentUser.hasRole("ROLE_ADMIN")) {
            Page<Appointment> pagedResult = appointmentRepository.findAll(paging);
            return pagedResult.stream()
                    .map(a -> modelMapper.map(a, AppointmentResponseDTO.class))
                    .collect(Collectors.toList());
        } else {
            Page<Appointment> pagedResult = appointmentRepository.findAllByUserId(currentUser.getId(), paging);
            return pagedResult.stream()
                    .map(a -> modelMapper.map(a, AppointmentResponseDTO.class))
                    .collect(Collectors.toList());
        }
    }

    public AppointmentResponseDTO getById(int id, MyUserDetails currentUser) {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(() -> new AppointmentNotFoundException(id));
        if (appointment.getUser().getId() == currentUser.getId() || currentUser.hasRole("ROLE_ADMIN")) {
            return modelMapper.map(appointment, AppointmentResponseDTO.class);
        } else {
            throw new ForbiddenException();
        }
    }

    public AppointmentResponseDTO add(AppointmentRequestDTO dto, int currentId) {
        Appointment app = mapAppointments(dto);
        Optional<User> currentUser = userRepository.findById(currentId);
        app.setUser(currentUser.get());
        Appointment appointment = calculateEndDate(app);
        checkForOverlap(appointment, currentId);
        appointmentRepository.save(appointment);
        return modelMapper.map(appointment, AppointmentResponseDTO.class);
    }

    public AppointmentResponseDTO updateById(AppointmentRequestDTO dto, int id, MyUserDetails currentUser) {
        Appointment oldAppointment = appointmentRepository.findById(id).orElseThrow(() -> new AppointmentNotFoundException(id));
        Appointment app = mapAppointments(dto);
        if (currentUser.hasRole("ROLE_ADMIN") && dto.getUserId() != 0) {
            User user = userRepository.findById(dto.getUserId()).orElseThrow(UserNotFoundException::new);
            app.setUser(user);
        } else if (!currentUser.hasRole("ROLE_ADMIN") && dto.getUserId() != 0) {
            throw new ForbiddenException("You have no right to set userId!");
        } else if (oldAppointment.getUser().getId() == currentUser.getId()) {
            app.setUser(oldAppointment.getUser());
        } else {
            throw new ForbiddenException();
        }
        app.setId(oldAppointment.getId());
        Appointment newApp = calculateEndDate(app);
        checkForOverlap(newApp, app.getUser().getId());
        appointmentRepository.save(newApp);
        return modelMapper.map(newApp, AppointmentResponseDTO.class);
    }

    private void checkForOverlap(Appointment appointment, int userId) {
        for (Appointment existingApp : appointmentRepository.findAllByUserId(userId)) {
            if (appointment.getId() != existingApp.getId()) {
                if (!((appointment.getStartDate().isAfter(LocalDateTime.now()) && appointment.getEndDate().isBefore(existingApp.getStartDate())) ||
                        (appointment.getStartDate().isAfter(LocalDateTime.now()) && appointment.getStartDate().isAfter(existingApp.getEndDate())))) {
                    throw new AppointmentExistsException();
                }
            }
        }
    }

    private Appointment calculateEndDate(Appointment app) {
        List<Activity> list = new ArrayList<>();
        int totalMinutes = 0;
        for (Activity activity : app.getActivities()) {
            Optional<Activity> currentActivity = activityRepository.findByName(activity.getName());
            if (currentActivity.isPresent()) {
                list.add(currentActivity.get());
                totalMinutes = totalMinutes + currentActivity.get().getMinutes();
            } else {
                throw new ActivityNotFoundException();
            }
        }
        app.setActivities(list);
        app.setEndDate(app.getStartDate().plusMinutes(totalMinutes));
        return app;
    }

    public void delete(int id, MyUserDetails currentUser) {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(() -> new AppointmentNotFoundException(id));
        if (appointment.getUser().getId() == currentUser.getId() || currentUser.hasRole("ROLE_ADMIN")) {
            appointmentRepository.deleteById(id);
        } else {
            throw new ForbiddenException();
        }
    }

    private Appointment mapAppointments(AppointmentRequestDTO dto) {
        Appointment app = new Appointment();
        List<Activity> currentActivities = new ArrayList<>();
        for (String name : dto.getActivitiesName()) {
            Activity activity = new Activity(name);
            currentActivities.add(activity);
        }
        app.setStartDate(dto.getStartDate());
        app.setActivities(currentActivities);
        return app;
    }

    @Autowired
    public void setAppointmentRepository(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setActivityRepository(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }

    @Autowired
    public void setModelMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }
}
