package com.udacity.jdnd.course3.critter.schedule;

import com.udacity.jdnd.course3.critter.pet.Pet;
import com.udacity.jdnd.course3.critter.pet.PetDTO;
import com.udacity.jdnd.course3.critter.pet.PetRepository;
import com.udacity.jdnd.course3.critter.pet.PetService;
import com.udacity.jdnd.course3.critter.user.dto.EmployeeDTO;
import com.udacity.jdnd.course3.critter.user.entity.Customer;
import com.udacity.jdnd.course3.critter.user.entity.Employee;
import com.udacity.jdnd.course3.critter.user.repository.EmployeeRepository;
import com.udacity.jdnd.course3.critter.user.service.CustomerService;
import com.udacity.jdnd.course3.critter.user.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

import static org.hibernate.bytecode.BytecodeLogger.LOGGER;

@Service
@Transactional
@AllArgsConstructor
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    private final PetService petService;
    private final EmployeeService employeeService;
    private final CustomerService customerService;
    private final PetRepository petRepository;
    private final EmployeeRepository employeeRepository;

    public ScheduleDTO createSchedule(ScheduleDTO scheduleRequest) {
        Schedule schedule = new Schedule();
        List<Pet> pets = new ArrayList<>();
        List<Employee> employees = new ArrayList<>();

        if (scheduleRequest.getPetIds() != null) {
            scheduleRequest.getPetIds().forEach((pet) -> {
                pets.add(petRepository.findById(pet).get());
            });
        }

        if (scheduleRequest.getEmployeeIds() != null) {
            scheduleRequest.getEmployeeIds().forEach((employee) -> {
                employees.add(employeeRepository.findById(employee).get());
            });
        }

        BeanUtils.copyProperties(scheduleRequest, schedule);
        schedule.setPets(pets);
        schedule.setEmployees(employees);
        scheduleRepository.save(schedule);
        scheduleRequest.setId(schedule.getId());
        return scheduleRequest;
    }

    public List<ScheduleDTO> getAllSchedules() {
        List<Schedule> schedules = scheduleRepository.findAll();
        List<ScheduleDTO> scheduleDTOList = new ArrayList<>();

        schedules.forEach(schedule -> scheduleDTOList.add(mapToScheduleDTO(schedule)));
        return scheduleDTOList;
    }

    public List<ScheduleDTO> getScheduleForPet(long petId) {
        PetDTO petDTO = petService.getPet(petId);
        Pet pet = new Pet();
        BeanUtils.copyProperties(petDTO, pet);
        pet.setCustomer(customerService.getCustomer(petDTO.getOwnerId()));

        List<Schedule> schedules = scheduleRepository.findByPetsContaining(pet);
        List<ScheduleDTO> scheduleDTOList = new ArrayList<>();

        schedules.forEach(schedule -> scheduleDTOList.add(mapToScheduleDTO(schedule)));
        return scheduleDTOList;
    }

    public List<ScheduleDTO> getScheduleForEmployee(long employeeId){
        EmployeeDTO employeeDTO = employeeService.getEmployee(employeeId);
        Employee employee = new Employee();
        BeanUtils.copyProperties(employeeDTO, employee);
        List<Schedule> schedules = scheduleRepository.findByEmployeesContaining(employee);
        List<ScheduleDTO> scheduleDTOList = new ArrayList<>();

        schedules.forEach(schedule -> scheduleDTOList.add(mapToScheduleDTO(schedule)));
        return scheduleDTOList;
    }

    public List<ScheduleDTO> getScheduleForCustomer(long customerId){
        Customer customer = customerService.getCustomer(customerId);
        List<Schedule> schedules = scheduleRepository.findByPetsIn(customer.getPets());
        List<ScheduleDTO> scheduleDTOList = new ArrayList<>();

        schedules.forEach(schedule -> scheduleDTOList.add(mapToScheduleDTO(schedule)));
        return scheduleDTOList;
    }

    private ScheduleDTO mapToScheduleDTO(Schedule schedule) {
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