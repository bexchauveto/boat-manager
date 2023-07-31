package com.bexchauvet.boatmanager.rest;

import com.bexchauvet.boatmanager.domain.Boat;
import com.bexchauvet.boatmanager.error.dto.ErrorDTO;
import com.bexchauvet.boatmanager.rest.dto.BoatDTO;
import com.bexchauvet.boatmanager.rest.dto.MessageDTO;
import com.bexchauvet.boatmanager.service.BoatService;
import com.bexchauvet.boatmanager.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController()
@RequestMapping("/boats")
@Tag(name = "Boat", description = "The Boat API.")
@AllArgsConstructor
@Slf4j
public class BoatController {

    private BoatService boatService;
    private ImageService imageService;

    @Operation(summary = "Get the list of boats")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all the boats",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Boat.class)))}),
            @ApiResponse(responseCode = "401", description = "Invalid authentication token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)))})
    @GetMapping("")
    public List<Boat> getAll() {
        return this.boatService.getAll();
    }

    @Operation(summary = "Get the specific number of boats from the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found all the boats",
                    content = {@Content(mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = Boat.class)))}),
            @ApiResponse(responseCode = "400", description = "Invalid id supplied",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "401", description = "Invalid authentication token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)))})
    @GetMapping("/filter")
    public Page<Boat> getPage(@ParameterObject Pageable pageable) {
        return this.boatService.getAllPage(pageable);
    }

    @Operation(summary = "Get a boat by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the boat corresponding",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boat.class))}),
            @ApiResponse(responseCode = "401", description = "Invalid authentication token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Boat not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)))})
    @GetMapping("/{id}")
    public Boat getId(@PathVariable("id") String id) {
        return this.boatService.getById(id);
    }

    @Operation(summary = "Get a boat by Name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the boat corresponding",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Boat.class))}),
            @ApiResponse(responseCode = "401", description = "Invalid authentication token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Boat not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)))})
    @GetMapping("/{name}")
    public Boat getName(@PathVariable("name") String name) {
        return this.boatService.getByName(name);
    }

    @Operation(summary = "Create a new boat in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Boat created",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid format for the boat",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "401", description = "Invalid authentication token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)))})
    @PostMapping()
    public ResponseEntity<MessageDTO> create(@RequestBody @Valid BoatDTO boatDTO) {
        return new ResponseEntity<>(this.boatService.create(boatDTO), HttpStatusCode.valueOf(201));
    }

    @Operation(summary = "Update a boat in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Boat information has been updated",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageDTO.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid format for the boat",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "401", description = "Invalid authentication token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Boat to update is not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)))})
    @PutMapping("/{id}")
    public ResponseEntity<MessageDTO> update(@PathVariable("id") String id, @RequestBody @Valid BoatDTO boatDTO) {
        return new ResponseEntity<>(this.boatService.update(id, boatDTO), HttpStatusCode.valueOf(200));
    }

    @Operation(summary = "Delete a boat in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Boat information has been deleted",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageDTO.class))}),
            @ApiResponse(responseCode = "401", description = "Invalid authentication token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Boat to delete is not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)))})
    @DeleteMapping("/{id}")
    public ResponseEntity<MessageDTO> delete(@PathVariable("id") String id) {
        if (boatService.hasImage(id)) {
            imageService.removeImage(id);
        }
        return new ResponseEntity<>(this.boatService.delete(id), HttpStatusCode.valueOf(200));
    }

    @Operation(summary = "Upload an image of a boat in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "The id of the updated boat with image",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageDTO.class))}),
            @ApiResponse(responseCode = "401", description = "Invalid authentication token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Boat to update is not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)))})
    @PostMapping(value = "/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<MessageDTO> uploadImage(@PathVariable("id") String id,
                                                  @Parameter(description = "File to be uploaded (max size 5MB)",
                                                          content = @Content(mediaType =
                                                                  MediaType.MULTIPART_FORM_DATA_VALUE))
                                                  @RequestPart(value = "file", required = false) MultipartFile file) {
        return new ResponseEntity<>(this.imageService.putImage(id, file), HttpStatusCode.valueOf(201));
    }

    @Operation(summary = "Download an image of a boat in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The image of the boat",
                    content = {@Content(mediaType = "image/png",
                            array = @ArraySchema(schema = @Schema(implementation = byte.class)))}),
            @ApiResponse(responseCode = "401", description = "Invalid authentication token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class))),
            @ApiResponse(responseCode = "404", description = "Boat not found or the boat has no image",
                    content = @Content(mediaType = "image/png",
                            schema = @Schema(implementation = ErrorDTO.class)))})
    @GetMapping(value = "/{id}/images", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public ResponseEntity<byte[]> downloadImage(@PathVariable("id") String id) throws IOException {
        return new ResponseEntity<>(IOUtils.toByteArray(this.imageService.downloadImage(id)),
                HttpStatusCode.valueOf(200));

    }

    @Operation(summary = "Update the position of all the boats in the database")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "The update is launched",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = MessageDTO.class))}),
            @ApiResponse(responseCode = "401", description = "Invalid authentication token",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ErrorDTO.class)))})
    @PostMapping(value = "/update-position")
    public ResponseEntity<MessageDTO> updatePositions() {
        this.boatService.updatePositions();
        return new ResponseEntity<>(new MessageDTO("Updating", HttpStatus.OK, null),
                HttpStatusCode.valueOf(200));
    }


}
