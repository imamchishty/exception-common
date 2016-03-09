package com.shedhack.exception.core;

/**
 * Sometimes in life you get more than one exception.
 * If that happens it would be nice to wrap them all up so that we get full visibility of the problem.
 * Each exception, if using the {@link com.shedhack.exception.core.BusinessException} will have an ID
 * which can be used to set the correlation ID on this object. The correlation ID can be used to track down
 * log entries.
 *
 * @author imamchishty
 */
public class ExceptionChainModel {

    private String correlationId, message;

    public ExceptionChainModel() {
    }

    /**
     * Chained Exception Model.
     * @param correlationId
     * @param message
     */
    public ExceptionChainModel(String correlationId, String message) {
        this.correlationId = correlationId;
        this.message = message;
    }

    public String getCorrelationId() {
        return correlationId;
    }

    public void setCorrelationId(String correlationId) {
        this.correlationId = correlationId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ExceptionChainModel that = (ExceptionChainModel) o;

        if (correlationId != null ? !correlationId.equals(that.correlationId) : that.correlationId != null)
            return false;
        if (message != null ? !message.equals(that.message) : that.message != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = correlationId != null ? correlationId.hashCode() : 0;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ExceptionChainModel{" +
                "correlationId='" + correlationId + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
