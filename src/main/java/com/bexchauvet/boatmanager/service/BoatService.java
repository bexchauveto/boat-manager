package com.bexchauvet.boatmanager.service;

import com.bexchauvet.boatmanager.domain.Boat;
import com.bexchauvet.boatmanager.rest.dto.BoatDTO;
import com.bexchauvet.boatmanager.rest.dto.MessageDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoatService {

    List<Boat> getAll();

    Page<Boat> getAllPage(Pageable pageable);

    Boat getById(String id);

    Boat getByName(String name);

    MessageDTO create(BoatDTO boat);

    MessageDTO update(String id, BoatDTO boat);

    MessageDTO delete(String id);

    Boolean exists(String id);

    Boolean hasImage(String id);

    void setImage(String id);

    void updatePositions();
}
