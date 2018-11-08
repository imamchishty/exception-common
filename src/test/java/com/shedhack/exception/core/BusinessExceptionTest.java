package com.shedhack.exception.core;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 *
 */
public class BusinessExceptionTest {


    @Test
    public void should_create_a_business_exception() {

        // Arrange & Act
        BusinessException exception = BusinessException.builder("Failed to log the user in")
                .withBusinessCode(FooBusinessCode.FOO_01)
                .withParam("user", "imam")
                .build();

        // Assert
        assertNotNull(exception);
        assertEquals("Failed to log the user in", exception.getMessage());
        assertEquals("imam", exception.getParams().get("user"));
        assertEquals(FooBusinessCode.FOO_01, exception.getBusinessCodes().get(0));
        assertNotNull(exception.getExceptionId());
        assertNull(exception.getCorrelationId());
        assertNull(exception.getHttpCode());
        
        // something
    }
}
