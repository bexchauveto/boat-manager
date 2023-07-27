package com.bexchauvet.boatmanager.error.exception;

public class BoatImageNotFoundException extends RuntimeException {

    public BoatImageNotFoundException(String id) {
        super(String.format("Image for Boat with ID %s not found", id));
    }
}
