package com.udacity.jdnd.course3.critter.user.service;

import com.udacity.jdnd.course3.critter.pet.Pet;
import com.udacity.jdnd.course3.critter.pet.PetRepository;
import com.udacity.jdnd.course3.critter.user.dto.CustomerDTO;
import com.udacity.jdnd.course3.critter.user.entity.Customer;
import com.udacity.jdnd.course3.critter.user.mapper.UserMapper;
import com.udacity.jdnd.course3.critter.user.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final PetRepository petRepository;
    private final UserMapper userMapper;
    public CustomerDTO createCustomer(CustomerDTO customerRequest) {
        List<Pet> pets = new ArrayList<>();
        if (customerRequest.getPetIds() != null) {
            customerRequest.getPetIds().forEach((pet) -> {
                pets.add(petRepository.findById(pet).orElseThrow(()->new IllegalArgumentException("Pet with this id was not found")));
            });
        }
        Customer customer = new Customer();
        BeanUtils.copyProperties(customerRequest, customer);
        customer.setPets(pets);
        customerRepository.save(customer);
        return userMapper.mapToCustomerDTO(customer);
    }

    public List<CustomerDTO> getAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        List<CustomerDTO> customerDTOList = new ArrayList<>();
        customers.forEach((customer) -> {
            customerDTOList.add(userMapper.mapToCustomerDTO(customer));
        });
        return customerDTOList;
    }

    public CustomerDTO getOwnerByPet(long id) {
        Customer customer = customerRepository.findByPetsId(id);
        return userMapper.mapToCustomerDTO(customer);
    }

    public Customer getCustomer(long id) {
        return customerRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Customer with this id was not found"));
    }

    public void addPet(Customer customer, Pet pet){
        if (customer.getPets() == null) {
            List<Pet> petList = new ArrayList<>();
            petList.add(pet);
            customer.setPets(petList);
        } else {
            customer.getPets().add(pet);
        }

        customerRepository.save(customer);
    }
}