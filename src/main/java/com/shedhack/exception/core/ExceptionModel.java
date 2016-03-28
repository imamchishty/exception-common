package com.shedhack.exception.core;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * In exceptional circumstances it is useful to provide a meaningful response to clients.
 * This class encapsulates enough meta-data to allow the client to handle problems accordingly.
 *
 * The client can use the exception Id to map the exception to the appropriate log file instance for the
 * fuller details (if permitted by service).
 *
 * Properties that are set include:
 *
 * - RequestId : unique HTTP request ID, this could be set by the HTTP server.
 * - ExceptionId: unique exception ID, can be used to easily find the exception in logs.
 * - Session Id: HTTP session Id.
 * - ExceptionChainModel (collection) : a collection of exception chain models.
 *      Contains correlation Ids so that external service failures can be traced using this ID.
 * - Http status code
 * - Http status desc
 * - Help Link: HTTP resource that can detail business codes/http codes.
 * - Message: exception message.
 * - Exception Class: root exception class that was caught and wrapped.
 * - Path: the http resource that was targeted when exception was thrown.
 * - Application name
 * - Meta-data: contains any useful meta-data that was available and added.
 * - Business Codes (collection): {@link com.shedhack.exception.core.BusinessException}
 * - Params: original params
 * - DateTime: date/time when problem occurred.
 *
 * </p>
 *
 * @author imamchishty
 */
public class ExceptionModel {

    // ----------------------------------
    // Static inner class for the builder
    // ----------------------------------

    public static class Builder {

        private static final Pattern UUID_PATTERN = Pattern.compile("[a-f0-9]{8}-[a-f0-9]{4}-4[a-f0-9]{3}-[89aAbB][a-f0-9]{3}-[a-f0-9]{12}");

        private static final String METADATA = "exception-core-model";

        ExceptionModel model;

        public Builder() {
            model = new ExceptionModel();
        }

        public Builder(String applicationName, Exception exception) {

            model = new ExceptionModel();
            withApplicationName(applicationName)
                .withExceptionId(UUID.randomUUID().toString())
                    .withException(exception.getClass().getName(), exception.getMessage() != null ? exception.getMessage() : DEFAULT_ERROR_MESSAGE)
                        .withDateTime(new Date())
                            .withExceptionChain(findExceptionChain(exception))
                                .withMetaData(METADATA);
        }

        /**
         * Sets the ID, attempts to find correlation ID, business codes, HTTP code .
         */
        public Builder(String applicationName, BusinessException exception) {

            model = new ExceptionModel();

            withApplicationName(applicationName)
                .withExceptionId(exception.getExceptionId() != null ? exception.getExceptionId() : UUID.randomUUID().toString())
                    .withException(exception.getClass().getName(), exception.getMessage() != null ? exception.getMessage() : DEFAULT_ERROR_MESSAGE)
                        .withDateTime(new Date())
                            .withExceptionChain(findExceptionChain(exception))
                                .withMetaData(METADATA);

            if(!exception.getBusinessCodes().isEmpty()) {
                withBusinessCodes(exception.getBusinessCodes());
            }

            if(!exception.getParams().isEmpty()) {
                withParams(exception.getParams());
            }

            if(!exception.getRequestId().isEmpty()) {
                withRequestId(exception.getRequestId());
            }
        }

        public Builder withExceptionId(String exceptionId) {
            model.exceptionId = exceptionId;
            return this;
        }

        public Builder withRequestId(String requestId) {
            model.requestId = requestId;
            return this;
        }

        public Builder withPath(String path) {
            model.path = path;
            return this;
        }

        public Builder withDateTime(Date dateTime) {
            model.dateTime = dateTime;
            return this;
        }

        public Builder withSessionId(String sessionId) {
            model.sessionId = sessionId;
            return this;
        }

        public Builder withHttpCode(int code, String description) {
            model.httpStatusCode = code;
            model.httpStatusDescription = description;
            return this;
        }

        public Builder withHelpLink(String link) {
            model.helpLink = link;
            return this;
        }

        public Builder withException(String exceptionClass, String message) {
            model.exceptionClass = exceptionClass;
            model.message = message;
            return this;
        }

        public Builder withParam(String key, Object value) {
            model.params.put(key, value);
            return this;
        }

        public Builder withParams(Map<String, Object> params) {
            model.params = params;
            return this;
        }

        public Builder withBusinessCode(BusinessCode code) {
            model.businessCodes.put(code.getCode(), code.getDescription());
            return this;
        }

        public Builder withBusinesssCodes(Map<String, String> codes) {
            model.businessCodes = codes;
            return this;
        }

        public Builder withBusinessCodes(List<BusinessCode> codes) {

            for(BusinessCode code : codes) {
                withBusinessCode(code);
            }

            return this;
        }

        public Builder withApplicationName(String name) {
            model.applicationName = name;
            return this;
        }

        public Builder withExceptionChain(List<ExceptionChainModel> models) {
            model.exceptionChain = models;
            return this;
        }

        public Builder withExceptionChain(ExceptionChainModel chainModel) {
            model.exceptionChain.add(chainModel);
            return this;
        }

        public Builder withMetaData(String metaData) {
            model.metadata = metaData;
            return this;
        }


        public ExceptionModel build() {
            return model;
        }

        private static List<ExceptionChainModel> findExceptionChain(Throwable throwable) {

            List<ExceptionChainModel> chain = new ArrayList<ExceptionChainModel>();

            while (throwable != null) {

                String correlationId = null;

                if(throwable instanceof BusinessException) {
                    correlationId = ((BusinessException)throwable).getExceptionId();
                }
                else {
                    correlationId = findCorrelation(throwable.getMessage());
                }

                chain.add(new ExceptionChainModel(correlationId, throwable.getMessage()));
                throwable = throwable.getCause();
            }

            return chain;
        }

