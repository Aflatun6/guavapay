package com.parcel.app.config;


import com.parcel.app.security.UserDetailsImp;
import lombok.NoArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;

@NoArgsConstructor
public class PrincipalConfig {

    public static UserDetailsImp getPrincipal() {
        return (UserDetailsImp) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static String getUserId() {
        return getPrincipal().getUserId();
    }
}
