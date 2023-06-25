package ru.practicum.shareit.unit;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.*;


import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class ErrorHandlerTest {

    ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleEmailAlreadyExistTest() {
        ErrorResponse exceptionMessage = errorHandler.handleAlreadyExistException(new DataAlreadyExistException("message"));
        assertThat(exceptionMessage.getError(), equalTo("message"));
    }

    @Test
    void handleNotFoundExceptionTest() {
        ErrorResponse exceptionMessage = errorHandler.handleNotFoundException(new DataNotFoundException("message"));
        assertThat(exceptionMessage.getError(), equalTo("message"));
    }

    @Test
    void validationExceptionTest() {
        ErrorResponse exceptionMessage = errorHandler.validationException(new ValidationException("message"));
        assertThat(exceptionMessage.getError(), equalTo("message"));
    }

    @Test
    void handleIllegalDataException() {
        ErrorResponse exceptionMessage = errorHandler.handleIllegalDataException(new IllegalDataException("message"));
        assertThat(exceptionMessage.getError(), equalTo("message"));
    }
}
