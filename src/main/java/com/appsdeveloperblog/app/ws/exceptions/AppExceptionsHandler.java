package com.appsdeveloperblog.app.ws.exceptions;

import java.util.Date;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import com.appsdeveloperblog.app.ws.ui.model.response.ErrorMessage;

@ControllerAdvice
public class AppExceptionsHandler {

  @ExceptionHandler(value = {UserServiceException.class})
  public ResponseEntity<Object> handleUserServiceException(final UserServiceException ex,
      final WebRequest request) {

    final ErrorMessage errorMessage = new ErrorMessage(new Date(), ex.getMessage());

    return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(value = {Exception.class})
  public ResponseEntity<Object> handleOtherExceptions(final Exception ex,
      final WebRequest request) {

    final ErrorMessage errorMessage = new ErrorMessage(new Date(), ex.getMessage());

    return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

}
