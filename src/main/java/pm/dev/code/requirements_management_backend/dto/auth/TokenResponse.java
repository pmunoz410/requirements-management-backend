package pm.dev.code.requirements_management_backend.dto.auth;

public record TokenResponse(
        String accessToken,
        String refreshToken
) {
}
