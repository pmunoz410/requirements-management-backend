package pm.dev.code.requirements_management_backend.dto.auth;

public record LoginRequest(
        String username,
        String password
) {
}
