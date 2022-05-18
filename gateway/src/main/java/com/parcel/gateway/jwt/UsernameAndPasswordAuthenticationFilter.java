package com.parcel.gateway.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parcel.gateway.auth.UserDetailsImp;
import com.parcel.gateway.auth.UserDetailsServiceImp;
import io.jsonwebtoken.Jwts;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

public class UsernameAndPasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtConfig jwtConfig;
    private final SecretKey secretKey;
    private final UserDetailsServiceImp userService;
    private final String REMEMBER_ME = "rememberMe";

    public UsernameAndPasswordAuthenticationFilter(AuthenticationManager authenticationManager,
                                                   JwtConfig jwtConfig,
                                                   SecretKey secretKey,
                                                   UserDetailsServiceImp userService) {
        this.authenticationManager = authenticationManager;
        this.jwtConfig = jwtConfig;
        this.secretKey = secretKey;
        this.userService = userService;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        try {
            LoginReq loginReq = new ObjectMapper().readValue(request.getInputStream(), LoginReq.class);
            response.addHeader(REMEMBER_ME, String.valueOf(loginReq.getRememberMe()));

            return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginReq.getUsername(), loginReq.getPassword()
            ));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {
        boolean rememberMe = Boolean.parseBoolean(response.getHeader(REMEMBER_ME));
        UserDetailsImp principal = (UserDetailsImp) authResult.getPrincipal();

        int days = rememberMe ? jwtConfig.getTokenExpirationAfterDays() : 1;

        String token = Jwts.builder()
                .setSubject(authResult.getName())
                .claim("authorities", authResult.getAuthorities())
                .claim("userId", userService.findUserByUsername(principal.getUsername()).getId())
                .setIssuedAt(Date.valueOf(LocalDate.now()))
                .setExpiration(Date.valueOf(LocalDate.now().plusDays(days)))
                .signWith(secretKey)
                .compact();

        response.addHeader(jwtConfig.getAuthorizationHeader(),
                String.format("%s %s", jwtConfig.getTokenPrefix(), token));
    }
}
