package pm.dev.code.requirements_management_backend.services;

import pm.dev.code.requirements_management_backend.dto.auth.LoginRequest;
import pm.dev.code.requirements_management_backend.dto.auth.TokenResponse;

public interface IAuthService {
    TokenResponse login(LoginRequest request);
    TokenResponse refreshToken(String refreshToken);
    void logout(String refreshToken);
}
