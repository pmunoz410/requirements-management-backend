package pm.dev.code.requirements_management_backend.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import pm.dev.code.requirements_management_backend.dto.auth.LoginRequest;
import pm.dev.code.requirements_management_backend.dto.auth.TokenResponse;
import pm.dev.code.requirements_management_backend.entities.RefreshToken;
import pm.dev.code.requirements_management_backend.entities.User;
import pm.dev.code.requirements_management_backend.repositories.IUserRepository;
import pm.dev.code.requirements_management_backend.security.RefreshTokenService;
import pm.dev.code.requirements_management_backend.utils.JwtUtil;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;
    private final IUserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    @PostMapping("/login")
    public TokenResponse login(@RequestBody LoginRequest request) {

        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(), request.password())
        );

        // UserDetails (Spring Security)
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Entidad User (BD)
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow();

        // Claims extra
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRol().name());

        String accessToken = jwtUtil.generateToken(userDetails, claims);
        RefreshToken refreshToken = refreshTokenService.create(user);

        return new TokenResponse(accessToken, refreshToken.getToken());
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@RequestParam String refreshToken) {

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

    @PostMapping("/logout")
    public void logout(@RequestParam String refreshToken) {
        RefreshToken rt = refreshTokenService.validate(refreshToken);
        refreshTokenService.revoke(rt);
    }
}
