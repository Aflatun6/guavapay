package com.parcel.app.security;

import com.google.common.base.Strings;
import com.parcel.app.config.JwtConfig;
import com.parcel.app.entity.UserEntity;
import com.parcel.app.exception.CustomExceptionHandler;
import com.parcel.app.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import javax.crypto.SecretKey;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
public class JwtTokenVerifier extends OncePerRequestFilter {

    private final SecretKey secretKey;
    private final JwtConfig jwtConfig;
    private final UserService userService;
    private final CustomExceptionHandler customExceptionHandler;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        String token = getToken(request, response, filterChain);
        if (token == null) {
            return;
        }
        try {
            Jws<Claims> claimsJws = Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token);

            Claims body = claimsJws.getBody();

            UserDetailsImp userDetailsImp = getUserDetailsImp(body);

            Set<SimpleGrantedAuthority> simpleGrantedAuthorities = getAuthorities(body);

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    userDetailsImp,
                    null,
                    simpleGrantedAuthorities
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (JwtException e) {
            customExceptionHandler.handle(request, response,
                    new AccessDeniedException(String.format("Token %s cannot " +
                            "be trusted", token)));
        }
        filterChain.doFilter(request, response);
    }

    private UserDetailsImp getUserDetailsImp(Claims body) {
        String userId = body.get("userId", String.class);
        UserEntity entity = userService.findById(userId);
        return new UserDetailsImp(entity);
    }

    private Set<SimpleGrantedAuthority> getAuthorities(Claims body) {
        return Objects.requireNonNull(
                        body.entrySet().stream().filter(b -> b.getKey().equals("authorities")).findFirst()
                                .map(b -> (List<Map<String, String>>) b.getValue())
                                .orElse(null)).stream()
                .flatMap(m -> m.values().stream()).map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    private String getToken(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {
        String authorizationHeader = request.getHeader(jwtConfig.getAuthorizationHeader());

        if (Strings.isNullOrEmpty(authorizationHeader) || !authorizationHeader.startsWith(jwtConfig.getTokenPrefix())) {
            filterChain.doFilter(request, response);
            return null;
        }
        return authorizationHeader.replace(jwtConfig.getTokenPrefix(), "");
    }
}
