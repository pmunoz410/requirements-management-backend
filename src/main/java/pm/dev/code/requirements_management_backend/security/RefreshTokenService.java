package pm.dev.code.requirements_management_backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pm.dev.code.requirements_management_backend.entities.RefreshToken;
import pm.dev.code.requirements_management_backend.entities.User;
import pm.dev.code.requirements_management_backend.repositories.IRefreshTokenRepository;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final IRefreshTokenRepository repository;

    @Value("${security.jwt.refresh-expiration}")
    private Long REFRESH_EXPIRATION;

    @Value("${security.jwt.session-days}")
    private Long SESSION_DAYS;

    public RefreshToken create(User user) {
        Instant now = Instant.now();

        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiresAt(now.plusMillis(REFRESH_EXPIRATION));

        token.setSessionExpiresAt(now.plus(SESSION_DAYS, ChronoUnit.DAYS));

        return repository.save(token);
    }

    public RefreshToken validate(String token) {
        RefreshToken rt = repository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("Refresh token inválido"));

        if (rt.isRevoked())
            throw new RuntimeException("Refresh token revocado");

        if (rt.getExpiresAt().isBefore(Instant.now()))
            throw new RuntimeException("Refresh token expirado");

        if (rt.getSessionExpiresAt().isBefore(Instant.now())) {
            rt.setRevoked(true);
            repository.save(rt);
            throw new RuntimeException("Sesión expirada, debe iniciar sesión nuevamente");
        }

        return rt;
    }

    public void revoke(RefreshToken token) {
        token.setRevoked(true);
        repository.save(token);
    }

    public RefreshToken rotate(RefreshToken oldToken) {
        // Revocar antiguo token
        oldToken.setRevoked(true);
        repository.save(oldToken);

        // Crear uno nuevo, MISMA sesión
        Instant now = Instant.now();

        RefreshToken newToken = new RefreshToken();
        newToken.setUser(oldToken.getUser());
        newToken.setToken(UUID.randomUUID().toString());
        newToken.setExpiresAt(now.plusMillis(REFRESH_EXPIRATION));
        newToken.setRevoked(false);

        // Mantener la fecha límite de sesión
        newToken.setSessionExpiresAt(oldToken.getSessionExpiresAt());

        return repository.save(newToken);
    }
}
