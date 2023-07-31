package com.bexchauvet.boatmanager.service;

import com.bexchauvet.boatmanager.domain.Boat;
import com.bexchauvet.boatmanager.error.exception.BoatImageNotFoundException;
import com.bexchauvet.boatmanager.error.exception.BoatNotFoundException;
import com.bexchauvet.boatmanager.repository.BoatRepository;
import com.bexchauvet.boatmanager.rest.dto.MessageDTO;
import com.bexchauvet.boatmanager.service.Impl.ImageServiceImpl;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.messages.ErrorResponse;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ImageServiceUnitTest {

    ImageService imageService;

    @Mock
    MinioClient minioClient;
    @Mock
    BoatRepository boatRepository;

    @BeforeEach
    void init() {
        imageService = new ImageServiceImpl(minioClient, boatRepository);
    }

    @Test
    @DisplayName("Test the put image function with bucket not exists and exception")
    void putImage_BucketNotExists_Exception() throws Exception {
        Instant now = Instant.now();
        when(boatRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(new Boat(1L, "my boat", "my description", false, null, "declaredName", "shipType", "flag", 0, 0, 0, 0, 0,
                        now, 0.0, 0.0)));
        when(boatRepository.save(Mockito.any(Boat.class)))
                .thenReturn(
                        new Boat(1L, "my new boat name", "brand new description", false, null, "declaredName", "shipType", "flag", 0, 0, 0, 0, 0,
                                now, 0.0, 0.0));
        when(minioClient.bucketExists(Mockito.any(BucketExistsArgs.class)))
                .thenThrow(new InsufficientDataException("message"));
        assertThrows(RuntimeException.class,
                () -> imageService.putImage("id", new MockMultipartFile("file.png", "data".getBytes())));
        verify(minioClient).bucketExists(Mockito.any(BucketExistsArgs.class));
        verify(boatRepository).findById(Mockito.anyString());
        verify(boatRepository).save(Mockito.any(Boat.class));
        verifyNoMoreInteractions(minioClient, boatRepository);
    }

    @Test
    @DisplayName("Test the put image function with bucket not exists and ErrorResponsException")
    void putImage_BucketNotExists_ErrorResponseException() throws Exception {
        Instant now = Instant.now();
        when(boatRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(new Boat(1L, "my boat", "my description", false, null, "declaredName", "shipType", "flag", 0, 0, 0, 0, 0,
                        now, 0.0, 0.0)));
        when(boatRepository.save(Mockito.any(Boat.class)))
                .thenReturn(
                        new Boat(1L, "my new boat name", "brand new description", false, null, "declaredName", "shipType", "flag", 0, 0, 0, 0, 0,
                                now, 0.0, 0.0));
        when(minioClient.bucketExists(Mockito.any(BucketExistsArgs.class)))
                .thenThrow(new ErrorResponseException(new ErrorResponse(),
                        new Response.Builder().code(400).protocol(Protocol.HTTP_1_0)
                                .request(new Request.Builder().url("https://url").build())
                                .message("error")
                                .build(),
                        "httpTrace"));
        assertThrows(BoatImageNotFoundException.class,
                () -> imageService.putImage("id", new MockMultipartFile("file.png", "data".getBytes())));
        verify(minioClient).bucketExists(Mockito.any(BucketExistsArgs.class));
        verify(boatRepository).findById(Mockito.anyString());
        verify(boatRepository).save(Mockito.any(Boat.class));
        verifyNoMoreInteractions(minioClient, boatRepository);
    }

    @Test
    @DisplayName("Test the put image function with bucket not exists, create bucket and insert")
    void putImage_BucketNotExists_CreateBucket_thenInsert() throws Exception {
        Instant now = Instant.now();
        when(boatRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(new Boat(1L, "my boat", "my description", false, null, "declaredName", "shipType", "flag", 0, 0, 0, 0, 0,
                        now, 0.0, 0.0)));
        when(boatRepository.save(Mockito.any(Boat.class)))
                .thenReturn(
                        new Boat(1L, "my new boat name", "brand new description", false, null, "declaredName", "shipType", "flag", 0, 0, 0, 0, 0,
                                now, 0.0, 0.0));
        when(minioClient.bucketExists(Mockito.any(BucketExistsArgs.class)))
                .thenReturn(false);
        doNothing().when(minioClient).makeBucket(Mockito.any(MakeBucketArgs.class));
        when(minioClient.putObject(Mockito.any(PutObjectArgs.class)))
                .thenReturn(new ObjectWriteResponse(null, "", "", "", "", ""));
        MessageDTO putImageResult = imageService.putImage("1",
                new MockMultipartFile("file.png", "file.png", "image/png",
                        "data".getBytes()));
        MessageDTO expectedResult = new MessageDTO("Boat image with ID 1 has been created",
                HttpStatus.CREATED, "1");
        assertEquals(expectedResult.getMessage(), putImageResult.getMessage());
        assertEquals(expectedResult.getStatus(), putImageResult.getStatus());
        assertEquals(expectedResult.getData(), putImageResult.getData());
        verify(minioClient).bucketExists(Mockito.any(BucketExistsArgs.class));
        verify(minioClient).makeBucket(Mockito.any(MakeBucketArgs.class));
        verify(minioClient).putObject(Mockito.any(PutObjectArgs.class));
        verify(boatRepository).findById(Mockito.anyString());
        verify(boatRepository).save(Mockito.any(Boat.class));
        verifyNoMoreInteractions(minioClient, boatRepository);
    }

    @Test
    @DisplayName("Test the put image function with bad id")
    void putImage_NOT_FOUND() throws Exception {
        Instant now = Instant.now();
        when(boatRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.empty());
        assertThrows(BoatNotFoundException.class,
                () -> imageService.putImage("id", new MockMultipartFile("file.png", "data".getBytes())));
        verify(boatRepository).findById(Mockito.anyString());
        verifyNoInteractions(minioClient);
        verifyNoMoreInteractions(boatRepository);
    }

    @Test
    @DisplayName("Test the put image function with bucket exists and insert")
    void putImage_BucketExists_thenInsert() throws Exception {
        Instant now = Instant.now();
        when(boatRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(new Boat(1L, "my boat", "my description", false, null, "declaredName", "shipType", "flag", 0, 0, 0, 0, 0,
                        now, 0.0, 0.0)));
        when(boatRepository.save(Mockito.any(Boat.class)))
                .thenReturn(
                        new Boat(1L, "my new boat name", "brand new description", false, null, "declaredName", "shipType", "flag", 0, 0, 0, 0, 0,
                                now, 0.0, 0.0));
        when(minioClient.bucketExists(Mockito.any(BucketExistsArgs.class)))
                .thenReturn(true);
        when(minioClient.putObject(Mockito.any(PutObjectArgs.class)))
                .thenReturn(new ObjectWriteResponse(null, "", "", "", "", ""));
        MessageDTO putImageResult = imageService.putImage("1",
                new MockMultipartFile("file.png", "file.png", "image/png",
                        "data".getBytes()));
        MessageDTO expectedResult = new MessageDTO("Boat image with ID 1 has been created",
                HttpStatus.CREATED, "1");
        assertEquals(expectedResult.getMessage(), putImageResult.getMessage());
        assertEquals(expectedResult.getStatus(), putImageResult.getStatus());
        assertEquals(expectedResult.getData(), putImageResult.getData());
        verify(minioClient).bucketExists(Mockito.any(BucketExistsArgs.class));
        verify(minioClient).putObject(Mockito.any(PutObjectArgs.class));
        verify(boatRepository).findById(Mockito.anyString());
        verify(boatRepository).save(Mockito.any(Boat.class));
        verifyNoMoreInteractions(minioClient, boatRepository);
    }

    @Test
    @DisplayName("Test the download image function with bucket not exists and exception")
    void downloadImage_BucketNotExists_Exception() throws Exception {
        when(boatRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(new Boat(1L, "my boat", "my description", false, null, "declaredName", "shipType", "flag", 0, 0, 0, 0, 0,
                        Instant.now(), 0.0, 0.0)));
        when(minioClient.getObject(Mockito.any(GetObjectArgs.class)))
                .thenThrow(new InsufficientDataException("message"));
        assertThrows(RuntimeException.class,
                () -> imageService.downloadImage("id"));
        verify(minioClient).getObject(Mockito.any(GetObjectArgs.class));
        verify(boatRepository).findById(Mockito.anyString());
        verifyNoMoreInteractions(minioClient, boatRepository);
    }

    @Test
    @DisplayName("Test the download image function with bucket not exists and ErrorResponsException")
    void downloadImage_BucketNotExists_ErrorResponseException() throws Exception {
        when(boatRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(new Boat(1L, "my boat", "my description", false, null, "declaredName", "shipType", "flag", 0, 0, 0, 0, 0,
                        Instant.now(), 0.0, 0.0)));
        when(minioClient.getObject(Mockito.any(GetObjectArgs.class)))
                .thenThrow(new ErrorResponseException(new ErrorResponse(), null, "httpTrace"));
        assertThrows(BoatImageNotFoundException.class,
                () -> imageService.downloadImage("id"));
        verify(minioClient).getObject(Mockito.any(GetObjectArgs.class));
        verifyNoMoreInteractions(minioClient);
        verify(boatRepository).findById(Mockito.anyString());
        verifyNoMoreInteractions(minioClient, boatRepository);
    }

    @Test
    @DisplayName("Test the download image function")
    void downloadImage() throws Exception {
        when(boatRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.of(new Boat(1L, "my boat", "my description", false, null, "declaredName", "shipType", "flag", 0, 0, 0, 0, 0,
                        Instant.now(), 0.0, 0.0)));
        byte[] data = new byte[]{1, 2, 3};
        when(minioClient.getObject(Mockito.any(GetObjectArgs.class)))
                .thenReturn(new GetObjectResponse(null, "bucket", "region", "object",
                        new ByteArrayInputStream(data)));
        InputStream downloadImageResult = imageService.downloadImage("1");
        assertArrayEquals(new ByteArrayInputStream(data).readAllBytes(), downloadImageResult.readAllBytes());
        verify(minioClient).getObject(Mockito.any(GetObjectArgs.class));
        verify(boatRepository).findById(Mockito.anyString());
        verifyNoMoreInteractions(minioClient, boatRepository);
    }

    @Test
    @DisplayName("Test the download image function with bad id")
    void downloadImage_NOT_FOUND() throws Exception {
        when(boatRepository.findById(Mockito.anyString()))
                .thenReturn(Optional.empty());
        assertThrows(BoatImageNotFoundException.class,
                () -> imageService.downloadImage("id"));
        verify(boatRepository).findById(Mockito.anyString());
        verifyNoInteractions(minioClient);
        verifyNoMoreInteractions(boatRepository);
    }

    @Test
    @DisplayName("Test the remove image function and exception")
    void removeImage_Exception() throws Exception {
        doThrow(new InsufficientDataException("message"))
                .when(minioClient).removeObject(Mockito.any(RemoveObjectArgs.class));
        assertThrows(RuntimeException.class,
                () -> imageService.removeImage("id"));
        verify(minioClient).removeObject(Mockito.any(RemoveObjectArgs.class));
        verifyNoMoreInteractions(minioClient);
    }

    @Test
    @DisplayName("Test the remove image function and ErrorResponseException")
    void removeImage_ErrorResponseException() throws Exception {
        doThrow(new ErrorResponseException(new ErrorResponse(), null, "httpTrace"))
                .when(minioClient).removeObject(Mockito.any(RemoveObjectArgs.class));
        assertThrows(RuntimeException.class,
                () -> imageService.removeImage("id"));
        verify(minioClient).removeObject(Mockito.any(RemoveObjectArgs.class));
        verifyNoMoreInteractions(minioClient);
    }

    @Test
    @DisplayName("Test the remove image function")
    void removeImage() throws Exception {
        doNothing().when(minioClient).removeObject(Mockito.any(RemoveObjectArgs.class));
        Boolean removeImageResult = imageService.removeImage("1");
        Boolean expectedResult = true;
        assertEquals(expectedResult, removeImageResult);
        verify(minioClient).removeObject(Mockito.any(RemoveObjectArgs.class));
        verifyNoMoreInteractions(minioClient);
    }
}
