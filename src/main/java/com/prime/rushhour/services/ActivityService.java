package com.prime.rushhour.services;

import com.prime.rushhour.entities.Activity;
import com.prime.rushhour.entities.Appointment;
import com.prime.rushhour.exception.ActivityNotFoundException;
import com.prime.rushhour.models.ActivityDTO;
import com.prime.rushhour.repository.ActivityRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ActivityService {

    private ActivityRepository activityRepository;
    private ModelMapper modelMapper;

    public List<ActivityDTO> getAll(Pageable paging) {
        Page<Activity> pagedResult = activityRepository.findAll(paging);
        return pagedResult.stream()
                .map(a -> modelMapper.map(a, ActivityDTO.class))
                .collect(Collectors.toList());
    }

    public ActivityDTO getById(int id) {
        Activity activity = activityRepository.findById(id).orElseThrow(ActivityNotFoundException::new);
        return modelMapper.map(activity, ActivityDTO.class);
    }

    public ActivityDTO add(ActivityDTO dto) {
        Activity activity = modelMapper.map(dto, Activity.class);
        activityRepository.save(activity);
        return modelMapper.map(activity,ActivityDTO.class);
    }

    public ActivityDTO updateById(ActivityDTO dto, int id) {
        Activity activity = modelMapper.map(dto, Activity.class);
        if (!activityRepository.existsById(id)) {
            throw new ActivityNotFoundException();
        }
        activity.setId(id);
        activityRepository.save(activity);
        return modelMapper.map(activity, ActivityDTO.class);
    }

    public void delete(int id) {
        if (!activityRepository.existsById(id)) {
            throw new ActivityNotFoundException();
        }
        activityRepository.deleteById(id);
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
