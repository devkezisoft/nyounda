package com.kezisoft.nyounda.api.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";

    public static final String CLIENT = "ROLE_CLIENT";
    public static final String PROVIDER = "ROLE_PROVIDER";

    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    private AuthoritiesConstants() {
    }
}
