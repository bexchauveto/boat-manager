package com.bexchauvet.boatmanager.error;

import com.bexchauvet.boatmanager.error.dto.ErrorDTO;
import com.bexchauvet.boatmanager.error.exception.BadLoginUnauthorizedException;
import com.bexchauvet.boatmanager.error.exception.BoatImageNotFoundException;
import com.bexchauvet.boatmanager.error.exception.BoatNotFoundException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.ArrayList;
import java.util.stream.Collectors;

@ControllerAdvice
public class BoatManagerAdvise {


    @ExceptionHandler(value = {BoatNotFoundException.class, BoatImageNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    protected ResponseEntity<ErrorDTO> handleNotFound(RuntimeException ex) {
        return new ResponseEntity<>(new ErrorDTO(ex.getMessage(), HttpStatus.NOT_FOUND, new ArrayList<>()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {BadLoginUnauthorizedException.class, UsernameNotFoundException.class})
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ResponseBody
    protected ResponseEntity<ErrorDTO> handleUnauthorized(RuntimeException ex) {
        return new ResponseEntity<>(new ErrorDTO(ex.getMessage(), HttpStatus.UNAUTHORIZED, new ArrayList<>()),
                HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    protected ResponseEntity<ErrorDTO> handleBadRequest(MethodArgumentNotValidException ex) {
        return new ResponseEntity<>(new ErrorDTO(ex.getMessage(), HttpStatus.BAD_REQUEST, ex
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(Collectors.toList())), HttpStatus.BAD_REQUEST);
    }
}
