package pm.dev.code.requirements_management_backend.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pm.dev.code.requirements_management_backend.exceptions.base.ApiErrorBuilder;
import pm.dev.code.requirements_management_backend.exceptions.base.AppException;
import pm.dev.code.requirements_management_backend.exceptions.security.ExpiredJwtTokenException;
import pm.dev.code.requirements_management_backend.exceptions.security.InvalidJwtTokenException;
import pm.dev.code.requirements_management_backend.exceptions.security.JwtErrorMessage;
import pm.dev.code.requirements_management_backend.exceptions.security.MissingJwtTokenException;
import pm.dev.code.requirements_management_backend.utils.JwtUtil;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final CustomUserDetailsService userDetailsService;
    private final ApiErrorBuilder apiErrorBuilder;
    private final ObjectMapper objectMapper;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        System.out.println("HEADER: " + header);

        try {
            if (header == null || !header.startsWith("Bearer ")) {
                throw new MissingJwtTokenException(JwtErrorMessage.MISSING_TOKEN);
            }

            String token = header.substring(7);

            // ======================
            // Validamos y extraemos claims
            // ======================
            String username;
            try {
                username = jwtUtil.extractUsername(token);
            } catch (ExpiredJwtTokenException | InvalidJwtTokenException ex) {
                throw ex; // Estas ya son tus excepciones personalizadas
            }

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // ======================
            // Validamos token contra el usuario
            // ======================
            try {
                jwtUtil.validateToken(token, userDetails); // lanza InvalidJwtTokenException si falla
            } catch (InvalidJwtTokenException ex) {
                throw ex;
            }

            // ======================
            // Seteamos autenticación
            // ======================
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

            filterChain.doFilter(request, response);

        } catch (AppException ex) {

            if (!response.isCommitted()) {
                var error = apiErrorBuilder.build(ex, request, ex);

                response.resetBuffer();
                response.setStatus(ex.getStatus().value());
                response.setContentType("application/json");

                objectMapper.writeValue(response.getOutputStream(), error);
                response.flushBuffer();
            }
        }

        /*
        try {

            if (header == null || !header.startsWith("Bearer ")) {
                throw new MissingJwtTokenException(JwtErrorMessage.MISSING_TOKEN);
            }

            String token = header.substring(7);

            if (!jwtUtil.isTokenValid(token)) {
                throw new InvalidJwtTokenException(JwtErrorMessage.INVALID_TOKEN);
            }

            if (jwtUtil.isTokenExpired(token)) {
                throw new ExpiredJwtTokenException(JwtErrorMessage.EXPIRED_TOKEN);
            }

            String username = jwtUtil.extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (!jwtUtil.validateToken(token, userDetails)) {
                throw new InvalidJwtTokenException(JwtErrorMessage.INVALID_TOKEN);
            }

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(auth);

            filterChain.doFilter(request, response);

        } catch (AppException ex) {

            if (!response.isCommitted()) {

                var error = apiErrorBuilder.build(ex, request, ex);

                response.resetBuffer();
                response.setStatus(ex.getStatus().value());
                response.setContentType("application/json");

                objectMapper.writeValue(response.getOutputStream(), error);
                response.flushBuffer();
            }
        }*/
    }
}
