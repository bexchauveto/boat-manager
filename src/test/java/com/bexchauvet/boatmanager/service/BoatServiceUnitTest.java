package com.bexchauvet.boatmanager.service;

import com.bexchauvet.boatmanager.domain.Boat;
import com.bexchauvet.boatmanager.error.exception.BoatNotFoundException;
import com.bexchauvet.boatmanager.repository.BoatRepository;
import com.bexchauvet.boatmanager.rest.dto.MessageDTO;
import com.bexchauvet.boatmanager.service.Impl.BoatServiceImpl;
import com.bexchauvet.boatmanager.service.dto.BoatDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BoatServiceUnitTest {

    BoatService boatService;

    @Mock
    BoatRepository boatRepository;

    @BeforeEach
    void init() {
        boatService = new BoatServiceImpl(boatRepository);
    }


    @Test
    void getAll() {
        when(boatRepository.findAll()).thenReturn(Collections.emptyList())
                .thenReturn(List.of(new Boat(1L, "my boat", "my description", false)));
        assertTrue(boatService.getAll().isEmpty());
        List<Boat> expectedResult =
                List.of(new Boat(1L, "my boat", "my description", false));
        assertEquals(expectedResult, boatService.getAll());
        verify(boatRepository, times(2)).findAll();
        verifyNoMoreInteractions(boatRepository);
    }

    @Test
    void getAllPage() {
        when(boatRepository.findAll(Mockito.any(Pageable.class))).thenReturn(Page.empty());
        assertTrue(boatService.getAllPage(Pageable.ofSize(1)).isEmpty());
        verify(boatRepository).findAll(Mockito.any(Pageable.class));
        verifyNoMoreInteractions(boatRepository);
    }

    @Test
    void getById_NOT_FOUND() {
        when(boatRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        assertThrows(BoatNotFoundException.class,
                () -> boatService.getById("id"));
        verify(boatRepository).findById(Mockito.anyString());
        verifyNoMoreInteractions(boatRepository);
    }

    @Test
    void getById() {
        when(boatRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(new Boat(1L, "my boat", "my description", false)));
        Boat expectedResult = new Boat(1L, "my boat", "my description", false);
        Boat result = boatService.getById("1");
        assertEquals(expectedResult, result);
        verify(boatRepository).findById(Mockito.anyString());
        verifyNoMoreInteractions(boatRepository);
    }

    @Test
    void create() {
        when(boatRepository.save(Mockito.any(Boat.class)))
                .thenReturn(new Boat(1L, "my boat", "my description", false));
        MessageDTO createResult = boatService.create(new BoatDTO("my boat", "my description"));
        MessageDTO expectedResult = new MessageDTO("Boat information with ID 1 has been created",
                HttpStatus.CREATED, new Boat(1L, "my boat", "my description", false));
        assertEquals(expectedResult.getMessage(), createResult.getMessage());
        assertEquals(expectedResult.getStatus(), createResult.getStatus());
        assertEquals(expectedResult.getData(), createResult.getData());
        verify(boatRepository).save(Mockito.any(Boat.class));
        verifyNoMoreInteractions(boatRepository);
    }

    @Test
    void update_NOT_FOUND() {
        when(boatRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        assertThrows(BoatNotFoundException.class,
                () -> boatService.update("id",
                        new BoatDTO("my new boat name", "brand new description")));
        verify(boatRepository).findById(Mockito.anyString());
        verifyNoMoreInteractions(boatRepository);
    }

    @Test
    void update() {
        when(boatRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(new Boat(1L, "my boat", "my description", false)));
        when(boatRepository.save(Mockito.any(Boat.class)))
                .thenReturn(
                        new Boat(1L, "my new boat name", "brand new description", false));
        MessageDTO updateResult = boatService.update("1",
                new BoatDTO("my new boat name", "brand new description"));
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
    void delete_NOT_FOUND() {
        when(boatRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        assertThrows(BoatNotFoundException.class,
                () -> boatService.delete("id"));
        verify(boatRepository).findById(Mockito.anyString());
        verifyNoMoreInteractions(boatRepository);
    }

    @Test
    void delete() {
        when(boatRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(new Boat(1L, "my boat", "my description", false)));
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
    void exists() {
        when(boatRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(new Boat(1L, "my boat", "my description", false)));
        assertTrue(boatService.exists("1"));
        verify(boatRepository).findById(Mockito.anyString());
        verifyNoMoreInteractions(boatRepository);
    }

    @Test
    void doNotExists() {
        when(boatRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.empty());
        assertFalse(boatService.exists("1"));
        verify(boatRepository).findById(Mockito.anyString());
        verifyNoMoreInteractions(boatRepository);
    }

    @Test
    void hasImage_NOT_FOUND() {
        when(boatRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.empty());
        assertFalse(boatService.hasImage("1"));
        verify(boatRepository).findById(Mockito.anyString());
        verifyNoMoreInteractions(boatRepository);
    }

    @Test
    void hasImage_false() {
        when(boatRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(new Boat(1L, "my boat", "my description", false)));
        assertFalse(boatService.hasImage("1"));
        verify(boatRepository).findById(Mockito.anyString());
        verifyNoMoreInteractions(boatRepository);
    }

    @Test
    void hasImage_true() {
        when(boatRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(new Boat(1L, "my boat", "my description", true)));
        assertTrue(boatService.hasImage("1"));
        verify(boatRepository).findById(Mockito.anyString());
        verifyNoMoreInteractions(boatRepository);
    }

    @Test
    void setImage_NOT_FOUND() {
        when(boatRepository.findById(Mockito.anyString())).thenReturn(Optional.empty());
        assertThrows(BoatNotFoundException.class,
                () -> boatService.setImage("id"));
        verify(boatRepository).findById(Mockito.anyString());
        verifyNoMoreInteractions(boatRepository);
    }

    @Test
    void setImage() {
        when(boatRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(new Boat(1L, "my boat", "my description", false)));
        when(boatRepository.save(Mockito.any(Boat.class)))
                .thenReturn(new Boat(1L, "my boat", "my description", true));
        boatService.setImage("1");
        verify(boatRepository).findById(Mockito.anyString());
        verify(boatRepository).save(Mockito.any(Boat.class));
        verifyNoMoreInteractions(boatRepository);
    }
}
