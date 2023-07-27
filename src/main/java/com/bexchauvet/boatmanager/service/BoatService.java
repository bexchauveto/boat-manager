package com.bexchauvet.boatmanager.service;

import com.bexchauvet.boatmanager.domain.Boat;
import com.bexchauvet.boatmanager.rest.dto.MessageDTO;
import com.bexchauvet.boatmanager.service.dto.BoatDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BoatService {

    List<Boat> getAll();

    Page<Boat> getAllPage(Pageable pageable);

    Boat getById(String id);

    MessageDTO create(BoatDTO boat);

    MessageDTO update(String id, BoatDTO boat);

    MessageDTO delete(String id);

    Boolean exists(String id);

    Boolean hasImage(String id);

    void setImage(String id);
}
