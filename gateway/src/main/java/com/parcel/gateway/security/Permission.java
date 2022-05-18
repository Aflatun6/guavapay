package com.parcel.gateway.security;

public enum Permission {
    ORDER_CREATE("order:create"),
    ORDER_VIEW("order:view"),
    ORDER_UPDATE_DESTINATION("order:update-destination"),
    ORDER_UPDATE_STATUS("order:update-status"),
    ORDER_ASSIGN("order:assign"),
    ORDER_CANCEL("order:cancel"),
    USER_CREATE("user:create"),
    COURIER_CREATE("courier:create"),
    COURIER_VIEW("courier:view");

    private final String permission;

    Permission(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }
}
