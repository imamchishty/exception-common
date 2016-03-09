package com.shedhack.exception.core;

/**
 * Business Codes interface, provides clients with meaningful codes + descriptions.
 *
 * @author imamchishty
 */
public interface BusinessCode {

    /**
     * Short code which represents a business issue.
     * @return String business code.
     */
    String getCode();

    /**
     * Full description of the business code.
     * @return String description.
     */
    String getDescription();

}
