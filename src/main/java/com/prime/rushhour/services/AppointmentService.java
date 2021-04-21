package com.prime.rushhour.services;

import com.prime.rushhour.entities.Activity;
import com.prime.rushhour.entities.Appointment;
import com.prime.rushhour.entities.User;
import com.prime.rushhour.exception.ActivityNotFoundException;
import com.prime.rushhour.exception.AppointmentNotFoundException;
import com.prime.rushhour.exception.ForbiddenException;
import com.prime.rushhour.models.AppointmentDTO;
import com.prime.rushhour.repository.ActivityRepository;
import com.prime.rushhour.repository.AppointmentRepository;
import com.prime.rushhour.repository.UserRepository;
import com.prime.rushhour.security.MyUserDetails;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    public List<AppointmentDTO> getAll() {
        return appointmentRepository.findAll().stream()
                .map(a -> modelMapper.map(a, AppointmentDTO.class))
                .collect(Collectors.toList());
    }

    public AppointmentDTO getById(int id, MyUserDetails currentUser) {
        Appointment appointment = appointmentRepository.findById(id).orElseThrow(AppointmentNotFoundException::new);
        if (appointment.getUser().getId() == currentUser.getId() || currentUser.hasRole("ROLE_ADMIN")) {
            return modelMapper.map(appointment, AppointmentDTO.class);
        } else {
            throw new ForbiddenException();
        }
    }

    public List<AppointmentDTO> getAllByUserId(int id) {
        List<Appointment> appointments = appointmentRepository.findAllByUserId(id);
        return appointments.stream()
                .map(a -> modelMapper.map(a, AppointmentDTO.class))
                .collect(Collectors.toList());
    }

    public AppointmentDTO add(AppointmentDTO dto, int currentId) {
        Appointment app = modelMapper.map(dto, Appointment.class);
        Optional<User> getCurrentUser = userRepository.findById(currentId);
        Appointment appointment = setActivities(app);
        appointment.setUser(getCurrentUser.get());
        appointmentRepository.save(appointment);
        return modelMapper.map(appointment, AppointmentDTO.class);
    }

    public AppointmentDTO updateById(AppointmentDTO dto, int id, MyUserDetails currentUser) {
        Appointment oldAppointment = appointmentRepository.findById(id).orElseThrow(AppointmentNotFoundException::new);
        if (oldAppointment.getUser().getId() == currentUser.getId() || currentUser.hasRole("ROLE_ADMIN")) {
            Appointment app = modelMapper.map(dto, Appointment.class);
            app.setUser(oldAppointment.getUser());
            app.setId(oldAppointment.getId());
            Appointment newApp = setActivities(app);
            appointmentRepository.save(newApp);
            return modelMapper.map(newApp, AppointmentDTO.class);
        } else {
            throw new ForbiddenException();
        }
    }

    private Appointment setActivities(Appointment app) {
        List<Activity> list = new ArrayList<>();
        for (Activity activity : app.getActivities()) {
            Optional<Activity> currentActivity = activityRepository.findByName(activity.getName());
            currentActivity.ifPresent(list::add);
        }
        if (list.isEmpty()) {
            throw new ActivityNotFoundException(); // not found these activities???
        }
        app.setActivities(list);
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
