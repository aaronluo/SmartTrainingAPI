package com.smarttraining.exception;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
//    private Logger logger = LoggerFactory.getLogger("ERROR_LOGGER");

    @ExceptionHandler(ApiException.class)
    protected ResponseEntity<Object> handleApiExcpetion(ApiException ex) {
//        LogEntity log = new LogEntity();
//        
//        log.setMessageLevel(MessageLevel.ERROR);
//        log.setClient("TFAPI");
//        log.setMessageText(ex.getMessage());
//        log.setMessageAppdx(ex.getOriginalException() != null ? ex.getOriginalException().getLocalizedMessage() : "");
//        
//        try {
//            logger.error(new ObjectMapper().writeValueAsString(log));
//        } catch (JsonProcessingException e) {
//
//        }
        
        return new ResponseEntity<Object>(ex.getApiError(), ex.getHttpStatus());
    }
    
    @ExceptionHandler(IllegalStateException.class)
    protected ResponseEntity<Object> handleIllegalStateException(IllegalStateException ex, WebRequest request) {
        ApiException apiEx = new ApiException(new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, 
                String.format("Ambiguous handler methods mapped for HTTP path[%s]", request.getDescription(false))), ex);
        
        return this.handleApiExcpetion(apiEx);
    }

//    @Override
//    protected ResponseEntity<Object> handleExceptionInternal(Exception ex,
//            Object body, HttpHeaders headers, HttpStatus status,
//            WebRequest request) {
//        // TODO Auto-generated method stub
//        return super.handleExceptionInternal(ex, body, headers, status, request);
//    }
//
//    @Override
//    protected ResponseEntity<Object> handleMissingPathVariable(
//            MissingPathVariableException ex, HttpHeaders headers,
//            HttpStatus status, WebRequest request) {
//        // TODO Auto-generated method stub
//        return super.handleMissingPathVariable(ex, headers, status, request);
//    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status,
            WebRequest request) {
        
        ApiException apiEx = new ApiException(new ApiError(status, 
                String.format("No handler found for the path[%s]", request.getDescription(false))), ex);
        
        return this.handleApiExcpetion(apiEx);
    }
}
