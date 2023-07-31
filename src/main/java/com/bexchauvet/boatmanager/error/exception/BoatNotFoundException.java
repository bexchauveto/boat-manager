package com.bexchauvet.boatmanager.error.exception;

public class BoatNotFoundException extends RuntimeException {

    public BoatNotFoundException(String id) {
        super(String.format("Boat with ID or Name %s not found", id));
    }

}
