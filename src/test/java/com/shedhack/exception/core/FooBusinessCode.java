package com.shedhack.exception.core;

/**
 * Test enum for exception codes
 */
public enum FooBusinessCode implements BusinessCode {

    FOO_01("User not found."),
    FOO_02("Users account has been locked."),
    FOO_03("Users account not active."),
    FOO_04("Security concern over users account.");

    private final String description;

    FooBusinessCode(String desc) {
        this.description = desc;
    }

    public String getCode() {
        return this.name();
    }

    public String getDescription() {
        return description;
    }
}
