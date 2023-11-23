package com.book.api.exceptions.advice;

import com.book.api.exceptions.ErrorObject;
import com.book.api.exceptions.MyResourceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MyResourceException.class)
    public ResponseEntity<ErrorObject> handleResourceNotFoundException(MyResourceException exception) {
        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatusCode(exception.getStatusCode());
        errorObject.setMessage(exception.getMessage());
        log.error(exception.getMessage(), exception);

        return new ResponseEntity<ErrorObject>(errorObject, HttpStatusCode.valueOf(exception.getStatusCode()));
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorObject> handleException(Exception e) {
        ErrorObject errorObject = new ErrorObject();
        errorObject.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorObject.setMessage(e.getMessage());
        log.error(e.getMessage(), e);

        return new ResponseEntity<ErrorObject>(errorObject, HttpStatusCode.valueOf(500));
    }

}
