package com.parcel.app.security;

import com.parcel.app.config.JwtConfig;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
    private final SecretKey secretKey;
    private final JwtConfig jwtConfig;
    private final UserDetailsServiceImp userService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(getAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
//                .antMatchers("/admin").hasRole(ADMIN.name())
//                .antMatchers("/user").hasRole(USER.name())
//                .antMatchers("/courier").hasRole(COURIER.name())
                .antMatchers(HttpMethod.POST, "/user").permitAll()
                .anyRequest().permitAll();
    }

    private JwtTokenVerifier getAuthorizationFilter() {
        return new JwtTokenVerifier(secretKey, jwtConfig, userService);
    }
}
