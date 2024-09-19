package com.eg.invoice.exception.handler;

import com.eg.invoice.dto.ErrorDetailDto;
import com.eg.invoice.exception.BillingNotExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BillingNotExistException.class)
    public ResponseEntity<ErrorDetailDto> handleInvoiceNotExistException(BillingNotExistException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorDetailDto(LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(), ex.getClass().getSimpleName(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDetailDto> handleMethodArgumentNotValid(final MethodArgumentNotValidException exception) {
        final BindingResult bindingResult = exception.getBindingResult();
        final List<String> fieldErrors = bindingResult.getFieldErrors().stream()
                .map(error -> error.getField().concat(" " + error.getDefaultMessage())).toList();
        ErrorDetailDto errorMessageDto = new ErrorDetailDto();
        errorMessageDto.setTimestamp(LocalDateTime.now());
        errorMessageDto.setHttpStatus(HttpStatus.BAD_REQUEST.value());
        errorMessageDto.setException(exception.getClass().getSimpleName());
        errorMessageDto.setMessage(fieldErrors.stream().findFirst().orElse(null));

        return new ResponseEntity<>(errorMessageDto, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDetailDto> handleGeneralException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDetailDto(LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getClass().getSimpleName(), ex.getMessage()));
    }

}
