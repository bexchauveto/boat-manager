package com.bexchauvet.boatmanager.service;

import com.bexchauvet.boatmanager.domain.Boat;
import com.bexchauvet.boatmanager.error.exception.BoatNotFoundException;
import com.bexchauvet.boatmanager.repository.BoatRepository;
import com.bexchauvet.boatmanager.rest.dto.BoatDTO;
import com.bexchauvet.boatmanager.rest.dto.MessageDTO;
import com.bexchauvet.boatmanager.service.Impl.BoatServiceImpl;
import com.bexchauvet.boatmanager.service.dto.ZylaShipInformationResponseDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BoatServiceUnitTest {

    final String apiKey = "api-key";
    BoatService boatService;
    @Mock
    BoatRepository boatRepository;
    @Mock
    WebClient webClient;
    @Mock
    private WebClient.RequestHeadersSpec requestHeadersMock;
    @Mock
    private WebClient.RequestHeadersUriSpec requestHeadersUriMock;
    @Mock
    private WebClient.ResponseSpec responseMock;

    @BeforeEach
    void init() {
        boatService = new BoatServiceImpl(boatRepository, webClient, apiKey);
    }

    private Boat createBoat(Long id, Instant now) {
        return new Boat(id, "my boat", "my description", false, null, "declaredName", "shipType", "flag", 0, 0, 0, 0, 0,
                now, 0.0, 0.0);
    }


    @Test
    @DisplayName("Test the getAll function")
    void getAll() {
        Instant now = Instant.now();
        when(boatRepository.findAll()).thenReturn(Collections.emptyList())
                .thenReturn(List.of(createBoat(2L, now)));
        assertTrue(boatService.getAll().isEmpty());
        List<Boat> expectedResult = List.of(createBoat(2L, now));
        assertEquals(expectedResult, boatService.getAll());
        verify(boatRepository, times(2)).findAll();
        verifyNoMoreInteractions(boatRepository);
    }

    @Test
    @DisplayName("Test the getAllPage function")
    void getAllPage() {
        when(boatRepository.findAll(Mockito.any(Pageable.class))).thenReturn(Page.empty());
        assertTrue(boatService.getAllPage(Pageable.ofSize(1)).isEmpty());
        verify(boatRepository).findAll(Mockito.any(Pageable.class));
        verifyNoMoreInteractions(boatRepository);
    }

    @Test
    @DisplayName("Test the getById function with bad id")
    void getById_NOT_FOUND() {
        when(boatRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        assertThrows(BoatNotFoundException.class,
                () -> boatService.getById("id"));
        verify(boatRepository).findById(Mockito.anyString());
        verifyNoMoreInteractions(boatRepository);
    }

    @Test
    @DisplayName("Test the getById function with good id")
    void getById() {
        Instant now = Instant.now();
        when(boatRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(createBoat(1L, now)));
        Boat expectedResult = createBoat(1L, now);
        Boat result = boatService.getById("1");
        assertEquals(expectedResult, result);
        verify(boatRepository).findById(Mockito.anyString());
        verifyNoMoreInteractions(boatRepository);
    }

    @Test
    @DisplayName("Test the getByName function with bad name")
    void getByName_NOT_FOUND() {
        when(boatRepository.findByName(Mockito.anyString())).thenReturn(Optional.empty());
        assertThrows(BoatNotFoundException.class,
                () -> boatService.getByName("name"));
        verify(boatRepository).findByName(Mockito.anyString());
        verifyNoMoreInteractions(boatRepository);
    }

    @Test
    @DisplayName("Test the getByName function with good name")
    void getByName() {
        Instant now = Instant.now();
        when(boatRepository.findByName(Mockito.anyString()))
                .thenReturn(Optional.of(createBoat(1L, now)));
        Boat expectedResult = createBoat(1L, now);
        Boat result = boatService.getByName("1");
        assertEquals(expectedResult, result);
        verify(boatRepository).findByName(Mockito.anyString());
        verifyNoMoreInteractions(boatRepository);
    }

    @Test
    @DisplayName("Test the create function without IMO code")
    void create_Without_IMO() {
        Instant now = Instant.now();
        when(boatRepository.save(Mockito.any(Boat.class)))
                .thenReturn(createBoat(1L, now));
        MessageDTO createResult = boatService.create(new BoatDTO("my boat", "my description", null));
        MessageDTO expectedResult = new MessageDTO("Boat information with ID 1 has been created",
                HttpStatus.CREATED, createBoat(1L, now));
        assertEquals(expectedResult.getMessage(), createResult.getMessage());
        assertEquals(expectedResult.getStatus(), createResult.getStatus());
        assertEquals(expectedResult.getData(), createResult.getData());
        verify(boatRepository).save(Mockito.any(Boat.class));
        verifyNoMoreInteractions(boatRepository);
    }

    @Test
    @DisplayName("Test the create function with IMO code")
    @Disabled("Not working in Dockerfile")
    void create_With_IMO() {
        ZylaShipInformationResponseDTO mockResponse = new ZylaShipInformationResponseDTO(200, true, "IMO Code 9999999" +
                " is valid", new ZylaShipInformationResponseDTO.ZylaShipInformationData[]{new ZylaShipInformationResponseDTO.ZylaShipInformationData("9999999",
                "vessel_name",
                "ship_type", "flag", "200", "2233", "234", "12", "2023", null, null)});
        when(webClient.get()).thenReturn(requestHeadersUriMock);
        when(requestHeadersUriMock.uri("https://zylalabs.com/api/1835/vessel+information+and+route+tracking+api/1498/get+vessel+info?imoCode=9999999"))
                .thenReturn(requestHeadersMock);
        when(requestHeadersMock.header(notNull(), notNull())).thenReturn(requestHeadersMock);
        when(requestHeadersMock.retrieve()).thenReturn(responseMock);
        when(responseMock.onStatus(any(), any())).thenReturn(responseMock);
        when(responseMock.bodyToMono(ZylaShipInformationResponseDTO.class)).thenReturn(Mono.just(mockResponse));
        Instant now = Instant.now();
        when(boatRepository.save(Mockito.any(Boat.class)))
                .thenReturn(new Boat(1L, "my boat", "my description", false, 9999999, null, null,
                        null, 0, 0, 0, 0, 0,
                        now, 0.0, 0.0));
        MessageDTO createResult = boatService.create(new BoatDTO("my boat", "my description", 9999999));
        MessageDTO expectedResult = new MessageDTO("Boat information with ID 1 has been created",
                HttpStatus.CREATED, new Boat(1L, "my boat", "my description", false, 9999999, "vessel_name",
                "ship_type", "flag", 200, 2233, 234, 12, 2023,
                now, 0.0, 0.0));
        assertEquals(expectedResult.getMessage(), createResult.getMessage());
        assertEquals(expectedResult.getStatus(), createResult.getStatus());
        assertEquals(expectedResult.getData(), createResult.getData());
        verify(boatRepository, times(2)).save(Mockito.any(Boat.class));
        verifyNoMoreInteractions(boatRepository, webClient);
    }

    @Test
    @DisplayName("Test the update function with bad id")
    void update_NOT_FOUND() {
        when(boatRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        assertThrows(BoatNotFoundException.class,
                () -> boatService.update("id",
                        new BoatDTO("my new boat name", "brand new description", null)));
        verify(boatRepository).findById(Mockito.anyString());
        verifyNoMoreInteractions(boatRepository);
    }

    @Test
    @DisplayName("Test the update function with good id")
    void update() {
        Instant now = Instant.now();
        when(boatRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(createBoat(1L, now)));
        when(boatRepository.save(Mockito.any(Boat.class)))
                .thenReturn(
                        new Boat(1L, "my new boat name", "brand new description", false, null, "declaredName", "shipType", "flag", 0, 0, 0, 0, 0,
                                now, 0.0, 0.0));
        MessageDTO updateResult = boatService.update("1",
                new BoatDTO("my new boat name", "brand new description", null));
        MessageDTO expectedResult = new MessageDTO("Boat information with ID 1 has been updated",
                HttpStatus.OK, "1");
        assertEquals(expectedResult.getMessage(), updateResult.getMessage());
        assertEquals(expectedResult.getStatus(), updateResult.getStatus());
        assertEquals(expectedResult.getData(), updateResult.getData());
        verify(boatRepository).findById(Mockito.anyString());
        verify(boatRepository).save(Mockito.any(Boat.class));
        verifyNoMoreInteractions(boatRepository);
    }

    @Test
    @DisplayName("Test the delete function with bad id")
    void delete_NOT_FOUND() {
        when(boatRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        assertThrows(BoatNotFoundException.class,
                () -> boatService.delete("id"));
        verify(boatRepository).findById(Mockito.anyString());
        verifyNoMoreInteractions(boatRepository);
    }

    @Test
    @DisplayName("Test the delete function with good id")
    void delete() {
        Instant now = Instant.now();
        when(boatRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(createBoat(1L, now)));
        doNothing().when(boatRepository).delete(Mockito.any(Boat.class));
        MessageDTO deleteResult = boatService.delete("1");
        MessageDTO expectedResult = new MessageDTO("Boat information with ID 1 has been deleted",
                HttpStatus.OK, "1");
        assertEquals(expectedResult.getMessage(), deleteResult.getMessage());
        assertEquals(expectedResult.getStatus(), deleteResult.getStatus());
        assertEquals(expectedResult.getData(), deleteResult.getData());
        verify(boatRepository).findById(Mockito.anyString());
        verify(boatRepository).delete(Mockito.any(Boat.class));
        verifyNoMoreInteractions(boatRepository);
    }

    @Test
    @DisplayName("Test the exists function")
    void exists() {
        Instant now = Instant.now();
        when(boatRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(createBoat(1L, now)));
        assertTrue(boatService.exists("1"));
        verify(boatRepository).findById(Mockito.anyString());
        verifyNoMoreInteractions(boatRepository);
    }

    @Test
    @DisplayName("Test the exists function with bad id")
    void doNotExists() {
        when(boatRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.empty());
        assertFalse(boatService.exists("1"));
        verify(boatRepository).findById(Mockito.anyString());
        verifyNoMoreInteractions(boatRepository);
    }

    @Test
    @DisplayName("Test the has image function with bad id")
    void hasImage_NOT_FOUND() {
        when(boatRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.empty());
        assertFalse(boatService.hasImage("1"));
        verify(boatRepository).findById(Mockito.anyString());
        verifyNoMoreInteractions(boatRepository);
    }

    @Test
    @DisplayName("Test the has image function return false")
    void hasImage_false() {
        Instant now = Instant.now();
        when(boatRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(createBoat(1L, now)));
        assertFalse(boatService.hasImage("1"));
        verify(boatRepository).findById(Mockito.anyString());
        verifyNoMoreInteractions(boatRepository);
    }

    @Test
    @DisplayName("Test the has image function return true")
    void hasImage_true() {
        when(boatRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(new Boat(1L, "my boat", "my description", true, null, "declaredName", "shipType", "flag", 0, 0, 0, 0, 0,
                        Instant.now(), 0.0, 0.0)));
        assertTrue(boatService.hasImage("1"));
        verify(boatRepository).findById(Mockito.anyString());
        verifyNoMoreInteractions(boatRepository);
    }

    @Test
    @DisplayName("Test the set image function with bad id")
    void setImage_NOT_FOUND() {
        when(boatRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        assertThrows(BoatNotFoundException.class,
                () -> boatService.setImage("id"));
        verify(boatRepository).findById(Mockito.anyString());
        verifyNoMoreInteractions(boatRepository);
    }

    @Test
    @DisplayName("Test the set image function with good id")
    void setImage() {
        Instant now = Instant.now();
        when(boatRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(createBoat(1L, now)));
        when(boatRepository.save(Mockito.any(Boat.class)))
                .thenReturn(new Boat(1L, "my boat", "my description", true, null, "declaredName", "shipType", "flag", 0, 0, 0, 0, 0,
                        now, 0.0, 0.0));
        boatService.setImage("1");
        verify(boatRepository).findById(Mockito.anyString());
        verify(boatRepository).save(Mockito.any(Boat.class));
        verifyNoMoreInteractions(boatRepository);
    }

    @Test
    @DisplayName("Test the update positions function")
    @Disabled("Not working in Dockerfile")
    void updatePositions() {
        ZylaShipInformationResponseDTO mockResponse = new ZylaShipInformationResponseDTO(200, true, "IMO Code " +
                "9999999" +
                " is valid", new ZylaShipInformationResponseDTO.ZylaShipInformationData[]{new ZylaShipInformationResponseDTO.ZylaShipInformationData(null, null,
                null, null, null, null, null, null, null,
                "2023-07-31 09:08 LT  (UTC +2)", "36.53408\u00b0 / -6.29092\u00b0")});
        when(webClient.get()).thenReturn(requestHeadersUriMock);
        when(requestHeadersUriMock.uri("https://zylalabs.com/api/1835/vessel+information+and+route+tracking+api/1575/get+current+position?imoCode=9999999"))
                .thenReturn(requestHeadersMock);
        when(requestHeadersMock.header(notNull(), notNull())).thenReturn(requestHeadersMock);
        when(requestHeadersMock.retrieve()).thenReturn(responseMock);
        when(responseMock.onStatus(any(), any())).thenReturn(responseMock);
        when(responseMock.bodyToMono(ZylaShipInformationResponseDTO.class)).thenReturn(Mono.just(mockResponse));
        Instant now = Instant.now();
        when(boatRepository.findAll()).thenReturn(List.of(new Boat(1L, "my boat", "my description", false, 9999999,
                null, null, null, 0, 0, 0, 0, 0, now, 0.0, 0.0)));
        boatService.updatePositions();
        verify(boatRepository).findAll();
        verify(boatRepository).save(Mockito.any(Boat.class));
        verifyNoMoreInteractions(boatRepository, webClient);
    }
}
