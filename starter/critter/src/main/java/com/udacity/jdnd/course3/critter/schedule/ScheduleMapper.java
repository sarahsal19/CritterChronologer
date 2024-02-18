package com.udacity.jdnd.course3.critter.schedule;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ScheduleMapper {

    public ScheduleDTO mapToScheduleDTO(Schedule schedule) {
        List<Long> employeeIds = new ArrayList<>();
        List<Long> petIds = new ArrayList<>();
        ScheduleDTO scheduleDTO = new ScheduleDTO();
        BeanUtils.copyProperties(schedule, scheduleDTO);
        if (schedule.getEmployees() != null) {
            schedule.getEmployees().forEach(employee -> employeeIds.add(employee.getId()));
            scheduleDTO.setEmployeeIds(employeeIds);
        }

        if (schedule.getPets() != null) {
            schedule.getPets().forEach(pet -> petIds.add(pet.getId()));
            scheduleDTO.setPetIds(petIds);
        }

        return scheduleDTO;
    }
}
