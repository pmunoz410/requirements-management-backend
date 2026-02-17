package pm.dev.code.requirements_management_backend.services.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import pm.dev.code.requirements_management_backend.dto.auth.LoginRequest;
import pm.dev.code.requirements_management_backend.dto.auth.TokenResponse;
import pm.dev.code.requirements_management_backend.entities.RefreshToken;
import pm.dev.code.requirements_management_backend.entities.User;
import pm.dev.code.requirements_management_backend.exceptions.business.auth.AuthErrorMessage;
import pm.dev.code.requirements_management_backend.exceptions.business.auth.InvalidCredentialsException;
import pm.dev.code.requirements_management_backend.exceptions.business.auth.UnauthorizedAccessException;
import pm.dev.code.requirements_management_backend.repositories.IUserRepository;
import pm.dev.code.requirements_management_backend.security.RefreshTokenService;
import pm.dev.code.requirements_management_backend.services.IAuthService;
import pm.dev.code.requirements_management_backend.utils.JwtUtil;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements IAuthService {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final IUserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    @Override
    public TokenResponse login(LoginRequest request) {
        try {
            // Autenticación Spring Security
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );

            // UserDetails (Spring Security)
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // Entidad User (BD)
            User user = userRepository.findByUsername(userDetails.getUsername())
                    .orElseThrow(() -> new InvalidCredentialsException(AuthErrorMessage.INVALID_CREDENTIALS));

            if (!user.isActive()) {
                throw new UnauthorizedAccessException(AuthErrorMessage.UNAUTHORIZED_ACCESS);
            }

            // Claims extra
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", user.getRol().name());

            String accessToken = jwtUtil.generateToken(userDetails, claims);
            RefreshToken refreshToken = refreshTokenService.create(user);

            return new TokenResponse(accessToken, refreshToken.getToken());

        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException(AuthErrorMessage.INVALID_CREDENTIALS);
        }
    }

    @Override
    public TokenResponse refreshToken(String refreshToken) {
        RefreshToken rt = refreshTokenService.validate(refreshToken);
        User user = rt.getUser();

        RefreshToken newRefresh = refreshTokenService.rotate(rt);

        UserDetails userDetails =
                org.springframework.security.core.userdetails.User.builder()
                        .username(user.getUsername())
                        .password(user.getPassword())
                        .roles(user.getRol().name())
                        .disabled(!user.isActive())
                        .build();

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRol().name());

        String newAccess = jwtUtil.generateToken(userDetails, claims);

        return new TokenResponse(newAccess, newRefresh.getToken());
    }

    @Override
    public void logout(String refreshToken) {
        RefreshToken rt = refreshTokenService.validate(refreshToken);
        refreshTokenService.revoke(rt);
    }
}
