package com.bexchauvet.boatmanager.rest;

import com.bexchauvet.boatmanager.domain.Boat;
import com.bexchauvet.boatmanager.error.exception.BoatNotFoundException;
import com.bexchauvet.boatmanager.rest.dto.BoatDTO;
import com.bexchauvet.boatmanager.rest.dto.MessageDTO;
import com.bexchauvet.boatmanager.service.BoatService;
import com.bexchauvet.boatmanager.service.ImageService;
import org.apache.commons.compress.utils.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BoatControllerUnitTest {

    BoatController boatController;

    @Mock
    BoatService boatService;
    @Mock
    ImageService imageService;

    @BeforeEach
    void init() {
        boatController = new BoatController(boatService, imageService);
    }


    @Test
    void getAll() {
        when(boatService.getAll()).thenReturn(Collections.emptyList())
                .thenReturn(List.of(new Boat(1L, "my boat", "my description", false)));
        assertTrue(boatController.getAll().isEmpty());
        List<Boat> expectedResult =
                List.of(new Boat(1L, "my boat", "my description", false));
        assertEquals(expectedResult, boatController.getAll());
        verify(boatService, times(2)).getAll();
        verifyNoMoreInteractions(boatService);
        verifyNoInteractions(imageService);
    }

    @Test
    void getAllPage() {
        when(boatService.getAllPage(Mockito.any(Pageable.class))).thenReturn(Page.empty());
        assertTrue(boatController.getPage(Pageable.ofSize(1)).isEmpty());
        verify(boatService).getAllPage(Mockito.any(Pageable.class));
        verifyNoMoreInteractions(boatService);
        verifyNoInteractions(imageService);
    }

    @Test
    void getById_NOT_FOUND() {
        when(boatService.getById(Mockito.anyString())).thenThrow(new BoatNotFoundException("id"));
        assertThrows(BoatNotFoundException.class,
                () -> boatController.getId("id"));
        verify(boatService).getById(Mockito.anyString());
        verifyNoMoreInteractions(boatService);
        verifyNoInteractions(imageService);
    }

    @Test
    void getById() {
        when(boatService.getById(Mockito.anyString()))
                .thenReturn(new Boat(1L, "my boat", "my description", false));
        Boat expectedResult = new Boat(1L, "my boat", "my description", false);
        Boat result = boatController.getId("1");
        assertEquals(expectedResult, result);
        verify(boatService).getById(Mockito.anyString());
        verifyNoMoreInteractions(boatService);
        verifyNoInteractions(imageService);
    }

    @Test
    void create() {
        when(boatService.create(Mockito.any(BoatDTO.class)))
                .thenReturn(new MessageDTO("Boat information with ID 1 has been created",
                        HttpStatus.CREATED,
                        new Boat(1L, "my boat", "my description", false)));
        ResponseEntity<MessageDTO> createResult = boatController
                .create(new BoatDTO("my boat", "my description"));
        ResponseEntity<MessageDTO> expectedResult = new ResponseEntity<>(
                new MessageDTO("Boat information with ID 1 has been created",
                        HttpStatus.CREATED,
                        new Boat(1L, "my boat", "my description", false)),
                HttpStatus.CREATED);
        assertEquals(expectedResult.getStatusCode(), createResult.getStatusCode());
        assertEquals(expectedResult.getBody(), createResult.getBody());
        verify(boatService).create(Mockito.any(BoatDTO.class));
        verifyNoMoreInteractions(boatService);
        verifyNoInteractions(imageService);
    }

    @Test
    void update_NOT_FOUND() {
        when(boatService.update(Mockito.anyString(), Mockito.any(BoatDTO.class)))
                .thenThrow(new BoatNotFoundException("id"));
        assertThrows(BoatNotFoundException.class,
                () -> boatController.update("id",
                        new BoatDTO("my new boat name", "brand new description")));
        verify(boatService).update(Mockito.anyString(), Mockito.any(BoatDTO.class));
        verifyNoMoreInteractions(boatService);
        verifyNoInteractions(imageService);
    }

    @Test
    void update() {
        when(boatService.update(Mockito.anyString(), Mockito.any(BoatDTO.class)))
                .thenReturn(new MessageDTO("Boat information with ID 1 has been updated",
                        HttpStatus.OK,
                        new Boat(1L, "my new boat name", "brand new description", false)));
        ResponseEntity<MessageDTO> updateResult = boatController
                .update("1", new BoatDTO("my new boat name", "brand new description"));
        ResponseEntity<MessageDTO> expectedResult = new ResponseEntity<>(
                new MessageDTO("Boat information with ID 1 has been updated",
                        HttpStatus.OK,
                        new Boat(1L, "my new boat name", "brand new description", false)),
                HttpStatus.OK);
        assertEquals(expectedResult.getStatusCode(), updateResult.getStatusCode());
        assertEquals(expectedResult.getBody(), updateResult.getBody());
        verify(boatService).update(Mockito.anyString(), Mockito.any(BoatDTO.class));
        verifyNoMoreInteractions(boatService);
        verifyNoInteractions(imageService);
    }

    @Test
    void delete_NOT_FOUNT() {
        when(boatService.hasImage(Mockito.anyString())).thenReturn(false);
        when(boatService.delete(Mockito.anyString())).thenThrow(new BoatNotFoundException("id"));
        assertThrows(BoatNotFoundException.class,
                () -> boatController.delete("id"));
        verify(boatService).delete(Mockito.anyString());
        verify(boatService).hasImage(Mockito.anyString());
        verifyNoMoreInteractions(boatService);
        verifyNoInteractions(imageService);
    }

    @Test
    void delete_hasImageFalse() {
        when(boatService.hasImage(Mockito.anyString())).thenReturn(false);
        when(boatService.delete(Mockito.anyString()))
                .thenReturn(new MessageDTO("Boat information with ID 1 has been deleted",
                        HttpStatus.OK, "1"));
        ResponseEntity<MessageDTO> deleteResult = boatController.delete("1");
        ResponseEntity<MessageDTO> expectedResult = new ResponseEntity<>(
                new MessageDTO("Boat information with ID 1 has been deleted",
                        HttpStatus.OK, "1"),
                HttpStatus.OK);
        assertEquals(expectedResult.getStatusCode(), deleteResult.getStatusCode());
        assertEquals(expectedResult.getBody(), deleteResult.getBody());
        verify(boatService).delete(Mockito.anyString());
        verify(boatService).hasImage(Mockito.anyString());
        verifyNoMoreInteractions(boatService);
        verifyNoInteractions(imageService);
    }

    @Test
    void delete_hasImageTrue() {
        when(boatService.hasImage(Mockito.anyString())).thenReturn(true);
        when(imageService.removeImage(Mockito.anyString())).thenReturn(true);
        when(boatService.delete(Mockito.anyString()))
                .thenReturn(new MessageDTO("Boat information with ID 1 has been deleted",
                        HttpStatus.OK, "1"));
        ResponseEntity<MessageDTO> deleteResult = boatController.delete("1");
        ResponseEntity<MessageDTO> expectedResult = new ResponseEntity<>(
                new MessageDTO("Boat information with ID 1 has been deleted",
                        HttpStatus.OK, "1"),
                HttpStatus.OK);
        assertEquals(expectedResult.getStatusCode(), deleteResult.getStatusCode());
        assertEquals(expectedResult.getBody(), deleteResult.getBody());
        verify(boatService).delete(Mockito.anyString());
        verify(boatService).hasImage(Mockito.anyString());
        verify(imageService).removeImage(Mockito.anyString());
        verifyNoMoreInteractions(boatService, imageService);
    }

    @Test
    void uploadImage_NOT_FOUND() {
        when(imageService.putImage(Mockito.anyString(), Mockito.any(MultipartFile.class)))
                .thenThrow(new BoatNotFoundException("id"));
        assertThrows(BoatNotFoundException.class,
                () -> boatController.uploadImage("id",
                        new MockMultipartFile("file.png", "data".getBytes())));
        verify(imageService).putImage(Mockito.anyString(), Mockito.any(MultipartFile.class));
        verifyNoMoreInteractions(imageService);
        verifyNoInteractions(boatService);
    }

    @Test
    void uploadImage() {
        when(imageService.putImage(Mockito.anyString(), Mockito.any(MultipartFile.class)))
                .thenReturn(new MessageDTO("Boat image with ID 1 has been created",
                        HttpStatus.CREATED, "1"));
        ResponseEntity<MessageDTO> updateResult = boatController
                .uploadImage("1", new MockMultipartFile("file.png", "data".getBytes()));
        ResponseEntity<MessageDTO> expectedResult = new ResponseEntity<>(
                new MessageDTO("Boat image with ID 1 has been created", HttpStatus.CREATED, "1"),
                HttpStatus.CREATED);
        assertEquals(expectedResult.getStatusCode(), updateResult.getStatusCode());
        assertEquals(expectedResult.getBody(), updateResult.getBody());
        verify(imageService).putImage(Mockito.anyString(), Mockito.any(MultipartFile.class));
        verifyNoMoreInteractions(imageService);
        verifyNoInteractions(boatService);
    }


    @Test
    void downloadImage_NOT_FOUND() {
        when(imageService.downloadImage(Mockito.anyString()))
                .thenThrow(new BoatNotFoundException("id"));
        assertThrows(BoatNotFoundException.class,
                () -> boatController.downloadImage("id"));
        verify(imageService).downloadImage(Mockito.anyString());
        verifyNoMoreInteractions(imageService);
        verifyNoInteractions(boatService);
    }

    @Test
    void downloadImage() throws IOException {
        byte[] data = new byte[]{1, 2, 3};
        when(imageService.downloadImage(Mockito.anyString()))
                .thenReturn(new ByteArrayInputStream(data));
        ResponseEntity<byte[]> downloadImageResult = boatController.downloadImage("1");
        assertArrayEquals(IOUtils.toByteArray(new ByteArrayInputStream(data)), downloadImageResult.getBody());
        verify(imageService).downloadImage(Mockito.anyString());
        verifyNoMoreInteractions(imageService);
        verifyNoInteractions(boatService);
    }


}
