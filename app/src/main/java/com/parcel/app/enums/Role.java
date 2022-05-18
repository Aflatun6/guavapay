package com.parcel.app.enums;

import static com.parcel.app.enums.Permission.COURIER_CREATE;
import static com.parcel.app.enums.Permission.COURIER_VIEW;
import static com.parcel.app.enums.Permission.ORDER_ASSIGN;
import static com.parcel.app.enums.Permission.ORDER_CANCEL;
import static com.parcel.app.enums.Permission.ORDER_CREATE;
import static com.parcel.app.enums.Permission.ORDER_UPDATE_DESTINATION;
import static com.parcel.app.enums.Permission.ORDER_UPDATE_STATUS;
import static com.parcel.app.enums.Permission.ORDER_VIEW;
import static com.parcel.app.enums.Permission.USER_CREATE;

import com.google.common.collect.Sets;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum Role {

    USER(Sets.newHashSet(USER_CREATE, ORDER_CREATE, ORDER_UPDATE_DESTINATION, ORDER_CANCEL, ORDER_VIEW)),
    ADMIN(Sets.newHashSet(ORDER_UPDATE_STATUS, ORDER_VIEW, ORDER_ASSIGN, COURIER_CREATE, COURIER_VIEW)),
    COURIER(Sets.newHashSet(ORDER_VIEW, ORDER_UPDATE_STATUS));

    private final Set<Permission> permissions;

    Role(Set<Permission> permissions) {
        this.permissions = permissions;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public Set<SimpleGrantedAuthority> getGrantedAuthorities() {
        Set<SimpleGrantedAuthority> permissions = getPermissions().stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toSet());
        permissions.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return permissions;
    }
}
