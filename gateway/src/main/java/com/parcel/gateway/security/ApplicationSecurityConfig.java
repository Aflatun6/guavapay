package com.parcel.gateway.security;

import com.parcel.gateway.auth.UserDetailsServiceImp;
import com.parcel.gateway.jwt.JwtConfig;
import com.parcel.gateway.jwt.UsernameAndPasswordAuthenticationFilter;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;


@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {

    private final PasswordEncoder passwordEncoder;
    private final UserDetailsServiceImp userDetailsServiceImp;
    private final SecretKey secretKey;
    private final JwtConfig jwtConfig;
    private final UserDetailsServiceImp userService;

    @Autowired
    public ApplicationSecurityConfig(PasswordEncoder passwordEncoder,
                                     UserDetailsServiceImp userDetailsServiceImp,
                                     SecretKey secretKey,
                                     JwtConfig jwtConfig, UserDetailsServiceImp userService) {
        this.passwordEncoder = passwordEncoder;
        this.userDetailsServiceImp = userDetailsServiceImp;
        this.secretKey = secretKey;
        this.jwtConfig = jwtConfig;
        this.userService = userService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(new UsernameAndPasswordAuthenticationFilter(authenticationManager(),
                        jwtConfig, secretKey, userService))
                .authorizeRequests()
                .antMatchers("/login").permitAll()
                .anyRequest().authenticated();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(userDetailsServiceImp);
        return provider;
    }

}
