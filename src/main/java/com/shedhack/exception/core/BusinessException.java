package com.shedhack.exception.core;

import java.util.*;

/**
 * Generic Service Exception.
 * Please set the exception Id accordingly as this can be used to link client problems to lig files.
 * The exception Id is generated using {@link java.util.UUID}.
 * Setting the correlation Id maybe useful when calling external services and to map local exceptions with external ones.
 *
 * @author imamchishty
 */
public class BusinessException extends RuntimeException {

    // ----------------------------------
    // Static inner class for the builder
    // ----------------------------------

    public static class Builder {

        BusinessException exception;

        public Builder(String message) {
            exception = new BusinessException(message);
        }

        public Builder(Exception ex) {
            exception = new BusinessException(ex.getMessage(), ex);
        }

        public Builder(String message, Exception ex) {
            exception = new BusinessException(message, ex);
        }

        public Builder generateId() {
            exception.exceptionId = UUID.randomUUID().toString();
            return this;
        }

        public Builder withCorrelationId(String correlationId) {
            exception.correlationId = correlationId;
            return this;
        }

        public Builder withParam(String key, Object value) {
            exception.params.put(key, value);
            return this;
        }

        public Builder withParams(Map<String, Object> params) {
            exception.params = params;
            return this;
        }

        public Builder withBusinessCode(BusinessCode code) {
            exception.businessCodes.add(code);
            return this;
        }

        public Builder withBusinessCodes(List<BusinessCode> codes) {
            exception.businessCodes = codes;
            return this;
        }

        public BusinessException build() {
            return exception;
        }
    }

    // ----------------
    // Static method
    // ----------------

    public static Builder builder(String message, Exception ex) {
        return new Builder(message, ex);
    }

    public static Builder builder(Exception ex) {
        return new Builder(ex);
    }

    public static Builder builder(String message) {
        return new Builder(message);
    }

    // ----------------
    // Class properties
    // ----------------

    private String exceptionId, correlationId;

    private Map<String, Object> params = new HashMap<String, Object>();

    private List<BusinessCode> businessCodes = new ArrayList<BusinessCode>();

    private Integer httpCode;


    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Exception ex) {
        super(message, ex);
    }

    public String getExceptionId() {
        return exceptionId;
    }

    public void setExceptionId(String exceptionId) {
        this.exceptionId = exceptionId;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public List<BusinessCode> getBusinessCodes() {
        return businessCodes;
    }

    public void setBusinessCodes(List<BusinessCode> businessCodes) {
        this.businessCodes = businessCodes;
    }

    public Integer getHttpCode() {
        return httpCode;
    }

    public void setHttpCode(Integer httpCode) {
        this.httpCode = httpCode;
    }

    @Override
    public String toString() {
        return "BusinessException{" +
                "exceptionId='" + exceptionId + '\'' +
                ", correlationId='" + correlationId + '\'' +
                ", params=" + params +
                ", businessCodes=" + businessCodes +
                ", httpCode=" + httpCode +
                '}';
    }
}
