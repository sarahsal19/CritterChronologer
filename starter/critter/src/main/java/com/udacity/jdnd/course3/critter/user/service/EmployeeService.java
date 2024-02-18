package com.udacity.jdnd.course3.critter.user.service;


import com.udacity.jdnd.course3.critter.user.dto.EmployeeDTO;
import com.udacity.jdnd.course3.critter.user.dto.EmployeeRequestDTO;
import com.udacity.jdnd.course3.critter.user.entity.Employee;
import com.udacity.jdnd.course3.critter.user.mapper.UserMapper;
import com.udacity.jdnd.course3.critter.user.repository.EmployeeRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.DayOfWeek;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
@Transactional
public class EmployeeService {
    private final EmployeeRepository employeeRepository;
    private final UserMapper userMapper;

    public EmployeeDTO createEmployee(EmployeeDTO employeeRequest) {
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeRequest, employee);
        employeeRepository.save(employee);
        employeeRequest.setId(employee.getId());
        return employeeRequest;
    }

    public EmployeeDTO getEmployee(long employeeId) {
        return userMapper.mapToEmployeeDTO(employeeRepository.findById(employeeId).orElseThrow(()-> new IllegalArgumentException("Employee with this id was not found")));
    }

    public void setAvailability(Set<DayOfWeek> daysAvailable, long employeeId) {
        Employee employee = employeeRepository.findById(employeeId).orElseThrow(()-> new IllegalArgumentException("Employee with this id was not found"));
        employee.setDaysAvailable(daysAvailable);
        employeeRepository.save(employee);
    }

    public List<EmployeeDTO> findEmployeesForService(EmployeeRequestDTO employeeDTO) {
        List<Employee> employees = employeeRepository.findByDaysAvailableContaining(employeeDTO.getDate().getDayOfWeek());
        List<EmployeeDTO> employeeDTOList = new ArrayList<>();

        employees.forEach((employee -> {
            if (employee.getSkills().containsAll(employeeDTO.getSkills())) {
                employeeDTOList.add(userMapper.mapToEmployeeDTO(employee));
            }
        }));
        return employeeDTOList;
    }

}

