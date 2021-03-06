package com.prime.rushhour.controllers;

import com.prime.rushhour.models.ActivityDTO;
import com.prime.rushhour.services.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/activities")
public class ActivityController {
    private ActivityService activityService;

    @GetMapping("/{id}")
    public ResponseEntity<ActivityDTO> getActivityById(@PathVariable int id) {
        ActivityDTO activity = activityService.getById(id);
        return ResponseEntity.ok(activity);
    }

    @GetMapping()
    public List<ActivityDTO> getAllActivities(Pageable paging) {
        return activityService.getAll(paging);
    }

    @Secured("ROLE_ADMIN")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ActivityDTO addNewActivity(@RequestBody @Valid ActivityDTO activity) {
        return activityService.add(activity);
    }

    @Secured("ROLE_ADMIN")
    @PutMapping("/{id}")
    public ResponseEntity<ActivityDTO> updateUser(@RequestBody @Valid ActivityDTO activity, @PathVariable int id) {
        ActivityDTO updateActivity = activityService.updateById(activity, id);
        return ResponseEntity.ok(updateActivity);
    }

    @Secured("ROLE_ADMIN")
    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable int id) {
        activityService.delete(id);
    }

    @Autowired
    public void setActivityService(ActivityService activityService) {
        this.activityService = activityService;
    }
}
