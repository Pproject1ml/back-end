package org._1mg.tt_backend.auth.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org._1mg.tt_backend.auth.Role;
import org._1mg.tt_backend.auth.dto.MemberDTO;
import org._1mg.tt_backend.auth.security.CustomAuthenticationToken;
import org._1mg.tt_backend.auth.security.CustomUserDetails;
import org._1mg.tt_backend.exception.auth.CustomJwtException;
import org._1mg.tt_backend.exception.auth.JwtExpiredTokenException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            log.error("JWT Token IS NULL");
            filterChain.doFilter(request, response);
            return;
        }

        log.info("JWT Token Validation Start");
        String token = authorization.split(" ")[1];
        log.info("INPUT TOKEN : {}", token);

        Claims claims;
        try {
            claims = jwtUtils.verifyToken(token);
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token");
            AuthenticationException newE = new JwtExpiredTokenException("EXPIRED JWT TOKEN : " + e.getMessage());
            request.setAttribute("customException", newE);
            throw newE;
        } catch (Exception e) {
            log.error("JWT ERROR {}", e.getMessage());
            AuthenticationException newE = new CustomJwtException("JWT ERROR : " + e.getMessage());
            request.setAttribute("customException", newE);
            throw newE;
        }

        //claims.getExpiration().before(new Date())

        String memberId = jwtUtils.getSubject(claims);
        String roleName = jwtUtils.getRole(claims);
        Role role = Role.getRole(roleName);
        if (role == null) {
            //권한에 대한 처리는 LoginFilter에서 수행
            log.error("JWT TOKEN MISSING REQUIRED AUTHORITY: {}", roleName);
            filterChain.doFilter(request, response);
        }

        CustomUserDetails userDetails = new CustomUserDetails(
                MemberDTO.builder()
                        .memberId(memberId)
                        .role(role)
                        .build()
        );


        CustomAuthenticationToken authenticated = CustomAuthenticationToken.authenticated(UUID.fromString(memberId), null, null, userDetails, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticated);

        log.info("JWT Token Validation End");
        filterChain.doFilter(request, response);
    }
}