        private static String findCorrelation(String message) {

            if(message != null) {
                Matcher matcher = UUID_PATTERN.matcher(message);

                while (matcher.find()) {
                    return matcher.group();
                }
            }

            return null;
        }

    }

    // ----------------
    // Static method
    // ----------------

    public static Builder builder(String applicationName, Exception ex) {
        return new Builder(applicationName, ex);
    }

    public static Builder builder(String applicationName, BusinessException ex) {
        return new Builder(applicationName, ex);
    }

    public static Builder builder() {
        return new Builder();
    }

    // ----------------
    // Static variables
    // ----------------

    private static final String DEFAULT_ERROR_MESSAGE = "Unable to complete request.";

    // ----------------
    // Class properties
    // ----------------

    private String requestId, exceptionId, httpStatusDescription,
            path, sessionId, helpLink, message, exceptionClass,
            applicationName, metadata;

    private int httpStatusCode;

    private Map<String, Object> params = new HashMap<String, Object>();

    private Map<String, String> businessCodes = new HashMap<String, String>();

    private List<ExceptionChainModel> exceptionChain = new ArrayList<ExceptionChainModel>();

    private Date dateTime;

    public ExceptionModel() {
    }

    public String getExceptionId() {
        return exceptionId;
    }

    public void setExceptionId(String exceptionId) {
        this.exceptionId = exceptionId;
    }

    public String getHttpStatusDescription() {
        return httpStatusDescription;
    }

    public void setHttpStatusDescription(String httpStatusDescription) {
        this.httpStatusDescription = httpStatusDescription;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getHelpLink() {
        return helpLink;
    }

    public void setHelpLink(String helpLink) {
        this.helpLink = helpLink;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getExceptionClass() {
        return exceptionClass;
    }

    public void setExceptionClass(String exceptionClass) {
        this.exceptionClass = exceptionClass;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public Map<String, String> getBusinessCodes() {
        return businessCodes;
    }

    public void setBusinessCodes(Map<String, String> businessCodes) {
        this.businessCodes = businessCodes;
    }

    public List<ExceptionChainModel> getExceptionChain() {
        return exceptionChain;
    }

    public void setExceptionChain(List<ExceptionChainModel> exceptionChain) {
        this.exceptionChain = exceptionChain;
    }

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date date) {
        this.dateTime = date;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @Override
    public String toString() {
        return "ExceptionModel{" +
                "requestId='" + requestId + '\'' +
                ", exceptionId='" + exceptionId + '\'' +
                ", httpStatusDescription='" + httpStatusDescription + '\'' +
                ", path='" + path + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", helpLink='" + helpLink + '\'' +
                ", message='" + message + '\'' +
                ", exceptionClass='" + exceptionClass + '\'' +
                ", applicationName='" + applicationName + '\'' +
                ", metadata='" + metadata + '\'' +
                ", httpStatusCode=" + httpStatusCode +
                ", params=" + params +
                ", businessCodes=" + businessCodes +
                ", exceptionChain=" + exceptionChain +
                ", dateTime=" + dateTime +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExceptionModel that = (ExceptionModel) o;

        if (httpStatusCode != that.httpStatusCode) return false;
        if (applicationName != null ? !applicationName.equals(that.applicationName) : that.applicationName != null)
            return false;
        if (businessCodes != null ? !businessCodes.equals(that.businessCodes) : that.businessCodes != null)
            return false;
        if (dateTime != null ? !dateTime.equals(that.dateTime) : that.dateTime != null) return false;
        if (exceptionChain != null ? !exceptionChain.equals(that.exceptionChain) : that.exceptionChain != null)
            return false;
        if (exceptionClass != null ? !exceptionClass.equals(that.exceptionClass) : that.exceptionClass != null)
            return false;
        if (exceptionId != null ? !exceptionId.equals(that.exceptionId) : that.exceptionId != null) return false;
        if (helpLink != null ? !helpLink.equals(that.helpLink) : that.helpLink != null) return false;
        if (httpStatusDescription != null ? !httpStatusDescription.equals(that.httpStatusDescription) : that.httpStatusDescription != null)
            return false;
        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        if (metadata != null ? !metadata.equals(that.metadata) : that.metadata != null) return false;
        if (params != null ? !params.equals(that.params) : that.params != null) return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;
        if (requestId != null ? !requestId.equals(that.requestId) : that.requestId != null) return false;
        if (sessionId != null ? !sessionId.equals(that.sessionId) : that.sessionId != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = requestId != null ? requestId.hashCode() : 0;
        result = 31 * result + (exceptionId != null ? exceptionId.hashCode() : 0);
        result = 31 * result + (httpStatusDescription != null ? httpStatusDescription.hashCode() : 0);
        result = 31 * result + (path != null ? path.hashCode() : 0);
        result = 31 * result + (sessionId != null ? sessionId.hashCode() : 0);
        result = 31 * result + (helpLink != null ? helpLink.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (exceptionClass != null ? exceptionClass.hashCode() : 0);
        result = 31 * result + (applicationName != null ? applicationName.hashCode() : 0);
        result = 31 * result + (metadata != null ? metadata.hashCode() : 0);
        result = 31 * result + httpStatusCode;
        result = 31 * result + (params != null ? params.hashCode() : 0);
        result = 31 * result + (businessCodes != null ? businessCodes.hashCode() : 0);
        result = 31 * result + (exceptionChain != null ? exceptionChain.hashCode() : 0);
        result = 31 * result + (dateTime != null ? dateTime.hashCode() : 0);
        return result;
    }
}
