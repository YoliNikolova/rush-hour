package com.prime.rushhour.controllers;

import com.prime.rushhour.models.AppointmentRequestDTO;
import com.prime.rushhour.models.AppointmentResponseDTO;
import com.prime.rushhour.security.MyUserDetails;
import com.prime.rushhour.services.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    private AppointmentService appointmentService;

    @GetMapping
    public List<AppointmentResponseDTO> getAllAppointments(@AuthenticationPrincipal MyUserDetails currentUser) {
        return appointmentService.getAll(currentUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> getAppById(@PathVariable int id, @AuthenticationPrincipal MyUserDetails currentUser) {
        AppointmentResponseDTO app = appointmentService.getById(id, currentUser);
        return ResponseEntity.ok(app);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponseDTO addNewAppointment(@RequestBody AppointmentRequestDTO app, @AuthenticationPrincipal MyUserDetails currentUser) {
        return appointmentService.add(app, currentUser.getId());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AppointmentResponseDTO> updateAppointment(@RequestBody AppointmentRequestDTO app, @PathVariable int id, @AuthenticationPrincipal MyUserDetails currentUser) {
        AppointmentResponseDTO updateApp = appointmentService.updateById(app, id, currentUser);
        return ResponseEntity.ok(updateApp);
    }

    @DeleteMapping("/{id}")
    public void deleteAppointmentById(@PathVariable int id, @AuthenticationPrincipal MyUserDetails currentUser) {
        appointmentService.delete(id, currentUser);
    }

    @Autowired
    public void setAppointmentService(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }
}
