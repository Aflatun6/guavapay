package com.parcel.app.security;

import static com.parcel.app.enums.Role.ADMIN;
import static com.parcel.app.enums.Role.COURIER;
import static com.parcel.app.enums.Role.USER;

import com.parcel.app.config.JwtConfig;
import com.parcel.app.exception.CustomExceptionHandler;
import com.parcel.app.service.UserService;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    private final SecretKey secretKey;
    private final JwtConfig jwtConfig;
    private final UserService userService;
    private final CustomExceptionHandler customExceptionHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling().accessDeniedHandler(customExceptionHandler).and()
                .addFilterAfter(getAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/user/").permitAll()
                .antMatchers("/admin/**").hasRole(ADMIN.name())
                .antMatchers("/user/**").hasRole(USER.name())
                .antMatchers("/courier/**").hasRole(COURIER.name())
                .anyRequest().authenticated();
    }

    private JwtTokenVerifier getAuthorizationFilter() {
        return new JwtTokenVerifier(secretKey, jwtConfig, userService, customExceptionHandler);
    }

}
