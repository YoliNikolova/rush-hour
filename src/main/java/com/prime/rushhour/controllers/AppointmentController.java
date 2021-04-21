package com.prime.rushhour.controllers;

import com.prime.rushhour.models.AppointmentDTO;
import com.prime.rushhour.security.MyUserDetails;
import com.prime.rushhour.services.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private AppointmentService appointmentService;

    @Secured("ROLE_ADMIN")
    @GetMapping
    public List<AppointmentDTO> getAllAppointments() {
        return appointmentService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentDTO> getAppById(@PathVariable int id, @AuthenticationPrincipal MyUserDetails currentUser) {
        AppointmentDTO app = appointmentService.getById(id, currentUser);
        return ResponseEntity.ok(app);
    }

    @GetMapping("/all")
    public List<AppointmentDTO> getAllAppByUserId(@AuthenticationPrincipal MyUserDetails currentUser) {
        return appointmentService.getAllByUserId(currentUser.getId());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addNewActivity(@RequestBody AppointmentDTO app, @AuthenticationPrincipal MyUserDetails currentUser) {
        appointmentService.add(app, currentUser.getId());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentDTO> updateUser(@RequestBody AppointmentDTO app, @PathVariable int id, @AuthenticationPrincipal MyUserDetails currentUser) {
        AppointmentDTO updateApp = appointmentService.updateById(app, id, currentUser);
        return ResponseEntity.ok(updateApp);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable int id, @AuthenticationPrincipal MyUserDetails currentUser) {
        appointmentService.delete(id, currentUser);
    }

    @Autowired
    public void setAppointmentService(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }
}
