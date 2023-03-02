package org.adaschool.api.utils;

public interface Constants {

    String CLAIMS_ROLES_KEY = "ada_roles";
    int TOKEN_DURATION_MINUTES = 1440;
    public static final String COOKIE_NAME = "";
    String ADMIN_ROLE = "ADMIN";
    String USER_ROLE = "USER";

    String MISSING_TOKEN_ERROR_MESSAGE = "Missing or wrong token";
    String TOKEN_EXPIRED_MALFORMED_ERROR_MESSAGE = "Token expired or malformed";
}
