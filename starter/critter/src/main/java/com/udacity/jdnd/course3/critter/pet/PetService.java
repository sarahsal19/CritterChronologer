package com.udacity.jdnd.course3.critter.pet;

import com.udacity.jdnd.course3.critter.user.entity.Customer;
import com.udacity.jdnd.course3.critter.user.service.CustomerService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@AllArgsConstructor
public class PetService {

    public final PetRepository petRepository;
    public final PetMapper petMapper;
    private final CustomerService customerService;
    public PetDTO createPet(PetDTO petDTO) {
        Pet pet = petMapper.mapToPet(petDTO);
        Customer customer = customerService.getCustomer(petDTO.getOwnerId());
        pet.setCustomer(customer);
        petRepository.save(pet);
        petDTO.setId(pet.getId());
        customerService.addPet(pet.getCustomer(), pet);
        return petDTO;
    }

    public PetDTO getPet(long id){
        Pet pet = petRepository.findById(id).orElseThrow(()->new IllegalArgumentException("Pet with this id was not found"));
        return petMapper.mapToPetDTO(pet);
    }
    public List<PetDTO> getPets(){
        List<Pet> petList = petRepository.findAll();
        List<PetDTO> petDTOList = new ArrayList<>();
        petList.forEach((pet) -> {
            petDTOList.add(petMapper.mapToPetDTO(pet));
        });
        return petDTOList;
    }

    public List<PetDTO> getPetsByOwner(long ownerId){
        List<Pet> petList = petRepository.findAllByCustomerId(ownerId);
        List<PetDTO> petDTOList = new ArrayList<>();
        petList.forEach((pet) -> {
            petDTOList.add(petMapper.mapToPetDTO(pet));
        });
        return petDTOList;
    }

}
