package com.bexchauvet.boatmanager.service.Impl;

import com.bexchauvet.boatmanager.domain.Boat;
import com.bexchauvet.boatmanager.error.exception.BoatImageNotFoundException;
import com.bexchauvet.boatmanager.error.exception.BoatNotFoundException;
import com.bexchauvet.boatmanager.repository.BoatRepository;
import com.bexchauvet.boatmanager.rest.dto.MessageDTO;
import com.bexchauvet.boatmanager.service.ImageService;
import io.minio.*;
import io.minio.errors.ErrorResponseException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Optional;


@Service
@AllArgsConstructor
@Slf4j
public class ImageServiceImpl implements ImageService {

    private MinioClient minioClient;
    private BoatRepository boatRepository;

    @Override
    public MessageDTO putImage(String id, MultipartFile file) {
        Optional<Boat> boat = this.boatRepository.findById(id);
        if (boat.isPresent()) {
            boat.get().setHasImage(true);
            boatRepository.save(boat.get());
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
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new BoatNotFoundException(id);
        }
    }

    @Override
    public InputStream downloadImage(String id) {
        if (this.boatRepository.findById(id).isPresent()) {
            try {
                return minioClient.getObject(GetObjectArgs.builder().bucket("boat").object(id).build());
            } catch (ErrorResponseException e) {
                throw new BoatImageNotFoundException(id);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new BoatImageNotFoundException(id);
        }
    }

    @Override
    public Boolean removeImage(String id) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder().bucket("boat").object(id).build());
            return true;
        } catch (ErrorResponseException e) {
            throw new BoatImageNotFoundException(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
