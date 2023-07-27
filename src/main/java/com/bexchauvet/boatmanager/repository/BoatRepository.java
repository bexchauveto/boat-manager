package com.bexchauvet.boatmanager.repository;


import com.bexchauvet.boatmanager.domain.Boat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BoatRepository extends JpaRepository<Boat, String> {

    Optional<Boat> findByName(String name);
}
