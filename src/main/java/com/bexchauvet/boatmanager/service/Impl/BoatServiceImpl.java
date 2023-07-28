package com.bexchauvet.boatmanager.service.Impl;

import com.bexchauvet.boatmanager.domain.Boat;
import com.bexchauvet.boatmanager.error.exception.BoatNotFoundException;
import com.bexchauvet.boatmanager.repository.BoatRepository;
import com.bexchauvet.boatmanager.rest.dto.BoatDTO;
import com.bexchauvet.boatmanager.rest.dto.MessageDTO;
import com.bexchauvet.boatmanager.service.BoatService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class BoatServiceImpl implements BoatService {

    private BoatRepository boatRepository;

    public List<Boat> getAll() {
        return this.boatRepository.findAll();
    }

    public Page<Boat> getAllPage(Pageable pageable) {
        return this.boatRepository.findAll(pageable);
    }

    public Boat getById(String id) throws BoatNotFoundException {
        return this.boatRepository.findById(id).orElseThrow(() -> new BoatNotFoundException(id));
    }

    public MessageDTO create(BoatDTO boatDTO) {
        Boat boat = new Boat();
        boat.setName(boatDTO.getName());
        boat.setDescription(boatDTO.getDescription());
        boat.setHasImage(false);
        boat = this.boatRepository.save(boat);
        return new MessageDTO(String.format("Boat information with ID %s has been created", boat.getId()),
                HttpStatus.CREATED, boat);
    }

    public MessageDTO update(String id, BoatDTO boatDTO) throws BoatNotFoundException {
        Optional<Boat> optionalBoat = this.boatRepository.findById(id);
        if (optionalBoat.isPresent()) {
            optionalBoat.get().setName(boatDTO.getName());
            optionalBoat.get().setDescription(boatDTO.getDescription());
            this.boatRepository.save(optionalBoat.get());
            return new MessageDTO(String.format("Boat information with ID %s has been updated", id), HttpStatus.OK, id);
        } else {
            throw new BoatNotFoundException(id);
        }
    }

    public MessageDTO delete(String id) throws BoatNotFoundException {
        Optional<Boat> optionalBoat = this.boatRepository.findById(id);
        if (optionalBoat.isPresent()) {
            this.boatRepository.delete(optionalBoat.get());
            return new MessageDTO(String.format("Boat information with ID %s has been deleted", id), HttpStatus.OK, id);
        } else {
            throw new BoatNotFoundException(id);
        }
    }

    public Boolean exists(String id) {
        return this.boatRepository.findById(id).isPresent();
    }

    public Boolean hasImage(String id) {
        Optional<Boat> boat = this.boatRepository.findById(id);
        return boat.isPresent() && boat.get().getHasImage();
    }

    public void setImage(String id) throws BoatNotFoundException {
        Optional<Boat> optionalBoat = this.boatRepository.findById(id);
        if (optionalBoat.isPresent()) {
            optionalBoat.get().setHasImage(true);
            this.boatRepository.save(optionalBoat.get());
        } else {
            throw new BoatNotFoundException(id);
        }
    }

}
