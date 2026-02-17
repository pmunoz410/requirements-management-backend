package pm.dev.code.requirements_management_backend.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import pm.dev.code.requirements_management_backend.exceptions.security.ExpiredJwtTokenException;
import pm.dev.code.requirements_management_backend.exceptions.security.InvalidJwtTokenException;
import pm.dev.code.requirements_management_backend.exceptions.security.JwtErrorMessage;
import pm.dev.code.requirements_management_backend.exceptions.technical.JwtGenerationException;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

@Component
public class JwtUtil {

    @Value("${security.jwt.expiration}")
    private Long EXPIRATION_IN_MILLIS;

    @Value("${security.jwt.secret}")
    private String SECRET_KEY;

    public String generateToken(UserDetails userDetails, Map<String, Object> extraClaims) {

        try {
            Date issuedAt = new Date(System.currentTimeMillis());
            Date expiration = new Date( (EXPIRATION_IN_MILLIS) + issuedAt.getTime() );

            return Jwts.builder()
                    // Header
                    .header()
                    .type("JWT")
                    .and()

                    // Payload
                    .subject(userDetails.getUsername())
                    .issuedAt(issuedAt)
                    .expiration(expiration)
                    .claims(extraClaims)

                    // Signature
                    .signWith(generateKey(), Jwts.SIG.HS256)
                    .compact();
        } catch (Exception ex) {
            throw new JwtGenerationException();
        }
    }

    // ==============================
    // VALIDATE & EXTRACT CLAIMS
    // ==============================
    public Claims validateAndExtractClaims(String token) {

        try {
            return Jwts.parser()
                    .verifyWith(generateKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (ExpiredJwtException ex) {
            throw new ExpiredJwtTokenException(JwtErrorMessage.EXPIRED_TOKEN);

        } catch (Exception ex) {
            throw new InvalidJwtTokenException(JwtErrorMessage.INVALID_TOKEN);
        }
    }

    // ==============================
    // EXTRACT USERNAME
    // ==============================
    public String extractUsername(String token) {
        return validateAndExtractClaims(token).getSubject();
    }

    // ==============================
    // VALIDATE TOKEN AGAINST USER
    // ==============================
    public void validateToken(String token, UserDetails userDetails) {

        Claims claims = validateAndExtractClaims(token);
        String username = claims.getSubject();

        if (!username.equals(userDetails.getUsername())) {
            throw new InvalidJwtTokenException(JwtErrorMessage.INVALID_TOKEN);
        }
    }

    // ==============================
    // GENERATE SECRET KEY
    // ==============================
    private SecretKey generateKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (Exception ex) {
            // Esto es error interno del sistema (500)
            throw new JwtGenerationException();
        }
    }

    /*
    private SecretKey generateKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(generateKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isTokenValid(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }*/
}
