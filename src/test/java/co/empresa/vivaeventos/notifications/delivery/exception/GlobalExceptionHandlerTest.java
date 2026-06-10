package co.empresa.vivaeventos.notifications.delivery.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    @DisplayName("handleRuntimeException should return 404")
    void handleRuntimeExceptionShouldReturn404() {
        RuntimeException ex = new RuntimeException("Not found");
        ResponseEntity<Map<String, Object>> response = handler.handleRuntimeException(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Not found", Objects.requireNonNull(response.getBody()).get("error"));
    }

    @Test
    @DisplayName("handleValidationException should return 400")
    void handleValidationExceptionShouldReturn400() throws Exception {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        var bindingResult = new org.springframework.validation.BeanPropertyBindingResult(new TestDto("test"), "testDto");
        bindingResult.rejectValue("name", "NotBlank", "must not be blank");
        when(ex.getBindingResult()).thenReturn(bindingResult);

        ResponseEntity<Map<String, Object>> response = handler.handleValidationException(ex);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("name: must not be blank", Objects.requireNonNull(response.getBody()).get("error"));
    }

    record TestDto(String name) { }

    @Test
    @DisplayName("handleGenericException should return 500")
    void handleGenericExceptionShouldReturn500() {
        Exception ex = new Exception("Unexpected");
        ResponseEntity<Map<String, Object>> response = handler.handleGenericException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Internal server error", Objects.requireNonNull(response.getBody()).get("error"));
    }
}
