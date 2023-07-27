package com.bexchauvet.boatmanager.service;

import com.bexchauvet.boatmanager.rest.dto.MessageDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public interface ImageService {

    MessageDTO putImage(String id, MultipartFile File);

    InputStream downloadImage(String id);

    Boolean removeImage(String id);


}
