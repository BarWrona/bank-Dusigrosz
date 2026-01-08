package pl.edu.pjwstk.dusigrosz.web.exceptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import pl.edu.pjwstk.dusigrosz.common.customException.*;
import pl.edu.pjwstk.dusigrosz.common.dto.ErrorResponse;


import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({
            AccountException.class,
            CurrencyException.class,
            TransferException.class,
            UserException.class,
            UserProfileException.class,
            VisorException.class
    })
    public ResponseEntity<ErrorResponse> handleBusinessExceptions(Exception e, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        ErrorResponse errorDetails = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                e.getClass().getSimpleName(),
                e.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );
        return new ResponseEntity<>(errorDetails, status);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception e, WebRequest request) {
        ErrorResponse errorDetails = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Unexpected error occurred: " + e.getMessage(),
                request.getDescription(false).replace("uri=", "")
        );

        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
