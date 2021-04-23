package com.prime.rushhour.repository;

import com.prime.rushhour.entities.Appointment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment,Integer> {
   Page<Appointment> findAllByUserId(int id, Pageable pageable);
   List<Appointment> findAllByUserId(int id);
}
