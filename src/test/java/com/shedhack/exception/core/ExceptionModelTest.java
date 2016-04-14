package com.shedhack.exception.core;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

/**
 * Tests the creation of the ExceptionModel.
 */
public class ExceptionModelTest {

    @Test
    public void should_create_exception_model_from_business_exception() {

        // Arrange
        BusinessException exception = buildException(FooBusinessCode.FOO_02, null, "The user didn't show his passport.");
        ExceptionModel.Builder builder = new ExceptionModel.Builder("foo", exception);
        builder.withHelpLink("http://help")
                .withHttpCode(500, "desc")
                .withPath("/api/v1/resource")
                .withSessionId("abcd1234");

        // Act
        ExceptionModel model = builder.build();

        // Assert
        assertNotNull(model);
        assertEquals("foo", model.getApplicationName());
        assertNotNull(model.getDateTime());
        assertEquals(exception.getMessage(), model.getMessage());
        assertEquals(exception.getClass().getName(), model.getExceptionClass());
        assertEquals(exception.getExceptionId(), model.getExceptionId());
        assertEquals(exception.getBusinessCodes().size(), model.getBusinessCodes().size());
        assertEquals(exception.getBusinessCodes().get(0).getDescription(), model.getBusinessCodes().get(exception.getBusinessCodes().get(0).getCode()));
        assertEquals("/api/v1/resource", model.getPath());
        assertEquals("abcd1234", model.getSessionId());
        assertEquals("http://help", model.getHelpLink());
        assertEquals(500, model.getHttpStatusCode());
        assertEquals("desc", model.getHttpStatusDescription());
        assertNotNull(model.getDateTime());
        assertEquals("exception-core-model", model.getMetadata());
        assertFalse(model.getExceptionChain().isEmpty());
        assertTrue(model.getParams().containsKey("user"));
        assertEquals(exception.getParams().get("user"), model.getParams().get("user"));
        assertEquals("ABCD12335", exception.getRequestId());
    }


    @Test
    public void should_create_exception_model_from_non_business_exception() {

        // Arrange
        IllegalArgumentException exception = buildIllegalArgException();
        ExceptionModel.Builder builder = new ExceptionModel.Builder("foo", exception);
        builder.withHelpLink("http://help")
                .withHttpCode(500, "desc")
                .withPath("/api/v1/resource")
                .withSessionId("abcd1234");

        // Act
        ExceptionModel model = builder.build();

        // Assert
        assertNotNull(model);
        assertEquals("foo", model.getApplicationName());
        assertNotNull(model.getDateTime());
        assertEquals(exception.getMessage(), model.getMessage());
        assertEquals(exception.getClass().getName(), model.getExceptionClass());
        assertEquals("/api/v1/resource", model.getPath());
        assertEquals("abcd1234", model.getSessionId());
        assertEquals("http://help", model.getHelpLink());
        assertEquals(500, model.getHttpStatusCode());
        assertEquals("desc", model.getHttpStatusDescription());
        assertNotNull(model.getDateTime());
        assertEquals("exception-core-model", model.getMetadata());
        assertFalse(model.getExceptionChain().isEmpty());
        assertNotNull(model.getExceptionId());
        assertEquals(model.getExceptionChain().get(0).getCorrelationId(), "d99306bc-4b04-4a34-b7e7-f5554383f570");
        assertEquals(model.getExceptionChain().get(0).getMessage(), "Something went wrong here is the exception Id d99306bc-4b04-4a34-b7e7-f5554383f570");
    }

    @Test
    public void should_have_multiple_exceptions_chained() {

        BusinessException exception = buildException(FooBusinessCode.FOO_04, buildException(FooBusinessCode.FOO_03,
                buildIllegalArgException(), "Account locked due to too many failed password attempts."), "security");

        ExceptionModel.Builder builder = new ExceptionModel.Builder("foo", exception);
        builder.withHelpLink("http://help")
                .withHttpCode(500, "desc")
                .withPath("/api/v1/resource")
                .withSessionId("abcd1234");
        ExceptionModel model = builder.build();

        assertEquals(3, model.getExceptionChain().size());
    }



    private BusinessException buildException(BusinessCode code, Exception ex, String message) {

        BusinessException.Builder builder;

        if(ex != null) {
            builder = new BusinessException.Builder(message, ex);
        }
        else {
            builder = new BusinessException.Builder(message);
        }

        builder.generateId().withBusinessCode(code).withParam("user", "imam").withRequestId("ABCD12335");

        return builder.build();
    }

    private IllegalArgumentException buildIllegalArgException() {
        return new IllegalArgumentException("Something went wrong here is the exception Id d99306bc-4b04-4a34-b7e7-f5554383f570");
    }

}
