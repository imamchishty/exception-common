package com.shedhack.exception.core;

import java.util.*;

/**
 * Generic Exception.
 * Please set the exception Id accordingly as this can be used to link client problems to log files/database.
 *
 * Please note that upon construction an exception Id will be generated using {@link java.util.UUID}.
 * This can be changed using the <code>withExceptionId(String id)</code> method.
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
            generateId();
        }

        public Builder(Exception ex) {
            exception = new BusinessException(ex.getMessage(), ex);
            generateId();
        }

        public Builder(String message, Exception ex) {
            exception = new BusinessException(message, ex);
            generateId();
        }

        public Builder generateId() {
            exception.exceptionId = UUID.randomUUID().toString();
            return this;
        }

        public Builder withExceptionId(String id) {
            if(!Utils.isEmptyOrNull(id)) {
                exception.exceptionId = id;
            }
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

        public Builder withSpanId(String id) {
            exception.spanId = id;
            return this;
        }

        public Builder withTraceId(String id) {
            exception.traceId = id;
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

    private String traceId, spanId, exceptionId, correlationId;

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

    public String getSpanId() {
        return spanId;
    }

    public void setSpanId(String spanId) {
        this.spanId = spanId;
    }

    public String getTraceId() {
        return traceId;
    }

    public void setTraceId(String traceId) {
        this.traceId = traceId;
    }

    @Override
    public String toString() {
        return "{\"BusinessException\":"
                + super.toString()
                + ", \"traceId\":\"" + traceId + "\""
                + ", \"spanId\":\"" + spanId + "\""
                + ", \"exceptionId\":\"" + exceptionId + "\""
                + ", \"correlationId\":\"" + correlationId + "\""
                + ", \"params\":" + params
                + ", \"businessCodes\":" + businessCodes
                + ", \"httpCode\":\"" + httpCode + "\""
                + "}";
    }
}
