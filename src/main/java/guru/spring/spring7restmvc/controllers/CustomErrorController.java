package guru.spring.spring7restmvc.controllers;

import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.h2.jdbc.JdbcSQLDataException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomErrorController {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity handleBindError(MethodArgumentNotValidException exception) {
    List errorList = exception.getBindingResult().getFieldErrors().stream()
        .map(fieldError -> {
          Map<String, Object> errorMap = new HashMap<>();
          errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
          return errorMap;
        }).toList();
    return ResponseEntity.badRequest().body(errorList);
  }

  @ExceptionHandler
  ResponseEntity handleJPAViolations(TransactionSystemException exception) {
    ResponseEntity.BodyBuilder responseEntity = ResponseEntity.badRequest();

    if(Objects.nonNull(exception.getCause())
        && exception.getCause().getCause() instanceof ConstraintViolationException) {
      ConstraintViolationException constraintViolationException = (ConstraintViolationException) exception.getCause().getCause();
      List errors = constraintViolationException.getConstraintViolations().stream()
          .map(constraintViolation -> {
            Map<String, Object> errorMap = new HashMap<>();
            errorMap.put(constraintViolation.getPropertyPath().toString(), constraintViolation.getMessage());
            return errorMap;
          }).toList();
      return responseEntity.body(errors);
    }
    return ResponseEntity.badRequest().body(exception.getMessage());
  }
}
