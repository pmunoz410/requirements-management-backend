package pm.dev.code.requirements_management_backend.dto.users;

public record ChangePasswordRequest(
        String currentPassword,
        String newPassword
) {
}
