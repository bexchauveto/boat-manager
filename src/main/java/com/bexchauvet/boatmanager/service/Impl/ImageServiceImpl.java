package com.bexchauvet.boatmanager.service.Impl;

import com.bexchauvet.boatmanager.error.exception.BoatImageNotFoundException;
import com.bexchauvet.boatmanager.rest.dto.MessageDTO;
import com.bexchauvet.boatmanager.service.ImageService;
import io.minio.*;
import io.minio.errors.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


@Service
@AllArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {

    private MinioClient minioClient;

    @Override
    public MessageDTO putImage(String id, MultipartFile file) {
        //Check if bucket exists, if not creates it.
        try {
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket("boat").build())) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket("boat").build());
            }
            // Add the object into the bucket
            minioClient.putObject(PutObjectArgs.builder().bucket("boat").object(id)
                    .stream(new ByteArrayInputStream(file.getBytes()), -1, 10485760)
                    .contentType(file.getContentType()).build());
            return new MessageDTO(String.format("Boat image with ID %s has been created", id),
                    HttpStatus.CREATED, id);
        } catch (ErrorResponseException e) {
            throw new BoatImageNotFoundException(id);
        } catch (InsufficientDataException | InternalException | InvalidKeyException | InvalidResponseException |
                 IOException | NoSuchAlgorithmException | ServerException | XmlParserException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public InputStream downloadImage(String id) {
        try {
            return minioClient.getObject(GetObjectArgs.builder().bucket("boat").object(id).build());
        } catch (ErrorResponseException e) {
            throw new BoatImageNotFoundException(id);
        } catch (InsufficientDataException | InternalException | InvalidKeyException | InvalidResponseException |
                 IOException | NoSuchAlgorithmException | ServerException | XmlParserException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Boolean removeImage(String id) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket("boat").object(id).build());
            return true;
        } catch (ErrorResponseException e) {
            throw new BoatImageNotFoundException(id);
        } catch (InsufficientDataException | InternalException | InvalidKeyException | InvalidResponseException |
                 IOException | NoSuchAlgorithmException | ServerException | XmlParserException e) {
            throw new RuntimeException(e);
        }
    }
}
