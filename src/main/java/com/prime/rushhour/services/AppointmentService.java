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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ModelMapper modelMapper;

    public List<AppointmentResponseDTO> getAll(MyUserDetails currentUser) {
        if (currentUser.hasRole("ROLE_ADMIN")) {
            return appointmentRepository.findAll().stream()
                    .map(a -> modelMapper.map(a, AppointmentResponseDTO.class))
                    .collect(Collectors.toList());
        } else {
            List<Appointment> appointments = appointmentRepository.findAllByUserId(currentUser.getId());
            return appointments.stream()
                    .map(a -> modelMapper.map(a, AppointmentResponseDTO.class))
                    .collect(Collectors.toList());
        }
    }

    public AppointmentResponseDTO getById(int id, MyUserDetails currentUser) {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(AppointmentNotFoundException::new);
        if (appointment.getUser().getId() == currentUser.getId() || currentUser.hasRole("ROLE_ADMIN")) {
            return modelMapper.map(appointment, AppointmentResponseDTO.class);
        } else {
            throw new ForbiddenException();
        }
    }

    public AppointmentResponseDTO add(AppointmentRequestDTO dto, int currentId) {
        Appointment app = modelMapper.map(dto, Appointment.class);
        Optional<User> currentUser = userRepository.findById(currentId);
        app.setUser(currentUser.get());
        Appointment appointment = calculateEndDate(app);
        checkForOverlap(appointment, currentId);
        appointmentRepository.save(appointment);
        return modelMapper.map(appointment, AppointmentResponseDTO.class);
    }

    public AppointmentResponseDTO updateById(AppointmentRequestDTO dto, int id, MyUserDetails currentUser) {
        Appointment oldAppointment = appointmentRepository.findById(id).orElseThrow(AppointmentNotFoundException::new);
        Appointment app = modelMapper.map(dto, Appointment.class);
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
                totalMinutes = totalMinutes + currentActivity.get().getDuration();
            }
        }
        if (list.isEmpty()) {
            throw new ActivityNotFoundException();
        }
        app.setActivities(list);
        app.setEndDate(app.getStartDate().plusMinutes(totalMinutes));
        return app;
    }

    public void delete(int id, MyUserDetails currentUser) {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(AppointmentNotFoundException::new);
        if (appointment.getUser().getId() == currentUser.getId() || currentUser.hasRole("ROLE_ADMIN")) {
            appointmentRepository.deleteById(id);
        } else {
            throw new ForbiddenException();
        }
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
}
