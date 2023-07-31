package com.bexchauvet.boatmanager.service.Impl;

import com.bexchauvet.boatmanager.domain.Boat;
import com.bexchauvet.boatmanager.error.exception.BoatNotFoundException;
import com.bexchauvet.boatmanager.error.exception.ZylaBoatIMONotFoundException;
import com.bexchauvet.boatmanager.error.exception.ZylaServiceUnavailableException;
import com.bexchauvet.boatmanager.repository.BoatRepository;
import com.bexchauvet.boatmanager.rest.dto.BoatDTO;
import com.bexchauvet.boatmanager.rest.dto.MessageDTO;
import com.bexchauvet.boatmanager.service.BoatService;
import com.bexchauvet.boatmanager.service.dto.ZylaShipInformationResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.*;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class BoatServiceImpl implements BoatService {

    private final BoatRepository boatRepository;
    private final WebClient webClient;
    private final String apiKey;

    public BoatServiceImpl(BoatRepository boatRepository, WebClient webClient,
                           @Value("${zyla.api-key}") String apiKey) {
        this.boatRepository = boatRepository;
        this.webClient = webClient;
        this.apiKey = apiKey;
    }

    public List<Boat> getAll() {
        return this.boatRepository.findAll();
    }

    public Page<Boat> getAllPage(Pageable pageable) {
        return this.boatRepository.findAll(pageable);
    }

    public Boat getById(String id) throws BoatNotFoundException {
        return this.boatRepository.findById(id).orElseThrow(() -> new BoatNotFoundException(id));
    }

    public Boat getByName(String name) throws BoatNotFoundException {
        return this.boatRepository.findByName(name).orElseThrow(() -> new BoatNotFoundException(name));
    }

    public MessageDTO create(BoatDTO boatDTO) {
        Boat boat = new Boat();
        boat.setName(boatDTO.getName());
        boat.setDescription(boatDTO.getDescription());
        boat.setHasImage(false);
        if (boatDTO.getImoCode() != null) {
            boat.setImoCode(boatDTO.getImoCode());
        }
        boat = this.boatRepository.save(boat);
        if (boatDTO.getImoCode() != null) {
            updateData(boat);
        }
        return new MessageDTO(String.format("Boat information with ID %s has been created", boat.getId()),
                HttpStatus.CREATED, boat);
    }

    public MessageDTO update(String id, BoatDTO boatDTO) throws BoatNotFoundException {
        Optional<Boat> optionalBoat = this.boatRepository.findById(id);
        if (optionalBoat.isPresent()) {
            optionalBoat.get().setName(boatDTO.getName());
            optionalBoat.get().setDescription(boatDTO.getDescription());
            this.boatRepository.save(optionalBoat.get());
            if (boatDTO.getImoCode() != null) {
                updateData(optionalBoat.get());
            }
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

    @Async("boatUpdateExecutor")
    private void updateData(Boat boat) {
        Mono<ZylaShipInformationResponseDTO> shipInformationResponseDTOMono = webClient.get()
                .uri("https://zylalabs.com/api/1835/vessel+information+and+route+tracking+api/1498/get+vessel" +
                        "+info?imoCode=" + boat.getImoCode())
                .header("Authorization", "Bearer " + apiKey)
                .retrieve().onStatus(HttpStatus.SERVICE_UNAVAILABLE::equals, clientResponse -> {
                    log.error("Zyla services unavailable");
                    return clientResponse.bodyToMono(String.class)
                            .map(ZylaServiceUnavailableException::new);
                }).onStatus(HttpStatus.NOT_FOUND::equals, clientResponse -> {
                    log.error("Boat IMO Not found");
                    return clientResponse.bodyToMono(String.class)
                            .map(ZylaBoatIMONotFoundException::new);
                }).bodyToMono(ZylaShipInformationResponseDTO.class)
                .timeout(Duration.ofMillis(10000));
        shipInformationResponseDTOMono.subscribe(boatInfo -> {
            log.info("Updating information for Boat: " + boat.getName());
            if (boatInfo.getSuccess()) {
                boat.setDeclaredName(boatInfo.getData()[0].getVesselName());
                boat.setShipType(boatInfo.getData()[0].getShipType());
                boat.setFlag(boatInfo.getData()[0].getFlag());
                boat.setGrossTonnage(Integer.valueOf(boatInfo.getData()[0].getGrossTonnage()));
                boat.setSummerDeadWeight(Integer.valueOf(boatInfo.getData()[0].getSummerDeadWeight()));
                boat.setLengthOverall(Integer.valueOf(boatInfo.getData()[0].getLengthOverall()));
                boat.setBeam(Integer.valueOf(boatInfo.getData()[0].getBeam()));
                boat.setYearOfBuilt(Integer.valueOf(boatInfo.getData()[0].getYearOfBuilt()));
                this.boatRepository.save(boat);
            }
        });
    }

    @Async("boatUpdateExecutor")
    private void updatePosition(Boat boat) {
        Mono<ZylaShipInformationResponseDTO> shipInformationResponseDTOMono = webClient.get()
                .uri("https://zylalabs.com/api/1835/vessel+information+and+route+tracking+api/1575/get+current" +
                        "+position?imoCode=" + boat.getImoCode())
                .header("Authorization", "Bearer " + apiKey)
                .retrieve().onStatus(HttpStatus.SERVICE_UNAVAILABLE::equals, clientResponse -> {
                    log.error("Zyla services unavailable");
                    return clientResponse.bodyToMono(String.class)
                            .map(ZylaServiceUnavailableException::new);
                }).bodyToMono(ZylaShipInformationResponseDTO.class)
                .timeout(Duration.ofMillis(20000));
        shipInformationResponseDTOMono.subscribe(boatInfo -> {
            if (boatInfo.getSuccess()) {
                log.info("Updating position for Boat: " + boat.getName());
                String[] formattedDate = boatInfo.getData()[0].getPositionReceived()
                        .replaceAll("\\(UTC", "")
                        .replaceAll("\\)", "").replaceAll(" LT  ", "").split(" ");
                boat.setPositionDate(LocalDateTime.of(LocalDate.parse(formattedDate[0]),
                        LocalTime.parse(formattedDate[1])).atOffset(ZoneOffset.of(formattedDate[2])).toInstant());
                boat.setLatitude(Double.valueOf(boatInfo.getData()[0].getLatitudeLongitude()
                        .replaceAll("\\u00b0", "").split(" / ")[0]));
                boat.setLongitude(Double.valueOf(boatInfo.getData()[0].getLatitudeLongitude()
                        .replaceAll("\\u00b0", "").split(" / ")[1]));
                this.boatRepository.save(boat);
            }
        });
    }


    @Scheduled(cron = "-")
    @Async("boatUpdateExecutor")
    public void updatePositions() {
        this.boatRepository.findAll().forEach(boat -> {
            if (boat.getImoCode() != null && boat.getDeclaredName() != null) {
                updatePosition(boat);
            }
        });
    }

}
