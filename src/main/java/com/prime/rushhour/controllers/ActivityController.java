package com.prime.rushhour.controllers;

import com.prime.rushhour.models.ActivityDTO;
import com.prime.rushhour.services.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/activities")
public class ActivityController {
    private ActivityService activityService;

    @GetMapping("/{id}")
    public ResponseEntity<ActivityDTO> getActivityById(@PathVariable int id){
        ActivityDTO activity = activityService.getById(id);
        if(activity == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(activity);
    }

    @GetMapping
    public List<ActivityDTO> getAllActivities(){
        return activityService.getAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void addNewActivity(@RequestBody ActivityDTO activity){
        activityService.add(activity);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ActivityDTO> updateUser(@RequestBody ActivityDTO activity, @PathVariable int id) {
        ActivityDTO updateActivity = activityService.updateById(activity, id);
        if (updateActivity == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(updateActivity);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable int id) {
        if (!activityService.delete(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Autowired
    public void setActivityService(ActivityService activityService) {
        this.activityService = activityService;
    }
}
