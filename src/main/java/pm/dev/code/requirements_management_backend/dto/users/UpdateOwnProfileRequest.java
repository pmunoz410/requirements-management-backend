package pm.dev.code.requirements_management_backend.dto.users;

public record UpdateOwnProfileRequest(
        String username,
        String firstName,
        String lastName,
        String email,
        String phone
) {
}
