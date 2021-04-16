package com.prime.rushhour.services;

import com.prime.rushhour.entities.Activity;
import com.prime.rushhour.models.ActivityDTO;
import com.prime.rushhour.repository.ActivityRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityService {

    private ActivityRepository activityRepository;

    @Autowired
    private ModelMapper modelMapper;

    public List<ActivityDTO> getAll() {
        return activityRepository.findAll().stream()
                .map(a -> modelMapper.map(a, ActivityDTO.class))
                .collect(Collectors.toList());
    }

    public ActivityDTO getById(int id) {
        Activity activity = activityRepository.getOne(id);
        if (!activityRepository.existsById(id)) {
            return null;
        }
        ActivityDTO activityDTO = modelMapper.map(activity, ActivityDTO.class);
        return activityDTO;
    }

    public void add(ActivityDTO dto) {
        Activity activity = modelMapper.map(dto, Activity.class);
        activityRepository.save(activity);
    }

    public ActivityDTO updateById(ActivityDTO dto, int id) {
        Activity activity = modelMapper.map(dto, Activity.class);
        activity.setId(id);
        activityRepository.save(activity);
        return modelMapper.map(activity, ActivityDTO.class);
    }

    public void delete(int id) {
        activityRepository.deleteById(id);
    }

    @Autowired
    public void setActivityRepository(ActivityRepository activityRepository) {
        this.activityRepository = activityRepository;
    }
}
