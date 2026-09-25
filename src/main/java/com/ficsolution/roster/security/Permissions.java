package com.ficsolution.roster.security;

/**
 * Permission names used for authorization. Values must match the rows seeded in the {@code permission} table.
 */
public final class Permissions {

    private Permissions() {
    }

    public static final String ROLE_MANAGE = "ROLE_MANAGE";

    public static final String STORE_READ = "STORE_READ";
    public static final String STORE_WRITE = "STORE_WRITE";

    public static final String EMPLOYEE_READ_SELF = "EMPLOYEE_READ_SELF";
    public static final String EMPLOYEE_READ_ANY = "EMPLOYEE_READ_ANY";
    public static final String EMPLOYEE_WRITE = "EMPLOYEE_WRITE";
    public static final String EMPLOYEE_PASSWORD_SELF = "EMPLOYEE_PASSWORD_SELF";
    public static final String EMPLOYEE_PASSWORD_ANY = "EMPLOYEE_PASSWORD_ANY";

    public static final String SHIFT_READ = "SHIFT_READ";
    public static final String SHIFT_WRITE = "SHIFT_WRITE";

    public static final String SWAP_REQUEST_SELF = "SWAP_REQUEST_SELF";
    public static final String SWAP_REQUEST_ANY = "SWAP_REQUEST_ANY";

    public static final String TIME_OFF_SELF = "TIME_OFF_SELF";
    public static final String TIME_OFF_ANY = "TIME_OFF_ANY";
    public static final String TIME_OFF_REVIEW = "TIME_OFF_REVIEW";

    public static final String AVAILABILITY_SELF = "AVAILABILITY_SELF";
    public static final String AVAILABILITY_ANY = "AVAILABILITY_ANY";
}
